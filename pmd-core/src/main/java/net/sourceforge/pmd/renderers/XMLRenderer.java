/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Renderer to XML format.
 */
public class XMLRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "xml";

    // TODO 7.0.0 use PropertyDescriptor<String> or something more specialized
    public static final PropertyDescriptor<String> ENCODING =
        PropertyFactory.stringProperty("encoding").desc("XML encoding format").defaultValue("UTF-8").build();

    private static final String PMD_REPORT_NS_URI = "http://pmd.sourceforge.net/report/2.0.0";
    private static final String PMD_REPORT_NS_LOCATION = "http://pmd.sourceforge.net/report_2_0_0.xsd";
    private static final String XSI_NS_PREFIX = "xsi";

    private XMLStreamWriter xmlWriter;
    private OutputStream stream;
    private byte[] lineSeparator;

    public XMLRenderer() {
        super(NAME, "XML format.");
        definePropertyDescriptor(ENCODING);
    }

    public XMLRenderer(String encoding) {
        this();
        setProperty(ENCODING, encoding);
    }

    @Override
    public String defaultFileExtension() {
        return "xml";
    }

    @Override
    public void start() throws IOException {
        String encoding = getProperty(ENCODING);
        String unmarkedEncoding = toUnmarkedEncoding(encoding);
        lineSeparator = System.lineSeparator().getBytes(unmarkedEncoding);

        try {
            xmlWriter.writeStartDocument(encoding, "1.0");
            writeNewLine();
            xmlWriter.setDefaultNamespace(PMD_REPORT_NS_URI);
            xmlWriter.writeStartElement(PMD_REPORT_NS_URI, "pmd");
            xmlWriter.writeDefaultNamespace(PMD_REPORT_NS_URI);
            xmlWriter.writeNamespace(XSI_NS_PREFIX, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            xmlWriter.writeAttribute(XSI_NS_PREFIX, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation",
                    PMD_REPORT_NS_URI + " " + PMD_REPORT_NS_LOCATION);
            xmlWriter.writeAttribute("version", PMDVersion.VERSION);
            xmlWriter.writeAttribute("timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date()));
            // FIXME: elapsed time not available until the end of the processing
            // xmlWriter.writeAttribute("time_elapsed", ...);
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    /**
     * Return a encoding, which doesn't write a BOM (byte order mark).
     * Only UTF-16 encoders might write a BOM, see {@link Charset}.
     *
     * <p>This is needed, so that we don't accidentally add BOMs whenever
     * we insert a newline.
     *
     * @return
     */
    private static String toUnmarkedEncoding(String encoding) {
        if (StandardCharsets.UTF_16.name().equalsIgnoreCase(encoding)) {
            return StandardCharsets.UTF_16BE.name();
        }
        // edge case: UTF-16LE with BOM
        if ("UTF-16LE_BOM".equalsIgnoreCase(encoding)) {
            return StandardCharsets.UTF_16LE.name();
        }
        return encoding;
    }

    /**
     * Outputs a platform dependent line separator.
     *
     * @throws XMLStreamException if XMLStreamWriter couldn't be flushed.
     * @throws IOException if an I/O error occurs.
     */
    private void writeNewLine() throws XMLStreamException, IOException {
        /*
         * Note: we are not using xmlWriter.writeCharacters(PMD.EOL), because some
         * XMLStreamWriter implementations might do extra encoding for \r and/or \n.
         * Notably IBM's Java 8 will escape "\r" with "&#xD;" which will render an
         * invalid XML document. IBM's Java 8 would also output a platform dependent
         * line separator when writing "\n" which results under Windows, that "\r"
         * actually is written twice (once escaped, once raw).
         *
         * Note2: Before writing the raw bytes to the underlying stream, we need
         * to flush XMLStreamWriter. Notably IBM's Java 8 might still need to output
         * data.
         *
         * Note3: Before writing the raw bytes, we issue a empty writeCharacters,
         * so that any open tags are closed and we are ready for writing raw bytes.
         */
        xmlWriter.writeCharacters("");
        xmlWriter.flush();
        stream.write(lineSeparator);
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        String filename = null;

        try {
            // rule violations
            while (violations.hasNext()) {
                RuleViolation rv = violations.next();
                String nextFilename = determineFileName(rv.getFilename());
                if (!nextFilename.equals(filename)) {
                    // New File
                    if (filename != null) {
                        // Not first file ?
                        xmlWriter.writeEndElement();
                    }
                    filename = nextFilename;
                    writeNewLine();
                    xmlWriter.writeStartElement("file");
                    xmlWriter.writeAttribute("name", filename);
                    writeNewLine();
                }

                xmlWriter.writeStartElement("violation");
                xmlWriter.writeAttribute("beginline", String.valueOf(rv.getBeginLine()));
                xmlWriter.writeAttribute("endline", String.valueOf(rv.getEndLine()));
                xmlWriter.writeAttribute("begincolumn", String.valueOf(rv.getBeginColumn()));
                xmlWriter.writeAttribute("endcolumn", String.valueOf(rv.getEndColumn()));
                xmlWriter.writeAttribute("rule", rv.getRule().getName());
                xmlWriter.writeAttribute("ruleset", rv.getRule().getRuleSetName());
                maybeAdd("package", rv.getAdditionalInfo().get(RuleViolation.PACKAGE_NAME));
                maybeAdd("class", rv.getAdditionalInfo().get(RuleViolation.CLASS_NAME));
                maybeAdd("method", rv.getAdditionalInfo().get(RuleViolation.METHOD_NAME));
                maybeAdd("variable", rv.getAdditionalInfo().get(RuleViolation.VARIABLE_NAME));
                // todo other additional info keys are not rendered
                maybeAdd("externalInfoUrl", rv.getRule().getExternalInfoUrl());
                xmlWriter.writeAttribute("priority", String.valueOf(rv.getRule().getPriority().getPriority()));
                writeNewLine();
                xmlWriter.writeCharacters(StringUtil.removedInvalidXml10Characters(rv.getDescription()));
                writeNewLine();
                xmlWriter.writeEndElement();
                writeNewLine();
            }
            if (filename != null) { // Not first file ?
                xmlWriter.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void end() throws IOException {
        try {
            // errors
            for (Report.ProcessingError pe : errors) {
                writeNewLine();
                xmlWriter.writeStartElement("error");
                xmlWriter.writeAttribute("filename", determineFileName(pe.getFile()));
                xmlWriter.writeAttribute("msg", pe.getMsg());
                writeNewLine();
                xmlWriter.writeCData(pe.getDetail());
                writeNewLine();
                xmlWriter.writeEndElement();
            }

            // suppressed violations
            if (showSuppressedViolations) {
                for (Report.SuppressedViolation s : suppressed) {
                    writeNewLine();
                    xmlWriter.writeStartElement("suppressedviolation");
                    xmlWriter.writeAttribute("filename", determineFileName(s.getRuleViolation().getFilename()));
                    xmlWriter.writeAttribute("suppressiontype", s.getSuppressor().getId().toLowerCase(Locale.ROOT));
                    xmlWriter.writeAttribute("msg", s.getRuleViolation().getDescription());
                    xmlWriter.writeAttribute("usermsg", s.getUserMessage() == null ? "" : s.getUserMessage());
                    xmlWriter.writeEndElement();
                }
            }

            // config errors
            for (final Report.ConfigurationError ce : configErrors) {
                writeNewLine();
                xmlWriter.writeEmptyElement("configerror");
                xmlWriter.writeAttribute("rule", ce.rule().getName());
                xmlWriter.writeAttribute("msg", ce.issue());
            }
            writeNewLine();
            xmlWriter.writeEndElement(); // </pmd>
            writeNewLine();
            xmlWriter.writeEndDocument();
            xmlWriter.flush();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    private void maybeAdd(String attr, String value) throws XMLStreamException {
        if (value != null && value.length() > 0) {
            xmlWriter.writeAttribute(attr, value);
        }
    }

    @Override
    public void setReportFile(String reportFilename) {
        String encoding = getProperty(ENCODING);

        try {
            this.stream = StringUtils.isBlank(reportFilename)
                    ? System.out : Files.newOutputStream(new File(reportFilename).toPath());

            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            this.xmlWriter = outputFactory.createXMLStreamWriter(this.stream, encoding);
            // for backwards compatibility, also provide a writer. Note: xmlWriter won't use that.
            this.writer = new WrappedOutputStreamWriter(xmlWriter, stream, encoding);
        } catch (IOException | XMLStreamException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void setWriter(final Writer writer) {
        String encoding = getProperty(ENCODING);
        // for backwards compatibility, create a OutputStream that writes to the writer.
        this.stream = IOUtil.fromWriter(writer, encoding);

        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        try {
            this.xmlWriter = outputFactory.createXMLStreamWriter(this.stream, encoding);
            // for backwards compatibility, also provide a writer.
            // Note: both XMLStreamWriter and this writer will write to this.stream
            this.writer = new WrappedOutputStreamWriter(xmlWriter, stream, encoding);
        } catch (XMLStreamException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class WrappedOutputStreamWriter extends OutputStreamWriter {
        private final XMLStreamWriter xmlWriter;

        WrappedOutputStreamWriter(XMLStreamWriter xmlWriter, OutputStream out, String charset) throws UnsupportedEncodingException {
            super(out, charset);
            this.xmlWriter = xmlWriter;
        }

        @Override
        public void flush() throws IOException {
            try {
                xmlWriter.flush();
            } catch (XMLStreamException e) {
                throw new IOException(e);
            }
            super.flush();
        }

        @Override
        public void close() throws IOException {
            try {
                xmlWriter.close();
            } catch (XMLStreamException e) {
                throw new IOException(e);
            }
            super.close();
        }
    }

    // FIXME: elapsed time not available until the end of the processing
    /*
     * private String createTimeElapsedAttr(Report rpt) {
     * Report.ReadableDuration d = new
     * Report.ReadableDuration(rpt.getElapsedTimeInMillis()); return
     * " elapsedTime=\"" + d.getTime() + "\""; }
     */
}
