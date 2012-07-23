package net.sourceforge.pmd.cpd;

import java.io.StringReader;
import java.util.Properties;

import net.sourceforge.pmd.lang.plsql.ast.Token;
import net.sourceforge.pmd.lang.plsql.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.SimpleCharStream;
import net.sourceforge.pmd.lang.plsql.ast.SimpleNode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserConstants;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserTokenManager;

public class PLSQLTokenizer implements Tokenizer{

    /**
    */
    public static final String IGNORE_COMMENTS = "ignore_comments";
    public static final String IGNORE_IDENTIFIERS = "ignore_identifiers";
    public static final String IGNORE_LITERALS = "ignore_literals";

    private boolean isInitialised = false;
    private boolean ignoreComments;
    private boolean ignoreIdentifiers;
    private boolean ignoreLiterals;

    public void setProperties(Properties properties) {
		/* The Tokenizer is derived from PLDoc, in which comments are very important
		 * When looking for duplication, we are probably not interested in comment variation,
		 * so we shall default ignoreComments to true
		*/
        ignoreComments = Boolean.parseBoolean(properties.getProperty(IGNORE_COMMENTS, "true"));
        ignoreIdentifiers = Boolean.parseBoolean(properties.getProperty(IGNORE_IDENTIFIERS, "false"));
        ignoreLiterals = Boolean.parseBoolean(properties.getProperty(IGNORE_LITERALS, "false"));
    }

    public void setIgnoreComments(boolean ignore) {
	this.ignoreComments = ignore;
    }

    public void setIgnoreLiterals(boolean ignore) {
	this.ignoreLiterals = ignore;
    }

    public void setIgnoreIdentifiers(boolean ignore) {
	this.ignoreIdentifiers = ignore;
    }

        /**
         * Read Reader from SourceCode and output an ordered tree of PLSQL tokens.
         * @param sourceCode PLSQL source in file, string or database (any suitable object that can return
         * a Reader).
         * @param tokenEntries  Derived based on PLSQL Abstract Syntax Tree (derived from PLDOc parser.) 
         */
	public void tokenize (SourceCode sourceCode, Tokens tokenEntries )
	{
            long encounteredTokens = 0, addedTokens = 0;
		//Initialisation has to go here because the System properties are not set up when the Tokenizer is constructed 
	    if (!isInitialised) {
			setProperties(System.getProperties());
			isInitialised =  true;

			System.err.println("PLSQLTokenizer: ignoreComments=="+ignoreComments);
			System.err.println("PLSQLTokenizer: ignoreIdentifiers=="+ignoreIdentifiers);
			System.err.println("PLSQLTokenizer: ignoreLiterals=="+ignoreLiterals);
		}

		String fileName = sourceCode.getFileName();
		StringBuilder sb = sourceCode.getCodeBuffer();

		PLSQLParserTokenManager tokenMgr = new PLSQLParserTokenManager( new SimpleCharStream( new StringReader(sb.toString()))); 
		Token currentToken = tokenMgr.getNextToken();
		while (currentToken.image.length()  > 0)
		{
			String image = currentToken.image;

                        encounteredTokens++;
			if (ignoreComments && 
			    ( currentToken.kind == PLSQLParserConstants.SINGLE_LINE_COMMENT
			    ||currentToken.kind == PLSQLParserConstants.MULTI_LINE_COMMENT
			    ||currentToken.kind == PLSQLParserConstants.FORMAL_COMMENT
			    ||currentToken.kind == PLSQLParserConstants.COMMENT
			    ||currentToken.kind == PLSQLParserConstants.IN_MULTI_LINE_COMMENT
			    ||currentToken.kind == PLSQLParserConstants.IN_FORMAL_COMMENT
				)
				) {
				image = String.valueOf(currentToken.kind);
			}

			if (ignoreIdentifiers && 
			    (currentToken.kind == PLSQLParserConstants.IDENTIFIER
				)
				) {
				image = String.valueOf(currentToken.kind);
			}

			if (ignoreLiterals
				&& (   
					   currentToken.kind == PLSQLParserConstants.UNSIGNED_NUMERIC_LITERAL 
					|| currentToken.kind == PLSQLParserConstants.FLOAT_LITERAL
					|| currentToken.kind == PLSQLParserConstants.INTEGER_LITERAL
					|| currentToken.kind == PLSQLParserConstants.CHARACTER_LITERAL
				    || currentToken.kind == PLSQLParserConstants.STRING_LITERAL
					|| currentToken.kind == PLSQLParserConstants.QUOTED_LITERAL
					)
				) {
				image = String.valueOf(currentToken.kind);
			}

			tokenEntries.add(new TokenEntry(image, fileName, currentToken.beginLine));
                        addedTokens++;
			currentToken = tokenMgr.getNextToken();
		}
		tokenEntries.add(TokenEntry.getEOF() );
	}



}


