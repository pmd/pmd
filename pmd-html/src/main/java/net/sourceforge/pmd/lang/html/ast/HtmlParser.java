/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.util.IOUtil;

public final class HtmlParser implements net.sourceforge.pmd.lang.Parser {

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        try {
            String data = IOUtil.readToString(source);
            Document doc = Parser.xmlParser().parseInput(data, "");
            HtmlTreeBuilder builder = new HtmlTreeBuilder();
            return builder.build(doc, data);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return new HashMap<>(); // FIXME
    }

    @Override
    public ParserOptions getParserOptions() {
        return new ParserOptions();
    }

    @Override
    public TokenManager getTokenManager(String fileName, Reader source) {
        return null;
    }
}
