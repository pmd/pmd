package net.sourceforge.pmd.lang.java.symbols

import net.sourceforge.pmd.lang.java.ParserTstUtil
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit

/** Testing utilities */

fun Class<*>.parse(): ASTCompilationUnit = ParserTstUtil.parseJavaDefaultVersion(this)
