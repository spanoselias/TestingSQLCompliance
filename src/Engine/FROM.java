package Engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public  class FROM
{
    private Relation rel [];

    //This variable will be used to create the Engine.FROM Clause String
    private String fromStm;

    //This HashMap is used to store all the relations with their associated attributes. The
    //key represents the relation name and the list stores all the attributes for each key (relation)
    private HashMap<String, LinkedList<String>> allRelAttrs;

    //This LinkedList will be used to store all the relations that will be randomly chosen
    //for the Engine.FROM Clause
    private LinkedList<String> selectedReltsInFrom;

    private LinkedList<String> alias;

    public FROM(  LinkedList<String> aliasIn, HashMap<String, LinkedList<String>> allRelAttrsIn )
    {
        this.alias = aliasIn;

        this.allRelAttrs = allRelAttrsIn;

        fromStm = "";

        //We copy all the relations from the LinkedList to an array in order to
        //shuffle the array and afterward to choose some random relations to create
        //FROM Clause
        rel = new Relation[this.allRelAttrs.size()];
        int i=0;
        for (String relation : this.allRelAttrs.keySet())
        {
            rel[i] = new Relation();

            rel[i].setRelName(relation);

            for (String attr : this.allRelAttrs.get(relation))
            {
                rel[i].setAttrName(attr);
            }

            i++;
        }

        //This list will be used to track which relations have been selected from the generator. Thus, it will be useful
        //to know the relations in the Engine.FROM statement in order to know what attributes to include in the Engine.SELECT_rmv statement
        selectedReltsInFrom = new LinkedList<>();
    }

    public String getFrom(long uniqID)
    {
        //Clear LinkedList
        selectedReltsInFrom.clear();

        Random randomGenerator = new Random();

        int pickRand;

        //This random number indicates how many relation the Engine.FROM Clause will have
        pickRand = (randomGenerator.nextInt(rel.length)) % rel.length + 1  ;

        //We shuffle the array of the relations to avoid choosing always the same order.
        shuffleArray(rel);

        String stm = "";

        String tmp = stm;
        stm = "FROM" + tmp;

        for (int i = 0; i < pickRand; i++)
        {
           //to be checked
           for(String attr: this.allRelAttrs.get(rel[i].getRelName()))
           {
               this.selectedReltsInFrom.add( (rel[i].getRelName().toLowerCase()) + "." + attr);
           }

           // this.allRelAttrs.put(rel[i].getRelName(), rel[i].getRelAttrs());

            if (i == 0)
                stm += String.format(" %s AS %s", rel[i].getRelName(), rel[i].getRelName().toLowerCase() );
            else
                stm += String.format(", %s AS %s", rel[i].getRelName(), rel[i].getRelName().toLowerCase() );
        }

        fromStm = stm;

        return stm;
    }

    public LinkedList<String> getSelectedTables()
    {
        return this.selectedReltsInFrom;
    }

    public String getFromSql() {
        return fromStm;
    }

    public HashMap<String, LinkedList<String>> getRelAttrs() {
        return this.allRelAttrs;
    }


    public  void shuffleArray(Relation[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    private  void swap(Relation[] a, int i, int change) {
        Relation helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }
}