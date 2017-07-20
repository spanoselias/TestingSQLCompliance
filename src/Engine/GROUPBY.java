/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                               */
/*Date: 5/06/2017                                                                  */
/*Filename: GROUPBY.java                                                           */
/*                                                                                 */
/***********************************************************************************/

package Engine;

/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.HashMap;
import java.util.LinkedList;

public  class GROUPBY
{
    private String stm;

    //This list is used to store all the attributes which are selected in the
    //GROUP by clause. The reason is that we cannot have attributes in the SELECT clause
    //that are not appear in the GROUP BY
    private LinkedList<Attribute> groubyAttr;

    //This hashMap will be used to store all the attributes for each relation
    HashMap<String, LinkedList<String>> relationsAttrs;

    ConfParameters confPar;

    public GROUPBY(ConfParameters confParIn)
    {
        groubyAttr = new LinkedList<>();

        this.confPar = confParIn;
        stm = "GROUP BY ";
    }

    public String getGroupBy(LinkedList<Attribute> frmRelts)
    {
        //It is used for string format purpose
        boolean isOut = false;

        int j=0;

        for (Attribute relName : frmRelts)
        {
            if( this.confPar.maxAttrGrpBy == j)
            {
                break;
            }

            //We store all the attributes that are chosen in the
            //GROUP BY clause because we can only project these attributes in
            //the SELECT clause
            groubyAttr.add(relName);

            //This check is just for formatting reasons
            if (isOut == false)
            {
                stm += String.format("%s", relName.attrName);
                isOut = true;
            }

            else
            {
                stm += String.format(", %s", relName.attrName);
            }

            j++;
        }

        return stm;
    }

    //This methods is used for retrieving the attributes which are selected
    //in the GROUPBY clause
    public LinkedList<Attribute> getAttrInGroup()
    {
        return this.groubyAttr;
    }

}