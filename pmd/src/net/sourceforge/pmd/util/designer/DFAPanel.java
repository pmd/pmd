package net.sourceforge.pmd.util.designer;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;
import net.sourceforge.pmd.util.HasLines;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class DFAPanel extends JPanel implements ListSelectionListener {

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
            SimpleNode n = (SimpleNode)node.jjtGetChild(1);
            return n.getImage();
        }
    }

/*
    private Image offScreenImage = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    private JPanel offScreenPanel = new JPanel();
    private Graphics2D offScreenGraphics = (Graphics2D)offScreenImage.getGraphics();
*/

    private DFACanvas dfaCanvas;
    private JList nodeList;
    private DefaultListModel nodes = new DefaultListModel();
    private JPanel wrapperPanel;

    public DFAPanel() {
        super();

        setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel();
        nodes.addElement("Hit 'Go'");
        nodeList = new JList(nodes);
        nodeList.setFixedCellWidth(100);
        nodeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nodeList.setBorder(BorderFactory.createLineBorder(Color.black));
        nodeList.addListSelectionListener(this);
        leftPanel.add(nodeList);
        add(leftPanel, BorderLayout.WEST);

        dfaCanvas = new DFACanvas();
        JScrollPane scrollPane = new JScrollPane(dfaCanvas);
        wrapperPanel = new JPanel();
        wrapperPanel.add(scrollPane);
        add(wrapperPanel, BorderLayout.EAST);
    }

    public void valueChanged(ListSelectionEvent event) {
        ElementWrapper wrapper = (ElementWrapper)nodeList.getSelectedValue();
        dfaCanvas.setMethod(wrapper.getNode());
        repaint();
/*
        board.renderTo(offScreenGraphics);
        offScreenPanel.getGraphics().drawImage(offScreenImage, 0, 0, offScreenPanel);
*/
    }

    public void resetTo(List newNodes, HasLines lines) {
        dfaCanvas.setCode(lines);
        nodes.clear();
        for (Iterator i = newNodes.iterator(); i.hasNext();) {
            nodes.addElement(new ElementWrapper((ASTMethodDeclaration)i.next()));
        }
        nodeList.setSelectedIndex(0);
        dfaCanvas.setMethod((SimpleNode)newNodes.get(0));
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);
        dfaCanvas.paint(g);
    }
}

