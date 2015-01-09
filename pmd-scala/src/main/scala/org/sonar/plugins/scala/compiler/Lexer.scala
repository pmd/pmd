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

import collection.mutable.ListBuffer

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

  import scala.collection.JavaConversions._
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
    val tokens = ListBuffer[Token]()

    scanner.init()
    while (scanner.token != scala.tools.nsc.ast.parser.Tokens.EOF) {
      val tokenVal =
        if (scala.tools.nsc.ast.parser.Tokens.isIdentifier(scanner.token)) scanner.name.toString() else null
      val linenr = scanner.parensAnalyzer.line(scanner.offset) + 1

      tokens += Token(scanner.token, linenr, tokenVal)
      scanner.nextToken()
    }
    tokens
  }

  def getComments(code: String): java.util.List[Comment] = {
    val unit = new CompilationUnit(new BatchSourceFile("", code.toCharArray))
    tokenizeComments(unit)
  }

  def getCommentsOfFile(path: String): java.util.List[Comment] = {
    val unit = new CompilationUnit(new BatchSourceFile(AbstractFile.getFile(path)))
    tokenizeComments(unit)
  }

  private def tokenizeComments(unit: CompilationUnit): java.util.List[Comment] = {
    val comments = ListBuffer[Comment]()
    val scanner = new syntaxAnalyzer.UnitScanner(unit) {

      private var lastDocCommentRange: Option[Range] = None

      private var foundToken = false

      override def nextToken() {
        super.nextToken()
        foundToken = token != 0
      }

      override def foundComment(value: String, start: Int, end: Int) = {
        super.foundComment(value, start, end)

        def isHeaderComment(value: String) = {
          !foundToken && comments.isEmpty && value.trim().startsWith("/*")
        }

        lastDocCommentRange match {

          case Some(r: Range) => {
            if (r.start != start || r.end != end) {
              comments += new Comment(value, CommentType.NORMAL)
            }
          }

          case None => {
            if (isHeaderComment(value)) {
              comments += new Comment(value, CommentType.HEADER)
            } else {
              comments += new Comment(value, CommentType.NORMAL)
            }
          }
        }
      }

      override def foundDocComment(value: String, start: Int, end: Int) = {
        super.foundDocComment(value, start, end)
        comments += new Comment(value, CommentType.DOC)
        lastDocCommentRange = Some(Range(start, end))
      }
    }

    scanner.init()
    while (scanner.token != scala.tools.nsc.ast.parser.Tokens.EOF) {
      scanner.nextToken()
    }

    comments
  }
}