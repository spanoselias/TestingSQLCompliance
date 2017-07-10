package Engine;

import java.util.LinkedList;
import java.util.Random;

//final, because it's not supposed to be subclassed
public final class Utilities
{

    /**
     * This method generate numbers between 0..inputSize
     *We can use this number in order to choose randomly relations or attributes
     *Thus, the inputSize can be the total number of relations or attributes
     *
     *@param inputSize is the image that represents the NORTH Direction
     */
    public static  int getRandChoice(int inputSize)
    {
        Random randomGenerator = new Random();

        int pickRand = (randomGenerator.nextInt(inputSize) % inputSize) ;

        return pickRand;
    }

    public static void shuffleArray(Relation[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    public static String chooseRandAttr(LinkedList<Attribute> attributesIn)
    {

        //This method is used for row comparisons. (r1.b,r2.a) > (r2.b, r1.a)
        int pick  = getRandChoice(100);
        if(pick <=  (int)(0.2 * 100) )
        {
            pick = getRandChoice(2);
        }
        else
        {
            pick = 2;
        }

        switch (pick)
        {
            case 0:
                return "null";

            case 1:
                return Integer.toString(getRandChoice(1000));

            case 2:
                return attributesIn.get(getRandChoice(attributesIn.size())).attrName;

        }

        return attributesIn.get(getRandChoice(attributesIn.size())).attrName;
    }

    public static String chooseRandAttrGrpBy(LinkedList<Attribute> grpAttrIn, LinkedList<Attribute> allAttr)
    {
        LinkedList<Attribute> newAttrs = new LinkedList<>();

        for(Attribute atr: allAttr)
        {
            boolean isFound = false;

            for(Attribute grp: grpAttrIn)
            {
                if(atr.attrName.compareTo(grp.attrName) == 0)
                {
                    isFound = true;

                }
            }

            if(!isFound)
            {
                newAttrs.add(atr);
            }
        }

        if(newAttrs.size() == 0)
        {
            newAttrs = grpAttrIn;
        }

        //This method is used for row comparisons. (r1.b,r2.a) > (r2.b, r1.a)
        int pick  = getRandChoice(100);
        if(pick <=  (int)(0.1 * 100) )
        {
            pick = 0;
        }
        else
        {
            pick = 1;
        }

        switch(pick)
        {
            case 0:
                return grpAttrIn.get(getRandChoice(grpAttrIn.size())).attrName;


            case 1:
                return newAttrs.get(getRandChoice(newAttrs.size())).attrName;
        }

        return newAttrs.get(getRandChoice(newAttrs.size())).attrName;
    }

    private static void swap(Relation[] a, int i, int change) {
        Relation helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }


}