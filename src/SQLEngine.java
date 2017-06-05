import Engine.FROM;
import Engine.SELECT;
import Engine.WHERE;
import Engine.QRYREPRES;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Properties;

import java.io.BufferedWriter;
import java.io.FileWriter;

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
            for(int j=0; j < 10; j++)
            {
                String newAlias= Character.toString( (char) (65 + (k))) + j;
                tmpAlias.add(newAlias);
            }
            k++;
        }

        return tmpAlias;
    }

    public static QRYREPRES genQuery( LinkedList<String> frmRelts, long uniqID, boolean isNest, boolean isOneAttr)
    {

        QRYREPRES res = new QRYREPRES();

        //This hashMap will be used to store all the attributes for each relation
        HashMap<String, LinkedList<String>> relationsAttrs = new HashMap<>();

        //This method is used to read all the relations and attributes from the configuration file (config.properties file)
        //and it stores them in the relationsAttrs hashMap
        readConfFile(relationsAttrs);

        LinkedList<String> alias =  genAlias();

        String tmpStm="";
        String finalQry="";

        FROM frmQry = new FROM(alias, relationsAttrs);
        WHERE whrQry = new WHERE(relationsAttrs);
        SELECT selQry = new SELECT(false,false, alias, 2,relationsAttrs);

        String stm = frmQry.getFrom( (++uniqID) );

        //In case where this is a nested query then the frmRelts list
        //contains the binds attributes. Otherwise, we just select attributes from the from
        if(frmRelts != null && frmRelts.size() > 0)
        {
             frmRelts = copySelRelts(frmRelts, frmQry.getSelectedTables());
             tmpStm = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr);
             finalQry = tmpStm + "\n" + stm;
             finalQry += "\n" + whrQry.getSqlWhere(frmRelts,2, isNest);
        }
        else
        {
             tmpStm = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr);
             finalQry = tmpStm + "\n" + stm;
             finalQry += "\n" + whrQry.getSqlWhere(frmQry.getSelectedTables(),5, isNest);
        }

        res.qryStr = finalQry;
        res.isOneAt = whrQry.getOneAttr();
        res.selRelts = frmQry.getSelectedTables();

        return res;
    }

    public static String genCompQuery(int subqry, LinkedList<String> frmRelts, long uniqID, boolean isNest, boolean isOneAttr)
    {
        //This hashMap will be used to store all the attributes for each relation
        HashMap<String, LinkedList<String>> relationsAttrs = new HashMap<>();

        //This method is used to read all the relations and attributes from the configuration file (config.properties file)
        //and it stores them in the relationsAttrs hashMap
        readConfFile(relationsAttrs);

        LinkedList<String> alias =  genAlias();

        //We create new objects for each statement
        FROM frmQry = new FROM(alias, relationsAttrs);
        WHERE whrQry = new WHERE(relationsAttrs);
        SELECT selQry = new SELECT(false,false, alias, 2,relationsAttrs);

        String substm="";

        while( (subqry--) > 0)
        {
            String subName = "Q" + subqry;

            String frmstm = frmQry.getFrom(++uniqID);
            String selstm = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr);
            String whrstm = whrQry.getSqlWhere(frmQry.getSelectedTables(),2, false);

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

        String from = frmQry.getFrom(++uniqID);
        for(String attr: frmQry.getSelectedTables())
        {
            frmRelts.add( attr );
        }

        String stm = from + " , " + substm;
        String tmpStm = selQry.getSelect(frmRelts, isOneAttr);
        String finalQry = tmpStm + "\n" + stm;
        finalQry += "\n" + whrQry.getSqlWhere(frmRelts ,2, isNest);

      return  finalQry;

    }

    //Not implemented yet
    public static String nestQuery(int nestLev, long uniqID)
    {
        String sqlRep="";

        LinkedList<String> levFrmBinds = new LinkedList<>();

        //This hashmap will be used to store the bind attributes on each level. We cannot have
        // two same alias. Thus, we will track of the alias for each level. In addition, we can
        //access alias from an upper level just be look up in the hashmap. The key represent the level,
        //and the LinkedList will store all the alias that we bind for each query
        HashMap<Integer, LinkedList<String>> levBindAttrs = new LinkedHashMap<>();

        QRYREPRES curQuery = genQuery( levFrmBinds, ++uniqID, true, false);
        levFrmBinds = copySelRelts(levFrmBinds,curQuery.selRelts);


        boolean isNest = true;

        if(nestLev >= 2)
        {
            sqlRep += curQuery.qryStr +  "(";
        }

        //This loop will be used to create the appropriate nesting which can be specify as
        //input parameter to the method nestQuery. In each nesting which store the binds attributes
        //and as key we store the nesting level
        for(int curLev=1; curLev < nestLev +1; curLev++)
        {
            if(curLev == nestLev  )
                isNest = false;

            sqlRep += " (";

            curQuery =  genQuery( levFrmBinds, ++uniqID, isNest, curQuery.isOneAt);
            sqlRep +="\n\t" + curQuery.qryStr;

            //We retrieve all the attributes that are selected in the FROM clause
            //from the outer sql. In other words, we store the binds attributes
            levFrmBinds = copySelRelts(levFrmBinds,curQuery.selRelts);

            //levBindAttrs.put(curLev, ((LinkedList<String>)levFrmBinds.clone()));

            if(isNest ==false)
                sqlRep += " )";
        }

        if(nestLev >= 2)
        {
            sqlRep += ")";
        }

        for(int i=0; i< nestLev-1; i++)
        {
            sqlRep += ")";
        }

        return sqlRep;

    }

    public static LinkedList<String> copySelRelts(LinkedList<String> curSelRels, LinkedList<String> newRelts)
    {

        for(String newAttr: newRelts)
        {
            curSelRels.add(newAttr);
        }

        return  curSelRels;
    }


    public static void wrtSql2File(String filename, String sql)
    {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try
        {
            fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);
            bw.write(sql);

            System.out.println("Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally
        {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }

    }

    public static void main(String[] args)
    {
        long uniqID=0;

        LinkedList<String> frmRelts = new LinkedList<>();

     /*   System.out.println("Complex query");
        System.out.println("*******************");
        String res = genCompQuery(1, frmRelts,1,false,false);
*/

        System.out.println("\n*******************");
        System.out.println("Simple query");
        System.out.println("*******************");
        QRYREPRES res = genQuery(null,uniqID, false, false);
        System.out.println(res.qryStr);

        wrtSql2File("rand_sql",res.qryStr);
        DbConnections dbcon = new DbConnections();
        dbcon.runAllDBMS(res.qryStr);

      //debugging mode
        //wrtSql2File("rand_sql",nestQuery(3, uniqID));
      //  connectToMicrosoftSql(nestQuery(2, uniqID));
    }

}

