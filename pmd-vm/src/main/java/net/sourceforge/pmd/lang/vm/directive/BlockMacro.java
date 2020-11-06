
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
 * BlockMacro directive is used to invoke Velocity macros with normal parameters
 * and a macro body.
 * <p>
 * The macro can then refer to the passed body AST. This directive can be used
 * as a "decorator". Body AST can contain any valid Velocity syntax.
 *
 * An example:
 *
 * <pre>
 * #set($foobar = "yeah!")
 *
 * #macro(strong $txt)
 * &lt;strong&gt;$bodyContent&lt;/strong&gt; $txt
 * #end
 *
 * #@strong($foobar)
 * &lt;u&gt;This text is underlined and bold&lt;/u&gt;
 * #end
 * </pre>
 *
 * Will print:
 *
 * <pre>
 * &lt;strong&gt;&lt;u&gt;This text is underlined and bold&lt;u&gt;&lt;/strong&gt; yeah!
 * </pre>
 *
 * bodyContent reference name is configurable (see velocity.properties).
 *
 * @author <a href="mailto:wyla@removethis.sci.fi">Jarkko Viinamaki</a>
 * @since 1.7
 * @version $Id$
 * @deprecated for removal in PMD 7.0.0
 */
@Deprecated
public class BlockMacro extends Block {
    private String name;

    public BlockMacro(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return key;
    }

    /**
     * Override to use the macro name, since it is within an #@myMacro() ...
     * #end block that the scope in question would be used.
     */
    @Override
    public String getScopeName() {
        return name;
    }

}
