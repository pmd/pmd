/*
 * Created on 30.06.2004
 */
package net.sourceforge.pmd.util;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.DataFlowFacade;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;
import net.sourceforge.pmd.symboltable.SymbolFacade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author raik
 */
public class DFAGrapher extends JFrame {

    public static class DisplayDataFlowRule extends AbstractRule {
        public Object visit(ASTMethodDeclaration node, Object data) {
            super.visit(node, data);
            RuleContext rc = (RuleContext) data;
            new DFAGrapher(node, rc.getSourceCodeFilename());
            return data;
        }
    }

    private class MyPanel extends JPanel {
        private SimpleNode node;
        private int x = 150;
        private int y = 50;
        private int radius = 10;
        private int d = 2 * radius;
        private String sourceFile;
        private int height;

        public MyPanel(SimpleNode node, String sourceFile) {
            super();
            this.node = node;
            this.sourceFile = sourceFile;
        }

        public void paint(Graphics g) {
            this.setPreferredSize(new Dimension(600, height + 100));
            super.paint(g);
            if (node == null) {
                System.out.println("node == null");
                return;
            }
            List flow = node.getDataFlowNode().getFlow();
            IDataFlowNode inode;

            for (int i = 0; i < flow.size(); i++) {
                inode = (IDataFlowNode) flow.get(i);

                y = this.computeDrawPos(inode.getIndex());

                g.drawArc(x, y, d, d, 0, 360);
                if (height < y) height = y;

                g.drawString(this.getLine(this.sourceFile, inode.getLine()), x + 200, y + 15);
                g.drawString(String.valueOf(inode.getIndex()), x + radius, y + radius);

                String exp = "";
                List access = inode.getVariableAccess();
                if (access != null) {

                    //System.out.println(access);
                    for (int k = 0; k < access.size(); k++) {
                        VariableAccess va = (VariableAccess) access.get(k);
                        switch (va.getAccessType()) {
                            case VariableAccess.DEFINITION:
                                exp += "d(";
                                break;
                            case VariableAccess.REFERENCING:
                                exp += "r(";
                                break;
                            case VariableAccess.UNDEFINITION:
                                exp += "u(";
                                break;
                            default:
                                exp += "?(";
                        }
                        exp += va.getVariableName() + "), ";
                    }
                    g.drawString(exp, x + 70, y + 15);
                }

                for (int j = 0; j < inode.getChildren().size(); j++) {
                    IDataFlowNode n = (IDataFlowNode) inode.getChildren().get(j);
                    this.drawMyLine(inode.getIndex(), n.getIndex(), g);
                    g.drawString("," + String.valueOf(n.getIndex()), x - 3 * d + (j * 20), y + radius - 2);
                }
            }
        }

        private int computeDrawPos(int index) {
            int z = radius * 4;
            return z + index * z;
        }

        private void drawMyLine(int index1, int index2, Graphics g) {
            int y1 = this.computeDrawPos(index1);
            int y2 = this.computeDrawPos(index2);

            int arrow = 3;

            //System.out.println(y1 + "|" + y2);

            if (index1 < index2) {
                if (index2 - index1 == 1) {
                    x += radius;
                    g.drawLine(x, y1 + d, x, y2);
                    g.fillRect(x - arrow, y2 - arrow, arrow * 2, arrow * 2);
                    x -= radius;
                } else if (index2 - index1 > 1) {
                    y1 = y1 + radius;
                    y2 = y2 + radius;
                    int n = ((index2 - index1 - 2) * 10) + 10;
                    g.drawLine(x, y1, x - n, y1);
                    g.drawLine(x - n, y1, x - n, y2);
                    g.drawLine(x - n, y2, x, y2);
                    g.fillRect(x - arrow, y2 - arrow, arrow * 2, arrow * 2);
                }

            } else {
                if (index1 - index2 > 1) {
                    y1 = y1 + radius;
                    y2 = y2 + radius;
                    x = x + this.d;
                    int n = ((index1 - index2 - 2) * 10) + 10;
                    g.drawLine(x, y1, x + n, y1);
                    g.drawLine(x + n, y1, x + n, y2);
                    g.drawLine(x + n, y2, x, y2);
                    g.fillRect(x - arrow, y2 - arrow, arrow * 2, arrow * 2);
                    x = x - this.d;
                } else if (index1 - index2 == 1) {
                    y2 = y2 + this.d;
                    g.drawLine(x + radius, y2, x + radius, y1);
                    g.fillRect(x + radius - arrow, y2 - arrow, arrow * 2, arrow * 2);
                }
            }
        }

        // copied from Papari Renderer
        /**
         * Retrieves the requested line from the specified file.
         *
         * @param sourceFile the java or cpp source file
         * @param line       line number to extract
         * @return a trimmed line of source code
         */
        private String getLine(String sourceFile, int line) {
            String code = null;
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(sourceFile)));
                for (int i = 0; line > i; i++) {
                    code = br.readLine().trim();
                }
                br.close();
            } catch (IOException ioErr) {
                ioErr.printStackTrace();
            }
            return code;
        }
    }

    public DFAGrapher(SimpleNode node, String sourceFile) {
        this.setSize(600, 800);
        this.setTitle("DFA graph for " + sourceFile);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        MyPanel p = new MyPanel(node, sourceFile);
        JScrollPane scrollPane = new JScrollPane(p);
        this.getContentPane().add(scrollPane);
        this.setVisible(true);
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java net.sourceforge.pmd.util.DFAGrapher /home/tom/tmp/Foo.java");
            System.exit(1);
        }
        try {
            Reader r = new FileReader(args[0]);
            JavaParser parser = new TargetJDK1_4().createParser(r);
            ASTCompilationUnit c = parser.CompilationUnit();
            SymbolFacade stb = new SymbolFacade();
            stb.initializeWith(c);
            DataFlowFacade dff = new DataFlowFacade();
            dff.initializeWith(c);
            List acus = new ArrayList();
            acus.add(c);
            RuleSet rs = new RuleSet();
            rs.addRule(new DisplayDataFlowRule());
            RuleContext ctx = new RuleContext();
            ctx.setSourceCodeFilename(args[0]);
            rs.apply(acus, ctx);
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
