/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                               */
/*Date: 5/06/2017                                                                  */
/*Filename: SETOPERATORS.java                                                      */
/*                                                                                 */
/***********************************************************************************/

package Engine;


/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.HashMap;
import java.util.LinkedList;

public class SETOPERATORS
{

    private String stm;

    //This hashMap will be used to store all the attributes for each relation
    HashMap<String, LinkedList<String>> relationsAttrs;

    private LinkedList<String> setOper;

    public SETOPERATORS(ConfParameters confParIn)
    {
        this.setOper = new LinkedList<>();
        this.setOper.add("UNION");
        this.setOper.add("UNION ALL");
        this.setOper.add("INTERSECT");
        this.setOper.add("INTERSECT ALL");
        this.setOper.add("EXCEPT");
        this.setOper.add("EXCEPT ALL");
    }

    public String getOper(LinkedList<String> frmRelts)
    {
        stm = setOper.get(Utilities.getRandChoice(setOper.size()));

        return stm;
    }

}
