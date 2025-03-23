/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
import net.sourceforge.pmd.lang.java.ast.variableAccess
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.test.ast.component6
import net.sourceforge.pmd.lang.test.ast.shouldMatchN

/**
 *
 */
class LocalVarInferenceTest : ProcessorTestSpec({
    parserTest("Test for var inference projection") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Foo {
                static <T> void take5(Iterable<? extends T> iter) {
                    for (var entry : iter) { } // entry is projected to `T`, not `? extends T`
                }
            }
            """.trimIndent()
        )

        val tvar = acu.typeVariables()[0]
        val entryId = acu.varId("entry")
        val iterAccess = acu.varAccesses("iter")[0]!!

        spy.shouldBeOk {
            entryId shouldHaveType tvar // not ? extends T
            iterAccess shouldHaveType gen.t_Iterable[captureMatcher(`?` extends tvar)]
        }
    }

    parserTest("Test local var inference") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Foo {{
                var map = new java.util.HashMap<Object, int[]>(((4 * convCount) / 3) + 1);
                for (var entry : map.entrySet()) {
                    int[] positions = entry.getValue();
                }
            }}
            """
        )

        val (entrySet, getValue) = acu.methodCalls().toList()

        spy.shouldBeOk {
            val entryType = java.util.Map.Entry::class[ts.OBJECT, int.toArray()]
            entrySet shouldHaveType java.util.Set::class[entryType]
            getValue shouldHaveType int.toArray()
            getValue.qualifier!!.shouldMatchN {
                variableAccess("entry") {
                    it shouldHaveType entryType
                }
            }
        }
    }

    parserTest("Unbounded wild is projected to upper bound of its underlying tvar") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
class Scratch<S extends Scratch<S>> {

    public S getS() {return null;}

    static {
        Scratch<?> unbounded = null;
        // Scratch<? extends Scratch<?>>,
        // by projection
        var k = unbounded.getS();
    }
}
            """.trimIndent()
        )

        val t_Scratch = acu.firstTypeSignature()

        spy.shouldBeOk {
            acu.varId("k") shouldHaveType t_Scratch[`?` extends t_Scratch[`?`]]
        }
    }

    parserTest("Local var for anonymous") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
class Scratch {

    static {
        var k = new Runnable() {};
    }
}
            """.trimIndent()
        )

        spy.shouldBeOk {
            // not the anon type
            acu.varId("k") shouldHaveType Runnable::class.decl
        }
    }
    parserTest("Local var with conditional and 2 poly expressions") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
interface Collector<T, A, R> {}
interface Stream<T> {
    <R, A> R collect(Collector<? super T, A, R> collector);
}
interface List<T> {}
interface Function<T,R> { R apply(T t); }
public class Scratch {

    static <T, R> Collector<T, ?, List<List<T>>> groupingByNullable(Function<? super T, ? extends R> key){}
    static {
        var lookup = System.currentTimeMillis() > 100L // permission based
            ? Dao.source1().collect(groupingByNullable(Data::parent)) // DAO call #1
            : Dao.source2().collect(groupingByNullable(Data::parent)); // DAO call #2
    }
    
    interface Data {
        Integer parent();
    }
    static class Dao {
       static Stream<Data> source1(){}
       static Stream<Data> source2(){}
    }
}
            """.trimIndent()
        )

        spy.shouldBeOk {
            val (_, _, list, _, _, data) = acu.declaredTypeSignatures()
            // not the anon type
            acu.varId("lookup") shouldHaveType list[list[data]]
        }
    }
})
