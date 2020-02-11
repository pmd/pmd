/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import net.sourceforge.pmd.lang.java.types.typeDsl

/**
 * @author Clément Fournier
 */
class CaptureInferenceTest : ProcessorTestSpec({

    parserTest("Test capture incompatibility recovery") {

        asIfIn(TypeInferenceTestCases::class.java)


        inContext(TypeBodyParsingCtx) {

            val normalizer = CaptureNormalizer()

            val getCall = doParse("""
                void something(List<?> l) {
                    l.set(1, l.get(0)); // captured, fails
                }
            """)
                    .descendants(ASTMethodCall::class.java).get(1)!!

            normalizer.normalizeCaptures(getCall.typeMirror.toString()) shouldBe "capture#1 of ?"
            normalizer.normalizeCaptures(getCall.methodType.toString()) shouldBe "java.util.List<capture#1 of ?>.get(int) -> capture#1 of ?"

            val setCall = getCall.ancestors(ASTMethodCall::class.java).first()!!

            // we still get a type
            normalizer.normalizeCaptures(setCall.methodType.toString()) shouldBe "java.util.List<capture#2 of ?>.set(int, capture#2 of ?) -> capture#2 of ?"

        }
    }


    parserTest("Test lower wildcard compatibility") {


        val acu = parser.parse("""
           package java.lang;

           import java.util.Iterator;
           import java.util.function.Consumer;

           public interface Iterable<T> {
               Iterator<T> iterator();

               default void forEach(Consumer<? super T> action) {
                   for (T t : this) {
                       action.accept(t);
                   }
               }
           }

        """.trimIndent())

        val tVar = acu.descendants(ASTTypeParameter::class.java).first()!!.symbol.typeMirror

        val call = acu.descendants(ASTMethodCall::class.java).first()!!

        call.shouldMatchN {
            methodCall("accept") {
                it::getTypeMirror shouldBe with(it.typeDsl) { ts.NO_TYPE }

                variableAccess("action") {}
                argList {
                    variableAccess("t") {
                        it.typeMirror shouldBe tVar
                    }
                }
            }
        }
    }

})

/**
 * Captured variables have unique identifiers, that should
 * be normalized when writing tests.
 */
class CaptureNormalizer {

    var curNum = 0

    val map = mutableMapOf<Int, String>()

    val r = Regex("(?<=capture#)-?\\d+")

    fun normalizeCaptures(s: String): String {

        return r.replace(s) {
            val num = it.value.toInt()

            if (num in map) map[num]!!
            else {
                curNum++
                map[num] = curNum.toString()
                curNum.toString()
            }
        }

    }

}


