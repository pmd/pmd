/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * This class does a best-guess try-anything tokenization.
 *
 * @author jheintz
 */
public class CsTokenizer implements Tokenizer {
	
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
    	BufferedReader reader = new BufferedReader(new CharArrayReader(sourceCode.getCodeBuffer().toString().toCharArray()));
    	try {
    		int ic = reader.read(), line=1;
    		char c;
    		StringBuilder b;
			while(ic!=-1)
			{
				c = (char)ic;
				switch(c)
				{
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

					// ignore semicolons
				case ';':
					ic = reader.read();
					break;

					// < << <= <<= > >> >= >>=
				case '<':
				case '>':
					ic = reader.read();
					if(ic == '=')
					{
						tokenEntries.add(new TokenEntry(String.valueOf(c)+"=", sourceCode.getFileName(), line));
						ic = reader.read();
					}
					else if(ic == c)
					{
						ic = reader.read();
						if(ic == '=')
						{
							tokenEntries.add(new TokenEntry(String.valueOf(c)+String.valueOf(c)+"=", sourceCode.getFileName(), line));
							ic = reader.read();
						}
						else
						{
							tokenEntries.add(new TokenEntry(String.valueOf(c)+String.valueOf(c), sourceCode.getFileName(), line));
						}
					}
					else
					{
						tokenEntries.add(new TokenEntry(String.valueOf(c), sourceCode.getFileName(), line));
					}
					break;
	
					// = == & &= && | |= || + += ++ - -= --
				case '=':
				case '&':
				case '|':
				case '+':
				case '-':
					ic = reader.read();
					if(ic == '=' || ic == c)
					{
						tokenEntries.add(new TokenEntry(String.valueOf(c)+String.valueOf((char)ic), sourceCode.getFileName(), line));
						ic = reader.read();
					}
					else
					{
						tokenEntries.add(new TokenEntry(String.valueOf(c), sourceCode.getFileName(), line));
					}
					break;
				
					// ! != * *= % %= ^ ^= ~ ~=
				case '!':
				case '*':
				case '%':
				case '^':
				case '~':
					ic = reader.read();
					if(ic == '=')
					{
						tokenEntries.add(new TokenEntry(String.valueOf(c)+"=", sourceCode.getFileName(), line));
						ic = reader.read();
					}
					else
					{
						tokenEntries.add(new TokenEntry(String.valueOf(c), sourceCode.getFileName(), line));
					}
					break;
					
					// strings & chars
				case '"':
				case '\'':
					b = new StringBuilder();
					b.append(c);
					while((ic = reader.read()) != c)
					{
						if(ic == -1)
							break;
						b.append((char)ic);
						if(ic == '\\') {
							int next = reader.read();
							if (next != -1) b.append((char)next);
						}
					}
					if (ic != -1) b.append((char)ic);
					tokenEntries.add(new TokenEntry(b.toString(), sourceCode.getFileName(), line));
					ic = reader.read();
					break;
					
					// / /= /*...*/ //...
				case '/':
					switch(c = (char)(ic = reader.read()))
					{
					case '*':
						int state = 1;
						b = new StringBuilder();
						b.append("/*");
						
						while((ic = reader.read()) != -1)
						{
							c = (char)ic;
							b.append(c);
							
							if(state==1)
							{
								if(c == '*')
									state = 2;
							}
							else
							{
								if(c == '/') {
									ic = reader.read();
									break;
								} else if(c != '*') {
									state = 1;
								}
							}
						}
						// ignore the /* comment
						//tokenEntries.add(new TokenEntry(b.toString(), sourceCode.getFileName(), line));
						break;
						
					case '/':
						b = new StringBuilder();
						b.append("//");
						while((ic = reader.read()) != '\n')
						{
							if(ic==-1)
								break;
							b.append((char)ic);
						}
						// ignore the // comment
						//tokenEntries.add(new TokenEntry(b.toString(), sourceCode.getFileName(), line));
						break;
						
					case '=':
						tokenEntries.add(new TokenEntry("/=", sourceCode.getFileName(), line));
						ic = reader.read();
						break;
						
					default:
						tokenEntries.add(new TokenEntry("/", sourceCode.getFileName(), line));
						break;
					}
					break;
					
					
					
				default:
					// [a-zA-Z_][a-zA-Z_0-9]*
					if(Character.isJavaIdentifierStart(c))
					{
						b = new StringBuilder();
						do
						{
							b.append(c);
							c = (char)(ic = reader.read());
						} while(Character.isJavaIdentifierPart(c));
						tokenEntries.add(new TokenEntry(b.toString(), sourceCode.getFileName(), line));
					}
					// numbers
					else if(Character.isDigit(c) || c == '.')
					{
						b = new StringBuilder();
						do
						{
							b.append(c);
							if(c == 'e' || c == 'E')
							{
								c = (char)(ic = reader.read());
								if("1234567890-".indexOf(c)==-1)
									break;
								b.append(c);
							}
							c = (char)(ic = reader.read());
						} while("1234567890.iIlLfFdDsSuUeExX".indexOf(c)!=-1);
						
						tokenEntries.add(new TokenEntry(b.toString(), sourceCode.getFileName(), line));	
					}
					// anything else
					else
					{
						tokenEntries.add(new TokenEntry(String.valueOf(c), sourceCode.getFileName(), line));
						ic = reader.read();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    IOUtils.closeQuietly(reader);
		    tokenEntries.add(TokenEntry.getEOF());
		}
    }
}
