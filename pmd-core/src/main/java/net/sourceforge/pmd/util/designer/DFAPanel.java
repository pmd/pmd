/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.designer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DFAGraphMethod;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.VariableAccess;
import net.sourceforge.pmd.util.StringUtil;

public class DFAPanel extends JComponent implements ListSelectionListener {

    public static class DFACanvas extends JPanel {

	private static final int NODE_RADIUS = 12;
	private static final int NODE_DIAMETER = 2 * NODE_RADIUS;

	private Node node;

	private int x = 150;
	private int y = 50;
	private LineGetter lines;

	private void addAccessLabel(StringBuffer sb, VariableAccess va) {

	    if (va.isDefinition()) {
		sb.append("d(");
	    } else if (va.isReference()) {
		sb.append("r(");
	    } else if (va.isUndefinition()) {
		sb.append("u(");
		//continue;  // eo - the u() entries add a lot of clutter to the report
	    } else {
		sb.append("?(");
	    }

	    sb.append(va.getVariableName()).append(')');
	}

	private String childIndicesOf(DataFlowNode node, String separator) {

	    List<DataFlowNode> kids = node.getChildren();
	    if (kids.isEmpty()) {
		return "";
	    }

	    StringBuffer sb = new StringBuffer();
	    sb.append(kids.get(0).getIndex());

	    for (int j = 1; j < node.getChildren().size(); j++) {
		sb.append(separator);
		sb.append(kids.get(j).getIndex());
	    }
	    return sb.toString();
	}

	private String[] deriveAccessLabels(List<DataFlowNode> flow) {

        if (flow == null || flow.isEmpty()) {
            return StringUtil.getEmptyStrings();
        }

	    String[] labels = new String[flow.size()];

	    for (int i = 0; i < labels.length; i++) {
		List<VariableAccess> access = flow.get(i).getVariableAccess();

		if (access == null || access.isEmpty()) {
		    continue; // leave a null at this slot
		}

		StringBuffer exp = new StringBuffer();
		addAccessLabel(exp, access.get(0));

		for (int k = 1; k < access.size(); k++) {
		    exp.append(", ");
		    addAccessLabel(exp, access.get(k));
		}

		labels[i] = exp.toString();
	    }
	    return labels;
	}

	private int maxWidthOf(String[] strings, FontMetrics fm) {

	    int max = 0;
	    String str;

	    for (String element : strings) {
		str = element;
		if (str == null) {
		    continue;
		}
		max = Math.max(max, SwingUtilities.computeStringWidth(fm, str));
	    }
	    return max;
	}

	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);

	    if (node == null) {
		return;
	    }

	    List<DataFlowNode> flow = node.getDataFlowNode().getFlow();
	    FontMetrics fm = g.getFontMetrics();
	    int halfFontHeight = fm.getAscent() / 2;

	    String[] accessLabels = deriveAccessLabels(flow);
	    int maxAccessLabelWidth = maxWidthOf(accessLabels, fm);

	    for (int i = 0; i < flow.size(); i++) {
		DataFlowNode inode = flow.get(i);

		y = computeDrawPos(inode.getIndex());

		g.drawArc(x, y, NODE_DIAMETER, NODE_DIAMETER, 0, 360);
		g.drawString(lines.getLine(inode.getLine()), x + 100 + maxAccessLabelWidth, y + 15);

		// draw index number centered inside of node
		String idx = String.valueOf(inode.getIndex());
		int halfWidth = SwingUtilities.computeStringWidth(fm, idx) / 2;
		g.drawString(idx, x + NODE_RADIUS - halfWidth, y + NODE_RADIUS + halfFontHeight);

		String accessLabel = accessLabels[i];
		if (accessLabel != null) {
		    g.drawString(accessLabel, x + 70, y + 15);
		}

		for (int j = 0; j < inode.getChildren().size(); j++) {
		    DataFlowNode n = inode.getChildren().get(j);
		    drawMyLine(inode.getIndex(), n.getIndex(), g);
		}
		String childIndices = childIndicesOf(inode, ", ");
		g.drawString(childIndices, x - 3 * NODE_DIAMETER, y + NODE_RADIUS - 2);
	    }
	}

	public void setCode(LineGetter h) {
	    this.lines = h;
	}

	public void setMethod(Node node) {
	    this.node = node;
	}

	private int computeDrawPos(int index) {
	    int z = NODE_RADIUS * 4;
	    return z + index * z;
	}

	private void drawArrow(Graphics g, int x, int y, int direction) {

	    final int height = NODE_RADIUS * 2 / 3;
	    final int width = NODE_RADIUS * 2 / 3;

	    switch (direction) {
	    case SwingConstants.NORTH:
		g.drawLine(x, y, x - width / 2, y + height);
		g.drawLine(x, y, x + width / 2, y + height);
		break;
	    case SwingConstants.SOUTH:
		g.drawLine(x, y, x - width / 2, y - height);
		g.drawLine(x, y, x + width / 2, y - height);
		break;
	    case SwingConstants.EAST:
		g.drawLine(x, y, x - height, y - width / 2);
		g.drawLine(x, y, x - height, y + width / 2);
		break;
	    case SwingConstants.WEST:
		g.drawLine(x, y, x + height, y - width / 2);
		g.drawLine(x, y, x + height, y + width / 2);
		break;
	    default:
		// Do nothing
		break;
	    }
	}

        private void drawMyLine(int index1, int index2, Graphics g) {
            int y1 = this.computeDrawPos(index1);
            int y2 = this.computeDrawPos(index2);

            if (index1 < index2) {
                if (index2 - index1 == 1) {
                    x += NODE_RADIUS;
                    g.drawLine(x, y1 + NODE_DIAMETER, x, y2);
                    drawArrow(g, x, y2, SwingConstants.SOUTH);
                    x -= NODE_RADIUS;
                } else if (index2 - index1 > 1) {
                    y1 = y1 + NODE_RADIUS;
                    y2 = y2 + NODE_RADIUS;
                    int n = (index2 - index1 - 2) * 10 + 10;
                    g.drawLine(x, y1, x - n, y1);
                    g.drawLine(x - n, y1, x - n, y2);
                    g.drawLine(x - n, y2, x, y2);
                    drawArrow(g, x, y2, SwingConstants.EAST);
                }

            } else {
                if (index1 - index2 > 1) {
                    y1 = y1 + NODE_RADIUS;
                    y2 = y2 + NODE_RADIUS;
                    x = x + NODE_DIAMETER;
                    int n = (index1 - index2 - 2) * 10 + 10;
                    g.drawLine(x, y1, x + n, y1);
                    g.drawLine(x + n, y1, x + n, y2);
                    g.drawLine(x + n, y2, x, y2);
                    drawArrow(g, x, y2, SwingConstants.WEST);
                    x = x - NODE_DIAMETER;
                } else if (index1 - index2 == 1) {
                    y2 = y2 + NODE_DIAMETER;
                    g.drawLine(x + NODE_RADIUS, y2, x + NODE_RADIUS, y1);
                    drawArrow(g, x + NODE_RADIUS, y2, SwingConstants.NORTH);
                }
            }
        }
    }

    private static class ElementWrapper {
	private DFAGraphMethod node;

	public ElementWrapper(DFAGraphMethod node) {
	    this.node = node;
	}

	public DFAGraphMethod getNode() {
	    return node;
	}

	@Override
	public String toString() {
	    return node.getName();
	}
    }

    private DFACanvas dfaCanvas;
    private JList nodeList;
    private DefaultListModel nodes = new DefaultListModel();

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
	dfaCanvas.setBackground(Color.WHITE);
	dfaCanvas.setPreferredSize(new Dimension(900, 1400));

	JScrollPane scrollPane = new JScrollPane(dfaCanvas);

	add(scrollPane, BorderLayout.CENTER);
    }

    @Override
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

    public void resetTo(List<DFAGraphMethod> newNodes, LineGetter lines) {
	dfaCanvas.setCode(lines);
	nodes.clear();
	for (DFAGraphMethod md : newNodes) {
	    nodes.addElement(new ElementWrapper(md));
	}
	nodeList.setSelectedIndex(0);
	dfaCanvas.setMethod(newNodes.get(0));
	repaint();
    }
}
