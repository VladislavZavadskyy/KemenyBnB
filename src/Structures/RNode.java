package Structures;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Vladislav Zavadskyy on 19/05/2017.
 * Hour 09 of so called "Friday"
 * Day 19 of cycle 2017 since alleged birth of alleged son of God
 * Subcycle be of 05
 */
public class RNode extends AbstractNode {

    public static int[][] r;
    private ArrayList<Integer> rankList;

    public RNode(RNode Parent, int[] Ranking, int[][] rankings, int diff){
        super(Parent, Ranking,rankings, diff);
        rankList = new ArrayList<>(Parent.rankList);
        rankList.set(diff,ranking[diff]);
        eval();
    }

    public RNode(RNode Parent, int[] Ranking, int[][] rankings){
        super(Parent, Ranking,rankings);
        r = r(rankings);
        rankList = rankToList();
        eval();
    }

    public void prune(){
        int i;

        while ((i = valueIn(sD(r), 0)) != -1) {
            int min = Collections.min(indexPoll);
            ranking[i] = min;
            rankList.set(i,min);
            indexPoll.remove(min);
        }

        while ((i = valueIn(sDD(r), 0)) != -1) {
            int max = Collections.max(indexPoll);
            ranking[i] = max;
            rankList.set(i,max);
            indexPoll.remove(max);
        }

        eval();
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

    private void eval(){
        int n = ranking.length, p,q;
        for(int i = 1; i < n+1; i++)
        for(int j = i+1; j < n+1; j++) {
            p = rankList.indexOf(i);
            q = rankList.indexOf(j);
            if (p!=-1 && q!=-1)
                cost += r[p][q];
        }
    }

    private ArrayList<Integer> rankToList(){
        ArrayList<Integer> rankList = new ArrayList<>();
        for(int rank:ranking)
            rankList.add(rank);
        return rankList;
    }

    //region r calculators
    public static int[][] r(int[][] rankings){
        int n = rankings[0].length;
        int[][] P = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                P[i][j] = 1;

        return r(rankings, P);
    }

    private static int[][] r(int[][] rankings, int[][] P) {
        int n = rankings[0].length;
        int[][][] p = p(rankings);
        int[][] r = new int[n][n];

        for (int[][] pv : p)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    r[i][j] += Math.abs(pv[i][j] - P[i][j]);

        for (int i = 0; i < n; i++)
            r[i][i] = 0;

        return r;
    }
    //endregion

    //region p calculators
    private static int[][][] p(int[][] rankings){
        int m = rankings.length;
        int[][][] p = new int[m][][];

        for (int v = 0; v < m; v++)
            p[v] = p(rankings[v]);

        return p;
    }

    private static int[][] p(int[] ranking){
        int n = ranking.length;
        int[][] p = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                p[i][j] = (ranking[j] == ranking[i]) ? 0 :
                        (ranking[j] > ranking[i]) ? 1 : -1;

        return p;
    }
    //endregion

    //region s (double) dash calculators
    private int[] sD(int[][] r){
        int n = r.length;
        int[] tensor = new int[n];
        for(int i=0; i<n; i++)
            if (rankList.get(i)>0)
                tensor[i]=-1;

        for(int i=0; i<r.length; i++)
            if (rankList.get(i)<0)
                for (int j = 0; j < r.length; j++)
                    if (rankList.get(j) < 0)
                        tensor[i] += (r[i][j] > r[j][i]) ? 1 : 0;

        return tensor;
    }

    private int[] sDD(int[][] r){
        int n = r.length;
        int[] tensor = new int[n];
        for(int i=0; i<n; i++)
            if (rankList.get(i)>0)
                tensor[i]=-1;

        for(int i =0; i<r.length; i++)
            if (rankList.get(i)<0)
            for (int j = 0; j < r.length; j++)
                if (rankList.get(j) < 0)
                    tensor[i] += (r[i][j] < r[j][i]) ? 1 : 0;

        return tensor;
    }
    //endregion

}
