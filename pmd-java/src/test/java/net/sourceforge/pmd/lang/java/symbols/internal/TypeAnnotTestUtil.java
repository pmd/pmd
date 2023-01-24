/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.AnnotationUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.sourceforge.pmd.lang.java.symbols.AnnotableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsInside;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 *
 */
public class TypeAnnotTestUtil {


    static final List<Annotation> ANNOT_A = listOf(createAnnotationInstance(ClassWithTypeAnnotationsInside.A.class));
    static final List<Annotation> ANNOT_B = listOf(createAnnotationInstance(ClassWithTypeAnnotationsInside.B.class));
    static final List<Annotation> ANNOTS_A_B = listOf(ANNOT_A.get(0), ANNOT_B.get(0));

    private TypeAnnotTestUtil() {
        // utility class
    }

    public static JTypeMirror getFieldType(JClassType sym, String fieldName) {
        return sym.getDeclaredField(fieldName).getTypeMirror();
    }


    public static JMethodSig getMethodType(JClassType sym, String fieldName) {
        return sym.streamMethods(it -> it.nameEquals(fieldName)).findFirst().get();
    }


    public static JMethodSymbol getMethodSym(JClassSymbol sym, String fieldName) {
        return sym.getDeclaredMethods().stream().filter(it -> it.nameEquals(fieldName)).findFirst().get();
    }


    public static void assertHasTypeAnnots(JTypeMirror t, List<Annotation> annots) {
        assertNotNull(t);
        assertThat(t.getTypeAnnotations(), equalTo(annots.stream().map(a -> SymbolicValue.of(t.getTypeSystem(), a)).collect(Collectors.toSet())));
    }

    public static void assertHasAnnots(AnnotableSymbol t, List<Annotation> annots) {
        assertNotNull(t);
        assertThat(t.getDeclaredAnnotations(), equalTo(annots.stream().map(a -> SymbolicValue.of(t.getTypeSystem(), a)).collect(Collectors.toSet())));
    }

    public static <A extends Annotation> A createAnnotationInstance(Class<A> annotationClass) {
        return createAnnotationInstance(annotationClass, Collections.emptyMap());
    }

    /**
     * Creates a fake instance of the annotation class using a {@link Proxy}.
     * The proxy tries to be a faithful implementation of an annotation, albeit simple.
     * It can use the provided map to get attribute values. If an attribute is
     * not provided in the map, it will use the default value defined on the
     * method declaration. If there is no defined default value, the invocation
     * will fail.
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A createAnnotationInstance(Class<A> annotationClass, Map<String, Object> attributes) {
        return (A) Proxy.newProxyInstance(annotationClass.getClassLoader(), new Class[] { annotationClass }, (proxy, method, args) -> {
            if (method.getName().equals("annotationType") && args == null) {
                return annotationClass;
            } else if (method.getName().equals("toString") && args == null) {
                return AnnotationUtils.toString((Annotation) proxy);
            } else if (method.getName().equals("hashCode") && args == null) {
                return AnnotationUtils.hashCode((Annotation) proxy);
            } else if (method.getName().equals("equals") && args.length == 1) {
                if (args[0] instanceof Annotation) {
                    return AnnotationUtils.equals((Annotation) proxy, (Annotation) args[0]);
                }
                return false;
            } else if (attributes.containsKey(method.getName()) && args == null) {
                return attributes.get(method.getName());
            } else if (method.getDefaultValue() != null && args == null) {
                return method.getDefaultValue();
            }

            throw new UnsupportedOperationException("Proxy does not implement " + method);
        });
    }

    private static Matcher<SymAnnot> matchesAnnot(Annotation o) {
        return new BaseMatcher<SymAnnot>() {
            @Override
            public boolean matches(Object actual) {
                return actual instanceof SymAnnot && ((SymAnnot) actual).valueEquals(o);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an annotation like " + o);
            }
        };
    }


}
