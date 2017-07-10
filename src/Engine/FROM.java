package Engine;

import java.util.HashMap;
import java.util.LinkedList;

public  class FROM
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

    private LinkedList<String> alias;

    ConfParameters confParSel;

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
    }

    public String getFrom(long uniqID)
    {
        //Clear LinkedList
        selectedReltsInFrom.clear();

        //We store the max number of relations that we can have in
        //the FROM clause.
        int maxRels = confParSel.maxTableFrom;

        //We handle the case where the max relations in the from which is given
        //is greater than the total relations. Then, we just store the total relations
        if(maxRels > rel.length )
        {
            maxRels = rel.length;
        }

        //This random number indicates how many relation the Engine.FROM Clause will have
        int pickRand = Utilities.getRandChoice(maxRels) + 1;

        //We shuffle the array of the relations to avoid choosing always the same order.
        Utilities.shuffleArray(rel);

        String stm = "";

        String tmp = stm;
        stm = "FROM" + tmp;

        String newAlias="";

        for (int i = 0; i < pickRand; i++)
        {
            //We want to avoid having more attributes than the max attributes
            //which is given an a parameter in the configuration file
            if(confParSel.maxTableFrom == i)
            {
                break;
            }

            newAlias = rel[i].getRelName().toLowerCase() + String.valueOf(uniqID);

           //to be checked
           for(Attribute attr: this.allRelAttrs.get(rel[i].getRelName()))
           {
               Attribute newAttr = new Attribute();
               newAttr.attrName = (newAlias) + "." + attr.attrName;
               newAttr.attrType = attr.attrType;

               this.selectedReltsInFrom.add( newAttr);
           }

            if (i == 0)
                stm += String.format(" %s AS %s", rel[i].getRelName(), newAlias );
            else
                stm += String.format(", %s AS %s", rel[i].getRelName(), newAlias );
        }

        fromStm = stm;

        return stm;
    }

    //We retrieve the attributes that were chosen in the FROM clause
    public LinkedList<Attribute> getSelectedTables()
    {
        return this.selectedReltsInFrom;
    }

    public String getFromSql()
    {
        return fromStm;
    }

    public HashMap<String, LinkedList<Attribute>> getRelAttrs() {
        return this.allRelAttrs;
    }


}