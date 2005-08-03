package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.RuleViolation;
import oracle.ide.editor.EditorManager;
import oracle.ide.net.URLFactory;
import oracle.jdeveloper.ceditor.CodeEditor;

import javax.swing.*;

/**
 * This class was inspired by a class on Sun's web site in the JList tutorial section.  It's
 * been twiddled somewhat since then.
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
            RuleViolation rv = ((RuleViolationWrapper)model.getElementAt(newIndex)).getRuleViolation();
            EditorManager.getEditorManager().openDefaultEditorInFrame(URLFactory.newFileURL(rv.getFilename()));
            ((CodeEditor)EditorManager.getEditorManager().getCurrentEditor()).gotoLine(rv.getLine(), 0, false);
        }
    }
}
