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
        private FROM genFrom = new FROM();
        private String stmFrom = genFrom.getFrom();
        private LinkedList<String> frmTbls = genFrom.selectedTables;
        private HashMap<String, LinkedList<String>> relAttrs = genFrom.getRelAttrs();

        private boolean isDistinct;
        private boolean isAttrs;

        public SELECT(boolean isDistinct, boolean isAttrs)
        {
            this.isDistinct = isDistinct;
            this.isAttrs = isAttrs;
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

            if(isAttrs)
            {
                stm += " *";
            }
            else
            {
                for(int i=0; i < frmTbls.size(); i++)
                {
                    //This loop will be used to go through all the attributed of the specific
                    //relation
                    for(int j=0; j< relAttrs.get(frmTbls.get(i)).size(); j++  )
                    {
                        if(i ==0)
                            stm += String.format(" %s.%s", frmTbls.get(i),relAttrs.get(frmTbls.get(i)).get(j));
                        else
                            stm += String.format(",%s.%s", frmTbls.get(i),relAttrs.get(frmTbls.get(i)).get(j));;
                    }
                }
            }

            stm += "\n" + genFrom.getFrom();

            return stm;
        }
    }


    public static class FROM
    {

        //it is used temporary for debugging reasons
        private String rel[] = {"R1", "R2", "R3"};
        private String alias[] = {"r1", "r2", "r3"};

        //This hashmap is used to store the associated attributes for each relation. The
        //key represents the relation name and the list stores all the attributes for each key (relation)
        private HashMap<String, LinkedList<String>> relAttrs = new HashMap<>();

        LinkedList<String> selectedTables;
        public FROM()
        {
            selectedTables = new LinkedList<>();

            for (int i=0; i<rel.length; i++ )
            {
                LinkedList<String> listRelAttrs = new LinkedList<String>();
                listRelAttrs.add("A");
                listRelAttrs.add("B");
                relAttrs.put(rel[i], listRelAttrs );
            }
        }

        private String getFrom()
        {

            //Clear linkedlist
            selectedTables.clear();

            Random randomGenerator = new Random();

            int pickRand;
            pickRand = (randomGenerator.nextInt(rel.length) + 1) % rel.length ;
            shuffleArray(rel);
            String stm = "FROM";

            for(int i=0; i < pickRand; i++)
            {
                this.selectedTables.add(rel[i]);

                if(i ==0)
                    stm += String.format(" %s AS %s", rel[i], alias[i]);
                else
                    stm += String.format(", %s AS %s", rel[i], alias[i]);
            }

            return stm;
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


    public static void shuffleArray(String[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    private static void swap(String[] a, int i, int change) {
        String helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }


    public static void main(String[] args)
    {
      /*  for (int i=0; i< 5; i++)
        {
            System.out.println(query());
        }*/


      SELECT sel = new SELECT(false, false);


      System.out.println(sel.getSelect());

    }
}
