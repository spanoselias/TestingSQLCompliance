
import java.util.Random;

public class GeneratorEngine
{

    public String random_query()
    {
        String query=" ";

        return query;
    }

    public static String query()
    {
        String tables[] = {"R1", "R2", "R3", "R4", "R5", "R6"};

        int randTable=0;

        Random randomGenerator = new Random();


        randTable = randomGenerator.nextInt(tables.length);


        return tables[randTable];
    }


    public static void main(String[] args)
    {

        for (int i=0; i< 5; i++)
        {
            System.out.println(query());
        }

    }
}
