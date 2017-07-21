/***********************************************************************************/
/*                                                                                 */
/*Name: Elias Spanos                                                               */
/*Date: 5/06/2017                                                                  */
/*Filename: STRINGS.java                                                           */
/*                                                                                 */
/***********************************************************************************/
package Engine;

/***********************************************************************************/
/*                                     LIBRARIES                                   */
/***********************************************************************************/
import java.util.HashMap;
import java.util.LinkedList;

public class STRINGS
{
    private String stm;

    //This list stores arithmetic operators for comparing different strings
    private LinkedList<String> comproper;

    //This hashMap will be used to store all the attributes for each relation
    HashMap<String, LinkedList<String>> relationsAttrs;

    ConfParameters confPar;

    public STRINGS(ConfParameters confParIn)
    {
        this.confPar = confParIn;
        stm = "LIKE ";
        stm = "NOT LIKE ";

        this.comproper = new LinkedList<>();
        this.comproper.add("<");
        this.comproper.add(">");
        this.comproper.add("<=");
        this.comproper.add("=");
        this.comproper.add(">=");
        this.comproper.add("<>");
    }

    public String genStrings(LinkedList<Attribute> stringAttrs)
    {
        String attr= stringAttrs.get(Utilities.getRandChoice(stringAttrs.size())).attrName;

        String str ="";
        int pick;

        String word = confPar.dictonary.get(Utilities.getRandChoice(confPar.dictonary.size()));
        int firspos = Utilities.getRandChoice(word.length())-1;
        int secpos;

        String attr1 =  stringAttrs.get(Utilities.getRandChoice(stringAttrs.size())).attrName;
        String attr2 =  stringAttrs.get(Utilities.getRandChoice(stringAttrs.size())).attrName;

        do
        {
          secpos = Utilities.getRandChoice(word.length()) - 1;
        }
        while(secpos < firspos);

        String subWord = word.substring( firspos, secpos );

        pick = Utilities.getRandChoice(9);

        switch(pick)
        {
            case 0:
                str += attr + " " + stm +  "'" + "%" + subWord + "'";
            break;

            case 1:
                str += attr + " " + stm  +  "'" +  subWord + "%" + "'";
            break;

            case 2:
                str += attr + " " + stm  +  "'" +  "%" + subWord + "%" + "'";
            break;
            case 3:
                str += attr + " " + stm  +  "'" +  "_" + subWord + "%" + "'";
            break;
            case 4:
                str =  attr + " = " + "'" + subWord + "'";
            break;

            case 5:
                int randNo = Utilities.getRandChoice(1000);
                str =  String.valueOf(randNo) + " = CAST( " + "'" + randNo + "'" + " AS INT )";
            break;

            case 6:
                str = attr1 + " " + comproper.get(Utilities.getRandChoice(this.comproper.size())) + " " + attr2;
            break;

            case 7:
                str = attr1 + " " + comproper.get(Utilities.getRandChoice(this.comproper.size())) + " " + "'"+ word + "'";
            break;

            case 8:
                str =  "'" + word + "'" + " " + comproper.get(Utilities.getRandChoice(this.comproper.size())) + " " + attr1;
            break;
        }

        return str;
    }

    public String genStringSelect(LinkedList<String> stringAttrs)
    {
        String stm="";
        String fun="";

        int pick = Utilities.getRandChoice(5);

        switch(pick)
        {
            case 0:
                String word1 = confPar.dictonary.get(Utilities.getRandChoice(confPar.dictonary.size()));
                String word2 = confPar.dictonary.get(Utilities.getRandChoice(confPar.dictonary.size()));

                stm =  "'" +  word1 +  "'"  + " || " + "'" + word2 + "'";
            break;

            case 1:
                fun = "CONCAT";

                fun +="(";

                int noOfWords  = Utilities.getRandChoice(3) + 1;
                for(int i=0; i < noOfWords;  i++ )
                {
                    if(i+1 != noOfWords )
                    {
                        fun += " " + "'" +  confPar.dictonary.get(Utilities.getRandChoice(confPar.dictonary.size())) + "'" + ", " ;
                    }
                    else
                    {
                        fun += " " + "'" + confPar.dictonary.get(Utilities.getRandChoice(confPar.dictonary.size())) + "'"  ;
                    }
                }
                fun += " )";

                stm = fun;
            break;

            case 2:

                fun = "REPLACE";

                String word = confPar.dictonary.get(Utilities.getRandChoice(confPar.dictonary.size()));
                String rep = word.substring(0, word.length() -1);
                String newWord = confPar.dictonary.get(Utilities.getRandChoice(confPar.dictonary.size()));

                fun +="( " + "'" + word + "' "  + ", '" + rep + "' ," + " '" +  newWord + "'" + ")";

                stm = fun;
            break;

            case 3:
                fun = "SUBSTR( ";
                String newWord1 = confPar.dictonary.get(Utilities.getRandChoice(confPar.dictonary.size()));
                int firspos = Utilities.getRandChoice(newWord1.length())-1;
                int secpos;

                do
                {
                    secpos = Utilities.getRandChoice(newWord1.length()) - 1;
                }
                while(secpos < firspos);

                fun  += "'" + newWord1 + "'" + "," + firspos + ", " + secpos + ")";

                stm = fun;
            break;

            case 4:
                fun = "TRIM";

                fun += "( " + " " + "' " + confPar.dictonary.get(Utilities.getRandChoice(confPar.dictonary.size())) + " '" + " )";

               stm = fun;

            break;
        }

        return stm;
    }
}
