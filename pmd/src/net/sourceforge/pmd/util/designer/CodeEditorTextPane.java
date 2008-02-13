package net.sourceforge.pmd.util.designer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.JTextPane;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.LineGetter;

public class CodeEditorTextPane extends JTextPane implements LineGetter, ActionListener {

    private static final String SETTINGS_FILE_NAME = System.getProperty("user.home") + System.getProperty("file.separator") + ".pmd_designer";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public CodeEditorTextPane() {
        setText(loadCode());
    }

    public String getLine(int number) {
        int count = 1;
        for (StringTokenizer st = new StringTokenizer(getText(), "\n"); st.hasMoreTokens();) {
            String tok = st.nextToken();
            if (count == number) {
                return tok;
            }
            count++;
        }
        throw new RuntimeException("Line number " + number + " not found");
    }

    private int getPosition(String[] lines, int line, int column) {
        int pos = 0;
        for (int count = 0; count < lines.length;) {
            String tok = lines[count++];
            if (count == line) {
                int linePos = 0;
                int i;
                for (i = 0; linePos < column; i++) {
                    linePos++;
                    if (tok.charAt(i) == '\t') {
                        linePos--;
                        linePos += (8 - (linePos & 07));
                    }
                }

                return pos + i - 1;
            }
            pos += tok.length() + 1;
        }
        throw new RuntimeException("Line " + line + " not found");
    }

    public void select(SimpleNode node) {
        String[] lines = getText().split(LINE_SEPARATOR);
        setSelectionStart(getPosition(lines, node.getBeginLine(), node.getBeginColumn()));
        setSelectionEnd(getPosition(lines, node.getEndLine(), node.getEndColumn())+1);
        requestFocus();
    }

    public void actionPerformed(ActionEvent ae) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File(SETTINGS_FILE_NAME));
            fw.write(getText());
        } catch (IOException ioe) {
        } finally {
            try {
                if (fw != null)
                    fw.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private String loadCode() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(SETTINGS_FILE_NAME)));
            StringBuffer text = new StringBuffer();
            String hold;
            while ((hold = br.readLine()) != null) {
                text.append(hold).append(LINE_SEPARATOR);
            }
            return text.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
