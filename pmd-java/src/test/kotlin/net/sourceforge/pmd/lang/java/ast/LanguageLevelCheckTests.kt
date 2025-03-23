/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.string.shouldContain
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J10
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J9

/**
 * @author Clément Fournier
 */
class LanguageLevelCheckTests : ParserTestSpec({
    parserTestContainer("Reserved 'var' identifier (java 10+)", javaVersions = J10..Latest) {
        inContext(TopLevelTypeDeclarationParsingCtx) {
            "/*Top*/ class var { }" should throwParseException { ex ->
                ex.message.shouldContain("'var' is reserved and cannot be used as a type name")
            }
        }

        inContext(TypeBodyParsingCtx) {
            "public enum var { A }" should throwParseException { ex ->
                ex.message.shouldContain("'var' is reserved and cannot be used as a type name")
            }

            "public class var {  }" should throwParseException { ex ->
                ex.message.shouldContain("'var' is reserved and cannot be used as a type name")
            }

            "public interface var {  }" should throwParseException { ex ->
                ex.message.shouldContain("'var' is reserved and cannot be used as a type name")
            }

            "public @interface var {  }" should throwParseException { ex ->
                ex.message.shouldContain("'var' is reserved and cannot be used as a type name")
            }

            "public void var() { return; }" should parse()
        }

        inContext(StatementParsingCtx) {
            "/*Local*/ class var { }" should throwParseException { ex ->
                ex.message.shouldContain("'var' is reserved and cannot be used as a type name")
            }

            "int var = 0;" should parse() // only fail on type decls
            "var var = 0;" should parse()
        }

        inContext(ExpressionParsingCtx) {
            "(var) -> {}" should parse() // only fail on type decls
        }
    }

    parserTestContainer("Reserved 'var' identifier (pre java-10)", javaVersions = Earliest..J9) {
        inContext(TopLevelTypeDeclarationParsingCtx) {
            "/*Top*/ class var { }" should parse()
        }

        inContext(StatementParsingCtx) {
            "/*Local*/ class var { }" should parse()
            "int var = 0;" should parse()
        }
    }
})
