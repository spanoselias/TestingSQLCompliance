import java.util.Random;


/*

Create by Elias Spanos

 */
public class GeneratorEngine
{

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


    
  /*  public static String[] randomSelect()
    {


    }


    public static String[] randomFrom()
    {

    }*/

    public static void main(String[] args)
    {
        for (int i=0; i< 5; i++)
        {
            System.out.println(query());
        }

    }
}
