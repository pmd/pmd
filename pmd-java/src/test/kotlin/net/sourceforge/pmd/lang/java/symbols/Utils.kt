package net.sourceforge.pmd.lang.java.symbols

import io.kotlintest.matchers.haveSize
import io.kotlintest.properties.Gen
import io.kotlintest.should
import net.sourceforge.pmd.lang.java.ParserTstUtil
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory
import java.io.File
import java.io.IOException
import java.util.*

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

fun <T, K> List<T>.groupByUnique(keySelector: (T) -> K): Map<K, T> =
        groupBy(keySelector).mapValues { (_, vs) ->
            vs should haveSize(1)
            vs.first()
        }

/** Generator of test instances. */
object TestClassesGen : Gen<Class<*>> {
    override fun constants(): Iterable<Class<*>> = emptyList()

    override fun random(): Sequence<Class<*>> =
            getClassesInPackage(javaClass.packageName + ".internal.testdata").asSequence()

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
