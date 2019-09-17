/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import java.io.StringReader;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;

public class ScalaParserTest {
    private static final String SCALA_TEST = "/parserFiles/helloworld.scala";


    @Test
    public void testCountNodes() throws Exception {
        LanguageVersionHandler scalaVersionHandler = LanguageRegistry.getLanguage(ScalaLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        Parser parser = scalaVersionHandler.getParser(scalaVersionHandler.getDefaultParserOptions());
        ScalaNode<?> root = (ScalaNode<?>) parser.parse(null,
                new StringReader(IOUtils.toString(getClass().getResourceAsStream(SCALA_TEST), "UTF-8")));

        final AtomicInteger nodeCount = new AtomicInteger();
        ScalaParserVisitorAdapter<Void, Void> visitor = new ScalaParserVisitorAdapter<Void, Void>() {
            @Override
            public Void visit(ScalaNode<?> node, Void data) {
                nodeCount.incrementAndGet();
                return super.visit(node, data);
            }
        };
        visitor.visit(root, null);
        Assert.assertEquals(12, nodeCount.get());
    }
}
