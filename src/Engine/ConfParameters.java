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


/***********************************************************************************/
/*                           ConfParameters class                                  */
/***********************************************************************************/
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

    public double isNULL;

    //Probability of having row comparisons
    public double rowcompar;

    //Probability of having IS NULL and NOT IS NULL in the where clause
    public double isSelectAll;

    public int compTool;

    public LinkedList<String> genAlias = new LinkedList<>();

    public HashMap<String, LinkedList<Attribute>> strAttrs = new HashMap<>();

    public LinkedList<String> dictonary;

    public LinkedList<Attribute> allStringAttrs = new LinkedList<>();

    public double stringInSel;

    public double stringInWhere;

    public String user;
    public String pass;
    public String dbName;
    public String DBMS;

    //We use this boolean to check if the current query is nesting, in this way we avoid
    //the problem to have IN operator and then * in a subquery
    public boolean nesting;

    public LinkedList<String> trackDomainAttr = new LinkedList<>();

    //This Hashmap is used to track the type of all attributes for all relations
    public HashMap<String, String> typeOfAllAttr = new HashMap<>();

    //This Hashmap is used to track the type of selected attributes
    public HashMap<String, String> selectedAttrType = new HashMap<>();

    public LinkedList<String> selectedTypeAttributesForSet = new LinkedList<>();

    public int intCounterAttr = 0;
    public int stringCounterAttr = 0;

}
