/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.SelfDescribing;
import org.junit.Assert;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JIntersectionType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.JWildcardType;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind;

/**
 *
 */
public class BaseTypeInferenceUnitTest {


    protected final TypeSystem ts = JavaParsingHelper.TEST_TYPE_SYSTEM;
    protected final JClassSymbol listSym = ts.getClassSymbol(List.class);

    protected InferenceContext emptyCtx() {
        return emptyCtx(TypeInferenceLogger.noop());
    }

    protected InferenceContext emptyCtx(TypeInferenceLogger log) {
        return new InferenceContext(ts, Collections.emptyList(), log);
    }


    protected InferenceVar newIvar(InferenceContext ctx) {
        JTypeVar mock = mock(JTypeVar.class);
        when(mock.getTypeSystem()).thenReturn(ts);
        when(mock.getLowerBound()).thenReturn(ts.NULL_TYPE);
        when(mock.getUpperBound()).thenReturn(ts.OBJECT);

        return ctx.addVar(mock);
    }

    /**
     * Note: we systematically incorporate because the order in which
     * constraints are added should not be overly specified by tests.
     * Eg whether {@code a.isConvertibleTo(b)} creates {@code 'a <: 'b} or
     * {@code 'b >: 'a} is irrelevant, provided the incorporation phase
     * properly propagates either.
     */
    protected void addSubtypeConstraint(InferenceContext ctx, JTypeMirror t, JTypeMirror s) {
        t.isConvertibleTo(s); // nota: this captures t
        ctx.incorporate();
    }

    protected void subtypeConstraintShouldFail(InferenceContext ctx, JTypeMirror t, JTypeMirror s) {
        t.isConvertibleTo(s); // nota: this captures t
        Assert.assertThrows(ResolutionFailedException.class,
                            ctx::incorporate);
    }

    public @NonNull JTypeMirror listType(JTypeMirror t) {
        return ts.parameterise(listSym, listOf(t));
    }

    public @NonNull JWildcardType extendsWild(JTypeMirror t) {
        return ts.wildcard(true, t);
    }

    public @NonNull JWildcardType superWild(JTypeMirror t) {
        return ts.wildcard(false, t);
    }

    public @NonNull JIntersectionType intersect(JTypeMirror... types) {
        JTypeMirror glb = ts.glb(Arrays.asList(types));
        assertThat(glb, Matchers.isA(JIntersectionType.class));
        return (JIntersectionType) glb;
    }

    public static Matcher<InferenceVar> hasBound(BoundKind kind, JTypeMirror t) {
        return new BaseMatcher<InferenceVar>() {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            public boolean matches(Object actual) {
                if (!(actual instanceof InferenceVar)) {
                    return false;
                }
                return ((InferenceVar) actual).getBounds(kind).contains(t);
            }
        };
    }

    /**
     * Exactly, modulo the upper(OBJECT), which can be omitted.
     */
    public static Matcher<InferenceVar> hasBoundsExactly(Bound... bounds) {
        return new BaseMatcher<InferenceVar>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("'_ ");
                Bound.describeList(description, Arrays.asList(bounds));
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                if (!(item instanceof InferenceVar)) {
                    description.appendText("Not an ivar: ").appendValue(item);
                    return;
                }
                InferenceVar ivar = (InferenceVar) item;
                description.appendText("was ");
                description.appendText(ivar.getName());
                description.appendText(" ");
                Bound.describeList(description, getBoundsObj(ivar));

            }

            @Override
            public boolean matches(Object actual) {
                if (!(actual instanceof InferenceVar)) {
                    return false;
                }
                InferenceVar ivar = (InferenceVar) actual;
                JClassType top = ivar.getTypeSystem().OBJECT;

                // note: don't use ivar.getBounds(ALL) as this would merge 'a >: T and 'a <: T
                Map<BoundKind, Set<JTypeMirror>> actualBounds = getBounds(ivar);

                // note: don't use sets/ maps to put Bound instances in,
                // as captureMatchers don't support hashing. Also don't
                // use Set::contains


                boolean expectTop = Arrays.stream(bounds).anyMatch(it -> it.kind == BoundKind.UPPER && it.t == top);

                int numToTest = actualBounds.values().stream().mapToInt(Set::size).sum();
                if (!expectTop) {
                    numToTest--;
                }

                if (numToTest != bounds.length) {
                    return false;
                }

                b:
                for (Bound bound : bounds) {
                    for (JTypeMirror t : actualBounds.getOrDefault(bound.kind, Collections.emptySet())) {
                        if (t.equals(bound.t)) {
                            numToTest--;
                            continue b;
                        }
                    }
                }

                return numToTest == 0;
            }
        };
    }

    public static @NonNull Map<BoundKind, Set<JTypeMirror>> getBounds(InferenceVar actual) {
        Map<BoundKind, Set<JTypeMirror>> actualBounds = new HashMap<>();

        for (BoundKind kind : BoundKind.values()) {
            Set<JTypeMirror> bounds = actual.getBounds(kind);
            if (!bounds.isEmpty()) {
                actualBounds.put(kind, bounds);
            }
        }

        return actualBounds;
    }

    public static @NonNull Set<Bound> getBoundsObj(InferenceVar actual) {
        Set<Bound> bounds = new LinkedHashSet<>();

        for (BoundKind kind : BoundKind.values()) {
            for (JTypeMirror t : actual.getBounds(kind)) {
                bounds.add(new Bound(kind, t));
            }
        }
        return bounds;
    }

    public static class Bound implements SelfDescribing {

        final BoundKind kind;
        final JTypeMirror t;

        Bound(BoundKind kind, JTypeMirror t) {
            this.kind = kind;
            this.t = t;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("_" + kind.getSym() + t);
        }

        public static Map<BoundKind, Set<JTypeMirror>> collectByKind(Bound... bounds) {
            return Arrays.stream(bounds).collect(Collectors.groupingBy(b -> b.kind,
                                                                       Collectors.mapping(b -> b.t, Collectors.toCollection(LinkedHashSet::new))));//mutable set
        }

        public static Bound lower(JTypeMirror t) {
            return new Bound(BoundKind.LOWER, t);
        }

        public static Bound eqBound(JTypeMirror t) {
            return new Bound(BoundKind.EQ, t);
        }

        public static Bound upper(JTypeMirror t) {
            return new Bound(BoundKind.UPPER, t);
        }

        public static Description describeList(Description description, Collection<Bound> bounds) {
            return description.appendList("{", ", ", "}", bounds);
        }
    }

}
