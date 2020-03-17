
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
 * Pluggable directive that handles the <code>#parse()</code> statement in VTL.
 *
 * <pre>
 * Notes:
 * -----
 *  1) The parsed source material can only come from somewhere in
 *    the TemplateRoot tree for security reasons. There is no way
 *    around this.  If you want to include content from elsewhere on
 *    your disk, use a link from somwhere under Template Root to that
 *    content.
 *
 *  2) There is a limited parse depth.  It is set as a property
 *    "directive.parse.max.depth = 10" by default.  This 10 deep
 *    limit is a safety feature to prevent infinite loops.
 * </pre>
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:Christoph.Reck@dlr.de">Christoph Reck</a>
 * @version $Id: Parse.java 928253 2010-03-27 19:39:04Z nbubna $
 * @deprecated for removal in PMD 7.0.0
 */
@Deprecated
public class Parse extends InputBase {

    /**
     * Return name of this directive.
     *
     * @return The name of this directive.
     */
    @Override
    public String getName() {
        return "parse";
    }

    /**
     * Overrides the default to use "template", so that all templates can use
     * the same scope reference, whether rendered via #parse or direct merge.
     */
    @Override
    public String getScopeName() {
        return "template";
    }

    /**
     * Return type of this directive.
     *
     * @return The type of this directive.
     */
    @Override
    public int getType() {
        return LINE;
    }
}
