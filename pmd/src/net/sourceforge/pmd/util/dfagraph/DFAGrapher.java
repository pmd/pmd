/*
 * Created on 30.06.2004
 */
package net.sourceforge.pmd.util.dfagraph;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

public class DFAGrapher {

    public interface HasLines {
        String getLine(int number);
    }

    private static class SaveListener implements ActionListener {
        public CodePanel codePanel;
        public SaveListener(CodePanel cp) {
            this.codePanel = cp;
        }
        public void actionPerformed(ActionEvent ae) {
            FileWriter fw = null;
            try {
                File f = new File(SETTINGS_FILE_NAME);
                fw = new FileWriter(f);
                fw.write(codePanel.getText());
            } catch (IOException ioe) {
            } finally {
            	try {
	            	if (fw != null)
	            		fw.close();
	            } catch (IOException ioe) {
	            }
            }
        }
    }

    private class CodePanel extends JPanel implements HasLines{
        private JTextPane code = new JTextPane();
        public CodePanel(String text) {
            code.setText(text);
            JScrollPane p = new JScrollPane(code);
            p.setPreferredSize(new Dimension(400,150));
            add(p);
            JButton goButton = new JButton("Go");
            goButton.setMnemonic('g');
            goButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    process();
                }
            });
            goButton.addActionListener(new SaveListener(this));
            add(goButton);
        }
        public String getText() {
            return code.getText();
        }
        public Reader getReader() {
            return new StringReader(code.getText());
        }
        public String getLine(int number) {
            StringTokenizer st = new StringTokenizer(code.getText(), "\n");
            int count = 1;
            while (st.hasMoreTokens()) {
                String tok = st.nextToken();
                if (count == number) {
                    return tok;
                }
                count++;
            }
            throw new RuntimeException("Line number " + number + " not found");
        }
    }

    private static final String SETTINGS_FILE_NAME = System.getProperty("user.home") + System.getProperty("file.separator") + ".pmd_dfagrapher";
    private DFAGraphRule dfaGraphRule;
    private JFrame myFrame;
    private DFAPanel dfaPanel;
    private CodePanel codePanel;

    public DFAGrapher(String code) {
        codePanel = new CodePanel(code);
    }

    public void show() {
        myFrame = new JFrame("DFA grapher");
        myFrame.setSize(600, 800);
        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        myFrame.getContentPane().setLayout(new BorderLayout());
        myFrame.getContentPane().add(codePanel, BorderLayout.NORTH);

        dfaPanel = new DFAPanel(null, codePanel);
        JScrollPane scrollPane = new JScrollPane(dfaPanel);
        myFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        myFrame.setVisible(true);
    }

    private void process() {
        try {
            dfaGraphRule = new DFAGraphRule();
            RuleSet rs = new RuleSet();
            rs.addRule(dfaGraphRule);
            RuleContext ctx = new RuleContext();
            ctx.setSourceCodeFilename("");
            new PMD().processFile(codePanel.getReader(), rs, ctx);

            dfaPanel.resetTo((ASTMethodDeclaration)dfaGraphRule.getMethods().get(0), codePanel);
            dfaPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            System.out.println("Usage: java net.sourceforge.pmd.util.dfagraph.DFAGrapher");
            System.exit(1);
        }
        DFAGrapher dfa = new DFAGrapher(loadText());
        dfa.show();
    }

    private static String loadText() {
        if (!(new File(SETTINGS_FILE_NAME).exists())) {
            return "";
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(SETTINGS_FILE_NAME)));
            StringBuffer text = new StringBuffer();
            String hold;
            while ( (hold = br.readLine()) != null) {
                text.append(hold);
                text.append(System.getProperty("line.separator"));
            }
            return text.toString();
        }   catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
        	try {
	        	if (br != null)
	        		br.close();
	        } catch (IOException e) {
            	e.printStackTrace();
	        }
        }
    }
}
