/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.util;

import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Vector;

public class ASTViewer {

    private static class JSmartPanel extends JPanel {

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
            JavaParser parser = (new TargetJDK1_4()).createParser(sr);
            MyPrintStream ps = new MyPrintStream();
            System.setOut(ps);
            try {
                ASTCompilationUnit c = parser.CompilationUnit();
                c.dump("");
                astArea.setText(ps.getString());
            } catch (ParseException pe) {
                astArea.setText(pe.fillInStackTrace().getMessage());
            }
        }
    }

    private class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            FileWriter fw = null;
            try {
                File f = new File(SETTINGS_FILE_NAME);
                fw = new FileWriter(f);
                fw.write(codeEditorPane.getText());
            } catch (IOException ioe) {
            } finally {
            	try {
	            	if (fw != null)
	            		fw.close();
	            } catch (IOException ioe) {
	            }
            }
        }
    }

    private class XPathListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            xpathResults.clear();
            if (xpathQueryArea.getText().length() == 0) {
                xpathResults.addElement("XPath query field is empty");
                xpathResultPanel.repaint();
                codeEditorPane.requestFocus();
                return;
            }
            StringReader sr = new StringReader(codeEditorPane.getText());
            JavaParser parser = (new TargetJDK1_4()).createParser(sr);
            try {
                XPath xpath = new BaseXPath(xpathQueryArea.getText(), new DocumentNavigator());
                ASTCompilationUnit c = parser.CompilationUnit();
                for (Iterator iter = xpath.selectNodes(c).iterator(); iter.hasNext();) {
                    StringBuffer sb = new StringBuffer();
                    SimpleNode node = (SimpleNode) iter.next();
                    String name = node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.')+1);
                    String line = " at line " + String.valueOf(node.getBeginLine());
                    sb.append(name).append(line).append(System.getProperty("line.separator"));
                    xpathResults.addElement(sb.toString().trim());
                }
                if (xpathResults.isEmpty()) {
                    xpathResults.addElement("No matching nodes " + System.currentTimeMillis());
                }
            } catch (ParseException pe) {
                xpathResults.addElement(pe.fillInStackTrace().getMessage());
            } catch (JaxenException je) {
                xpathResults.addElement(je.fillInStackTrace().getMessage());
            }
            xpathResultPanel.repaint();
            xpathQueryArea.requestFocus();
        }
    }

    private static final String SETTINGS_FILE_NAME = System.getProperty("user.home") + System.getProperty("file.separator") + ".pmd_astviewer";

    private JTextPane codeEditorPane = new JTextPane();
    private JTextArea astArea = new JTextArea();
    private DefaultListModel xpathResults = new DefaultListModel();
    private JList xpathResultList = new JList(xpathResults);
    private JTextArea xpathQueryArea = new JTextArea(10, 30);
    private JFrame frame = new JFrame("AST Viewer");
    private JPanel xpathResultPanel;

    public ASTViewer() {
        JPanel controlPanel = new JPanel();
        codeEditorPane.setPreferredSize(new Dimension(400,200));
        controlPanel.add(createCodeEditPanel());
        controlPanel.add(createXPathQueryPanel());

        JSmartPanel astPanel = new JSmartPanel();
        astArea.setRows(20);
        astArea.setColumns(20);
        JScrollPane astScrollPane = new JScrollPane(astArea);
        astPanel.add(astScrollPane, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0));

        xpathResultPanel = new JPanel();
        xpathResults.addElement("No results yet");
        xpathResultList.setBorder(BorderFactory.createLineBorder(Color.black));
        xpathResultList.setFixedCellWidth(300);
        JScrollPane xpathResultListScrollPane = new JScrollPane();
        xpathResultListScrollPane.getViewport().setView(xpathResultList);
        xpathResultPanel.add(xpathResultListScrollPane);

        JSplitPane resultsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, astPanel, xpathResultPanel);
        JSplitPane containerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlPanel, resultsSplitPane);

        frame.getContentPane().add(containerSplitPane);

        frame.setSize(1000, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        frame.setLocation((screenWidth/2) - frame.getWidth()/2, (screenHeight/2) - frame.getHeight()/2);
        frame.setVisible(true);
        frame.show();

        containerSplitPane.setDividerLocation(containerSplitPane.getMaximumDividerLocation() - (containerSplitPane.getMaximumDividerLocation()/2));
        resultsSplitPane.setDividerLocation(resultsSplitPane.getMaximumDividerLocation() - (resultsSplitPane.getMaximumDividerLocation()/2));
        codeEditorPane.setText(loadText());
    }

    private JSmartPanel createCodeEditPanel() {
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        JSmartPanel panel = new JSmartPanel();
        JScrollPane codeScrollPane = new JScrollPane(codeEditorPane);
        panel.add(codeScrollPane, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0));
        return panel;
    }

    private JPanel createXPathQueryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("XPath Query (if any)"), BorderLayout.NORTH);
        xpathQueryArea.setBorder(BorderFactory.createLineBorder(Color.black));
        JScrollPane jScrollPane = new JScrollPane(xpathQueryArea);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(jScrollPane, BorderLayout.CENTER);
        JButton goButton = new JButton("Go");
        goButton.setMnemonic('g');
        goButton.addActionListener(new ShowListener());
        goButton.addActionListener(new SaveListener());
        goButton.addActionListener(new XPathListener());
        panel.add(goButton, BorderLayout.SOUTH);
        return panel;
    }

    private String loadText() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(SETTINGS_FILE_NAME)));
            StringBuffer text = new StringBuffer();
            String hold;
            while ( (hold = br.readLine()) != null) {
                text.append(hold);
                text.append(System.getProperty("line.separator"));
            }
            return text.toString();
        }   catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
        	try {
	        	if (br != null)
	        		br.close();
	        } catch (IOException e) {
            	e.printStackTrace();
	        }
        }
    }

    public static void main(String[] args) {
        new ASTViewer();
    }
}
