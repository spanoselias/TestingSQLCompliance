package Engine;

import java.util.HashMap;
import java.util.LinkedList;

import java.util.Random;


public class NESTCOMPARISON
{
        private LinkedList<String> oper1;
        private LinkedList<String> oper2;
        private LinkedList<String> oper3;
        private LinkedList<String> oper4;

        private boolean isOneAttr;

        private HashMap<String, LinkedList<String>> relAttrs;
        private LinkedList<String> selectedTables;

        public NESTCOMPARISON()
        {
            this.oper1 = new LinkedList<>();
            this.oper1.add("EXISTS");
            this.oper1.add("NOT EXISTS");

            this.oper2 = new LinkedList<>();
            this.oper2.add("NOT IN");
            this.oper2.add("IN");
            //this.oper2.add("NOT IN");
            //this.oper2.add("IN");


            this.oper3 = new LinkedList<>();
            this.oper3.add("ANY");
            this.oper3.add("ALL");

            this.oper4 = new LinkedList<>();
            this.oper4.add("<");
            this.oper4.add(">");
            this.oper4.add("<=");
            this.oper4.add("=");
            this.oper4.add(">=");
            this.oper4.add("<>");

            this.isOneAttr = false;

        }


        public String getAttrComparison(HashMap<String, LinkedList<String>> relAttrs, LinkedList<String> selectedTablesIn)
        {

            this.isOneAttr = false;
            String stm="";

            this.selectedTables = selectedTablesIn;

            int pick;

            int switchPick = Utilities.getRandChoice(3);
            switch(switchPick)
            {
                case 0:
                    pick = Utilities.getRandChoice(oper1.size());
                    stm = oper1.get(pick);
                break;

                case 1:
                    String rel1 = this.selectedTables.get(Utilities.getRandChoice(this.selectedTables.size()));
                    stm +=rel1 + " " + oper2.get(Utilities.getRandChoice(oper2.size()));
                    this.isOneAttr = true;
                break;

                case 2:
                    String rel = this.selectedTables.get(Utilities.getRandChoice(this.selectedTables.size()));
                    stm +=rel + " " + oper4.get(Utilities.getRandChoice(oper4.size())) + " " + oper3.get(Utilities.getRandChoice(oper3.size()));
                    this.isOneAttr = true;
                break;
            }

            return stm;
        }

        public boolean getIsOneAttr()
        {
            return this.isOneAttr;
        }


    }

