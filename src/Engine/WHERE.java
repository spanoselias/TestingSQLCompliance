package Engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public  class WHERE
{
    private String stm;
    private COMPARISON genCom;
    private int whereNo=-1;

    //This is useful when we generate
    //nested queries
    private NESTCOMPARISON nestGenCom;

    // Represents the connectivities in the WHERE clause. The "NOT"  will
    //be added as well
    private String conn[] = {" AND", " OR"};

    //This hashMap will be used to store all the attributes for each relation
    HashMap<String, LinkedList<String>> relationsAttrs;

    ConfParameters confPar;

    /**
     *The Engine.WHERE constructor takes two parameters. The first one (allReltsWithAttrs) has all the relations
     *that our database schema has with the associated attributes for each relation. The second parameter
     *(selectedReltsInFrom)  is a linkedlist and it stores all the relations which are randomly choose for
     * the Engine.FROM CLAUSE.
     *
     *@param allReltsWithAttrs ,it stores all the relations with their accosiated attributes
     */
    public WHERE(HashMap<String, LinkedList<String>> allReltsWithAttrs)
    {
        this.relationsAttrs =  allReltsWithAttrs;
        stm = "WHERE ";

        nestGenCom = new NESTCOMPARISON();
        genCom = new COMPARISON();
    }

    public String getSqlWhere(LinkedList<String> selectedReltsInFrom,boolean isNested , ConfParameters confPar, int whereCompNo)
    {
        this.confPar = confPar;

        //The variable isParenOpen indicates if we have an open parenthesis
        // Means that we need to close it
        int isParenOpen = 0;

        stm = "WHERE ";

        //In case where the comparisons in the WHERE clause is greater
        //than the given in the configuration, then we set the number of conf
        if(whereCompNo > confPar.maxCondWhere)
        {
            whereCompNo = confPar.maxCondWhere;
        }

        for (int i = 0; i < (whereCompNo + 1); i++)
        {
            //We flip a coin to decide if we will open/close
            //a parenthesis
            int pick = getRandChoice(2);

            if (i == 0)
            {
                 int pickNeg = getRandChoice(2);

                 //we flip a coin to decide if there will be a negation or not
                 if(pickNeg == 1) {stm += "NOT" ;}

                  stm += "("; isParenOpen =1;

                stm +=  genCom.getAttrComparison(this.relationsAttrs, selectedReltsInFrom, confPar.probWhrConst);
            }
            else
            {
                //If there is already an open parenthesis then we need to close
                //the previous one
                if( pick ==1 && isParenOpen == 1)
                {
                    stm += ") " + conn[getRandChoice(conn.length)] + " " + genCom.getAttrComparison(this.relationsAttrs, selectedReltsInFrom, confPar.probWhrConst);
                    isParenOpen = 0;
                }

                //If there is not open parenthesis then we can open a new one
                else if ( pick ==1 && isParenOpen == 0 )
                {
                    //We randomly choose if we will have negation outside
                    //of the parenthesis OR NOT
                    String par ="";
                    int pickNeg = getRandChoice(10);
                    if(pickNeg == 1)
                    {
                        par += " NOT" ;
                    }

                    par += " ( ";

                    stm += conn[getRandChoice(conn.length)] + par + genCom.getAttrComparison(this.relationsAttrs, selectedReltsInFrom, confPar.probWhrConst);
                    isParenOpen = 1;
                }
                else
                {
                    stm += conn[getRandChoice(conn.length)] + " " + genCom.getAttrComparison(this.relationsAttrs, selectedReltsInFrom, confPar.probWhrConst);

                }
            }

            //If it's the last condition and there is an open parenthesis, then
            //we need to close it
            if(isParenOpen == 1 && (i + 1) == (whereCompNo + 1 ))
            {
                stm += " )";
            }
        }

        //In case where we have nested queries and this is the outer query
        if(isNested == true)
        {
            stm += " AND " + nestGenCom.getAttrComparison(this.relationsAttrs, selectedReltsInFrom);
        }

        return stm;

    }


    public boolean getOneAttr()
    {
        return nestGenCom.getIsOneAttr();
    }

    private  int getRandChoice(int inputSize)
    {

        Random randomGenerator = new Random();

        int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

        return pickRand;
    }

}