/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.util;

import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
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
import java.util.*;
import java.util.List;

public class ASTViewer {

    public static class DFAGraphRule extends AbstractRule {

        private List methods;
        private List constructors;

        public DFAGraphRule() {
            super.setUsesDFA();
            super.setUsesSymbolTable();
        }

        public List getMethods() {
            return this.methods;
        }

        public List getConstructors() {
            return this.constructors;
        }

        public Object visit(ASTCompilationUnit acu, Object data) {
            methods = acu.findChildrenOfType(ASTMethodDeclaration.class);
            constructors = acu.findChildrenOfType(ASTMethodDeclaration.class);
            return data;
        }
    }

    public static class DFAPanel extends JPanel  {

        private SimpleNode node;
        private int x = 150;
        private int y = 50;
        private int radius = 10;
        private int d = 2 * radius;
        private int height;
        private HasLines lines;

        public DFAPanel(SimpleNode node, HasLines lines) {
            super();
            if (node == null) {
                return;
            }
            this.lines = lines;
            this.node = node;
        }

        public void resetTo(SimpleNode node, HasLines lines) {
            this.lines = lines;
            this.node = node;
        }

        public void paint(Graphics g) {
            super.paint(g);
            if (node == null) {
                return;
            }
            java.util.List flow = node.getDataFlowNode().getFlow();
            for (int i = 0; i < flow.size(); i++) {
                IDataFlowNode inode = (IDataFlowNode) flow.get(i);

                y = this.computeDrawPos(inode.getIndex());

                g.drawArc(x, y, d, d, 0, 360);
                if (height < y) height = y;

                g.drawString(lines.getLine(inode.getLine()), x + 200, y + 15);
                g.drawString(String.valueOf(inode.getIndex()), x + radius-2, y + radius+4);

                String exp = "";
                java.util.List access = inode.getVariableAccess();
                if (access != null) {
                    for (int k = 0; k < access.size(); k++) {
                        VariableAccess va = (VariableAccess) access.get(k);
                        switch (va.getAccessType()) {
                            case VariableAccess.DEFINITION:
                                exp += "d(";
                                break;
                            case VariableAccess.REFERENCING:
                                exp += "r(";
                                break;
                            case VariableAccess.UNDEFINITION:
                                exp += "u(";
                                break;
                            default:
                                exp += "?(";
                        }
                        exp += va.getVariableName() + "), ";
                    }
                    g.drawString(exp, x + 70, y + 15);
                }

                for (int j = 0; j < inode.getChildren().size(); j++) {
                    IDataFlowNode n = (IDataFlowNode) inode.getChildren().get(j);
                    this.drawMyLine(inode.getIndex(), n.getIndex(), g);
                    String output = (j==0 ? "" : "," ) + String.valueOf(n.getIndex());
                    g.drawString(output, x - 3 * d + (j * 20), y + radius - 2);
                }
            }
        }

        private int computeDrawPos(int index) {
            int z = radius * 4;
            return z + index * z;
        }

        private void drawMyLine(int index1, int index2, Graphics g) {
            int y1 = this.computeDrawPos(index1);
            int y2 = this.computeDrawPos(index2);

            int arrow = 3;

            if (index1 < index2) {
                if (index2 - index1 == 1) {
                    x += radius;
                    g.drawLine(x, y1 + d, x, y2);
                    g.fillRect(x - arrow, y2 - arrow, arrow * 2, arrow * 2);
                    x -= radius;
                } else if (index2 - index1 > 1) {
                    y1 = y1 + radius;
                    y2 = y2 + radius;
                    int n = ((index2 - index1 - 2) * 10) + 10;
                    g.drawLine(x, y1, x - n, y1);
                    g.drawLine(x - n, y1, x - n, y2);
                    g.drawLine(x - n, y2, x, y2);
                    g.fillRect(x - arrow, y2 - arrow, arrow * 2, arrow * 2);
                }

            } else {
                if (index1 - index2 > 1) {
                    y1 = y1 + radius;
                    y2 = y2 + radius;
                    x = x + this.d;
                    int n = ((index1 - index2 - 2) * 10) + 10;
                    g.drawLine(x, y1, x + n, y1);
                    g.drawLine(x + n, y1, x + n, y2);
                    g.drawLine(x + n, y2, x, y2);
                    g.fillRect(x - arrow, y2 - arrow, arrow * 2, arrow * 2);
                    x = x - this.d;
                } else if (index1 - index2 == 1) {
                    y2 = y2 + this.d;
                    g.drawLine(x + radius, y2, x + radius, y1);
                    g.fillRect(x + radius - arrow, y2 - arrow, arrow * 2, arrow * 2);
                }
            }
        }
    }

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

    private class DFAListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            try {
                DFAGraphRule dfaGraphRule = new DFAGraphRule();
                RuleSet rs = new RuleSet();
                rs.addRule(dfaGraphRule);
                RuleContext ctx = new RuleContext();
                ctx.setSourceCodeFilename("");
                new PMD().processFile(new StringReader(codeEditorPane.getText()), rs, ctx);
                dfaPanel.resetTo((ASTMethodDeclaration)dfaGraphRule.getMethods().get(0), codeEditorPane);
                dfaPanel.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class XPathListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            xpathResults.clear();
            if (xpathQueryArea.getText().length() == 0) {
                xpathResults.addElement("XPath query field is empty");
                xpathResultList.repaint();
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
            xpathResultList.repaint();
            xpathQueryArea.requestFocus();
        }
    }

    private static class CodeEditorTextPane extends JTextPane implements HasLines, ActionListener {
        public CodeEditorTextPane() {
            setPreferredSize(new Dimension(400,200));
            setText(loadCode());
        }
        public String getLine(int number) {
            int count = 1;
            for (StringTokenizer st = new StringTokenizer(getText(), "\n"); st.hasMoreTokens();) {
                String tok = st.nextToken();
                if (count == number) {
                    return tok;
                }
                count++;
            }
            throw new RuntimeException("Line number " + number + " not found");
        }
        public void actionPerformed(ActionEvent ae) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(new File(SETTINGS_FILE_NAME));
                fw.write(getText());
            } catch (IOException ioe) {
            } finally {
            	try {
	            	if (fw != null)
	            		fw.close();
	            } catch (IOException ioe) {
	            }
            }
        }
        private String loadCode() {
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
    }

    private static final String SETTINGS_FILE_NAME = System.getProperty("user.home") + System.getProperty("file.separator") + ".pmd_astviewer";

    private CodeEditorTextPane codeEditorPane;
    private JTextArea astArea = new JTextArea();
    private DefaultListModel xpathResults = new DefaultListModel();
    private JList xpathResultList = new JList(xpathResults);
    private JTextArea xpathQueryArea = new JTextArea(10, 30);
    private JFrame frame = new JFrame("AST Viewer");
    private DFAPanel dfaPanel;

    public ASTViewer() {
        JPanel controlPanel = new JPanel();
        controlPanel.add(createCodeEditPanel());
        controlPanel.add(createXPathQueryPanel());

        JSmartPanel astPanel = createASTPanel();
        JPanel xpathResultPanel = createXPathResultPanel();

        JSplitPane resultsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, astPanel, xpathResultPanel);

        dfaPanel = new DFAPanel(null, codeEditorPane);

        JTabbedPane tabbed = new JTabbedPane();
        tabbed.addTab("Abstract Syntax Tree / XPath", resultsSplitPane);
        tabbed.addTab("Data Flow Analysis", dfaPanel);

        JSplitPane containerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlPanel, tabbed);

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
    }

    private JSmartPanel createASTPanel() {
        JSmartPanel astPanel = new JSmartPanel();
        astArea.setRows(20);
        astArea.setColumns(20);
        JScrollPane astScrollPane = new JScrollPane(astArea);
        astPanel.add(astScrollPane, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0));
        return astPanel;
    }

    private JPanel createXPathResultPanel() {
        JPanel p = new JPanel();
        xpathResults.addElement("No results yet");
        xpathResultList.setBorder(BorderFactory.createLineBorder(Color.black));
        xpathResultList.setFixedCellWidth(300);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(xpathResultList);
        p.add(scrollPane);
        return p;
    }

    private JSmartPanel createCodeEditPanel() {
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        JSmartPanel p = new JSmartPanel();
        codeEditorPane = new CodeEditorTextPane();
        JScrollPane codeScrollPane = new JScrollPane(codeEditorPane);
        p.add(codeScrollPane, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0));
        return p;
    }

    private JPanel createXPathQueryPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(new JLabel("XPath Query (if any)"), BorderLayout.NORTH);
        xpathQueryArea.setBorder(BorderFactory.createLineBorder(Color.black));
        JScrollPane scrollPane = new JScrollPane(xpathQueryArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        p.add(scrollPane, BorderLayout.CENTER);
        JButton b = new JButton("Go");
        b.setMnemonic('g');
        b.addActionListener(new ShowListener());
        b.addActionListener(codeEditorPane);
        b.addActionListener(new XPathListener());
        b.addActionListener(new DFAListener());
        p.add(b, BorderLayout.SOUTH);
        return p;
    }

    public static void main(String[] args) {
        new ASTViewer();
    }
}
