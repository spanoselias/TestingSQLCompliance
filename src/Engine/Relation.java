/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                               */
/*Date: 5/06/2017                                                                  */
/*Filename: Relation.java                                                          */
/*                                                                                 */
/***********************************************************************************/
package Engine;


/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.LinkedList;

/***********************************************************************************/
/*                                     RELATION CLASS                              */
/***********************************************************************************/
public class Relation
{

    String relName;
    LinkedList<Attribute> attributes;

    public Relation()
    {
        relName = "";
        attributes  = new LinkedList<>();
    }

    public String getRelName()
    {
        return this.relName;
    }

    public  LinkedList<Attribute> getRelAttrs()
    {
        return this.attributes;
    }

    public void setRelName( String newRelName)
    {
        this.relName = newRelName;
    }

    public void setAttrName( Attribute newattrName)
    {
       attributes.add(newattrName);
    }

    public void setAttrList( LinkedList newAttrList )
    {
        this.attributes = newAttrList;
    }

    public void addAttribute (Attribute newAttribute)
    {
        this.attributes.add(newAttribute);
    }
}
