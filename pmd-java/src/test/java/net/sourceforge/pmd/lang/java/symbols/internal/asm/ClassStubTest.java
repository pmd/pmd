/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JRecordComponentSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.util.CollectionUtil;

class ClassStubTest {
    // while parsing the annotation type, ClassStub's parseLock.ensureParsed()
    // is called multiple times, reentering the parselock while the status is
    // still BEING_PARSED.
    @Test
    void loadAndParseAnnotation() {
        // class stub - annotation type
        TypeSystem ts = TypeSystem.usingClassLoaderClasspath(JavaParsingHelper.class.getClassLoader());
        JClassSymbol classSymbol = ts.getClassSymbol("java.lang.Deprecated");
        PSet<String> annotationAttributeNames = classSymbol.getAnnotationAttributeNames();
        assertFalse(annotationAttributeNames.isEmpty());
    }


    @Test
    void recordReflectionTest() {
        TypeSystem ts = TypeSystem.usingClassLoaderClasspath(JavaParsingHelper.class.getClassLoader());
        JClassSymbol pointRecord = loadRecordClass(ts, "Point");
        List<JRecordComponentSymbol> components = pointRecord.getRecordComponents();
        assertThat(components, hasSize(2));
        assertThat(components.get(0).getSimpleName(), equalTo("x"));
        assertThat(components.get(1).getSimpleName(), equalTo("y"));
        int modifiers = components.get(0).getModifiers();
        assertEquals(JRecordComponentSymbol.RECORD_COMPONENT_MODIFIERS, modifiers, Modifier.toString(modifiers));

        JClassType ty = (JClassType) ts.typeOf(pointRecord, false);
        assertEquals(ty.getDeclaredField("x").getTypeMirror(), ts.INT);
        assertEquals(ty.getDeclaredField("y").getTypeMirror(), ts.INT);

    }



    @Test
    void varargsRecordReflectionTest() {
        TypeSystem ts = TypeSystem.usingClassLoaderClasspath(JavaParsingHelper.class.getClassLoader());
        JClassSymbol record = loadRecordClass(ts, "Varargs");
        List<JRecordComponentSymbol> components = record.getRecordComponents();
        assertThat(components, hasSize(1));
        assertThat(components.get(0).getSimpleName(), equalTo("varargs"));
        JClassType ty = (JClassType) ts.typeOf(record, false);
        assertEquals(ty.getDeclaredField("varargs").getTypeMirror(), ts.arrayType(ts.FLOAT));

        List<JConstructorSymbol> ctors = record.getConstructors();
        assertThat(ctors, hasSize(1));
        assertTrue(ctors.get(0).isVarargs(), "varargs");
        assertEquals(ctors.get(0).getFormalParameterTypes(Substitution.EMPTY).get(0), ts.arrayType(ts.FLOAT));
    }


    @Test
    void annotatedRecordReflectionTest() {
        TypeSystem ts = TypeSystem.usingClassLoaderClasspath(JavaParsingHelper.class.getClassLoader());
        JClassSymbol record = loadRecordClass(ts, "Annotated");
        List<JRecordComponentSymbol> components = record.getRecordComponents();
        assertThat(components, hasSize(2));

        assertThat(components.get(0).getSimpleName(), equalTo("x"));
        // Interestingly record components cannot be deprecated.
        // The field and accessor method are marked with the deprecated annotation though
        assertNull(components.get(0).getDeclaredAnnotation(Deprecated.class), "should not be deprecated");
        assertNotNull(record.getDeclaredField("x").getDeclaredAnnotation(Deprecated.class), "should be deprecated");

        JClassSymbol annot = ts.getClassSymbol("net.sourceforge.pmd.lang.java.symbols.recordclasses.TypeAnnotation");
        assertNotNull(annot, "annot should exist");

        JClassType ty = (JClassType) ts.typeOf(record, false);
        JClassType withTyAnnotation = (JClassType) ty.getDeclaredField("strings").getTypeMirror();
        assertIsListWithTyAnnotation(withTyAnnotation);

        List<JConstructorSymbol> ctors = record.getConstructors();
        assertThat(ctors, hasSize(1));
        JClassType secondParm = (JClassType) ctors.get(0).getFormalParameterTypes(Substitution.EMPTY).get(1);
        assertIsListWithTyAnnotation(secondParm);
    }

    @Test
    void targetRecordComponentReflectionTest() {
        TypeSystem ts = TypeSystem.usingClassLoaderClasspath(JavaParsingHelper.class.getClassLoader());
        JClassSymbol record = loadRecordClass(ts, "AnnotatedForRecord");
        List<JRecordComponentSymbol> components = record.getRecordComponents();
        assertThat(components, hasSize(1));

        assertThat(components.get(0).getSimpleName(), equalTo("x"));
        assertThat(components.get(0).getDeclaredAnnotations(), hasSize(1));

    }


    private static void assertIsListWithTyAnnotation(JClassType withTyAnnotation) {
        assertThat(withTyAnnotation.getSymbol().getBinaryName(), equalTo("java.util.List"));
        JTypeMirror tyArg = withTyAnnotation.getTypeArgs().get(0);
        assertThat(tyArg.getTypeAnnotations(), hasSize(1));
        assertThat(CollectionUtil.asSingle(tyArg.getTypeAnnotations()).getAnnotationSymbol().getBinaryName(),
                   equalTo("net.sourceforge.pmd.lang.java.symbols.recordclasses.TypeAnnotation"));
    }


    private static @NonNull JClassSymbol loadRecordClass(TypeSystem typeSystem, String simpleName) {
        String binaryName = "net.sourceforge.pmd.lang.java.symbols.recordclasses." + simpleName;
        JClassSymbol sym = typeSystem.getClassSymbol(binaryName);
        assertNotNull(sym, binaryName + " not found");
        assertTrue(sym.isRecord(), "is a record");
        return sym;
    }
}
