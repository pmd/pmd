package net.sourceforge.pmd.swingui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class ComponentFactory
{

    /**
     ******************************************************************************
     *
     * @return
     */
    protected static final JPanel createButtonPanel()
    {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        EtchedBorder etchedBorder = new EtchedBorder(EtchedBorder.RAISED);
        buttonPanel.setBorder(etchedBorder);

        return buttonPanel;
    }

    /**
     ******************************************************************************
     *
     * @param title
     *
     * @return
     */
    protected static final JButton createButton(String title)
    {
        JButton button;
        BevelBorder bevelBorder;
        EtchedBorder etchedBorder;
        CompoundBorder compoundBorder;
        LineBorder lineBorder;
        Dimension size;

        button = new JButton(title);
        lineBorder = new LineBorder(Color.black, 1, true);
        bevelBorder = new BevelBorder(BevelBorder.RAISED);
        compoundBorder = new CompoundBorder(bevelBorder, lineBorder);
        etchedBorder = new EtchedBorder(EtchedBorder.LOWERED);
        compoundBorder = new CompoundBorder(etchedBorder, compoundBorder);
        compoundBorder = new CompoundBorder(lineBorder, compoundBorder);
        size = new Dimension(80, 35);

        button.setBorder(compoundBorder);
        button.setFont(UIManager.getFont("buttonFont"));
        button.setSize(size);
        button.setPreferredSize(size);
        button.setOpaque(true);

        return button;
    }

    /**
     *******************************************************************************
     *
     */
    protected static final JButton createSaveButton(ActionListener actionListener)
    {
        JButton saveButton = ComponentFactory.createButton("Save");

        saveButton.setForeground(Color.white);
        saveButton.setBackground(UIManager.getColor("pmdGreen"));
        saveButton.addActionListener(actionListener);

        return saveButton;
    }

    /**
     *******************************************************************************
     *
     */
    protected static final JButton createCancelButton(ActionListener actionListener)
    {
        JButton cancelButton = ComponentFactory.createButton("Cancel");

        cancelButton.setForeground(Color.white);
        cancelButton.setBackground(UIManager.getColor("pmdRed"));
        cancelButton.addActionListener(actionListener);

        return cancelButton;
    }

    /**
     *******************************************************************************
     *
     */
    protected static final JPanel createSaveCancelButtonPanel(ActionListener saveActionListener,
                                                              ActionListener cancelActionListener)
    {
        JPanel buttonPanel = createButtonPanel();
        buttonPanel.add(createSaveButton(saveActionListener));
        buttonPanel.add(createCancelButton(cancelActionListener));

        return buttonPanel;
    }

    /**
     ******************************************************************************
     *
     * @param title
     *
     * @return
     */
    protected static final TitledBorder createTitledBorder(String title)
    {
        TitledBorder titledBorder;
        EtchedBorder etchedBorder;
        CompoundBorder compoundBorder;

        etchedBorder = new EtchedBorder(EtchedBorder.LOWERED);
        compoundBorder = new CompoundBorder(etchedBorder, etchedBorder);
        titledBorder = new TitledBorder(compoundBorder, title);

        titledBorder.setTitleFont(UIManager.getFont("titleFont"));
        titledBorder.setTitleColor(UIManager.getColor("pmdBlue"));
        titledBorder.setTitleJustification(TitledBorder.LEFT);

        return titledBorder;
    }

    /**
     ******************************************************************************
     *
     * @param view
     *
     * @return
     */
    protected static final JScrollPane createScrollPane(Component view)
    {
        JScrollPane scrollPane = new JScrollPane(view);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Color.white);
        scrollPane.setAutoscrolls(true);
        scrollPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        return scrollPane;
    }

    /**
     ******************************************************************************
     *
     * @return
     */
    protected static final JTextArea createTextArea(String text)
    {
        JTextArea textArea;
        BevelBorder bevelBorder;
        EmptyBorder emptyBorder;
        CompoundBorder compoundBorder;

        textArea = new JTextArea(text);
        bevelBorder = new BevelBorder(BevelBorder.LOWERED);
        emptyBorder = new EmptyBorder(3,3,3,3);
        compoundBorder = new CompoundBorder(bevelBorder, emptyBorder);

        textArea.setFont(UIManager.getFont("dataFont"));
        textArea.setBackground(Color.white);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(compoundBorder);
        textArea.setOpaque(true);

        return textArea;
    }
}