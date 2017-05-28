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
  /*  private SELECT selAccess;

    SQLGenerator( HashMap<String, LinkedList<String>> relAttrIn)
    {
          selAccess = new SELECT(true, true, relAttrIn);
    }

    public  SELECT getGenerator()
    {
        return this.selAccess;
    }*/

    public static class SELECT
    {
        private HashMap<String, LinkedList<String>> relAttr;

        //It calls the FROM class to choose randomly some relations
        private FROM genFrom = new FROM();

        //It creates a randomly SQL FROM string
        private String stmFrom = genFrom.getFrom();

        //The  which relations are randomly selected
        private LinkedList<String> frmRels = genFrom.selectedTables;

        private HashMap<String, LinkedList<String>> relAttrs = genFrom.getRelAttrs();

        private WHERE genWhere = new WHERE(relAttrs, frmRels, 2);

        private boolean isDistinct;
        private boolean isAllAttrs;

        public SELECT(boolean isDistinctIn, boolean isAttrsIn )
        {
            this.isDistinct = isDistinctIn;
            this.isAllAttrs = isAttrsIn;

            this.relAttr = new HashMap<>();
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
                            stm += String.format(" %s.%s", relName.toLowerCase(), relAttrs.get(relName).get(j));
                            isOut = true;
                        } else
                            stm += String.format(", %s.%s", relName.toLowerCase(), relAttrs.get(relName).get(j));
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

    public static class FROM
    {
        private Relation rel [];

        private String fromStm;

        //This HashMap is used to store the associated attributes for each relation. The
        //key represents the relation name and the list stores all the attributes for each key (relation)
        private HashMap<String, LinkedList<String>> relAttrs;

        LinkedList<String> selectedTables;

        public FROM( )
        {

            relAttrs = new HashMap<>();

            //This method is used to read all the relations and attributes from the configuration file (config.properties file)
            //and it stores them in the relationsAttrs hashMap
            readConfFile(  this.relAttrs);



            fromStm = "";

            rel = new Relation [this.relAttrs.size()];
            int i=0;
            for (String relation : this.relAttrs.keySet()) {
                rel[i] = new Relation();

                rel[i].setRelName(relation);

                for (String attr : this.relAttrs.get(relation))
                {
                    rel[i].setAttrName(attr);
                }

                i++;
            }

            //This list will be used to track which relations have been selected from the generator. Thus, it will be useful
            //to know the relations in the FROM statement in order to know what attributes to include in the SELECT statement
            selectedTables = new LinkedList<>();
        }

        private String getFrom()
        {
            //Clear linkedlist
            selectedTables.clear();

            Random randomGenerator = new Random();

            int pickRand;
            pickRand = (randomGenerator.nextInt(rel.length) % rel.length) + 1;
            shuffleArray(rel);
            String stm = "FROM";

            for (int i = 0; i < pickRand; i++)
            {
                this.selectedTables.add(rel[i].getRelName());
                this.relAttrs.put(rel[i].getRelName(), rel[i].getRelAttrs());

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
            return this.selectedTables;
        }

        private HashMap<String, LinkedList<String>> getRelAttrs() {
            return this.relAttrs;
        }
    }

    private static int genRandNo(int inputSize)
    {

        Random randomGenerator = new Random();

        int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

        return pickRand;
    }

    public static class WHERE
    {
        private String stm;
        private COMPARISON genCom;
        private int whereNo=-1;
        private String conn[] = {" AND", " OR"};

        public WHERE(HashMap<String, LinkedList<String>> relAttrs, LinkedList<String> selectedTablesIn)
        {
            stm = "WHERE ";

            genCom = new COMPARISON(relAttrs, selectedTablesIn);
        }

        public WHERE(HashMap<String, LinkedList<String>> relAttrs, LinkedList<String> selectedTablesIn, int whereNoComp)
        {
            stm = "WHERE ";
            genCom = new COMPARISON(relAttrs, selectedTablesIn);

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

        private static int genRandNo(int inputSize)
        {

            Random randomGenerator = new Random();

            int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

            return pickRand;
        }

        private String getAttrComparison() {
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

                //We are trying to do the comparison from two different relations
                do
                {
                    pickRand = genRandNo(this.selectedTables.size());

                } while(prevPickRand == pickRand);

                pickRandAttr = genRandNo(relAttrs.get(this.selectedTables.get(pickRand)).size());

                rel2 = this.selectedTables.get(pickRand) + "." + relAttrs.get(this.selectedTables.get(pickRand)).get(pickRandAttr);


            } catch (ArrayIndexOutOfBoundsException e)

            {
                System.out.println("Error in Comparison Class, getAttrComparison: " + e);
            }

            return rel1 + " " + oper + " " + rel2;

        }
    }


    public static char[] genAttr(int startCode, int endCode) {
        char[] attr = new char[57];

        int k = 0;
        for (int i = 0; i < 26; i++) {
            attr[i] = (char) (65 + (k++));
        }
        return attr;
    }

    public static String query() {

        Relation relations[] = new Relation[10];
        char attr[] = genAttr(1, 2);

        String rel = "R";

        for (int i = 0; i < 5; i++) {
            //Initialization & allocation of memory for each object relation
            relations[i] = new Relation();

            String buildRel = rel + (i + 1);
            relations[i].relName = buildRel;

            for (int j = 0; j < 5; j++) {
                relations[i].attributes.add(Character.toString(attr[j]));
            }
        }

        String tables[] = {"R1", "R2", "R3", "R4", "R5", "R6"};

        int randTable = 0;

        Random randomGenerator = new Random();

        randTable = randomGenerator.nextInt(tables.length);


        return tables[randTable];
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
        //This hashMap will be used to store all the attributes for each relation
        HashMap<String, LinkedList<String>> relationsAttrs = new HashMap<>();

        //This method is used to read all the relations and attributes from the configuration file (config.properties file)
        //and it stores them in the relationsAttrs hashMap
        readConfFile(relationsAttrs);

        SELECT sel = new SELECT(false, false );

        System.out.println(sel.getSelect());

    }
}

