/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.testdata.GenericMethodReference;

class GenericMethodReferenceTest {

    @Test
    void typeResolveVariable() {
        ASTCompilationUnit root = JavaParsingHelper.DEFAULT.parseClass(GenericMethodReference.class);

        root.descendants(ASTVariableDeclaratorId.class).forEach(variable -> {
            assertTrue(variable.getName().startsWith("supplier"));
            @Nullable
            JTypeDeclSymbol symbol = variable.getInitializer().getTypeMirror().getSymbol();
            assertEquals(Supplier.class.getSimpleName(), symbol.getSimpleName());
        });
    }
}
