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

    parserTestContainer("Lombok @Builder annotation") {
        val code =
            """
            import lombok.Builder;

            @Builder
            public class Book {
                private String title;
                private int pages;
                private final String isbn = "123";
                private static int count;
                @Builder.Default
                private final String language = "en";

                public static void main(String[] args) {
                    Book b = Book.builder().title("Dune").pages(412).language("de").build();
                }
            }
            """.trimIndent()

        doTest("Creates the builder class and the builder method") {
            val acu = parser.parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().symbol
                classSymbol.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("main", "builder")

                val builderMethod = classSymbol.declaredMethods.first { it.simpleName == "builder" }
                Modifier.isStatic(builderMethod.modifiers) shouldBe true
                Modifier.isPublic(builderMethod.modifiers) shouldBe true

                val builderClass = classSymbol.getDeclaredClass("BookBuilder")!!
                builderClass.binaryName shouldBe "Book\$BookBuilder"
                builderClass.enclosingClass shouldBe classSymbol
                Modifier.isStatic(builderClass.modifiers) shouldBe true
                builderClass.declaredMethods.map { it.simpleName }.toSet() shouldBe
                    setOf("title", "pages", "language", "build")

                acu.methodCalls().toList().forEach {
                    it.overloadSelectionInfo.isFailed shouldBe false
                }
                acu.varId("b") shouldHaveType classSymbol.typeSystem.declaration(classSymbol)
                acu.methodCalls().firstOrThrow().methodType.symbol.simpleName shouldBe "build"
                acu.methodCalls().firstOrThrow() shouldHaveType classSymbol.typeSystem.declaration(classSymbol)
            }
        }

        doTest("Do nothing when lombok support is disabled") {
            val acu = parser.disableLombok().parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().symbol
                classSymbol.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("main")
                classSymbol.declaredClasses shouldBe emptyList()
            }
        }
    }

    parserTestContainer("Lombok @Builder annotation with custom names") {
        val code =
            """
            import lombok.AccessLevel;
            import lombok.Builder;

            @Builder(builderClassName = "Maker", builderMethodName = "make", buildMethodName = "create",
                     setterPrefix = "with", toBuilder = true, access = AccessLevel.PACKAGE)
            public class Book {
                private String title;

                public static void main(String[] args) {
                    Book b = Book.make().withTitle("Dune").create().toBuilder().create();
                }
            }
            """.trimIndent()

        doTest("Uses the names from the annotation") {
            val acu = parser.parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().symbol
                classSymbol.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("main", "make", "toBuilder")
                val toBuilder = classSymbol.declaredMethods.first { it.simpleName == "toBuilder" }
                Modifier.isStatic(toBuilder.modifiers) shouldBe false

                val builderClass = classSymbol.getDeclaredClass("Maker")!!
                Modifier.isPublic(builderClass.modifiers) shouldBe false
                builderClass.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("withTitle", "create")

                acu.methodCalls().toList().forEach {
                    it.overloadSelectionInfo.isFailed shouldBe false
                }
                acu.varId("b") shouldHaveType classSymbol.typeSystem.declaration(classSymbol)
            }
        }
    }

    parserTestContainer("Lombok @Builder annotation with a handwritten builder class") {
        val code =
            """
            import lombok.Builder;

            @Builder
            public class Book {
                private String title;

                public static class BookBuilder {
                    public BookBuilder upperTitle(String title) {
                        return title(title.toUpperCase());
                    }
                }

                public static void main(String[] args) {
                    Book b = Book.builder().upperTitle("Dune").build();
                }
            }
            """.trimIndent()

        doTest("Adds the missing methods to the handwritten builder class") {
            val acu = parser.parse(code)
            acu.withTypeDsl {
                val classSymbol = acu.typeDeclarations().firstOrThrow().symbol
                classSymbol.declaredClasses.size shouldBe 1
                val builderClass = classSymbol.getDeclaredClass("BookBuilder")!!
                builderClass.declaredMethods.map { it.simpleName }.toSet() shouldBe setOf("upperTitle", "title", "build")

                acu.methodCalls().toList().forEach {
                    it.overloadSelectionInfo.isFailed shouldBe false
                }
                acu.varId("b") shouldHaveType classSymbol.typeSystem.declaration(classSymbol)
            }
        }
    }
})
