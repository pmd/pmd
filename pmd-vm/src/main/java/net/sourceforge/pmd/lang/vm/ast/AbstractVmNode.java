
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

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

abstract class AbstractVmNode extends AbstractJjtreeNode<AbstractVmNode, VmNode> implements VmNode {

    protected AbstractVmNode(final int i) {
        super(i);
    }


    @Override // override to make protected member accessible to parser
    protected void setImage(String image) {
        super.setImage(image);
    }

    @Override
    public String getXPathNodeName() {
        return VmParserImplTreeConstants.jjtNodeName[id];
    }


    protected abstract <P, R> R acceptVmVisitor(VmVisitor<? super P, ? extends R> visitor, P data);

    @Override
    @SuppressWarnings("unchecked")
    public final <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof VmVisitor) {
            return acceptVmVisitor((VmVisitor<? super P, ? extends R>) visitor, data);
        }
        return visitor.cannotVisit(this, data);
    }

    /*
     * see org.apache.velocity.runtime.parser.node.Node#literal()
     */
    public String literal() {
        // if we have only one string, just return it and avoid
        // buffer allocation. VELOCITY-606
        if (getFirstToken() != null && getFirstToken().equals(getLastToken())) {
            return NodeUtils.tokenLiteral(getFirstToken());
        }

        JavaccToken t = getFirstToken();
        final StringBuilder sb = new StringBuilder(NodeUtils.tokenLiteral(t));
        while (t != null && !t.equals(getLastToken())) {
            t = t.getNext();
            sb.append(NodeUtils.tokenLiteral(t));
        }
        return sb.toString();
    }

}
