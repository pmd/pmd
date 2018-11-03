/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.util.FastStringBuffer;
import net.sf.saxon.tree.util.Navigator.AxisFilter;
import net.sf.saxon.tree.wrapper.AbstractNodeWrapper;
import net.sf.saxon.type.Type;


/**
 * A wrapper for Saxon around a Node.
 */
public final class AstNodeWrapper extends AbstractNodeWrapper {

    private final AstNodeWrapper parent;
    private final Node wrappedNode;
    private final int id;

    private final List<AstNodeWrapper> children;
    private final Map<String, AstAttributeWrapper> attributes;


    public AstNodeWrapper(AstDocument document,
                          IdGenerator idGenerator,
                          AstNodeWrapper parent,
                          Node wrappedNode) {
        this.treeInfo = document;
        this.parent = parent;
        this.wrappedNode = wrappedNode;
        this.id = idGenerator.getNextId();

        this.children = new ArrayList<>(wrappedNode.jjtGetNumChildren());

        for (int i = 0; i < wrappedNode.jjtGetNumChildren(); i++) {
            children.add(new AstNodeWrapper(document, idGenerator, this, wrappedNode.jjtGetChild(i)));
        }

        Map<String, AstAttributeWrapper> atts = new HashMap<>();
        Iterator<Attribute> it = wrappedNode.getXPathAttributesIterator();

        while (it.hasNext()) {
            Attribute next = it.next();
            atts.put(next.getName(), new AstAttributeWrapper(this, next));
        }

        this.attributes = atts;
    }


    @Override
    public AstDocument getTreeInfo() {
        return (AstDocument) super.getTreeInfo();
    }


    @Override
    public Node getUnderlyingNode() {
        return wrappedNode;
    }


    @Override
    public int getColumnNumber() {
        return wrappedNode.getBeginColumn();
    }


    @Override
    public int compareOrder(NodeInfo other) {
        return Integer.compare(id, ((AstNodeWrapper) other).id);
    }


    private <T> AxisIterator mapIterator(Iterator<? extends T> it, Function<? super T, NodeInfo> map, NodeTest nodeTest) {
        AxisIterator axisIterator = new AxisIterator() {
            @Override
            public NodeInfo next() {
                return it.hasNext() ? map.apply(it.next()) : null;
            }


            @Override
            public void close() {
                // nothing to do
            }


            @Override
            public int getProperties() {
                return 0;
            }
        };

        return nodeTest != null ? new AxisFilter(axisIterator, nodeTest) : axisIterator;
    }


    @Override
    protected AxisIterator iterateAttributes(NodeTest nodeTest) {
        return mapIterator(attributes.values().iterator(), Function.identity(), nodeTest);
    }


    @Override
    protected AxisIterator iterateChildren(NodeTest nodeTest) {
        return mapIterator(children.iterator(), Function.identity(), nodeTest);
    }


    private AxisIterator empty() {
        return new AxisIterator() {
            @Override
            public NodeInfo next() {
                return null;
            }


            @Override
            public void close() {
                // nothing to do
            }


            @Override
            public int getProperties() {
                return 0;
            }
        };
    }


    @Override
    protected AxisIterator iterateSiblings(NodeTest nodeTest, boolean forwards) {
        int startIdx = wrappedNode.getIndexInParent() + (forwards ? +1 : -1);

        if (parent == null) {
            return empty();
        }

        AxisIterator siblings = new AxisIterator() {

            int curIdx = startIdx;


            @Override
            public NodeInfo next() {
                if (!forwards && startIdx < 0
                        || forwards && startIdx > parent.wrappedNode.jjtGetNumChildren()) {
                    return null;
                }

                AstNodeWrapper next = parent.children.get(curIdx);
                curIdx += forwards ? +1 : -1;
                return next;
            }


            @Override
            public void close() {
                // nothing to do
            }


            @Override
            public int getProperties() {
                return 0;
            }
        };

        return nodeTest != null ? new AxisFilter(siblings, nodeTest) : siblings;
    }


    private Stream<AstNodeWrapper> streamDescendants() {
        return Stream.concat(Stream.of(this), children.stream().flatMap(AstNodeWrapper::streamDescendants));
    }


    @Override
    public String getAttributeValue(String uri, String local) {
        AstAttributeWrapper attributeWrapper = attributes.get(local);

        return attributeWrapper == null ? null : attributeWrapper.getStringValue();
    }


    @Override
    protected AxisIterator iterateDescendants(NodeTest nodeTest, boolean includeSelf) {
        AxisIterator descendants = mapIterator(streamDescendants().iterator(), Function.identity(), null);

        if (!includeSelf) {
            // skip one
            descendants.next();
        }

        return nodeTest != null ? new AxisFilter(descendants, nodeTest) : descendants;
    }


    @Override
    public int getLineNumber() {
        return wrappedNode.getBeginLine();
    }


    @Override
    public int getNodeKind() {
        return parent == null ? Type.DOCUMENT : Type.ELEMENT;
    }


    @Override
    public NodeInfo getRoot() {
        return getTreeInfo().getRootNode();
    }


    @Override
    public void generateId(FastStringBuffer buffer) {
        buffer.append(Integer.toString(hashCode()));
    }


    public int getId() {
        return id;
    }


    @Override
    public String getLocalPart() {
        return wrappedNode.getXPathNodeName();
    }


    @Override
    public String getURI() {
        return "";
    }


    @Override
    public String getPrefix() {
        return "";
    }


    @Override
    public NodeInfo getParent() {
        return parent;
    }


    @Override
    public CharSequence getStringValueCS() {
        return getStringValue();
    }


    @Override
    public String toString() {
        return "Wrapper[" + getLocalPart() + "]@" + hashCode();
    }
}
