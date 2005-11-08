package net.sourceforge.pmd.bluej;

import bluej.extensions.BClass;
import bluej.extensions.BObject;
import bluej.extensions.BPackage;
import bluej.extensions.MenuGenerator;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;

import javax.swing.*;
import java.awt.event.ActionEvent;

/* This class shows how you can bind different menus to different parts of BlueJ
 * Remember:
 * - getToolsMenuItem, getClassMenuItem and getObjectMenuItem may be called by BlueJ at any time.
 * - They must generate a new JMenuItem each time they are called.
 * - No reference to the JMenuItem should be stored in the extension.
 * - You must be quick in generating your menu.
 */
public class MenuBuilder extends MenuGenerator {
    private BPackage curPackage;
    private BClass curClass;
    private BObject curObject;

    public JMenuItem getToolsMenuItem(BPackage aPackage) {
        return new JMenuItem(new SimpleAction("Click Tools", "Tools menu:"));
    }

    public JMenuItem getClassMenuItem(BClass aClass) {
        JMenu jm = new JMenu("Simple Extension");
        jm.add(new JMenuItem(new SimpleAction("Click Class", "Class menu:")));
        jm.add(new JMenuItem(new EditAction()));
        return jm;
    }

    public JMenuItem getObjectMenuItem(BObject anObject) {
        return new JMenuItem(new SimpleAction("Click Object", "Object menu:"));
    }

    // These methods will be called when
    // each of the different menus are about to be invoked.
    public void notifyPostToolsMenu(BPackage bp, JMenuItem jmi) {
        System.out.println("Post on Tools menu");
        curPackage = bp ; curClass = null ; curObject = null;
    }

    public void notifyPostClassMenu(BClass bc, JMenuItem jmi) {
        System.out.println("Post on Class menu");
        curPackage = null ; curClass = bc ; curObject = null;
    }

    public void notifyPostObjectMenu(BObject bo, JMenuItem jmi) {
        System.out.println("Post on Object menu");
        curPackage = null ; curClass = null ; curObject = bo;
    }

    // A utility method which pops up a dialog detailing the objects
    // involved in the current (SimpleAction) menu invocation.
    private void showCurrentStatus(String header) {
        try {
            if (curObject != null)
                curClass = curObject.getBClass();
            if (curClass != null)
                curPackage = curClass.getPackage();

            String msg = header;
            if (curPackage != null)
                msg += "\nCurrent Package = " + curPackage;
            if (curClass != null)
                msg += "\nCurrent Class = " + curClass;
            if (curObject != null)
                msg += "\nCurrent Object = " + curObject;
            JOptionPane.showMessageDialog(null, msg);
        } catch (Exception exc) { }
    }

    // A method to add a comment at the end of the current class, using the Editor API
    private void addComment() {
        Editor classEditor = null;
        try {
            classEditor = curClass.getEditor();
        } catch (Exception e) { }
        if(classEditor == null) {
            System.out.println("Can't create Editor for " + curClass);
            return;
        }

        int textLen = classEditor.getTextLength();
        TextLocation lastLine = classEditor.getTextLocationFromOffset(textLen);
        lastLine.setColumn(0);
        // The TextLocation now points before the first character of the last line of the current text
        // which we'll assume contains the closing } bracket for the class
        classEditor.setText(lastLine, lastLine, "// Comment added by SimpleExtension\n");
    }

    // The nested class that instantiates the different (simple) menus.
    class SimpleAction extends AbstractAction {
        private String msgHeader;

        public SimpleAction(String menuName, String msg) {
            putValue(AbstractAction.NAME, menuName);
            msgHeader = msg;
        }
        public void actionPerformed(ActionEvent anEvent) {
            showCurrentStatus(msgHeader);
        }
    }

    // And the nested class which implements the editor interaction menu
    class EditAction extends AbstractAction {
        public EditAction() {
            putValue(AbstractAction.NAME, "Add comment");
        }
        public void actionPerformed(ActionEvent e) {
            addComment();
        }
    }
}