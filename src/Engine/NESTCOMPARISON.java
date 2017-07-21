/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                               */
/*Date: 5/06/2017                                                                  */
/*Filename: NESTCOMPARISON.java                                                    */
/*                                                                                 */
/***********************************************************************************/

package Engine;

/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.HashMap;
import java.util.LinkedList;

public class NESTCOMPARISON
{
        private LinkedList<String> oper1;
        private LinkedList<String> oper2;
        private LinkedList<String> oper3;
        private LinkedList<String> comproper;

        private boolean isOneAttr;

        private HashMap<String, LinkedList<String>> relAttrs;
        private LinkedList<Attribute> selectedTables;

        private int noAllAttr;

        public NESTCOMPARISON()
        {
            this.oper1 = new LinkedList<>();
            this.oper1.add("EXISTS");
            this.oper1.add("NOT EXISTS");

            this.oper2 = new LinkedList<>();
            this.oper2.add("NOT IN");
            this.oper2.add("IN");

            this.oper3 = new LinkedList<>();
            this.oper3.add("ANY");
            this.oper3.add("ALL");

            this.comproper = new LinkedList<>();
            this.comproper.add("<");
            this.comproper.add(">");
            this.comproper.add("<=");
            this.comproper.add("=");
            this.comproper.add(">=");
            this.comproper.add("<>");

            this.isOneAttr = false;
        }

        public String getAttrComparison(HashMap<String, LinkedList<Attribute>> relAttrs, LinkedList<Attribute> selectedTablesIn)
        {
            this.isOneAttr = false;
            String stm="";

            this.selectedTables = selectedTablesIn;

            int pick;

            int switchPick = Utilities.getRandChoice(3);

            //We can have comparisons with more than one attributes e.g (r1.b, r2.a) > (r1.a, r2.c). Thus,
            //we choose the number of attributes
            int numOfAttr = Utilities.getRandChoice(selectedTables.size());

            //Always have at least one attribute to compare
            if(numOfAttr ==0)
                 { numOfAttr =1; }

            this.noAllAttr = numOfAttr;

            //The below code is used to perform row comparisons. Comparisons with
            //more than one attribute. For example, (r1.a, r2.b) > ANY (SELECT r1.c, r2.a ..... )
            String allAttr="";
            for(int g=0; g < numOfAttr; g++ )
            {

                if( g==0 )
                {
                    allAttr += "( " + Utilities.chooseRandAttr(selectedTablesIn);
                }
                else if( g < numOfAttr )
                {
                    allAttr += "," + Utilities.chooseRandAttr(selectedTablesIn);
                }
            }

            allAttr +=" )";

            switch(switchPick)
            {
                case 0:
                    pick = Utilities.getRandChoice(oper1.size());
                    stm = oper1.get(pick);
                    this.noAllAttr = 0;
                break;

                case 1:
                    //Attribute rel1 = this.selectedTables.get(Utilities.getRandChoice(this.selectedTables.size()));
                    stm += allAttr + " " + oper2.get(Utilities.getRandChoice(oper2.size()));
                    this.isOneAttr = true;
                break;

                case 2:
                   // Attribute rel = this.selectedTables.get(Utilities.getRandChoice(this.selectedTables.size()));
                    stm += allAttr + " " + comproper.get(Utilities.getRandChoice(comproper.size())) + " " + oper3.get(Utilities.getRandChoice(oper3.size()));
                    this.isOneAttr = true;
                break;
            }

            return stm;
        }

        public boolean getIsOneAttr()
        {
            return this.isOneAttr;
        }

        public int getTotalAttr()
        {
            return this.noAllAttr;
        }

    }

