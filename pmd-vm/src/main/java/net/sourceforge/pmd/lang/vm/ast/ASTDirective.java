
package net.sourceforge.pmd.lang.vm.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
 * <p>For example : #foreach()
 *
 * <p>Please look at the Parser.jjt file which is what controls the generation of
 * this class.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:kav@kav.dk">Kasper Nielsen</a>
 * @version $Id: ASTDirective.java 724825 2008-12-09 18:56:06Z nbubna $
 */
public final class ASTDirective extends AbstractVmNode {

    private static final Set<String> DIRECTIVE_NAMES;
    private static final Set<String> BLOCK_DIRECTIVES;
    private static final Set<String> LINE_DIRECTIVES;

    static {
        Set<String> blocks = new HashSet<>();
        blocks.add("define");
        blocks.add("foreach");
        blocks.add("literal");
        blocks.add("macro");

        Set<String> lines = new HashSet<>();
        lines.add("break");
        lines.add("evaluate");
        lines.add("include");
        lines.add("parse");
        lines.add("stop");

        Set<String> directives = new HashSet<>();
        directives.addAll(blocks);
        directives.addAll(lines);
        DIRECTIVE_NAMES = Collections.unmodifiableSet(directives);
        BLOCK_DIRECTIVES = Collections.unmodifiableSet(blocks);
        LINE_DIRECTIVES = Collections.unmodifiableSet(lines);
    }

    private String directiveName = "";


    ASTDirective(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVmVisitor(VmVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Sets the directive name. Used by the parser. This keeps us from having to
     * dig it out of the token stream and gives the parse the change to
     * override.
     */
    void setDirectiveName(final String str) {
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

    boolean isDirective() {
        assert directiveName != null; // directive name must be set before
        return DIRECTIVE_NAMES.contains(directiveName);
    }

    // block macro call of type: #@foobar($arg1 $arg2) astBody #end
    boolean isBlock() {
        assert directiveName != null; // directive name must be set before
        return directiveName.startsWith("@")
                || BLOCK_DIRECTIVES.contains(directiveName);
    }

    boolean isLine() {
        assert directiveName != null; // directive name must be set before
        return LINE_DIRECTIVES.contains(directiveName)
                // not a real directive, but maybe a Velocimacro
                || !isDirective();
    }
}
