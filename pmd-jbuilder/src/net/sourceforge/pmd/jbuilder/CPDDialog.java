package net.sourceforge.pmd.jbuilder;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import javax.swing.plaf.ProgressBarUI;
import java.io.File;
import net.sourceforge.pmd.cpd.CPDListener;
import net.sourceforge.pmd.cpd.CPD;

public class CPDDialog  extends JFrame implements CPDListener  {
    private CPD cpd;
    int progress = 0;
    boolean firstUpdate = true;
    boolean firstToken = true;
    boolean firstFile = true;
    boolean firstExpansion = true;
    private static final int PROG_MAX = 100;
    private VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    private JLabel jLabel1 = new JLabel();
    private JProgressBar jProgressBar1 = new JProgressBar();

    public CPDDialog(CPD cpd) {
        super("CPD Status Monitor");
        this.cpd = cpd;
        cpd.setListener(this);
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void update(String msg) {
        if (firstUpdate) {
            firstUpdate = false;
            jLabel1.setText("Updating...");
            jProgressBar1.setString(msg);
        }
        jProgressBar1.setValue(progress++ % PROG_MAX);
    }

    public void addedFile(int fileCount, File file) {
        if (firstFile) {
            firstFile = false;
            jLabel1.setText("Adding Files...");
            jProgressBar1.setString(file.getName());
        }
        jProgressBar1.setValue(progress++ % PROG_MAX);
    }


    public void addingTokens(int tokenSetCount, int doneSoFar, String tokenSrcID) {
        if (firstToken) {
            firstToken = false;
            jLabel1.setText("Adding Tokens...");
            jProgressBar1.setMaximum(tokenSetCount);
            progress = 0;
        }
        jProgressBar1.setValue(progress++);
    }

    public void expandingTile(String tileImage) {
        if (firstExpansion) {
            firstExpansion = false;
            jLabel1.setText("Expanding Tokens...");
            jProgressBar1.setMaximum(PROG_MAX);
            progress = 0;
        }
        jProgressBar1.setValue(progress++ % PROG_MAX);
    }

    public void close() {
        this.dispose();
    }

    public CPDDialog() {
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void jbInit() throws Exception {
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("CPD Status");
        this.getContentPane().setLayout(verticalFlowLayout1);
        this.getContentPane().add(jLabel1, null);
        this.getContentPane().add(jProgressBar1, null);
        this.setSize(400,400);
        this.setVisible(true) ;
        this.show();
    }
}