package net.sourceforge.pmd.lang.java.symbols;

import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.AsmSymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.testdata.AnnotWithDefaults;
import net.sourceforge.pmd.lang.java.symbols.testdata.SomeClass;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

public abstract class AbstractSymbolTest {

    protected final TypeSystem ts = JavaParsingHelper.TEST_TYPE_SYSTEM;
    protected final AsmSymbolResolver loader = (AsmSymbolResolver) ts.bootstrapResolver();
    
    private final boolean expectDebugSymbols;
    
    public AbstractSymbolTest(boolean expectDebugSymbols) {
        this.expectDebugSymbols = expectDebugSymbols;
    }

    protected abstract JClassSymbol resolveSymbol(Class<?> clazz);

    @Test
    public void testReflectionOfClassMethods() {
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = resolveSymbol(actualClass);
    
        Assert.assertNotNull(sym);
        assertAllMethodsMatch(actualClass, sym);
    }

    @Test
    public void testReflectionOfAnnotDefaults() {
        Class<AnnotWithDefaults> actualClass = AnnotWithDefaults.class;
        JClassSymbol sym = loadAnnotation(actualClass);
        
        Assert.assertNotNull(sym);
        assertAllMethodsMatch(actualClass, sym);
        
        // TODO : Check fields match too
    }
    
    @Test
    public void testRetentionClassAnnot() {
        // note that as the annotation has retention class, we can't use reflection to check

        /*
            @AnnotWithDefaults(valueNoDefault = "ohio",
                       stringArrayDefault = {})
         */
        JClassSymbol sym = resolveSymbol(SomeClass.class);

        Assert.assertTrue(sym.isAnnotationPresent(AnnotWithDefaults.class));

        SymAnnot target = sym.getDeclaredAnnotation(AnnotWithDefaults.class);

        // explicit values are known
        Assert.assertEquals(YES, target.attributeMatches("valueNoDefault", "ohio"));
        Assert.assertEquals(YES, target.attributeMatches("stringArrayDefault", new String[] {}));
        Assert.assertEquals(NO, target.attributeMatches("stringArrayDefault", "0"));
        
        // default values may be known only if parsing the class file…
        if (target.getAttributeNames().contains("stringArrayEmptyDefault")) {
            Assert.assertEquals(YES, target.attributeMatches("stringArrayEmptyDefault", new String[] {}));
            Assert.assertEquals(NO, target.attributeMatches("stringArrayEmptyDefault", new String[] {"a"}));
        }
        
        // Non existing values are always considered unknown
        Assert.assertEquals(UNKNOWN, target.attributeMatches("attributeDoesNotExist", new String[] {"a"}));
    }
    
    @Test
    public void testAnnotOnClass() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Class<AnnotWithDefaults> actualClass = AnnotWithDefaults.class;
        JClassSymbol sym = resolveSymbol(actualClass);

        for (final Annotation annot : actualClass.getDeclaredAnnotations()) {
            Class<? extends Annotation> annotType = annot.annotationType();
            Assert.assertTrue(sym.isAnnotationPresent(annotType));
    
            SymAnnot symAnnot = sym.getDeclaredAnnotation(annotType);
            Assert.assertTrue(symAnnot.isOfType(annotType));
            Assert.assertTrue(symAnnot.isOfType(annotType.getName()));

            // There is no way in Java's reflection API to know which attributes were set, so we trust our symbol…
            for (String attribute : symAnnot.getAttributeNames()) {
                final Object value = annotType.getMethod(attribute).invoke(annot);
                Assert.assertEquals(YES, symAnnot.attributeMatches(attribute, value));
            }
        }
    }
    
    @Test
    public void testAnnotOnParameter() {
        // This is for Target.PARAMETER annotations, do not confuse with TYPE_PARAMETER
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = resolveSymbol(actualClass);

        List<JMethodSymbol> ms = sym.getDeclaredMethods();
        Method[] actualMethods = actualClass.getDeclaredMethods();
   
        for (final Method m : actualMethods) {
            JMethodSymbol mSym = ms.stream().filter(it -> it.getSimpleName().equals(m.getName()))
                    .findFirst().orElseThrow(AssertionError::new);
            
            final Parameter[] parameters = m.getParameters();
            List<JFormalParamSymbol> formals = mSym.getFormalParameters();
            
            for (int i = 0; i < m.getParameterCount(); i++) {
                Parameter p = parameters[i];
                JFormalParamSymbol pSym = formals.get(i);
                
                Assert.assertEquals(p.getDeclaredAnnotations().length, pSym.getDeclaredAnnotations().size());
                for (Annotation annot : p.getDeclaredAnnotations()) {
                    Assert.assertEquals(symValueOf(annot), pSym.getDeclaredAnnotation(annot.annotationType()));
                }
            }
        }
    }
    
    @Test
    public void testAnnotOnField() {
        // TODO
    }
    
    @Test
    public void testAnnotOnMethod() {
        // TODO
    }
    
    @Test
    public void testAnnotOnConstructor() {
        // TODO
    }
    
    protected SymbolicValue symValueOf(Object o) {
        return SymbolicValue.of(ts, o);
    }
    
    protected @NonNull JClassSymbol loadAnnotation(Class<?> klass) {
        JClassSymbol sym = resolveSymbol(klass);
        Assert.assertTrue(sym.isAbstract());
        Assert.assertTrue(sym.isAnnotation());
        Assert.assertTrue(sym.isInterface());
        return sym;
    }

    private void assertAllMethodsMatch(Class<?> actualClass, JClassSymbol sym) {
        List<JMethodSymbol> ms = sym.getDeclaredMethods();
        Method[] actualMethods = actualClass.getDeclaredMethods();
        Assert.assertEquals(actualMethods.length, ms.size());
   
        for (final Method m : actualMethods) {
            JMethodSymbol mSym = ms.stream().filter(it -> it.getSimpleName().equals(m.getName()))
                    .findFirst().orElseThrow(AssertionError::new);
            
            assertMethodMatch(m, mSym);
        }
    }
    
    private void assertMethodMatch(Method m, JMethodSymbol mSym) {
        Assert.assertEquals(m.getParameterCount(), mSym.getArity());
        
        final Parameter[] parameters = m.getParameters();
        List<JFormalParamSymbol> formals = mSym.getFormalParameters();
        
        for (int i = 0; i < m.getParameterCount(); i++) {
            assertParameterMatch(parameters[i], formals.get(i));
        }
        
        // Defaults should match too (even if not an annotation method, both should be null)
        Assert.assertEquals(symValueOf(m.getDefaultValue()), mSym.getDefaultAnnotationValue());
    }

    private void assertParameterMatch(Parameter p, JFormalParamSymbol pSym) {
        if (expectDebugSymbols) {
            if (p.isNamePresent()) {
                Assert.assertEquals(p.getName(), pSym.getSimpleName());
                Assert.assertEquals(Modifier.isFinal(p.getModifiers()), pSym.isFinal());
            } else {
                System.out.println("WARN: test classes were not compiled with -parameters, parameters not fully checked");
            }
        } else {
            // note that this asserts, that the param names are unavailable
            Assert.assertEquals("", pSym.getSimpleName());
            Assert.assertFalse(pSym.isFinal());
        }
        
        // Ensure type matches
        final JTypeMirror expectedType = typeMirrorOf(p.getType());
        
        Assert.assertEquals(expectedType, pSym.getTypeMirror(Substitution.EMPTY));
    }
    
    private JTypeMirror typeMirrorOf(Class<?> type) {
        if (type.isPrimitive()) {
            return ts.getPrimitive(PrimitiveTypeKind.fromName(type.getName()));
        }
        
        return ts.typeOf(loader.resolveClassFromBinaryName(type.getName()), true);
    }
}