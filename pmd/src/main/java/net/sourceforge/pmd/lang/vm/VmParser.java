/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.vm;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.vm.util.VelocityCharStream;

/**
 * Adapter for the VmParser.
 */
public class VmParser extends AbstractParser {

    public VmParser(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(final Reader source) {
        return new VmTokenManager(source);
    }

    public boolean canParse() {
        return true;
    }

    public Node parse(final String fileName, final Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        return new net.sourceforge.pmd.lang.vm.ast.VmParser(new VelocityCharStream(source, 1, 1)).process();
    }

    public Map<Integer, String> getSuppressMap() {
        return new HashMap<Integer, String>(); // FIXME
    }
}
