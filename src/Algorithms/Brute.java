package Algorithms;

import File.FileIO;
import Structures.AbstractNode;
import Structures.DistanceNode;
import Structures.RNode;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by Vladislav Zavadskyy on 04/05/2017.
 * Hour 19 of so called "Thursday"
 * Day 04 of cycle 2017 since alleged birth of alleged son of God
 * Subcycle be of 05
 */
public class Brute<N extends AbstractNode> {

    private N min;
    private FileIO f;
    private Class<N> cls;
    private int[][] rankings;
    private long iterNum;

    public Brute(int Rankings[][], FileIO f, Class<N> cls){
        rankings = Rankings;
        min = null;
        this.f = f;
        iterNum = 0;
        this.cls = cls;
    }

    public int[] force() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ArrayList<Integer> ranking = new ArrayList<>();
        for (int i = 0; i < rankings[0].length; i++)
            ranking.add(i+1);
        long sTime = System.currentTimeMillis();
        permute(ranking,0);
        //region write result
        f.write("Brute force:\r\n");
        f.write("The median: \r\n\t");
        f.write(AbstractNode.transform(min.ranking) + "\r\n");
        costsWriter(min);
        f.write(String.format("Iterations: %d\r\n", iterNum));
        f.write(String.format("Time elapsed: %d ms.\r\n", System.currentTimeMillis() - sTime));
        f.write("\r\n_____________________________________\r\n");
        //endregion
        RNode.r = null;
        return min.ranking;
    }

    private void permute(ArrayList<Integer> arr, int k) throws InstantiationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for(int i = k; i < arr.size(); i++){
            java.util.Collections.swap(arr, i, k);
            permute(arr, k+1);
            java.util.Collections.swap(arr, k, i);
        }
        if (k == arr.size() -1){
            iterNum++;
            N current = cls.getDeclaredConstructor(cls,int[].class,int[][].class)
                    .newInstance(null,toArray(arr),rankings);
            if (min==null||current.cost<min.cost)
                min = current;
        }
    }

    private static int[] toArray(ArrayList<Integer> list){
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
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
