import Engine.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

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
    public static ConfParameters readConfFile()
    {
        Properties prop = new Properties();
        InputStream input = null;

        ConfParameters confPar = new ConfParameters();

        try
        {
            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            //We read all the relations from the configuration file. Then, we
            //store each relation in the HashMap
       /*   String[] relations = prop.getProperty("relations").split(",");
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
            }*/

            //It retrieves the parameters from the configuration file and store them
            //to the appropriate variables
            confPar.maxTableFrom = Integer.parseInt( prop.getProperty( "maxTablesFrom" ) );
            confPar.maxAttrSel = Integer.parseInt( prop.getProperty( "maxAttrSel" ) );
            confPar.maxCondWhere = Integer.parseInt( prop.getProperty( "maxCondWhere" ) );
            confPar.probWhrConst = Double.parseDouble( prop.getProperty( "probWhrConst" ) );

            //It retrieves all the relations with their associated attributes from mysql database
            retrieveDBSchema(confPar.relationsAttrs);

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

        return confPar;
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
        for (int i = 0; i < 10; i++)
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

    public static void retrieveDBSchema( HashMap<String, LinkedList<String>> allRelAttr )
    {

        //This sql queries retrieve all the tables with their associated attributes
        String retSchema =  "SELECT TABLE_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'testdb'";

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }


        Connection conn = null;

        try
        {
            conn = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/testdb", "elias881", "testing1");

        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }

        LinkedList<String> tableRes= new LinkedList<>();

        try
        {
            DatabaseMetaData md = conn.getMetaData( );
            ResultSet rs = md.getTables(null, null, "R1", null);

            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            rs = st.executeQuery( retSchema );

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            System.out.println("**********************************************");

            String newRow="";
            String prevName = "";

            LinkedList<String> curAttr = new LinkedList<>();
            while (rs.next())
            {

                String relName = rs.getString("TABLE_NAME");

                if(prevName == "")
                {
                    prevName = relName;
                    curAttr.add(rs.getString("COLUMN_NAME"));
                }

                else if(prevName.equals( relName))
                {
                    curAttr.add(rs.getString("COLUMN_NAME"));
                }
                else if( ! prevName.equals( relName))
                {
                    allRelAttr.put(prevName, ((LinkedList<String>)curAttr.clone()));
                    curAttr.clear();
                    prevName = relName;

                    curAttr.add(rs.getString("COLUMN_NAME"));
                }
            }


        } catch (SQLException ex)
        {
            ex.printStackTrace();
        } finally
        {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static QRYREPRES genQuery( LinkedList<String> frmRelts, long uniqID, boolean isNest, boolean isOneAttr ,  ConfParameters confPar)
    {

        QRYREPRES res = new QRYREPRES();

        LinkedList<String> alias =  genAlias();

        String tmpStm="";
        String finalQry="";

        FROM frmQry = new FROM(alias, confPar.relationsAttrs, confPar);
        WHERE whrQry = new WHERE(confPar.relationsAttrs);
        SELECT selQry = new SELECT(false,false, alias, 2, confPar.relationsAttrs, confPar);

        String stm = frmQry.getFrom( (++uniqID) );

        //In case where this is a nested query then the frmRelts list
        //contains the binds attributes. Otherwise, we just select attributes from the from
        if(frmRelts != null && frmRelts.size() > 0)
        {
             frmRelts = copySelRelts(frmRelts, frmQry.getSelectedTables());
             tmpStm = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr, confPar.repAlias);
             finalQry = tmpStm + "\n" + stm;
             finalQry += "\n" + whrQry.getSqlWhere(frmRelts, isNest, confPar, 5);
        }

        else
        {
             tmpStm = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr, confPar.repAlias);
             finalQry = tmpStm + "\n" + stm;
             finalQry += "\n" + whrQry.getSqlWhere(frmQry.getSelectedTables(),isNest,  confPar, 5);
        }

        res.qryStr = finalQry;
        res.isOneAt = whrQry.getOneAttr();
        res.selRelts = frmQry.getSelectedTables();

        return res;
    }

    public static String genCompQuery(int subqry, LinkedList<String> frmRelts, long uniqID, boolean isNest, boolean isOneAttr,   ConfParameters confPar)
    {
        LinkedList<String> alias =  genAlias();

        //We create new objects for each statement
        FROM frmQry = new FROM(alias, confPar.relationsAttrs, confPar);
        WHERE whrQry = new WHERE(confPar.relationsAttrs);
        SELECT selQry = new SELECT(false,false, alias, 2,confPar.relationsAttrs, confPar);

        String substm="";

        while( (subqry--) > 0)
        {
            String subName = "Q" + subqry;

            String frmstm = frmQry.getFrom(++uniqID);
            String selstm = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr, confPar.repAlias);
            String whrstm = whrQry.getSqlWhere(frmQry.getSelectedTables(), false,  confPar, 3);

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
        String tmpStm = selQry.getSelect(frmRelts, isOneAttr, confPar.repAlias);
        String finalQry = tmpStm + "\n" + stm;
        finalQry += "\n" + whrQry.getSqlWhere(frmRelts , isNest,  confPar, 2);

      return  finalQry;

    }

    public static String nestQuery(int nestLev, long uniqID, ConfParameters confPar)
    {

        String sqlRep="";

        LinkedList<String> levFrmBinds = new LinkedList<>();

        //This hashmap will be used to store the bind attributes on each level. We cannot have
        // two same alias. Thus, we will track of the alias for each level. In addition, we can
        //access alias from an upper level just be look up in the hashmap. The key represent the level,
        //and the LinkedList will store all the alias that we bind for each query
        HashMap<Integer, LinkedList<String>> levBindAttrs = new LinkedHashMap<>();

        QRYREPRES curQuery = genQuery( levFrmBinds, ++uniqID, true, false, confPar);
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

            curQuery =  genQuery( levFrmBinds, ++uniqID, isNest, curQuery.isOneAt, confPar);
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
        HashMap<String, LinkedList<String>> allRelAttr = new HashMap<>();

        //It retrieves all the relations which their associated attributes from MySQL database
        retrieveDBSchema(allRelAttr);

        ConfParameters confPar = readConfFile();

        long uniqID=0;

        int option  = Integer.parseInt(args[0]);

        LinkedList<String> frmRelts = new LinkedList<>();

        String qry="";

        //The option is given as input parameter to the program
        switch(option)
        {
            case 1:
                System.out.println("Complex query");
                System.out.println("*******************");
                qry  = genCompQuery(1, frmRelts,1,false,false, confPar);
            break;

            case 2:
                System.out.println("Simple query");
                System.out.println("*******************");
                QRYREPRES res = genQuery(null,uniqID, false, false, confPar);
                qry = res.qryStr;
                break;

            case 3:

                qry = nestQuery(3, uniqID, confPar);
                wrtSql2File("rand_sql", qry);
                break;
        }

            System.out.println(qry);
            wrtSql2File("rand_sql",qry);



     /*  while(true)
        {

            String sql = nestQuery(9, uniqID);

            wrtSql2File("rand_sql",sql);
            DbConnections dbcon = new DbConnections();
            int myVal = dbcon.connectToMySql(sql);
            int MSVal = dbcon.connectToMicrosoftSql(sql);

            if(myVal != MSVal)
            {
                System.out.println(sql);
                break;
            }


        }*/

       // System.out.println("MYSql: " + myVal + " MS Server: " + MSVal);

    }

}

