package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.RuleViolation;
import oracle.ide.Ide;
import oracle.ide.editor.Editor;
import oracle.ide.layout.ViewId;
import oracle.ide.log.AbstractLogPage;
import oracle.ide.model.Document;
import oracle.jdeveloper.ceditor.CodeEditor;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.util.Iterator;
import java.util.List;

public class RuleViolationPage extends AbstractLogPage {

    public class SingleSelectionModel extends DefaultListSelectionModel {
        public SingleSelectionModel() {
            setSelectionMode(SINGLE_SELECTION);
        }

        public void setSelectionInterval(int index0, int index1) {
            int oldIndex = getMinSelectionIndex();
            super.setSelectionInterval(index0, index1);
            int newIndex = getMinSelectionIndex();
            if (oldIndex != newIndex) {
                updateSingleSelection(oldIndex, newIndex);
            }
        }

        public void updateSingleSelection(int oldIndex, int newIndex) {
            RuleViolation rv = ((RVWrapper)model.getElementAt(newIndex)).getRV();
            List editors = Ide.getEditorManager().getAllEditors();
            for (Iterator i = editors.iterator(); i.hasNext();) {
                Editor editor = (Editor)i.next();
                Document doc = editor.getContext().getDocument();
                if (doc.getLongLabel().equals(rv.getFilename()) && editor instanceof CodeEditor) {
                    Ide.getEditorManager().openDefaultEditorInFrame(editor.getContext());
                    ((CodeEditor)editor).gotoLine(rv.getLine(), 0, false);
                    break;
                }
            }
        }
    }

    private static class RVWrapper {
        private RuleViolation rv;
        public RVWrapper(RuleViolation rv) {
            this.rv = rv;
        }
        public RuleViolation getRV() {
            return this.rv;
        }
        public String toString() {
            return rv.getFilename() + ":" + rv.getLine() +":"+ rv.getDescription();
        }
    }


    private DefaultListModel model = new DefaultListModel();
    private JScrollPane scrollPane;
    private JList list;

    public RuleViolationPage() {
        super(new ViewId("PMDPage", "PMD"), null, false);

        list = new JList(model);
        list.setSelectionModel(new SingleSelectionModel());
        scrollPane = new JScrollPane(list);
        Ide.getLogManager().addPage(this);
    }

    public void add(RuleViolation ruleViolation) {
        model.addElement(new RVWrapper(ruleViolation));
    }

    public Component getGUI() {
        return scrollPane;
    }

    public void clearAll() {
        super.clearAll();
        model.clear();
    }

}
