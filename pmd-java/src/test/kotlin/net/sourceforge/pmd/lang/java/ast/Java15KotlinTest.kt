/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.matchNode

class Java15KotlinTest: ParserTestSpec( {

    // Note: More tests are in ASTLiteralTest.
    parserTest("textBlocks", javaVersions = JavaVersion.J15..JavaVersion.Latest) {
        ("\"\"\"\n" +
        "                            <html>   \n" +
        "                                <body>\n" +
        "                                    <p>Hello, world</p>    \n" +
        "                                </body> \n" +
        "                            </html>   \n" +
        "                            \"\"\"") should matchExpr<ASTExpression> {
                child<ASTPrimaryExpression> {
                    child<ASTPrimaryPrefix> {
                        child<ASTLiteral> {
                            it.isTextBlock shouldBe true
                            it.escapedStringLiteral shouldBe
                                    "\"\"\"\n" +
                                    "            <html>   \n" +
                                    "                <body>\n" +
                                    "                    <p>Hello, world</p>    \n" +
                                    "                </body> \n" +
                                    "            </html>   \n" +
                                    "            \"\"\""
                            it.textBlockContent shouldBe
                                    "<html>\n" +
                                    "    <body>\n" +
                                    "        <p>Hello, world</p>\n" +
                                    "    </body>\n" +
                                    "</html>\n"
                        }
                    }
                }
        }
    }

})
