
package net.sourceforge.pmd.lang.vm.ast;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
 * ASTMethod.java
 *
 * Method support for references : $foo.method()
 *
 * NOTE :
 *
 * introspection is now done at render time.
 *
 * Please look at the Parser.jjt file which is what controls the generation of
 * this class.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: ASTMethod.java 720228 2008-11-24 16:58:33Z nbubna $
 */
public class ASTMethod extends AbstractVmNode {
    @InternalApi
    @Deprecated
    public ASTMethod(final int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTMethod(final VmParser p, final int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(final VmParserVisitor visitor, final Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Internal class used as key for method cache. Combines ASTMethod fields
     * with array of parameter classes. Has public access (and complete
     * constructor) for unit test purposes.
     *
     * @since 1.5
     * @deprecated for removal in PMD 7.0.0 - it's not used anywhere
     */
    @Deprecated
    public static class MethodCacheKey {
        private final String methodName;

        private final Class<?>[] params;

        public MethodCacheKey(final String methodName, final Class<?>[] params) {
            /**
             * Should never be initialized with nulls, but to be safe we refuse
             * to accept them.
             */
            this.methodName = (methodName != null) ? methodName : StringUtils.EMPTY;
            this.params = (params != null) ? params : ArrayUtils.EMPTY_CLASS_ARRAY;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object o) {
            /**
             * note we skip the null test for methodName and params due to the
             * earlier test in the constructor
             */
            if (o instanceof MethodCacheKey) {
                final MethodCacheKey other = (MethodCacheKey) o;
                if (params.length == other.params.length && methodName.equals(other.methodName)) {
                    for (int i = 0; i < params.length; ++i) {
                        if (params[i] == null) {
                            if (params[i] != other.params[i]) {
                                return false;
                            }
                        } else if (!params[i].equals(other.params[i])) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            int result = 17;

            /**
             * note we skip the null test for methodName and params due to the
             * earlier test in the constructor
             */
            for (int i = 0; i < params.length; ++i) {
                final Class<?> param = params[i];
                if (param != null) {
                    result = result * 37 + param.hashCode();
                }
            }

            result = result * 37 + methodName.hashCode();

            return result;
        }
    }

    /**
     * @return Returns the methodName.
     * @since 1.5
     */
    public String getMethodName() {
        return "";
    }

}
