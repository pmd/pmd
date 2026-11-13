/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.schema;

import java.net.URL;
import java.util.Objects;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import net.sourceforge.pmd.util.internal.xml.XmlUtil;

/**
 * Internal for now, there's only one version.
 *
 * @author Clément Fournier
 */
enum TestSchemaVersion {
    V1("rule-tests_1_1_1.xsd", new BaseTestParserImpl.ParserV1());

    private final Schema schema;
    private String schemaLoc;
    private BaseTestParserImpl parser;

    TestSchemaVersion(String schemaLoc, BaseTestParserImpl parser) {
        this.schemaLoc = schemaLoc;
        this.parser = parser;
        this.schema = parseSchema();
    }

    BaseTestParserImpl getParserImpl() {
        return parser;
    }

    public Schema getSchema() {
        return schema;
    }

    private Schema parseSchema() {
        try {
            SchemaFactory schemaFactory = XmlUtil.newSchemaFactory();
            return schemaFactory.newSchema(locateSchema());
        } catch (SAXException e) {
            throw new RuntimeException("Cannot parse schema " + this, e);
        }
    }

    private URL locateSchema() {
        URL resource = TestSchemaVersion.class.getResource(schemaLoc);
        return Objects.requireNonNull(resource, "Cannot find schema location " + schemaLoc);
    }

}
