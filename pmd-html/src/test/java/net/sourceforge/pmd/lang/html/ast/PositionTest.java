/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.test.BaseNodeAttributePrinter;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.util.CollectionUtil;

public class PositionTest extends BaseTreeDumpTest {
    public PositionTest() {
        super(new PositionRenderer(), ".html");
    }

    @Override
    public BaseParsingHelper<HtmlParsingHelper, HtmlDocument> getParser() {
        return HtmlParsingHelper.DEFAULT.withResourceContext(HtmlTreeDumpTest.class, "testdata");
    }

    @Test
    public void testPositions() {
        doTest("SimpleHtmlFile2");
    }

    private static class PositionRenderer extends BaseNodeAttributePrinter {
        private final Set<String> pos = CollectionUtil.asSet(new String[] {"BeginLine", "BeginColumn", "EndLine", "EndColumn"});

        @Override
        protected boolean ignoreAttribute(Node node, Attribute attribute) {
            return !pos.contains(attribute.getName());
        }
    }
}
