package Engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Elias on 6/18/2017.
 */
public class OPERATORS
{

    private String stm;

    //This hashMap will be used to store all the attributes for each relation
    HashMap<String, LinkedList<String>> relationsAttrs;


    private LinkedList<String> oper1;

    public OPERATORS(ConfParameters confParIn)
    {

        this.oper1 = new LinkedList<>();
        this.oper1.add("UNION");
        this.oper1.add("UNION ALL");
        this.oper1.add("INTERSECT");
        this.oper1.add("INTERSECT ALL");
    }

    public String getOper(LinkedList<String> frmRelts)
    {

        return stm;
    }

    private  int getRandChoice(int inputSize)
    {
        Random randomGenerator = new Random();

        int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

        return pickRand;
    }

}
