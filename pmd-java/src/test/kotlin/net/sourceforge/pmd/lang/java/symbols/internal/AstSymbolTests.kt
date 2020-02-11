package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.component6
import net.sourceforge.pmd.lang.ast.test.component7
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration
import net.sourceforge.pmd.lang.java.ast.ParserTestSpec
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectSymInternals
import java.lang.reflect.Modifier

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class AstSymbolTests : ParserTestSpec({

    parserTest("Parsed class symbols") {

        val acu = parser.withProcessing().parse("""

            package com.foo;

            public final class Foo extends java.util.List {

                void bar() {

                }

                private void ohio() {

                }

                private interface Inner {}


                enum EE {
                    A,
                    B
                }
            }
        """)

        val (fooClass, innerItf, innerEnum) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

        val (barM, ohioM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.symbol }



        doTest("should reflect their modifiers") {
            fooClass::getModifiers shouldBe (Modifier.PUBLIC or Modifier.FINAL)
            innerItf::getModifiers shouldBe (Modifier.PRIVATE or Modifier.ABSTRACT or Modifier.STATIC)
            innerEnum::getModifiers shouldBe (Modifier.FINAL or Modifier.STATIC)
        }

        doTest("should reflect their simple names properly") {
            fooClass::getSimpleName shouldBe "Foo"
            innerItf::getSimpleName shouldBe "Inner"
            innerEnum::getSimpleName shouldBe "EE"
        }

        doTest("should reflect their canonical names properly") {
            fooClass::getCanonicalName shouldBe "com.foo.Foo"
            innerItf::getCanonicalName shouldBe "com.foo.Foo.Inner"
            innerEnum::getCanonicalName shouldBe "com.foo.Foo.EE"
        }

        doTest("should reflect their methods") {
            fooClass.declaredMethods shouldBe listOf(barM, ohioM)
        }

        doTest("should fetch their methods properly") {
            fooClass.getDeclaredMethods("ohio") shouldBe listOf(ohioM)
        }

        doTest("should reflect their super class") {
            // Postponed
            // fooClass::getSuperclass shouldBe testSymFactory.getClassSymbol(java.util.List::class.java)

            innerEnum::getSuperclass shouldBe ReflectSymInternals.ENUM_SYM
        }

        doTest("should reflect their member types") {
            fooClass.declaredClasses shouldBe listOf(innerItf, innerEnum)
            innerEnum.declaredClasses shouldBe emptyList()
            innerItf.declaredClasses shouldBe emptyList()
        }

        doTest("should reflect their declaring type") {
            fooClass::getEnclosingClass shouldBe null
            innerEnum::getEnclosingClass shouldBe fooClass
            innerItf::getEnclosingClass shouldBe fooClass
        }

        doTest("(enums) should reflect enum constants as fields") {
            val constants =
                    acu.descendants(ASTEnumDeclaration::class.java)
                            .take(1)
                            .flatMap { it.enumConstants }
                            .toList { it.varId.symbol as JFieldSymbol }

            constants.shouldHaveSize(2)

            innerEnum.declaredFields.shouldBe(constants)

            constants.forEach {
                it::isEnumConstant shouldBe true
                it::getModifiers shouldBe (Modifier.PUBLIC or Modifier.STATIC or Modifier.FINAL)
            }
        }
    }


    parserTest("Default/implicit constructors") {

        val acu = parser.withProcessing().parse("""

            package com.foo;

            public final class Foo extends java.util.List {

                private interface Inner {}
                private @interface Annot {}


                enum EE { A, B }
                enum E2 {
                    A, B;
                    E2(String s) {}
                }

                class BClass {
                    protected BClass() { }
                }
                class CClass {
                    // default, package private
                }
            }
        """)

        val (fooClass, innerItf, innerAnnot, e1, e2, bClass, cClass) =
                acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

        doTest("Annotations/itfs should have no constructors") {
            innerItf.constructors.shouldBeEmpty()
            innerAnnot.constructors.shouldBeEmpty()
        }

        doTest("Classes should have a default constructor if there are none") {
            fooClass.constructors[0].shouldBeA<JConstructorSymbol> {
                it.formalParameters.shouldBeEmpty()
                it.modifiers shouldBe Modifier.PUBLIC
            }

            cClass.constructors[0].shouldBeA<JConstructorSymbol> {
                it.formalParameters.shouldBeEmpty()
                it.modifiers shouldBe 0
            }
        }

        doTest("Classes should have no default constructor if there are explicit ones") {

            bClass.constructors[0].shouldBeA<JConstructorSymbol> {
                it.formalParameters.shouldBeEmpty()
                it.modifiers shouldBe Modifier.PROTECTED
            }
        }

        doTest("Enums should have an implicit private constructor") {
            e1.constructors.shouldHaveSize(1)
            e1.constructors[0].shouldBeA<JConstructorSymbol> {
                it::getModifiers shouldBe Modifier.PRIVATE
                it::getFormalParameters shouldBe emptyList()
            }
        }

        doTest("Enums should have no implicit constructor if there are explicit ones") {
            e2.constructors.shouldHaveSize(1)
            e2.constructors[0].shouldBeA<JConstructorSymbol> { ctor ->
                ctor::getModifiers shouldBe Modifier.PRIVATE
                ctor.formalParameters.shouldHaveSize(1)
                ctor.formalParameters[0].shouldBeA<JFormalParamSymbol> {
                    it::getDeclaringSymbol shouldBe ctor
                    it::getSimpleName shouldBe "s"
                }
            }
        }
    }

    parserTest("Enum implicit methods") {

        val acu = parser.withProcessing().parse("enum EE { A, B }")

        val (enum) =
                acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

        doTest("Enums should have a values() method") {
            val values = enum.getDeclaredMethods("values")
            values.shouldHaveSize(1)
            values[0].shouldBeA<JMethodSymbol> {
                it::getEnclosingClass shouldBe enum
                it::getModifiers shouldBe (Modifier.PUBLIC or Modifier.STATIC)
                it::getSimpleName shouldBe "values"
            }
        }

        doTest("Enums should have a valueOf() method") {
            val values = enum.getDeclaredMethods("valueOf")
            values.shouldHaveSize(1)
            values[0].shouldBeA<JMethodSymbol> {
                it::getEnclosingClass shouldBe enum
                it::getModifiers shouldBe (Modifier.PUBLIC or Modifier.STATIC)
                it::getSimpleName shouldBe "valueOf"
            }
        }
    }

})
