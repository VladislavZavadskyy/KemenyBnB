package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by Vladislav Zavadskyy on 18/05/2017.
 * Hour 15 of so called "Thursday"
 * Day 18 of cycle 2017 since alleged birth of alleged son of God
 * Subcycle be of 05
 */
class RunParameters extends JDialog  {

    RunParameters(JFrame owner, Properties props){
        super(owner, "Run parameters");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel outputParamLabel = new JLabel("Output parameters");
        JCheckBox writeHeadCheck = new JCheckBox("Write header");
        JCheckBox writeDetCheck = new JCheckBox("Write details");

        JLabel runParamLabel = new JLabel("Branch and Bound");
        JCheckBox runBnBCheck = new JCheckBox("Run BnB");
        JComboBox<String> bnbMode = new JComboBox<>(new String[]{
                "R evaluation", "Distance evaluation"
        });
        JCheckBox pruneCheck = new JCheckBox("Apply pruning");
        JLabel bruteLabel = new JLabel("Brute force");
        JCheckBox includeBruteCheck = new JCheckBox("Run brute force");
        JComboBox<String> bruteMode = new JComboBox<>(new String[]{
                "R evaluation", "Distance evaluation"
        });
        bruteMode.setEnabled(false);
        pruneCheck.setEnabled(false);

        JLabel filenameLabel = new JLabel("Log file name");
        JTextField filenameTF = new JTextField();

        JButton cancelButton = new JButton("Cancel");
        JButton applyButton = new JButton("Apply");

        //region groups enablers
        runBnBCheck.addActionListener(e -> {
            if (runBnBCheck.isSelected()){
                if (bnbMode.getSelectedIndex()==0)
                    pruneCheck.setEnabled(true);
                writeDetCheck.setEnabled(true);
                bnbMode.setEnabled(true);
            } else {
                pruneCheck.setEnabled(false);
                writeDetCheck.setEnabled(false);
                bnbMode.setEnabled(false);
            }
        });

        bnbMode.addItemListener(e->{
            if(e.getItem()=="R evaluation")
                pruneCheck.setEnabled(true);
            else pruneCheck.setEnabled(false);
        });

        includeBruteCheck.addActionListener(e -> {
            if (includeBruteCheck.isSelected())
                bruteMode.setEnabled(true);
            else
                bruteMode.setEnabled(false);
        });
        //endregion

        //region initialize inputs
        if (strToBool(props.getProperty("run.brute")))
            includeBruteCheck.doClick();
        if (strToBool(props.getProperty("run.bnb")))
            runBnBCheck.doClick();
        if (Objects.equals(props.getProperty("mode.bnb"), "r"))
            bnbMode.setSelectedIndex(0);
        else bnbMode.setSelectedIndex(1);
        pruneCheck.setSelected(strToBool(props.getProperty("apply.prune")));
        writeDetCheck.setSelected(strToBool(props.getProperty("write.det")));
        writeHeadCheck.setSelected(strToBool(props.getProperty("write.head")));
        if (Objects.equals(props.getProperty("mode.brute"), "r"))
            bruteMode.setSelectedIndex(0);
        else bruteMode.setSelectedIndex(1);
        filenameTF.setText(props.getProperty("log.file.name"));
        //endregion

        //region apply properties
        getRootPane().setDefaultButton(applyButton);
        applyButton.addActionListener(e->{
            props.setProperty("run.brute",boolToStr(includeBruteCheck.isSelected()));
            if(bruteMode.getSelectedIndex()==0)
                props.setProperty("mode.brute","r");
            else props.setProperty("mode.brute","d");

            if(bnbMode.getSelectedIndex()==0)
                props.setProperty("mode.bnb","r");
            else props.setProperty("mode.bnb","d");

            props.setProperty("run.bnb",boolToStr(runBnBCheck.isSelected()));
            props.setProperty("apply.prune",boolToStr(pruneCheck.isSelected()));
            props.setProperty("write.head",boolToStr(writeHeadCheck.isSelected()));
            props.setProperty("write.det",boolToStr(writeDetCheck.isSelected()));
            try{
                filenameTF.setBackground(Color.white);
                File f = new File(filenameTF.getText());
                f.getCanonicalPath();
                props.setProperty("log.file.name",filenameTF.getText());
                props.store(new BufferedWriter(new FileWriter("your.preferences")),"Your last run's preferences");
                this.dispose();
            } catch (IOException exc){
                filenameTF.setBackground(Color.pink);
            }

        });
        cancelButton.addActionListener(e->this.dispose());
        //endregion

        //region layout
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(runParamLabel)
                                .addComponent(runBnBCheck)
                                .addComponent(bnbMode)
                                .addComponent(pruneCheck)
                                .addComponent(outputParamLabel)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(writeHeadCheck)
                                        .addComponent(writeDetCheck))
                                .addComponent(bruteLabel)
                                .addComponent(includeBruteCheck)
                                .addComponent(bruteMode)
                                .addComponent(filenameLabel)
                                .addComponent(filenameTF)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(cancelButton)
                                        .addComponent(applyButton)))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(runParamLabel)
                        .addComponent(runBnBCheck)
                        .addComponent(bnbMode)
                        .addComponent(pruneCheck)
                        .addComponent(outputParamLabel)
                        .addComponent(writeHeadCheck)
                        .addComponent(writeDetCheck)
                        .addComponent(bruteLabel)
                        .addComponent(includeBruteCheck)
                        .addComponent(bruteMode)
                        .addComponent(filenameLabel)
                        .addComponent(filenameTF)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(applyButton))
        );
        //endregion

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private String boolToStr(boolean bool){
        if (bool) return "t";
        else return "f";
    }

    private boolean strToBool(String s){
        return Objects.equals(s, "t");
    }
}
