/*
 * User: tom
 * Date: Oct 18, 2002
 * Time: 7:57:38 AM
 */
package net.sourceforge.pmd.util;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.StringReader;

public class ASTViewer {

    private class MyPrintStream extends PrintStream {

        public MyPrintStream() {
            super(System.out);
        }

        private StringBuffer buf = new StringBuffer();

        public void println(String s) {
            super.println(s);
            buf.append(s);
            buf.append(System.getProperty("line.separator"));
        }

        public String getString() {
            return buf.toString();
        }
    }

    private class ShowListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            StringReader sr = new StringReader(codeEditorPane.getText());
            JavaParser parser = new JavaParser(sr);
            MyPrintStream ps = new MyPrintStream();
            System.setOut(ps);
            try {
                ASTCompilationUnit c = parser.CompilationUnit();
                c.dump("");
                astArea.setText(ps.getString());
            } catch (ParseException pe) {
                astArea.setText(pe.fillInStackTrace().getMessage());
            }
            codeEditorPane.requestFocus();
        }
    }

    private JTextPane codeEditorPane = new JTextPane();
    private JTextArea astArea = new JTextArea();
    private JFrame frame = new JFrame("AST Viewer");

    public ASTViewer() {
        JSmartPanel codePanel = new JSmartPanel();
        JScrollPane codeScrollPane = new JScrollPane(codeEditorPane);
        codePanel.add(codeScrollPane, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0));

        JSmartPanel astPanel = new JSmartPanel();
        astArea.setRows(40);
        astArea.setColumns(40);
        JScrollPane astScrollPane = new JScrollPane(astArea);
        astPanel.add(astScrollPane, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0));

        JButton showButton = new JButton("Show AST");
        showButton.setMnemonic('s');
        showButton.addActionListener(new ShowListener());

        frame.getContentPane().setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codePanel, astPanel);
        frame.getContentPane().add(splitPane, BorderLayout.NORTH);
        frame.getContentPane().add(showButton, BorderLayout.SOUTH);

        frame.setSize(1000, 800);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.show();

        splitPane.setDividerLocation(splitPane.getMaximumDividerLocation() / 2);
    }

    public static void main(String[] args) {
        new ASTViewer();
    }

    public class JSmartPanel extends JPanel {

        private GridBagConstraints constraints;

        /**
         * Create a JPanel with a GridBagLayout layout
         *
         * @see java.awt.GridBagLayout
         */
        public JSmartPanel() {
            super(new GridBagLayout());
            constraints = new GridBagConstraints();
        }

        /**
         * Add a component to the layout
         *
         * @see java.awt.GridBagLayout
         * @see java.awt.GridBagConstraints
         */
        public void add(Component comp, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill, Insets insets) {
            constraints.gridx = gridx;
            constraints.gridy = gridy;
            constraints.gridwidth = gridwidth;
            constraints.gridheight = gridheight;
            constraints.weightx = weightx;
            constraints.weighty = weighty;
            constraints.anchor = anchor;
            constraints.fill = fill;
            constraints.insets = insets;

            add(comp, constraints);
        }

    }

}
