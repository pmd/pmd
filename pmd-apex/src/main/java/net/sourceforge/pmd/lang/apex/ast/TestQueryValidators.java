/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
// Note: taken from https://github.com/forcedotcom/idecore/blob/3083815933c2d015d03417986f57bd25786d58ce/com.salesforce.ide.apex.core/src/apex/jorje/semantic/common/TestQueryValidators.java

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
 *
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * The test query validators will return back the query it was given. The real implementation actually creates its own
 * query.
 *
 * @author jspagnola
 */
@SuppressWarnings("PMD.MissingStaticMethodInNonInstantiatableClass") // this class provides utility classes
@Deprecated
@InternalApi
public final class TestQueryValidators {

    private TestQueryValidators() {
    }

    /*
    public static class Noop implements QueryValidator {
        @Override
        public String validateSoql(
            final SymbolResolver symbols,
            final ValidationScope scope,
            final SoqlExpression soql
        ) {
            return soql.getCanonicalQuery();
        }

        @Override
        public String validateSosl(
            final SymbolResolver symbols,
            final ValidationScope typeInfo,
            final SoslExpression sosl
        ) {
            return sosl.getCanonicalQuery();
        }
    }

    public static class Error implements QueryValidator {
        @Override
        public String validateSoql(
            final SymbolResolver symbols,
            final ValidationScope scope,
            final SoqlExpression soql
        ) {
            scope.getErrors().markInvalid(soql, "Bad Soql");
            return soql.getCanonicalQuery();
        }

        @Override
        public String validateSosl(
            final SymbolResolver symbols,
            final ValidationScope scope,
            final SoslExpression sosl
        ) {
            scope.getErrors().markInvalid(sosl, "Bad Sosl");
            return sosl.getCanonicalQuery();
        }
    }
     */
    // TODO(b/239648780)
}
