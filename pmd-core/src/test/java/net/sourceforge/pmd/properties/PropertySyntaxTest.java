/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import net.sourceforge.pmd.RulesetFactoryTestBase;
import net.sourceforge.pmd.util.internal.xml.PmdXmlReporter;

import com.github.oowekyala.ooxml.messages.OoxmlFacade;

/**
 * @author Clément Fournier
 */
public class PropertySyntaxTest extends RulesetFactoryTestBase {

    protected static @NonNull String property(String name, String... contents) {
        return "<property name='" + name + ">\n"
            + body(contents)
            + "\n</property>";
    }

    protected static Element parseXml(String xmlElt) {
        OoxmlFacade ooxml = new OoxmlFacade();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return ooxml.parse(
                builder,
                new InputSource(new StringReader(xmlElt))
            ).getDocument().getDocumentElement();

        } catch (IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testValue() {
        // given an xml elt and a pdescriptor
        // ensure fromXml runs properly
        XmlMapper<String> mapper = PropertyFactory.stringProperty("eude")
                                                  .desc("eu")
                                                  .defaultValue("")
                                                  .build()
                                                  .xmlMapper();
        assertParsesAs(
            mapper,
            valueTag("adu"),
            "adu"
        );
    }

    @Test
    void testListPropertyParsingFromValue() {
        // given an xml elt and a pdescriptor
        // ensure fromXml runs properly
        XmlMapper<List<String>> mapper = PropertyFactory.stringProperty("eude")
                                                        .desc("eu")
                                                        .toList()
                                                        .emptyDefaultValue()
                                                        .delim('-')
                                                        .build()
                                                        .xmlMapper();
        assertParsesAs(mapper,
                       valueTag("ad-u-"),
                       listOf("ad", "u"));

    }

    private @NonNull String valueTag(String x) {
        return tagOneLine("value", x);
    }

    @Test
    void testSeqParsing() {
        // given an xml elt and a pdescriptor
        // ensure fromXml runs properly
        XmlMapper<List<String>> mapper = PropertyFactory.stringProperty("eude")
                                                        .desc("eu")
                                                        .toList()
                                                        .emptyDefaultValue()
                                                        .delim('-')
                                                        .build()
                                                        .xmlMapper();
        assertParsesAs(mapper,
                       tag("seq",
                           valueTag("ad"),
                           valueTag("u")
                       ),
                       listOf("ad", "u"));

    }

    private <T> void assertParsesAs(XmlMapper<T> mapper, String xmlCode, T expected) {
        Element xml = parseXml(xmlCode);
        T parsed = mapper.fromXml(xml, mock(PmdXmlReporter.class));
        assertEquals(expected, parsed);
    }

}
