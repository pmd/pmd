package net.sourceforge.pmd.swingui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class ComponentFactory {

    /**
     ******************************************************************************
     *
     * @return
     */
    protected static final JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        EmptyBorder emptyBorder = new EmptyBorder(3, 3, 3, 3);
        EtchedBorder etchedBorder = new EtchedBorder(EtchedBorder.RAISED);
        CompoundBorder compoundBorder = new CompoundBorder(etchedBorder, emptyBorder);
        buttonPanel.setBorder(compoundBorder);


        return buttonPanel;
    }

    /**
     ******************************************************************************
     *
     * @param title
     *
     * @return
     */
    protected static final JButton createButton(String title) {
        return createButton(title, null, null);
    }

    /**
     ******************************************************************************
     *
     * @param title
     *
     * @return
     */
    protected static final JButton createButton(String title, Color background, Color foreground) {
        JButton button;
        BevelBorder bevelBorder;
        EtchedBorder etchedBorder;
        CompoundBorder compoundBorder;
        LineBorder lineBorder;
        Dimension size;

        if (background == null) {
            background = UIManager.getColor("standardButtonBackground");
        }

        if (foreground == null) {
            foreground = UIManager.getColor("standardButtonForeground");
        }

        button = new JButton(title);
        lineBorder = new LineBorder(background.darker(), 1, true);
        bevelBorder = new BevelBorder(BevelBorder.RAISED);
        compoundBorder = new CompoundBorder(bevelBorder, lineBorder);
        etchedBorder = new EtchedBorder(EtchedBorder.LOWERED);
        compoundBorder = new CompoundBorder(etchedBorder, compoundBorder);
        compoundBorder = new CompoundBorder(lineBorder, compoundBorder);
        size = new Dimension(80, 30);

        button.setBackground(background);
        button.setForeground(foreground);
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
    protected static final JButton createSaveButton(ActionListener actionListener) {
        Color background = UIManager.getColor("pmdGreen");
        Color foreground = Color.white;
        JButton saveButton = ComponentFactory.createButton("Save", background, foreground);
        saveButton.addActionListener(actionListener);

        return saveButton;
    }

    /**
     *******************************************************************************
     *
     */
    protected static final JButton createCancelButton(ActionListener actionListener) {
        Color background = UIManager.getColor("pmdRed");
        Color foreground = Color.white;
        JButton cancelButton = ComponentFactory.createButton("Cancel", background, foreground);
        cancelButton.addActionListener(actionListener);

        return cancelButton;
    }

    /**
     *******************************************************************************
     *
     */
    protected static final JPanel createSaveCancelButtonPanel(ActionListener saveActionListener, ActionListener cancelActionListener) {
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
    protected static final TitledBorder createTitledBorder(String title) {
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
    protected static final JSplitPane createHorizontalSplitPane(Component leftPane, Component rightPane) {
        JSplitPane splitPane = new JSplitPane();

        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(10);
        splitPane.setLeftComponent(leftPane);
        splitPane.setRightComponent(rightPane);
        splitPane.setOpaque(true);

        return splitPane;
    }

    /**
     ******************************************************************************
     *
     * @param view
     *
     * @return
     */
    protected static final JSplitPane createVerticalSplitPane(Component topPane, Component bottomPane) {
        JSplitPane splitPane = new JSplitPane();

        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(10);
        splitPane.setTopComponent(topPane);
        splitPane.setBottomComponent(bottomPane);
        splitPane.setOpaque(true);

        return splitPane;
    }

    /**
     ******************************************************************************
     *
     * @param view
     *
     * @return
     */
    protected static final JScrollPane createScrollPane(Component view) {
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
    protected static final JTextArea createTextArea(String text) {
        JTextArea textArea;
        BevelBorder bevelBorder;
        EmptyBorder emptyBorder;
        CompoundBorder compoundBorder;

        textArea = new JTextArea(text);
        bevelBorder = new BevelBorder(BevelBorder.LOWERED);
        emptyBorder = new EmptyBorder(3, 3, 3, 3);
        compoundBorder = new CompoundBorder(bevelBorder, emptyBorder);

        textArea.setFont(UIManager.getFont("dataFont"));
        textArea.setBackground(Color.white);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(compoundBorder);
        textArea.setOpaque(true);

        return textArea;
    }

    /**
     *******************************************************************************
     *
     * @param windowWidth
     * @param windowHeight
     */
    protected static final Dimension adjustWindowSize(int windowWidth, int windowHeight) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (windowWidth >= screenSize.width) {
            windowWidth = screenSize.width - 10;
        }

        if (windowHeight >= screenSize.height) {
            windowHeight = screenSize.height - 20;
        }

        return new Dimension(windowWidth, windowHeight);
    }
}