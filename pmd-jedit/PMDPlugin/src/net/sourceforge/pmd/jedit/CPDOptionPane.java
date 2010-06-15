/*
 * User: tom
 * Date: Jul 8, 2002
 * Time: 4:29:19 PM
 */
package net.sourceforge.pmd.jedit;


import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.jEdit;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

import ise.java.awt.KappaLayout;

public class CPDOptionPane extends AbstractOptionPane implements OptionPane {


    private JCheckBox chkIgnoreLiterals;

    JTextField txtMinTileSize;

    JComboBox comboRenderer;

    public CPDOptionPane() {
        super(PMDJEditPlugin.NAME);
    }

    public void _init() {
        removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel panel = new JPanel(new KappaLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JLabel title = new JLabel("<html><b>" + jEdit.getProperty("cpd-viewer.label", "Copy/Paste Detector"));
 
        chkIgnoreLiterals = new JCheckBox(jEdit.getProperty("net.sf.pmd.Ignore_Literals_&_identifiers_when_detecting_Duplicate_Code", "Ignore Literals & identifiers when detecting Duplicate Code"), jEdit.getBooleanProperty(PMDJEditPlugin.IGNORE_LITERALS));

        JLabel lblMinTileSize = new JLabel(jEdit.getProperty("net.sf.pmd.Minimum_Tile_Size>", "Minimum Tile Size:"));
        // TODO: add a document that only accepts numbers
        txtMinTileSize = new JTextField(jEdit.getProperty(PMDJEditPlugin.DEFAULT_TILE_MINSIZE_PROPERTY, "100"), 5);
        ((AbstractDocument) txtMinTileSize.getDocument()).setDocumentFilter(new NumericDocumentFilter());
 
        JLabel lblRenderer = new JLabel(jEdit.getProperty("net.sf.pmd.Export_Output_as_", "Export output as: "));
        comboRenderer = new JComboBox(new String [] { "None", "Text", "Html", "XML", "CSV"} );
        comboRenderer.setSelectedItem(jEdit.getProperty(PMDJEditPlugin.RENDERER));
 
        panel.add("0, 0, 2, 1, W, w, 3", title);
        panel.add("0, 1, 2, 1, W, w, 3", chkIgnoreLiterals);
        panel.add("0, 2, 1, 1, W, w, 3", lblMinTileSize);
        panel.add("1, 2, 1, 1, W, w, 3", txtMinTileSize);
        panel.add("0, 3, 1, 1, W, w, 3", lblRenderer);
        panel.add("1, 3, 1, 1, W, w, 3", comboRenderer);
 
 
        add(panel);
    }

    public void _save() {
        jEdit.setBooleanProperty(PMDJEditPlugin.IGNORE_LITERALS, chkIgnoreLiterals.isSelected());
        jEdit.setIntegerProperty(PMDJEditPlugin.DEFAULT_TILE_MINSIZE_PROPERTY, (txtMinTileSize.getText().length() == 0) ? 100 : Integer.parseInt(txtMinTileSize.getText()));
        jEdit.setProperty(PMDJEditPlugin.RENDERER, (String) comboRenderer.getSelectedItem());
    }
 
    class NumericDocumentFilter extends DocumentFilter {
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) {
                return ;
            }

            if (isNumeric(string)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
        }

        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) {
                return ;
            }

            if (isNumeric(text)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isNumeric(String string) {
            for (char c : string.toCharArray()) {
                if (! Character.isDigit(c)) {
                    return false;
                }
            }
            return true;
        }
    }
}