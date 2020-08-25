/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.testdata.SomeClass;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 *
 */
public class TestMethodReflection {

    private final TypeSystem ts = JavaParsingHelper.TEST_TYPE_SYSTEM;
    private final AsmSymbolResolver loader = (AsmSymbolResolver) ts.bootstrapResolver();


    @Test
    public void testReflectionOfParamNames() {
        JClassSymbol sym = loader.resolveClassFromBinaryName(SomeClass.class.getName());

        Assert.assertNotNull(sym);
        List<JMethodSymbol> ms = sym.getDeclaredMethods();
        Assert.assertEquals(2, ms.size());

        JMethodSymbol m1 = ms.stream().filter(it -> it.getSimpleName().equals("m1")).findFirst().orElseThrow(AssertionError::new);
        Assert.assertEquals(2, m1.getArity());
        List<JFormalParamSymbol> m1Formals = m1.getFormalParameters();
        Assert.assertEquals(2, m1Formals.size());

        JFormalParamSymbol p = m1Formals.get(0);
        Assert.assertEquals("", p.getSimpleName());
        Assert.assertFalse(p.isFinal());

        Assert.assertEquals(ts.INT, p.getTypeMirror(Substitution.EMPTY));

        p = m1Formals.get(1);
        Assert.assertEquals("", p.getSimpleName());
        Assert.assertFalse(p.isFinal());


        Assert.assertEquals(ts.getClassSymbol(String.class), p.getTypeMirror(Substitution.EMPTY).getSymbol());
    }

}
