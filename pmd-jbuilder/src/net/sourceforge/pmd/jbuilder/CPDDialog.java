package net.sourceforge.pmd.jbuilder;

import com.borland.jbcl.layout.VerticalFlowLayout;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.CPDListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

public class CPDDialog extends JFrame implements CPDListener, WindowListener {

    int progress;
    boolean firstToken = true;
    boolean firstFile = true;
    boolean firstNewTile = true;
    private VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
    private JLabel label = new JLabel();
    private JProgressBar progressBar = new JProgressBar();
    private boolean retCode = true;

    public CPDDialog(CPD cpd) {
        super("CPD Status Monitor");
        cpd.setCpdListener(this);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean update(String msg) {
        return retCode;
    }

    public void addedFile(int fileCount, File file) {
        if (firstFile) {
            firstFile = false;
            label.setText("Adding files");
            progressBar.setString(file.getName());
            progressBar.setMaximum(fileCount);
            progress = 0;
        }
        progressBar.setValue(++progress);
    }

    public void phaseUpdate(int i) {
        if (i == CPDListener.INIT) {
            label.setText("Initializing");
        } else if (i == CPDListener.HASH) {
            label.setText("Hashing");
        } else if (i == CPDListener.MATCH) {
            label.setText("Matching");
        } else if (i == CPDListener.GROUPING) {
            label.setText("Grouping");
        } else if (i == CPDListener.DONE) {
            label.setText("Done");
        }
    }

    public boolean wasCancelled() {
        return !retCode;
    }

    public void close() {
        this.dispose();
    }

    private void jbInit() throws Exception {
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText("CPD Status");
        this.getContentPane().setLayout(verticalFlowLayout1);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.getContentPane().add(label, null);
        this.getContentPane().add(progressBar, null);
        this.setVisible(true);
        this.pack();
        this.setSize(new Dimension(400, 114));
        int xpos = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int ypos = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;
        this.setLocation(xpos, ypos);
        this.addWindowListener(this);
        this.show();
    }

    public void windowClosing(WindowEvent e) {
        retCode = false;
    }

    public void windowOpened(WindowEvent e) {   }
    public void windowClosed(WindowEvent e) {   }
    public void windowIconified(WindowEvent e) {   }
    public void windowDeiconified(WindowEvent e) {   }
    public void windowActivated(WindowEvent e) {   }
    public void windowDeactivated(WindowEvent e) {   }
}