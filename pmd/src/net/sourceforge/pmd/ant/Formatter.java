package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import org.apache.tools.ant.BuildException;

import java.io.Writer;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

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
        } else {
            throw new BuildException("Formatter type must be 'xml', 'text', or 'html'; you specified " + type);
        }
    }

    public void setToFile(String toFile) {
        this.toFile = toFile;
    }

    public Renderer getRenderer() {
        return renderer;
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
