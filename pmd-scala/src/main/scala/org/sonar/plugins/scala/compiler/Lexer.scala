/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 - 2014 All contributors
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.scala.compiler

import scala.collection.mutable.Buffer

import org.sonar.plugins.scala.language.{Comment, CommentType}
import scala.reflect.io.AbstractFile
import scala.reflect.internal.util.BatchSourceFile

/**
 * This class is a wrapper for accessing the lexer of the Scala compiler
 * from Java in a more convenient way.
 *
 * @author Felix Müller
 * @since 0.1
 */
class Lexer {

  import scala.collection.JavaConverters._
  import Compiler._

  def getTokens(code: String): java.util.List[Token] = {
    val unit = new CompilationUnit(new BatchSourceFile("", code.toCharArray))
    tokenize(unit)
  }

  def getTokensOfFile(path: String): java.util.List[Token] = {
    val unit = new CompilationUnit(new BatchSourceFile(AbstractFile.getFile(path)))
    tokenize(unit)
  }

  private def tokenize(unit: CompilationUnit): java.util.List[Token] = {
    val scanner = new syntaxAnalyzer.UnitScanner(unit)
    val tokens = Buffer[Token]()

    scanner.init()
    while (scanner.token != scala.tools.nsc.ast.parser.Tokens.EOF) {
      val tokenVal =
        if (scala.tools.nsc.ast.parser.Tokens.isIdentifier(scanner.token)) scanner.name.toString() else null
      val linenr = scanner.parensAnalyzer.line(scanner.offset) + 1

      tokens += Token(scanner.token, linenr, tokenVal)
      scanner.nextToken()
    }
    tokens.asJava
  }

}