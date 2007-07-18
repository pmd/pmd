package net.sourceforge.pmd.jerry.xpath;

import java.util.Iterator;

public interface AxisNavigator {
	Iterator<Object> getAxisIterator(Object node, AxisEnum axis);
}
