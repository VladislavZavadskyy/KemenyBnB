package GUI;

import Algorithms.BranchAndBound;
import Algorithms.Brute;
import File.FileIO;
import Structures.DistanceNode;
import Structures.RNode;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by Vladislav Zavadskyy on 10/05/2017.
 * Hour 20 of so called "Wednesday"
 * Day 10 of cycle 2017 since alleged birth of alleged son of God
 * Subcycle be of 05
 */
public class FrameMain extends JFrame {

    private JPanel medianDisplay;
    private JTextField[][] fields;
    private final JFileChooser fc = new JFileChooser();

    private FrameMain(Properties props){
        super("Kemeny Median: Branch and Bound method");
        JPanel rootPanel = new JPanel();
        setContentPane(rootPanel);
        setLayout(new BoxLayout(rootPanel, BoxLayout.X_AXIS));
        setResizable(false);
        BorderLayout parametersLayout = new BorderLayout();
        JPanel parameters = new JPanel(parametersLayout);
        add(parameters);

        add(medianDisplay = new JPanel());
        //region look and feel
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //endregion

        //region menu
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        JMenuItem generateItem = new JMenuItem("Generate");
        generateItem.setAccelerator(KeyStroke.getKeyStroke('G', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        fileMenu.add(loadItem);
        fileMenu.add(generateItem);
        menuBar.add(fileMenu);

        JMenu miscMenu = new JMenu("Misc");
        JMenuItem paramItem = new JMenuItem("Parameters");
        paramItem.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        JMenuItem licItem = new JMenuItem("License");
        miscMenu.add(paramItem);
        miscMenu.add(licItem);
        menuBar.add(miscMenu);

        this.setJMenuBar(menuBar);
        //endregion
        //region misc menu listeners
        licItem.addActionListener(e -> JOptionPane.showMessageDialog(null,
                "This work is licensed under MIT License\n" +
                        "© 2017 Vladislav Zavadskyy"));
        paramItem.addActionListener(e -> new RunParameters(this, props));
        //endregion

        //region bottom control group
        JPanel controls = new JPanel();
        controls.setBorder(new EmptyBorder(10,5,10,5));
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
        parameters.add(controls, BorderLayout.PAGE_START);
        parameters.add(new JPanel(), BorderLayout.CENTER);

        JButton calculateButton = new JButton("Calculate");
        //endregion
        //region bottom control group listeners
        calculateButton.addActionListener(e -> {
            int m = fields.length, n = fields[0].length;
            int[][] rankings = new int[m][n];
            medianDisplay.removeAll();

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    try {
                        fields[i][j].setBackground(Color.WHITE);
                        int cur = Integer.parseInt(fields[i][j].getText());
                        if (cur>0) rankings[i][j] = cur;
                        else throw new Exception();
                    } catch (Exception exc) {
                        fields[i][j].setBackground(Color.PINK);
                        return;
                    }
                }
            }
            FileIO out = new FileIO(props.getProperty("log.file.name"));
            //region write header
            if (Objects.equals(props.getProperty("write.head"), "t")) {
                out.write("\r\n_____________________________________\r\n");
                out.write("Rankings input:\r\n");
                for (int[] ranking : rankings)
                    out.write(DistanceNode.transform(ranking) + "\r\n");
            }
            //endregion
            int[] bnbRes = new int[0];
            int[] bruteRes = new int[0];
            try {
            if(Objects.equals(props.getProperty("run.bnb"), "t"))
                if(Objects.equals(props.getProperty("mode.bnb"), "r"))
                    bnbRes = new BranchAndBound<>(rankings,props,out,RNode.class).run();
                else bnbRes = new BranchAndBound<>(rankings,props,out,DistanceNode.class).run();
            if(Objects.equals(props.getProperty("run.brute"), "t"))
                if(Objects.equals(props.getProperty("mode.brute"), "r"))
                    bruteRes = new Brute<>(rankings,out, RNode.class).force();
                else bruteRes = new Brute<>(rankings,out,DistanceNode.class).force();

            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
            if (bnbRes.length!=0)
                medianDisplay.add(displayMedian(bnbRes));
            else medianDisplay.add(displayMedian(bruteRes));
            out.close();
            pack(); repaint();
        });
        //endregion

        //region top control group
        JLabel expLabel = new JLabel("Experts:");
        margin10(expLabel);
        controls.add(expLabel);
        JTextField mField = new JTextField();
        mField.setMaximumSize(new Dimension(60,25));
        mField.setPreferredSize(new Dimension(40,25));
        controls.add(mField);

        JLabel altLabel = new JLabel("Alternatives:");
        margin10(altLabel);
        controls.add(altLabel);
        JTextField nField = new JTextField();
        nField.setMaximumSize(new Dimension(60,25));
        nField.setPreferredSize(new Dimension(40,25));
        controls.add(nField);

        JButton createButton = new JButton("Create");
        createButton.setBorder(new EmptyBorder(6,10,6,10));
        controls.add(createButton);
        //endregion
        //region top control group listeners
        createButton.addActionListener(e -> {
            if (parametersLayout.getLayoutComponent(BorderLayout.CENTER)!=null)
                parameters.remove(parametersLayout.getLayoutComponent(BorderLayout.CENTER));
            JTextField cur = mField;
            int m,n;
            try {
                cur.setBackground(Color.WHITE);
                m = Integer.parseInt(mField.getText());
                if (m<=0) throw new Exception();
                cur = nField;
                cur.setBackground(Color.WHITE);
                n = Integer.parseInt(nField.getText());
                if (n<=0) throw new Exception();
                parameters.add(createTable(n,m));
                parameters.add(calculateButton, BorderLayout.PAGE_END);
                pack(); repaint();
            } catch (Exception exc) {
                cur.setBackground(Color.PINK);
            }
        });
        //endregion

        //region file menu
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        loadItem.addActionListener( e -> {
            fc.setApproveButtonText("Load");
            int returnVal = fc.showOpenDialog(rootPanel);

            if (returnVal == JFileChooser.APPROVE_OPTION)
                FileIO.readAndRun(fc.getSelectedFile(), props);
        });
        generateItem.addActionListener(e -> new GenerateParameters(this));
        //endregion

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private JPanel createTable(int n, int m){
        JPanel panel = new JPanel();
        fields = new JTextField[m][n];
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        for (int i = 0; i < m; i++){
            JPanel expert = new JPanel();
            expert.setBorder(new EmptyBorder(5,5,5,5));
            expert.setLayout(new BoxLayout(expert, BoxLayout.Y_AXIS));
            expert.add(new JLabel("Expert "+Integer.toString(i+1)));
            for (int j = 0; j < n; j++){
                fields[i][j] = new JTextField();
                fields[i][j].setMaximumSize(new Dimension(100,20));
                fields[i][j].setHorizontalAlignment(JTextField.CENTER);
                expert.add(fields[i][j]);
            }
            panel.add(expert);
        }

        return panel;
    }

    private JPanel displayMedian(int[] ranking){
        JPanel median = new JPanel();
        if (ranking.length==0) {
            JOptionPane.showMessageDialog(null,
                    "You should select either Branch and Bound or Algorithms.Brute force\n" +
                            "in Misc->Parameters in order to see a result");
            return median;
        } else {
        median.setBorder(new EmptyBorder(47,10,5,10));
        median.setLayout(new BoxLayout(median, BoxLayout.Y_AXIS));
        int n = ranking.length;
        String[] tRanking = transform(ranking);
        median.add(new JLabel("The median"));
        for (int j = 0; j < n; j++){
            JTextField tf = new JTextField(tRanking[j]);
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setEditable(false);
            tf.setBackground(Color.white);
            median.add(tf);
        }
        return median;
        }
    }

    private static void margin10(JComponent c){
        Border border = c.getBorder();
        Border margin = new EmptyBorder(0, 10, 0, 10);
        c.setBorder(new CompoundBorder(border, margin));
    }

    private static String[] transform(int[] ranking){
        String[] alts = new String[ranking.length];
        for(int i = 0; i < ranking.length; i++)
            if (ranking[i]>=0) alts[ranking[i]-1] = "A" + (i+1);
        for (int i = 0; i < alts.length; i++)
            if (alts[i] == null) alts[i]="None";
        return alts;
    }

    public static void main(String[] args) {
        Properties defaults = new Properties();
        defaults.setProperty("run.brute","f");
        defaults.setProperty("mode.brute","d");
        defaults.setProperty("run.bnb","t");
        defaults.setProperty("mode.bnb","d");
        defaults.setProperty("apply.prune","f");
        defaults.setProperty("write.head","t");
        defaults.setProperty("write.det","f");
        defaults.setProperty("log.file.name", "log");
        try {
            defaults.load(new BufferedReader(new FileReader("your.preferences")));
        } catch (Exception ignored) {}
        new FrameMain(defaults);
    }
}
