package net.sourceforge.pmd.jbuilder;

import com.borland.primetime.ide.Browser;
import com.borland.primetime.ide.Message;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.StringReader;

public class CodeFragmentMessage extends Message {
    String codeFragment = null;
    static Font CODE_FONT = new Font("Monospaced", Font.ITALIC, 12);
     public CodeFragmentMessage(String codeFragment) {
         super("View Code");
        this.setLazyFetchChildren(true);
        this.codeFragment = codeFragment;
    }
    public void fetchChildren(Browser browser) {
        BufferedReader reader = new BufferedReader(new StringReader(codeFragment));
        try {
            String line = reader.readLine();
            while (line != null) {
                Message msg = new Message(line);
                msg.setFont(CODE_FONT);
                browser.getMessageView().addMessage(PMDOpenTool.cpdCat, this, msg);
                line = reader.readLine();
            }
        } catch (Exception e) {
        }
     }
}