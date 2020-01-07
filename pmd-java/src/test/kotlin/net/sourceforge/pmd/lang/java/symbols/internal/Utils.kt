/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.matchers.haveSize
import io.kotlintest.properties.Gen
import io.kotlintest.should
import net.sourceforge.pmd.lang.LanguageRegistry
import net.sourceforge.pmd.lang.LanguageVersion
import net.sourceforge.pmd.lang.ast.AstAnalysisContext
import net.sourceforge.pmd.lang.java.JavaLanguageModule
import net.sourceforge.pmd.lang.java.ParserTstUtil
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ClasspathSymbolResolver
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectionSymFactory
import net.sourceforge.pmd.lang.java.symbols.table.internal.HeaderScopesTest
import net.sourceforge.pmd.lang.java.symbols.table.internal.SymbolTableResolveHelper
import java.io.File
import java.io.IOException
import java.util.*
import java.util.stream.Stream

/** Testing utilities */

fun Class<*>.getAst(): ASTCompilationUnit = ParserTstUtil.parseJavaDefaultVersion(this)

fun Class<*>.getTypeDeclaration(): ASTAnyTypeDeclaration =
        ParserTstUtil.parseJavaDefaultVersion(this).let { acu ->
            if (this.enclosingClass == null) {
                acu.getFirstDescendantOfType(ASTAnyTypeDeclaration::class.java)
            } else {
                val qname = QualifiedNameFactory.ofClass(this)
                acu.findDescendantsOfType(ASTAnyTypeDeclaration::class.java, true)
                        .stream().filter { it.qualifiedName == qname }
                        .findFirst()
                        .get()
            }
        }

object DefaultAnalysisConfiguration : AstAnalysisContext {
    override fun getTypeResolutionClassLoader(): ClassLoader = ParserTstUtil::class.java.classLoader

    override fun getLanguageVersion(): LanguageVersion = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).defaultVersion
}

fun Class<*>.parse(): ASTCompilationUnit = ParserTstUtil.parseJavaDefaultVersion(this)

fun <T> Stream<T>.firstOrNull(): T? = findFirst().orElse(null)

val testSymFactory = ReflectionSymFactory()
val testSymResolver = ClasspathSymbolResolver(HeaderScopesTest::class.java.classLoader, testSymFactory)
fun classSym(klass: Class<*>?) = testSymFactory.getClassSymbol(klass)


fun <T, K> List<T>.groupByUnique(keySelector: (T) -> K): Map<K, T> =
        groupBy(keySelector).mapValues { (_, vs) ->
            vs should haveSize(1)
            vs.first()
        }


fun <T, R> Gen<T>.forAllEqual(test: (T) -> Pair<R, R>) {
    random().forEach {
        val (t, r) = test(it)
        if (t != r && r == t || t == r && r != t) {
            throw AssertionError("Asymmetry in equals relation $t <=> $r")
        } else if (t != r) {
            throw AssertionError("Expected property of $it to be $r, got $t")
        }
    }
}

/** Generator of test instances. */
object TestClassesGen : Gen<Class<*>> {
    override fun constants(): Iterable<Class<*>> = emptyList()

    override fun random(): Sequence<Class<*>> =
            sequenceOf(
                    java.lang.Object::class.java,
                    IntArray::class.java,
                    Cloneable::class.java,
                    Integer.TYPE,
                    Array<String>::class.java) +
                    getClassesInPackage(javaClass.`package`.name + ".internal.testdata").asSequence()

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    fun getClassesInPackage(packageName: String): List<Class<*>> {

        val result = ArrayList<Class<*>>()

        /** Recursive method side-effecting on the result list. */
        fun findClasses(directory: File, packageName: String) {
            if (!directory.exists()) {
                return
            }
            val files = directory.listFiles()
            for (file in files) {
                if (file.isDirectory) {
                    assert(!file.name.contains("."))
                    findClasses(file, packageName + "." + file.name)
                } else if (file.name.endsWith(".class")) {
                    result.add(Class.forName(packageName + '.' + file.name.substring(0, file.name.length - ".class".length)))
                }
            }
        }

        val classLoader = Thread.currentThread().contextClassLoader!!
        val path = packageName.replace('.', '/')
        val resources = classLoader.getResources(path)
        val dirs = generateSequence {
            if (resources.hasMoreElements())
                File(resources.nextElement().file)
            else null
        }

        for (directory in dirs) {
            findClasses(directory, packageName)
        }
        return result
    }
}

/** Generator of test instances. */
object PrimitiveSymGen : Gen<JClassSymbol> {
    override fun constants() = listOf(
            SymbolFactory.INT_SYM,
            SymbolFactory.DOUBLE_SYM,
            SymbolFactory.FLOAT_SYM,
            SymbolFactory.VOID_SYM,
            SymbolFactory.CHAR_SYM,
            SymbolFactory.BYTE_SYM,
            SymbolFactory.SHORT_SYM,
            SymbolFactory.LONG_SYM,
            SymbolFactory.BOOLEAN_SYM
    )

    override fun random() = emptySequence<JClassSymbol>()
}
