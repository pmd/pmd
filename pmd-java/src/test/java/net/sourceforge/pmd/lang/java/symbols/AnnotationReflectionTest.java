/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.SymImplementation;
import net.sourceforge.pmd.lang.java.symbols.testdata.AnnotWithDefaults;
import net.sourceforge.pmd.lang.java.symbols.testdata.LocalVarAnnotation;
import net.sourceforge.pmd.lang.java.symbols.testdata.SomeClass;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

public class AnnotationReflectionTest {

    private final TypeSystem ts = JavaParsingHelper.TEST_TYPE_SYSTEM;

    public AnnotationReflectionTest() {
    }


    @ParameterizedTest
    @EnumSource
    public void testReflectionOfClassMethods(SymImplementation impl) {
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = impl.getSymbol(ts, actualClass);
    
        Assert.assertNotNull(sym);
        impl.assertAllMethodsMatch(actualClass, sym);
        impl.assertAllFieldsMatch(actualClass, sym);
    }

    @ParameterizedTest
    @EnumSource
    public void testReflectionOfAnnotDefaults(SymImplementation impl) {
        Class<AnnotWithDefaults> actualClass = AnnotWithDefaults.class;
        JClassSymbol sym = impl.getSymbol(ts, actualClass);
        
        Assert.assertNotNull(sym);
        impl.assertAllMethodsMatch(actualClass, sym);
    }

    @ParameterizedTest
    @EnumSource
    public void testRetentionClassAnnot(SymImplementation impl) {
        // note that as the annotation has retention class, we can't use reflection to check

        /*
            @AnnotWithDefaults(valueNoDefault = "ohio",
                       stringArrayDefault = {})
         */
        JClassSymbol sym = impl.getSymbol(ts, SomeClass.class);

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

    @ParameterizedTest
    @EnumSource
    public void testAnnotOnType(SymImplementation impl) {
        // This only checks for Target.TYPE annotations
        Class<AnnotWithDefaults> actualClass = AnnotWithDefaults.class;
        JClassSymbol sym = impl.getSymbol(ts, actualClass);

        assertEqualsAnnotations(actualClass, sym);
    }

    @ParameterizedTest
    @EnumSource
    public void testAnnotOnAnnot(SymImplementation impl) {
        // This only checks for Target.ANNOTATION_TYPE annotations
        Class<AnnotWithDefaults> actualClass = AnnotWithDefaults.class;
        JClassSymbol sym = impl.getSymbol(ts, actualClass);

        assertEqualsAnnotations(actualClass, sym);
    }

    @ParameterizedTest
    @EnumSource
    public void testAnnotOnParameter(SymImplementation impl) {
        // This only checks Target.PARAMETER annotations, do not confuse with TYPE_PARAMETER / TYPE_USE
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = impl.getSymbol(ts, actualClass);

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
                
                assertEqualsAnnotations(p, pSym);
            }
        }
    }

    @ParameterizedTest
    @EnumSource
    public void testAnnotOnField(SymImplementation impl) {
        // This only checks Target.FIELD annotations, do not confuse with TYPE_USE annotations
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = impl.getSymbol(ts, actualClass);
        
        List<JFieldSymbol> fs = sym.getDeclaredFields();
        Field[] actualFields = actualClass.getDeclaredFields();
        
        for (final Field f : actualFields) {
            JFieldSymbol fSym = fs.stream().filter(it -> it.getSimpleName().equals(f.getName()))
                    .findFirst().orElseThrow(AssertionError::new);
            
            assertEqualsAnnotations(f, fSym);
        }
    }
    
    @ParameterizedTest
    @EnumSource
    public void testAnnotOnMethod(SymImplementation impl) {
        // This only checks Target.METHOD annotations, do not confuse with TYPE_USE on return types
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = impl.getSymbol(ts, actualClass);

        List<JMethodSymbol> ms = sym.getDeclaredMethods();
        Method[] actualMethods = actualClass.getDeclaredMethods();
   
        for (final Method m : actualMethods) {
            JMethodSymbol mSym = ms.stream().filter(it -> it.getSimpleName().equals(m.getName()))
                    .findFirst().orElseThrow(AssertionError::new);
            
            assertEqualsAnnotations(m, mSym);
        }
    }
    
    @ParameterizedTest
    @EnumSource
    public void testAnnotOnConstructor(SymImplementation impl) {
        // This only checks Target.CONSTRUCTOR annotations, do not confuse with TYPE_USE on return types
        Class<SomeClass> actualClass = SomeClass.class;
        JClassSymbol sym = impl.getSymbol(ts, actualClass);

        List<JConstructorSymbol> cs = sym.getConstructors();
        Constructor<?>[] actualConstructors = actualClass.getDeclaredConstructors();
   
        for (final Constructor<?> c : actualConstructors) {
            // TODO : Arity alone is not unequivocal
            JConstructorSymbol mSym = cs.stream().filter(it -> it.getArity() == c.getParameterCount())
                    .findFirst().orElseThrow(AssertionError::new);
            
            assertEqualsAnnotations(c, mSym);
        }
    }

    @Test
    public void testAnnotOnLocalVar() {
        // This only checks Target.LOCAL_VAR annotations, do not confuse with TYPE_USE on return types
        JClassSymbol sym = SymImplementation.AST.getSymbol(ts, SomeClass.class);

        JMethodSymbol method = sym.getDeclaredMethods().stream().filter(it -> it.getSimpleName().equals("withAnnotatedLocal"))
                                  .findFirst().orElseThrow(AssertionError::new);

        ASTVariableDeclarator variableDeclarator = method.tryGetNode().descendants(ASTVariableDeclarator.class).first();
        JVariableSymbol localSym = variableDeclarator.getSymbolTable().variables().resolveFirst("local").getSymbol();

        PSet<SymAnnot> declaredAnnotations = localSym.getDeclaredAnnotations();

        Assert.assertEquals(1, declaredAnnotations.size());
        Assert.assertNotNull(localSym.getDeclaredAnnotation(LocalVarAnnotation.class));
    }
    
    protected SymbolicValue symValueOf(Object o) {
        return SymbolicValue.of(ts, o);
    }

    private void assertEqualsAnnotations(AnnotatedElement e, AnnotableSymbol eSym) {
        Assert.assertEquals(e.getDeclaredAnnotations().length, eSym.getDeclaredAnnotations().size());
        for (Annotation annot : e.getDeclaredAnnotations()) {
            Class<? extends Annotation> annotType = annot.annotationType();
            SymAnnot symAnnot = eSym.getDeclaredAnnotation(annotType);
            
            Assert.assertEquals(symValueOf(annot), symAnnot);
            
            Assert.assertTrue(eSym.isAnnotationPresent(annotType));
            Assert.assertTrue(symAnnot.isOfType(annotType));
            Assert.assertTrue(symAnnot.isOfType(annotType.getName()));
            
            // There is no way in Java's reflection API to know which attributes were set, so we trust our symbol…
            for (String attribute : symAnnot.getAttributeNames()) {
                Object value;
                try {
                    value = annotType.getMethod(attribute).invoke(annot);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                    throw new AssertionError("Failed to get annotation value.", ex);
                }
                Assert.assertEquals(YES, symAnnot.attributeMatches(attribute, value));
            }
        }
    }

}
