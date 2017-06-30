import Engine.SQLQURERY;

/**
 * Created by Elias on 6/13/2017.
 */
public class Debugging
{

    public static void main (String args[])
    {

        DbConnections conn = new DbConnections();
        SQLQURERY newSQL = new SQLQURERY();


     /*  String qury ="SELECT r31.A AS A0, r31.B AS A1, r11.A AS A2, r11.B AS A3, r31.B AS A4, SUM(r11.B), (MIN(r11.A) - MIN(r31.A) ), (SUM(r31.B) / NULL)\n" +
                "FROM r3 AS r31, r1 AS r11\n" +
                "WHERE NOT(NOT(NULL <> 10 )  AND 18 <= 13)  OR NULL = 13 AND ( 17 <= 2)  OR 3 < 8 AND NOT(r11.B < r31.A )\n" +
                "GROUP BY r31.A, r31.B, r11.A, r11.B\n" +
                "HAVING MIN(r11.A) < AVG(r31.A)" ;*/

        //conn.connectToMicrosoftSql(qury);

        conn.connectToIBMDb2();





    }




}
