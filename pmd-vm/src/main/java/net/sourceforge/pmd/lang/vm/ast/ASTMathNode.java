
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
 * Helps handle math<br>
 * <br>
 *
 * Please look at the Parser.jjt file which is what controls the generation of
 * this class.
 *
 * @author <a href="mailto:wglass@forio.com">Will Glass-Husain</a>
 * @author <a href="mailto:pero@antaramusic.de">Peter Romianowski</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author Nathan Bubna
 * @version $Id: ASTMathNode.java 517553 2007-03-13 06:09:58Z wglass $
 */
@InternalApi
@Deprecated
public abstract class ASTMathNode extends AbstractVmNode {
    protected boolean strictMode = false;

    @InternalApi
    @Deprecated
    public ASTMathNode(final int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTMathNode(final VmParser p, final int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(final VmParserVisitor visitor, final Object data) {
        return visitor.visit(this, data);
    }

}
