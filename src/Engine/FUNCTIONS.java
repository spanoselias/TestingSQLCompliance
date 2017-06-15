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

        private LinkedList<String> arithCompr;

        public FUNCTIONS()
        {
            this.functions = new LinkedList<>();
            this.functions.add("COUNT");
            this.functions.add("AVG");
            this.functions.add("MIN");
            this.functions.add("MAX");
            this.functions.add("SUM");


            this.oper4 = new LinkedList<>();
            this.oper4.add("<");
            this.oper4.add(">");
            this.oper4.add("<=");
            this.oper4.add("=");
            this.oper4.add(">=");
            this.oper4.add("<>");


            this.arithCompr = new LinkedList<>();
            this.arithCompr.add("*");
            this.arithCompr.add("-");
            this.arithCompr.add("+");
            this.arithCompr.add("/");
            this.arithCompr.add("%");

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


        public String getSelectAggr( LinkedList<String> grpAttrIn)
        {

            String arith = arithCompr.get( genRandChoice(arithCompr.size()) );

            String stm="";

            this.selAttrs = grpAttrIn;

            String attr1 ="";
            String attr2 ="";

            String rel = "";
            String curFun1 = "";
            String curFun2 = "";


            int pick = genRandChoice(4);
            switch (pick)
            {
                case 0:
                    attr1 = grpAttrIn.get(genRandChoice(grpAttrIn.size()));
                    curFun1   =  functions.get(genRandChoice(functions.size()));
                    stm = curFun1 + "(" + attr1 + ")" ;

                break;

                case 1:
                    attr1 = grpAttrIn.get(genRandChoice(grpAttrIn.size()));
                    curFun1   =  functions.get(genRandChoice(functions.size()));
                    rel = grpAttrIn.get(genRandChoice(grpAttrIn.size()));
                    stm = "(" + curFun1 + "(" + attr1 + ") " + arith + " " + rel + ")";

                break;

                case 2:
                    attr1 = grpAttrIn.get(genRandChoice(grpAttrIn.size()));
                    attr2 = grpAttrIn.get(genRandChoice(grpAttrIn.size()));
                    curFun1   =  functions.get(genRandChoice(functions.size()));
                    curFun2   =  functions.get(genRandChoice(functions.size()));
                    stm = "(" +  curFun1 + "(" + attr1 + ") " + arith + " " + curFun2 + "(" + attr2 + ") " + ")";
                break;

                case 3:
                    attr1 = grpAttrIn.get(genRandChoice(grpAttrIn.size()));
                    curFun1   =  functions.get(genRandChoice(functions.size()));
                    stm = "(" +  curFun1 + "(" + attr1 + ") " + arith + " " + "NULL" + ")";
                break;

            }

            return stm;

        }

}

