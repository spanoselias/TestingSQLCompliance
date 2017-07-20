/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                               */
/*Date: 5/06/2017                                                                  */
/*Filename: ConfParameters.java                                                    */
/*                                                                                 */
/***********************************************************************************/


package Engine;

/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.HashMap;
import java.util.LinkedList;

public class ConfParameters
{
    //Indicates the max number of tables that can exist in a From clause
    public  int maxTableFrom;

    //Indicates the max number of attributes that can have in the SELECT clause
    public int maxAttrSel;

    //Indicates the max number of conditions that can have in the WHERE clause
    public int maxCondWhere;

    public double probWhrConst;

    public double repAlias;

    public HashMap<String, LinkedList<Attribute>> relationsAttrs = new HashMap<>();

    //Represents the nesting depth of an SQL query
    public int nestLev;

    public int maxAttrGrpBy;

    public double arithmCompar;

    public double isDistinct;

    public LinkedList<String> genAlias = new LinkedList<>();

    public HashMap<String, LinkedList<Attribute>> strAttrs = new HashMap<>();

    public LinkedList<String> dictonary;

    public double stringInSel;

    public double stringInWhere;

    public String user;
    public String pass;
    public String dbName;
    public String DBMS;
}
