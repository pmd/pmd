package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration
import net.sourceforge.pmd.lang.java.symbols.getAst
import net.sourceforge.pmd.lang.java.symbols.groupByUnique
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.IdenticalToSomeFields
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.SomeMethodsNoOverloads

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class JMethodSymbolTest : WordSpec({

    // Add method tests
    // The test data here can't have overloads because of the map..
    // TODO add a separate test case for that
    run {

        fun Class<*>.getAstMethodsByName(): Map<String, ASTMethodDeclaration> {
            return this.getAst()
                    .findDescendantsOfType(ASTMethodDeclaration::class.java, true)
                    .groupByUnique { it.methodName!! }
        }

        fun Class<*>.getReflectedMethodsByName() = declaredMethods.toList().groupByUnique { it.name!! }

        val reflectedMethods =
                SomeMethodsNoOverloads::class.java.getReflectedMethodsByName() +
                SomeMethodsNoOverloads.Other::class.java.getReflectedMethodsByName()

        val sameReflectedMethodsInOtherClass =
                IdenticalToSomeFields::class.java.getReflectedMethodsByName() +
                IdenticalToSomeFields.Other::class.java.getReflectedMethodsByName()

        val methodNodes = SomeMethodsNoOverloads::class.java.getAstMethodsByName()
        val sameNodesInOtherClass = IdenticalToSomeFields::class.java.getAstMethodsByName()

        fun onMethodSymbol(methodName: String, behaviourTest: (JMethodSymbol) -> Unit) {
            val fromAst = JMethodSymbol(methodNodes[methodName])
            val fromOtherAst = JMethodSymbol(sameNodesInOtherClass[methodName])
            val fromReflect = JMethodSymbol(reflectedMethods[methodName])
            val fromOtherReflected = JMethodSymbol(sameReflectedMethodsInOtherClass[methodName])

            fromAst shouldBe fromReflect

            // both must pass exactly the same assertions
            behaviourTest(fromAst)
            behaviourTest(fromReflect)


            "A method symbol ($methodName) obtained from an AST" should {

                "be equivalent to the same method symbol obtained through reflection" {
                    fromAst shouldBe fromReflect
                    fromReflect shouldBe fromAst

                    // both must pass exactly the same assertions
                    behaviourTest(fromAst)
                    behaviourTest(fromReflect)
                }

                "not be equivalent to an identical method symbol belonging to another class" {
                    fromAst shouldNotBe fromOtherAst
                    fromAst shouldNotBe fromOtherReflected
                }
            }

            "A method symbol ($methodName) obtained from reflection" should {

                "not be equivalent to an identical method symbol belonging to another class" {
                    fromReflect shouldNotBe fromOtherReflected
                    fromReflect shouldNotBe fromOtherAst
                }
            }
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