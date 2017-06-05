import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

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

public class DbConnections
{
    public  void connectToMySql(String sqlQuery)
    {

            System.out.println("-------- MySQL JDBC Connection Testing ------------");

            try
            {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e)
            {
                System.out.println("Where is your MySQL JDBC Driver?");
                e.printStackTrace();
                return;
            }

            System.out.println("MySQL JDBC Driver Registered!");
            Connection conn = null;

            try
            {
                conn = DriverManager
                        .getConnection("jdbc:mysql://localhost:3306/testdb", "elias881", "testing1");

            } catch (SQLException e)
            {
                System.out.println("Connection Failed! Check output console");
                e.printStackTrace();
                return;
            }

            LinkedList<String> mySqlList = execQuery(conn, sqlQuery);
            System.out.println("MySQlResSize: " + mySqlList.size());
        }

        public  void connectToPostgres()
        {

            System.out.println("-------- PostgreSQL "
                    + "JDBC Connection Testing ------------");

            try {

                Class.forName("org.postgresql.Driver");

            } catch (ClassNotFoundException e) {

                System.out.println("Where is your PostgreSQL JDBC Driver? "
                        + "Include in your library path!");
                e.printStackTrace();
                return;

            }

            System.out.println("PostgreSQL JDBC Driver Registered!");

            Connection connection = null;

            try {

                connection = DriverManager.getConnection(
                        "jdbc:postgresql://127.0.0.1:5432/testdb", "postgres",
                        "testing1");

            } catch (SQLException e)
            {

                System.out.println("Connection Failed! Check output console");
                e.printStackTrace();
                return;

            }

            if (connection != null) {
                System.out.println("You made it, take control your database now!");
            } else {
                System.out.println("Failed to make connection!");
            }

        }

        public  void connectToIBMDb2() {
            String jdbcClassName = "com.ibm.db2.jcc.DB2Driver";

            String url = "jdbc:db2://localhost:50000/SAMPLE";
            String user = "db2admin";
            String password = "testing1";

            Connection connection = null;
            try {
                //Load class into memory
                Class.forName("com.ibm.db2.jcc.DB2Driver");
                //Establish connection
                connection = DriverManager.getConnection(url, user, password);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    System.out.println("IDB DB2 Connected successfully.");
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    public  void connectToOracle() {
            System.out.println("-------- Oracle JDBC Connection Testing ------");

            try {

                Class.forName("oracle.jdbc.driver.OracleDriver");

            } catch (ClassNotFoundException e) {

                System.out.println("Where is your Oracle JDBC Driver?");
                e.printStackTrace();
                return;

            }

            System.out.println("Oracle JDBC Driver Registered!");

            Connection connection = null;

            try {

                connection = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:TEMP", "elias881", "testing1");

            } catch (SQLException e) {

                System.out.println("Connection Failed! Check output console");
                e.printStackTrace();
                return;

            }

            if (connection != null) {
                System.out.println("You made it, take control your database now!");
            } else {
                System.out.println("Failed to make connection!");
            }

        }

    public  void connectToMicrosoftSql(String sqlQuery) {
        Connection conn = null;

        try {

            //String dbURL = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=testdb";
            String dbURL = "jdbc:sqlserver://localhost; databaseName=testdb";
            //  String dbURL = "jdbc:sqlserver://localhost;user=elias;password=testing1; databaseName=testdb";

            // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // the sql server url
            // String dbURL = "jdbc:microsoft:sqlserver://HOST:1433;DatabaseName=testdb";

            String user = "elias881";
            String pass = "testing1";
            conn = DriverManager.getConnection(dbURL, user, pass);
            // conn = DriverManager.getConnection(dbURL);

        /*    if (conn != null)
            {
                DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
                System.out.println("Driver name: " + dm.getDriverName());
                System.out.println("Driver version: " + dm.getDriverVersion());
                System.out.println("Product name: " + dm.getDatabaseProductName());
                System.out.println("Product version: " + dm.getDatabaseProductVersion());
            }*/

        /*    DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "R1", null);


            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            rs = st.executeQuery(sqlQuery);


            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            System.out.println("**********************************************");

            LinkedList<String> tableRes= new LinkedList<>();
            String newRow="";
            while (rs.next())
            {
                newRow = "";

                // The column count starts from 1
                for (int i = 1; i <= columnCount; i++ )
                {
                    String col = rsmd.getColumnName(i);

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
                    tableRes.add(newRow);
                }

                Collections.sort(tableRes);
                //tableRes.forEach(System.out::println);*/

            LinkedList<String> mySqlList = execQuery(conn, sqlQuery);
            System.out.println("MS Server: " + mySqlList.size());


          /*  System.out.println("**********************************************");


            System.out.println("***********************************************");
            System.out.println(sqlQuery);
            System.out.println("**********************************************");*/


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
            } catch (SQLException ex) {
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
            ResultSet rs = md.getTables(null, null, "R1", null);

            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            rs = st.executeQuery(sqlQry);

            ResultSetMetaData rsmd = rs.getMetaData();
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
                    tableRes.add(newRow);
                }

                Collections.sort(tableRes);
                //tableRes.forEach(System.out::println);
             //   System.out.println(tableRes.size());
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

        return tableRes;
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

       /* public static void main(String[] args)

        {

            runAllDBMS();

        }*/
    }


