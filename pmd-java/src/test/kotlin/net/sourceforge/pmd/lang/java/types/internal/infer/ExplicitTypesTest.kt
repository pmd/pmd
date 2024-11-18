/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression
import net.sourceforge.pmd.lang.test.ast.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
import net.sourceforge.pmd.lang.java.types.*
import java.util.*


class ExplicitTypesTest : ProcessorTestSpec({

    // todo test explicit type args on ctor call

    parserTest("Test explicit type arguments") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
           """

           import java.util.Collection;

           public class SomeIter {
               {
                  foreach(java.util.Collections.<String>emptyList());
               }

               <T> void foreach(Collection<? super T> action) {}
           }

           """.trimIndent()
        )


        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call shouldHaveType ts.NO_TYPE
            call.arguments[0].shouldBeA<ASTMethodCall> {
                it.methodType.shouldMatchMethod(
                    named = "emptyList",
                    declaredIn = Collections::class.decl,
                    withFormals = emptyList(),
                    returning = gen.`t_List{String}`
                )
            }
        }
    }

    parserTest("Explicitly typed lambda") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """

interface Function<U,V> { 
    V apply(U u);
}
interface Comparable {}
interface Comparator<T> {
    static <U, X extends Comparable> Comparator<U> comparing(Function<? super U, ? extends X> fun) {}
}
interface Foo extends Comparable { Foo foo(); }

class NodeStream {
    static {
        Comparator<Foo> cmp = Comparator.comparing((Foo s) -> s.foo());           
    }

}
            """
        )

        val (t_Function, _, _, t_Foo) = acu.declaredTypeSignatures()
        val (lambda) = acu.descendants(ASTLambdaExpression::class.java).crossFindBoundaries().toList()
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            lambda shouldHaveType t_Function[t_Foo, t_Foo]
            call.overloadSelectionInfo.isFailed shouldBe false
        }
    }

    parserTest("Explicitly typed lambda with wildcard") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """

interface Number {}
interface Integer extends Number {}
interface Predicate<T> { boolean test(T t); }

class NodeStream {
    static {
         // note that the lambda is inferred to Predicate<Number> not Predicate<? super Integer> or something
         Predicate<? super Integer> p = (Number n) -> n.equals(23);        
    }

}
            """
        )

        val (t_Number, _, t_Predicate) = acu.declaredTypeSignatures()
        val (lambda) = acu.descendants(ASTLambdaExpression::class.java).crossFindBoundaries().toList()

        spy.shouldBeOk {
            lambda shouldHaveType t_Predicate[t_Number]
        }
    }
})
