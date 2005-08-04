package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.RuleViolation;
import oracle.ide.layout.ViewId;
import oracle.ide.log.AbstractLogPage;

import javax.swing.*;
import java.awt.*;

public class RuleViolationPage extends AbstractLogPage {

    private DefaultListModel model = new DefaultListModel();
    private JScrollPane scrollPane;
    private JList list;

    public RuleViolationPage() {
        super(new ViewId("PMDPage", Plugin.TITLE), null, false);
        list = new JList(model);
        list.setSelectionModel(new JumpingSelectionModel(model));
        scrollPane = new JScrollPane(list);
    }

    public void add(RuleViolation ruleViolation) {
        model.addElement(new RuleViolationWrapper(ruleViolation));
    }

    public Component getGUI() {
        return scrollPane;
    }

    public void clearAll() {
        model.clear();
    }
}
