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
package net.sourceforge.pmd.util;

import net.sourceforge.pmd.RuleSetNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {

    // Single static method, so we shouldn't allow an instance to be created
    private ResourceLoader() {
    }

    /**
     *
     * Method to find a file, first by finding it as a file
     * (either by the absolute or relative path), then as
     * a URL, and then finally seeing if it is on the classpath.
     *
     */
    public static InputStream loadResourceAsStream(String name) throws RuleSetNotFoundException {
        InputStream stream = ResourceLoader.loadResourceAsStream(name, new ResourceLoader().getClass().getClassLoader());
        if (stream == null) {
            throw new RuleSetNotFoundException("Can't find resource " + name + ". Make sure the resource is a valid file or URL or is on the CLASSPATH");
        }
        return stream;
    }

    /**
     *
     * Uses the ClassLoader passed in to attempt to load the
     * resource if it's not a File or a URL
     *
     */
    public static InputStream loadResourceAsStream(String name, ClassLoader loader) throws RuleSetNotFoundException {
        File file = new File(name);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                // if the file didn't exist, we wouldn't be here
            }
        } else {
            try {
                return new URL(name).openConnection().getInputStream();
            } catch (Exception e) {
                return loader.getResourceAsStream(name);
            }
        }
        throw new RuleSetNotFoundException("Can't find resource " + name + ". Make sure the resource is a valid file or URL or is on the CLASSPATH");
    }
}
