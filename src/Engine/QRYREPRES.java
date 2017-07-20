/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                               */
/*Date: 5/06/2017                                                                  */
/*Filename: QRYREPRES.java                                                         */
/*                                                                                 */
/***********************************************************************************/

package Engine;


/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.LinkedList;

//Simple class to store some information while generating SQL queries
public class QRYREPRES
{
    public String qryStr;
    public boolean isOneAt;
    public LinkedList<Attribute> selRelts;
    public int totalAttr;
}
