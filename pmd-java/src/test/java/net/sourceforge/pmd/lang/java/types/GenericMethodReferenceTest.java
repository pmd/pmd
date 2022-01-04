/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.testdata.GenericMethodReference;

public class GenericMethodReferenceTest {

    @Test
    public void typeResolveVariable() {
        ASTCompilationUnit root = JavaParsingHelper.WITH_PROCESSING.parseClass(GenericMethodReference.class);

        root.descendants(ASTVariableDeclaratorId.class).forEach(variable -> {
            Assert.assertTrue(variable.getName().startsWith("supplier"));
            @Nullable
            JTypeDeclSymbol symbol = variable.getInitializer().getTypeMirror().getSymbol();
            Assert.assertEquals(Supplier.class.getSimpleName(), symbol.getSimpleName());
        });
    }
}
