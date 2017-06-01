import Engine.FROM;
import Engine.SELECT;
import Engine.WHERE;


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
public class SQLEngine
{

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

    /**
     *The genAlias methods is used to generate random alias that will be used as alias for attributes
     * in the SELECT clause or for a subquery
     *
     */
    public static LinkedList<String> genAlias()
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

    public static String genQuery()
    {
        //This hashMap will be used to store all the attributes for each relation
        HashMap<String, LinkedList<String>> relationsAttrs = new HashMap<>();

        //This method is used to read all the relations and attributes from the configuration file (config.properties file)
        //and it stores them in the relationsAttrs hashMap
        readConfFile(relationsAttrs);

        LinkedList<String> alias =  genAlias();

        LinkedList<String> frmRelts = new LinkedList<>();

        FROM frmQry = new FROM(alias, relationsAttrs);
        WHERE whrQry = new WHERE(relationsAttrs);
        SELECT selQry = new SELECT(false,false, alias, 2,relationsAttrs);

        String stm = frmQry.getFrom();
        String tmpStm = selQry.getSelect(frmQry.getSelectedTables());
        String finalQry = tmpStm + "\n" + stm;

        finalQry += "\n" + whrQry.getSqlWhere(frmQry.getSelectedTables(),2);

        return  finalQry;

    }

    public static String genCompQuery(int subqry)
    {
        //This hashMap will be used to store all the attributes for each relation
        HashMap<String, LinkedList<String>> relationsAttrs = new HashMap<>();

        //This method is used to read all the relations and attributes from the configuration file (config.properties file)
        //and it stores them in the relationsAttrs hashMap
        readConfFile(relationsAttrs);

        LinkedList<String> alias =  genAlias();

        LinkedList<String> frmRelts = new LinkedList<>();

        //We create new objects for each statement
        FROM frmQry = new FROM(alias, relationsAttrs);
        WHERE whrQry = new WHERE(relationsAttrs);
        SELECT selQry = new SELECT(false,false, alias, 2,relationsAttrs);

        String substm="";

        while( (subqry--) > 0)
        {

            String subName = "Q" + subqry;

            String frmstm = frmQry.getFrom();
            String selstm = selQry.getSelect(frmQry.getSelectedTables());
            String whrstm = whrQry.getSqlWhere(frmQry.getSelectedTables(),2);

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
                frmRelts.add( subName + "." + attr );
            }
        }

        String from = frmQry.getFrom();
        for(String attr: frmQry.getSelectedTables())
        {
            frmRelts.add( attr );
        }

        String stm = from + " , " + substm;
        String tmpStm = selQry.getSelect(frmRelts);
        String finalQry = tmpStm + "\n" + stm;
        finalQry += "\n" + whrQry.getSqlWhere(frmRelts ,2);

      return  finalQry;

    }

    public static void main(String[] args)
    {
        System.out.println("Complex query");
        System.out.println("*******************");
        System.out.println(genCompQuery(2));


        System.out.println("\n*******************");
        System.out.println("Simple query");
        System.out.println("*******************");
        System.out.println(genQuery());

    }

}

