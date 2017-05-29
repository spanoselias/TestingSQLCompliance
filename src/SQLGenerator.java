import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

/**
 * The class SQLGenerator is used to generate SQL queries
 *
 *
 * @author ELIAS SPANOS
 * @version 0.0.1
 */
public class SQLGenerator
{
    public class GenQuery
    {
        private int isDistinct;
        private int isAttrs;
        private int whereNo;
        private LinkedList<String> alias =  genAlias();

        SELECT query;


        public GenQuery(boolean isDistinctIn, boolean isAttrsIn, int whereNo )
        {
            query = new SELECT(isDistinctIn, isAttrsIn,whereNo, alias);
        }

        public String getSqlQuery()
        {
            return this.query.getSelect();
        }


        /**
         *The genAlias methods is used to generate different alias that will be used in the SELECT clause
         *Thus, it generates different alias and store them in the linkedlist
         *
         */
        public LinkedList<String> genAlias()
        {
             LinkedList<String> tmpAlias = new LinkedList<>();

            char[] attr = new char[57];

            int k = 0;
            for (int i = 0; i < 26; i++)
            {
                for(int j=0; j < 26; j++)
                {
                    String newAlias= Character.toString( (char) (65 + (k++))) + j;
                    tmpAlias.add(newAlias);
                }
            }

            return tmpAlias;
        }
    }

    public static class SELECT
    {
        //This linkedlist holds different alias that can be used in SELECT clause
        private LinkedList<String> alias;

        private HashMap<String, LinkedList<String>> relAttr;

        //This hashtable will be used to store all the selected alias that exist
        //in this sql query. This is useful in case where we need to have subquery
        private HashMap<String, String> selectedAlias;

        private int whereNo;

        //It calls the FROM class to choose randomly some relations
        private FROM genFrom = new FROM();

        //It creates a randomly SQL FROM string
        private String stmFrom = genFrom.getFrom();

        //The  which relations are randomly selected
        private LinkedList<String> frmRels = genFrom.selectedReltsInFrom;

        private HashMap<String, LinkedList<String>> relAttrs = genFrom.getRelAttrs();

        private WHERE genWhere = new WHERE(relAttrs, frmRels, whereNo);

        private boolean isDistinct;
        private boolean isAllAttrs;

        public SELECT(boolean isDistinctIn, boolean isAttrsIn, int whereNoIn, LinkedList<String> aliasIn )
        {
            this.isDistinct = isDistinctIn;
            this.isAllAttrs = isAttrsIn;
            this.whereNo = whereNoIn;
            this.alias = aliasIn;

            this.relAttr = new HashMap<>();
            this.selectedAlias = new HashMap<>();
        }


        public String getSelect()
        {
            String stm = "SELECT";

            //This condition is used to check if the sql query will include DISTINCT keyword
            //in the SELECT statement
            if (this.isDistinct)
            {
                stm += " DISTINCT";
            }

            //This condition check if all the attributes should be included in the output
            if (isAllAttrs)
            {
                stm += " *";
            } else
                {

                boolean isOut = false;

                for (String relName : frmRels) {
                    //This loop will be used to go through all the attributed of the specific
                    //relation
                    for (int j = 0; j < relAttrs.get(relName).size(); j++)
                    {
                        if (isOut == false)
                        {
                            stm += String.format(" %s.%s AS %s", relName.toLowerCase(), relAttrs.get(relName).get(j), alias.get(j));
                            isOut = true;
                        }
                        else
                        {
                            stm += String.format(", %s.%s AS %s", relName.toLowerCase(), relAttrs.get(relName).get(j), alias.get(j));
                        }

                        //We need to store all the alias that we chose in the SELECT clause because they will
                        //be useful when we will implement subqueries. We store the as key (relation.attributeName) and as
                        //value the alias that we gave
                        String key = String.format("%s.%s", relName.toLowerCase(), relAttrs.get(relName).get(j));
                        selectedAlias.put(key, alias.get(j));
                    }
                }
            }

            //We retrieve the FROM SQL statement and we append it to the
            //final SQL string
            stm += "\n" + genFrom.getFromSql();

            //We retrieve the WHERE SQL statement and we append it to the
            //final SQL string
            stm += "\n" + genWhere.getSqlWhere();

            return stm;
        }
    }

    /**
     * The class FROM is used to create the FROM clause. The idea is to store
     * all the relations that will be randomly choose in the FROM clause in order
     * to pass them in the other classes. For example, we need to know in the SELECT &
     * WHERE Clause which relations are selected in the FROM clause
     *
     */
    public static class FROM
    {
        private Relation rel [];

        //This variable will be used to create the FROM Clause String
        private String fromStm;

        //This HashMap is used to store all the relations with their associated attributes. The
        //key represents the relation name and the list stores all the attributes for each key (relation)
        private HashMap<String, LinkedList<String>> allRelAttrs;

        //This LinkedList will be used to store all the relations that will be randomly chosen
        //for the FROM Clause
        private LinkedList<String> selectedReltsInFrom;


        public FROM( )
        {

            allRelAttrs = new HashMap<>();

            //This method is used to read all the relations and attributes from the configuration file (config.properties file)
            //and it stores them in the allRelAttrs hashMap
            readConfFile(  this.allRelAttrs);

            fromStm = "";


            //We copy all the relations from the LinkedList to an array in order to
            //shuffle the array and afterward to choose some random relation to create
            //the FROM Clause
            rel = new Relation [this.allRelAttrs.size()];
            int i=0;
            for (String relation : this.allRelAttrs.keySet()) {
                rel[i] = new Relation();

                rel[i].setRelName(relation);

                for (String attr : this.allRelAttrs.get(relation))
                {
                    rel[i].setAttrName(attr);
                }

                i++;
            }

            //This list will be used to track which relations have been selected from the generator. Thus, it will be useful
            //to know the relations in the FROM statement in order to know what attributes to include in the SELECT statement
            selectedReltsInFrom = new LinkedList<>();
        }

        private String getFrom()
        {
            //Clear LinkedList
            selectedReltsInFrom.clear();

            Random randomGenerator = new Random();

            int pickRand;

            //This random number indicates how many relation the FROM Clause will have
            pickRand = (randomGenerator.nextInt(rel.length)) % rel.length + 1  ;

            //We shuffle the array of the relations to avoid choosing always the same order.
            shuffleArray(rel);

            String stm = "FROM";

            for (int i = 0; i < pickRand; i++)
            {
                this.selectedReltsInFrom.add(rel[i].getRelName());
                this.allRelAttrs.put(rel[i].getRelName(), rel[i].getRelAttrs());

                if (i == 0)
                    stm += String.format(" %s AS %s", rel[i].getRelName(), rel[i].getRelName().toLowerCase());
                else
                    stm += String.format(", %s AS %s", rel[i].getRelName(), rel[i].getRelName().toLowerCase());
            }

            fromStm = stm;

            return stm;
        }

        private String getFromSql() {
            return fromStm;
        }

        private LinkedList<String> getTables() {
            return this.selectedReltsInFrom;
        }

        private HashMap<String, LinkedList<String>> getRelAttrs() {
            return this.allRelAttrs;
        }
    }

    private static int genRandNo(int inputSize)
    {

        Random randomGenerator = new Random();

        int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

        return pickRand;
    }


    /**
     * The class WHERE is used to create the WHERE clause. In order to know
     * which attributes are valid to use the WHERE clause, we have a Linkedlist that
     * stores all the relations that are part of the FROM clause
     *
     */
        public static class WHERE
        {
            private String stm;
            private COMPARISON genCom;
            private int whereNo=-1;
            private String conn[] = {" AND", " OR"};


            /**
             *The WHERE constructor takes two parameters. The first one (allReltsWithAttrs) has all the relations
             *that our database schema has with the associated attributes for each relation. The second parameter
             *(selectedReltsInFrom)  is a linkedlist and it stores all the relations which are randomly choose for
             * the FROM CLAUSE.
             *
             *@param allReltsWithAttrs ,it stores all the relations with their accosiated attributes
             *@param selectedReltsInFrom ,it stores all the relations that have choose for the FROM CLAUSE
             */
            public WHERE(HashMap<String, LinkedList<String>> allReltsWithAttrs, LinkedList<String> selectedReltsInFrom)
            {
                stm = "WHERE ";

                genCom = new COMPARISON(allReltsWithAttrs, selectedReltsInFrom);
            }


            /**
             *The WHERE constructor takes two parameters. The first one (allReltsWithAttrs) has all the relations
             *that our database schema has with the associated attributes for each relation. The second parameter
             *(selectedReltsInFrom)  is a linkedlist and it stores all the relations which are randomly choose for
             * the FROM CLAUSE.
             *
             *@param allReltsWithAttrs ,it stores all the relations with their accosiated attributes
             *@param selectedReltsInFrom ,it stores all the relations that have choose for the FROM CLAUSE
             *@param whereNoComp ,it is an integer number which indicates how many connectivities (AND, OR ...) we will
             *have in the WHERE clause
             */
            public WHERE(HashMap<String, LinkedList<String>> allReltsWithAttrs, LinkedList<String> selectedReltsInFrom, int whereNoComp)
            {
                stm = "WHERE ";
                genCom = new COMPARISON(allReltsWithAttrs, selectedReltsInFrom);

                //The whereNo variable indicates the number of connectivities such as "AND", "OR"
                //we will have the WHERE sql statement
                this.whereNo = whereNoComp;
            }

            private String getSqlWhere()
            {
                if(whereNo != -1)
                {
                    for(int i=0; i< (whereNo +1) ; i++)
                    {
                        if(i==0)
                        {
                            stm += genCom.getAttrComparison();
                        }
                        else
                        {
                            stm += conn[genRandNo(conn.length)] + " " + genCom.getAttrComparison();
                        }
                    }
                }

                else
                {
                    stm += genCom.getAttrComparison();
                }

                return stm;

            }
        }

    public static class COMPARISON
    {
        private LinkedList<String> operators;
        private HashMap<String, LinkedList<String>> relAttrs;
        private LinkedList<String> selectedTables;

        public COMPARISON(HashMap<String, LinkedList<String>> relAttrs, LinkedList<String> selectedTablesIn)
        {
            //this.relAttrs = new HashMap<String, LinkedList<String>>();
            this.relAttrs = relAttrs;

            this.selectedTables = new LinkedList<String>();
            selectedTables = (LinkedList) selectedTablesIn.clone();

            this.operators = new LinkedList<>();
            this.operators.add("<");
            this.operators.add(">");
            this.operators.add("<=");
            this.operators.add(">=");
            this.operators.add("<>");
        }


        /**
         * This method generate numbers between 0..inputSize
         *We can use this number in order to choose randomly relations or attributes
         *Thus, the inputSize can be the total number of relations or attributes
         *
         *@param inputSize is the image that represents the NORTH Direction
         */
        private static int genRandNo(int inputSize)
        {
            Random randomGenerator = new Random();

            int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

            return pickRand;
        }

        private String getAttrComparison()
        {
            Random randomGenerator = new Random();

            int pickRand;
            int pickRandAttr;

            String oper = "";
            String rel1 = "";
            String rel2 = "";

            try
            {

                pickRand = genRandNo(this.operators.size());
                oper = this.operators.get(pickRand);

                pickRand = genRandNo(this.selectedTables.size());
                pickRandAttr = genRandNo(relAttrs.get(this.selectedTables.get(pickRand)).size());


                rel1 = this.selectedTables.get(pickRand) + "." + relAttrs.get(this.selectedTables.get(pickRand)).get(pickRandAttr);

                int prevPickRand = pickRand;
                int counter=0;

                //We are trying to do the comparison from two different relations
                do
                {
                    pickRand = genRandNo(this.selectedTables.size());

                    ++counter;
                } while(prevPickRand == pickRand && counter < 100);

                pickRandAttr = genRandNo(relAttrs.get(this.selectedTables.get(pickRand)).size());

                rel2 = this.selectedTables.get(pickRand) + "." + relAttrs.get(this.selectedTables.get(pickRand)).get(pickRandAttr);


            } catch (ArrayIndexOutOfBoundsException e)

            {
                System.out.println("Error in Comparison Class, getAttrComparison: " + e);
            }

            return rel1 + " " + oper + " " + rel2;
        }
    }

    public static void shuffleArray(Relation[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    private static void swap(Relation[] a, int i, int change) {
        Relation helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }

    public static void shuffleArray(LinkedList<Relation> a) {
        int n = a.size();
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    private static void swap(LinkedList<Relation> a, int i, int change) {
        Relation helper = a.get(i);
        a.set(i, a.get(change));
        a.set(change,helper);
    }

    public static void readConfFile(HashMap<String, LinkedList<String>> relAttrs)
    {
        Properties prop = new Properties();
        InputStream input = null;

        try
        {
            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            //We read all the relations from the configuration file. Then, we
            //store each relation in the HashMap
            String[] relations = prop.getProperty("relations").split(",");
            String[] attributes = prop.getProperty("attributes").split(",");
            for (String relation : relations)
            {
                LinkedList<String> attrList = new LinkedList<>();
                for (String attr : attributes)
                {
                    attrList.add(attr);
                }

                //We insert the relation (as key) in the hashMap, and a likedlist that stores all the
                //attributes for the specific relation
                relAttrs.put(relation, attrList);
            }

        } catch (IOException ex)
        {
            ex.printStackTrace();
        } finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args)
    {
        //It creates a new SQL generator engine
        SQLGenerator newGen = new SQLGenerator();

        SQLGenerator.GenQuery query =  newGen.new GenQuery(false,false, 2);

        System.out.println(query.getSqlQuery());

    }
}

