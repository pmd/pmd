/*
 * User: tom
 * Date: Oct 18, 2002
 * Time: 7:57:38 AM
 */
package net.sourceforge.pmd.util;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import net.sourceforge.pmd.RuleContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Iterator;

import org.jaxen.BaseXPath;
import org.jaxen.XPath;
import org.jaxen.JaxenException;

public class ASTViewer {

    private static class MyPrintStream extends PrintStream {

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

    private class XPathListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            StringReader sr = new StringReader(codeEditorPane.getText());
            JavaParser parser = new JavaParser(sr);
            try {
                if (xpathQueryField.getText().length() == 0) {
                    astArea.setText("XPath query field is empty");
                    xpathQueryField.requestFocus();
                    return;
                }
                XPath xpath = new BaseXPath(xpathQueryField.getText(), new DocumentNavigator());
                ASTCompilationUnit c = parser.CompilationUnit();
                StringBuffer sb = new StringBuffer();
                for (Iterator iter = xpath.selectNodes(c).iterator(); iter.hasNext();) {
                    SimpleNode node = (SimpleNode) iter.next();
                    String name = node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.')+1);
                    String line = " at line " + String.valueOf(node.getBeginLine());
                    sb.append(name).append(line).append(System.getProperty("line.separator"));
                }
                astArea.setText(sb.toString());
                if (sb.length() == 0) {
                    astArea.setText("No results returned " + System.currentTimeMillis());
                }
            } catch (ParseException pe) {
                astArea.setText(pe.fillInStackTrace().getMessage());
            } catch (JaxenException je) {
                astArea.setText(je.fillInStackTrace().getMessage());
            }
            xpathQueryField.requestFocus();
        }
    }

    private JTextPane codeEditorPane = new JTextPane();
    private JTextArea astArea = new JTextArea();
    private JFrame frame = new JFrame("AST Viewer");
    private JTextField xpathQueryField = new JTextField(40);

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

        JPanel controlPanel = new JPanel();
        controlPanel.add(xpathQueryField);
        JButton xPathButton = new JButton("Run XPath query");
        xPathButton.setMnemonic('r');
        xPathButton.addActionListener(new XPathListener());
        controlPanel.add(xPathButton);
        controlPanel.add(showButton);

        frame.getContentPane().setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codePanel, astPanel);
        frame.getContentPane().add(splitPane, BorderLayout.NORTH);
        frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);

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

        private GridBagConstraints constraints = new GridBagConstraints();

        public JSmartPanel() {
            super(new GridBagLayout());
        }

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
