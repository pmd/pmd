
package net.sourceforge.pmd.lang.vm.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

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
 * Utilities for dealing with the AST node structure.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: NodeUtils.java 687386 2008-08-20 16:57:07Z nbubna $
 */
final class NodeUtils {
    private NodeUtils() { }

    /**
     * Collect all the &lt;SPECIAL_TOKEN&gt;s that are carried along with a token.
     * Special tokens do not participate in parsing but can still trigger
     * certain lexical actions. In some cases you may want to retrieve these
     * special tokens, this is simply a way to extract them.
     *
     * @param t
     *            the Token
     * @return StrBuilder with the special tokens.
     */
    private static StringBuilder getSpecialText(final JavaccToken t) {
        final StringBuilder sb = new StringBuilder();

        JavaccToken tmpToken = t.getPreviousComment();

        while (tmpToken.getPreviousComment() != null) {
            tmpToken = tmpToken.getPreviousComment();
        }

        while (tmpToken != null) {
            final String st = tmpToken.getImage();

            int i = 0;
            while (i < st.length()) {
                final char c = st.charAt(i);

                if (c == '#' || c == '$') {
                    sb.append(c);
                }

                /*
                 * more dreaded MORE hack :)
                 *
                 * looking for ("\\")*"$" sequences
                 */

                if (c == '\\') {
                    boolean ok = true;
                    boolean term = false;

                    int j = i;
                    for (ok = true; ok && j < st.length(); j++) {
                        final char cc = st.charAt(j);

                        if (cc == '\\') {
                            /*
                             * if we see a \, keep going
                             */
                            continue;
                        } else if (cc == '$') {
                            /*
                             * a $ ends it correctly
                             */
                            term = true;
                            ok = false;
                        } else {
                            /*
                             * nah...
                             */
                            ok = false;
                        }
                    }

                    if (term) {
                        final String foo = st.substring(i, j);
                        sb.append(foo);
                        i = j;
                    }
                }
                i++;
            }

            tmpToken = tmpToken.next;
        }
        return sb;
    }

    /**
     * complete node literal
     *
     * @param t
     * @return A node literal.
     */
    static String tokenLiteral(final JavaccToken t) {
        // Look at kind of token and return "" when it's a multiline comment
        if (t.kind == VmTokenKinds.MULTI_LINE_COMMENT) {
            return "";
        } else if (t.getPreviousComment() == null || t.getPreviousComment().getImage().startsWith("##")) {
            return t.getImage();
        } else {
            final StringBuilder special = getSpecialText(t);
            if (special.length() > 0) {
                return special.append(t.getImage()).toString();
            }
            return t.getImage();
        }
    }

}
