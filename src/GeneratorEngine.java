import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/*
 Create by Elias Spanos
 */

public class GeneratorEngine
{
    private static class SELECT
    {
        //It calls the FROM class to choose randomly some relations
        private FROM genFrom = new FROM();

        //It creates a randomly FROM string
        private String stmFrom = genFrom.getFrom();

        //It is stored which relations are randomly selected
        private LinkedList<String> frmTbls = genFrom.selectedTables;

        private HashMap<String, LinkedList<String>> relAttrs = genFrom.getRelAttrs();


        private WHERE genWhere = new WHERE(relAttrs , frmTbls);

        private boolean isDistinct;
        private boolean isAllAttrs;

        public SELECT(boolean isDistinct, boolean isAttrs)
        {
            this.isDistinct = isDistinct;
            this.isAllAttrs = isAttrs;
        }

        private  String getSelect()
        {
             String stm = "SELECT";

            //This condition is used to check if the sql query will include DISTINCT keyword
            //in the SELECT statement
            if (this.isDistinct)
            {
                stm += " DISTINCT";
            }

            //This condition check if all the attributes should be included in the output
            if(isAllAttrs)
            {
                stm += " *";
            }

            else
            {

               boolean isOut=false;

               for (String relName : frmTbls)
                {
                    //This loop will be used to go through all the attributed of the specific
                    //relation
                    for(int j=0; j< relAttrs.get(relName).size(); j++  )
                    {
                        if(isOut ==false)
                        {
                            stm += String.format(" %s.%s", relName.toLowerCase(), relAttrs.get(relName).get(j));
                            isOut=true;
                        }
                        else
                            stm += String.format(", %s.%s", relName.toLowerCase(),relAttrs.get(relName).get(j));
                    }
                }
            }

            stm += "\n" + genFrom.getFromSql();

            stm += "\n" + genWhere.getSqlWhere();


            return stm;
        }

    }

    public static class FROM
    {
        private Relation rel[];
        private String fromStm;

        //This HashMap is used to store the associated attributes for each relation. The
        //key represents the relation name and the list stores all the attributes for each key (relation)
        private HashMap<String, LinkedList<String>> relAttrs = new HashMap<>();

        LinkedList<String> selectedTables;
        public FROM()
        {
             // Allocate memory for each object
             rel= new Relation[3];

             fromStm="";

            for(int i=0; i <3; i++ )
            {
                rel[i] = new Relation();

                rel[i].setRelName("R" + i);

                rel[i].setAttrName("A");
                rel[i].setAttrName("B");
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
            pickRand = ( randomGenerator.nextInt(rel.length) % rel.length ) + 1 ;
            shuffleArray(rel);
            String stm = "FROM";

            for(int i=0; i < pickRand; i++)
            {
                this.selectedTables.add(rel[i].getRelName());
                this.relAttrs.put(rel[i].getRelName(), rel[i].getRelAttrs());

                if(i ==0)
                    stm += String.format(" %s AS %s", rel[i].getRelName(), rel[i].getRelName().toLowerCase());
                else
                    stm += String.format(", %s AS %s",rel[i].getRelName(), rel[i].getRelName().toLowerCase());
            }

            fromStm=stm;

            return stm;
        }

        private String getFromSql()
        {
            return fromStm;
        }

        private LinkedList<String> getTables()
        {
            return this.selectedTables;
        }

        private HashMap<String, LinkedList<String>> getRelAttrs()
        {
            return this.relAttrs;
        }
    }

    public static class WHERE
    {
        private String stm;
        private COMPARISON genCom;

        public WHERE(HashMap<String, LinkedList<String>> relAttrs, LinkedList<String> selectedTablesIn)
        {
            stm = "WHERE ";
            genCom = new COMPARISON(relAttrs ,selectedTablesIn);
        }

        private String getWhere()
        {
            //Need to be expanded
            return  stm + genCom.getAttrComparison();
        }

        private String getSqlWhere()
        {
            return  stm + genCom.getAttrComparison();
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
                this.relAttrs=relAttrs;

                this.selectedTables = new  LinkedList<String>();
                selectedTables = (LinkedList) selectedTablesIn.clone();

                this.operators = new LinkedList<>();
                this.operators.add("<");
                this.operators.add(">");
                this.operators.add("<=");
                this.operators.add(">=");
                this.operators.add("<>");
            }

        private String getAttrComparison()
        {
            Random randomGenerator = new Random();

            int pickRand;
            int pickRandAttr;

            pickRand = ( randomGenerator.nextInt(this.operators.size()) ) % this.operators.size()  ;

            String oper = this.operators.get(pickRand);

            pickRand = ( randomGenerator.nextInt(this.selectedTables.size())) % this.selectedTables.size()   ;
            pickRandAttr = ( randomGenerator.nextInt(relAttrs.get(this.selectedTables.get(pickRand)).size())) % relAttrs.get(this.selectedTables.get(pickRand)).size()   ;

            String rel1 = this.selectedTables.get(pickRand) + "." + relAttrs.get(this.selectedTables.get(pickRand)).get(pickRandAttr);
            this.selectedTables.remove(pickRand);

            pickRand = ( randomGenerator.nextInt(this.selectedTables.size())) % this.selectedTables.size()   ;
            pickRandAttr = ( randomGenerator.nextInt(relAttrs.get(this.selectedTables.get(pickRand)).size()) ) % relAttrs.get(this.selectedTables.get(pickRand)).size()  ;

            String rel2 = this.selectedTables.get(pickRand) + "." + relAttrs.get(this.selectedTables.get(pickRand)).get(pickRandAttr);
            this.selectedTables.remove(pickRand);


            return rel1 + " " + oper + " " + rel2;
        }

    }


    public static String random_query()
    {
        String query=" ";

        return query;
    }
    

    public static char[] genAttr(int startCode, int endCode)
    {
        char[] attr = new char[57];

        int k = 0;
        for(int i = 0; i < 26; i++)
        {
            attr[i] = (char)(65 + (k++));
        }
        return attr;
    }

    public static String query()
    {

        Relation relations [] = new Relation[10];
        char attr [] = genAttr(1,2);

        String rel = "R";

        for(int i=0; i < 5; i++)
        {
            //Initialization & allocation of memory for each object relation
            relations[i] = new Relation();

            String buildRel = rel + (i + 1);
            relations[i].relName = buildRel;

            for(int j=0; j<5; j++)
            {
                relations[i].attributes.add(Character.toString(attr[j]));
            }
        }

        String tables[] = {"R1", "R2", "R3", "R4", "R5", "R6"};

        int randTable=0;

        Random randomGenerator = new Random();

        randTable = randomGenerator.nextInt(tables.length);


        return tables[randTable];
    }

    public static void shuffleArray(Relation[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++)
        {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    private static void swap(Relation[] a, int i, int change)
    {
        Relation helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }

    public static void main(String[] args)
    {

      SELECT sel = new SELECT(false, false);


      System.out.println(sel.getSelect());

    }
}
