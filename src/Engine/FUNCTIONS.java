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



        public String getAttrComparison( LinkedList<String> selAttrsIn)
        {
            String stm="";

            this.selAttrs = selAttrsIn;

            String attr = selAttrsIn.get(Utilities.getRandChoice(selAttrsIn.size()));

            int switchPick = Utilities.getRandChoice(3);
            String curFun1 =  functions.get(Utilities.getRandChoice(functions.size()));
            String curFun2 = "";
            String oper = oper4.get(Utilities.getRandChoice(oper4.size()));

            switch(switchPick)
            {
                //Function with constant comparison
                case 0:
                    String con = String.valueOf(Utilities.getRandChoice(10000));
                    stm = curFun1 + "(" + attr + ")" + " " + oper + " " + con;
                break;

                //Function with function comparison
                case 1:
                    int counter =0;
                    //We trying to avoid comparing the same functions
                    do
                    {
                        counter ++;
                        curFun2 =functions.get(Utilities.getRandChoice(functions.size()));
                    }while(curFun2 == curFun1 && counter < 10000);

                    counter =0;
                    String attr2 ;
                    //We trying to avoid comparing the same functions
                    do
                    {
                        counter ++;
                        attr2 = selAttrsIn.get(Utilities.getRandChoice(selAttrsIn.size()));
                    }while(attr == attr2 && counter < 10000);

                    stm = curFun1 + "(" + attr + ")" + " " + oper + " " + curFun2 + "(" + attr2 + ")";
                break;

                case 2:
                    String constant = String.valueOf(Utilities.getRandChoice(10000));
                    stm = attr  + " "  + oper + " " + constant;
                break;
            }

            return stm;

        }


        public String getSelectAggr( LinkedList<String> grpAttrIn)
        {

            String arith = arithCompr.get( Utilities.getRandChoice(arithCompr.size()) );

            String stm="";

            this.selAttrs = grpAttrIn;

            String attr1 ="";
            String attr2 ="";

            String rel = "";
            String curFun1 = "";
            String curFun2 = "";


            int pick = Utilities.getRandChoice(4);
            switch (pick)
            {
                case 0:
                    attr1 = grpAttrIn.get(Utilities.getRandChoice(grpAttrIn.size()));
                    curFun1   =  functions.get(Utilities.getRandChoice(functions.size()));
                    stm = curFun1 + "(" + attr1 + ")" ;

                break;

                case 1:
                    attr1 = grpAttrIn.get(Utilities.getRandChoice(grpAttrIn.size()));
                    curFun1   =  functions.get(Utilities.getRandChoice(functions.size()));
                    rel = grpAttrIn.get(Utilities.getRandChoice(grpAttrIn.size()));
                    stm = "(" + curFun1 + "(" + attr1 + ") " + arith + " " + rel + ")";

                break;

                case 2:
                    attr1 = grpAttrIn.get(Utilities.getRandChoice(grpAttrIn.size()));
                    attr2 = grpAttrIn.get(Utilities.getRandChoice(grpAttrIn.size()));
                    curFun1   =  functions.get(Utilities.getRandChoice(functions.size()));
                    curFun2   =  functions.get(Utilities.getRandChoice(functions.size()));
                    stm = "(" +  curFun1 + "(" + attr1 + ") " + arith + " " + curFun2 + "(" + attr2 + ") " + ")";
                break;

                case 3:
                    attr1 = grpAttrIn.get(Utilities.getRandChoice(grpAttrIn.size()));
                    curFun1   =  functions.get(Utilities.getRandChoice(functions.size()));
                    stm = "(" +  curFun1 + "(" + attr1 + ") " + arith + " " + "NULL" + ")";
                break;

            }

            return stm;

        }

}

