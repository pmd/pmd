package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.Renderer;
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
    private String toFile;

    public void setType(String type) {
        if (type.equals("xml")) {
            renderer = new XMLRenderer();
        } else if (type.equals("html")) {
            renderer = new HTMLRenderer();
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
            throw new BuildException("Formatter type must be 'xml', 'text', 'html', or a class name; you specified " + type);
        }
    }

    public void setToFile(String toFile) {
        this.toFile = toFile;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public boolean isToFileNull() {
        return this.toFile == null;
    }

    public Writer getToFileWriter(String baseDir) throws IOException {
        String outFile = toFile;
        PathChecker pc = new PathChecker(System.getProperty("os.name"));
        if (!pc.isAbsolute(toFile)) {
            outFile = baseDir + System.getProperty("file.separator") + toFile;
        }
        return new BufferedWriter(new FileWriter(new File(outFile)));
    }
}
