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

import net.sourceforge.pmd.PMD;

import java.util.List;

public class SourceCode {

    private String fileName;
    private List code;

    public SourceCode(String fileName) {
        this.fileName = fileName;
    }

    public void setCode(List newCode) {
        code = newCode;
    }

    public String getSlice(int startLine, int endLine) {
        StringBuffer sb = new StringBuffer();
        for (int i = startLine; i <= endLine && i < code.size(); i++) {
            if (sb.length() != 0) {
                sb.append(PMD.EOL);
            }
            sb.append((String) code.get(i));
        }
        return sb.toString();
    }

    public String getFileName() {
        return fileName;
    }

    public boolean equals(Object other) {
        SourceCode o = (SourceCode) other;
        return o.fileName.equals(fileName);
    }

    public int hashCode() {
        return fileName.hashCode();
    }
}
