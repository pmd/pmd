package net.sourceforge.pmd.jbuilder;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import javax.swing.plaf.ProgressBarUI;
import java.io.File;
import net.sourceforge.pmd.cpd.CPDListener;
import net.sourceforge.pmd.cpd.CPD;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import net.sourceforge.pmd.cpd.Tile;

public class CPDDialog  extends JFrame implements CPDListener  {
    private CPD cpd;
    int progress = 0;
    boolean firstToken = true;
    boolean firstFile = true;
    boolean firstNewTile = true;
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
    }

    public void addedFile(int fileCount, File file) {
        if (firstFile) {
            firstFile = false;
            jLabel1.setText("Adding Files...");
            jProgressBar1.setString(file.getName());
            jProgressBar1.setMaximum(fileCount);
            progress = 0;
        }
        jProgressBar1.setValue(++progress);
    }


    public void addingTokens(int tokenSetCount, int doneSoFar, String tokenSrcID) {
        if (firstToken) {
            firstToken = false;
            jLabel1.setText("Adding Tokens...");
            jProgressBar1.setMaximum(tokenSetCount);
        }
        jProgressBar1.setValue(doneSoFar);
    }


    public void addedNewTile(Tile tile, int tilesSoFar, int totalTiles) {
        if (firstNewTile) {
            firstNewTile = false;
            jLabel1.setText("Adding Tiles... ");
            jProgressBar1.setMaximum(totalTiles);
        }
        if (jProgressBar1.getMaximum() != totalTiles)
            jProgressBar1.setMaximum(totalTiles);

        jProgressBar1.setValue(tilesSoFar);
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
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.getContentPane().add(jLabel1, null);
        this.getContentPane().add(jProgressBar1, null);
        this.setVisible(true) ;
        this.pack();
        this.setSize(new Dimension(400, 114));
        int xpos = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width)/2;
        int ypos = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height)/2;
        this.setLocation(xpos, ypos);
        this.show();
    }
}