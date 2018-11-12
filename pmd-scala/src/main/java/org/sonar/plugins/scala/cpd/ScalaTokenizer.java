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

package org.sonar.plugins.scala.cpd;

import java.util.List;

import org.sonar.plugins.scala.compiler.Lexer;
import org.sonar.plugins.scala.compiler.Token;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.lang.ast.TokenMgrError;

/**
 * Scala tokenizer for PMD CPD.
 *
 * @since 0.1
 */
public final class ScalaTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode source, Tokens cpdTokens) {
        String filename = source.getFileName();

        try {
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.getTokensOfFile(filename);
            for (Token token : tokens) {
                String tokenVal = token.tokenVal() != null ? token.tokenVal() : Integer.toString(token.tokenType());

                TokenEntry cpdToken = new TokenEntry(tokenVal, filename, token.line());
                cpdTokens.add(cpdToken);
            }
            cpdTokens.add(TokenEntry.getEOF());
        } catch (RuntimeException e) {
            e.printStackTrace();
            // Wrap exceptions of the Scala tokenizer in a TokenMgrError, so
            // they are correctly handled
            // when CPD is executed with the '--skipLexicalErrors' command line
            // option
            throw new TokenMgrError(
                    "Lexical error in file " + filename + ". The scala tokenizer exited with error: " + e.getMessage(),
                    TokenMgrError.LEXICAL_ERROR);
        }
    }

}
