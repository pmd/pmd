package net.sourceforge.pmd.util.designer;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;
import net.sourceforge.pmd.util.HasLines;

import javax.swing.*;
import java.awt.Graphics;

public class DFAPanel extends JPanel {

    private SimpleNode node;
    private int x = 150;
    private int y = 50;
    private int radius = 10;
    private int d = 2 * radius;
    private int height;
    private HasLines lines;

    public DFAPanel(SimpleNode node, HasLines lines) {
        super();
        if (node == null) {
            return;
        }
        this.lines = lines;
        this.node = node;
    }

    public void resetTo(SimpleNode node, HasLines lines) {
        this.lines = lines;
        this.node = node;
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (node == null) {
            return;
        }
        java.util.List flow = node.getDataFlowNode().getFlow();
        for (int i = 0; i < flow.size(); i++) {
            IDataFlowNode inode = (IDataFlowNode) flow.get(i);

            y = this.computeDrawPos(inode.getIndex());

            g.drawArc(x, y, d, d, 0, 360);
            if (height < y) height = y;

            g.drawString(lines.getLine(inode.getLine()), x + 200, y + 15);
            g.drawString(String.valueOf(inode.getIndex()), x + radius-2, y + radius+4);

            String exp = "";
            java.util.List access = inode.getVariableAccess();
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
                g.drawString(output, x - 3 * d + (j * 20), y + radius - 2);
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
}

