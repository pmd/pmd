/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.PapariTextRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.SummaryHTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.VBHTMLRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.renderers.YAHTMLRenderer;
import net.sourceforge.pmd.renderers.CSVRenderer;
import org.apache.tools.ant.BuildException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Formatter {

    private interface RendererBuilder {
        Renderer build(Object[] optionalArg);
    } // factory template

    private File toFile;
    private String linkPrefix;
    private String linePrefix;
    private String type;
    private boolean toConsole;
    private boolean showSuppressed;

    private static final Map renderersByCode = new HashMap(8);

    static {
        renderersByCode.put("xml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new XMLRenderer(); }
        });
        renderersByCode.put("html", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new HTMLRenderer((String) arg[0], (String) arg[1]); }
        });
        renderersByCode.put("summaryhtml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new SummaryHTMLRenderer((String) arg[0], (String) arg[1]); }
        });
        renderersByCode.put("papari", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new PapariTextRenderer(); }
        });
        renderersByCode.put("csv", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new CSVRenderer(); }
        });
        renderersByCode.put("emacs", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new EmacsRenderer(); }
        });
        renderersByCode.put("vbhtml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new VBHTMLRenderer(); }
        });
        renderersByCode.put("yahtml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new YAHTMLRenderer(); }
        });
        renderersByCode.put("text", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new TextRenderer(); }
        });
        // add additional codes & factories here
    }

    public void setShowSuppressed(boolean value) {
        this.showSuppressed = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLinkPrefix(String linkPrefix) {
        this.linkPrefix = linkPrefix;
    }

    public void setToFile(File toFile) {
        this.toFile = toFile;
    }

    public void setToConsole(boolean toConsole) {
        this.toConsole = toConsole;
    }

    public void setLinePrefix(String linePrefix) {
        this.linePrefix = linePrefix;
    }

    public void outputReport(Report report, String baseDir) {
        try {
            if (toConsole) {
                outputReportTo(new BufferedWriter(new OutputStreamWriter(System.out)), report, true);
            }
            if (toFile != null) {
                outputReportTo(getToFileWriter(baseDir), report, false);
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage());
        }
    }

    public boolean isNoOutputSupplied() {
        return toFile == null && !toConsole;
    }

    public String toString() {
        return "file = " + toFile + "; renderer = " + type;
    }

    private static String[] validRendererCodes() {
        Iterator iter = renderersByCode.keySet().iterator();
        String[] validTypes = new String[renderersByCode.size()];
        int i = 0;
        while (iter.hasNext()) validTypes[i++] = (String) iter.next();
        return validTypes;
    }

    private void outputReportTo(Writer writer, Report report, boolean consoleRenderer) throws IOException {
        getRenderer(consoleRenderer).render(writer, report);
        writer.write(PMD.EOL);
        writer.close();
    }


    private static String unknownRendererMessage(String userSpecifiedType) {
        StringBuffer sb = new StringBuffer(100);
        sb.append("Formatter type must be one of: '");
        String[] typeCodes = validRendererCodes();
        sb.append(typeCodes[0]);
        for (int i = 1; i < typeCodes.length; i++) {
            sb.append("', '").append(typeCodes[i]);
        }
        sb.append("', or a class name; you specified: ");
        sb.append(userSpecifiedType);
        return sb.toString();
    }

    private Renderer fromClassname(String rendererClassname) {
        try {
            return (Renderer) Class.forName(rendererClassname).newInstance();
        } catch (Exception e) {
            throw new BuildException(unknownRendererMessage(rendererClassname));
        }
    }

    // FIXME - hm, what about this consoleRenderer thing... need a test for this
    private Renderer getRenderer(boolean consoleRenderer) {
        if ("".equals(type)) {
            throw new BuildException(unknownRendererMessage("<unspecified>"));
        }
        RendererBuilder builder = (RendererBuilder) renderersByCode.get(type);
        Renderer renderer = builder == null ? fromClassname(type) : builder.build(new String[]{linkPrefix, linePrefix});
        renderer.showSuppressedViolations(showSuppressed);
        return renderer;
    }

    private Writer getToFileWriter(String baseDir) throws IOException {
        if (!toFile.isAbsolute()) {
            return new BufferedWriter(new FileWriter(new File(baseDir + System.getProperty("file.separator") + toFile.getPath())));
        }
        return new BufferedWriter(new FileWriter(toFile));
    }
}
