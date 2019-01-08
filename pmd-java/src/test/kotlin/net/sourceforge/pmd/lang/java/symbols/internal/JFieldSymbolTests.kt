package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId
import net.sourceforge.pmd.lang.java.symbols.getAst
import net.sourceforge.pmd.lang.java.symbols.groupByUnique
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.IdenticalToSomeFields
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.SomeFields

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class JFieldSymbolTests : WordSpec({

    // Add field tests
    run {

        fun Class<*>.getAstFieldsByName(): Map<String, ASTVariableDeclaratorId> {
            return this.getAst()
                    .findDescendantsOfType(ASTFieldDeclaration::class.java)
                    .flatten() // gets the declarator ids
                    .groupByUnique { it.image!! }
        }

        val reflectedFields =
                SomeFields::class.java.declaredFields.toList().groupByUnique { it.name }
        val reflectedFieldsInOtherClass =
                IdenticalToSomeFields::class.java.declaredFields.toList().groupByUnique { it.name }

        val fieldNodes =
                SomeFields::class.java.getAstFieldsByName()
        val sameFieldsInOtherClass =
                IdenticalToSomeFields::class.java.getAstFieldsByName()


        fun onFieldSymbol(fieldName: String, behaviourTest: (JFieldSymbol) -> Unit) {
            val fromAst = JFieldSymbol(fieldNodes[fieldName])
            val fromReflect = JFieldSymbol(reflectedFields[fieldName])

            // test that a field with the exact same declaration but not in the same class is not equivalent
            val fromOtherAst = JFieldSymbol(sameFieldsInOtherClass[fieldName])
            val fromOtherReflected = JFieldSymbol(reflectedFieldsInOtherClass[fieldName])

            "A field symbol ($fieldName) obtained from an AST" should {

                "be equivalent to the same field symbol obtained through reflection" {
                    fromAst shouldBe fromReflect
                    fromReflect shouldBe fromAst


                    fun testBehaviour(it: JFieldSymbol) {
                        it.isField shouldBe true
                        it.isLocalVar shouldBe false
                        behaviourTest(it)
                    }

                    // both must pass exactly the same assertions
                    testBehaviour(fromAst)
                    testBehaviour(fromReflect)
                }

                "not be equivalent to an identical field symbol declared in another class" {
                    fromAst shouldNotBe fromOtherAst
                    fromAst shouldNotBe fromOtherReflected
                }
            }

            "A field symbol ($fieldName) obtained from reflection" should {

                "not be equivalent to an identical field symbol belonging to another class" {
                    fromReflect shouldNotBe fromOtherReflected
                    fromReflect shouldNotBe fromOtherAst
                }
            }
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



})