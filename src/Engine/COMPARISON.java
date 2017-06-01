package Engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class COMPARISON
{
    private LinkedList<String> operators;
    private HashMap<String, LinkedList<String>> relAttrs;
    private LinkedList<String> selectedTables;
    private LinkedList<String> constsAndNull;

    public COMPARISON()
    {
       // this.selectedTables = new LinkedList<String>();

        this.operators = new LinkedList<>();
        this.operators.add("<");
        this.operators.add(">");
        this.operators.add("<=");
        this.operators.add(">=");
        this.operators.add("<>");

        constsAndNull = new LinkedList<>();
        constsAndNull.add("NULL");
        for(int i=0; i< 20; i++)
        {
            constsAndNull.add(Integer.toString(i));
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


    public String getAttrComparison(HashMap<String, LinkedList<String>> relAttrs, LinkedList<String> selectedTablesIn)
    {

        this.selectedTables = selectedTablesIn;

        int pick;
        String oper = "";
        String rel1 = "";
        String rel2 = "";

        pick = genRandChoice(operators.size());
        oper = operators.get(pick);


        pick = genRandChoice(5);

        switch (pick)
        {
            case 0:
                rel1 =  constsAndNull.get(genRandChoice(constsAndNull.size()));
                rel2 =  constsAndNull.get(genRandChoice(constsAndNull.size()));
            break;

            case 1:
                rel1 =  constsAndNull.get(genRandChoice(constsAndNull.size()));
                rel2 = this.selectedTables.get(genRandChoice(this.selectedTables.size()));
            break;

            case 2:

                rel1 = this.selectedTables.get(genRandChoice(this.selectedTables.size()));
                rel2 = constsAndNull.get(genRandChoice(constsAndNull.size()));
            break;
            case 3:

                rel1 = "NULL";
                rel2 = constsAndNull.get(genRandChoice(constsAndNull.size()));
                break;

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

        return rel1 + " " + oper + " " + rel2;

    }


}