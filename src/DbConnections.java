import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/*import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import com.microsoft.sqlserver.jdbc.*;
import java.sql.DriverManager;*/

/**
 *
 *
 */

public class DbConnections
{
    public static void connectToMySql() {

            System.out.println("-------- MySQL JDBC Connection Testing ------------");

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println("Where is your MySQL JDBC Driver?");
                e.printStackTrace();
                return;
            }

            System.out.println("MySQL JDBC Driver Registered!");
            Connection connection = null;

            try {
                connection = DriverManager
                        .getConnection("jdbc:mysql://localhost:3306/testing", "elias881", "testing1");

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

        public static void connectToPostgres() {

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

        public static void connectToIBMDb2() {
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

        public static void connectToOracle() {
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

        public static void connectToMicrosoftSql() {
            Connection conn = null;

            try {

                String dbURL = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=testdb";
                //  String dbURL = "jdbc:sqlserver://localhost;user=elias;password=testing1; databaseName=testdb";

                // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                // the sql server url
                // String dbURL = "jdbc:microsoft:sqlserver://HOST:1433;DatabaseName=testdb";

                String user = "elias";
                String pass = "testing1";
                conn = DriverManager.getConnection(dbURL, user, pass);
                // conn = DriverManager.getConnection(dbURL);

                if (conn != null)
                {
                    DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
                    System.out.println("Driver name: " + dm.getDriverName());
                    System.out.println("Driver version: " + dm.getDriverVersion());
                    System.out.println("Product name: " + dm.getDatabaseProductName());
                    System.out.println("Product version: " + dm.getDatabaseProductVersion());
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }


        }

        public static void runAllDBMS()
        {
            System.out.println("*****************");
            connectToMySql();
            System.out.println("*****************");
            connectToPostgres();
            System.out.println("*****************");
            connectToIBMDb2();
            System.out.println("*****************");
            connectToMicrosoftSql();
            System.out.println("*****************");
        }

        public static void main(String[] args)

        {

            runAllDBMS();

        }
    }


