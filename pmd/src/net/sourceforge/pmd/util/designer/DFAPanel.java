package net.sourceforge.pmd.util.designer;

import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;
import net.sourceforge.pmd.util.HasLines;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.List;

public class DFAPanel extends JComponent implements ListSelectionListener {

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
                
                // draw index number centered inside of node
                String idx = String.valueOf(inode.getIndex());
                int hack = 4*(idx.length() / 2); // eo - hack to get one and two digit numbers centered
                g.drawString(idx, x + NODE_RADIUS - 2 - hack, y + NODE_RADIUS + 4);

                List access = inode.getVariableAccess();
                if (access != null) {
                    StringBuffer exp = new StringBuffer();
                    for (int k = 0; k < access.size(); k++) {
                        VariableAccess va = (VariableAccess) access.get(k);
                        if (va.isDefinition()) {
                            exp.append("d(");
                        } else if (va.isReference()) {
                            exp.append("r(");
                        } else if (va.isUndefinition()) {
                            exp.append("u(");
                            //continue;  // eo - the u() entries add a lot of clutter to the report
                        } else {
                            exp.append("?(");
                        }
                        exp.append(va.getVariableName() + "), ");
                    }
                    g.drawString(exp.toString(), x + 70, y + 15);
                }

                for (int j = 0; j < inode.getChildren().size(); j++) {
                    IDataFlowNode n = (IDataFlowNode) inode.getChildren().get(j);
                    this.drawMyLine(inode.getIndex(), n.getIndex(), g);
                    String output = (j == 0 ? "" : ",") + String.valueOf(n.getIndex());
                    g.drawString(output, x - 3 * NODE_DIAMETER + (j * 20), y + NODE_RADIUS - 2);
                }
            }
        }

        public void setCode(HasLines h) {
            this.lines = h;
        }

        public void setMethod(SimpleNode node) {
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

    private static class ElementWrapper {
        private ASTMethodDeclaration node;
        public ElementWrapper(ASTMethodDeclaration node) {
            this.node = node;
        }
        public ASTMethodDeclaration getNode() {
            return node;
        }
        public String toString() {
            return node.getMethodName();
        }
    }

    private DFACanvas dfaCanvas;
    private JList nodeList;
    private DefaultListModel nodes = new DefaultListModel();
    private JPanel wrapperPanel;

    public DFAPanel() {
        super();

        setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel();
        nodeList = new JList(nodes);
        nodeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nodeList.setFixedCellWidth(150);
        nodeList.setBorder(BorderFactory.createLineBorder(Color.black));
        nodeList.addListSelectionListener(this);
        leftPanel.add(nodeList);
        add(leftPanel, BorderLayout.WEST);

        dfaCanvas = new DFACanvas();
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(800, 450));  // eo - it would be better to calculate the size based on the containing object's size
        dfaCanvas.setSize(2000,4000);  // eo - these seem to give a big enough canvas
        scrollPane.add(dfaCanvas);
        wrapperPanel = new JPanel();
        wrapperPanel.add(scrollPane);
        wrapperPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        add(wrapperPanel, BorderLayout.EAST);
    }

    public void valueChanged(ListSelectionEvent event) {
        ElementWrapper wrapper = null;
        if (nodes.size() == 1) {
            wrapper = (ElementWrapper) nodes.get(0);
        } else if (nodes.isEmpty()) {
            return;
        } else if (nodeList.getSelectedValue() == null) {
            wrapper = (ElementWrapper) nodes.get(0);
        } else {
            wrapper = (ElementWrapper) nodeList.getSelectedValue();
        }
        dfaCanvas.setMethod(wrapper.getNode());
        dfaCanvas.repaint();
    }

    public void resetTo(List newNodes, HasLines lines) {
        dfaCanvas.setCode(lines);
        nodes.clear();
        for (Iterator i = newNodes.iterator(); i.hasNext();) {
            nodes.addElement(new ElementWrapper((ASTMethodDeclaration) i.next()));
        }
        nodeList.setSelectedIndex(0);
        dfaCanvas.setMethod((SimpleNode) newNodes.get(0));
        repaint();
    }
}

