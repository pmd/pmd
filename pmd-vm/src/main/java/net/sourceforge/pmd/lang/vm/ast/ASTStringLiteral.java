
package net.sourceforge.pmd.lang.vm.ast;

import org.apache.commons.lang3.text.StrBuilder;

import net.sourceforge.pmd.annotation.InternalApi;

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
public class ASTStringLiteral extends AbstractVmNode {
    @InternalApi
    @Deprecated
    public ASTStringLiteral(final int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTStringLiteral(final VmParser p, final int id) {
        super(p, id);
    }

    /**
     * Adjust all the line and column numbers that comprise a node so that they
     * are corrected for the string literals position within the template file.
     * This is neccessary if an exception is thrown while processing the node so
     * that the line and column position reported reflects the error position
     * within the template and not just relative to the error position within
     * the string literal.
     * 
     * @deprecated for removal with PMD 7.0.0
     */
    @Deprecated
    public void adjTokenLineNums(final AbstractVmNode node) {
        Token tok = node.getFirstToken();
        // Test against null is probably not neccessary, but just being safe
        while (tok != null && tok != node.getLastToken()) {
            // If tok is on the first line, then the actual column is
            // offset by the template column.

            if (tok.beginLine == 1) {
                tok.beginColumn += getColumn();
            }

            if (tok.endLine == 1) {
                tok.endColumn += getColumn();
            }

            tok.beginLine += getLine() - 1;
            tok.endLine += getLine() - 1;
            tok = tok.next;
        }
    }

    /**
     * @since 1.6
     * @deprecated for removal with PMD 7.0.0
     */
    @InternalApi
    @Deprecated
    public static String unescape(final String string) {
        int u = string.indexOf("\\u");
        if (u < 0) {
            return string;
        }

        final StrBuilder result = new StrBuilder();

        int lastCopied = 0;

        for (;;) {
            result.append(string.substring(lastCopied, u));

            /*
             * we don't worry about an exception here, because the lexer checked
             * that string is correct
             */
            final char c = (char) Integer.parseInt(string.substring(u + 2, u + 6), 16);
            result.append(c);

            lastCopied = u + 6;

            u = string.indexOf("\\u", lastCopied);
            if (u < 0) {
                result.append(string.substring(lastCopied));
                return result.toString();
            }
        }
    }

    @Override
    public Object jjtAccept(final VmParserVisitor visitor, final Object data) {
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
