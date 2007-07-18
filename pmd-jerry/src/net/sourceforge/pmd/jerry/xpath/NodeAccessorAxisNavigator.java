package net.sourceforge.pmd.jerry.xpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.jerry.xdm.NodeAccessor;

public class NodeAccessorAxisNavigator implements AxisNavigator {
	private NodeAccessor nodeAccessor;

	public NodeAccessorAxisNavigator() {
	}

	public NodeAccessorAxisNavigator(NodeAccessor nodeAccessor) {
		this.nodeAccessor = nodeAccessor;
	}

	public NodeAccessor getNodeAccessor() {
		return nodeAccessor;
	}

	public void setNodeAccessor(NodeAccessor nodeAccessor) {
		this.nodeAccessor = nodeAccessor;
	}

	public Iterator<Object> getAxisIterator(Object node, AxisEnum axis) {
		if (nodeAccessor == null) {
			throw new IllegalStateException("NodeAccessor must be not null!");
		}

		Iterator<Object> iterator = null;
		switch (axis) {
		case CHILD:
			iterator = nodeAccessor.getChildren(node);
			break;
		case DESCENDANT:
			// TODO
			break;
		case ATTRIBUTE:
			iterator = nodeAccessor.getAttributes(node);
			break;
		case DESCENDANT_OR_SELF:
			// TODO
			break;
		case FOLLOWING_SIBLING:
			// TODO
			break;
		case FOLLOWING:
			// TODO
			break;
		case NAMESPACE:
			// TODO
			break;
		case PARENT:
			Object parent = nodeAccessor.getParent(node);
			if (parent == null) {
				iterator = Collections.EMPTY_LIST.iterator();
			} else {
				List<Object> list = new ArrayList<Object>(1);
				list.add(parent);
				iterator = list.iterator();
			}
			break;
		case ANCESTOR:
			// TODO
			break;
		case PRECEDING_SIBLING:
			// TODO
			break;
		case PRECEDING:
			// TODO
			break;
		case ANCESTOR_OR_SELF:
			// TODO
			break;
		}

		if (iterator == null) {
			throw new UnsupportedOperationException(
					"Not yet implemented for axis '" + axis + "'");
		}

		return iterator;
	}
}
