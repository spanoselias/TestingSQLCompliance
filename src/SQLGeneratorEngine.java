import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

/**
 * The class SQLGeneratorEngine is used to produce thousands of
 * SQL queries in order to evaluate them on all DBMS
 *
 * @author ELIAS SPANOS
 * @version 0.0.1
 */
public class SQLGeneratorEngine {

    public static void readConfFile(HashMap<String, LinkedList<String>> relAttrs)
    {
        Properties prop = new Properties();
        InputStream input = null;

        try
        {
            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
          /*  System.out.println(prop.getProperty("relations"));
            System.out.println(prop.getProperty("attributes"));*/

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

        HashMap<String, LinkedList<String>> relA = new HashMap<>();

        readConfFile(relA);

        System.out.println("Test");

    }
}

