import Engine.*;

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


            //It retrieves the parameters from the configuration file and store them
            //to the appropriate variables
            confPar.maxTableFrom = Integer.parseInt( prop.getProperty( "maxTablesFrom" ) );
            confPar.maxAttrSel = Integer.parseInt( prop.getProperty( "maxAttrSel" ) );
            confPar.maxCondWhere = Integer.parseInt( prop.getProperty( "maxCondWhere" ) );
            confPar.probWhrConst = Double.parseDouble( prop.getProperty( "probWhrConst" ) );
            confPar.nestLev =  Integer.parseInt( prop.getProperty( "nestLevel" ) );
            confPar.repAlias =  Double.parseDouble( prop.getProperty( "repAlias" ) );
            confPar.arithmCompar =  Double.parseDouble( prop.getProperty( "arithCompSel" ) );

            confPar.user =   prop.getProperty( "user" ) ;
            confPar.pass =   prop.getProperty( "pass" ) ;
            confPar.dbName =   prop.getProperty( "dbName" ) ;
            confPar.DBMS = prop.getProperty( "DBMS" ) ;

            //It retrieves all the relations with their associated attributes from mysql database
            retrieveDBSchema(confPar.relationsAttrs, confPar);

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

    public static void retrieveDBSchema( HashMap<String, LinkedList<String>> allRelAttr, ConfParameters confIn )
    {

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
                        "jdbc:postgresql://127.0.0.1:5432/test", confIn.user, confIn.pass);
            }

        LinkedList<String> tableRes= new LinkedList<>();


            DatabaseMetaData md = conn.getMetaData( );
            ResultSet rs = md.getTables(null, null, "R1", null);

            // create the java statement
         /*   Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            rs = st.executeQuery( retSchema );*/

            //This sql queries retrieve all the tables with their associated attributes
            if(confIn.DBMS.compareTo("mysql") == 0)
            {
                stm = conn.prepareStatement(  "SELECT TABLE_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA =?");
            }
            else
            {

                stm = conn.prepareStatement(  "SELECT TABLE_NAME, column_name\n" +
                        "FROM information_schema.columns\n" +
                        "where table_schema = 'public' AND\n" +
                        "table_catalog =?");
            }

            stm.setString(1, "test");

            rs = stm.executeQuery();

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

        }
        catch (SQLException ex)
        {
            unable2Conn = true;
            ex.printStackTrace();
        }

        finally
        {
            if(unable2Conn == true)
            {
                Properties prop = new Properties();
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
                    allRelAttr.put(relation, attrList);
                }
            }

            try
            {

                if (conn != null && !conn.isClosed())
                {
                    conn.close();
                }
            } catch (SQLException ex)
            {
                ex.printStackTrace();
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

        DbConnections dbcon = new DbConnections();
        LinkedList<String> myVal = dbcon.connectToMySql(sql);
        LinkedList<String> MSVal = dbcon.connectToMicrosoftSql(sql);
        LinkedList<String> oracleDb = dbcon.connectToMySql(sql);
        LinkedList<String> postgres = dbcon.connectToMicrosoftSql(sql);

       if( dbcon.diff(MSVal,myVal , oracleDb, postgres) == false )
       {
            System.out.println("Difference found!!!");

            try
            {
                fw = new FileWriter(filename, true);
                bw = new BufferedWriter(fw);
                bw.write("*******************************************************************\n");
                bw.write(sql);
                bw.write("\n*******************************************************************");

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

                } catch (IOException ex) {

                    ex.printStackTrace();

                }

             }
       }

    }

    public static int genRandChoice(int inputSize)
    {
        Random randomGenerator = new Random();

        int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

        return pickRand;
    }

    public static void main(String[] args)
    {

        HashMap<String, LinkedList<String>> allRelAttr = new HashMap<>();


        //It retrieves parameters from the configuration file
        ConfParameters confPar = readConfFile();

        //It retrieves all the relations which their associated attributes from MySQL database
        retrieveDBSchema(allRelAttr, confPar );

        long uniqID=0;

        int option  = Integer.parseInt(args[0]);

        LinkedList<String> frmRelts = new LinkedList<>();

        String qry="";

        int pick;


        SQLQURERY newSQL = new SQLQURERY();


        pick = genRandChoice(4);

        //The option is given as input parameter to the program
        switch(pick)
        {
            case 0:
                System.out.println("Complex query");
                System.out.println("*******************");
                qry  = newSQL.genCompQuery(1, frmRelts,1,false,false, confPar);
            break;

            case 1:
                System.out.println("Simple query");
                System.out.println("*******************");
                QRYREPRES res = newSQL.genQuery(null,uniqID, false, false, confPar);
                qry = res.qryStr;
                break;

            case 2:
                qry = newSQL.nestQuery(uniqID, confPar);
               // wrtSql2File("rand.", qry);
                break;

            case 3:
                QRYREPRES res1 = newSQL.aggrGuery(uniqID, confPar);
                qry = res1.qryStr;
                break;
        }
            System.out.println(qry);
            wrtSql2File("rand.sql",qry);


          //  String sql = nestQuery(uniqID, confPar);
          //  wrtSql2File("rand.sql",qry);

            //genLogFile(qry);


    }

}

