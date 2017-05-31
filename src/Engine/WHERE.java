package Engine;

import Engine.COMPARISON;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public  class WHERE
{
    private String stm;
    private COMPARISON genCom;
    private int whereNo=-1;
    private String conn[] = {" AND", " OR"};

    //This hashMap will be used to store all the attributes for each relation
    HashMap<String, LinkedList<String>> relationsAttrs;

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

        genCom = new COMPARISON();
    }

    public String getSqlWhere(LinkedList<String> selectedReltsInFrom, int whereNoComp)
    {
        this.whereNo = whereNoComp;

        stm ="WHERE ";

        if(whereNo != -1)
        {
            for(int i=0; i< (whereNo +1) ; i++)
            {
                if(i==0)
                {
                    stm += genCom.getConsAndNullComp(this.relationsAttrs, selectedReltsInFrom);
                }
                else
                {
                    stm += conn[genRandNo(conn.length)] + " " + genCom.getAttrComparison(this.relationsAttrs, selectedReltsInFrom);
                }
            }
        }

        else
        {
            stm += genCom.getConsAndNullComp(this.relationsAttrs, selectedReltsInFrom);
        }

        return stm;

    }

    private  int genRandNo(int inputSize)
    {

        Random randomGenerator = new Random();

        int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

        return pickRand;
    }

}