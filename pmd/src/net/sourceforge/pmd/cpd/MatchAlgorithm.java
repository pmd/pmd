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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MatchAlgorithm {

    private Map pool = new TreeMap();
    private List code = new ArrayList();
    private List marks = new ArrayList();
    private List matches;
    private Map source;
    private Tokens tokens;
    private CPDListener cpdListener;

    public MatchAlgorithm(Map sourceCode, Tokens tokens) {
        this.source = sourceCode;
        this.tokens = tokens;
        for (Iterator i = tokens.iterator(); i.hasNext();) {
            add((TokenEntry)i.next());
        }
    }

    public void setListener(CPDListener listener) {
        this.cpdListener = listener;
    }

    public void add(TokenEntry token) {
        if (!pool.containsKey(token)) {
            pool.put(token, token);
        }
        code.add(pool.get(token));
        if (!(token.equals(TokenEntry.EOF))) {
            marks.add(new Mark(code.size(), token.getTokenSrcID(), token.getIndex(), token.getBeginLine()));
        }
    }

    public void findMatches(int min) {
       /*
         Assign sort codes to all the pooled code. This should speed
         up sorting them.
       */
        int count = 1;
        for (Iterator iter = pool.keySet().iterator(); iter.hasNext();) {
           TokenEntry token = (TokenEntry)iter.next();
           token.setSortCode(count++);
        }

        MarkComparator mc = new MarkComparator(cpdListener, code);
        Collections.sort(marks, mc);

        MatchCollector coll = new MatchCollector(marks, mc);
        matches = coll.collect(min);
        Collections.sort(matches);

        for (Iterator i = matches(); i.hasNext();) {
            Match match = (Match)i.next();
            for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
                Mark mark = (Mark)occurrences.next();
                match.setLineCount(tokens.getLineCount(mark, match));
                if (!occurrences.hasNext()) {
                    int start = mark.getBeginLine();
                    int end = start + match.getLineCount() - 1;
                    SourceCode sourceCode = (SourceCode)source.get(mark.getTokenSrcID());
                    match.setSourceCodeSlice(sourceCode.getSlice(start, end));
                }
            }
        }
    }

    public Iterator matches() {
        return matches.iterator();
    }
}

