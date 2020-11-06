
package net.sourceforge.pmd.lang.vm.directive;

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
 * A very simple directive that leverages the Node.literal() to grab the literal
 * rendition of a node. We basically grab the literal value on init(), then
 * repeatedly use that during render(). <em>This is deprecated and will be
 * removed in Velocity 2.0; please use #[[unparsed content]]# instead.</em>
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id: Literal.java 746438 2009-02-21 05:41:24Z nbubna $
 * @deprecated Use the #[[unparsed content]]# syntax instead. Deprecated for removal in PMD 7.0.0
 */
@Deprecated
public class Literal extends Directive {
    String literalText;

    /**
     * Return name of this directive.
     *
     * @return The name of this directive.
     */
    @Override
    public String getName() {
        return "literal";
    }

    /**
     * Return type of this directive.
     *
     * @return The type of this directive.
     */
    @Override
    public int getType() {
        return BLOCK;
    }

    /**
     * Since there is no processing of content, there is never a need for an
     * internal scope.
     */
    @Override
    public boolean isScopeProvided() {
        return false;
    }

}
