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

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridLayout;

public class PMDOptionPane extends AbstractOptionPane implements OptionPane {

    public class CheckboxList extends JList {

        private class MyMouseAdapter extends MouseAdapter {
            public void mouseEntered(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    JCheckBox box = (JCheckBox)getModel().getElementAt(index);
                    String example = rules.getRule(box).getExample();
                    exampleTextArea.setText(example);
                    exampleTextArea.setCaretPosition(0);
                }
            }

            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    JCheckBox box = (JCheckBox)getModel().getElementAt(index);
                    box.setSelected(!box.isSelected());
                    repaint();
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
        }

    }

    private SelectedRules rules;
    private JTextArea exampleTextArea= new JTextArea(10, 50);
    private JCheckBox directoryPopupBox;
	JTextField txtMinTileSize;

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

        addComponent(new JLabel("Please see http://pmd.sf.net/ for more information"));

        JPanel rulesPanel = new JPanel();
        rulesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        JList list = new CheckboxList(rules.getAllBoxes());
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        rulesPanel.add(new JScrollPane(list), BorderLayout.NORTH);

        JPanel textPanel = new JPanel();
        textPanel.setBorder(BorderFactory.createTitledBorder("Example"));
        textPanel.add(new JScrollPane(exampleTextArea));

        if (!jEdit.getProperties().containsKey(PMDJEditPlugin.OPTION_UI_DIRECTORY_POPUP)) {
            jEdit.setBooleanProperty(PMDJEditPlugin.OPTION_UI_DIRECTORY_POPUP, false);
        }

        directoryPopupBox = new JCheckBox("Ask for directory?", jEdit.getBooleanProperty(PMDJEditPlugin.OPTION_UI_DIRECTORY_POPUP));

		JPanel pnlSouth = new JPanel(new GridLayout(0,1));

		JPanel pnlTileSize = new JPanel();
		((FlowLayout)pnlTileSize.getLayout()).setAlignment(FlowLayout.LEFT);
		JLabel lblMinTileSize = new JLabel("Minimum Tile Size :");
		txtMinTileSize = new JTextField(jEdit.getProperty("pmd.cpd.defMinTileSize","100"),5);
		pnlTileSize.add(lblMinTileSize);
		pnlTileSize.add(txtMinTileSize);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(rulesPanel, BorderLayout.NORTH);
        mainPanel.add(textPanel, BorderLayout.CENTER);

		pnlSouth.add(directoryPopupBox);
		pnlSouth.add(pnlTileSize);
        mainPanel.add(pnlSouth, BorderLayout.SOUTH);

        addComponent(mainPanel);
    }

    public void _save() {
        rules.save();
        if (directoryPopupBox != null) {
            jEdit.setBooleanProperty(PMDJEditPlugin.OPTION_UI_DIRECTORY_POPUP, directoryPopupBox.isSelected());
        }

		jEdit.setIntegerProperty("pmd.cpd.defMinTileSize",(txtMinTileSize.getText().length() == 0)?100:Integer.parseInt(txtMinTileSize.getText()));
    }
}
