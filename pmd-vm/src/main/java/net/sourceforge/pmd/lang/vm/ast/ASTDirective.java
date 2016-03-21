package net.sourceforge.pmd.lang.vm.ast;

import org.apache.commons.lang3.builder.ToStringBuilder;

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
 * This class is responsible for handling the pluggable directives in VTL.
 * 
 * For example : #foreach()
 * 
 * Please look at the Parser.jjt file which is what controls the generation of this class.
 * 
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:kav@kav.dk">Kasper Nielsen</a>
 * @version $Id: ASTDirective.java 724825 2008-12-09 18:56:06Z nbubna $
 */
public class ASTDirective extends AbstractVmNode {
    private String directiveName = "";

    /**
     * @param id
     */
    public ASTDirective(final int id) {
        super(id);
    }

    /**
     * @param p
     * @param id
     */
    public ASTDirective(final VmParser p, final int id) {
        super(p, id);
    }

    /**
     * @see org.apache.velocity.runtime.parser.node.SimpleNode#jjtAccept(org.apache.velocity.runtime.parser.node.VmParserVisitor,
     *      java.lang.Object)
     */
    @Override
    public Object jjtAccept(final VmParserVisitor visitor, final Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Sets the directive name. Used by the parser. This keeps us from having to dig it out of the token stream and
     * gives the parse the change to override.
     * 
     * @param str
     */
    public void setDirectiveName(final String str) {
        directiveName = str;
    }

    /**
     * Gets the name of this directive.
     * 
     * @return The name of this directive.
     */
    public String getDirectiveName() {
        return directiveName;
    }

    /**
     * @since 1.5
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("directiveName", getDirectiveName())
                .toString();
    }

}
