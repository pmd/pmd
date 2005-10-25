package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.editor.EditorPane;
import com.borland.primetime.editor.LineMark;
import com.borland.primetime.ide.Browser;
import com.borland.primetime.ide.Message;
import com.borland.primetime.node.FileNode;
import com.borland.primetime.vfs.Url;
import com.borland.primetime.viewer.TextNodeViewer;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Wrapper for the OpenTools message object
 */
public class CPDMessage extends Message {
    final static LineMark MARK = new EditorPane.HighlightMark();
    static Font PARENT_FONT = new Font("SansSerif", Font.BOLD, 12);
    static Font CHILD_FONT = new Font("SansSerif", Font.PLAIN, 12);
    String filename;
    FileNode javaNode = null;
    int startline = 0;
    int lineCount = 0;
    int column = 0;
    boolean isParent = true;
    ArrayList childMessages = new ArrayList();
    String codeBlock = null;

    private CPDMessage(String msg, String codeBlock) {
        super(msg);
        this.codeBlock = codeBlock;
        this.setLazyFetchChildren(true);
    }

    private CPDMessage(String msg, int startline, int lineCount, String fileName) {
        super(msg);
        this.startline = startline;
        this.lineCount = lineCount;
        this.filename = fileName;
        try {
            File javaFile = new File(fileName);
            javaNode = Browser.getActiveBrowser().getActiveProject().findNode(new Url(javaFile));
        } catch (Exception e) {
            Browser.getActiveBrowser().getMessageView().addMessage(Constants.MSGCAT_TEST, e.toString());
        }
    }

    public static CPDMessage createMessage(String msg, String codeBlock) {
        CPDMessage cpdm = new CPDMessage(msg, codeBlock);
        cpdm.isParent = true;
        cpdm.setFont(PARENT_FONT);
        return cpdm;
    }


    public void addChildMessage(int startline, int endline, String fileName) {
        this.lazyFetchChildren = true;
        String msg = PMDOpenTool.getFileName(fileName) + ": duplicate code starts at line " + String.valueOf(startline);
        CPDMessage cpdmsg = new CPDMessage(msg, startline, endline, fileName);
        cpdmsg.isParent = false;
        cpdmsg.setFont(CHILD_FONT);
        childMessages.add(cpdmsg);

    }

    public void fetchChildren(Browser browser) {
        CodeFragmentMessage cfm = new CodeFragmentMessage(this.codeBlock);
        browser.getMessageView().addMessage(PMDOpenTool.cpdCat, this, cfm);
        for (Iterator iter = childMessages.iterator(); iter.hasNext();) {
            browser.getMessageView().addMessage(PMDOpenTool.cpdCat, this, (CPDMessage) iter.next());
        }
    }

    /**
     * Called by JBuilder when user selects a message
     *
     * @param browser JBuilder Browser
     */
    public void selectAction(Browser browser) {
        displayResult(browser, true);
    }

    /**
     * Called by JBuilder when the user double-clicks a message
     *
     * @param browser JBuilder Browser
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
        MARK.removeEditor();
        if (!isParent) {
            try {
                if (requestFocus || browser.isOpenNode(javaNode)) {
                    browser.setActiveNode(javaNode, requestFocus);
                    TextNodeViewer viewer = (TextNodeViewer) browser.getViewerOfType(javaNode,
                            TextNodeViewer.class);
                    browser.setActiveViewer(javaNode, viewer, requestFocus);
                    EditorPane editor = viewer.getEditor();
                    editor.gotoLine(startline, false, EditorPane.CENTER_IF_NEAR_EDGE);
                    if (requestFocus) {
                        editor.requestFocus();
                    }
                    /*EditorDocument ed = (EditorDocument)editor.getDocument();
                    int[] lines = new int[lineCount];
                    for (int i=0; i<lineCount; i++)
                        lines[i] = startline+i-1;
                    ed.setLightweightLineMarks(lines, MARK);*/
                    editor.setTemporaryMark(startline, MARK);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

