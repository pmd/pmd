/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.ast.ParseException;

import org.apache.commons.io.IOUtils;

import apex.jorje.semantic.ast.compilation.UserClass;

public class ApexParser {
	protected final ApexParserOptions parserOptions;

	private Map<Integer, String> suppressMap;
	private String suppressMarker = "NOPMD"; // that's the default value

	public ApexParser(ApexParserOptions parserOptions) {
		this.parserOptions = parserOptions;
		
		if (parserOptions.getSuppressMarker() != null) {
			suppressMarker = parserOptions.getSuppressMarker();
		}
	}

	protected UserClass parseApex(final String sourceCode) throws ParseException {
		UserClass astRoot = null; // TODO How to use Jorje to get AST root nodes?
		return astRoot;
	}

	public ApexNode<UserClass> parse(final Reader reader) {
		try {
			final String sourceCode = IOUtils.toString(reader);
			final UserClass astRoot = parseApex(sourceCode);
			final ApexTreeBuilder treeBuilder = new ApexTreeBuilder();
			suppressMap = new HashMap<>();

			ApexNode<UserClass> tree = treeBuilder.build(astRoot);
			return tree;
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}

	public Map<Integer, String> getSuppressMap() {
		return suppressMap;
	}
}
