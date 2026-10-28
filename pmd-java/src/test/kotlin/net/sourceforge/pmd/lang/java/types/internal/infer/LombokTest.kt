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
import java.lang.reflect.Modifier

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

    parserTestContainer("Lombok @Getter annotation") {
        val code =
            """
            import lombok.AccessLevel;
            import lombok.Getter;
            
            public class Book {
                @Getter
                private String title;
                
                @Getter
                private boolean hardcover;
                
                @Getter(AccessLevel.PRIVATE)
                private String author;
                
                @Getter(AccessLevel.NONE)
                private int pages;
                
                public static void main(String[] args) {
                    Book b = new Book();
                    String t = b.getTitle(); // first method call
                    String a = b.getAuthor(); // second method call
                    System.out.println(t);
                }
            }
            """.trimIndent()

        doTest("Creates getter methods for title and author when supported") {
            val acu = parser.parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().typeMirror.symbol
                classSymbol.declaredMethods.size shouldBe 4
                classSymbol.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("main", "getTitle", "getAuthor", "isHardcover")

                val firstMethodCall = acu.firstMethodCall()
                firstMethodCall.overloadSelectionInfo.isFailed shouldBe false
                val methodSymbol = firstMethodCall.overloadSelectionInfo.methodType.symbol
                methodSymbol.simpleName shouldBe "getTitle"
                methodSymbol.arity shouldBe 0
                methodSymbol.enclosingClass shouldBe classSymbol
                Modifier.isPublic(methodSymbol.modifiers) shouldBe true
                val returnType = methodSymbol.getReturnType(null)
                TypeTestUtil.isA("java.lang.String", returnType) shouldBe true

                val secondMethodCall = acu.methodCalls().toList()[1]
                secondMethodCall.overloadSelectionInfo.isFailed shouldBe false
                val secondMethodSymbol = secondMethodCall.overloadSelectionInfo.methodType.symbol
                secondMethodSymbol.simpleName shouldBe "getAuthor"
                secondMethodSymbol.arity shouldBe 0
                secondMethodSymbol.enclosingClass shouldBe classSymbol
                Modifier.isPrivate(secondMethodSymbol.modifiers) shouldBe true
                TypeTestUtil.isA("java.lang.String", secondMethodSymbol.getReturnType(null))
            }
        }

        doTest("Do nothing when lombok support is disabled") {
            val acu = parser.disableLombok().parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().typeMirror.symbol
                classSymbol.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("main")

                val firstMethodCall = acu.firstMethodCall()
                firstMethodCall.overloadSelectionInfo.isFailed shouldBe true
                val methodType = firstMethodCall.overloadSelectionInfo.methodType
                methodType.symbol.isUnresolved shouldBe true
            }
        }
    }

    parserTestContainer("Lombok @Data annotation") {
        val code =
            """
            import lombok.AccessLevel;
            import lombok.Data;
            import lombok.Getter;
            
            @Data
            public class Book {
                private String title;
                
                @Getter(AccessLevel.PRIVATE)
                private String author;
                
                @Getter(AccessLevel.NONE)
                private int pages;
                
                private String isbn;
                
                public static void main(String[] args) {
                    Book b = new Book();
                    String t = b.getTitle(); // first method call
                    String a = b.getAuthor(); // second method call
                    System.out.println(t);
                }
                
                // manual getter
                public String getIsbn() {
                    return isbn;
                }
            }
            """.trimIndent()

        doTest("Creates getter methods for title and author when supported") {
            val acu = parser.parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().typeMirror.symbol
                classSymbol.declaredMethods.size shouldBe 4
                classSymbol.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("main", "getTitle", "getAuthor", "getIsbn")

                val firstMethodCall = acu.firstMethodCall()
                firstMethodCall.overloadSelectionInfo.isFailed shouldBe false
                val methodSymbol = firstMethodCall.overloadSelectionInfo.methodType.symbol
                methodSymbol.simpleName shouldBe "getTitle"
                methodSymbol.arity shouldBe 0
                methodSymbol.enclosingClass shouldBe classSymbol
                Modifier.isPublic(methodSymbol.modifiers) shouldBe true
                val returnType = methodSymbol.getReturnType(null)
                TypeTestUtil.isA("java.lang.String", returnType) shouldBe true

                val secondMethodCall = acu.methodCalls().toList()[1]
                secondMethodCall.overloadSelectionInfo.isFailed shouldBe false
                val secondMethodSymbol = secondMethodCall.overloadSelectionInfo.methodType.symbol
                secondMethodSymbol.simpleName shouldBe "getAuthor"
                secondMethodSymbol.arity shouldBe 0
                secondMethodSymbol.enclosingClass shouldBe classSymbol
                Modifier.isPrivate(secondMethodSymbol.modifiers) shouldBe true
                TypeTestUtil.isA("java.lang.String", secondMethodSymbol.getReturnType(null))
            }
        }

        doTest("Do nothing when lombok support is disabled") {
            val acu = parser.disableLombok().parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().typeMirror.symbol
                classSymbol.declaredMethods.size shouldBe 2
                classSymbol.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("main", "getIsbn")

                val firstMethodCall = acu.firstMethodCall()
                firstMethodCall.overloadSelectionInfo.isFailed shouldBe true
                val methodType = firstMethodCall.overloadSelectionInfo.methodType
                methodType.symbol.isUnresolved shouldBe true
            }
        }
    }

    parserTestContainer("Lombok @Value annotation") {
        val code =
            """
            import lombok.AccessLevel;
            import lombok.Getter;
            import lombok.Value;
            
            @Value
            public class Book {
                private String title;
                
                @Getter(AccessLevel.PRIVATE)
                private String author;
                
                @Getter(AccessLevel.NONE)
                private int pages;
                
                public static void main(String[] args) {
                    Book b = new Book();
                    String t = b.getTitle(); // first method call
                    String a = b.getAuthor(); // second method call
                    System.out.println(t);
                }
            }
            """.trimIndent()

        doTest("Creates getter methods for title and author when supported") {
            val acu = parser.parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().typeMirror.symbol
                classSymbol.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("main", "getTitle", "getAuthor")

                val firstMethodCall = acu.firstMethodCall()
                firstMethodCall.overloadSelectionInfo.isFailed shouldBe false
                val methodSymbol = firstMethodCall.overloadSelectionInfo.methodType.symbol
                methodSymbol.simpleName shouldBe "getTitle"
                methodSymbol.arity shouldBe 0
                methodSymbol.enclosingClass shouldBe classSymbol
                Modifier.isPublic(methodSymbol.modifiers) shouldBe true
                val returnType = methodSymbol.getReturnType(null)
                TypeTestUtil.isA("java.lang.String", returnType) shouldBe true

                val secondMethodCall = acu.methodCalls().toList()[1]
                secondMethodCall.overloadSelectionInfo.isFailed shouldBe false
                val secondMethodSymbol = secondMethodCall.overloadSelectionInfo.methodType.symbol
                secondMethodSymbol.simpleName shouldBe "getAuthor"
                secondMethodSymbol.arity shouldBe 0
                secondMethodSymbol.enclosingClass shouldBe classSymbol
                Modifier.isPrivate(secondMethodSymbol.modifiers) shouldBe true
                TypeTestUtil.isA("java.lang.String", secondMethodSymbol.getReturnType(null))
            }
        }

        doTest("Do nothing when lombok support is disabled") {
            val acu = parser.disableLombok().parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().typeMirror.symbol
                classSymbol.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("main")

                val firstMethodCall = acu.firstMethodCall()
                firstMethodCall.overloadSelectionInfo.isFailed shouldBe true
                val methodType = firstMethodCall.overloadSelectionInfo.methodType
                methodType.symbol.isUnresolved shouldBe true
            }
        }
    }

    parserTestContainer("Lombok constructor annotations") {
        val code =
            """
            import lombok.AccessLevel;
            import lombok.AllArgsConstructor;
            import lombok.NoArgsConstructor;
            import lombok.NonNull;
            import lombok.RequiredArgsConstructor;

            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            @RequiredArgsConstructor
            @AllArgsConstructor(staticName = "of", access = AccessLevel.PUBLIC)
            public class Book {
                private static int count;
                private final String title;
                private final int edition = 1;
                @NonNull private String author;
                private int pages;

                public static void main(String[] args) {
                    Book b = new Book("title", "author");
                }
            }
            """.trimIndent()

        doTest("Creates constructors when supported") {
            val acu = parser.parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().symbol
                // no implicit default constructor
                classSymbol.constructors.map { ctor -> ctor.formalParameters.map { it.simpleName } } shouldBe listOf(
                    emptyList(),
                    listOf("title", "author"),
                    listOf("title", "author", "pages"),
                )
                classSymbol.constructors.map { Modifier.toString(it.modifiers) } shouldBe listOf(
                    "private",
                    "public",
                    "private", // staticName makes the constructor private
                )
                classSymbol.constructors[2].getFormalParameterTypes(null) shouldBe listOf(
                    ts.STRING,
                    ts.STRING,
                    int,
                )

                val ctorCall = acu.firstCtorCall()
                ctorCall.overloadSelectionInfo.isFailed shouldBe false
                ctorCall.methodType.symbol shouldBe classSymbol.constructors[1]
            }
        }

        doTest("Keeps the default constructor when lombok support is disabled") {
            val acu = parser.disableLombok().parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().symbol
                classSymbol.constructors.map { it.arity } shouldBe listOf(0)
                Modifier.isPublic(classSymbol.constructors[0].modifiers) shouldBe true
                acu.firstCtorCall().overloadSelectionInfo.isFailed shouldBe true
            }
        }

        doTest("Handles explicit constructors, AccessLevel.NONE/PACKAGE and enums") {
            val acu = parser.parse(
                """
                import lombok.AccessLevel;
                import lombok.AllArgsConstructor;
                import lombok.NoArgsConstructor;

                public class Outer {
                    @NoArgsConstructor(access = AccessLevel.NONE)
                    public static class NoneAccess { }

                    @NoArgsConstructor(access = AccessLevel.PACKAGE)
                    static class Explicit { private Explicit(int x) { } }

                    @AllArgsConstructor
                    enum Color {
                        RED(1);
                        private final int value;
                    }
                }
                """.trimIndent()
            )
            val (_, noneAccess, explicit, color) = acu.typeDeclarations().toList { it.symbol }

            // AccessLevel.NONE generates nothing, so the default constructor remains
            noneAccess.constructors.map { Modifier.toString(it.modifiers) to it.arity } shouldBe listOf("public" to 0)
            // explicit constructors are kept
            explicit.constructors.map { Modifier.toString(it.modifiers) to it.arity } shouldBe listOf("private" to 1, "" to 0)
            // enum constructors are always private
            color.constructors.map { Modifier.toString(it.modifiers) to it.arity } shouldBe listOf("private" to 1)
        }
    }
})
