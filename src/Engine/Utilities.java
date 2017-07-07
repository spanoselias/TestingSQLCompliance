package Engine;

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

}