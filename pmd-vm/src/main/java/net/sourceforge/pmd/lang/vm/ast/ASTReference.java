
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

/**
 * This class is responsible for handling the references in VTL ($foo).
 *
 * <p>Please look at the Parser.jjt file which is what controls the generation of
 * this class.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:Christoph.Reck@dlr.de">Christoph Reck</a>
 * @author <a href="mailto:kjohnson@transparent.com">Kent Johnson</a>
 * @version $Id: ASTReference.java 806597 2009-08-21 15:21:44Z nbubna $
 */
public final class ASTReference extends AbstractVmNode {
    private String rootString;

    private String literal = null;

    ASTReference(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVmVisitor(VmVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the 'root string', the reference key.
     *
     * @return the root string.
     */
    public String getRootString() {
        return rootString;
    }

    /**
     * Routine to allow the literal representation to be externally overridden.
     * Used now in the VM system to override a reference in a VM tree with the
     * literal of the calling arg to make it work nicely when calling arg is
     * null. It seems a bit much, but does keep things consistant.
     *
     * <p>Note, you can only set the literal once...
     *
     * @param literal
     *            String to render to when null
     */
    void setLiteral(final String literal) {
        /*
         * do only once
         */

        if (this.literal == null) {
            this.literal = literal;
        }
    }

    /**
     * Override of the SimpleNode method literal() Returns the literal
     * representation of the node. Should be something like $&lt;token&gt;.
     *
     * @return A literal string.
     */
    @Override
    public String literal() {
        if (literal != null) {
            return literal;
        }

        // this value could be cached in this.literal but it increases memory
        // usage
        return super.literal();
    }
}
