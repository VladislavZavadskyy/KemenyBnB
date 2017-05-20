package Algorithms;

import File.FileIO;
import Structures.AbstractNode;
import Structures.DistanceNode;
import Structures.RNode;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Vladislav Zavadskyy on 25/04/2017.
 * Hour 15 of so called "Tuesday"
 * Day 25 of cycle 2017 since alleged birth of alleged son of God
 * Subcycle be of 04
 *
 * Naїve implementation with sDash checks
 */

public class BranchAndBound<N extends AbstractNode> {
    private int[][] rankings; //a matrix with element equal to ranking
                              // of alternative i of expert j
    private FileIO f;
    private boolean prune, detail;
    private N record;
    private int r[][];
    private int nodesExpanded, nodesCreated;
    private Class<N> cls;

    //region Helper functions
    public static int max(int array[]){
        int max=array[0];

        for (int i = 1; i < array.length; i++)
            if (array[i] > max)
                max = array[i];

        return max;
    }

    private static int[] init(int length, int value){
        int[] array = new int[length];
        for (int i = 0; i<length; i++)
            array[i]=value;
        return array;
    }
    //endregion

    //region Constructor
    public BranchAndBound(int Rankings[][], Properties props, FileIO F, Class<N> cls){
        rankings = Rankings; f = F;
        AbstractNode.num = 0;
        record = null;
        this.cls = cls;
        r = RNode.r(rankings);
        nodesExpanded = 0;
        nodesCreated = 1;

        prune = Objects.equals(props.getProperty("apply.prune"), "t");
        detail = Objects.equals(props.getProperty("write.det"), "t");
    }
    //endregion

    public int[] run() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        long sTime = System.currentTimeMillis();
        bnbCall(cls.getDeclaredConstructor(cls,int[].class,int[][].class)
                .newInstance(null,init(rankings[0].length, -1),rankings));
        //region write summary
        f.write("\r\n_____________________________________");
        f.write("\r\n\tSolution obtained:\r\n");
        f.write(AbstractNode.transform(record.ranking) + "\r\n");
        costsWriter(record);
        f.write(String.format("Nodes expanded: %d\r\n", nodesExpanded));
        f.write(String.format("Nodes created: %d\r\n", nodesCreated));
        f.write(String.format("Time elapsed: %d ms.\r\n", System.currentTimeMillis() - sTime));
        f.write("_____________________________________\r\n");
        //endregion
        return record != null ? record.ranking : new int[0];
    }

    private void bnbCall(N parent) throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        ArrayList<N> leafs = createSuccessors(parent);
        //region write successors
        if (detail&&!leafs.isEmpty()) {
            f.write(String.format("Expanding node:\r\n%s", parent.toString()));
            f.write("Successors:");
            for (N successor : leafs)
                f.write(String.format("\t%s", successor.toString()));
        }
        //endregion
        //region counters
        nodesExpanded++;
        nodesCreated += leafs.size();
        //endregion
        while (!leafs.isEmpty()) {
            //region find min
            Iterator<N> it = leafs.iterator();
            N min = it.next(), cur;
            while (it.hasNext()) {
                cur = it.next();
                if (cur.cost < min.cost) min = cur;
            }
            //endregion
            //region check if record is optimal
            if (record != null && min.cost > record.cost) {
                leafs.clear();
                return;
            }
            //endregion
            //region prune
            if (prune)
                min.prune();
            //endregion
            bnbCall(min);
            leafs.remove(min);
        }
        //region check if record
        if (parent.indexPoll.size()==0 && (record == null || parent.cost < record.cost)) {
            record = parent;
            if (detail) {
                f.write("\r\n--Record achieved--\r\n");
                f.write(record.toString());
                f.write("\r\n--------------------\r\n");
            }
        }
        //endregion
    }

    private ArrayList<N> createSuccessors(AbstractNode parent) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        ArrayList<N> successors = new ArrayList<>();
        int i;
        HashSet<Integer> indexPoll = parent.indexPoll;
        if ((i = AbstractNode.valueIn(parent.ranking,-1))!=-1)
            for(int index: indexPoll) {
                int[] copy = parent.ranking.clone();
                copy[i] = index;
                successors.add(cls.getDeclaredConstructor(cls,int[].class,int[][].class, int.class)
                        .newInstance(parent,copy,rankings, i));
            }
        return successors;
    }

    private void costsWriter(N node) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        if (cls == DistanceNode.class) {
            f.write(String.format("Distance: %1$,.2f\r\n", node.cost));
            f.write(String.format("R cost: %d\r\n", (int) RNode.class.getDeclaredConstructor(RNode.class, int[].class, int[][].class)
                    .newInstance(null,node.ranking,rankings).cost));
        } else {
            f.write(String.format("Distance: %1$,.2f\r\n", DistanceNode.class.getDeclaredConstructor(DistanceNode.class, int[].class, int[][].class)
                    .newInstance(null,node.ranking,rankings).cost));
            f.write(String.format("R cost: %d\r\n", (int)node.cost));
        }
    }
}
