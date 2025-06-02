/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.test.ast.shouldBe

class Java15KotlinTest : ParserTestSpec({
    // Note: More tests are in ASTLiteralTest.
    parserTestContainer("textBlocks", javaVersions = JavaVersion.J15..JavaVersion.Latest) {
        val tblock = "\"\"\"\n" +
                // 4 spaces of insignificant indentation
                "    <html>   \n" +
                "        <body>\n" +
                "            <p>Hello, world</p>    \n" +
                "        </body> \n" +
                "    </html>   \n" +
                "    \"\"\""

        inContext(ExpressionParsingCtx) {
            tblock should parseAs {
                textBlock {
                    it::getConstValue shouldBe "<html>\n" +
                            "    <body>\n" +
                            "        <p>Hello, world</p>\n" +
                            "    </body>\n" +
                            "</html>\n"

                    it.literalText.toString() shouldBe tblock
                }
            }
        }
    }
})
