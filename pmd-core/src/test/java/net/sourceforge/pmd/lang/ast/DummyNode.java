/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;

public class DummyNode extends AbstractNode {
    private final boolean findBoundary;
    private final String xpathName;

    private final Map<String, String> attributes = new HashMap<>();

    public DummyNode(int id) {
        this(id, false);
    }

    public DummyNode() {
        this(0);
    }

    public DummyNode(int id, boolean findBoundary) {
        this(id, findBoundary, "dummyNode");
    }

    public DummyNode(int id, boolean findBoundary, String xpathName) {
        super(id);
        this.findBoundary = findBoundary;
        this.xpathName = xpathName;
    }

    public void setXPathAttribute(String name, String value) {
        attributes.put(name, value);
    }

    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {

        List<Attribute> attrs = new ArrayList<>();
        for (String name : attributes.keySet()) {
            attrs.add(new Attribute(this, name, attributes.get(name)));
        }

        return attrs.iterator();
    }

    @Override
    public String toString() {
        return xpathName;
    }

    @Override
    public String getXPathNodeName() {
        return xpathName;
    }

    @Override
    public boolean isFindBoundary() {
        return findBoundary;
    }
}
