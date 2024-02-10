/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.schema;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.function.Consumer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;

import net.sourceforge.pmd.lang.rule.Rule;

import com.github.oowekyala.ooxml.messages.NiceXmlMessageSpec;
import com.github.oowekyala.ooxml.messages.OoxmlFacade;
import com.github.oowekyala.ooxml.messages.PositionedXmlDoc;
import com.github.oowekyala.ooxml.messages.PrintStreamMessageHandler;
import com.github.oowekyala.ooxml.messages.XmlException;
import com.github.oowekyala.ooxml.messages.XmlMessageReporter;
import com.github.oowekyala.ooxml.messages.XmlMessageReporterBase;
import com.github.oowekyala.ooxml.messages.XmlPosition;
import com.github.oowekyala.ooxml.messages.XmlPositioner;
import com.github.oowekyala.ooxml.messages.XmlSeverity;


/**
 * Entry point to parse a test file.
 *
 * @author Clément Fournier
 */
public class TestSchemaParser {

    private final TestSchemaVersion version;

    TestSchemaParser(TestSchemaVersion version) {
        this.version = version;
    }

    public TestSchemaParser() {
        this(TestSchemaVersion.V1);
    }

    /**
     * Entry point to parse a test file.
     *
     * @param rule        Rule which owns the tests
     * @param inputSource Where to access the test file to parse
     *
     * @return A test collection, possibly incomplete
     *
     * @throws IOException  If parsing throws this
     * @throws XmlException If parsing throws this
     */
    public RuleTestCollection parse(Rule rule, InputSource inputSource) throws IOException, XmlException {
        // note: need to explicitly specify the writer here, so that in unit tests
        // System.err can be swapped out and in
        OoxmlFacade ooxml = new OoxmlFacade().withPrinter(new PrintStreamMessageHandler(System.err));
        PositionedXmlDoc doc = ooxml.parse(newDocumentBuilder(), inputSource);

        try (PmdXmlReporterImpl err = new PmdXmlReporterImpl(ooxml, doc.getPositioner())) {
            RuleTestCollection collection = version.getParserImpl().parseDocument(rule, doc, err);
            if (err.hasError()) {
                // todo maybe add a way not to throw here
                throw new IllegalStateException("Errors were encountered while parsing XML tests");
            }
            return collection;
        }
    }

    interface PmdXmlReporter extends XmlMessageReporter<Reporter> {

        boolean hasError();

        PmdXmlReporter newScope();
    }

    private static class PmdXmlReporterImpl
        extends XmlMessageReporterBase<Reporter>
        implements PmdXmlReporter {

        private boolean hasError;

        protected PmdXmlReporterImpl(OoxmlFacade ooxml,
                                     XmlPositioner positioner) {
            super(ooxml, positioner);
        }

        @Override
        protected Reporter create2ndStage(XmlPosition position, XmlPositioner positioner) {
            return new Reporter(position, positioner, ooxml, this::handleEx);
        }

        @Override
        protected void handleEx(XmlException e) {
            super.handleEx(e);
            hasError |= e.getSeverity() == XmlSeverity.ERROR;
        }

        @Override
        public PmdXmlReporter newScope() {
            return new PmdXmlReporterImpl(ooxml, positioner) {
                @Override
                protected void handleEx(XmlException e) {
                    super.handleEx(e);
                    PmdXmlReporterImpl.this.hasError |= this.hasError();
                }
            };
        }

        @Override
        public boolean hasError() {
            return hasError;
        }

    }

    private DocumentBuilder newDocumentBuilder() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // don't use the schema as it adds deprecated attributes implicitly...
            // dbf.setSchema(version.getSchema());
            dbf.setNamespaceAware(true);
            return dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


    static final class Reporter {

        private final XmlPosition position;
        private final XmlPositioner positioner;
        private final OoxmlFacade ooxml;

        private final Consumer<XmlException> handler;

        private Reporter(XmlPosition position, XmlPositioner positioner, OoxmlFacade ooxml, Consumer<XmlException> handler) {
            this.position = position;
            this.positioner = positioner;
            this.ooxml = ooxml;
            this.handler = handler;
        }

        public void warn(String messageFormat, Object... args) {
            reportImpl(XmlSeverity.WARNING, MessageFormat.format(messageFormat, args));

        }

        public void error(String messageFormat, Object... args) {
            reportImpl(XmlSeverity.ERROR, MessageFormat.format(messageFormat, args));
        }

        private void reportImpl(XmlSeverity severity, String formattedMessage) {
            NiceXmlMessageSpec spec =
                new NiceXmlMessageSpec(position, formattedMessage)
                    .withSeverity(severity);
            String fullMessage = ooxml.getFormatter().formatSpec(ooxml, spec, positioner);
            XmlException ex = new XmlException(spec, fullMessage);
            handler.accept(ex);
        }
    }

}
