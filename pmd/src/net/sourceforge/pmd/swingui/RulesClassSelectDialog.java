package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDDirectory;
import net.sourceforge.pmd.PMDException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RulesClassSelectDialog extends JDialog
{

    private PMDViewer m_pmdViewer;
    private DirectoryTree m_tree;
    private DirectoryTable m_table;
    private JSplitPane m_splitPane;
    private JScrollPane m_treeScrollPane;
    private JScrollPane m_tableScrollPane;
    private File m_selectedClassFile;
    private boolean m_selectWasPressed;

    /**
     *******************************************************************************
     *
     * @param parentWindow
     */
    protected RulesClassSelectDialog(JDialog parentWindow, PMDViewer pmdViewer)
        throws PMDException
    {
        super(parentWindow, "Rules Class File Selector", true);

        m_pmdViewer = pmdViewer;
        int windowWidth = 1200;
        int windowHeight = 800;
        Dimension screenSize = getToolkit().getScreenSize();

        if (windowWidth >= screenSize.width)
        {
            windowWidth = screenSize.width - 10;
        }

        if (windowHeight >= screenSize.height)
        {
            windowHeight = screenSize.height - 20;
        }

        int windowLocationX = pmdViewer.getX();
        int windowLocationY = pmdViewer.getY();
        setLocation(windowLocationX, windowLocationY);
        setSize(windowWidth, windowHeight);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        createDirectoryTreeScrollPane();
        createDirectoryTableScrollPane();
        createDirectorySplitPane();
        buildTree();
        JPanel buttonPanel = createButtonPanel();
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(m_splitPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(contentPanel);
    }

    /**
     *********************************************************************************
     *
     */
    private void createDirectoryTreeScrollPane()
    {
        Color background;

        m_tree = new DirectoryTree("Rules Repository");
        m_treeScrollPane = ComponentFactory.createScrollPane(m_tree);
        background = UIManager.getColor("pmdTreeBackground");

        m_treeScrollPane.getViewport().setBackground(background);
    }

    /**
     *********************************************************************************
     *
     */
    private void createDirectoryTableScrollPane()
    {
        Color background;

        m_table = new DirectoryTable(m_tree, ".class");
        m_tableScrollPane = ComponentFactory.createScrollPane(m_table);
        background = UIManager.getColor("pmdTableBackground");

        m_tableScrollPane.getViewport().setBackground(background);
    }

    /**
     *********************************************************************************
     *
     */
    private void createDirectorySplitPane()
    {
        m_splitPane = new JSplitPane();

        m_splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        m_splitPane.setResizeWeight(0.5);
        m_splitPane.setDividerSize(5);
        m_splitPane.setLeftComponent(m_treeScrollPane);
        m_splitPane.setRightComponent(m_tableScrollPane);
    }

    /**
     *******************************************************************************
     *
     */
    private void buildTree()
        throws PMDException
    {
        PMDDirectory pmdDirectory = m_pmdViewer.getPMDDirectory();
        String rulesDirectoryPath = pmdDirectory.getRuleSetsDirectory();
        File[] rulesDirectory = { new File(rulesDirectoryPath) };
        ((DirectoryTreeModel) m_tree.getModel()).setupFiles(rulesDirectory);
        m_tree.expandRootNode();
    }

    /**
     *******************************************************************************
     *
     */
    private JPanel createButtonPanel()
    {
        ActionListener selectActionListener = new SelectButtonActionListener();
        ActionListener cancelActionListener = new CancelButtonActionListener();
        JPanel buttonPanel = ComponentFactory.createButtonPanel();
        JButton selectButton = ComponentFactory.createSaveButton(selectActionListener);
        JButton cancelButton = ComponentFactory.createCancelButton(cancelActionListener);
        selectButton.setText("Select");
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected File getSelectedClassFile()
    {
        return m_selectedClassFile;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected boolean selectWasPressed()
    {
        return m_selectWasPressed;
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class SelectButtonActionListener implements ActionListener
    {

        /**
         ********************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            m_selectWasPressed = true;
            m_selectedClassFile = m_table.getSelectedFile();
            RulesClassSelectDialog.this.setVisible(false);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class CancelButtonActionListener implements ActionListener
    {

        /**
         ********************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            RulesClassSelectDialog.this.setVisible(false);
        }
    }
}