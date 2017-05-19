package File;

import Algorithms.BranchAndBound;
import Algorithms.Brute;
import Structures.DistanceNode;
import Structures.RNode;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

/**
 * Created by Vladislav Zavadskyy on 11/05/2017.
 * Hour 15 of so called "Thursday"
 * Day 11 of cycle 2017 since alleged birth of alleged son of God
 * Subcycle be of 05
 */
public class FileIO {

    private FileWriter f = null;
    private static final Random rnd = new Random();

    //region constructor and elementary ops
    public FileIO(String name){
        if(!name.endsWith(".txt"))
            name+=".txt";
        try {
            f = new FileWriter(name, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        if (f!=null)
            try {
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void write(String s){
        try {
            f.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

    public static void randomGen(int n, int m, int batches){
        try {
            FileWriter f = new FileWriter("A"+n+" E"+m+" B"+batches+".txt");
            for (int b = 0; b < batches; b++) {
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        f.write(Integer.toString(rnd.nextInt(n)+1) + " ");
                    }
                    f.write("\r\n");
                }
                f.write("\r\n");
            }
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readAndRun(File f, Properties props){
        try{
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            String[] nums;
            ArrayList<Integer> cur;
            FileIO out = new FileIO(props.getProperty("log.file.name"));
            while ((line = br.readLine()) != null)   {
                while (Objects.equals(line, "")){
                    line = br.readLine();
                }
                if (line == null){
                    in.close();
                    JOptionPane.showMessageDialog(null, "Data read and processed successfully, check log.");
                    return;
                }
                ArrayList<ArrayList<Integer>> list = new ArrayList<>();
                while (!Objects.equals(line, "")&&line!=null){
                    nums = line.split(" ");
                    cur = new ArrayList<>();
                    for (String num: nums){
                        cur.add(Integer.parseInt(num));
                    }
                    list.add(cur);
                    line = br.readLine();
                }
                int m = list.size(), n = list.get(0).size();
                int[][] rankings = new int[m][n];
                for (int i = 0, listSize = list.size(); i < listSize; i++) {
                    ArrayList<Integer> row = list.get(i);
                    for (int i1 = 0, rowSize = row.size(); i1 < rowSize; i1++) {
                        Integer a = row.get(i1);
                        rankings[i][i1] = a;
                    }
                }
                //region write header
                if (Objects.equals(props.getProperty("write.head"), "t")) {
                    out.write("\r\n_____________________________________\r\n");
                    out.write("Rankings input:\r\n");
                    for (int[] ranking : rankings)
                        out.write(DistanceNode.transform(ranking) + "\r\n");
                }
                //endregion
                if(Objects.equals(props.getProperty("run.brute"), "t"))
                    if(Objects.equals(props.getProperty("mode.brute"), "r"))
                        new Brute<>(rankings,out,RNode.class).force();
                    else new Brute<>(rankings,out,DistanceNode.class).force();
                if(Objects.equals(props.getProperty("run.bnb"), "t"))
                    if(Objects.equals(props.getProperty("mode.bnb"), "r"))
                        new BranchAndBound<>(rankings,props,out,RNode.class).run();
                    else new BranchAndBound<>(rankings,props,out,DistanceNode.class).run();
            }
            out.write("\r\n");
            out.close();
            in.close();
            JOptionPane.showMessageDialog(null, "Data read and processed successfully, check log.");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
