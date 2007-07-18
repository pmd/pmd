package net.sourceforge.pmd.jerry;

import net.sourceforge.pmd.jerry.xdm.NodeKind;

/**
 * This interface represents a navigator on a logical XML Document.
 */
public interface DocumentNavigator {
	NodeKind getNodeTypedValue(Object node);
	String getNodeStringValue(Object node);
}
