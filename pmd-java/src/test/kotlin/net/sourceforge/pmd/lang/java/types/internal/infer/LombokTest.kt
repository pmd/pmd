/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J9
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProperties
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.test.ast.IntelliMarker
import net.sourceforge.pmd.lang.test.ast.shouldBeA

/**
 * @author Clément Fournier
 */
class LombokTest : IntelliMarker, ProcessorTestSpec({

    fun JavaParsingHelper.disableLombok() =
        withLanguageProperties {
            (this as JavaLanguageProperties).setProperty(JavaLanguageProperties.FIRST_CLASS_LOMBOK, false)
        }

    val lombokValCode = """
            import lombok.val;
            import java.util.List;
            class Archive {
                void something(java.util.List l) {
                    val q = 2f;
                    val n = l;
                }
            }
            """.trimIndent()

    parserTest("Lombok val is inferred in all versions", javaVersions = JavaVersion.since(JavaVersion.Earliest)) {
        val acu = parser.parse(lombokValCode)

        acu.withTypeDsl {
            acu.varId("q").let {
                it shouldHaveType float
                it.isTypeInferred shouldBe true
                it.isFinal shouldBe true
                it.ancestors(ASTLocalVariableDeclaration::class.java).firstOrThrow().isFinal shouldBe true
            }
            acu.varId("n") shouldHaveType java.util.List::class.raw
        }
    }


    parserTest(
        "Lombok val is inferred in all versions, except when language property is disabled",
        javaVersions = JavaVersion.since(JavaVersion.Earliest)
    ) {
        val acu = parser.disableLombok().parse(lombokValCode)

        acu.withTypeDsl {
            acu.varId("q").let {
                it.isTypeInferred shouldBe false
                it.isFinal shouldBe false
                it.typeMirror.shouldBeA<JClassType> {
                    it.symbol.canonicalName shouldBe "lombok.val"
                }
                it.ancestors(ASTLocalVariableDeclaration::class.java).firstOrThrow().isFinal shouldBe false
            }
            acu.varId("n").let {
                it.isTypeInferred shouldBe false
                it.isFinal shouldBe false
                it.typeMirror.shouldBeA<JClassType> {
                    it.symbol.canonicalName shouldBe "lombok.val"
                }
            }
        }
    }

    val lombokVarCode =
        """
            import lombok.val;
            import lombok.var;
            import java.util.List;
            class Archive {
                void something(java.util.List l) {
                    var i = 0;
                }
            }
            """.trimIndent()

    parserTest("Lombok var is inferred (when version <= 9)", javaVersions = JavaVersion.until(J9)) {
        val acu = parser.parse(lombokVarCode)

        acu.withTypeDsl {
            acu.varId("i").let {
                it.isTypeInferred shouldBe true
                it shouldHaveType int
                it.isFinal shouldBe false
            }
        }
    }

    parserTest(
        "Lombok var is not inferred when language property is disabled",
        javaVersions = JavaVersion.until(J9)
    ) {
        val acu = parser.disableLombok().parse(lombokVarCode)

        acu.withTypeDsl {
            acu.varId("i").let {
                it.isTypeInferred shouldBe false
                it.isFinal shouldBe false
                it.typeMirror.shouldBeA<JClassType> {
                    it.symbol.canonicalName shouldBe "lombok.var"
                }
            }
        }
    }


    parserTest(
        "var is inferred in java >= 10 (but not because of lombok)",
        javaVersions = JavaVersion.since(JavaVersion.J10)
    ) {
        val acu = parser.disableLombok().parse(lombokVarCode)

        acu.withTypeDsl {
            acu.varId("i").let {
                it.isTypeInferred shouldBe true
                it shouldHaveType int
                it.typeNode shouldBe null
                it.isFinal shouldBe false
            }
        }
    }



    parserTestContainer("Lombok @Slf4j annotation") {

        val code =
            """
            import lombok.extern.slf4j.Slf4j;
            @Slf4j
            public class Book {
                public void logMe() {
                    log.info("Hello, %s!");
                }
            }
            """.trimIndent()


        doTest("Creates a log field when supported") {
            val acu = parser.parse(code)
            acu.withTypeDsl {
                val qualifier = acu.firstMethodCall().qualifier!!
                qualifier shouldHaveType org.slf4j.Logger::class.decl
                qualifier.shouldBeA<ASTVariableAccess>()
            }
        }

        doTest("Do nothing when lombok support is disabled") {
            val acu = parser.disableLombok().parse(code)
            acu.withTypeDsl {
                val qualifier = acu.firstMethodCall().qualifier!!
                qualifier shouldHaveType ts.UNKNOWN
                qualifier.shouldBeA<ASTAmbiguousName>()
            }
        }
    }

})
