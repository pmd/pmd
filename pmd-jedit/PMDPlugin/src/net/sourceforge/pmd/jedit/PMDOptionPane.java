/*
 * User: tom
 * Date: Jul 8, 2002
 * Time: 4:29:19 PM
 */
package net.sourceforge.pmd.jedit;

import net.sourceforge.pmd.RuleSetNotFoundException;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PMDOptionPane extends AbstractOptionPane implements OptionPane {


    public class CheckboxList extends JList {

        private class MyMouseAdapter extends MouseAdapter {
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    JCheckBox box = (JCheckBox)getModel().getElementAt(index);
                    box.setSelected(!box.isSelected());
                    repaint();
                }
            }
        }
		
		
		private class MyMouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
            public void mouseMoved(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    JCheckBox box = (JCheckBox)getModel().getElementAt(index);
                    String example = rules.getRule(box).getExample();
                    exampleTextArea.setText(example);
                    exampleTextArea.setCaretPosition(0);
                }
            }
        }

        public class CheckboxListCellRenderer implements ListCellRenderer {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JCheckBox box = (JCheckBox)value;
                box.setEnabled(isEnabled());
                box.setFont(getFont());
                box.setFocusPainted(false);
                box.setBorderPainted(true);
                box.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : new EmptyBorder(1,1,1,1));
                return box;
            }
        }

        public CheckboxList(Object[] args) {
            super(args);
            setCellRenderer(new CheckboxListCellRenderer());
			addMouseListener(new MyMouseAdapter());
			addMouseMotionListener(new MyMouseMotionAdapter());
        }

    }

    SelectedRules rules;
    JTextArea exampleTextArea= new JTextArea(10, 50);
    private JCheckBox chkRunPMDOnSave, chkShowProgressBar, chkIgnoreLiterals;
	JTextField txtMinTileSize;
	JTextField txtCustomRules;
	JComboBox comboRenderer;



    public PMDOptionPane() {
        super(PMDJEditPlugin.NAME);
        try {
            rules = new SelectedRules();
        } catch (RuleSetNotFoundException rsne) {
            rsne.printStackTrace();
        }
    }

    public void _init() {
        removeAll();
		
		setLayout(new FlowLayout(FlowLayout.LEADING));
        addComponent(new JLabel("Please see http://pmd.sf.net/ for more information"));

        JPanel rulesPanel = new JPanel(new BorderLayout());
        rulesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        JList list = new CheckboxList(rules.getAllBoxes());
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        rulesPanel.add(new JScrollPane(list), BorderLayout.CENTER);
		//Custom Rule Panel Defination.
		JPanel pnlCustomRules = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlCustomRules.add(new JLabel("Path to custom rules.xml files(seperated by comma)"));
		pnlCustomRules.add((txtCustomRules = new JTextField(jEdit.getProperty(PMDJEditPlugin.CUSTOM_RULES_PATH_KEY,""),30)));

		rulesPanel.add(pnlCustomRules, BorderLayout.SOUTH);

        JPanel textPanel = new JPanel();
        textPanel.setBorder(BorderFactory.createTitledBorder("Example"));
        textPanel.add(new JScrollPane(exampleTextArea));

        chkRunPMDOnSave = new JCheckBox("Run PMD on Save", jEdit.getBooleanProperty(PMDJEditPlugin.RUN_PMD_ON_SAVE));
		chkShowProgressBar = new JCheckBox("Show PMD Progress Bar", jEdit.getBooleanProperty(PMDJEditPlugin.SHOW_PROGRESS));
		chkIgnoreLiterals = new JCheckBox("Ignore Literals & identifiers when detecting Duplicate Code", jEdit.getBooleanProperty(PMDJEditPlugin.IGNORE_LITERALS));

		JPanel pnlSouth = new JPanel(new GridLayout(0,1));

		JPanel pnlTileSize = new JPanel();
		((FlowLayout)pnlTileSize.getLayout()).setAlignment(FlowLayout.LEFT);
		JLabel lblMinTileSize = new JLabel("Minimum Tile Size :");
		txtMinTileSize = new JTextField(jEdit.getProperty(PMDJEditPlugin.DEFAULT_TILE_MINSIZE_PROPERTY,"100"),5);
		pnlTileSize.add(lblMinTileSize);
		pnlTileSize.add(txtMinTileSize);

		comboRenderer = new JComboBox(new String[] {"None", "Text", "Html", "XML", "CSV"});
		comboRenderer.setSelectedItem(jEdit.getProperty(PMDJEditPlugin.RENDERER));
        JLabel lblRenderer = new JLabel("Export Output as ");

		pnlTileSize.add(lblRenderer);
		pnlTileSize.add(comboRenderer);
		pnlTileSize.add(chkShowProgressBar);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0,1));
		
        mainPanel.add(rulesPanel);
        mainPanel.add(textPanel);

		pnlSouth.add(chkRunPMDOnSave);
		pnlSouth.add(chkIgnoreLiterals);
		pnlSouth.add(pnlTileSize);
        mainPanel.add(pnlSouth);
        addComponent(mainPanel);
    }

    public void _save() {
        rules.save();

		jEdit.setIntegerProperty(PMDJEditPlugin.DEFAULT_TILE_MINSIZE_PROPERTY,(txtMinTileSize.getText().length() == 0)?100:Integer.parseInt(txtMinTileSize.getText()));
		jEdit.setBooleanProperty(PMDJEditPlugin.RUN_PMD_ON_SAVE,(chkRunPMDOnSave.isSelected()));
		jEdit.setBooleanProperty(PMDJEditPlugin.IGNORE_LITERALS,(chkIgnoreLiterals.isSelected()));
		jEdit.setProperty(PMDJEditPlugin.RENDERER, (String)comboRenderer.getSelectedItem());
		jEdit.setBooleanProperty(PMDJEditPlugin.SHOW_PROGRESS, chkShowProgressBar.isSelected());

		if(txtCustomRules != null)
		{
			jEdit.setProperty(PMDJEditPlugin.CUSTOM_RULES_PATH_KEY,txtCustomRules.getText());
		}
    }
}
