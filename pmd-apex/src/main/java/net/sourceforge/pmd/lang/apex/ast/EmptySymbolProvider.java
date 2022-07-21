/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
// Note: taken from https://github.com/forcedotcom/idecore/blob/3083815933c2d015d03417986f57bd25786d58ce/com.salesforce.ide.apex.core/src/com/salesforce/ide/apex/internal/core/EmptySymbolProvider.java

/*
 * Copyright 2016 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * @author jspagnola
 */
@Deprecated
@InternalApi
public final class EmptySymbolProvider /*implements SymbolProvider*/ {

    private static final EmptySymbolProvider INSTANCE = new EmptySymbolProvider();

    private EmptySymbolProvider() {
    }

    public static EmptySymbolProvider get() {
        return INSTANCE;
    }

    /*
    @Override
    public TypeInfo find(final SymbolResolver symbols, final TypeInfo referencingType, final String lowerCaseFullName) {
        return null;
    }

    @Override
    public TypeInfo getVfComponentType(final SymbolResolver symbols, final TypeInfo referencingType,
            final Namespace namespace, final String name) {
        return null;
    }

    @Override
    public TypeInfo getFlowInterviewType(final SymbolResolver symbols, final TypeInfo referencingType,
            final Namespace namespace, final String name) {
        return null;
    }

    @Override
    public TypeInfo getSObjectType(final TypeInfo referencingType, final String name) {
        return null;
    }

    @Override
    public String getPageReference(final TypeInfo referencingType, final String name) {
        return null;
    }

    @Override
    public boolean hasLabelField(final TypeInfo referencingType, final Namespace namespace, final String name) {
        return false;
    }

    @Override
    public String getQuickAction(TypeInfo arg0, String arg1, String arg2) {
        return null;
    }

    @Override
    public TypeInfo getAggregateResultType(TypeInfo arg0) {
        return null;
    }

    @Override
    public boolean isDynamicTypeNamespace(String var1, String var2) {
        return false;
    }

    @Override
    public boolean isDynamicTypeNamespace(String var1) {
        return false;
    }
     */
    // TODO(b/239648780)
}
