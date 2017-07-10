import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/*import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import com.microsoft.sqlserver.jdbc.*;
import java.sql.DriverManager;*/

/**
 *
 * This tool is still in progress and will be used to evaluate all the DBMS
 *
 */

public class ComparisonTool
{
    public  LinkedList<String> connectToMySql(String sqlQuery)
    {
            try
            {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e)
            {
                 e.printStackTrace();
            }

          //  System.out.println("MySQL JDBC Driver Registered!");
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
            finally
            {
                LinkedList<String> mySqlList = execQuery(conn, sqlQuery);
              //System.out.println("MySQlResSize: " + mySqlList.size());

                return mySqlList;
            }
        }

    public LinkedList<String> connectToPostgres(String sqlQuery)
    {

      /*  System.out.println("-------- PostgreSQL "
                + "JDBC Connection Testing ------------");*/
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
                    "jdbc:postgresql://127.0.0.1:5432/testdb", "postgres",
                    "testing1");

        } catch (SQLException e)
        {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

        }
        finally
        {
            LinkedList<String> mySqlList = execQuery(connection, sqlQuery);
            //System.out.println("MySQlResSize: " + mySqlList.size());

            return mySqlList;
        }

      /*  if (connection != null)
        {
            System.out.println("You made it, take control your database now!");
        }
        else
        {
            System.out.println("Failed to make connection!");
        }*/

    }

    public  void connectToIBMDb2()
    {
        String jdbcClassName = "com.ibm.db2.jcc.DB2Driver";

        String url = "jdbc:db2://localhost:50000/testdb";
        String user = "elias881";
        String password = "testing1";

        Connection conn = null;
        try
        {
            //Load class into memory
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            //Establish connection
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);

        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
          /*  if (conn != null)
            {
                System.out.println("IDB DB2 Connected successfully.");
                try
                {
                   // conn.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }*/
        }

        String schemaName = "ELIAS";
        try
        {

            ResultSet rs;

            // create the java statement
            Statement st = conn.createStatement();


            st.executeUpdate("set current sqlid = " + schemaName);

            // execute the query, and get a java resultset
           // rs = st.executeQuery("SELECT A FROM r1");

           // rs = st.executeQuery(" SELECT  NAME FROM  SYSIBM.SYSTABLES WHERE creator = 'ELIAS'");
            rs = st.executeQuery(" SELECT  A FROM  r1");

          //  DatabaseMetaData md = conn.getMetaData();
           // ResultSet rs = md.getTables(null, null, "R1", null);





       /*     ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            System.out.println("**********************************************");

            String newRow="";
            while (rs.next())
            {
                newRow = "";

                // The column count starts from 1
                for (int i = 1; i <= columnCount; i++ )
                {
                    String col = rsmd.getColumnLabel(i);

                    if(i != columnCount)
                    {
                        newRow += rs.getInt(col) + " , ";
                        //  System.out.print(rs.getInt(col) + " , ");
                    }
                    else
                    {
                        newRow += rs.getInt(col) + " , ";
                        //System.out.print(rs.getInt(col));
                    }

                }


                //tableRes.forEach(System.out::println);
                //   System.out.println(tableRes.size());
            }*/

            while (rs.next())
            {               // Position the cursor

                System.out.println(rs.getString("A"));
                //  empNo = rs.getString(1);             // Retrieve only the first column value
                //System.out.println("Employee number = " + empNo);
                // Print the column value
            }
            rs.close();                       // Close the ResultSet
            st.close();                     // Close the Statement

        } catch (SQLException ex)
        {
            ex.printStackTrace();
        } finally
        {
            try {
                if (conn != null && !conn.isClosed()) {
                 //   conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public  LinkedList<String> connectToOracle( String sqlQuery )
    {

            LinkedList<String> mySqlList=null;

            System.out.println("-------- Oracle JDBC Connection Testing ------");

            try {

                Class.forName("oracle.jdbc.driver.OracleDriver");

            } catch (ClassNotFoundException e)
            {

                System.out.println("Where is your Oracle JDBC Driver?");
                e.printStackTrace();
            //    return;

            }

            System.out.println("Oracle JDBC Driver Registered!");

            Connection connection = null;

            try
            {

                connection = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:orcl", "elias881", "testing1");

            } catch (SQLException e)
            {

                System.out.println("Connection Failed! Check output console");
                e.printStackTrace();
               // return;
            }

            if (connection != null) {
                System.out.println("You made it, take control your database now!");
            } else {
                System.out.println("Failed to make connection!");
            }

            //Oracle does not support 'AS' in the FROM STATEMENT
            sqlQuery.replace("AS", " ");

            mySqlList = execQuery(connection, sqlQuery);

        return mySqlList;
        }

    public  LinkedList<String> connectToMicrosoftSql(String sqlQuery)
    {
        Connection conn = null;
        LinkedList<String> mySqlList=null;
        try
        {

            String dbURL = "jdbc:sqlserver://localhost; databaseName=testdb";

            String user = "elias881";
            String pass = "testing1";
            conn = DriverManager.getConnection(dbURL, user, pass);
            // conn = DriverManager.getConnection(dbURL);

            mySqlList = execQuery(conn, sqlQuery);
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

        return mySqlList;
    }

    public static void genCsv( String row)
    {

        BufferedWriter bw = null;
        FileWriter fw = null;
        String path = "C:\\Users\\Elias\\Documents\\TestingSQLCompliance\\src\\";

        String filename = path + "1.csv";

            try
            {
                fw = new FileWriter(filename, true);
                bw = new BufferedWriter(fw);
                bw.write(row);
                bw.write("\n");

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

                } catch (IOException ex)
                {

                    ex.printStackTrace();

                }

            }
    }

    public LinkedList<String> execQuery(Connection conn, String sqlQry)
    {
        LinkedList<String> tableRes= new LinkedList<>();

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
            ex.printStackTrace();
            return null;
        } finally
        {
            try
            {
                if (conn != null && !conn.isClosed())
                {
                    conn.close();
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            return tableRes;
        }
    }

    public boolean diff(LinkedList<String> MSServer, LinkedList<String> MySQL, LinkedList<String> OracleDB, LinkedList<String> PostGres )
    {

        System.out.print("\n**************************\n");
        System.out.print("DIff: " + MSServer.size());
        System.out.print("\n**************************\n");

        if(MSServer.size() != MySQL.size() || MSServer.size() != PostGres.size())
        {
            return false;
        }

        for(int i=0; i < MSServer.size(); i++)
        {
            if( MSServer.get(i).compareTo(MySQL.get(i)) != 0  || MSServer.get(i).compareTo(PostGres.get(i))!= 0  ||MSServer.get(i).compareTo(OracleDB.get(i))!= 0 )
            {
                return false;
            }
        }
        return true;
    }

    public void runAllDBMS( String sqlquery)
    {
        System.out.println("*****************");
        connectToMySql(sqlquery);
        /*System.out.println("*****************");
        connectToPostgres();
        System.out.println("*****************");
        connectToIBMDb2();
        System.out.println("*****************");*/
        connectToMicrosoftSql(sqlquery);
        System.out.println("*****************");
    }

    }


