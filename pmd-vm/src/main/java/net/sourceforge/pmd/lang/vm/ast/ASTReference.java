
package net.sourceforge.pmd.lang.vm.ast;

import net.sourceforge.pmd.annotation.InternalApi;

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
 * Please look at the Parser.jjt file which is what controls the generation of
 * this class.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:Christoph.Reck@dlr.de">Christoph Reck</a>
 * @author <a href="mailto:kjohnson@transparent.com">Kent Johnson</a>
 * @version $Id: ASTReference.java 806597 2009-08-21 15:21:44Z nbubna $
 */
public class ASTReference extends AbstractVmNode {
    private String rootString;

    private String literal = null;

    /**
     * Indicates if we are running in strict reference mode.
     *
     * @deprecated for removal with PMD 7.0.0
     */
    @Deprecated
    public boolean strictRef = false;

    /**
     * Indicates if toString() should be called during condition evaluation just
     * to ensure it does not return null. Check is unnecessary if all toString()
     * implementations are known to have non-null return values. Disabling the
     * check will give a performance improval since toString() may be a complex
     * operation on large objects.
     *
     * @deprecated for removal with PMD 7.0.0
     */
    @Deprecated
    public boolean toStringNullCheck = true;

    @InternalApi
    @Deprecated
    public ASTReference(final int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTReference(final VmParser p, final int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(final VmParserVisitor visitor, final Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the 'root string', the reference key
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
     * Note, you can only set the literal once...
     *
     * @param literal
     *            String to render to when null
     */
    @InternalApi
    @Deprecated
    public void setLiteral(final String literal) {
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
