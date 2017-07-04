package Engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public  class HAVING
{
    private String stm;

    //This linkedlist is used to store all the attributes which are selected in the
    //GROUP by clause. The reason is that we cannot have attributes in the SELECT clause
    //that are not appear in the GROUP BY
    private LinkedList<String> havingAttr;

    private FUNCTIONS genFunctions;

    //This hashMap will be used to store all the attributes for each relation
    HashMap<String, LinkedList<String>> relationsAttrs;

    ConfParameters confPar;


    public HAVING(ConfParameters confParIn)
    {
        havingAttr = new LinkedList<>();

        this.confPar = confParIn;
        stm = "HAVING ";

        genFunctions = new FUNCTIONS();
    }

    public String genHaving(LinkedList<String> frmRelts)
    {
        stm += genFunctions.getAttrComparison(frmRelts);

        return stm;
    }

    public LinkedList<String> getAttrInHaving()
    {
        return this.havingAttr;
    }

}