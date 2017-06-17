import Engine.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            confPar.nestLev =  Integer.parseInt( prop.getProperty( "nestLevel" ) );
            confPar.repAlias =  Double.parseDouble( prop.getProperty( "repAlias" ) );
            confPar.arithmCompar =  Double.parseDouble( prop.getProperty( "arithCompSel" ) );

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

        confPar.genAlias = genAlias();

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

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        finally
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

    }

    public static QRYREPRES aggrGuery(   long uniqID , ConfParameters confPar)
    {

        QRYREPRES res = new QRYREPRES();

        LinkedList<String> alias =  confPar.genAlias;

        String tmpStm="";
        String finalQry="";

        FROM frmQry = new FROM(alias, confPar.relationsAttrs, confPar);
        WHERE whrQry = new WHERE(confPar.relationsAttrs);
        SELECT selQry = new SELECT(false,false, alias, 2, confPar.relationsAttrs, confPar);
        GROUPBY grpQry = new GROUPBY(confPar);
        HAVING hvgQry = new HAVING(confPar);

        String stm = frmQry.getFrom( (++uniqID) );
        String grp = grpQry.getGroupBy(frmQry.getSelectedTables());
        String hvg = hvgQry.genHaving(grpQry.getAttrInGroup());
        tmpStm = selQry.getSelect(grpQry.getAttrInGroup(), false, false, confPar.repAlias, true, grpQry.getAttrInGroup());
        finalQry = tmpStm + "\n" + stm;
        finalQry += "\n" + whrQry.getSqlWhere(grpQry.getAttrInGroup(),false,  confPar, 5);
        finalQry += "\n" + grp + "\n" + hvg;

        res.qryStr = finalQry;
        res.isOneAt = whrQry.getOneAttr();
        res.selRelts = frmQry.getSelectedTables();

        return res;
    }

    public static QRYREPRES genQuery( LinkedList<String> frmRelts, long uniqID, boolean isNest, boolean isOneAttr ,  ConfParameters confPar)
    {

        QRYREPRES res = new QRYREPRES();

        LinkedList<String> alias =  confPar.genAlias;

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
            tmpStm = selQry.getSelect(frmRelts, isOneAttr, false, 0.1, false, null);
            finalQry = tmpStm + "\n" + stm;
            finalQry += "\n" + whrQry.getSqlWhere(frmRelts, isNest, confPar, 5);
        }
        else
        {
            tmpStm = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr, false, confPar.repAlias, false, null);
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
        LinkedList<String> alias =  confPar.genAlias;

        //We create new objects for each statement
        FROM frmQry = new FROM(alias, confPar.relationsAttrs, confPar);
        WHERE whrQry = new WHERE(confPar.relationsAttrs);
        SELECT selQry = new SELECT(false,false, alias, 2,confPar.relationsAttrs, confPar);

        String substm="";

        while( (subqry--) > 0)
        {
            String subName = "Q" + subqry;

            String frmstm = frmQry.getFrom(++uniqID);
            String selstm = selQry.getSelect(frmQry.getSelectedTables(), isOneAttr, false, 0.0, false, null);
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
        String tmpStm = selQry.getSelect(frmRelts, isOneAttr, false, confPar.repAlias, false, null);
        String finalQry = tmpStm + "\n" + stm;
        finalQry += "\n" + whrQry.getSqlWhere(frmRelts , isNest,  confPar, 2);

      return  finalQry;

    }

    public static String nestQuery( long uniqID, ConfParameters confPar)
    {

        //Store the depth of the nesting query
        int nestLev = confPar.nestLev;

        String sqlRep="";

        //This list will be used to store all the attributes from the current FROM clause and from outer
        //queries as well
        LinkedList<String> levFrmBinds = new LinkedList<>();

        //It creates the first outer query
        QRYREPRES curQuery = genQuery( levFrmBinds, ++uniqID, true, false, confPar);

        //It stores all the attributes that are selected in the first outer query
        levFrmBinds = copySelRelts(levFrmBinds,curQuery.selRelts);

        boolean isNest = true;

        //It creates the right format
        if(nestLev >= 2)
        {
            sqlRep += curQuery.qryStr +  "(";
        }

        //This loop will be used to create the appropriate nesting which can be specify as
        //input parameter to the method nestQuery. In each nesting which store the binds attributes
        //and as key we store the nesting level
        for(int curLev=1; curLev < nestLev +1; curLev++)
        {
            if( curLev == nestLev  )
                isNest = false;

            //It creates the right format
            sqlRep += " (";

            //It create the inner queries
            curQuery =  genQuery( levFrmBinds, ++uniqID, isNest, curQuery.isOneAt, confPar);

            //It creates the right format
            sqlRep +="\n\t" + curQuery.qryStr;

            //We retrieve all the attributes that are selected in the FROM clause
            //from the outer sql. In other words, we store the binds attributes
            levFrmBinds = copySelRelts(levFrmBinds,curQuery.selRelts);
        }

        for(int i=0; i< nestLev +1; i++)
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

        //It retrieves all the relations which their associated attributes from MySQL database
        retrieveDBSchema(allRelAttr);

        //It retrieves parameters from the configuration file
        ConfParameters confPar = readConfFile();

        long uniqID=0;

        int option  = Integer.parseInt(args[0]);

        LinkedList<String> frmRelts = new LinkedList<>();

        String qry="";

        int pick;

   /*     while(true)
        {

            pick = genRandChoice(4);

        //The option is given as input parameter to the program
        switch(pick)
        {
            case 0:
                System.out.println("Complex query");
                System.out.println("*******************");
                qry  = genCompQuery(1, frmRelts,1,false,false, confPar);
            break;

            case 1:
                System.out.println("Simple query");
                System.out.println("*******************");
                QRYREPRES res = genQuery(null,uniqID, false, false, confPar);
                qry = res.qryStr;
                break;

            case 2:
                qry = nestQuery(uniqID, confPar);
               // wrtSql2File("rand.", qry);
                break;

            case 3:
                QRYREPRES res1 =  aggrGuery(uniqID, confPar);
                qry = res1.qryStr;
                break;
        }
            System.out.println(qry);
            wrtSql2File("rand.sql",qry);


          //  String sql = nestQuery(uniqID, confPar);
            wrtSql2File("rand.sql",qry);

            genLogFile(qry);

        }*/

        String qury ="SELECT r31.A AS A0, r31.B AS A1, r11.A AS A2, r11.B AS A3, r31.B AS A4, SUM(r11.B), (MIN(r11.A) - MIN(r31.A) ), (SUM(r31.B) / NULL)\n" +
                "FROM r3 AS r31, r1 AS r11\n" +
                "WHERE NOT(NOT(NULL <> 10 )  AND 18 <= 13)  OR NULL = 13 AND ( 17 <= 2)  OR 3 < 8 AND NOT(r11.B < r31.A )\n" +
                "GROUP BY r31.A, r31.B, r11.A, r11.B\n" +
                "HAVING MIN(r11.A) < AVG(r31.A)" ;

        genLogFile(qury);


    }

}

