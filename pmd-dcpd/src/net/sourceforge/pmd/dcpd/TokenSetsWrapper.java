package net.sourceforge.pmd.dcpd;

import net.jini.core.entry.Entry;
import net.sourceforge.pmd.cpd.TokenSets;
import net.sourceforge.pmd.cpd.TokenList;
import net.sourceforge.pmd.cpd.TokenEntry;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class TokenSetsWrapper implements Entry {

    public TokenSets tokenSets;
    public Job job;

    public TokenSetsWrapper() {}

    public TokenSetsWrapper(TokenSets tss, Job job) {
        this.tokenSets = tss;
        this.job = job;
    }

}
