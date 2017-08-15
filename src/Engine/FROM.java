/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                 			   */
/*Date: 5/06/2017                                                                  */
/*Filename: FROM.java                                                              */
/*                                                                                 */
/***********************************************************************************/

package Engine;

/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.HashMap;
import java.util.LinkedList;

/***********************************************************************************/
/*                                    FROM CLASS                                   */
/***********************************************************************************/
public class FROM
{
    private Relation rel [];

    //This variable will be used to create the Engine.FROM Clause String
    private String fromStm;

    //This HashMap is used to store all the relations with their associated attributes. The
    //key represents the relation name and the list stores all the attributes for each key (relation)
    private HashMap<String, LinkedList<Attribute>> allRelAttrs;

    //This LinkedList will be used to store all the relations that will be randomly chosen
    //for the Engine.FROM Clause
    private LinkedList<Attribute> selectedReltsInFrom;

    //This list will be used to store all the attributes that of type String. This list will
    //be useful when we will generate SQL with like operation and other string manipulations
    private LinkedList<Attribute> selStringAttr;

    private LinkedList<String> alias;

    ConfParameters confParSel;

/***********************************************************************************/
/*                               CLASS CONSTRUCTOR                                 */
/***********************************************************************************/
public FROM(  LinkedList<String> aliasIn, HashMap<String, LinkedList<Attribute>> allRelAttrsIn, ConfParameters confPar )
{
    this.alias = aliasIn;

    this.allRelAttrs = allRelAttrsIn;

    this.confParSel = confPar;

    fromStm = "";

    //We copy all the relations from the LinkedList to an array in order to
    //shuffle the array and afterward to choose some random relations to create
    //FROM Clause
    rel = new Relation[this.allRelAttrs.size()];
    int i=0;
    for (String relation : this.allRelAttrs.keySet())
    {
        rel[i] = new Relation();

        rel[i].setRelName(relation);

        for (Attribute attr : this.allRelAttrs.get(relation))
        {
            rel[i].setAttrName(attr);
        }

        i++;
    }

    //This list will be used to track which relations have been selected from the generator. Thus, it will be useful
    //to know the relations in the Engine.FROM statement in order to know what attributes to include in the Engine.SELECT_rmv statement
    selectedReltsInFrom = new LinkedList<>();

    selStringAttr = new LinkedList<>();

}

/***********************************************************************************/
/*                              GENERATING FROM CLAUSE                             */
/***********************************************************************************/
public String getFrom(long uniqID)
{
    //Initialize LinkedList
    selectedReltsInFrom.clear();

    //We store the max number of tables that we can have in
    //the FROM clause.
    int maxRels = confParSel.maxTableFrom;

    //We handle the case where the max number of tables in the FROM clause which is given
    //is greater than the total relations. Then, we just store the total relations
    if(maxRels > rel.length )
    {
        maxRels = rel.length;
    }

    //This random number indicates how many relation the Engine.FROM clause will have
    int pickRand = Utilities.getRandChoice(maxRels) + 1;

    //We shuffle the array of the tables to avoid choosing always the same order.
    Utilities.shuffleArray(rel);

    String stm = "";

    String tmp = stm;
    stm = "FROM" + tmp;

    String newAlias="";

    for (int i = 0; i < pickRand; i++)
    {
        //We want to avoid having more tables than the max tables
        //which is given an a parameter in the configuration file
        if(confParSel.maxTableFrom == i)
        {
            break;
        }

       //We generate an alias for each table
       newAlias = rel[i].getRelName().toLowerCase() + String.valueOf(uniqID);

       //to be checked
       for(Attribute attr: this.allRelAttrs.get(rel[i].getRelName()))
       {
           //We access the attribute of each table using the alias which is given before.
           //Also, we store the type of the attribute
           Attribute newAttr = new Attribute();
           newAttr.attrName = (newAlias) + "." + attr.attrName;
           newAttr.attrType = attr.attrType;

           //to be check
           confParSel.selectedAttrType.put(newAttr.attrName, newAttr.attrType);

           this.selectedReltsInFrom.add( newAttr);
       }

        //Initialize the LinkedList
        this.selStringAttr.clear();

       // We check if the strAttrs hashMap has attributes type of strings. This attributes are retrieved from the
       // databases and they will be used to generate SQL queries with Strings, String functions and operation
       // 'LIKE', and other String comparisons.
       if(confParSel.strAttrs.get(rel[i].getRelName()) != null)
        {
            //Based on the tables that are chosen in the FROM Clause, we store the attributes with
            //the appropriate alias to know how to access them in the SELECT and WHERE Clause
            for (Attribute attr : confParSel.strAttrs.get(rel[i].getRelName()))
            {
                Attribute newAttr = new Attribute();
                newAttr.attrName = (newAlias) + "." + attr.attrName;
                newAttr.attrType = attr.attrType;

                this.selStringAttr.add(newAttr);

                //to be check
                confParSel.selectedAttrType.put(newAttr.attrName, newAttr.attrType);
            }

            confParSel.allStringAttrs = this.selStringAttr;
        }

        if (i == 0)
            stm += String.format(" %s AS %s", rel[i].getRelName(), newAlias );
        else
            stm += String.format(", %s AS %s", rel[i].getRelName(), newAlias );
    }

    fromStm = stm;

    return stm;
}
/***********************************************************************************/
/*                              RETRIEVING  FROM CLAUSE ATTRIBUTES                 */
/***********************************************************************************/
//We retrieve the attributes that were chosen in the FROM clause
public LinkedList<Attribute> getSelectedTables()
{
    return this.selectedReltsInFrom;
}

/***********************************************************************************/
/*                              RETRIEVING STRING TYPE ATTRIBUTES                 */
/***********************************************************************************/
public LinkedList<Attribute> getStringAttrs()
    {
        return this.selStringAttr;
    }

/***********************************************************************************/
/*                              RETRIEVES ALL ATTRIBUTES                           */
/***********************************************************************************/
public HashMap<String, LinkedList<Attribute>> getRelAttrs() {
        return this.allRelAttrs;
    }

}