package Structures;

import Algorithms.BranchAndBound;
import org.jetbrains.annotations.Contract;

import java.util.HashSet;

/**
 * Created by Vladislav Zavadskyy on 18/05/2017.
 * Hour 21 of so called "Thursday"
 * Day 18 of cycle 2017 since alleged birth of alleged son of God
 * Subcycle be of 05
 */
public abstract class AbstractNode {
    AbstractNode parent;
    public int[] ranking;

    public double cost;
    int diff;
    public HashSet<Integer> indexPoll;

    int node_num;
    public static int num = 0;

    AbstractNode(AbstractNode Parent, int[] Ranking, int[][] rankings, int diff){
        this.parent = Parent;
        this.ranking = Ranking;
        this.diff = diff;
        this.indexPoll = (HashSet<Integer>)parent.indexPoll.clone();
        indexPoll.remove(ranking[diff]);
        node_num = ++num;
    }

    AbstractNode(AbstractNode Parent, int[] Ranking, int[][] rankings){
        this.parent = Parent;
        this.ranking = Ranking;
        diff = -1;
        this.indexPoll = formIndexPoll(ranking);
        node_num = ++num;
    }

    public abstract void prune(int[][] r, int[][] rankings);
    public abstract double eval(int[][]rankings);

    //region r calculators
    public static int[][] r(int[][] rankings){
        int n = rankings[0].length;
        int[][] P = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                P[i][j] = 1;

        return r(rankings, P);
    }

    static int[][] r(int[][] rankings, int[][] P) {
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

    static int[][] p(int[] ranking){
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
    @Contract(pure = true)
    static int[] sD(int[][] r){
        int n = r.length;
        int[] tensor = new int[n];

        for (int i = 0; i < n; i++) {
            if (r[i][i] != 0) {
                tensor[i]=-1;
                continue;
            }
            for (int j = 0; j < n; j++) {
                if (r[j][j] != 0) continue;
                tensor[i] += (r[i][j] > r[j][i]) ? 1 : 0;
            }
        }

        return tensor;
    }

    @Contract(pure = true)
    static int[] sDD(int[][] r){
        int n = r.length;
        int[] tensor = new int[n];

        for (int i = 0; i < n; i++) {
            if (r[i][i] != 0) {
                tensor[i]=-1;
                continue;
            }
            for (int j = 0; j < n; j++) {
                if (r[j][j] != 0) continue;
                tensor[i] += (r[i][j] < r[j][i]) ? 1 : 0;
            }
        }

        return tensor;
    }
    //endregion

    //region helpers
    public static int valueIn(int array[], int value){
        for (int i = 0; i < array.length; i++)
            if  (array[i]==value) return i;
        return -1;
    }

    static int[][] mClone(int[][] matrix){
        int [][] clone = new int[matrix.length][];
        for(int i = 0; i < matrix.length; i++)
            clone[i] = matrix[i].clone();
        return clone;
    }

    private HashSet<Integer> formIndexPoll(int[] ranking){
        HashSet<Integer> indexPoll = new HashSet<>();
        for (int j = 1; j<ranking.length+1; j++)
            indexPoll.add(j);
        for (int index : ranking)
            indexPoll.remove(index);
        return indexPoll;
    }
    //endregion

    //region transformer
        public static String transform(int[] ranking){
        String[] alts;
        if (BranchAndBound.max(ranking)<0|| BranchAndBound.max(ranking)<=ranking.length)
            alts = new String[ranking.length];
        else
            alts = new String[BranchAndBound.max(ranking)];
        for(int i = 0; i < ranking.length; i++) {
            if (ranking[i]>=0)
                if (alts[ranking[i]-1]==null) alts[ranking[i]-1] = "A" + (i+1) + ", ";
                else alts[ranking[i]-1] = alts[ranking[i]-1]
                        .substring(0, alts[ranking[i]-1].length() - 2)+"~A"+(i+1)+ ", ";
        }
        StringBuilder ret = new StringBuilder("(");
        for (String alt: alts) {
            if (alt!=null) ret.append(alt);
            else ret.append("_, ");
        }
        return ret.substring(0, ret.length() - 2)+")";
    }
    //endregion
}
