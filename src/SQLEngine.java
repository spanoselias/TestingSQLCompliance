/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                               */
/*Date: 5/06/2017                                                                  */
/*Filename: SQLEngine.java                                                         */
/*                                                                                 */
/***********************************************************************************/
import ComparisonTool.ComparisonTool;
import Engine.*;
import  ComparisonTool.ResInfo;

/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;

/**
 * The class SQLEngine is used to produce thousands of
 * SQL queries in order to evaluate them on all DBMS
 *
 * @author ELIAS SPANOS
 * @version 0.0.2
 */
public class SQLEngine
{
    public static ConfParameters readConfFile()
    {
        Properties prop = new Properties();
        InputStream input = null;

        ConfParameters confPar = new ConfParameters();

        HashMap<String, LinkedList<String>> allRelAttr = new HashMap<>();

        try
        {
            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            //It retrieves the parameters from the configuration file and store them
            //to the appropriate variables
            confPar.maxTableFrom = Integer.parseInt( prop.getProperty( "maxTablesFrom" ) );
            confPar.maxAttrSel = Integer.parseInt( prop.getProperty( "maxAttrSel" ) );
            confPar.maxCondWhere = Integer.parseInt( prop.getProperty( "maxCondWhere" ) );
            confPar.probWhrConst = Double.parseDouble( prop.getProperty( "probWhrConst" ) );
            confPar.nestLev =  Integer.parseInt( prop.getProperty( "nestLevel" ) );
            confPar.repAlias =  Double.parseDouble( prop.getProperty( "repAlias" ) );
            confPar.arithmCompar =  Double.parseDouble( prop.getProperty( "arithCompSel" ) );
            confPar.maxAttrGrpBy =  Integer.parseInt( prop.getProperty( "maxAttrGrpBy" ) );
            confPar.isDistinct = Double.parseDouble( prop.getProperty( "distinct" ) );
            confPar.stringInSel = Double.parseDouble( prop.getProperty( "stringInSel" ) );
            confPar.stringInWhere = Double.parseDouble( prop.getProperty( "stringInWhere" ) );
            confPar.rowcompar = Double.parseDouble( prop.getProperty( "rowcompar" ) );

            confPar.user =   prop.getProperty( "user" ) ;
            confPar.pass =   prop.getProperty( "pass" ) ;
            confPar.dbName =   prop.getProperty( "dbName" ) ;
            confPar.DBMS = prop.getProperty( "DBMS" ) ;

            //It retrieves all the relations with their associated attributes from mysql database
            retrieveDBSchema(confPar.relationsAttrs, confPar);

        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
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

            confPar.genAlias = genAlias();

            return confPar;
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

    //The purpose of this method is to retrieve the schema of db in order to generate random sql queries based on this schema
    public static void retrieveDBSchema( HashMap<String, LinkedList<Attribute>> allRelAttr, ConfParameters confIn ) throws IOException {

        boolean unable2Conn=false;

        PreparedStatement stm;

        String connStr = "jdbc:mysql://localhost:3306/" + confIn.dbName;

        try
        {
            if(confIn.DBMS.compareTo("mysql") == 0)
            {
                Class.forName("com.mysql.jdbc.Driver");
            }
            else
            {
                Class.forName("org.postgresql.Driver");
            }
        }
        catch (ClassNotFoundException e)
        {
            unable2Conn = true;
            e.printStackTrace();
        }
    finally
        {

        Connection conn = null;

       try
       {
            if(confIn.DBMS.compareTo("mysql") == 0)
            {
                conn = DriverManager
                        .getConnection(connStr,confIn.user, confIn.pass);
            }
            else
            {

                conn = DriverManager.getConnection(
                        "jdbc:postgresql://127.0.0.1:5432/" + confIn.dbName, confIn.user, confIn.pass);
            }


            LinkedList<String> tableRes= new LinkedList<>();

            DatabaseMetaData md = conn.getMetaData( );
            ResultSet rs = md.getTables(null, null, "R1", null);

            // create the java statement
           /*  Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            rs = st.executeQuery( retSchema );*/

            //This sql queries retrieve all the tables with their associated attributes
            if(confIn.DBMS.compareTo("mysql") == 0)
            {
                stm = conn.prepareStatement(  "SELECT TABLE_NAME, COLUMN_NAME, data_type FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA =?");
            }
            else
            {
                stm = conn.prepareStatement(  "SELECT TABLE_NAME, column_name, data_type\n" +
                        "FROM information_schema.columns\n" +
                        "where table_schema = 'public' AND\n" +
                        "table_catalog =?");
            }

            stm.setString(1, confIn.dbName);

            rs = stm.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

           // System.out.println("**********************************************");

            String newRow="";
            String prevName = "";

            LinkedList<Attribute> curAttr = new LinkedList<>();
            LinkedList<Attribute> strAttr = new LinkedList<>();

            if(rs.next() == false)
            {
                System.out.println("Error!!There is NO SCHEMA TO RETRIEVE!\nThe schema is retrieved from the configuration file");
                unable2Conn=true;
            }

            while (rs.next())
            {
                Attribute newAttr = new Attribute();
                String relName = rs.getString("TABLE_NAME");

                if(prevName == "")
                {
                    prevName = relName;
                    newAttr.attrName = rs.getString("COLUMN_NAME");
                    newAttr.attrType = rs.getString("data_type");

                    //
                    String name = prevName + "." + newAttr.attrName;
                    confIn.typeOfAllAttr.put(name,newAttr.attrType);


                    String type = rs.getString("data_type");
                    if(  Arrays.asList("text", "character varying").contains(type) == true)
                    {
                        strAttr.add(newAttr);
                    }
                    else
                    {
                        curAttr.add(newAttr);
                    }
                }

                else if(prevName.equals( relName))
                {
                    newAttr.attrName = rs.getString("COLUMN_NAME");
                    newAttr.attrType = rs.getString("data_type");

                    String name = prevName + "." + newAttr.attrName;
                    confIn.typeOfAllAttr.put(name,newAttr.attrType);

                    String type = rs.getString("data_type");
                    if(  Arrays.asList("text", "character varying").contains(type) == true)
                    {
                        strAttr.add(newAttr);
                    }
                    else
                    {
                        curAttr.add(newAttr);
                    }
                }
                else if( ! prevName.equals( relName))
                {
                    allRelAttr.put(prevName, ((LinkedList<Attribute>)curAttr.clone()));

                    if(strAttr.size() > 0)
                    {
                        confIn.strAttrs.put(prevName,((LinkedList<Attribute>)strAttr.clone() ));
                        strAttr.clear();
                    }

                    curAttr.clear();

                    prevName = relName;

                    newAttr.attrName = rs.getString("COLUMN_NAME");
                    newAttr.attrType = rs.getString("data_type");

                    String name = prevName + "." + newAttr.attrName;
                    confIn.typeOfAllAttr.put(name,newAttr.attrType);

                    String type = rs.getString("data_type");
                    if(  Arrays.asList("text", "character varying").contains(type) == true)
                    {
                        strAttr.add(newAttr);
                    }
                    else
                    {
                        curAttr.add(newAttr);
                    }
                }
            }

           //In case where there is only one relation
           if(curAttr.isEmpty() == false && prevName != "")
           {
               if(curAttr.size() > 0)
               {
                   allRelAttr.put(prevName, ((LinkedList<Attribute>)curAttr.clone()));
               }
               if(strAttr.size() > 0)
               {
                   confIn.strAttrs.put(prevName,((LinkedList<Attribute>)strAttr.clone() ));
               }
           }
       }
        catch (SQLException ex)
        {
            unable2Conn = true;
        }

    finally
    {
        //In case where the schema of the DB cannot be retrieved from the current databases, then, we retrieve
        // it from the configuration file
        if(unable2Conn == true)
         {
             System.out.println("Error");

             Properties prop = new Properties();
             InputStream input = null;

             input = new FileInputStream("config.properties");

             // load a properties file
             prop.load(input);

             String[] relations = prop.getProperty("relations").split(",");
             String[] attributes = prop.getProperty("attributes").split(",");
             for (String relation : relations)
             {
                 LinkedList<Attribute> attrList = new LinkedList<>();
                 for (String attr : attributes)
                 {
                     Attribute newAttr = new Attribute();
                     newAttr.attrName = attr;
                     attrList.add(newAttr);
                 }

                 //We insert the relation (as key) in the hashMap, and a likedlist that stores all the
                 //attributes for the specific relation
                 allRelAttr.put(relation, attrList);
             }

             confIn.relationsAttrs = allRelAttr;
         }
        try
            {
                if (conn != null && !conn.isClosed())
                {
                    conn.close();
                }
            }
            catch (SQLException ex)
            {
              //  ex.printStackTrace();
            }
        }

        }
    }

    public static void genStrings(  ConfParameters confIn ) throws IOException
    {
        boolean unable2Conn = false;

        PreparedStatement stm;
        Connection conn = null;

        String connStr = "jdbc:mysql://localhost:3306/" + confIn.dbName;

        try
        {
            if (confIn.DBMS.compareTo("mysql") == 0)
            {
                Class.forName("com.mysql.jdbc.Driver");
            } else
                {
                Class.forName("org.postgresql.Driver");
                }
        } catch (ClassNotFoundException e) {
            unable2Conn = true;
            e.printStackTrace();
        } finally
        {

            try
            {
                if (confIn.DBMS.compareTo("mysql") == 0)
                {
                    conn = DriverManager
                            .getConnection(connStr, confIn.user, confIn.pass);
                }
                else
                {
                    conn = DriverManager.getConnection(
                            "jdbc:postgresql://127.0.0.1:5432/" + confIn.dbName, confIn.user, confIn.pass);
                }

                //System.out.println("**********************************************");
            }
            catch (SQLException ex)
            {
                unable2Conn = true;
                //  ex.printStackTrace();
            }
            finally
            {
                //In case where the schema of the DB cannot be retrieved from the current databases, then, we retrieve
                // it from the configuration file
                if (unable2Conn == true)
                {
                    System.out.println("Error");
                }
            }

            ComparisonTool ctool = new ComparisonTool();
            String sql="";
            LinkedList<String> allStrings = new LinkedList<>();

            if(confIn.strAttrs.size() > 0)
            {

                for(String relname: confIn.strAttrs.keySet())
                {
                    for (Attribute attr : confIn.strAttrs.get(relname))
                    {
                        sql = "SELECT " + attr.attrName + " FROM " + relname;
                    }

                    ResInfo info = new ResInfo();
                    LinkedList<String> res = ctool.execQuery(conn, sql, info);
                    allStrings.addAll(res);
                }

                confIn.dictonary = allStrings;

            }
        }
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

            try
            {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }

    }

    public static void genLogFile( String sql)
    {

      //  DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();

        BufferedWriter bw = null;
        FileWriter fw = null;

        String path = "C:\\Users\\Elias\\Documents\\TestingSQLCompliance\\src\\Log\\";

     //   String filename = path + dateFormat.format(date);

        String filename = path + "1.log";

      //  String conten = String.valueOf(myval) + " : " + String.valueOf(msval) + "\n";

        ResInfo MySQL;
        ResInfo MicrosoftSQL ;
        ResInfo OracleDb ;
        ResInfo Postgres;


        ComparisonTool dbcon = new ComparisonTool();
        MySQL = dbcon.connectToMySql(sql);
        MicrosoftSQL = dbcon.connectToMicrosoftSql(sql);
        OracleDb = dbcon.connectToOracle(sql);
        Postgres = dbcon.connectToMicrosoftSql(sql);


        boolean isDiffFound = false;

       // LinkedList<String> DB2 = dbcon.connectToIBMDb2();

           //System.out.println("Difference found!!!");

            try
            {
                fw = new FileWriter(filename, true);
                bw = new BufferedWriter(fw);

                bw.write("*******************************************************************\n");
                bw.write("New Result!!\n");
                bw.write("+++++++++++++++++++++++++++++++++++++++++++++\n");
                if(MySQL.res == false)
                {
                    bw.write("----------------------------------------------------------\n");
                    bw.write("MySQl Error: \n");
                    bw.write(MySQL.msg);
                    bw.write("----------------------------------------------------------\n");
                    isDiffFound = true;
                }
                if(MicrosoftSQL.res == false)
                {
                    bw.write("----------------------------------------------------------\n");
                    bw.write("Microsoft SQL Server Error: \n");
                    bw.write(MicrosoftSQL.msg);
                    bw.write("----------------------------------------------------------\n");
                    isDiffFound = true;

                }
                if(OracleDb.res == false)
                {
                    bw.write("----------------------------------------------------------\n");
                    bw.write("Oracle DB Error: \n");
                    bw.write(OracleDb.msg);
                    bw.write("----------------------------------------------------------\n");
                    isDiffFound = true;

                }
                if(Postgres.res == false)
                {
                    bw.write("----------------------------------------------------------\n");
                    bw.write("PostgreSQL Error: \n");
                    bw.write(Postgres.msg);
                    bw.write("----------------------------------------------------------\n");
                    isDiffFound = true;

                }

                if( dbcon.diff(MicrosoftSQL.mySqlList, MySQL.mySqlList , OracleDb.mySqlList,Postgres.mySqlList ) == false)
                {
                    isDiffFound = true;
                }

            if(isDiffFound == true)
            {
                bw.write(sql);
                bw.write("\n*******************************************************************");
            }


            } catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (bw != null)
                        bw.close();

                    if (fw != null)
                        fw.close();

                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
             }
    }


    /***********************************************************************************/
    /*                                     MAIN CLASS                                  */
    /***********************************************************************************/
    public static void main(String[] args) throws IOException {

        //It retrieves parameters from the configuration file
        ConfParameters confPar = readConfFile();

        genStrings(confPar);

        long uniqID=0;

        LinkedList<Attribute> frmRelts = new LinkedList<>();

        String qry="";

        int pick;

        SQLQUERY newSQL = new SQLQUERY();

        int counter=0;

  //  while(true)
    //{

        pick = Utilities.getRandChoice(4);


               //The option is given as input parameter to the program
            switch (pick)
            {
                case 0:
                    qry = newSQL.genCompQuery(1, frmRelts, 1, false, false, confPar);
                break;

                case 1:
                    QRYREPRES res = newSQL.genQuery(null, uniqID, false, false, confPar, 0);
                    qry = res.qryStr;
                break;

                case 2:
                    qry = newSQL.nestQuery(uniqID, confPar);
                break;

                case 3:
                    QRYREPRES res1 = newSQL.aggrGuery(uniqID, confPar);
                    qry = res1.qryStr;
                break;

                case 4:
                    QRYREPRES res2 = newSQL.operQuery(null, uniqID, false, false, confPar);
                    qry = res2.qryStr;
                break;
            }

            qry = "SELECT DISTINCT r11.c1 AS A1, r11.c2 AS A2\n" +
                    "FROM r1 AS r11\n" +
                    "WHERE (r11.c2 IS NOT NULL AND '770 Armistice Crossing,' >= '9576 Prentice Drive,' )";

            System.out.println(counter++);

               /* QRYREPRES res1 = newSQL.operQuery(null,uniqID, false, false, confPar);
                qry = res1.qryStr;*/

            System.out.println(qry);
            wrtSql2File("rand.sql", qry);

            // String sql = nestQuery(uniqID, confPar);
              wrtSql2File("rand.sql",qry);

             genLogFile(qry);

   // }

    }

}

