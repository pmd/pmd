/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.PapariTextRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.SummaryHTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import org.apache.tools.ant.BuildException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Formatter {

    private Renderer renderer;
    private File toFile;

    public void setType(String type) {
        if (type.equals("xml")) {
            renderer = new XMLRenderer();
        } else if (type.equals("html")) {
            renderer = new HTMLRenderer();
        } else if (type.equals("summaryhtml")) {
            renderer = new SummaryHTMLRenderer();
        } else if (type.equals("papari")) {
            renderer = new PapariTextRenderer();
        } else if (type.equals("csv")) {
            renderer = new CSVRenderer();
        } else if (type.equals("text")) {
            renderer = new TextRenderer();
        } else if (!type.equals("")) {
            try {
                renderer = (Renderer)Class.forName(type).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new BuildException("Unable to instantiate custom formatter: " + type);
            }
        } else {
            throw new BuildException("Formatter type must be 'xml', 'text', 'html', 'summaryhtml', 'papari', 'csv', or a class name; you specified " + type);
        }
    }

    public void setToFile(File toFile) {
        this.toFile = toFile;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public boolean isToFileNull() {
        return toFile == null;
    }

    public Writer getToFileWriter(String baseDir) throws IOException {
        if (!toFile.isAbsolute()) {
            return new BufferedWriter(new FileWriter(new File(baseDir + System.getProperty("file.separator") + toFile.getPath())));
        }
        return new BufferedWriter(new FileWriter(toFile));
    }

    public String toString() {
        return "file = " + toFile + "; renderer = " + renderer.getClass().getName();
    }
}
