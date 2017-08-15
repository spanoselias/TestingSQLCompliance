package ComparisonTool;

/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                               */
/*Date: 5/06/2017                                                                  */
/*Filename: COMPARISONTool.java                                                    */
/*                                                                                 */
/***********************************************************************************/

import java.io.*;
import java.sql.*;
import java.util.*;

/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
public class ComparisonTool
{
    private ConfComparison confSettings=null;

    public ComparisonTool()
    {

        confSettings = readComprConfig();
    }

/***********************************************************************************/
/*                           DBMSs Configuration properties                        */
/***********************************************************************************/
public ConfComparison readComprConfig()
{
    Properties prop = new Properties();
    InputStream input = null;

    ConfComparison conComp= new ConfComparison();

    try
    {
        input = new FileInputStream("comparisonTool.properties");

        // load a properties file
        prop.load(input);

        conComp.MySQLUser = String.valueOf( prop.getProperty( "MySQLUser" ) );
        conComp.MySQLPass = String.valueOf( prop.getProperty( "MySQLPass" ) );
        conComp.MySQLDB = String.valueOf( prop.getProperty( "MySQLDB" ) );

        conComp.DB2User = String.valueOf( prop.getProperty( "DB2User" ) );
        conComp.DB2Pass = String.valueOf( prop.getProperty( "DB2Pass" ) );
        conComp.DB2DB = String.valueOf( prop.getProperty( "DB2DB" ) );

        conComp.MicrosoftSqlUser = String.valueOf( prop.getProperty( "MicrosoftSqlUser" ) );
        conComp.MicrosoftSqlPass = String.valueOf( prop.getProperty( "MicrosoftSqlPass" ) );
        conComp.MicrosoftSqlDB = String.valueOf( prop.getProperty( "MicrosoftSqlDB" ) );

        conComp.PostgreUser = String.valueOf( prop.getProperty( "PostgreUser" ) );
        conComp.PostgrePass = String.valueOf( prop.getProperty( "PostgrePass" ) );
        conComp.PostgreDB = String.valueOf( prop.getProperty( "PostgreDB" ) );

        conComp.OracleUser = String.valueOf( prop.getProperty( "OracleUser" ) );
        conComp.OraclePass = String.valueOf( prop.getProperty( "OraclePass" ) );
        conComp.OracleDB = String.valueOf( prop.getProperty( "OracleDB" ) );

    }
    catch (IOException ex)
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
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return conComp;
    }
}


/***********************************************************************************/
/*                           MySQL connection properties                           */
/***********************************************************************************/
public ResInfo connectToMySql(String sqlQuery)
{

    ResInfo info = new ResInfo();
    try
        {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e)
        {
             e.printStackTrace();
        }

        Connection conn = null;
        try
        {
            conn = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/" + confSettings.MySQLDB, confSettings.MySQLUser, confSettings.MySQLPass);

        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

        }
        finally
        {
            LinkedList<String> mySqlList = execQuery(conn, sqlQuery, info);
          //System.out.println("MySQlResSize: " + mySqlList.size());

            return info;
        }
    }

/***********************************************************************************/
/*                           PostgreSQL connection properties                      */
/***********************************************************************************/
public ResInfo connectToPostgres(String sqlQuery)
{

    ResInfo info = new ResInfo();

    try
    {
        Class.forName("org.postgresql.Driver");
    }
    catch (ClassNotFoundException e)
    {

        System.out.println("Where is your PostgreSQL JDBC Driver? "
                + "Include in your library path!");
        e.printStackTrace();
        //return;
    }

    System.out.println("PostgreSQL JDBC Driver Registered!");

    Connection connection = null;

    try
    {

        connection = DriverManager.getConnection(
                "jdbc:postgresql://127.0.0.1:5432/" + confSettings.PostgreDB, confSettings.PostgreUser,
                confSettings.PostgrePass);

    }
    catch (SQLException e)
    {

        System.out.println("Connection Failed! Check output console");
        e.printStackTrace();

    }
    finally
    {
        LinkedList<String> mySqlList = execQuery(connection, sqlQuery, info);
        //System.out.println("MySQlResSize: " + mySqlList.size());

        return info;
    }
}

/***********************************************************************************/
/*                          IBMDB2 connection properties                           */
/***********************************************************************************/
public ResInfo connectToIBMDb2(String sqlQry)
{

    ResInfo info = new ResInfo();

    String jdbcClassName = "com.ibm.db2.jcc.DB2Driver";

    String url = "jdbc:db2://localhost:50000/" + confSettings.DB2DB;
    String user = confSettings.DB2User;
    String password = confSettings.DB2Pass;

    Connection conn = null;
    try
    {
        //Load class into memory
        Class.forName("com.ibm.db2.jcc.DB2Driver");
        //Establish connection
        conn = DriverManager.getConnection(url, user, password);
        conn.setAutoCommit(false);

    }
    catch (ClassNotFoundException e)
    {
        e.printStackTrace();
    }
    catch (SQLException e)
    {
        e.printStackTrace();
    }
    finally
    {

    }

    String schemaName = "ELIAS";

    LinkedList<String> tableRes= new LinkedList<>();
    info.res = true;

    try
    {
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs ;

        // create the java statement
        Statement st = conn.createStatement();

        st.executeUpdate("set current sqlid = " + schemaName);

        // execute the query, and get a java resultset
        rs = st.executeQuery(sqlQry);

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        //  System.out.println("**********************************************");

        String newRow="";

        while (rs.next())
        {
            newRow = "";

            // The column count starts
            for (int i = 1; i <= columnCount; i++ )
            {
                String col = rsmd.getColumnLabel(i);

                if(i == columnCount-1)
                {
                    // newRow += String.valueOf(rs.getString(col)).trim().split(".")[0] + ",";
                    newRow += String.valueOf(rs.getString(col)).trim() + ",";
                }
                else
                {
                    //newRow += String.valueOf(rs.getString(col)).trim().split(".")[0] + ",";
                    newRow += String.valueOf(rs.getString(col)).trim() + ",";
                }
            }

            tableRes.add(newRow);
        }

        Collections.sort(tableRes);
    }
    catch (SQLException ex)
    {
        info.res = false;
        info.msg  = ex.toString();

        ex.printStackTrace();
        return null;
    } finally
    {
        try
        {
            if (conn != null && !conn.isClosed())
            {
                // conn.close();
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        info.mySqlList = tableRes;

        return info;
    }

}

/***********************************************************************************/
/*                          Oracle connection properties                           */
/***********************************************************************************/
public ResInfo connectToOracle(String sqlQuery )
{
        ResInfo info = new ResInfo();

        LinkedList<String> mySqlList=null;

        System.out.println("-------- Oracle JDBC Connection Testing ------");

        try
        {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        }
        catch (ClassNotFoundException e)
        {

            info.res = false;
            info.msg = e.toString();

            e.printStackTrace();
        //    return;

        }

        System.out.println("Oracle JDBC Driver Registered!");

        Connection connection = null;

        try
        {

           /* connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:" + confSettings.OracleDB +","+  confSettings.OracleUser  +","+ confSettings.OraclePass) ;*/

            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:orcl", "elias881", "testing1");


        } catch (SQLException e)
        {

            info.res = false;
            info.msg = e.toString();

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
           // return;
        }

        if (connection != null)
        {
            System.out.println("You made it, take control your database now!");
        } else
        {
            System.out.println("Failed to make connection!");
        }

        //Oracle does not support 'AS' in the FROM STATEMENT
       sqlQuery =  sqlQuery.replace("AS", " ");

        mySqlList = execQuery(connection, sqlQuery, info);
        info.mySqlList = mySqlList;

    return info;
}

/***********************************************************************************/
/*                  Microsoft SQL Server connection properties                     */
/***********************************************************************************/
public ResInfo connectToMicrosoftSql(String sqlQuery)
{
    ResInfo info = new ResInfo();

    Connection conn = null;
    LinkedList<String> mySqlList=null;
    try
    {
        String dbURL = "jdbc:sqlserver://localhost; databaseName=teststr";

        String user = "elias881";
        String pass = "testing1@";
        conn = DriverManager.getConnection(dbURL, user, pass);
        // conn = DriverManager.getConnection(dbURL);

        mySqlList = execQuery(conn, sqlQuery, info);
        //System.out.println("MS Server: " + mySqlList.size());

    } catch (SQLException ex)
    {
        ex.printStackTrace();
    } finally
    {
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

    return info;
}

/***********************************************************************************/
/*                            Query Execution                                      */
/***********************************************************************************/

public LinkedList<String> execQuery(Connection conn, String sqlQry, ResInfo info)
{
    LinkedList<String> tableRes= new LinkedList<>();
    info.res = true;

    try
    {
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs ;

        // create the java statement
        Statement st = conn.createStatement();

        // execute the query, and get a java resultset
        rs = st.executeQuery(sqlQry);

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

      //  System.out.println("**********************************************");

        String newRow="";

        while (rs.next())
        {
            newRow = "";

            // The column count starts
            for (int i = 1; i <= columnCount; i++ )
            {
                String col = rsmd.getColumnLabel(i);

                if(i == columnCount-1)
                {
                   // newRow += String.valueOf(rs.getString(col)).trim().split(".")[0] + ",";
                    newRow += String.valueOf(rs.getString(col)).trim() + ",";
                }
                else
                {
                    //newRow += String.valueOf(rs.getString(col)).trim().split(".")[0] + ",";
                    newRow += String.valueOf(rs.getString(col)).trim() + ",";
                }
            }

            tableRes.add(newRow);
        }

        Collections.sort(tableRes);
    }
    catch (SQLException ex)
    {
        info.res = false;
        info.msg  = ex.toString();

        ex.printStackTrace();
        return null;
    } finally
    {
        try
        {
            if (conn != null && !conn.isClosed())
            {
               // conn.close();
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        info.mySqlList = tableRes;

        return tableRes;
    }
}

/***********************************************************************************/
/*                           Results Comparison                                     */
/***********************************************************************************/
public boolean diff(LinkedList<String> MSServer, LinkedList<String> MySQL, LinkedList<String> OracleDB, LinkedList<String> PostGres, LinkedList<String> IBMDB2 )
{

    if(MSServer.size() != MySQL.size() || MSServer.size() != PostGres.size() || MSServer.size() != OracleDB.size() || MSServer.size() != IBMDB2.size() )
    {
        return false;
    }

    for(int i=0; i < MSServer.size(); i++)
    {
        if( MSServer.get(i).compareTo(MySQL.get(i)) != 0)
        {
            return false;
        }
        if( MSServer.get(i).compareTo(PostGres.get(i))!= 0 )
        {
            return false;

        }
        if( MSServer.get(i).compareTo(OracleDB.get(i))!= 0 )
        {
            return false;
        }

        if( MSServer.get(i).compareTo(IBMDB2.get(i))!= 0 )
        {
            return false;
        }
    }

    return true;
}
}


