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
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
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

    private JEditorPane codeEditorPane = new JEditorPane();
    private JTextArea astArea = new JTextArea();
    private JFrame frame = new JFrame("AST Viewer");

    public ASTViewer() {
        JPanel codePanel = new JPanel();
        codeEditorPane.setPreferredSize(new Dimension(500, 700));
        JScrollPane codeScrollPane = new JScrollPane(codeEditorPane);
        codePanel.add(codeScrollPane);

        JPanel astPanel = new JPanel();
        astArea.setRows(40);
        astArea.setColumns(40);
        JScrollPane astScrollPane = new JScrollPane(astArea);
        astPanel.add(astScrollPane);


        JButton showButton = new JButton("Show AST");
        showButton.setMnemonic('s');
        showButton.addActionListener(new ShowListener());

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(codePanel, BorderLayout.WEST);
        frame.getContentPane().add(astPanel, BorderLayout.EAST);
        frame.getContentPane().add(showButton, BorderLayout.SOUTH);

        frame.setSize(1000, 800);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.show();
    }

    public static void main(String[] args) {
        new ASTViewer();
    }
}
