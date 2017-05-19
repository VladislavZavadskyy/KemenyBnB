package Structures;

import java.util.Collections;
import java.util.HashSet;

/**
 * Created by Vladislav Zavadskyy on 10/05/2017.
 * Hour 16 of so called "Wednesday"
 * Day 10 of cycle 2017 since alleged birth of alleged son of God
 * Subcycle be of 05
 */
public class DistanceNode extends AbstractNode{

    private double[] squareSum;
    private DistanceNode parent;

    public DistanceNode(DistanceNode Parent, int[] Ranking, int[][] rankings, int diff){
        super(Parent, Ranking,rankings, diff);
        this.parent = Parent;
        this.cost = calcDistance(rankings);
    }

    public DistanceNode(DistanceNode Parent, int[] Ranking, int[][] rankings){
        super(Parent, Ranking, rankings);
        this.parent = Parent;
        this.cost = eval(rankings);
        this.squareSum = getSquareSums(ranking,rankings);
    }

    public void prune(int[][] r, int[][] rankings){
        if (valueIn(sD(r),0)!=-1||valueIn(sDD(r),0)!=-1) {
            int i, n = ranking.length;

            int[][] modR = mClone(r);
            for (int j = 0; j < n; j++) {
                if (ranking[j] != -1)
                    modR[j][j] = ranking[j];
            }

            while ((i = valueIn(sD(modR), 0)) != -1) {
                int min = Collections.min(indexPoll);
                ranking[i] = min;
                modR[i][i] = min;
                indexPoll.remove(min);
            }

            while ((i = valueIn(sDD(modR), 0)) != -1) {
                int max = Collections.max(indexPoll);
                ranking[i] = max;
                modR[i][i] = max;
                indexPoll.remove(max);
            }


            squareSum = getSquareSums(ranking,rankings);
            cost = eval(rankings);
        }
    }

    @Override
    public String toString(){
        String s = "";
        if (parent == null)
            s+="Root node\r\n";
        else
            s+=String.format("\r\nStructures.DistanceNode %d, child of node %d.\r\n",
                    node_num, parent.node_num);
        s+=String.format("\tRanking: \r\n\t%s\r\n", transform(ranking));
        s+=String.format("\tDistance: %1$,.2f\r\n",  cost);
        return s;
    }

    //region distance evaluators
    public double eval(int[][] rankings){
        HashSet<Integer> indexes = new HashSet<>();
        double squareSum, distSum = 0;
        for (int i = 0, l = ranking.length; i < l; i++)
            if (ranking[i] > 0) indexes.add(i);
        for (int[] expRanking: rankings) {
            squareSum = 0;
            for (int i : indexes)
                squareSum += Math.pow(ranking[i] - expRanking[i], 2);
            distSum += Math.sqrt(squareSum);
        }
        return distSum;
    }

    private double calcDistance(int[][] rankings){
        cost = 0;
        this.squareSum = parent.squareSum.clone();
        for (int i = 0, l = rankings.length; i < l; i++) {
            squareSum[i] += Math.pow(ranking[diff] - rankings[i][diff], 2);
            cost += Math.sqrt(squareSum[i]);
        }
        return cost;
    }

    private static double[] getSquareSums(int[] current, int[][]rankings){
        HashSet<Integer> indexes = new HashSet<>();
        double squareSums[] = new double[rankings.length];
        for (int i = 0, l = current.length; i < l; i++)
            if (current[i] > 0) indexes.add(i);
        for (int i = 0, l = rankings.length; i < l; i++)
            for (int j : indexes)
                squareSums[i] += Math.pow(current[j] - rankings[i][j], 2);
        return squareSums;
    }
    //endregion
}
