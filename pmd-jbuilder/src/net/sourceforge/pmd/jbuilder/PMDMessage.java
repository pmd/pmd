package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.ide.Message;
import com.borland.primetime.ide.Browser;
import com.borland.primetime.viewer.TextNodeViewer;
import com.borland.primetime.editor.EditorPane;
import com.borland.jbuilder.node.JavaFileNode;

public class PMDMessage extends Message {
    //final LineMark MARK = new HighlightMark();
    JavaFileNode javaNode;
    int line;
    int column;

    /**
     * @param msg  text message
     * @param line line of code to associate this message with
     * @param node the node that the code belongs to
     */
    public PMDMessage(String msg, int line, JavaFileNode node) {
        super(msg);
        this.line = line;
        this.javaNode = node;
    }

    /**
     * Called by JBuilder when user selects a message
     */
    public void selectAction(Browser browser) {
        displayResult(browser, true);
    }

    /**
     * Called by JBuilder when the user double-clicks a message
     */
    public void messageAction(Browser browser) {
        displayResult(browser, true);
    }

    /**
     * Position the code window to the line number that the message is associated with
     *
     * @param browser      JBuilder Browser
     * @param requestFocus whether or not the code window should receive focus
     */
    private void displayResult(Browser browser, boolean requestFocus) {
        try {
            if (requestFocus || browser.isOpenNode(javaNode)) {
                browser.setActiveNode(javaNode, requestFocus);
                TextNodeViewer viewer = (TextNodeViewer) browser.getViewerOfType(javaNode,
                        TextNodeViewer.class);
                browser.setActiveViewer(javaNode, viewer, requestFocus);
                EditorPane editor = viewer.getEditor();
                editor.gotoLine(line, false, EditorPane.CENTER_IF_NEAR_EDGE);
                if (requestFocus) {
                    editor.requestFocus();
                }
                editor.setTemporaryMark(line, new EditorPane.HighlightMark());

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


