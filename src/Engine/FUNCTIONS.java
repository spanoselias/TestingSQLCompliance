/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                 			   */
/*Date: 5/06/2017                                                                  */
/*Filename: FUNCTIONS.java                                                          */
/*                                                                                 */
/***********************************************************************************/

package Engine;

/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.HashMap;
import java.util.LinkedList;

/***********************************************************************************/
/*                          AGGREGATION FUNCTIONS CLASS                            */
/***********************************************************************************/
public class FUNCTIONS
{
        private LinkedList<String> aggrFunctions;

        private LinkedList<String> comprOper;

        private HashMap<String, LinkedList<String>> relAttrs;
        private LinkedList<Attribute> selAttrs;

        private LinkedList<String> arithCompr;

        public FUNCTIONS()
        {
            //Aggregation Functions
            this.aggrFunctions = new LinkedList<>();
            this.aggrFunctions.add("COUNT");
            this.aggrFunctions.add("AVG");
            this.aggrFunctions.add("MIN");
            this.aggrFunctions.add("MAX");
            this.aggrFunctions.add("SUM");


            this.comprOper = new LinkedList<>();
            this.comprOper.add("<");
            this.comprOper.add(">");
            this.comprOper.add("<=");
            this.comprOper.add("=");
            this.comprOper.add(">=");
            this.comprOper.add("<>");

            this.arithCompr = new LinkedList<>();
            this.arithCompr.add("*");
            this.arithCompr.add("-");
            this.arithCompr.add("+");
            this.arithCompr.add("/");
            this.arithCompr.add("%");
        }

/***********************************************************************************/
/*                              GENERATES COMPARISONS                              */
/***********************************************************************************/
public String getAttrComparison( LinkedList<Attribute> selAttrsIn)
{
    String stm="";

    this.selAttrs = selAttrsIn;

    Attribute attr = selAttrsIn.get(Utilities.getRandChoice(selAttrsIn.size()));

    int switchPick = Utilities.getRandChoice(3);
    String curFun1 =  aggrFunctions.get(Utilities.getRandChoice(aggrFunctions.size()));
    String curFun2 = "";
    String oper = comprOper.get(Utilities.getRandChoice(comprOper.size()));

    switch(switchPick)
    {
        //Function with constant comparison
        case 0:
            String con = String.valueOf(Utilities.getRandChoice(10000));
            stm = curFun1 + "(" + attr.attrName + ")" + " " + oper + " " + con;
        break;

        //Function with function comparison
        case 1:
            int counter =0;
            //We trying to avoid comparing the same functions
            do
            {
                counter ++;
                curFun2 =aggrFunctions.get(Utilities.getRandChoice(aggrFunctions.size()));
            }while(curFun2 == curFun1 && counter < 10000);

            counter =0;
            Attribute attr2 ;
            //We trying to avoid comparing the same functions
            do
            {
                counter ++;
                attr2 = selAttrsIn.get(Utilities.getRandChoice(selAttrsIn.size()));
            }while(attr.attrName == attr2.attrName && counter < 10000);

            stm = curFun1 + "(" + attr.attrName + ")" + " " + oper + " " + curFun2 + "(" + attr2.attrName + ")";
        break;

        case 2:
            String constant = String.valueOf(Utilities.getRandChoice(10000));
            stm = attr.attrName  + " "  + oper + " " + constant;
        break;
    }

    return stm;
}

/***********************************************************************************/
/*                GENERATING AGGREGATION FUNCTIONS FOR SELECT CLAUSE               */
/***********************************************************************************/
public String getSelectAggr( LinkedList<Attribute> selAttrIn, LinkedList<Attribute> grpAttrIn)
{

//This method is used for performing aggregation functions in the SELECT Clause based on the
//attributes which are selected in the GROUP BY Clause. E.g MAX(r1.a) > 100, MAX(r1.a) > AVG(r2.b).

    String arith = arithCompr.get( Utilities.getRandChoice(arithCompr.size()) );

    String stm="";

    //this.selAttrs = grpAttrIn;

    Attribute attr1;
    Attribute attr2;

    Attribute rel;
    String curFun1 = "";
    String curFun2 = "";

    int pick = Utilities.getRandChoice(6);
    switch (pick)
    {
        case 0:
            //"E.g MAX(r1.b)"
            curFun1   =  aggrFunctions.get(Utilities.getRandChoice(aggrFunctions.size()));
            stm = curFun1 + "(" + Utilities.chooseRandAttrGrpBy(selAttrIn, grpAttrIn) + ")" ;
        break;

        case 1:
            //"E.g MAX(r1.b) < r2.b "
            curFun1   =  aggrFunctions.get(Utilities.getRandChoice(aggrFunctions.size()));
            rel = grpAttrIn.get(Utilities.getRandChoice(grpAttrIn.size()));
            stm = "(" + curFun1 + "(" + Utilities.chooseRandAttrGrpBy(selAttrIn, grpAttrIn) + ") " + arith + " " + rel.attrName + ")";
        break;

        case 2:
            //"E.g MAX(r1.b) < AVG(r2.b)"
            curFun1   =  aggrFunctions.get(Utilities.getRandChoice(aggrFunctions.size()));
            curFun2   =  aggrFunctions.get(Utilities.getRandChoice(aggrFunctions.size()));
            stm = "(" +  curFun1 + "(" + Utilities.chooseRandAttrGrpBy(selAttrIn, grpAttrIn) + ") " + arith + " " + curFun2 + "(" + Utilities.chooseRandAttrGrpBy(selAttrIn, grpAttrIn) + ") " + ")";
        break;

        case 3:
            //"E.g MAX(r1.b) < NULL"
            curFun1   =  aggrFunctions.get(Utilities.getRandChoice(aggrFunctions.size()));
            stm = "(" +  curFun1 + "(" + Utilities.chooseRandAttrGrpBy(selAttrIn, grpAttrIn) + ") " + arith + " " + "NULL" + ")";
        break;

        case 4:
            //E.g MAX(r1.b) < 25"
            curFun1   =  aggrFunctions.get(Utilities.getRandChoice(aggrFunctions.size()));
            stm = "(" +  curFun1 + "(" + Utilities.chooseRandAttrGrpBy(selAttrIn, grpAttrIn) + ") " + arith + " " + Utilities.getRandChoice(1000) + ")";
        break;

        case 5:
            //E.g 25 > MAX(r1.b)"
            curFun1   =  aggrFunctions.get(Utilities.getRandChoice(aggrFunctions.size()));
            stm =  "(" + Utilities.getRandChoice(1000) + arith +   curFun1 + "(" + Utilities.chooseRandAttrGrpBy(selAttrIn, grpAttrIn) + ") )"  ;
        break;
    }

    return stm;

}

}

