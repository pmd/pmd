package net.sourceforge.pmd.dcpd;

import net.jini.core.entry.Entry;
import net.sourceforge.pmd.cpd.TokenSets;
import net.sourceforge.pmd.cpd.TokenList;
import net.sourceforge.pmd.cpd.TokenEntry;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class TokenSetsWrapper implements Entry {

    public TokenSets tss;
    public Integer jobID;

    public TokenSetsWrapper() {}

    public TokenSetsWrapper(TokenSets tss, Integer jobID) {
        this.tss = tss;
        this.jobID = jobID;
    }

}
