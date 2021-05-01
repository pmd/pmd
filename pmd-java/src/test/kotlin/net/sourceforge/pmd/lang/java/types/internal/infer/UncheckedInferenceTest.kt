/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*

class UncheckedInferenceTest : ProcessorTestSpec({

    parserTest("Test raw type in argument erases result") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

class C {

    static <T extends Comparable<?>> T valueOf(Class<T> k) { return null; } 

    static {
        var c = valueOf((Class) Object.class);
    }
}

                """.trimIndent()
        )

        val (t_C) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()
        val id = acu.descendants(ASTVariableDeclaratorId::class.java).first { it.name == "c" }!!

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                    named = "valueOf",
                    declaredIn = t_C,
                    withFormals = listOf(Class::class[gen.t_Comparable[`?`]]),
                    returning = gen.t_Comparable
            )
            call shouldHaveType gen.t_Comparable
            id shouldHaveType gen.t_Comparable
            call.shouldUseUncheckedConversion()
        }
    }

    parserTest("Test raw type erases result (return type is Class<T>)") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

class C {

    static <T extends Comparable<?>> Class<T> valueOf(Class<T> k) { return null; } 

    static {
        Class<?> c = valueOf((Class) Object.class);
    }
}

                """.trimIndent()
        )

        val (t_C) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.firstMethodCall()
        val id = acu.descendants(ASTVariableDeclaratorId::class.java).first { it.name == "c" }!!

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                    named = "valueOf",
                    declaredIn = t_C,
                    withFormals = listOf(Class::class[gen.t_Comparable[`?`]]),
                    returning = Class::class.raw
            )
            call shouldHaveType Class::class.raw
            id shouldHaveType Class::class[`?`]
            call.shouldUseUncheckedConversion()
        }
    }

    parserTest("Test f-bound on raw type, explicit Object bound") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
import java.util.*;

class C {

    public static <T> T min(Collection<? extends T> coll, Comparator<? super T> comp) {
        if (comp==null)
            return (T) min((Collection) coll);
        return null;
    }

    public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> coll) {
        return null;
    }

}

                """.trimIndent()
        )

        val (t_C) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                    named = "min",
                    declaredIn = t_C,
                    withFormals = listOf(gen.t_Collection[`?` extends gen.t_Comparable]), // Comparable is raw
                    returning = gen.t_Comparable // not Object
            )
            call shouldHaveType gen.t_Comparable
            call.shouldUseUncheckedConversion()
        }
    }

    parserTest("Test f-bound on raw type") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

class C {

    static <T extends Enum<T>> T valueOf(Class<T> k) { return null; } 

    static {
        var c = valueOf((Class) Object.class);
    }
}

                """.trimIndent()
        )

        val (t_C) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()
        val id = acu.descendants(ASTVariableDeclaratorId::class.java).first { it.name == "c" }!!


        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                    named = "valueOf",
                    declaredIn = t_C,
                    withFormals = listOf(Class::class[gen.t_Enum]),
                    returning = gen.t_Enum
            )
            call shouldHaveType gen.t_Enum
            id shouldHaveType gen.t_Enum
            call.shouldUseUncheckedConversion()
        }
    }


    parserTest("TODO unchecked assignment for intersection") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
class Scratch<N extends Number> {

    interface I {}

    static <N2 extends Number & I> N2 getN() {return null;}

    public void main() {
        N n = getN(); // unchecked assignment Scratch.I to N
    }
}
        """)

        val (t_Scratch, t_I) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
        val (nvar) = acu.descendants(ASTTypeParameter::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                    named = "getN",
                    declaredIn = t_Scratch,
                    withFormals = emptyList(),
                    returning = nvar * t_I
            )
        }
    }

    parserTest("Raw type as target type") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
import java.util.List;
class Scratch {
    static {
        List l = asList("");
    }
    static <T> List<T> asList(T... ts) { return null; }
}
        """)

        val (t_Scratch) = acu.typeDeclarations.toList { it.typeMirror }
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.overloadSelectionInfo.isFailed shouldBe false
            call.methodType.shouldMatchMethod(
                    named = "asList",
                    declaredIn = t_Scratch,
                    withFormals = listOf(gen.t_String.toArray()),
                    returning = gen.`t_List{String}`
            )
        }
    }

    parserTest("Type with raw bound") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
// Note: Enum is raw, not Enum<T>
class StringToEnum<T extends Enum> implements Converter<String, T> {

    private final Class<T> enumType;

    public StringToEnum(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T convert(String source) {
        // Because `this.enumType` is raw, the expr `Enum.valueOf(...)` has its 
        // return type erased to Enum
        // So the cast is necessary
        return (T) Enum.valueOf(this.enumType, source.trim());
    }
}

interface Converter<From, To> {
    To convert(From source);
}
        """)

        val call = acu.firstMethodCall()
        val tparam = acu.typeVar("T")

        spy.shouldBeOk {
            call.overloadSelectionInfo::isFailed shouldBe false
            call.overloadSelectionInfo::needsUncheckedConversion shouldBe true
            call.methodType.shouldMatchMethod(
                    named = "valueOf",
                    declaredIn = Enum::class.raw,
                    withFormals = listOf(Class::class[tparam], gen.t_String),
                    returning = Enum::class.raw
            )
        }
    }

})
