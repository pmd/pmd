package net.sourceforge.pmd.util.designer;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;
import net.sourceforge.pmd.util.HasLines;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Canvas;
import java.util.List;
import java.util.Iterator;

public class DFAPanel extends JPanel {

    public static class DFACanvas extends Canvas {

        private static final int NODE_RADIUS = 10;
        private static final int NODE_DIAMETER = 2 * NODE_RADIUS;

        private SimpleNode node;

        private int x = 150;
        private int y = 50;
        private HasLines lines;

        public void paint(Graphics g) {
            super.paint(g);
            if (node == null) {
                return;
            }
            List flow = node.getDataFlowNode().getFlow();
            for (int i = 0; i < flow.size(); i++) {
                IDataFlowNode inode = (IDataFlowNode) flow.get(i);

                y = computeDrawPos(inode.getIndex());

                g.drawArc(x, y, NODE_DIAMETER, NODE_DIAMETER, 0, 360);

                g.drawString(lines.getLine(inode.getLine()), x + 200, y + 15);
                g.drawString(String.valueOf(inode.getIndex()), x + NODE_RADIUS-2, y + NODE_RADIUS+4);

                String exp = "";
                List access = inode.getVariableAccess();
                if (access != null) {
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
                    String output = (j==0 ? "" : "," ) + String.valueOf(n.getIndex());
                    g.drawString(output, x - 3 * NODE_DIAMETER + (j * 20), y + NODE_RADIUS - 2);
                }
            }
        }

        public void setMethod(SimpleNode node, HasLines lines) {
            this.lines = lines;
            this.node = node;
        }

        private int computeDrawPos(int index) {
            int z = NODE_RADIUS * 4;
            return z + index * z;
        }

        private void drawMyLine(int index1, int index2, Graphics g) {
            int y1 = this.computeDrawPos(index1);
            int y2 = this.computeDrawPos(index2);

            int arrow = 3;

            if (index1 < index2) {
                if (index2 - index1 == 1) {
                    x += NODE_RADIUS;
                    g.drawLine(x, y1 + NODE_DIAMETER, x, y2);
                    g.fillRect(x - arrow, y2 - arrow, arrow * 2, arrow * 2);
                    x -= NODE_RADIUS;
                } else if (index2 - index1 > 1) {
                    y1 = y1 + NODE_RADIUS;
                    y2 = y2 + NODE_RADIUS;
                    int n = ((index2 - index1 - 2) * 10) + 10;
                    g.drawLine(x, y1, x - n, y1);
                    g.drawLine(x - n, y1, x - n, y2);
                    g.drawLine(x - n, y2, x, y2);
                    g.fillRect(x - arrow, y2 - arrow, arrow * 2, arrow * 2);
                }

            } else {
                if (index1 - index2 > 1) {
                    y1 = y1 + NODE_RADIUS;
                    y2 = y2 + NODE_RADIUS;
                    x = x + NODE_DIAMETER;
                    int n = ((index1 - index2 - 2) * 10) + 10;
                    g.drawLine(x, y1, x + n, y1);
                    g.drawLine(x + n, y1, x + n, y2);
                    g.drawLine(x + n, y2, x, y2);
                    g.fillRect(x - arrow, y2 - arrow, arrow * 2, arrow * 2);
                    x = x - NODE_DIAMETER;
                } else if (index1 - index2 == 1) {
                    y2 = y2 + NODE_DIAMETER;
                    g.drawLine(x + NODE_RADIUS, y2, x + NODE_RADIUS, y1);
                    g.fillRect(x + NODE_RADIUS - arrow, y2 - arrow, arrow * 2, arrow * 2);
                }
            }
        }
    }

    private DFACanvas dfaCanvas;
    private DefaultListModel nodes = new DefaultListModel();

    public DFAPanel() {
        super();
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        nodes.addElement("FOOO");
        nodes.addElement("FOOO");
        nodes.addElement("FOOO");
        nodes.addElement("FOOO");
        JList nodeList = new JList(nodes);
        nodeList.setFixedCellWidth(300);
        nodeList.setBorder(BorderFactory.createLineBorder(Color.black));
        leftPanel.add(nodeList);
        add(leftPanel, BorderLayout.WEST);

        dfaCanvas = new DFACanvas();
        JScrollPane scrollPane = new JScrollPane(dfaCanvas);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
        add(scrollPane, BorderLayout.EAST);
    }

    public void resetTo(List nodes, HasLines lines) {
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            SimpleNode n = (SimpleNode)i.next();
            System.out.println("image = " + ((SimpleNode)n.jjtGetChild(1)).getImage());
        }
        dfaCanvas.setMethod((SimpleNode)nodes.get(0), lines);
    }

    public void paint(Graphics g) {
        super.paint(g);
        dfaCanvas.paint(g);
    }
}

