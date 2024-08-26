/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.rule.MetricRule;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

/**
 * Renderer to metrics XML format.
 */
public class MetricsRenderer extends XMLRenderer {

    public static final String METRICS_NAME = "metrics-xml";

    public MetricsRenderer() {
        super(METRICS_NAME, "Metrics XML format.");
    }

    public MetricsRenderer(String encoding) {
        this();
        setProperty(ENCODING, encoding);
    }

    @Override
    public void start() throws IOException {
        initLineSeparator();

        try {
            getXmlWriter().writeStartDocument(getProperty(ENCODING), "1.0");
            writeNewLine();
            getXmlWriter().writeStartElement("metrics");
            getXmlWriter().writeAttribute("version", PMDVersion.VERSION);
            getXmlWriter().writeAttribute("timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date()));
            getXmlWriter().writeAttribute("source", "PMD");
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        String packageName = null;
        String filename = null;
        String className = null;
        FileLocation methodLocation = null;

        try {
            // rule violations
            while (violations.hasNext()) {
                RuleViolation rv = violations.next();
                if (!(rv.getRule() instanceof MetricRule)) {
                    continue;
                }
                String nextPackageName = rv.getAdditionalInfo().get(RuleViolation.PACKAGE_NAME);
                if (nextPackageName == null) {
                    continue;
                }
                if (!nextPackageName.equals(packageName)) {
                    if (methodLocation != null) {
                        writeNewLine();
                        getXmlWriter().writeEndElement();
                    }
                    if (className != null) {
                        writeNewLine();
                        getXmlWriter().writeEndElement();
                    }
                    if (filename != null) {
                        writeNewLine();
                        getXmlWriter().writeEndElement();
                    }
                    // New package
                    if (packageName != null) {
                        // Not first package ?
                        writeNewLine();
                        getXmlWriter().writeEndElement();
                    }
                    packageName = nextPackageName;
                    filename = null;
                    className = null;
                    methodLocation = null;
                    writeNewLine();
                    getXmlWriter().writeStartElement("package");
                    getXmlWriter().writeAttribute("name", packageName);
                }
                String nextFilename = determineFileName(rv.getFileId());
                if (!nextFilename.equals(filename)) {
                    if (methodLocation != null) {
                        writeNewLine();
                        getXmlWriter().writeEndElement();
                    }
                    if (className != null) {
                        writeNewLine();
                        getXmlWriter().writeEndElement();
                    }
                    // New File
                    if (filename != null) {
                        // Not first file ?
                        writeNewLine();
                        getXmlWriter().writeEndElement();
                    }
                    filename = nextFilename;
                    className = null;
                    methodLocation = null;
                    writeNewLine();
                    getXmlWriter().writeStartElement("file");
                    getXmlWriter().writeAttribute("name", filename);
                }
                String nextClassName = rv.getAdditionalInfo().get(RuleViolation.CLASS_NAME);
                if (!nextClassName.equals(className)) {
                    if (methodLocation != null) {
                        writeNewLine();
                        getXmlWriter().writeEndElement();
                    }
                    // New Class
                    if (className != null) {
                        // Not first class ?
                        writeNewLine();
                        getXmlWriter().writeEndElement();
                    }
                    className = nextClassName;
                    methodLocation = null;
                    writeNewLine();
                    getXmlWriter().writeStartElement("class");
                    getXmlWriter().writeAttribute("name", className);
                }
                MetricRule.Infos infos = MetricRule.getInfos(rv.getDescription());
                if (infos.type == MetricRule.Type.METHOD) {
                    FileLocation nextMethodLocation = rv.getLocation();
                    if (!nextMethodLocation.equals(methodLocation)) {
                        if (methodLocation != null) {
                            writeNewLine();
                            getXmlWriter().writeEndElement();
                        }
                        methodLocation = rv.getLocation();
                        writeNewLine();
                        getXmlWriter().writeStartElement("method");
                        getXmlWriter().writeAttribute("name", rv.getAdditionalInfo().get(RuleViolation.METHOD_NAME));
                        getXmlWriter().writeAttribute("beginline", String.valueOf(rv.getBeginLine()));
                        getXmlWriter().writeAttribute("endline", String.valueOf(rv.getEndLine()));
                        getXmlWriter().writeAttribute("begincolumn", String.valueOf(rv.getBeginColumn()));
                        getXmlWriter().writeAttribute("endcolumn", String.valueOf(rv.getEndColumn()));
                    }
                } else if (methodLocation != null) {
                    methodLocation = null;
                    writeNewLine();
                    getXmlWriter().writeEndElement();
                }

                writeNewLine();
                getXmlWriter().writeEmptyElement("metric");
                getXmlWriter().writeAttribute("name", infos.checkName);
                getXmlWriter().writeAttribute("value", infos.value);
            }
            if (methodLocation != null) {
                writeNewLine();
                getXmlWriter().writeEndElement();
            }
            if (className != null) {
                writeNewLine();
                getXmlWriter().writeEndElement();
                writeNewLine();
            }
            if (filename != null) {
                getXmlWriter().writeEndElement();
                writeNewLine();
            }
            if (packageName != null) {
                getXmlWriter().writeEndElement();
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
                getXmlWriter().writeStartElement("error");
                getXmlWriter().writeAttribute("filename", determineFileName(pe.getFileId()));
                getXmlWriter().writeAttribute("msg", pe.getMsg());
                writeNewLine();
                getXmlWriter().writeCData(pe.getDetail());
                writeNewLine();
                getXmlWriter().writeEndElement();
            }

            // config errors
            for (final Report.ConfigurationError ce : configErrors) {
                writeNewLine();
                getXmlWriter().writeEmptyElement("error");
                getXmlWriter().writeAttribute("filename", ce.rule().getName());
                getXmlWriter().writeAttribute("msg", ce.issue());
            }
            writeNewLine();
            getXmlWriter().writeEndElement(); // </pmd>
            writeNewLine();
            getXmlWriter().writeEndDocument();
            getXmlWriter().flush();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
