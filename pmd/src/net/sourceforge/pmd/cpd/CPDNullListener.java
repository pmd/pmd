package net.sourceforge.pmd.cpd;

import java.io.File;

public class CPDNullListener implements CPDListener {
    public void addedFile(int fileCount, File file) {}
    public void comparisonCountUpdate(long comparisons) {}
}
