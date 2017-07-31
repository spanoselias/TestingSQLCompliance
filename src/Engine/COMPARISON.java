/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                 			   */
/*Date: 5/06/2017                                                                  */
/*Filename: COMPARISON.java                                                        */
/*                                                                                 */
/***********************************************************************************/
package Engine;

/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.HashMap;
import java.util.LinkedList;


public class COMPARISON
{
    //This variable is used in order to generate some constant numbers
    //which can be used for comparisons
    final int CONSTNO = 50;

    //Stores all the operators that can be used for any comparison
    private LinkedList<String> comproper;

    private LinkedList<String> checkNull;

    private LinkedList<Attribute> selectedTables;

    private LinkedList<String> Null;

    //This list will be used to have arithmetic with constants and NULL
    //in the SELECT CLAUSE
    private LinkedList<String> arithmOper;

    private LinkedList<String> constAndNullAttr;

    public COMPARISON()
    {
        this.comproper = new LinkedList<>();
        this.comproper.add("<");
        this.comproper.add(">");
        this.comproper.add("<=");
        this.comproper.add("=");
        this.comproper.add(">=");
        this.comproper.add("<>");

        Null = new LinkedList<>();
        Null.add("NULL");

        //This loop in used to insert some constants in the linkedlist in order to
        //have some comparisons like NULL = NULL, attr > 2 and so for.
        for(int i=0; i< CONSTNO; i++)
        {
            Null.add(Integer.toString(i));
        }

        this.arithmOper = new LinkedList<>();
        this.arithmOper.add("*");
        this.arithmOper.add("-");
        this.arithmOper.add("+");
        this.arithmOper.add("/");
        this.arithmOper.add("%");

        this.constAndNullAttr = new LinkedList<>();
        for(int i =0; i< 8; i++ )
        {
            this.constAndNullAttr.add(String.valueOf(i));
        }
        for(int i =0; i< 1; i++ )
        {
            this.constAndNullAttr.add("NULL");
        }

        this.checkNull = new LinkedList<>();
        this.checkNull.add("IS NULL");
        this.checkNull.add("IS NOT NULL");
    }


    public String getAttrComparison(HashMap<String, LinkedList<Attribute>> relAttrs, LinkedList<Attribute> selectedTablesIn, ConfParameters confIn)
    {

        this.selectedTables = selectedTablesIn;

        int pick;
        Attribute rel1 = new Attribute();
        Attribute rel2 = new Attribute();

        rel1.attrName = "";
        rel2.attrName = "";

        String arithmCompStr = "";

        String oper = comproper.get(Utilities.getRandChoice(comproper.size()));

        //The probWhr represents the probability of having
        //constants or NULLS to the WHERE comparisons
        pick = Utilities.getRandChoice( 100 );
        if(pick <  (int)(confIn.probWhrConst * 100) )
        {
            pick = Utilities.getRandChoice(3);
        }
        else
        {
             //We randomly choose if the comparison will be
             //with one attribute and one constant
             pick =  Utilities.getRandChoice(2);
             if(pick == 0 )
             {
                 pick = 4;
             }
             else
             {
                 pick = Utilities.getRandChoice(100);
                 if (pick < (int) (confIn.rowcompar * 100))
                 {
                     pick = 5;
                 }
                 else
                 {
                     pick = Utilities.getRandChoice(100);
                     if (pick < (int) (confIn.isNULL * 100))
                     {
                         pick = 6;
                     }
                     else
                     {
                         pick = 4;
                     }
                 }
             }
        }

        //The idea of current switch is to do comparisons between two relation's attributes or between
        //one attribute and one constant or between an attribute and a NULL
        switch (pick)
        {
            case 0:
                //In this case both comparisons are nulls e.g r1.b > null
                rel1.attrName =  Null.get(Utilities.getRandChoice(Null.size()));
                rel2 = this.selectedTables.get(Utilities.getRandChoice(this.selectedTables.size()));
            break;

            case 1:
                rel1.attrName = "NULL";
                rel2.attrName = Null.get(Utilities.getRandChoice(Null.size()));
            break;

            case 2:
                rel1 = this.selectedTables.get(Utilities.getRandChoice(this.selectedTables.size()));
                rel2.attrName = Null.get(Utilities.getRandChoice(Null.size()));
            break;

            case 3:
                    //Comparison between two attributes, one attribute and one constant and so forth. Goto
                    // getArithCompr method for more details
                    arithmCompStr  = getArithCompr(this.selectedTables);
            break;

            case 4:
                int pickRandRel = Utilities.getRandChoice(this.selectedTables.size());
                int pickRand;

                rel1 = this.selectedTables.get(pickRandRel);

                int prevPickRand = pickRandRel;
                int counter=0;

                //We are trying to do the comparison from two different relations
                do
                {
                    pickRand = Utilities.getRandChoice(this.selectedTables.size());

                    ++counter;

                  // The idea is that we try to do the comparison in the WHERE clause with
                  // two different attributes. Thus, if the attributes are the same we try to
                  // pick a new attribute. But we set up a threshold in order to avoid the case where
                  // we have for many times the same attributes
                } while(prevPickRand == pickRand && counter < 1000);


                rel2 = this.selectedTables.get(pickRand);
            break;

            case 5:

                //This case is used to generate row comparisons e.g (r1.b, r2.a) > (r1.a, r2.c). Thus,
                //we choose the number of attributes
                int numOfAttr = Utilities.getRandChoice(this.selectedTables.size());

                //Always have at least one attribute to compare
                if(numOfAttr ==0)
                { numOfAttr =1; }

                if(numOfAttr > 0)
                {
                    for (int j = 0; j < 2; j++)
                    {
                        //We create our format. For example (r1.b, r2.a)
                        String attrs = "";

                        for (int i = 0; i < numOfAttr; i++)
                        {
                            if (i == 0)
                            {
                                attrs +=   selectedTablesIn.get(Utilities.getRandChoice(selectedTablesIn.size())).attrName;
                            } else if (i < numOfAttr)
                            {
                                attrs += "," + selectedTablesIn.get(Utilities.getRandChoice(selectedTablesIn.size())).attrName;
                            }
                        }

                        if (j == 0) {
                            rel1.attrName = "(" + attrs + ")" ;
                        } else if (j == 1)
                        {
                            rel2.attrName = "(" +  attrs + ")" ;
                        }
                    }
                }
            break;

            case 6:
                arithmCompStr = selectedTablesIn.get(Utilities.getRandChoice(selectedTablesIn.size())).attrName + " " + this.checkNull.get(Utilities.getRandChoice(this.checkNull.size()));
            break;

        }//Switch

        //We want to use negation in some of the expressions. Thus, we randomly decide if
        //the below expression will have negation or not.
        String res="";

        pick = Utilities.getRandChoice(10);
        if( pick ==0 )
        {
            if(arithmCompStr != "")
            {
                res = "NOT" + "(" + arithmCompStr + " ) "  ;
            }
            else
            {
                res = "NOT" + "(" + rel1.attrName +  " " + oper + " " + rel2.attrName + " ) "  ;
            }
        }
        else
        {
            if(arithmCompStr != "")
            {
                res =arithmCompStr;
            }
            else
            {
                res =  rel1.attrName + " " + oper + " " + rel2.attrName  ;
            }
        }

        return res;

    }

    public String getArithCompr(LinkedList<Attribute> selectedTablesIn)
    {
        String stm="";
        int pick;
        Attribute rel1 = new Attribute();
        Attribute rel2 = new Attribute();
        String const1 = "";
        String const2 = "";

        String arith = arithmOper.get( Utilities.getRandChoice(arithmOper.size()) );

        pick = Utilities.getRandChoice(4);
        switch (pick)
        {
            case 0:
                //The comparison is performed with two constants
                const1 = constAndNullAttr.get( (Utilities.getRandChoice(constAndNullAttr.size())));
                const2 = constAndNullAttr.get( (Utilities.getRandChoice(constAndNullAttr.size())));
                stm = const1 + arith + const2;
            break;

            case 1:
                //The comparison is performed with two attributes
                rel1 = selectedTablesIn.get(Utilities.getRandChoice(selectedTablesIn.size()));
                rel2 = selectedTablesIn.get(Utilities.getRandChoice(selectedTablesIn.size()));
                stm = rel1.attrName + arith + rel2.attrName;
            break;

            case 2:
                //The comparison is performed with one attribute and one constant
                rel1 = selectedTablesIn.get(Utilities.getRandChoice(selectedTablesIn.size()));
                const1 = constAndNullAttr.get( (Utilities.getRandChoice(constAndNullAttr.size())));
                stm = rel1.attrName + arith + const1;
            break;

            case 3:
                //The comparison is performed with one constant and one attribute
                rel1 = selectedTablesIn.get(Utilities.getRandChoice(selectedTablesIn.size()));
                const1 = constAndNullAttr.get( (Utilities.getRandChoice(constAndNullAttr.size())));
                stm = const1 + arith + rel1.attrName;
            break;
        }

        return stm;
    }
}