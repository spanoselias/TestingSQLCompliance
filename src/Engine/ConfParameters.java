package Engine;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Elias on 6/6/2017.
 */
public class ConfParameters
{

    //Indicates the max number of tables that can exist in a From clause
    public  int maxTableFrom;

    //Indicates the max number of attributes that can have in the SELECT clause
    public int maxAttrSel;

    //Indicates the max number of conditions that can have in the WHERE clause
    public int maxCondWhere;

    public double probWhrConst;

    public double repAlias;

    public HashMap<String, LinkedList<String>> relationsAttrs = new HashMap<>();

}