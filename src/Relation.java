import java.util.LinkedList;

/**
 * Created by elias on 4/13/17.
 */
public class Relation
{

    String relName;
    LinkedList<String> attributes;


    public Relation()
    {
        relName = "";
        attributes  = new LinkedList<>();
    }


    public String getRelName()
    {
        return relName;
    }


    public void setRelName( String newRelName)
    {
        this.relName = newRelName;
    }

    
    public void addAttribute (String newAttribute)
    {
        this.attributes.add(newAttribute);
    }

}
