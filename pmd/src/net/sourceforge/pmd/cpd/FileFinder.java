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
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileFinder {

    private FilenameFilter filter;
    private static final String FILE_SEP = System.getProperty("file.separator");

    public List findFilesFrom(String dir, FilenameFilter filter, boolean recurse) {
        this.filter = filter;
        List files = new ArrayList();
        scanDirectory(new File(dir), files, recurse);
        return files;
    }

    /**
     * Implements a tail recursive file scanner
     */
    private void scanDirectory(File dir, List list, boolean recurse) {
        String[] candidates = dir.list(filter);
        for (int i = 0; i < candidates.length; i++) {
            File tmp = new File(dir + FILE_SEP + candidates[i]);
            if (tmp.isDirectory()) {
                if (recurse) {
                    scanDirectory(tmp, list, true);
                }
            } else {
                list.add(new File(dir + FILE_SEP + candidates[i]));
            }
        }
    }
}
