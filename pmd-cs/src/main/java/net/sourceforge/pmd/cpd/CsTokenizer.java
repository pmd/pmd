/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * This class does a best-guess try-anything tokenization.
 *
 * @author jheintz
 */
public class CsTokenizer implements Tokenizer {

    private boolean ignoreUsings = false;

    public void setProperties(Properties properties) {
        if (properties.containsKey(IGNORE_USINGS)) {
            ignoreUsings = Boolean.parseBoolean(properties.getProperty(IGNORE_USINGS, "false"));
        }
    }

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        try (Tokenizer tokenizer = new Tokenizer(sourceCode.getCodeBuffer().toString())) {
            Token token = tokenizer.getNextToken();

            while (!token.equals(Token.EOF)) {
                Token lookAhead = tokenizer.getNextToken();

                // Ignore using directives
                // Only using directives should be ignored, because these are used
                // to import namespaces
                //
                // Using directive: 'using System.Math;'
                // Using statement: 'using (Font font1 = new Font(..)) { .. }'
                if (ignoreUsings && "using".equals(token.image) && !"(".equals(lookAhead.image)) {
                    // We replace the 'using' token by a random token, because it
                    // should not be part of
                    // any duplication block. When we omit it from the token stream,
                    // there is a change that
                    // we get a duplication block that starts before the 'using'
                    // directives and ends afterwards.
                    String randomTokenText = RandomStringUtils.randomAlphanumeric(20);

                    token = new Token(randomTokenText, token.lineNumber);
                    // Skip all other tokens of the using directive to prevent a
                    // partial matching
                    while (!";".equals(lookAhead.image) && !lookAhead.equals(Token.EOF)) {
                        lookAhead = tokenizer.getNextToken();
                    }
                }
                if (!";".equals(token.image)) {
                    tokenEntries.add(new TokenEntry(token.image, sourceCode.getFileName(), token.lineNumber));
                }
                token = lookAhead;
            }
            tokenEntries.add(TokenEntry.getEOF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIgnoreUsings(boolean ignoreUsings) {
        this.ignoreUsings = ignoreUsings;
    }

    private static class Tokenizer implements Closeable {
        private boolean endOfFile;
        private int line;
        private final PushbackReader reader;

        Tokenizer(String sourceCode) {
            endOfFile = false;
            line = 1;
            reader = new PushbackReader(new BufferedReader(new CharArrayReader(sourceCode.toCharArray())));
        }

        public Token getNextToken() {
            if (endOfFile) {
                return Token.EOF;
            }

            try {
                int ic = reader.read();
                char c;
                StringBuilder b;
                while (ic != -1) {
                    c = (char) ic;
                    switch (c) {
                    // new line
                    case '\n':
                        line++;
                        ic = reader.read();
                        break;

                    // white space
                    case ' ':
                    case '\t':
                    case '\r':
                        ic = reader.read();
                        break;

                    case ';':
                        return new Token(";", line);

                    // < << <= <<= > >> >= >>=
                    case '<':
                    case '>':
                        ic = reader.read();
                        if (ic == '=') {
                            return new Token(c + "=", line);
                        } else if (ic == c) {
                            ic = reader.read();
                            if (ic == '=') {
                                return new Token(c + c + "=", line);
                            } else {
                                reader.unread(ic);
                                return new Token(String.valueOf(c) + c, line);
                            }
                        } else {
                            reader.unread(ic);
                            return new Token(String.valueOf(c), line);
                        }

                        // = == & &= && | |= || + += ++ - -= --
                    case '=':
                    case '&':
                    case '|':
                    case '+':
                    case '-':
                        ic = reader.read();
                        if (ic == '=' || ic == c) {
                            return new Token(c + String.valueOf((char) ic), line);
                        } else {
                            reader.unread(ic);
                            return new Token(String.valueOf(c), line);
                        }

                        // ! != * *= % %= ^ ^= ~ ~=
                    case '!':
                    case '*':
                    case '%':
                    case '^':
                    case '~':
                        ic = reader.read();
                        if (ic == '=') {
                            return new Token(c + "=", line);
                        } else {
                            reader.unread(ic);
                            return new Token(String.valueOf(c), line);
                        }

                        // strings & chars
                    case '"':
                    case '\'':
                        int beginLine = line;
                        b = new StringBuilder();
                        b.append(c);
                        while ((ic = reader.read()) != c) {
                            if (ic == -1) {
                                break;
                            }
                            b.append((char) ic);
                            if (ic == '\\') {
                                int next = reader.read();
                                if (next != -1) {
                                    b.append((char) next);

                                    if (next == '\n') {
                                        line++;
                                    }
                                }
                            } else if (ic == '\n') {
                                line++;
                            }
                        }
                        if (ic != -1) {
                            b.append((char) ic);
                        }
                        return new Token(b.toString(), beginLine);

                    // / /= /*...*/ //...
                    case '/':
                        ic = reader.read();
                        c = (char) ic;
                        switch (c) {
                        case '*':
                            // int beginLine = line;
                            int state = 1;
                            b = new StringBuilder();
                            b.append("/*");

                            while ((ic = reader.read()) != -1) {
                                c = (char) ic;
                                b.append(c);

                                if (c == '\n') {
                                    line++;
                                }

                                if (state == 1) {
                                    if (c == '*') {
                                        state = 2;
                                    }
                                } else {
                                    if (c == '/') {
                                        ic = reader.read();
                                        break;
                                    } else if (c != '*') {
                                        state = 1;
                                    }
                                }
                            }
                            // ignore the /* comment
                            // tokenEntries.add(new TokenEntry(b.toString(),
                            // sourceCode.getFileName(), beginLine));
                            break;

                        case '/':
                            b = new StringBuilder();
                            b.append("//");
                            while ((ic = reader.read()) != '\n') {
                                if (ic == -1) {
                                    break;
                                }
                                b.append((char) ic);
                            }
                            // ignore the // comment
                            // tokenEntries.add(new TokenEntry(b.toString(),
                            // sourceCode.getFileName(), line));
                            break;

                        case '=':
                            return new Token("/=", line);

                        default:
                            reader.unread(ic);
                            return new Token("/", line);
                        }
                        break;

                    default:
                        // [a-zA-Z_][a-zA-Z_0-9]*
                        if (Character.isJavaIdentifierStart(c)) {
                            b = new StringBuilder();
                            do {
                                b.append(c);
                                ic = reader.read();
                                c = (char) ic;
                            } while (Character.isJavaIdentifierPart(c));
                            reader.unread(ic);
                            return new Token(b.toString(), line);
                        } else if (Character.isDigit(c) || c == '.') {
                            // numbers
                            b = new StringBuilder();
                            do {
                                b.append(c);
                                if (c == 'e' || c == 'E') {
                                    ic = reader.read();
                                    c = (char) ic;
                                    if ("1234567890-".indexOf(c) == -1) {
                                        break;
                                    }
                                    b.append(c);
                                }
                                ic = reader.read();
                                c = (char) ic;
                            } while ("1234567890.iIlLfFdDsSuUeExX".indexOf(c) != -1);
                            reader.unread(ic);
                            return new Token(b.toString(), line);
                        } else {
                            // anything else
                            return new Token(String.valueOf(c), line);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            endOfFile = true;
            return Token.EOF;
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }

    private static class Token {
        public static final Token EOF = new Token("EOF", -1);

        public final String image;
        public final int lineNumber;

        Token(String image, int lineNumber) {
            this.image = image;
            this.lineNumber = lineNumber;
        }
    }
}
