package Structures;

import java.util.Collections;

/**
 * Created by Vladislav Zavadskyy on 19/05/2017.
 * Hour 09 of so called "Friday"
 * Day 19 of cycle 2017 since alleged birth of alleged son of God
 * Subcycle be of 05
 */
public class RNode extends AbstractNode {

    public RNode(RNode Parent, int[] Ranking, int[][] rankings, int diff){
        super(Parent, Ranking,rankings, diff);
        this.cost = eval(rankings);
    }

    public RNode(RNode Parent, int[] Ranking, int[][] rankings){
        super(Parent, Ranking,rankings);
        this.cost = eval(rankings);
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
        s+=String.format("\tR Cost: %1$,.2f\r\n",  cost);
        return s;
    }

    //region r evaluators
    private static double evalR(int[][] r){
        int sum = 0, n = r.length;

        for (int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++)
                sum += r[i][j];

        return sum;
    }

    public double eval(int[][]rankings){
        int n=0, c=0;
        for (int i : ranking)
            if (i>0) n++;
        int[] curPruned = new int[n];
        for (int i : ranking)
            if (i>0) {
                curPruned[c]=i;
                c++;
            }

        c=0;
        int[][] tmpRankings = new int[rankings.length][n];

        for (int i = 0; i < ranking.length; i++)
            if (ranking[i]>0){
                for(int j =0; j < rankings.length; j++)
                    tmpRankings[j][c]=rankings[j][i];
                c++;
            }
        return evalR(r(tmpRankings, p(curPruned)));
    }
    //endregion

}
