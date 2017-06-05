package Engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

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

    public String getSelect( LinkedList<String> frmRels, boolean isOneAttr)
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

            if(isOneAttr == false )
            {
                for (String relName : frmRels)
                {
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
            }

            //If isOneAttr is true, then it means that we need to have only one attribute in the select
            //statement. The reason is because this query might be a nested query and the outer query has
            //an "IN" in the WHERE clause which only need one attribute
            else
            {
                String randAttr = frmRels.get(genRandChoice(frmRels.size()));

                aliasAttr.add(alias.get(j));
                stm += String.format(" %s AS %s", randAttr, alias.get(j));
            }


        return stm;

    }

    public static int genRandChoice(int inputSize)
    {
        Random randomGenerator = new Random();

        int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

        return pickRand;
    }

    public LinkedList<String> getAliasAttr()
    {
        return this.aliasAttr;
    }
}
