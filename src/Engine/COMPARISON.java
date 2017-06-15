package Engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class COMPARISON
{
    final int CONSTNO = 20;

    private LinkedList<String> operators;
    private HashMap<String, LinkedList<String>> relAttrs;
    private LinkedList<String> selectedTables;
    private LinkedList<String> constsAndNull;


    //This list will be used to have arithmetic with constants and NULL
    //in the SELECT CLAUSE
    private LinkedList<String> arithCompr;

    private LinkedList<String> constAndNullAttr;


    public COMPARISON()
    {
        this.operators = new LinkedList<>();
        this.operators.add("<");
        this.operators.add(">");
        this.operators.add("<=");
        this.operators.add("=");
        this.operators.add(">=");
        this.operators.add("<>");

        constsAndNull = new LinkedList<>();
        constsAndNull.add("NULL");

        //This loop in used to insert some constants in the linkedlist in order to
        //have some comparisons like NULL = NULL, attr > 2 and so for.
        for(int i=0; i< CONSTNO; i++)
        {
            constsAndNull.add(Integer.toString(i));
        }

        this.arithCompr = new LinkedList<>();
        this.arithCompr.add("*");
        this.arithCompr.add("-");
        this.arithCompr.add("+");
        this.arithCompr.add("/");
        this.arithCompr.add("%");

        this.constAndNullAttr = new LinkedList<>();
        for(int i =0; i< 8; i++ )
        {
            this.constAndNullAttr.add(String.valueOf(i));
        }
        for(int i =0; i< 5; i++ )
        {
            this.constAndNullAttr.add("NULL");
        }
    }

    /**
     * This method generate numbers between 0..inputSize
     *We can use this number in order to choose randomly relations or attributes
     *Thus, the inputSize can be the total number of relations or attributes
     *
     *@param inputSize is the image that represents the NORTH Direction
     */
    public static int genRandChoice(int inputSize)
    {
        Random randomGenerator = new Random();

        int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

        return pickRand;
    }


    public String getAttrComparison(HashMap<String, LinkedList<String>> relAttrs, LinkedList<String> selectedTablesIn, double probWhr)
    {

        this.selectedTables = selectedTablesIn;

        int pick;
        String rel1 = "";
        String rel2 = "";

        String arithmCompStr = "";


        pick = genRandChoice(operators.size());
        String oper = operators.get(pick);

        //We randomly choose if the comparison will be
        //one attribute with a constant
        int pick2 = genRandChoice(5);

        int pick3 = genRandChoice(1);

        //The probWhr represents the probability of having
        //constants or NULLS to the WHERE comparisons
        pick = genRandChoice( 100 );
        if(pick <=  (int)(probWhr * 100) )
        {
            pick = genRandChoice(2);
        }
        else if(pick2 == 0)
        {
            pick = 2;
        }
       /* else if(pick3 ==0)
        {
            pick =3;
        }*/
        else
        {
            pick = 4;
        }

        //The idea of switch is to do comparisons between two relation's attributes or between
        //one attribute and one constant or between an attribute and a NULL
        switch (pick)
        {
            case 0:
                rel1 =  constsAndNull.get(genRandChoice(constsAndNull.size()));
                rel2 =  constsAndNull.get(genRandChoice(constsAndNull.size()));
            break;

            case 1:

                rel1 = "NULL";
                rel2 = constsAndNull.get(genRandChoice(constsAndNull.size()));
                break;

            case 2:
                rel1 = this.selectedTables.get(genRandChoice(this.selectedTables.size()));
                rel2 = constsAndNull.get(genRandChoice(constsAndNull.size()));
            break;

            case 3:
                    arithmCompStr  = getArithCompr();
            break;


           /* case 2:

                rel1 =  constsAndNull.get(genRandChoice(constsAndNull.size()));
                rel2 = this.selectedTables.get(genRandChoice(this.selectedTables.size()));
            break;*/

            case 4:
                int pickRandRel = genRandChoice(this.selectedTables.size());
                int pickRand;

                rel1 = this.selectedTables.get(pickRandRel);

                int prevPickRand = pickRandRel;
                int counter=0;

                //We are trying to do the comparison from two different relations
                do
                {
                    pickRand = genRandChoice(this.selectedTables.size());

                    ++counter;

                  //The idea is that we try to do the comparison in the WHERE clause with
                  // with two different attributes. Thus, if the attributes are the same we try to
                  // pick a new attribute. But we set up a threshold in order to avoid the case where
                  //we have for many times the same attributes
                } while(prevPickRand == pickRand && counter < 1000);


                rel2 = this.selectedTables.get(pickRand);
            break;
        }

        //We want to use negation within some the expressions. Thus, we randomly decide if
        //this expression will have negation or not.
        String res="";

        pick = genRandChoice(10);
        if( pick ==0 )
        {
            if(arithmCompStr != "")
            {
                res = "NOT" + "(" + arithmCompStr + " ) "  ;
            }
            else
            {
                res = "NOT" + "(" + rel1 +  " " + oper + " " + rel2 + " ) "  ;
            }

        }
        else
        {
            if(arithmCompStr != "")
            {
                res =arithmCompStr;
            }
            else
            {
                res =  rel1 + " " + oper + " " + rel2  ;
            }

        }

        return res;

    }


    public String getArithCompr()
    {
        String arith = arithCompr.get( genRandChoice(arithCompr.size()) );
        String const1 = constAndNullAttr.get( (genRandChoice(constAndNullAttr.size())));
        String const2 = constAndNullAttr.get( (genRandChoice(constAndNullAttr.size())));

        String stm = const1 + arith + const2;

        return stm;
    }
}