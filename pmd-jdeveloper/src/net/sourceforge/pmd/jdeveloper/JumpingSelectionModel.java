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

/**
 * This class was inspired by a class on Sun's web site in the JList tutorial section.  It's
 * been twiddled somewhat since then, though.
 */
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
                    System.out.println("GOING THERE");
                    Ide.getEditorManager().openDefaultEditorInFrame(editor.getContext().getDocument().getURL());
                    editor.activate();
                    editor.open();
                    ((CodeEditor)editor).gotoLine(rv.getLine(), 0, false);
                    break;
                }
            }
        }
    }

}
