/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

class Java15KotlinTest : ParserTestSpec({

    // Note: More tests are in ASTLiteralTest.
    parserTest("textBlocks", javaVersions = JavaVersion.J15..JavaVersion.Latest) {

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

                    it::getImage shouldBe tblock
                }
            }
        }
    }

})
