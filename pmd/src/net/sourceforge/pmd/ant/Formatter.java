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
import net.sourceforge.pmd.renderers.XSLTRenderer;
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

    private static final Map<String, RendererBuilder> renderersByCode = new HashMap<String, RendererBuilder>(8);

    static {
        renderersByCode.put("xml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new XMLRenderer(); }
        });
        renderersByCode.put("betterhtml", new RendererBuilder() {
            public Renderer build(Object[] arg) { return new XSLTRenderer(); }
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

    private Writer writer;

    private Renderer renderer;

    public Renderer getRenderer() {
        return renderer;
    }

    public void start(String baseDir) {
        try {
            if (toConsole) {
                writer = new BufferedWriter(new OutputStreamWriter(System.out));
            }
            if (toFile != null) {
                writer = getToFileWriter(baseDir);
            }
            renderer = getRenderer(toConsole);
            renderer.setWriter(writer);
            renderer.start();
        } catch (IOException ioe) {
            throw new BuildException(ioe.getMessage());
        }
    }

    public void end(Report errorReport) {
        try {
            renderer.renderFileReport(errorReport);
            renderer.end();
            writer.write(PMD.EOL);
            if (toConsole) {
                writer.flush();
            } else {
                writer.close();
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
        return renderersByCode.keySet().toArray(new String[renderersByCode.size()]);
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
        RendererBuilder builder = renderersByCode.get(type);
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
