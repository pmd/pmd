package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.RuleViolation;
import oracle.ide.Ide;
import oracle.ide.layout.ViewId;
import oracle.ide.log.AbstractLogPage;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.Component;

public class RuleViolationPage extends AbstractLogPage {

    private DefaultListModel model = new DefaultListModel();
    private JScrollPane scrollPane = new JScrollPane(new JList(model));

    public RuleViolationPage() {
        super(new ViewId("PMDPage", "PMD"), null, false);
        Ide.getLogManager().addPage(this);
    }

    public void add(RuleViolation ruleViolation) {
        model.addElement(ruleViolation.getFilename() + ":" + ruleViolation.getLine() +":"+ ruleViolation.getDescription());
    }

    public Component getGUI() {
        return scrollPane;
    }

    public void clearAll() {
        super.clearAll();
        model.clear();
    }
}
