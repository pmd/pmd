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
