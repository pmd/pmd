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

public class Mark {

    private int indexIntoFile;
    private int indexIntoTokenArray;
    private String tokenSrcID;
    private int  beginLine;

    public Mark(int offset, String tokenSrcID, int index, int beginLine) {
        this.indexIntoTokenArray = offset;
        this.indexIntoFile = index;
        this.tokenSrcID = tokenSrcID;
        this.beginLine = beginLine;
    }

    public int getBeginLine() {
        return this.beginLine;
    }

    public String getTokenSrcID() {
        return this.tokenSrcID;
    }

    public int getIndexIntoFile() {
        return this.indexIntoFile;
    }

    public int getIndexIntoTokenArray() {
        return indexIntoTokenArray;
    }

    public String toString() {
        return "Mark:\r\nindexIntoFile = " + indexIntoFile + "\r\nindexIntoTokenArray = " + indexIntoTokenArray + "\r\nbeginLine = " + beginLine;
    }
}
