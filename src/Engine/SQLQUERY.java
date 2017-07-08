package Engine;

import java.util.LinkedList;

/**
 * Created by Elias on 6/19/2017.
 */
public class SQLQUERY
{
    public SQLQUERY()
    {

    }

    public QRYREPRES operQuery( LinkedList<String> frmRelts, long uniqID, boolean isNest, boolean isOneAttr ,  ConfParameters confPar)
    {

        boolean isDistinct =false;
        int rand = Utilities.getRandChoice(2);
        if(rand ==1)
            isDistinct = true;

        QRYREPRES res = new QRYREPRES();
        OPERATORS oper = new OPERATORS(confPar);

        LinkedList<String> alias =  confPar.genAlias;

        String finalQry="";

        FROM frmQry = new FROM(alias, confPar.relationsAttrs, confPar);
        WHERE whrQry = new WHERE(confPar.relationsAttrs);
        SELECT selQry = new SELECT(isDistinct,false, alias, 2, confPar.relationsAttrs, confPar);

        String stm = frmQry.getFrom( (++uniqID) );
        finalQry = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr, false, confPar.repAlias, false, null, false);
        finalQry += "\n" + stm + "\n" + whrQry.getSqlWhere(frmQry.getSelectedTables(),isNest,  confPar, confPar.maxCondWhere);

        finalQry += "\n" + oper.getOper(frmRelts) + "\n";

        stm = frmQry.getFrom( (++uniqID) );
        finalQry += selQry.getSelect(frmQry.getSelectedTables(), isOneAttr, false, confPar.repAlias, false, null, true);
        finalQry += "\n" + stm + "\n" + whrQry.getSqlWhere(frmQry.getSelectedTables(),isNest,  confPar, confPar.maxCondWhere);

        res.qryStr = finalQry;
        res.isOneAt = whrQry.getOneAttr();
        res.selRelts = frmQry.getSelectedTables();

        return res;
    }

    public QRYREPRES aggrGuery(   long uniqID , ConfParameters confPar)
    {
        boolean isDistinct =false;
        int rand = Utilities.getRandChoice(2);
        if(rand ==1)
            isDistinct = true;

        QRYREPRES res = new QRYREPRES();

        LinkedList<String> alias =  confPar.genAlias;

        String tmpStm="";
        String finalQry="";

        FROM frmQry = new FROM(alias, confPar.relationsAttrs, confPar);
        WHERE whrQry = new WHERE(confPar.relationsAttrs);
        SELECT selQry = new SELECT(isDistinct,false, alias, 2, confPar.relationsAttrs, confPar);
        GROUPBY grpQry = new GROUPBY(confPar);
        HAVING hvgQry = new HAVING(confPar);

        String stm = frmQry.getFrom( (++uniqID) );
        String grp = grpQry.getGroupBy(frmQry.getSelectedTables());
        String hvg = hvgQry.genHaving(grpQry.getAttrInGroup());
        tmpStm = selQry.getSelect(frmQry.getSelectedTables(), false, false, confPar.repAlias, true, grpQry.getAttrInGroup(), false);
        finalQry = tmpStm + "\n" + stm;
        finalQry += "\n" + whrQry.getSqlWhere(grpQry.getAttrInGroup(),false,  confPar, 5);
        finalQry += "\n" + grp + "\n" + hvg;

        res.qryStr = finalQry;
        res.isOneAt = whrQry.getOneAttr();
        res.selRelts = frmQry.getSelectedTables();

        return res;
    }

    public QRYREPRES genQuery( LinkedList<Attribute> frmRelts, long uniqID, boolean isNest, boolean isOneAttr ,  ConfParameters confPar)
    {

        QRYREPRES res = new QRYREPRES();

        LinkedList<String> alias =  confPar.genAlias;

        String tmpStm="";
        String finalQry="";

        boolean isDistinct =false;
        int rand = Utilities.getRandChoice(2);
        if(rand ==1)
            isDistinct = true;

        FROM frmQry = new FROM(alias, confPar.relationsAttrs, confPar);
        WHERE whrQry = new WHERE(confPar.relationsAttrs);
        SELECT selQry = new SELECT(isDistinct,false, alias, 2, confPar.relationsAttrs, confPar);

        String stm = frmQry.getFrom( (++uniqID) );

        //In case where this is a nested query then the frmRelts list
        //contains the binds attributes. Otherwise, we just select attributes from the from
        if(frmRelts != null && frmRelts.size() > 0)
        {
            frmRelts = copySelRelts(frmRelts, frmQry.getSelectedTables());
            tmpStm = selQry.getSelect(frmRelts, isOneAttr, false, 0.1, false, null, false);
            finalQry = tmpStm + "\n" + stm;
            finalQry += "\n" + whrQry.getSqlWhere(frmRelts, isNest, confPar, 5);
        }
        else
        {
            tmpStm = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr, false, confPar.repAlias, false, null, false);
            finalQry = tmpStm + "\n" + stm;
            finalQry += "\n" + whrQry.getSqlWhere(frmQry.getSelectedTables(),isNest,  confPar, 5);
        }

        res.qryStr = finalQry;
        res.isOneAt = whrQry.getOneAttr();
        res.selRelts = frmQry.getSelectedTables();

        return res;
    }

    public String genCompQuery(int subqry, LinkedList<Attribute> frmRelts, long uniqID, boolean isNest, boolean isOneAttr, ConfParameters confPar)
    {
        boolean isDistinct =false;
        int rand = Utilities.getRandChoice(2);
        if(rand ==1)
            isDistinct = true;

        LinkedList<String> alias =  confPar.genAlias;

        //We create new objects for each statement
        FROM frmQry = new FROM(alias, confPar.relationsAttrs, confPar);
        WHERE whrQry = new WHERE(confPar.relationsAttrs);
        SELECT selQry = new SELECT(isDistinct,false, alias, 2,confPar.relationsAttrs, confPar);

        String substm="";

        while( (subqry--) > 0)
        {
            String subName = "Q" + subqry;

            String frmstm = frmQry.getFrom(++uniqID);
            String selstm = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr, false, 0.0, false, null, false);
            String whrstm = whrQry.getSqlWhere(frmQry.getSelectedTables(), false,  confPar, 3);

            if( (subqry ) > 0 )
            {
                substm += "\n" + " ( " +  selstm + " " + frmstm + " " + whrstm + " ) AS " + subName + ", ";
            }
            else
            {
                substm += "\n" + " ( " +  selstm + " " + frmstm + " " + whrstm + " ) AS " + subName ;
            }

            for(String attr: selQry.getAliasAttr())
            {
                Attribute newAttr = new Attribute();
                newAttr.attrName = subName + "." + attr;

                frmRelts.add( newAttr );
            }
        }

        String from = frmQry.getFrom(++uniqID);
        for(Attribute attr: frmQry.getSelectedTables())
        {
            frmRelts.add( attr );
        }

        String stm = from + " , " + substm;
        String tmpStm = selQry.getSelect(frmRelts, isOneAttr, false, confPar.repAlias, false, null, false);
        String finalQry = tmpStm + "\n" + stm;
        finalQry += "\n" + whrQry.getSqlWhere(frmRelts , isNest,  confPar, 2);

        return  finalQry;

    }

    public String nestQuery( long uniqID, ConfParameters confPar)
    {


        //Store the depth of the nesting query
        int nestLev = confPar.nestLev;

        String sqlRep="";

        //This list will be used to store all the attributes from the current FROM clause and from outer
        //queries as well
        LinkedList<Attribute> levFrmBinds = new LinkedList<>();

        //It creates the first outer query
        QRYREPRES curQuery = genQuery( levFrmBinds, ++uniqID, true, false, confPar);

        //It stores all the attributes that are selected in the first outer query
        levFrmBinds = copySelRelts(levFrmBinds,curQuery.selRelts);

        boolean isNest = true;

        //It creates the right format
        if(nestLev >= 2)
        {
            sqlRep += curQuery.qryStr +  "(";
        }

        //This loop will be used to create the appropriate nesting which can be specify as
        //input parameter to the method nestQuery. In each nesting which store the binds attributes
        //and as key we store the nesting level
        for(int curLev=1; curLev < nestLev +1; curLev++)
        {
            if( curLev == nestLev  )
                isNest = false;

            //It creates the right format
            sqlRep += " (";

            //It create the inner queries
            curQuery =  genQuery( levFrmBinds, ++uniqID, isNest, curQuery.isOneAt, confPar);

            //It creates the right format
            sqlRep +="\n\t" + curQuery.qryStr;

            //We retrieve all the attributes that are selected in the FROM clause
            //from the outer sql. In other words, we store the binds attributes
            levFrmBinds = copySelRelts(levFrmBinds,curQuery.selRelts);
        }

        for(int i=0; i< nestLev +1; i++)
        {
            sqlRep += ")";
        }

        return sqlRep;
    }

    public String operQuery(long uniqID, ConfParameters confPar)
    {
        OPERATORS opert = new OPERATORS(confPar);

        String stm="";
        QRYREPRES res=null;

        String outterQry;
        SQLQUERY newSQL = new SQLQUERY();

        res = newSQL.genQuery(null,uniqID, false, false, confPar);
        outterQry = res.qryStr;

        stm +=outterQry + "\n " + opert.getOper(null) + "\n";

        res = newSQL.genQuery(null,uniqID, false, false, confPar);
        outterQry = res.qryStr;
        stm += outterQry;

        return stm;
    }

    public LinkedList<Attribute> copySelRelts(LinkedList<Attribute> curSelRels, LinkedList<Attribute> newRelts)
    {

        for(Attribute newAttr: newRelts)
        {
            curSelRels.add(newAttr);
        }

        return  curSelRels;
    }

}