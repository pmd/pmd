/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static net.sourceforge.pmd.lang.java.symbols.AnnotationUtils.ofArray;
import static net.sourceforge.pmd.lang.java.symbols.AnnotationUtils.ofEnum;
import static net.sourceforge.pmd.lang.java.symbols.AnnotationUtils.symValueFor;
import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.AsmSymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.testdata.AnnotWithDefaults;
import net.sourceforge.pmd.lang.java.symbols.testdata.AnnotWithDefaults.MyEnum;
import net.sourceforge.pmd.lang.java.symbols.testdata.SomeClass;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 *
 */
public class SymbolReflectionTest {

    private final TypeSystem ts = JavaParsingHelper.TEST_TYPE_SYSTEM;
    private final AsmSymbolResolver loader = (AsmSymbolResolver) ts.bootstrapResolver();


    @Test
    public void testReflectionOfParamNames() {
        // note that this asserts, that the param names are unavailable

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

    @Test
    public void testReflectionOfAnnotDefault() {
        // note that this asserts, that the param names are unavailable

        JClassSymbol sym = loadAnnotation(AnnotWithDefaults.class);

        JMethodSymbol m;

        m = getMethod(sym, "valueWithDefault");
        Assert.assertEquals(symValueFor("ddd"), m.getDefaultAnnotationValue());

        m = getMethod(sym, "valueNoDefault");
        Assert.assertNull(m.getDefaultAnnotationValue());

        m = getMethod(sym, "stringArrayDefault");
        Assert.assertEquals(symValueFor(new String[] {"ddd"}), m.getDefaultAnnotationValue());

        m = getMethod(sym, "stringArrayEmptyDefault");
        Assert.assertEquals(ofArray(), m.getDefaultAnnotationValue());
    }


    @Test
    public void testReflectionOfEnumDefault() {
        // note that this asserts, that the param names are unavailable

        JClassSymbol sym = loadAnnotation(AnnotWithDefaults.class);

        JMethodSymbol m;

        m = getMethod(sym, "enumArr");
        Assert.assertEquals(ofArray(ofEnum(MyEnum.AA), ofEnum(MyEnum.BB)),
                            m.getDefaultAnnotationValue());

        m = getMethod(sym, "enumSimple");
        Assert.assertEquals(ofEnum(MyEnum.AA), m.getDefaultAnnotationValue());
    }


    @Test
    public void testAnnotOnClass() {
        // note that this asserts, that the param names are unavailable

        JClassSymbol sym = loadClass(AnnotWithDefaults.class);

        Assert.assertTrue(sym.isAnnotationPresent(Target.class));

        SymAnnot target = sym.getDeclaredAnnotation(Target.class);
        Assert.assertTrue(target.isOfType(Target.class));
        Assert.assertTrue(target.isOfType(Target.class.getName()));

        Assert.assertEquals(YES, target.attributeMatches("value", new ElementType[] {ElementType.ANNOTATION_TYPE, ElementType.TYPE_USE}));

    }


    @Test
    public void testAnnotThatHasDefaults() {
        // note that this asserts, that the param names are unavailable

        /*
            @AnnotWithDefaults(valueNoDefault = "ohio",
                       stringArrayDefault = {})

         */
        JClassSymbol sym = loadClass(SomeClass.class);

        Assert.assertTrue(sym.isAnnotationPresent(AnnotWithDefaults.class));

        SymAnnot target = sym.getDeclaredAnnotation(AnnotWithDefaults.class);


        Assert.assertEquals(YES, target.attributeMatches("valueNoDefault", "ohio"));
        Assert.assertEquals(YES, target.attributeMatches("stringArrayDefault", new String[] {}));
        Assert.assertEquals(NO, target.attributeMatches("stringArrayDefault", "0"));
        Assert.assertEquals(UNKNOWN, target.attributeMatches("stringArrayEmptyDefault", new String[] {}));

    }


    @Test
    public void testSymValueEquality() {
        Assert.assertEquals(symValueFor(new String[] {"ddd", "eee"}),
                            ofArray(symValueFor("ddd"), symValueFor("eee")));
    }


    private static JMethodSymbol getMethod(JClassSymbol sym, String name) {
        return sym.getDeclaredMethods().stream().filter(it -> it.getSimpleName().equals(name))
                  .findFirst()
                  .orElseThrow(AssertionError::new);
    }

    private @NonNull JClassSymbol loadAnnotation(Class<?> klass) {
        JClassSymbol sym = loadClass(klass);
        Assert.assertTrue(sym.isAbstract());
        Assert.assertTrue(sym.isAnnotation());
        Assert.assertTrue(sym.isInterface());
        return sym;
    }
    private @NonNull JClassSymbol loadClass(Class<?> klass) {
        JClassSymbol sym = loader.resolveClassFromBinaryName(klass.getName());
        Assert.assertNotNull(sym);
        return sym;
    }


}
