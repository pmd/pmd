
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
 * <p>
 * Pluggable directive that handles the #include() statement in VTL. This
 * #include() can take multiple arguments of either StringLiteral or Reference.
 * </p>
 *
 * <p>
 * Notes:
 * </p>
 * <ol>
 * <li>For security reasons, the included source material can only come from
 * somewhere within the template root tree. If you want to include content from
 * elsewhere on your disk, add extra template roots, or use a link from somwhere
 * under template root to that content.</li>
 *
 * <li>By default, there is no output to the render stream in the event of a
 * problem. You can override this behavior with two property values :
 * include.output.errormsg.start include.output.errormsg.end If both are defined
 * in velocity.properties, they will be used to in the render output to bracket
 * the arg string that caused the problem. Ex. : if you are working in html then
 * include.output.errormsg.start=&lt;!-- #include error :
 * include.output.errormsg.end= --&gt; might be an excellent way to
 * start...</li>
 *
 * <li>As noted above, #include() can take multiple arguments. Ex :
 * #include('foo.vm' 'bar.vm' $foo) will include all three if valid to output
 * without any special separator.</li>
 * </ol>
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:kav@kav.dk">Kasper Nielsen</a>
 * @version $Id: Include.java 746438 2009-02-21 05:41:24Z nbubna $
 * @deprecated for removal in PMD 7.0.0
 */
@Deprecated
public class Include extends InputBase {

    /**
     * Return name of this directive.
     *
     * @return The name of this directive.
     */
    @Override
    public String getName() {
        return "include";
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

    /**
     * Since there is no processing of content, there is never a need for an
     * internal scope.
     */
    @Override
    public boolean isScopeProvided() {
        return false;
    }
}
