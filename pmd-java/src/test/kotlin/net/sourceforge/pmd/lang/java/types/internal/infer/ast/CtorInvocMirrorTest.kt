/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast

import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
import net.sourceforge.pmd.lang.java.types.shouldBeUnresolvedClass

class CtorInvocMirrorTest : ProcessorTestSpec({

    parserTest("Qualified constructor invocation with unresolved types") {
        val acu = parser.parse(
                """
                class Foo {
                    void bar() {
                        Foo myObject = new Foo();
                        myObject.new Nested();
                    }
                
                    class Nested {}
                }
                """)
        val invocation = acu.descendants(ASTConstructorCall::class.java).get(1)!!
        invocation.typeMirror shouldNotBe null
        invocation.typeMirror.shouldBeUnresolvedClass("Foo.Nested")
    }

    parserTest("Qualified constructor invocation with unresolved types uncompilable") {
        val acu = parser.parse(
                """
                class Foo {
                    void bar() {
                        Foo myObject = new Foo();
                        myObject.new Nested();
                    }
                
                    //the nested type is not declared, so this code actually doesn't compile
                    //but PMD should not crash
                    //class Nested {}
                }
                """)
        val invocation = acu.descendants(ASTConstructorCall::class.java).get(1)!!
        invocation.typeMirror shouldNotBe null
        invocation.typeMirror.shouldBeSameInstanceAs(invocation.typeSystem.UNKNOWN)
    }
})
