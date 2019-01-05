package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.SomeFields
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.SomeMethodsNoOverloads
import net.sourceforge.pmd.lang.java.symbols.parse

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class JAccessibleDeclarationTests : FunSpec({

    fun <T, K> List<T>.groupByUnique(keySelector: (T) -> K): Map<K, T> =
            groupBy(keySelector).mapValues { (_, vs) ->
                vs should haveSize(1)
                vs.first()
            }



    test("Test field declaration symbol equivalence") {
        // test equivalence between the representation obtained from the AST,
        // and the one obtained from reflection

        val acu = SomeFields::class.java.parse()

        val fieldNodes =
                acu.findDescendantsOfType(ASTFieldDeclaration::class.java)
                        .flatten() // gets the declarator ids
                        .groupByUnique { it.image!! }

        val reflectedFields = SomeFields::class.java.declaredFields.toList().groupByUnique { it.name }

        fun onFieldSymbol(fieldName: String, behaviourTest: (JFieldSymbol) -> Unit) {
            val fromAst = JFieldSymbol(fieldNodes[fieldName])
            val fromReflect = JFieldSymbol(reflectedFields[fieldName])

            fromAst shouldBe fromReflect

            fun testBehaviour(it: JFieldSymbol) {
                it.isField shouldBe true
                it.isLocalVar shouldBe false
                behaviourTest(it)
            }

            // both must pass exactly the same assertions
            testBehaviour(fromAst)
            testBehaviour(fromReflect)
        }

        // public final String foo = "";
        onFieldSymbol("foo") {
            it.isPrivate shouldBe false
            it.isPackagePrivate shouldBe false
            it.isPublic shouldBe true
            it.isProtected shouldBe false

            it.isStatic shouldBe false
            it.isTransient shouldBe false
            it.isFinal shouldBe true
            it.isVolatile shouldBe false
        }

        // private int a;
        onFieldSymbol("a") {
            it.isPrivate shouldBe true
            it.isPackagePrivate shouldBe false
            it.isPublic shouldBe false
            it.isProtected shouldBe false

            it.isFinal shouldBe false
            it.isVolatile shouldBe false
        }

        // protected volatile int bb;
        onFieldSymbol("bb") {
            it.isPrivate shouldBe false
            it.isPackagePrivate shouldBe false
            it.isPublic shouldBe false
            it.isProtected shouldBe true

            it.isFinal shouldBe false
            it.isVolatile shouldBe true
        }
    }


    test("Test method declaration symbol equivalence") {

        // This test data can't have overloads, there's a separate test case for that
        val acu = SomeMethodsNoOverloads::class.java.parse()

        val methodNodes =
                acu.findDescendantsOfType(ASTMethodDeclaration::class.java, true)
                        .groupByUnique { it.methodName!! }

        fun Class<*>.methodMap() = declaredMethods.toList().groupByUnique { it.name!! }

        val reflectedFields =
                SomeMethodsNoOverloads::class.java.methodMap() +
                SomeMethodsNoOverloads.Other::class.java.methodMap()

        fun onMethodSymbol(fieldName: String, behaviourTest: (JMethodSymbol) -> Unit) {
            val fromAst = JMethodSymbol(methodNodes[fieldName])
            val fromReflect = JMethodSymbol(reflectedFields[fieldName])

            fromAst shouldBe fromReflect

            // both must pass exactly the same assertions
            behaviourTest(fromAst)
            behaviourTest(fromReflect)
        }

        // public final String foo() {
        onMethodSymbol("foo") {
            it.isPrivate shouldBe false
            it.isPackagePrivate shouldBe false
            it.isPublic shouldBe true
            it.isProtected shouldBe false

            it.isStatic shouldBe false
            it.isFinal shouldBe true
            it.isDefault shouldBe false
            it.isAbstract shouldBe false
        }

        // default int defaultMethod() {
        onMethodSymbol("defaultMethod") {
            it.isPrivate shouldBe false
            it.isPackagePrivate shouldBe false
            it.isPublic shouldBe true
            it.isProtected shouldBe false

            it.isFinal shouldBe false
            it.isDefault shouldBe true
        }

    }

})