/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                               */
/*Date: 5/06/2017                                                                  */
/*Filename: OPERATORS.java                                                         */
/*                                                                                 */
/***********************************************************************************/

package Engine;


/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.HashMap;
import java.util.LinkedList;


public class OPERATORS
{

    private String stm;

    //This hashMap will be used to store all the attributes for each relation
    HashMap<String, LinkedList<String>> relationsAttrs;

    private LinkedList<String> oper1;

    public OPERATORS(ConfParameters confParIn)
    {
        this.oper1 = new LinkedList<>();
        this.oper1.add("UNION");
        this.oper1.add("UNION ALL");
        this.oper1.add("INTERSECT");
        this.oper1.add("INTERSECT ALL");
        this.oper1.add("EXCEPT");
        this.oper1.add("EXCEPT ALL");
    }

    public String getOper(LinkedList<String> frmRelts)
    {
        stm = oper1.get(Utilities.getRandChoice(oper1.size()));

        return stm;
    }

}
