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

public class Tokens {

    private List tokens = new ArrayList();

    public void add(TokenEntry tokenEntry) {
        this.tokens.add(tokenEntry);
    }

    public Iterator iterator() {
        return tokens.iterator();
    }

    private TokenEntry get(int index) {
        return (TokenEntry)tokens.get(index);
    }

    public int size() {
        return tokens.size();
    }

    public int getLineCount(Mark mark, Match match) {
        TokenEntry endTok = get(mark.getIndexIntoTokenArray() + match.getTokenCount());
        if (endTok.equals(TokenEntry.EOF)) {
            endTok = get(mark.getIndexIntoTokenArray() + match.getTokenCount() - 1);
        }
        return endTok.getBeginLine() - mark.getBeginLine() - 1;
    }
}
