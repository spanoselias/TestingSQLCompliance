import fo
import copy
from collections import OrderedDict, namedtuple

class VarCount:
    def __init__(self, start):
        self.num = start
    def inc(self):
        n = self.num
        self.num += 1
        return n

class Query:
    schema = None
    def __init__(self, expr):
        self.expr = expr
        self.kwargs = { 'var_count': VarCount(1) }
    def __str__(self):
        return str(self.expr)
    def fo_query(self):
        result = self.expr.fo_query(**self.kwargs)
        return fo.Query(result['free_vars'], result['fo_expr'])

class Union(Query):
    sym = 'UNION'
    def __init__(self, arg1, arg2, *args):
        self.args = (arg1,arg2) + args
    def __str__(self):
        queries = [ '( '+str(q)+' )' if isinstance(q,(Intersect,Except)) else str(q)
                    for q in self.args ]
        return ' {} '.format(Union.sym).join(queries)
    def fo_query(self, **kwargs):
        results = [ q.fo_query(var_count=kwargs['var_count']) for q in self.args ]
        disjuncts = [ results[0]['fo_expr'] ]
        for i in range(1,len(results)):
            subst = dict(zip(results[i]['free_vars'],results[0]['free_vars']))
            disjuncts.append( results[i]['fo_expr'].rename(subst) )
        return { 'fo_expr'  : fo.Or(*disjuncts),
                 'free_vars': results[0]['free_vars'],
                 'out_attr' : results[0]['out_attr'] }

class Intersect(Query):
    sym = 'INTERSECT'
    def __init__(self, arg1, arg2, *args):
        self.args = (arg1,arg2) + args
    def __str__(self):
        queries = [ '( '+str(q)+' )' if isinstance(q,(Union,Except)) else str(q)
                    for q in self.args ]
        return ' {} '.format(Intersect.sym).join(queries)
    def fo_query(self, **kwargs):
        results = [ q.fo_query(var_count=kwargs['var_count']) for q in self.args ]
        conjuncts = [ results[0]['fo_expr'] ]
        for i in range(1,len(results)):
            subst = dict(zip(results[i]['free_vars'],results[0]['free_vars']))
            conjuncts.append( results[i]['fo_expr'].rename(subst) )
        return { 'fo_expr'  : fo.And(*conjuncts),
                 'free_vars': results[0]['free_vars'],
                 'out_attr' : results[0]['out_attr'] }

class Except(Query):
    sym = 'EXCEPT'
    def __init__(self, left, right):
        self.left = left
        self.right = right
    def __str__(self):
        template = "{} {} " if isinstance(self.left,Select) else "( {} ) {}"
        if isinstance(self.right,Select):
            template += "{}"
        else:
            template += "( {} )"
        return template.format(str(self.left), Except.sym, str(self.right))
    def fo_query(self, **kwargs):
        l_result = self.left.fo_query(var_count=kwargs['var_count'])
        r_result = self.right.fo_query(var_count=kwargs['var_count'])
        subst = dict(zip(r_result['free_vars'],l_result['free_vars']))
        return { 'fo_expr'  : fo.And( l_result['fo_expr'],
                                      fo.Not(r_result['fo_expr'].rename(subst)) ),
                 'free_vars': l_result['free_vars'],
                 'out_attr' : l_result['out_attr'] }

class Select(Query):
    sym = 'Engine.SELECT_rmv'
    def __init__(self, attr_list, from_list=None, where=None):
        self.attr_list = attr_list
        self.distinct = True
        self.from_list = from_list
        self.where = where
    def __str__(self):
        out = Select.sym
        if self.distinct:
            out += ' DISTINCT'
        if len(self.attr_list) == 0:
            out += ' *'
        else:
            out += ' ' + ', '.join(self.attr_list)
        if self.from_list is not None:
            out += ' {} {}'.format(From.sym, str(self.from_list))
        if self.where is not None:
            out += ' {} {}'.format(Where.sym, str(self.where))
        return out
    def fo_query(self, **kwargs):
        if self.from_list is None:
            raise Exception("Not implemented yet")
        from_result = self.from_list.fo_query(**kwargs)
        from_vars = from_result['from_vars']
        var_attr = [ (a,v) for k in from_vars.keys() for (a,v) in from_vars[k] ]
        all_attr, all_vars = tuple(map(list, zip(*var_attr)))
        out_vars = []
        out_attr = []
        if len(self.attr_list) == 0:
            out_vars = all_vars
            out_attr = all_attr
        else:
            sel_vars = [ Condition.unpack_arg(x) for x in self.attr_list ]
            out_vars = [ Condition.resolve_var(v[0], v[1], from_vars)[1] for v in sel_vars ]
            out_attr = [ a for r,a in sel_vars ]
        quantified_vars = set(all_vars) - set(out_vars)

        out_query = None
        from_expr = from_result['fo_expr']
        if self.where is None:
            out_query = from_expr
        else:
            d = { 'from_vars' : from_vars, 'var_count' : kwargs['var_count'] }
            if 'outer_vars' in kwargs:
                d['outer_vars'] = kwargs['outer_vars']
            where_expr = self.where.fo_query(**d)
            out_query = fo.And(from_expr, where_expr)
        if len(quantified_vars) > 0:
            out_query = fo.Exists(list(quantified_vars), out_query)
        result = { 'fo_expr'  : fo.UnkToFalse(out_query),
                   'free_vars': out_vars,
                   'all_vars' : all_vars,
                   'out_attr' : out_attr,
                   'var_count': kwargs['var_count'] }
        return result

class From:
    sym = 'Engine.FROM'
    def __init__(self, tables):
        self.tables = OrderedDict(tables)
    def __str__(self):
        from_list = []
        for alias, table in self.tables.items():
            if isinstance(table, Query):
                from_list += [ "( {} ) AS {}".format(table, alias) ]
            elif table != alias:
                from_list += [ "{} AS {}".format(table ,alias) ]
            else:
                from_list += [ table ]
        return ', '.join(from_list)
    def fo_query(self, **kwargs):
        from_vars = OrderedDict() # important to keep the variables' order
        relations = []
        for alias, table in self.tables.items():
            if table is None:
                self.tables[alias] = alias
            if isinstance(table, Query):
                result = table.fo_query(var_count=kwargs['var_count'])
                relations.append(result['fo_expr'])
                from_vars[alias] = zip(result['out_attr'], result['free_vars'])
            else:
                table_schema = Query.schema[table]
                from_vars[alias] = [ (table_schema[i], '?x{}'.format(kwargs['var_count'].inc()))
                                     for i in range(len(table_schema)) ]
                relations += [ fo.Rel(table, *[var for (attr,var) in from_vars[alias]]) ]
        from_expr = fo.And(*relations) if len(relations) > 1 else relations[0]
        return { 'fo_expr': from_expr, 'from_vars': from_vars, 'var_count': kwargs['var_count'] }

class Where:
    sym = 'Engine.WHERE'
    def __init__(self, condition):
        self.condition = condition
    def __str__(self):
        return str(self.condition)
    def fo_query(self, **kwargs):
        return self.condition.fo_query(**kwargs)

class Condition:
    @staticmethod
    def unpack_arg(x):
        split = x.split('.')
        return (None, split[0]) if len(split) == 1 else (split[0], split[1])
    @staticmethod
    def pack_arg(x):
        return '.'.join(x) if x[0] is not None else x[1]
    @staticmethod
    def resolve_var(rel, attr, varmap):
        matches = None
        if rel is not None:
            if rel in varmap.keys():
                matches = [ (rel,v) for a,v in varmap[rel] if a==attr ]
            else:
                raise Exception("No matches for relation {}".format(rel))
        else:
            matches = [ (r,v) for r in varmap.keys() for a,v in varmap[r] if a==attr ]
        if len(matches) == 0:
            raise Exception("No matches for attribute")
        elif len(matches) > 1:
            raise Exception("More than one match for attribute")
        else:
            return matches[0]

class And:
    sym = 'AND'
    def __init__(self, condition, *conditions):
        self.conditions = (condition,) + conditions
    def __str__(self):
        conjuncts = [ "( {} )".format(x) if isinstance(x,Or) else str(x)
                      for x in self.conditions ]
        return " {} ".format(And.sym).join(conjuncts)
    def fo_query(self, **kwargs):
        return fo.And(*[ cond.fo_query(**kwargs) for cond in self.conditions ])

class Or:
    sym = 'OR'
    def __init__(self, condition, *conditions):
        self.conditions = (condition,) + conditions
    def __str__(self):
        disjuncts = [ "( {} )".format(x) if isinstance(x,And) else str(x)
                      for x in self.conditions ]
        return " {} ".format(Or.sym).join(disjuncts)
    def fo_query(self, **kwargs):
        return fo.Or(*[ cond.fo_query(**kwargs) for cond in self.conditions ])

class Not:
    sym = 'NOT'
    def __init__(self, condition):
        self.condition = condition
    def __str__(self):
        template = "{} {}" if isinstance(self.condition,(Exists,Not)) else "{}( {} )"
        return template.format(Not.sym, str(self.condition))
    def fo_query(self, **kwargs):
        return fo.Not(self.condition.fo_query(**kwargs))

class Eq(Condition):
    sym = '='
    def __init__(self, arg1, arg2):
        self.arg1 = self.unpack_arg(arg1)
        self.arg2 = self.unpack_arg(arg2)
    def __str__(self):
        return '{}{}{}'.format(self.pack_arg(self.arg1), Eq.sym, self.pack_arg(self.arg2))
    def fo_query(self, **kwargs):
        try:
            r1,v1 = self.resolve_var(self.arg1[0], self.arg1[1], kwargs['from_vars'])
        except:
            r1,v1 = self.resolve_var(self.arg1[0], self.arg1[1], kwargs['outer_vars'])
        try:
            r2,v2 = self.resolve_var(self.arg2[0], self.arg2[1], kwargs['from_vars'])
        except:
            r2,v2 = self.resolve_var(self.arg2[0], self.arg2[1], kwargs['outer_vars'])
        return fo.Eq(v1,v2)

class NotEq(Condition):
    sym = '<>'
    def __init__(self, arg1, arg2):
        self.arg1 = self.unpack_arg(arg1)
        self.arg2 = self.unpack_arg(arg2)
    def __str__(self):
        return '{}{}{}'.format(self.pack_arg(self.arg1), NotEq.sym, self.pack_arg(self.arg2))
    def fo_query(self, **kwargs):
        try:
            r1,v1 = self.resolve_var(self.arg1[0], self.arg1[1], kwargs['from_vars'])
        except:
            r1,v1 = self.resolve_var(self.arg1[0], self.arg1[1], kwargs['outer_vars'])
        try:
            r2,v2 = self.resolve_var(self.arg2[0], self.arg2[1], kwargs['from_vars'])
        except:
            r2,v2 = self.resolve_var(self.arg2[0], self.arg2[1], kwargs['outer_vars'])
        return fo.NotEq(v1,v2)

class Exists:
    sym = 'EXISTS'
    def __init__(self, subquery):
        self.subquery = subquery
    def __str__(self):
        return "{} ( {} )".format(Exists.sym, str(self.subquery))
    def fo_query(self, **kwargs):
        outer_vars = copy.deepcopy(kwargs['outer_vars']) if 'outer_vars' in kwargs else dict()
        outer_vars.update(kwargs['from_vars'])
        result = self.subquery.fo_query(**{ 'outer_vars': outer_vars,
                                            'var_count' : kwargs['var_count'] })
        return fo.Exists(result['free_vars'], result['fo_expr'])

class In:
    sym = 'IN'
    def __init__(self, row, subquery):
        self.row = row
        self.subquery = subquery
    def __str__(self):
        template = "({}) {} ( {} )" if len(self.row) > 1 else "{} {} ( {} )"
        return template.format(','.join(self.row), In.sym, str(self.subquery))
    def fo_query(self, **kwargs):
        row = [ Condition.unpack_arg(x) for x in self.row ]
        row_vars = []
        for x in row:
            try:
                r,v = Condition.resolve_var(x[0], x[1], kwargs['from_vars'])
                row_vars += [v]
            except:
                r,v = Condition.resolve_var(x[0], x[1], kwargs['outer_vars'])
                row_vars += [v]
        outer_vars = copy.deepcopy(kwargs['outer_vars']) if 'outer_vars' in kwargs else dict()
        outer_vars.update(kwargs['from_vars'])
        result = self.subquery.fo_query(**{ 'outer_vars': outer_vars,
                                            'var_count' : kwargs['var_count'] })
        equalities = [ fo.Eq(x,y) for (x,y) in zip(row_vars, result['free_vars']) ]
        return fo.Exists( result['free_vars'], fo.And( result['fo_expr'], *equalities ) )

class NotIn:
    sym = 'NOT IN'
    def __init__(self, row, subquery):
        self.row = row
        self.subquery = subquery
    def __str__(self):
        template = "({}) {} ( {} )" if len(self.row) > 1 else "{} {} ( {} )"
        return template.format(','.join(self.row), NotIn.sym, str(self.subquery))
    def fo_query(self, **kwargs):
        return fo.Not( In(self.row,self.subquery).fo_query(**kwargs) )
