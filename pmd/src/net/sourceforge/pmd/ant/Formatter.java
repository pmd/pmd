/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.renderers.CSVRenderer;
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
    private File toFile;

    public void setType(String type) {
        if (type.equals("xml")) {
            renderer = new XMLRenderer();
        } else if (type.equals("html")) {
            renderer = new HTMLRenderer();
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
            throw new BuildException("Formatter type must be 'xml', 'text', 'html', 'csv', or a class name; you specified " + type);
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
