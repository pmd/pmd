package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SimpleListener implements MatchListener {

    private List matches = new ArrayList();

    public void matchFound(Match match) {
        matches.add(match);
    }

    public Iterator iterator() {
        return matches.iterator();
    }

}
