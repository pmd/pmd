
package net.sourceforge.pmd.lang.vm.ast;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.lang3.text.StrBuilder;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;

/**
 *
 */
public class AbstractVmNode extends AbstractJjtreeNode<VmNode> implements VmNode {

    /** */
    // TODO - It seems that this field is only valid when parsing, and should
    // not be kept around.
    protected VmParser parser;

    /** */
    protected int info; // added

    /** */
    public boolean state;

    /** */
    protected boolean invalid = false;

    /** */
    protected Token first;

    /** */
    protected Token last;

    protected String templateName;

    /**
     * @param i
     */
    public AbstractVmNode(final int i) {
        super(i);
    }

    /**
     * @param p
     * @param i
     */
    public AbstractVmNode(final VmParser p, final int i) {
        this(i);
        parser = p;
        templateName = parser.currentTemplateName;
    }

    @Override
    public void jjtOpen() {
        first = parser.getToken(1); // added
        if (beginLine == -1 && parser.token.next != null) {
            beginLine = parser.token.next.beginLine;
            beginColumn = parser.token.next.beginColumn;
        }
    }

    @Override
    public void jjtClose() {
        last = parser.getToken(0); // added
        if (beginLine == -1 && (children == null || children.length == 0)) {
            beginColumn = parser.token.beginColumn;
        }
        if (beginLine == -1) {
            beginLine = parser.token.beginLine;
        }
        endLine = parser.token.endLine;
        endColumn = parser.token.endColumn;
    }

    @InternalApi
    @Deprecated
    public void setFirstToken(final Token t) {
        this.first = t;
    }

    public Token getFirstToken() {
        return first;
    }

    public Token getLastToken() {
        return last;
    }

    @Override
    public Object jjtAccept(final VmParserVisitor visitor, final Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object childrenAccept(final VmParserVisitor visitor, final Object data) {
        for (VmNode c : children()) {
            c.jjtAccept(visitor, data);
        }
        return data;
    }


    @Override
    public String getXPathNodeName() {
        return VmParserTreeConstants.jjtNodeName[id];
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to
     * customize the way the node appears when the tree is dumped. If your
     * output uses more than one line you should override toString(String),
     * otherwise overriding toString() is probably all you need to do.
     */



    /**
     * @param prefix
     * @return String representation of this node.
     */
    public String toString(final String prefix) {
        return prefix + toString();
    }

    /**
     * Override this method if you want to customize how the node dumps out its
     * children.
     *
     * @param prefix
     * @deprecated This method will be removed with PMD 7. The rule designer is a better way to inspect nodes.
     */
    @Deprecated
    public void dump(final String prefix, final boolean recurse, final Writer writer) {
        final PrintWriter printWriter = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
        printWriter.println(toString(prefix));
        if (children != null && recurse) {
            for (int i = 0; i < children.length; ++i) {
                final AbstractVmNode n = (AbstractVmNode) children[i];
                if (n != null) {
                    n.dump(prefix + " ", recurse, printWriter);
                }
            }
        }
    }

    // All additional methods

    /*
     * see org.apache.velocity.runtime.parser.node.Node#literal()
     */
    public String literal() {
        // if we have only one string, just return it and avoid
        // buffer allocation. VELOCITY-606
        if (first != null && first.equals(last)) {
            return NodeUtils.tokenLiteral(first);
        }

        Token t = first;
        final StrBuilder sb = new StrBuilder(NodeUtils.tokenLiteral(t));
        while (t != null && !t.equals(last)) {
            t = t.next;
            sb.append(NodeUtils.tokenLiteral(t));
        }
        return sb.toString();
    }

    /*
     * see org.apache.velocity.runtime.parser.node.Node#getType()
     */
    public int getType() {
        return id;
    }

    /*
     * see org.apache.velocity.runtime.parser.node.Node#setInfo(int)
     */
    public void setInfo(final int info) {
        this.info = info;
    }

    /*
     * see org.apache.velocity.runtime.parser.node.Node#getInfo()
     */
    public int getInfo() {
        return info;
    }

    /*
     * see org.apache.velocity.runtime.parser.node.Node#setInvalid()
     */
    public void setInvalid() {
        invalid = true;
    }

    /*
     * see org.apache.velocity.runtime.parser.node.Node#isInvalid()
     */
    public boolean isInvalid() {
        return invalid;
    }

    /*
     * see org.apache.velocity.runtime.parser.node.Node#getLine()
     */
    public int getLine() {
        return first.beginLine;
    }

    /*
     * see org.apache.velocity.runtime.parser.node.Node#getColumn()
     */
    public int getColumn() {
        return first.beginColumn;
    }

    public String getTemplateName() {
        return templateName;
    }
}
