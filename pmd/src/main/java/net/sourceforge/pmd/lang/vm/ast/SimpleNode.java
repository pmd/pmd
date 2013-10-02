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

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.text.StrBuilder;

/**
 *
 */
public class SimpleNode extends AbstractNode implements Node {

    /** */
    // TODO - It seems that this field is only valid when parsing, and should not be kept around.
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
    public SimpleNode(final int i) {
        super(i);
    }

    /**
     * @param p
     * @param i
     */
    public SimpleNode(final VmParser p, final int i) {
        this(i);
        parser = p;
        templateName = parser.currentTemplateName;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#jjtOpen()
     */
    @Override
    public void jjtOpen() {
        first = parser.getToken(1); // added
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#jjtClose()
     */
    @Override
    public void jjtClose() {
        last = parser.getToken(0); // added
    }

    /**
     * @param t
     */
    public void setFirstToken(final Token t) {
        this.first = t;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#getFirstToken()
     */
    public Token getFirstToken() {
        return first;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#getLastToken()
     */
    public Token getLastToken() {
        return last;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#jjtAccept(org.apache.velocity.runtime.parser.node.VmParserVisitor,
     *      java.lang.Object)
     */
    public Object jjtAccept(final VmParserVisitor visitor, final Object data) {
        return visitor.visit(this, data);
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#childrenAccept(org.apache.velocity.runtime.parser.node.VmParserVisitor,
     *      java.lang.Object)
     */
    public Object childrenAccept(final VmParserVisitor visitor, final Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                ((SimpleNode) children[i]).jjtAccept(visitor, data);
            }
        }
        return data;
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to customize the way the node appears when the
     * tree is dumped. If your output uses more than one line you should override toString(String), otherwise overriding
     * toString() is probably all you need to do.
     */

    // public String toString()
    // {
    // return ParserTreeConstants.jjtNodeName[id];
    // }
    /**
     * @param prefix
     * @return String representation of this node.
     */
    public String toString(final String prefix) {
        return prefix + toString();
    }

    /**
     * Override this method if you want to customize how the node dumps out its children.
     * 
     * @param prefix
     */
    public void dump(final String prefix) {
        System.out.println(toString(prefix));
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                final SimpleNode n = (SimpleNode) children[i];
                if (n != null) {
                    n.dump(prefix + " ");
                }
            }
        }
    }

    // All additional methods

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#literal()
     */
    public String literal() {
        // if we have only one string, just return it and avoid
        // buffer allocation. VELOCITY-606
        if (first == last) {
            return NodeUtils.tokenLiteral(first);
        }

        Token t = first;
        final StrBuilder sb = new StrBuilder(NodeUtils.tokenLiteral(t));
        while (t != last) {
            t = t.next;
            sb.append(NodeUtils.tokenLiteral(t));
        }
        return sb.toString();
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#getType()
     */
    public int getType() {
        return id;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#setInfo(int)
     */
    public void setInfo(final int info) {
        this.info = info;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#getInfo()
     */
    public int getInfo() {
        return info;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#setInvalid()
     */
    public void setInvalid() {
        invalid = true;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#isInvalid()
     */
    public boolean isInvalid() {
        return invalid;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#getLine()
     */
    public int getLine() {
        return first.beginLine;
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.Node#getColumn()
     */
    public int getColumn() {
        return first.beginColumn;
    }

    /**
     * @since 1.5
     */
    @Override
    public String toString() {
        final StrBuilder tokens = new StrBuilder();

        for (Token t = getFirstToken(); t != null;) {
            tokens.append("[").append(t.image).append("]");
            if (t.next != null) {
                if (t.equals(getLastToken())) {
                    break;
                }
                else {
                    tokens.append(", ");
                }
            }
            t = t.next;
        }

        return new ToStringBuilder(this).append("id", getType()).append("info", getInfo())
                .append("invalid", isInvalid()).append("children", jjtGetNumChildren()).append("tokens", tokens)
                .toString();
    }

    public String getTemplateName() {
        return templateName;
    }

    @Override
    public SimpleNode jjtGetChild(final int index) {
        return (SimpleNode) children[index];
    }

}
