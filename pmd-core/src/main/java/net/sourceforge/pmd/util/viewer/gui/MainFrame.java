/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.viewer.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;
import net.sourceforge.pmd.util.viewer.util.NLS;


/**
 * viewer's main frame
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 */

public class MainFrame
        extends JFrame
        implements ActionListener, ViewerModelListener {
    private ViewerModel model;
    private SourceCodePanel sourcePanel;
    private XPathPanel xPathPanel;
    private JButton evalBtn;
    private JLabel statusLbl;
    private JRadioButtonMenuItem jdk13MenuItem;
    private JRadioButtonMenuItem jdk14MenuItem;
    private JRadioButtonMenuItem jdk15MenuItem;	//NOPMD
    private JRadioButtonMenuItem jdk16MenuItem;
    private JRadioButtonMenuItem jdk17MenuItem;
    private JRadioButtonMenuItem plsqlMenuItem; 

    /**
     * constructs and shows the frame
     */
    public MainFrame() {
        super(NLS.nls("MAIN.FRAME.TITLE") + " (v " + PMD.VERSION + ')');
        init();
    }

    private void init() {
        model = new ViewerModel();
        model.addViewerModelListener(this);
        sourcePanel = new SourceCodePanel(model);
        ASTPanel astPanel = new ASTPanel(model);
        xPathPanel = new XPathPanel(model);
        getContentPane().setLayout(new BorderLayout());
        JSplitPane editingPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sourcePanel, astPanel);
        editingPane.setResizeWeight(0.5d);
        JPanel interactionsPane = new JPanel(new BorderLayout());
        interactionsPane.add(xPathPanel, BorderLayout.SOUTH);
        interactionsPane.add(editingPane, BorderLayout.CENTER);
        getContentPane().add(interactionsPane, BorderLayout.CENTER);
        JButton compileBtn = new JButton(NLS.nls("MAIN.FRAME.COMPILE_BUTTON.TITLE"));
        compileBtn.setActionCommand(ActionCommands.COMPILE_ACTION);
        compileBtn.addActionListener(this);
        evalBtn = new JButton(NLS.nls("MAIN.FRAME.EVALUATE_BUTTON.TITLE"));
        evalBtn.setActionCommand(ActionCommands.EVALUATE_ACTION);
        evalBtn.addActionListener(this);
        evalBtn.setEnabled(false);
        statusLbl = new JLabel();
        statusLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPane.add(compileBtn);
        btnPane.add(evalBtn);
        btnPane.add(statusLbl);
        getContentPane().add(btnPane, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Language");
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
        jdk16MenuItem = new JRadioButtonMenuItem("JDK 1.6");
        jdk16MenuItem.setSelected(false);
        group.add(jdk16MenuItem);
        menu.add(jdk16MenuItem);
        jdk17MenuItem = new JRadioButtonMenuItem("JDK 1.7");
        jdk17MenuItem.setSelected(false);
        group.add(jdk17MenuItem);
        menu.add(jdk17MenuItem);
	//PLSQL
        plsqlMenuItem = new JRadioButtonMenuItem("PLSQL");
        plsqlMenuItem.setSelected(false);
        group.add(plsqlMenuItem);
        menu.add(plsqlMenuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setSize(800, 600);
        setVisible(true);
    }

    private LanguageVersion getLanguageVersion() {
        String javaName = "Java";

        if (jdk14MenuItem.isSelected()) {
            return LanguageRegistry.getLanguage(javaName).getVersion("1.4");
        } else if (jdk13MenuItem.isSelected()) {
            return LanguageRegistry.getLanguage(javaName).getVersion("1.3");
        } else if (jdk15MenuItem.isSelected()) {
            return LanguageRegistry.getLanguage(javaName).getVersion("1.5");
        } else if (jdk16MenuItem.isSelected()) {
            return LanguageRegistry.getLanguage(javaName).getVersion("1.6");
        } else if (jdk17MenuItem.isSelected()) {
            return LanguageRegistry.getLanguage(javaName).getVersion("1.7");
        } else if (plsqlMenuItem.isSelected()) {
            return LanguageRegistry.getLanguage("PLSQL").getDefaultVersion();
        }
        return LanguageRegistry.getLanguage(javaName).getVersion("1.5");
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        long t0;
        long t1;
        if (ActionCommands.COMPILE_ACTION.equals(command)) {
            try {
                t0 = System.currentTimeMillis();
                model.commitSource(sourcePanel.getSourceCode(), getLanguageVersion() );
                t1 = System.currentTimeMillis();
                setStatus(NLS.nls("MAIN.FRAME.COMPILATION.TOOK") + " " + (t1 - t0) + " ms");
            } catch (ParseException exc) {
                setStatus(NLS.nls("MAIN.FRAME.COMPILATION.PROBLEM") + " " + exc.toString());
                new ParseExceptionHandler(this, exc);
            }
        } else if (ActionCommands.EVALUATE_ACTION.equals(command)) {
            try {
                t0 = System.currentTimeMillis();
                model.evaluateXPathExpression(xPathPanel.getXPathExpression(), this);
                t1 = System.currentTimeMillis();
                setStatus(NLS.nls("MAIN.FRAME.EVALUATION.TOOK") + " " + (t1 - t0) + " ms");
            } catch (Exception exc) {
                setStatus(NLS.nls("MAIN.FRAME.EVALUATION.PROBLEM") + " " + exc.toString());
                new ParseExceptionHandler(this, exc);
            }
        }
    }

    /**
     * Sets the status bar message
     *
     * @param string the new status, the empty string will be set if the value is <code>null</code>
     */
    private void setStatus(String string) {
        statusLbl.setText(string == null ? "" : string);
    }

    /**
     * @see ViewerModelListener#viewerModelChanged(ViewerModelEvent)
     */
    public void viewerModelChanged(ViewerModelEvent e) {
        evalBtn.setEnabled(model.hasCompiledTree());
    }
}
