/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.designer;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.TargetJDK1_3;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import net.sourceforge.pmd.jaxen.MatchesFunction;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class Designer implements ClipboardOwner {

    private JavaParser createParser() {
        return getJDKVersion().createParser(new StringReader(codeEditorPane.getText()));
    }

    private TargetJDKVersion getJDKVersion() {
        if (jdk14MenuItem.isSelected()) {
            return new TargetJDK1_4();
        } else if (jdk13MenuItem.isSelected()) {
            return new TargetJDK1_3();
        }
        return new TargetJDK1_5();
    }

    private class ShowListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            MyPrintStream ps = new MyPrintStream();
            System.setOut(ps);
            try {
                ASTCompilationUnit lastCompilationUnit = createParser().CompilationUnit();
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
                ctx.setSourceCodeFilename("[no filename]");
                StringReader reader = new StringReader(codeEditorPane.getText());
                new PMD(getJDKVersion()).processFile(reader, rs, ctx);
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
            JavaParser parser = createParser();
            try {
                XPath xpath = new BaseXPath(xpathQueryArea.getText(), new DocumentNavigator());
                ASTCompilationUnit c = parser.CompilationUnit();
                for (Iterator iter = xpath.selectNodes(c).iterator(); iter.hasNext();) {
                    StringBuffer sb = new StringBuffer();
                    Object obj = iter.next();
                    if (obj instanceof String) {
                        System.out.println("Result was a string: " + ((String) obj));
                    } else if (!(obj instanceof Boolean)) {
                        // if it's a Boolean and it's 'false', what does that mean?
                        SimpleNode node = (SimpleNode) obj;
                        String name = node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.') + 1);
                        String line = " at line " + String.valueOf(node.getBeginLine());
                        sb.append(name).append(line).append(System.getProperty("line.separator"));
                        xpathResults.addElement(sb.toString().trim());
                    }
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

    private final CodeEditorTextPane codeEditorPane = new CodeEditorTextPane();
    private final JTextArea astArea = new JTextArea();
    private DefaultListModel xpathResults = new DefaultListModel();
    private final JList xpathResultList = new JList(xpathResults);
    private final JTextArea xpathQueryArea = new JTextArea(15, 30);
    private final JFrame frame = new JFrame("PMD Rule Designer");
    private final DFAPanel dfaPanel = new DFAPanel();
    private JRadioButtonMenuItem jdk13MenuItem;
    private JRadioButtonMenuItem jdk14MenuItem;
    private JRadioButtonMenuItem jdk15MenuItem;

    public Designer() {
        MatchesFunction.registerSelfInSimpleContext();

        xpathQueryArea.setFont(new Font("Verdana", Font.PLAIN, 16));
        JSplitPane controlSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(codeEditorPane), createXPathQueryPanel());
        JSplitPane resultsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createASTPanel(), createXPathResultPanel());

        JTabbedPane tabbed = new JTabbedPane();
        tabbed.addTab("Abstract Syntax Tree / XPath", resultsSplitPane);
        tabbed.addTab("Data Flow Analysis", dfaPanel);
        try {
            // Remove when minimal runtime support is >= JDK 1.4
            Method setMnemonicAt = JTabbedPane.class.getMethod("setMnemonicAt", new Class[]{Integer.TYPE, Integer.TYPE});
            if (setMnemonicAt != null) {
                //        // Compatible with >= JDK 1.4
                //        tabbed.setMnemonicAt(0, KeyEvent.VK_A);
                //        tabbed.setMnemonicAt(1, KeyEvent.VK_D);
                setMnemonicAt.invoke(tabbed, new Object[]{new Integer(0), new Integer(KeyEvent.VK_A)});
                setMnemonicAt.invoke(tabbed, new Object[]{new Integer(1), new Integer(KeyEvent.VK_D)});
            }
        } catch (NoSuchMethodException nsme) { // Runtime is < JDK 1.4
        } catch (IllegalAccessException e) { // Runtime is >= JDK 1.4 but there was an error accessing the function
            e.printStackTrace();
            throw new InternalError("Runtime reports to be >= JDK 1.4 yet String.split(java.lang.String) is broken.");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new InternalError("Runtime reports to be >= JDK 1.4 yet String.split(java.lang.String) is broken.");
        } catch (InvocationTargetException e) { // Runtime is >= JDK 1.4 but there was an error accessing the function
            e.printStackTrace();
            throw new InternalError("Runtime reports to be >= JDK 1.4 yet String.split(java.lang.String) is broken.");
        }

        JSplitPane containerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlSplitPane, tabbed);
        containerSplitPane.setContinuousLayout(true);

        JMenuBar menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(containerSplitPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        frame.setSize(screenHeight - (screenHeight / 4), screenHeight - (screenHeight / 4));
        frame.setLocation((screenWidth / 2) - frame.getWidth() / 2, (screenHeight / 2) - frame.getHeight() / 2);
        frame.setVisible(true);
        frame.pack();
        frame.show();
        resultsSplitPane.setDividerLocation(resultsSplitPane.getMaximumDividerLocation() - (resultsSplitPane.getMaximumDividerLocation() / 2));
        //containerSplitPane.setDividerLocation(containerSplitPane.getMaximumDividerLocation() / 2);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("JDK");
        ButtonGroup group = new ButtonGroup();
        jdk13MenuItem = new JRadioButtonMenuItem("JDK 1.3");
        jdk13MenuItem.setSelected(false);
        group.add(jdk13MenuItem);
        menu.add(jdk13MenuItem);
        jdk14MenuItem = new JRadioButtonMenuItem("JDK 1.4");
        jdk14MenuItem.setSelected(true);
        group.add(jdk14MenuItem);
        menu.add(jdk14MenuItem);
        jdk15MenuItem = new JRadioButtonMenuItem("JDK 1.5");
        jdk15MenuItem.setSelected(false);
        group.add(jdk15MenuItem);
        menu.add(jdk15MenuItem);
        menuBar.add(menu);

        JMenu actionsMenu = new JMenu("Actions");
        JMenuItem copyXMLItem = new JMenuItem("Copy xml to clipboard");
        copyXMLItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyXmlToClipboard();
            }
        });
        actionsMenu.add(copyXMLItem);
        JMenuItem createRuleXMLItem = new JMenuItem("Create rule XML");
        createRuleXMLItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createRuleXML();
            }
        });
        actionsMenu.add(createRuleXMLItem);
        menuBar.add(actionsMenu);
        return menuBar;
    }


    private void createRuleXML() {
        JPanel rulenamePanel = new JPanel();
        rulenamePanel.setLayout(new FlowLayout());
        rulenamePanel.add(new JLabel("Rule name"));
        final JTextField rulenameField = new JTextField(30);
        rulenamePanel.add(rulenameField);
        JPanel rulemsgPanel = new JPanel();
        rulemsgPanel.setLayout(new FlowLayout());
        rulemsgPanel.add(new JLabel("Rule msg"));
        final JTextField rulemsgField = new JTextField(60);
        rulemsgPanel.add(rulemsgField);
        JPanel ruledescPanel = new JPanel();
        ruledescPanel.setLayout(new FlowLayout());
        ruledescPanel.add(new JLabel("Rule desc"));
        final JTextField ruledescField = new JTextField(60);
        ruledescPanel.add(ruledescField);
        JPanel ruleXMLPanel = new JPanel();
        final JTextArea ruleXMLArea = new JTextArea(30, 50);
        ruleXMLPanel.add(ruleXMLArea);
        JButton go = new JButton("Create rule XML");
        go.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringBuffer sb = new StringBuffer();
                sb.append("<rule  name=\"" + rulenameField.getText() + "\"" + PMD.EOL);
                sb.append("  message=\"" + rulemsgField.getText() + "\"" + PMD.EOL);
                sb.append("  class=\"" + (xpathQueryArea.getText().length() == 0 ? "" : "net.sourceforge.pmd.rules.XPathRule") + "\">" + PMD.EOL);
                sb.append("  <description>" + PMD.EOL);
                sb.append("  " + ruledescField.getText() + PMD.EOL);
                sb.append("  </description>" + PMD.EOL);
                if (xpathQueryArea.getText().length() != 0) {
                    sb.append("  <properties>" + PMD.EOL);
                    sb.append("    <property name=\"xpath\">" + PMD.EOL);
                    sb.append("    <value>" + PMD.EOL);
                    sb.append("<![CDATA[" + PMD.EOL);
                    sb.append(xpathQueryArea.getText() + PMD.EOL);
                    sb.append("]]>" + PMD.EOL);
                    sb.append("    </value>" + PMD.EOL);
                    sb.append("    </property>" + PMD.EOL);
                    sb.append("  </properties>" + PMD.EOL);
                }
                sb.append("  <priority>3</priority>" + PMD.EOL);
                sb.append("  <example>" + PMD.EOL);
                sb.append("<![CDATA[" + PMD.EOL);
                sb.append(codeEditorPane.getText());
                sb.append("]]>" + PMD.EOL);
                sb.append("  </example>" + PMD.EOL);
                sb.append("</rule>" + PMD.EOL);

                ruleXMLArea.setText(sb.toString());
            }
        });

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BorderLayout());
        fieldsPanel.add(rulenamePanel, BorderLayout.NORTH);
        fieldsPanel.add(rulemsgPanel, BorderLayout.CENTER);
        fieldsPanel.add(ruledescPanel, BorderLayout.SOUTH);

        JPanel fieldBtnPanel = new JPanel();
        fieldBtnPanel.setLayout(new BorderLayout());
        fieldBtnPanel.add(fieldsPanel, BorderLayout.NORTH);
        fieldBtnPanel.add(go, BorderLayout.SOUTH);

        JPanel outer = new JPanel(new BorderLayout());
        outer.add(fieldBtnPanel, BorderLayout.NORTH);
        outer.add(ruleXMLPanel, BorderLayout.SOUTH);

        JDialog d = new JDialog(frame);
        d.setSize(200, 300);
        d.getContentPane().add(outer);
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        d.setLocation((screenWidth / 2) - frame.getWidth() / 2, (screenHeight / 2) - frame.getHeight() / 2);
        d.setVisible(true);
        d.pack();
        d.show();
    }

    private JComponent createASTPanel() {
        astArea.setRows(10);
        astArea.setColumns(20);
        JScrollPane astScrollPane = new JScrollPane(astArea);
        return astScrollPane;
    }

    private JComponent createXPathResultPanel() {
        xpathResults.addElement("No results yet");
        xpathResultList.setBorder(BorderFactory.createLineBorder(Color.black));
        xpathResultList.setFixedCellWidth(300);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(xpathResultList);
        return scrollPane;
    }

    private JPanel createXPathQueryPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        xpathQueryArea.setBorder(BorderFactory.createLineBorder(Color.black));
        JScrollPane scrollPane = new JScrollPane(xpathQueryArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        final JButton b = createGoButton();

        p.add(new JLabel("XPath Query (if any)"), BorderLayout.NORTH);
        p.add(scrollPane, BorderLayout.CENTER);
        p.add(b, BorderLayout.SOUTH);

        return p;
    }

    private JButton createGoButton() {
        JButton b = new JButton("Go");
        b.setMnemonic('g');
        b.addActionListener(new ShowListener());
        b.addActionListener(codeEditorPane);
        b.addActionListener(new XPathListener());
        b.addActionListener(new DFAListener());
        return b;
    }

    public static void main(String[] args) {
        new Designer();
    }

    private final void copyXmlToClipboard() {
        if (codeEditorPane.getText() != null && codeEditorPane.getText().trim().length() > 0) {
            ASTCompilationUnit cu = createParser().CompilationUnit();
            String xml = "";
            if (cu != null) {
                try {
                    xml = getXmlString(cu);
                } catch (IOException e) {
                    e.printStackTrace();
                    xml = "Error trying to construct XML representation";
                }
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(xml), this);
        }
    }

    /**
     * Returns an unformatted xml string (without the declaration)
     *
     * @param node
     * @return
     * @throws java.io.IOException
     */
    private String getXmlString(SimpleNode node) throws IOException {
        StringWriter writer = new StringWriter();
        XMLSerializer xmlSerializer = new XMLSerializer(writer, new OutputFormat("XML", "UTF-8", true));
        xmlSerializer.asDOMSerializer();
        xmlSerializer.serialize(node.asXml());
        return writer.toString();
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
