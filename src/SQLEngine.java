import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

/**
 * The class SQLEngine is used to produce thousands of
 * SQL queries in order to evaluate them on all DBMS
 *
 * @author ELIAS SPANOS
 * @version 0.0.1
 */
public class SQLEngine {

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

        //It creates a new SQL generator engine
        //SQLGenerator newEngine = new SQLGenerator(  relationsAttrs);

     //   System.out.println( newEngine.getGenerator().getSelect());

    }
}

