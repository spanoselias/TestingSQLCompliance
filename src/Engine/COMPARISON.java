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
    public static int genRandNo(int inputSize)
    {
        Random randomGenerator = new Random();

        int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

        return pickRand;
    }

    public String getAttrComparison(HashMap<String, LinkedList<String>> relAttrs, LinkedList<String> selectedTablesIn)
    {
        this.relAttrs = relAttrs;
        this.selectedTables = selectedTablesIn;

        int pickRand;
        int pickRandAttr;

        String oper = "";
        String rel1 = "";
        String rel2 = "";

        try
        {
            pickRand = genRandNo(this.operators.size());
            oper = this.operators.get(pickRand);

            pickRand = genRandNo(this.selectedTables.size());
            //pickRandAttr = genRandNo(relAttrs.get(this.selectedTables.get(pickRand)).size());
            //pickRandAttr = genRandNo(this.selectedTables.get(pickRand)

            rel1 = this.selectedTables.get(pickRand);

            int prevPickRand = pickRand;
            int counter=0;

            //We are trying to do the comparison from two different relations
            do
            {
                pickRand = genRandNo(this.selectedTables.size());

                ++counter;
            } while(rel1 == rel2 && counter < 100);


            rel2 = this.selectedTables.get(pickRand);


        } catch (ArrayIndexOutOfBoundsException e)

        {
            System.out.println("Error in Comparison Class, getAttrComparison: " + e);
        }

        return rel1 + " " + oper + " " + rel2;
    }

    public String getConsAndNullComp(HashMap<String, LinkedList<String>> relAttrs, LinkedList<String> selectedTablesIn)
    {

        this.selectedTables = selectedTablesIn;

        int pick;
        String oper = "";
        String rel1 = "";
        String rel2 = "";

        pick = genRandNo(operators.size());
        oper = operators.get(pick);


        pick = genRandNo(4);

        switch (pick)
        {
            case 0:
                rel1 =  constsAndNull.get(genRandNo(constsAndNull.size()));
                rel2 =  constsAndNull.get(genRandNo(constsAndNull.size()));
            break;

            case 1:
                rel1 =  constsAndNull.get(genRandNo(constsAndNull.size()));
                rel2 = this.selectedTables.get(genRandNo(this.selectedTables.size()));
            break;


            case 2:

                rel1 = this.selectedTables.get(genRandNo(this.selectedTables.size()));
                rel2 = constsAndNull.get(genRandNo(constsAndNull.size()));
            break;
            case 3:

                rel1 = "NULL";
                rel2 = constsAndNull.get(genRandNo(constsAndNull.size()));
                break;
        }

        return rel1 + " " + oper + " " + rel2;

    }


}