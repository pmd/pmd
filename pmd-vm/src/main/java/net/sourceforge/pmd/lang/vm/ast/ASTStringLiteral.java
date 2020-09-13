
package net.sourceforge.pmd.lang.vm.ast;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * ASTStringLiteral support. Will interpolate!
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id: ASTStringLiteral.java 705297 2008-10-16 17:59:24Z nbubna $
 */
public final class ASTStringLiteral extends AbstractVmNode {

    ASTStringLiteral(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVmVisitor(VmVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Check to see if this is an interpolated string.
     *
     * @return true if this is constant (not an interpolated string)
     * @since 1.6
     */
    public boolean isConstant() {
        return false;
    }

}
