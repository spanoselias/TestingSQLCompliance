package Engine;

import java.util.HashMap;
import java.util.LinkedList;

public class SELECT
{

    //This linkedlist will be used to store all the alias that
    //we will give in the SELECT clause. Thus, they will be useful in case
    //where we have a subquery and we need to access it using its alias
    private LinkedList<String> aliasAttr;

    //This linkedlist holds different alias that can be used in Engine.SELECT_rmv clause
    private LinkedList<String> alias;

    private HashMap<String, LinkedList<String>> relAttr;

    //This hashMap will be used to store all the selected alias that exist
    //in this sql query. This is useful in case where we need to have subquery
    private HashMap<String, String> selectedAlias;

    private HashMap<String, LinkedList<String>> relAttrs;

    //This HashMap is used to store all the relations with their associated attributes. The
    //key represents the relation name and the list stores all the attributes for each key (relation)
    private HashMap<String, LinkedList<String>> allRelAttrs;

    private boolean isDistinct;
    private boolean isAllAttrs;

    public SELECT(boolean isDistinctIn, boolean isAttrsIn,
                      LinkedList<String> aliasIn, int subqueryIn, HashMap<String, LinkedList<String>> allRelAttrsIn )
    {
        this.isDistinct = isDistinctIn;
        this.isAllAttrs = isAttrsIn;
        this.alias = aliasIn;
        this.allRelAttrs = allRelAttrsIn;

        //This linkedlist will be used to store all the alias
        this.aliasAttr = new LinkedList<>();

        this.relAttr = new HashMap<>();
        this.selectedAlias = new HashMap<>();

        //It retrieves a hashmap that contains all the relation with its associated attributes
        relAttrs = allRelAttrsIn;
    }

    public String getSelect(boolean isSubquery, String relAliasName, LinkedList<String> frmRels)
    {
        aliasAttr.clear();

        String stm = "SELECT";

        //This condition is used to check if the sql query will include DISTINCT keyword
        //in the Engine.SELECT_rmv statement
        if (this.isDistinct)
        {
            stm += " DISTINCT";
        }

        //This condition check if all the attributes should be included in the output
        if (isAllAttrs)
        {
            stm += " *";
        }

            boolean isOut = false;
            int j=0;
            for (String relName : frmRels)
            {
                //This loop will be used to go through all the attributes of the current
                //relation
           //     for (int j = 0; j < relAttrs.get(relName).size(); j++)
              //  {

                    //This is useful if this query will be used as a subquery in the FROM clause
                    aliasAttr.add(alias.get(j));

                    if (isOut == false)
                    {
                        stm += String.format(" %s AS %s", relName, alias.get(j));
                        isOut = true;
                    }
                    else
                    {
                        stm += String.format(", %s AS %s", relName, alias.get(j));
                    }

                    //We need to store all the alias that we chose in the Engine.SELECT_rmv clause because they will
                    //be useful when we will implement subqueries. We store the as key (relation.attributeName) and as
                    //value the alias that we gave
                   // String key = String.format("%s", relName.toLowerCase(), relAttrs.get(relName).get(j));
                    //selectedAlias.put(key, alias.get(j));
             //   }
            j++;
            }

        return stm;

    }


    public LinkedList<String> getAliasAttr()
    {
        return this.aliasAttr;
    }
}
