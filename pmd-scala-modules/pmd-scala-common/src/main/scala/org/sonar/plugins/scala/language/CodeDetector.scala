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
package org.sonar.plugins.scala.language

import org.sonar.plugins.scala.compiler.{ Compiler, Parser }

/**
 * This object is a helper object for detecting valid Scala code
 * in a given piece of source code.
 *
 * @author Felix Müller
 * @since 0.1
 */
object CodeDetector {

  import Compiler._

  private lazy val parser = new Parser()

  def hasDetectedCode(code: String) = {

    def lookingForSyntaxTreesWithCode(tree: Tree) : Boolean = tree match {

      case PackageDef(identifier: RefTree, content) =>
        if (!identifier.name.equals(nme.EMPTY_PACKAGE_NAME)) {
          true
        } else {
          content.exists(lookingForSyntaxTreesWithCode)
        }

      case Apply(function, args) =>
        args.exists(lookingForSyntaxTreesWithCode)

      case ClassDef(_, _, _, _)
        | ModuleDef(_, _, _)
        | ValDef(_, _, _, _)
        | DefDef(_, _, _, _, _, _)
        | Function(_ , _)
        | Assign(_, _)
        | LabelDef(_, _, _) =>
          true

      case _ =>
        false
    }

    lookingForSyntaxTreesWithCode(parser.parse(code))
  }
}