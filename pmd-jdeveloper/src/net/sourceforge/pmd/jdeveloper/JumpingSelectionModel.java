package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.RuleViolation;
import oracle.ide.Ide;
import oracle.ide.editor.Editor;
import oracle.ide.model.Document;
import oracle.jdeveloper.ceditor.CodeEditor;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import java.util.Iterator;
import java.util.List;

public class JumpingSelectionModel extends DefaultListSelectionModel {

    private DefaultListModel model;

    public JumpingSelectionModel(DefaultListModel model) {
        this.model = model;
    }

    public JumpingSelectionModel() {
        setSelectionMode(SINGLE_SELECTION);
    }

    public void setSelectionInterval(int index0, int index1) {
        int oldIndex = getMinSelectionIndex();
        super.setSelectionInterval(index0, index1);
        int newIndex = getMinSelectionIndex();
        if (oldIndex != newIndex) {
            RuleViolation rv = ((RuleViolationWrapper)model.getElementAt(newIndex)).getRV();
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

}
