package net.sourceforge.pmd.cpd;

import java.util.Iterator;

public interface MatchListener {
    public void matchFound(Match match);
    public Iterator iterator();
}
