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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Match implements Comparable {

    private int tokenCount;
    private int lineCount;
    private List marks = new ArrayList();
    private String code;

    public Match(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    public Match(int tokenCount, Mark first, Mark second) {
        marks.add(first);
        marks.add(second);
        this.tokenCount = tokenCount;
    }

    public void add(Mark mark) {
        marks.add(mark);
    }

    public int getMarkCount() {
        return this.marks.size();
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public int getLineCount() {
        return this.lineCount;
    }

    public int getTokenCount() {
        return this.tokenCount;
    }

    public String getSourceCodeSlice() {
        return this.code;
    }

    public void setSourceCodeSlice(String code) {
        this.code = code;
    }

    public Iterator iterator() {
        return marks.iterator();
    }

    public int compareTo(Object o) {
        Match other = (Match)o;
        return other.getTokenCount() - this.getTokenCount();
    }

    public String toString() {
        return "Match:\r\ntokenCount = " + tokenCount + "\r\nmark1 = " + marks.get(0) + "\r\nmark2 =" + marks.get(1);
    }
}
