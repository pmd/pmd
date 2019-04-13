package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_8
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J9

class ASTMethodDeclarationTest : ParserTestSpec({

    // notes about dsl:
    // * testGroup generates one test per "should" assertion that
    //   uses a node matcher, which is nice to know which one failed
    //   (without explicitly giving them each a specific name)
    // * the it::isPublic syntax allows including the property name in the error message in case of failure

    parserTest("Non-private interfaces members should be public", javaVersions = Earliest..Latest) {

        genClassHeader = "interface Bar"

        "int foo();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isPublic shouldBe true
            it::isPrivate shouldBe false
            it::isSyntacticallyPublic shouldBe false
        }

        "public int kk();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isPublic shouldBe true
            it::isPrivate shouldBe false
            it::isSyntacticallyPublic shouldBe true
        }

        "int FOO = 0;" should matchDeclaration<ASTFieldDeclaration>(ignoreChildren = true) {
            it::isPublic shouldBe true
            it::isPrivate shouldBe false
            it::isSyntacticallyPublic shouldBe false
        }

        "public int FOO = 0;" should matchDeclaration<ASTFieldDeclaration>(ignoreChildren = true) {
            it::isPublic shouldBe true
            it::isPrivate shouldBe false
            it::isSyntacticallyPublic shouldBe true
        }
    }

    parserTest("Private methods in interface should be private", J9..Latest) {

        "private int de() { return 1; }" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isPublic shouldBe false
            it::isPrivate shouldBe true
            it::isSyntacticallyPublic shouldBe false
        }

    }

    parserTest("Non-default methods in interfaces should be abstract", javaVersions = J1_8..Latest) {

        genClassHeader = "interface Bar"

        "int bar();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isDefault shouldBe false
            it::isAbstract shouldBe true
        }

        "abstract int bar();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isDefault shouldBe false
            it::isAbstract shouldBe true
        }

    }

    parserTest("Default methods in interfaces should not be abstract", javaVersions = J1_8..Latest) {

        genClassHeader = "interface Bar"

        "default int kar() { return 1; } " should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isDefault shouldBe true
            it::isAbstract shouldBe false
        }

        // default abstract is an invalid combination of modifiers so we won't encounter it in real analysis
    }

})
