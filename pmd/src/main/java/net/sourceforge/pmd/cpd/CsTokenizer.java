/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/**
 * This class does a best-guess try-anything tokenization.
 *
 * @author jheintz
 */
public class CsTokenizer implements Tokenizer {
	
    public static final String IGNORE_ANNOTATIONS = "ignore_annotations";
    private boolean ignoreAnnotations;
    public static final String CPD_START = "/*CPD-IGNORE-START*/";
    public static final String CPD_END = "/*CPD-IGNORE-END*/";
    public static final String AUTO_GEN = "// <auto-generated";
    
    
    public void setProperties(Properties properties) {
         ignoreAnnotations = Boolean.parseBoolean(properties.getProperty(IGNORE_ANNOTATIONS, "false"));
    }
    
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
    	BufferedReader reader = new BufferedReader(new CharArrayReader(sourceCode.getCodeBuffer().toString().toCharArray()));
        boolean inCPDIgnore = false;
        boolean isAutogenerated = false;
        
    	try {
    		int ic = reader.read(), lic = -1, line=1;
    		char c;
    		StringBuilder b;
			while((ic!=-1) && (!isAutogenerated))
			{                              
                                c = (char)ic;
				switch(c)
				{
					// new line
				case '\n':
					line++;
                                        lic = ic;
					ic = reader.read();
					break;
				
					// white space
				case ' ':
				case '\t':
				case '\r':
                                        lic = ic;
					ic = reader.read();
					break;

					// ignore semicolons
				case ';':
                                        lic = ic;
					ic = reader.read();
					break;

					// < << <= <<= > >> >= >>=
				case '<':
				case '>':
                                        lic = ic;
					ic = reader.read();
					if(ic == '=')
					{
						if (!inCPDIgnore)
                                                    tokenEntries.add(new TokenEntry(String.valueOf(c)+"=", sourceCode.getFileName(), line));
                                                lic = ic;
						ic = reader.read();
					}
					else if(ic == c)
					{
						
                                                lic = ic;
                                                ic = reader.read();
						if(ic == '=')
						{
							if (!inCPDIgnore)
                                                            tokenEntries.add(new TokenEntry(String.valueOf(c)+String.valueOf(c)+"=", sourceCode.getFileName(), line));
							lic = ic;
                                                        ic = reader.read();
						}
						else
						{
							if (!inCPDIgnore)
                                                            tokenEntries.add(new TokenEntry(String.valueOf(c)+String.valueOf(c), sourceCode.getFileName(), line));
						}
					}
					else
					{
						if (!inCPDIgnore)
                                                    tokenEntries.add(new TokenEntry(String.valueOf(c), sourceCode.getFileName(), line));
					}
					break;
	
					// = == & &= && | |= || + += ++ - -= --
				case '=':
				case '&':
				case '|':
				case '+':
				case '-':
					lic = ic;
                                        ic = reader.read();
					if(ic == '=' || ic == c)
					{
						if (!inCPDIgnore)
                                                    tokenEntries.add(new TokenEntry(String.valueOf(c)+String.valueOf((char)ic), sourceCode.getFileName(), line));
						lic = ic;
                                                ic = reader.read();
					}
					else
					{
						if (!inCPDIgnore)
                                                    tokenEntries.add(new TokenEntry(String.valueOf(c), sourceCode.getFileName(), line));
					}
					break;
				
					// ! != * *= % %= ^ ^= ~ ~=
				case '!':
				case '*':
				case '%':
				case '^':
				case '~':
					lic = ic;
                                        ic = reader.read();
					if(ic == '=')
					{
						if (!inCPDIgnore)
                                                    tokenEntries.add(new TokenEntry(String.valueOf(c)+"=", sourceCode.getFileName(), line));
						lic = ic;
                                                ic = reader.read();
					}
					else
					{
						if (!inCPDIgnore)
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
					if (!inCPDIgnore)
                                            tokenEntries.add(new TokenEntry(b.toString(), sourceCode.getFileName(), line));
                                        lic = ic;
					ic = reader.read();
					break;
					
					// / /= /*...*/ //...
				case '/':
                                        lic = ic;
					switch(c = (char)(ic = reader.read()))
					{
					case '*':
                                                lic = -1;
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
						};
                                                if (b.toString().equalsIgnoreCase(CPD_START)) {
                                                    inCPDIgnore = true;
                                                } else if (b.toString().equalsIgnoreCase(CPD_END)) {
                                                    inCPDIgnore = false;
                                                };
						// ignore the /* comment
						//tokenEntries.add(new TokenEntry(b.toString(), sourceCode.getFileName(), line));
						break;
                                       
					case '/':
						b = new StringBuilder();
						b.append("//");
                                                lic = -1;
						while((ic = reader.read()) != '\n')
						{
							if(ic==-1)
								break;
							b.append((char)ic);
						};
                                                if (b.toString().startsWith(AUTO_GEN)) {
                                                    isAutogenerated = true;
                                                };
						// ignore the // comment
						//tokenEntries.add(new TokenEntry(b.toString(), sourceCode.getFileName(), line));
						break;
						
					case '=':
						if (!inCPDIgnore)
                                                    tokenEntries.add(new TokenEntry("/=", sourceCode.getFileName(), line));
                                                lic = ic;
						ic = reader.read();
						break;
						
					default:
						if (!inCPDIgnore)
                                                    tokenEntries.add(new TokenEntry("/", sourceCode.getFileName(), line));
						break;
					}
					break;
					
					
				 case '[':
                                 	 if (((lic == -1) && ignoreAnnotations) || (!Character.isJavaIdentifierStart((char)lic) && ignoreAnnotations)){
                                             while((ic = reader.read()) != -1){                                                      
                                                if((char)ic == ']') {
                                                    lic = ic;
                                                    ic = reader.read();
                                                    break;
                                                }
                                             }                                                     
                                         } else {
                                             if (!inCPDIgnore)
                                                    tokenEntries.add(new TokenEntry(String.valueOf(c), sourceCode.getFileName(), line));
                                             lic = ic;
                                             ic = reader.read();
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
                                                        lic = ic;
							c = (char)(ic = reader.read());
						} while(Character.isJavaIdentifierPart(c));
						if (!inCPDIgnore) {
                                                    tokenEntries.add(new TokenEntry(b.toString(), sourceCode.getFileName(), line));
                                                }
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
                                                                lic = ic;
								c = (char)(ic = reader.read());
								if("1234567890-".indexOf(c)==-1)
									break;
								b.append(c);
							}
                                                        lic = ic;
							c = (char)(ic = reader.read());
						} while("1234567890.iIlLfFdDsSuUeExX".indexOf(c)!=-1);
						
						if (!inCPDIgnore)
                                                    tokenEntries.add(new TokenEntry(b.toString(), sourceCode.getFileName(), line));	
					}
					// anything else
					else
					{
						if (!inCPDIgnore)
                                                    tokenEntries.add(new TokenEntry(String.valueOf(c), sourceCode.getFileName(), line));
                                                lic = ic;
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
