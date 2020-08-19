/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
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
            call.typeMirror shouldBe gen.t_Comparable
            id.typeMirror shouldBe gen.t_Comparable
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
            call.typeMirror shouldBe Class::class.raw
            id.typeMirror shouldBe Class::class[`?`]
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
            call.typeMirror shouldBe gen.t_Comparable
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
            call.typeMirror shouldBe gen.t_Enum
            id.typeMirror shouldBe gen.t_Enum
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

})
