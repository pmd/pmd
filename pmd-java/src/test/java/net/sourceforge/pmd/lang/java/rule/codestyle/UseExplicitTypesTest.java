/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.internal.FakeSymAnnot;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.test.PmdRuleTst;

class UseExplicitTypesTest extends PmdRuleTst {

    @Nested
    class SimpleNameVisitorTest {
        @Test
        void simpleName() {
            JClassSymbol listSymbol = JavaParsingHelper.TEST_TYPE_SYSTEM.getClassSymbol("java.util.List");
            JClassSymbol stringSymbol = JavaParsingHelper.TEST_TYPE_SYSTEM.getClassSymbol("java.lang.String");
            JClassSymbol nonNullSymbol = JavaParsingHelper.TEST_TYPE_SYSTEM.getClassSymbol("org.checkerframework.checker.nullness.qual.NonNull");
            SymbolicValue.SymAnnot nonNullAnnot = new FakeSymAnnot(nonNullSymbol);
            JTypeMirror stringType = JavaParsingHelper.TEST_TYPE_SYSTEM.rawType(stringSymbol).addAnnotation(nonNullAnnot);
            JTypeMirror listOfString = JavaParsingHelper.TEST_TYPE_SYSTEM.parameterise(listSymbol, Collections.singletonList(stringType));

            UseExplicitTypesRule.SimpleNameVisitor visitor = new UseExplicitTypesRule.SimpleNameVisitor(Collections.emptyList());
            StringBuilder sb = listOfString.acceptVisitor(visitor, new StringBuilder());
            assertEquals("List<@NonNull String>", sb.toString());
        }
    }
}
