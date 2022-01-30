/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe

class OverrideResolutionTest : ProcessorTestSpec({

    parserTest("Test override resolution prefers superclass method") {
        val acu = parser.parse("""
            interface Foo { default void foo() {} }
            interface Bar { default void foo() {} }
            class Sup { public void foo() {} }

            public class Sub extends Sup implements Foo, Bar {
                public void foo() {
                    super.foo(); // useless
                }
            }
        """)
        val (fooFoo, barFoo, supFoo, subFoo) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        subFoo.overriddenMethod shouldBe supFoo.genericSignature
        barFoo.overriddenMethod shouldBe null
        fooFoo.overriddenMethod shouldBe null
        supFoo.overriddenMethod shouldBe null
    }

    parserTest("Test override resolution without superclass") {
        val acu = parser.parse("""
            interface Foo { default void foo() {} }
            interface Bar { default void foo() {} }
            class Sup implements Bar { public void foo() {} }

            public class Sub implements Foo, Bar {
                public void foo() {
                    super.foo(); // useless
                }
            }
        """)
        val (fooFoo, barFoo, supFoo, subFoo) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        supFoo.overriddenMethod shouldBe barFoo.genericSignature
        subFoo.overriddenMethod shouldBe fooFoo.genericSignature
        barFoo.overriddenMethod shouldBe null
        fooFoo.overriddenMethod shouldBe null
    }

    parserTest("Test override resolution unresolved") {
        val acu = parser.parse("""
            public class Sub implements Unresolved {
                @Override
                public void foo() {
                }
            }
        """)
        val (subFoo) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        subFoo.overriddenMethod shouldBe subFoo.typeSystem.UNRESOLVED_METHOD
    }

})
