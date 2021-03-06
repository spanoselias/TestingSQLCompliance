/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                 			   */
/*Date: 5/06/2017                                                                  */
/*Filename: SELECT.java                                                            */
/*                                                                                 */
/***********************************************************************************/

package Engine;

/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.HashMap;
import java.util.LinkedList;

/***********************************************************************************/
/*                                     SELECT CLASS                                */
/***********************************************************************************/
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

    private HashMap<String, LinkedList<Attribute>> relAttrs;

    //This HashMap is used to store all the relations with their associated attributes. The
    //key represents the relation name and the list stores all the attributes for each key (relation)
    private HashMap<String, LinkedList<Attribute>> allRelAttrs;


    //This will be used to generate arithmetic comparison in the where clause
    COMPARISON newCom;

    ConfParameters confParSel;

    //This variable will be used to count the total number of attributes
    //in the SELECT CLAUSE.
    private int countAttr;

    private FUNCTIONS genFunctions;
    private STRINGS genStrings;

    private boolean isDistinct;
    private boolean isAllAttrs;

    public SELECT(boolean isDistinctIn, boolean isAttrsIn,
                      LinkedList<String> aliasIn, int subqueryIn, HashMap<String, LinkedList<Attribute>> allRelAttrsIn, ConfParameters confpar )
    {
        this.isDistinct = isDistinctIn;
        this.isAllAttrs = isAttrsIn;
        this.alias = aliasIn;
        this.allRelAttrs = allRelAttrsIn;

        //This list will be used to store all the alias
        this.aliasAttr = new LinkedList<>();

        this.relAttr = new HashMap<>();
        this.selectedAlias = new HashMap<>();

        //It retrieves a hashmap that contains all the relation with its associated attributes
        relAttrs = allRelAttrsIn;

        this.confParSel = confpar;

        newCom = new COMPARISON();

        genFunctions = new FUNCTIONS();

        genStrings = new STRINGS(this.confParSel);
    }

/***********************************************************************************/
/*                                 GENERATES SELECT CLAUSE                         */
/***********************************************************************************/
public String getSelect( LinkedList<Attribute> frmRels, boolean isOneAttr, boolean isSubqry, double isRepAlias, boolean isAggr, LinkedList<Attribute> aggrAttrsIn, boolean isOperator, int noOfAttrs)
{
    //The isSubqry parameter is important in order to know if this is a subquery parameter to avoid having
    //repetition of alias in the SELECT clause because is not valid

    LinkedList<Attribute> allSelAttrs=null;

    //Operator represents operations like UNION, INTERSECTION.. Thus, we need to track
    //the number of attributes
    if(isOperator == false)
    {
        this.countAttr = 0;
    }

    if(isAggr==true)
    {
        allSelAttrs = frmRels;
        frmRels = aggrAttrsIn;
    }

    aliasAttr.clear();

    String stm = "SELECT";

    //This condition is used to check if the sql query will include DISTINCT keyword
    //in the Engine.SELECT_rmv statement
    if (this.isDistinct && isOperator == false )
    {
        stm += " DISTINCT";
    }

    //This condition check if all the attributes should be included in the output. The probability
    //is given in the Utilities class. We check also if this query is aggregation, if yet, then we
    //do not need the "*"
    if (Utilities.randChoice(confParSel) == true && isOperator == false  && isAggr == false && confParSel.nesting ==false )
    {
        stm += " *";
    }
    else
    {
        //It is used for formatting purposes
        boolean isOut = false;

        //It counts the attributes
        int j=0;

        //This LinkedList will be used store the current alias in order to generate
        //repetition of the alias
        LinkedList<String> curAlias = new LinkedList<>();

        if( isOneAttr == false )
        {
            //frmRels list stores all the attributes that we can project in the SELECT clause. Thus, we take
            //into account the tables what we have chosen in the FROM clause and what attributes each table has
            for (Attribute relName : frmRels)
            {
                if( isOperator == false )
                {
                    //We want to avoid having more attributes than the max attributes
                    //which is specified in the configuration file
                    if(confParSel.maxAttrSel == j)
                    {
                        break;
                    }
                }

                //Operators like UNION, INTERSECTION need to have the same number of attributes. Thus, this
                //condition achieve that
                else if (isOperator == true)
                {
                    if( countAttr == j )
                    {
                        break;
                    }
                }

                //It counts the attributes
                j +=1;

                //It counts the integer attributes
                confParSel.intCounterAttr +=1;

                if(isOperator == false)
                {
                    countAttr +=1;
                }
                    //This is useful if this query will be used as a subquery in the FROM clause
                    aliasAttr.add(alias.get(j));

                    String repAlias = String.format("%s AS %s", relName.attrName, alias.get(j));
                    curAlias.add(repAlias);

                    if (isOut == false)
                    {
                        stm += String.format(" %s AS %s", relName.attrName, alias.get(j));
                        isOut = true;
                    }
                    else
                    {
                        stm += String.format(", %s AS %s", relName.attrName, alias.get(j));
                    }

                    //It stores the type for each attribute. This is quite important in case where we have
                    //set operators
                    confParSel.selectedTypeAttributesForSet.add(confParSel.selectedAttrType.get(relName));

            }//For statement

         //This code will run if we want repetition of alias
         if(isRepAlias > 0 && isOperator == false)
            {
                int pick;
                for(int i=0; i < curAlias.size(); i++)
                {
                    //We randomly choose if we will have repetition of
                    //attributes in the SELECT CLAUSE based on the probability
                    //which give in the configuration file
                    pick = Utilities.getRandChoice( 100 );
                    if(pick <  (int)(confParSel.repAlias * 100) )
                    {
                        j +=1;

                        stm += ", " + curAlias.get(i);
                    }
                }
            }

           //The isOperator variable is used to indicate if we have set operators such as
           // UNION, INTERSECTION and EXCEPT
           if( isOperator == true && j < countAttr )
            {
                for(int g=0; g < countAttr; g++)
                {
                    for(int i=0; i < curAlias.size(); i++)
                    {
                        if(j < countAttr)
                        {
                            j +=1;
                            stm += ", " + curAlias.get(i);
                        }
                        else
                        {
                            break;
                        }
                    }
                }
            }

            if(isOperator == false)
            {
                //We randomly choose if we will have arithmetic comparison in the SELECT clause. The probability
                //is calculated based on the probability which is given in the configuration file
                int pick = Utilities.getRandChoice( 100 );
                if(pick <  (int)(confParSel.arithmCompar * 100) )
                {
                    for(int i=0; i< Utilities.getRandChoice(5); i++)
                    {
                        stm += ", " + newCom.getArithCompr(frmRels) + " AS ART" + i ;
                    }
                }
            }

             //The isAggr variable indicates if we have aggregation in this query. If yes, then we can
             // have aggregation functions in the SELECT STATEMENT
             if(isAggr == true)
             {
                 for(int i=0; i< Utilities.getRandChoice(5); i++)
                 {
                     stm += ", " + genFunctions.getSelectAggr(allSelAttrs, aggrAttrsIn) + " AS AGGR" + i ;
                 }
             }
            if( confParSel.strAttrs.size() > 0 && isAggr == false)
            {
                if(isOperator ==true)
                {
                    for(int i=0; i < confParSel.stringCounterAttr; i++ )
                    {
                        stm += " , " + genStrings.genStringSelect(confParSel.dictonary);
                    }
                }

                else if (isOperator == false)
                {
                    //We randomly choose if we will have string comparisons in the
                    //SELECT Clause
                    int pick = Utilities.getRandChoice( 100 );
                    if(pick <  (int)(confParSel.stringInSel * 100) )
                    {
                        stm += " , " + genStrings.genStringSelect(confParSel.dictonary);

                        confParSel.stringCounterAttr +=1;
                    }
                }

            }
        }

        //If isOneAttr is true, then it means that we need to have only one attribute in the select
        //statement. The reason is because this query might be a nested query and the outer query has
        //an "IN" in the WHERE clause which only need one attribute
        else
        {
            //If the number of attributes that we need to project is 0, it means that the outer query
            //is with EXIST OR NOT EXIST
          /*  if(noOfAttrs == 0)
            {
                noOfAttrs = Utilities.getRandChoice(frmRels.size());
            }*/

            isOut=false;

            for(int i=0; i < noOfAttrs; i++)
            {
               // Attribute randAttr = frmRels.get(Utilities.getRandChoice(frmRels.size()));

                aliasAttr.add(alias.get(i));

                if (isOut == false)
                {
                    stm += String.format(" %s AS %s", frmRels.get(i).attrName, alias.get(i));
                    isOut = true;
                }
                else
                {
                    stm += String.format(", %s AS %s", frmRels.get(i).attrName, alias.get(i));
                }
            }
        }
    }

    return stm;
}

/***********************************************************************************/
/*                                 RETRIEVES ALL ALIAS                             */
/***********************************************************************************/
public LinkedList<String> getAliasAttr()
    {
        return this.aliasAttr;
    }
}
