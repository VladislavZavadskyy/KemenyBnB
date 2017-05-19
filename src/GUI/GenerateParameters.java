package GUI;

import File.FileIO;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Vladislav Zavadskyy on 16/05/2017.
 * Hour 19 of so called "Tuesday"
 * Day 16 of cycle 2017 since alleged birth of alleged son of God
 * Subcycle be of 05
 */
class GenerateParameters extends JDialog {

    GenerateParameters(JFrame owner){
        super(owner, "Set generation parameters");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel expLabel = new JLabel("Experts");
        JTextField expTF = new JTextField();
        JLabel altLabel = new JLabel("Alternatives");
        JTextField altTF = new JTextField();
        JLabel batchLabel = new JLabel("Batches");
        JTextField batchTF = new JTextField();

        JButton cancel = new JButton("Cancel");
        JButton generate = new JButton("Generate");

        this.getRootPane().setDefaultButton(generate);
        this.setResizable(false);

        cancel.addActionListener( e -> this.dispose());

        generate.addActionListener(e -> {
            JTextField cur = expTF;
            int m, n, batch;
            try {
                cur.setBackground(Color.WHITE);
                m = Integer.parseInt(cur.getText());
                if (m<=0) throw new Exception();
                cur = altTF;
                cur.setBackground(Color.WHITE);
                n = Integer.parseInt(cur.getText());
                if (n<=0) throw new Exception();
                cur = batchTF;
                cur.setBackground(Color.WHITE);
                batch = Integer.parseInt(cur.getText());
                if (batch<=0) throw new Exception();

                FileIO.randomGen(n,m,batch);
                this.dispose();
            } catch (Exception exc) {
                cur.setBackground(Color.PINK);
            }
        });

        //region layout
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(expLabel)
                                    .addComponent(expTF))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(altLabel)
                                        .addComponent(altTF))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(batchLabel)
                                        .addComponent(batchTF)))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(cancel)
                                .addComponent(generate))

        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(expLabel)
                                .addComponent(altLabel)
                                .addComponent(batchLabel))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(expTF)
                                .addComponent(altTF)
                                .addComponent(batchTF))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(cancel)
                                .addComponent(generate))
        );
        //endregion

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
