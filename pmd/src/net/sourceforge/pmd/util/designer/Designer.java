/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.designer;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import sun.rmi.runtime.GetThreadPoolAction;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

public class Designer implements ClipboardOwner {

    private ASTCompilationUnit doParse() {
        StringReader sr = new StringReader(codeEditorPane.getText());
        JavaParser parser = (new TargetJDK1_4()).createParser(sr);
        return parser.CompilationUnit();        
    }
    private class ShowListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            MyPrintStream ps = new MyPrintStream();
            System.setOut(ps);
            try {
                ASTCompilationUnit lastCompilationUnit = doParse();
                lastCompilationUnit.dump("");
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
                ctx.setSourceCodeFilename("[scratchpad]");
                new PMD().processFile(new StringReader(codeEditorPane.getText()), rs, ctx);
                List methods = dfaGraphRule.getMethods();
                if (!methods.isEmpty()) {
                    dfaPanel.resetTo(methods, codeEditorPane);
                    dfaPanel.repaint();
                }
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
                    String name = node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.') + 1);
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


    private CodeEditorTextPane codeEditorPane;
    private JTextArea astArea = new JTextArea();
    private DefaultListModel xpathResults = new DefaultListModel();
    private JList xpathResultList = new JList(xpathResults);
    private JTextArea xpathQueryArea = new JTextArea(10, 30);
    private JFrame frame = new JFrame("PMD Rule Designer");
    private DFAPanel dfaPanel;
    private final MouseListener codeEditPanelMouseListener = new MouseListener() {
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popUpMenu(e);
            }
        }
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        };

    public Designer() {
        JPanel controlPanel = new JPanel();
        
        controlPanel.add(createCodeEditPanel());
        controlPanel.add(createXPathQueryPanel());

        JSmartPanel astPanel = createASTPanel();
        JPanel xpathResultPanel = createXPathResultPanel();

        JSplitPane resultsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, astPanel, xpathResultPanel);

        dfaPanel = new DFAPanel();

        JTabbedPane tabbed = new JTabbedPane();
        tabbed.addTab("Abstract Syntax Tree / XPath", resultsSplitPane);
        tabbed.addTab("Data Flow Analysis", dfaPanel);
        // TODO Remove when minimal runtime support is >= JDK 1.4
        try {
            if (JTabbedPane.class.getMethod("setMnemonicAt", new Class[]{Integer.TYPE, Integer.TYPE}) != null) {
                // Compatible with >= JDK 1.4
                tabbed.setMnemonicAt(0, KeyEvent.VK_A);
                tabbed.setMnemonicAt(1, KeyEvent.VK_D);
            }
        } catch (NoSuchMethodException nsme) {
            // Ok, means we're running < JDK 1.4
        } 

        JSplitPane containerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlPanel, tabbed);

        frame.getContentPane().add(containerSplitPane);

        frame.setSize(1000, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        frame.setLocation((screenWidth / 2) - frame.getWidth() / 2, (screenHeight / 2) - frame.getHeight() / 2);
        frame.setVisible(true);
        frame.show();

        containerSplitPane.setDividerLocation(containerSplitPane.getMaximumDividerLocation() - (containerSplitPane.getMaximumDividerLocation() / 2));
        resultsSplitPane.setDividerLocation(resultsSplitPane.getMaximumDividerLocation() - (resultsSplitPane.getMaximumDividerLocation() / 2));
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
        
        codeEditorPane.addMouseListener(codeEditPanelMouseListener);
        
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
        new Designer();
    }
    private final void popUpMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem mi = new JMenuItem("Copy xml");
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyXmlToClipboard();
            }
        });
        menu.add(mi);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    private final void copyXmlToClipboard() {
        String text = this.codeEditorPane.getText();
        if (text!=null && text.trim().length()>0) {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            String xml = getXml();
            StringSelection ss = new StringSelection(xml);
            cb.setContents(ss, this);
        } 
    }

    private String getXml() {
        ASTCompilationUnit cu = doParse();
        if (cu!=null) {
            return cu.asXml();
        }
        return "";
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //NOOP
    }
}
