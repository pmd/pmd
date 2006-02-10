/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.PapariTextRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.SummaryHTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.VBHTMLRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.renderers.YAHTMLRenderer;
import org.apache.tools.ant.BuildException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Formatter {

    private File toFile;
    private String linkPrefix;
    private String type;
    private boolean toConsole;

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

    public Renderer getRenderer() {
        Renderer renderer;
        if (type.equals("xml")) {
            renderer = new XMLRenderer();
        } else if (type.equals("html")) {
            renderer = new HTMLRenderer(linkPrefix);
        } else if (type.equals("summaryhtml")) {
            renderer = new SummaryHTMLRenderer();
        } else if (type.equals("papari")) {
            renderer = new PapariTextRenderer();
        } else if (type.equals("csv")) {
            renderer = new CSVRenderer();
        } else if (type.equals("text")) {
            renderer = new TextRenderer();
        } else if (type.equals("emacs")) {
            renderer = new EmacsRenderer();
        } else if (type.equals("vbhtml")) {
            renderer = new VBHTMLRenderer();
        } else if (type.equals("yahtml")) {
            renderer = new YAHTMLRenderer();
        } else if (!type.equals("")) {
            try {
                renderer = (Renderer) Class.forName(type).newInstance();
            } catch (Exception e) {
                throw new BuildException("Unable to instantiate custom formatter: " + type);
            }
        } else {
            throw new BuildException("Formatter type must be 'xml', 'text', 'html', 'emacs', 'summaryhtml', 'papari', 'csv', 'vbhtml', 'yahtml', or a class name; you specified " + type);
        }
        return renderer;
    }

    public boolean isToFileNull() {
        return toFile == null && !toConsole;
    }

    public Writer getToFileWriter(String baseDir) throws IOException {
        if (!toConsole) {
            if (!toFile.isAbsolute()) {
                return new BufferedWriter(new FileWriter(new File(baseDir + System.getProperty("file.separator") + toFile.getPath())));
            }
            return new BufferedWriter(new FileWriter(toFile));
        } else {
            return new BufferedWriter(new OutputStreamWriter(System.out));
        }
    }

    public String toString() {
        return "file = " + toFile + "; renderer = " + type;
    }
}
