package net.sourceforge.pmd.swingui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;
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
     * @param title
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
        bevelBorder = new BevelBorder(BevelBorder.RAISED);
        etchedBorder = new EtchedBorder(EtchedBorder.LOWERED);
        compoundBorder = new CompoundBorder(etchedBorder, bevelBorder);
        etchedBorder = new EtchedBorder(EtchedBorder.RAISED);
        compoundBorder = new CompoundBorder(compoundBorder, etchedBorder);
        lineBorder = new LineBorder(Color.black, 1, true);
        compoundBorder = new CompoundBorder(lineBorder, compoundBorder);
        size = new Dimension(80,30);

        button.setBorder(compoundBorder);
        button.setFont(UIManager.getFont("buttonFont"));
        button.setSize(size);
        button.setPreferredSize(size);

        return button;
    }

    /**
     ******************************************************************************
     *
     * @param title
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
     * @param view
     */
    protected static final JTextArea createTextArea()
    {
        JTextArea textArea;
        BevelBorder bevelBorder;
        EmptyBorder emptyBorder;
        CompoundBorder compoundBorder;

        textArea = new JTextArea();
        bevelBorder = new BevelBorder(BevelBorder.LOWERED);
        emptyBorder = new EmptyBorder(3,3,3,3);
        compoundBorder = new CompoundBorder(bevelBorder, emptyBorder);

        textArea.setFont(UIManager.getFont("dataFont"));
        textArea.setBackground(Color.white);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(compoundBorder);

        return textArea;
    }
}