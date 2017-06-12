package Engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class FUNCTIONS
{
        private LinkedList<String> functions;
        private LinkedList<String> oper4;


        private HashMap<String, LinkedList<String>> relAttrs;
        private LinkedList<String> selAttrs;

        public FUNCTIONS()
        {
            this.functions = new LinkedList<>();
            this.functions.add("COUNT");
            this.functions.add("AVG");
            this.functions.add("MIN");
            this.functions.add("MAX");


            this.oper4 = new LinkedList<>();
            this.oper4.add("<");
            this.oper4.add(">");
            this.oper4.add("<=");
            this.oper4.add("=");
            this.oper4.add(">=");
            this.oper4.add("<>");

        }

        public static int genRandChoice(int inputSize)
        {
            Random randomGenerator = new Random();

            int pickRand = (randomGenerator.nextInt(inputSize) % inputSize);

            return pickRand;
        }

        public String getAttrComparison( LinkedList<String> selAttrsIn)
        {
            String stm="";

            this.selAttrs = selAttrsIn;

            String attr = selAttrsIn.get(genRandChoice(selAttrsIn.size()));

            int switchPick = genRandChoice(3);
            String curFun1 =  functions.get(genRandChoice(functions.size()));
            String curFun2 = "";
            String oper = oper4.get(genRandChoice(oper4.size()));

            switch(switchPick)
            {
                //Function with constant comparison
                case 0:
                    String con = String.valueOf(genRandChoice(10000));
                    stm = curFun1 + "(" + attr + ")" + " " + oper + " " + con;
                break;

                //Function with function comparison
                case 1:
                    int counter =0;
                    //We trying to avoid comparing the same functions
                    do
                    {
                        counter ++;
                        curFun2 =functions.get(genRandChoice(functions.size()));
                    }while(curFun2 == curFun1 && counter < 10000);

                    counter =0;
                    String attr2 ;
                    //We trying to avoid comparing the same functions
                    do
                    {
                        counter ++;
                        attr2 = selAttrsIn.get(genRandChoice(selAttrsIn.size()));
                    }while(attr == attr2 && counter < 10000);

                    stm = curFun1 + "(" + attr + ")" + " " + oper + " " + curFun2 + "(" + attr2 + ")";
                break;

                case 2:
                    String constant = String.valueOf(genRandChoice(10000));
                    stm = attr  + " "  + oper + " " + constant;
                break;
            }

            return stm;

        }



    }

