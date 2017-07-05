package Engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public  class GROUPBY
{
    private String stm;

    //This linkedlist is used to store all the attributes which are selected in the
    //GROUP by clause. The reason is that we cannot have attributes in the SELECT clause
    //that are not appear in the GROUP BY
    private LinkedList<String> groubyAttr;

    //This hashMap will be used to store all the attributes for each relation
    HashMap<String, LinkedList<String>> relationsAttrs;

    ConfParameters confPar;

    public GROUPBY(ConfParameters confParIn)
    {
        groubyAttr = new LinkedList<>();

        this.confPar = confParIn;
        stm = "GROUP BY ";
    }

    public String getGroupBy(LinkedList<String> frmRelts)
    {
        //It is used for string format purpose
        boolean isOut = false;

        int j=0;

        for (String relName : frmRelts)
        {
            if( this.confPar.maxAttrGrpBy == j)
            {
                break;
            }

            //We store each attribute which is selected in the GROUP BY
            //clause because we can only SELECT this attributes
            groubyAttr.add(relName);

            if (isOut == false)
            {
                stm += String.format("%s", relName);
                isOut = true;
            }

            else
            {
                stm += String.format(", %s", relName);
            }

            j++;
        }

        return stm;
    }

    public LinkedList<String> getAttrInGroup()
    {
        return this.groubyAttr;
    }

}