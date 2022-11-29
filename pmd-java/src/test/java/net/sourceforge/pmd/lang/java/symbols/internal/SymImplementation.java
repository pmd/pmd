/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Abstracts over which symbol implementation to use. Allows running
 * the same tests with different implementations of the symbol.
 * Use with {@link ParameterizedTest} and {@link EnumSource}.
 */
public enum SymImplementation {
    ASM {
        @Override
        public JClassSymbol getSymbol(TypeSystem ts, Class<?> aClass) {
            return ts.getClassSymbol(aClass);
        }
    },
    AST {
        @Override
        public JClassSymbol getSymbol(TypeSystem ts, Class<?> aClass) {
            ASTCompilationUnit ast = JavaParsingHelper.DEFAULT.withTypeSystem(ts).parseClass(aClass);
            return ast.getTypeDeclarations().first(it -> it.getSimpleName().equals(aClass.getSimpleName())).getSymbol();
        }

        @Override
        public JClassType getDeclaration(TypeSystem ts, Class<?> aClass) {
            ASTCompilationUnit ast = JavaParsingHelper.DEFAULT.withTypeSystem(ts).parseClass(aClass);
            return ast.getTypeDeclarations().first(it -> it.getSimpleName().equals(aClass.getSimpleName())).getTypeMirror();
        }

        @Override
        public boolean supportsDebugSymbols() {
            return true;
        }
    };

    public boolean supportsDebugSymbols() {
        return false;
    }

    public abstract JClassSymbol getSymbol(TypeSystem ts, Class<?> aClass);

    public JClassType getDeclaration(TypeSystem ts, Class<?> aClass) {
        return (JClassType) ts.declaration(getSymbol(ts, aClass));
    }


    public void assertAllFieldsMatch(Class<?> actualClass, JClassSymbol sym) {
        List<JFieldSymbol> fs = sym.getDeclaredFields();
        Field[] actualFields = actualClass.getDeclaredFields();
        Assert.assertEquals(actualFields.length, fs.size());

        for (final Field f : actualFields) {
            JFieldSymbol fSym = fs.stream().filter(it -> it.getSimpleName().equals(f.getName()))
                                  .findFirst().orElseThrow(AssertionError::new);

            // Type matches
            final JTypeMirror expectedType = typeMirrorOf(sym.getTypeSystem(), f.getType());
            Assert.assertEquals(expectedType, fSym.getTypeMirror(Substitution.EMPTY));

            Assert.assertEquals(f.getModifiers(), fSym.getModifiers());
        }
    }

    private static JTypeMirror typeMirrorOf(TypeSystem ts, Class<?> type) {
        return ts.declaration(ts.getClassSymbol(type));
    }

    public void assertAllMethodsMatch(Class<?> actualClass, JClassSymbol sym) {
        List<JMethodSymbol> ms = sym.getDeclaredMethods();
        Method[] actualMethods = actualClass.getDeclaredMethods();
        Assert.assertEquals(actualMethods.length, ms.size());

        for (final Method m : actualMethods) {
            JMethodSymbol mSym = ms.stream().filter(it -> it.getSimpleName().equals(m.getName()))
                                   .findFirst().orElseThrow(AssertionError::new);

            assertMethodMatch(m, mSym);
        }
    }

    public void assertMethodMatch(Method m, JMethodSymbol mSym) {
        Assert.assertEquals(m.getParameterCount(), mSym.getArity());

        final Parameter[] parameters = m.getParameters();
        List<JFormalParamSymbol> formals = mSym.getFormalParameters();

        for (int i = 0; i < m.getParameterCount(); i++) {
            assertParameterMatch(parameters[i], formals.get(i));
        }

        // Defaults should match too (even if not an annotation method, both should be null)
        Assert.assertEquals(SymbolicValue.of(mSym.getTypeSystem(), m.getDefaultValue()), mSym.getDefaultAnnotationValue());
    }

    private void assertParameterMatch(Parameter p, JFormalParamSymbol pSym) {
        if (supportsDebugSymbols()) {
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
        final JTypeMirror expectedType = typeMirrorOf(pSym.getTypeSystem(), p.getType());

        Assert.assertEquals(expectedType, pSym.getTypeMirror(Substitution.EMPTY));
    }

}
