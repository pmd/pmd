package net.sourceforge.pmd.cpd;

public class RollingHash {

	private final static int MOD = 37;
    private int a;
    private String lastFile;
    private int length;
    private int firstMod = 1;
    private MatchAlgorithm ma;

    public RollingHash(int length, MatchAlgorithm ma) {
        this.length = length;
        this.ma = ma;
        for (int i = 0; i < length; i++) {
            firstMod *= MOD;
        }
    }

    public void compute(Mark m1) {
        if (lastFile != m1.getTokenSrcID()) {
            a = 0;
            for (int i = 0; i < length; i++) {
                a = MOD*a + ma.tokenAt(i, m1).getIdentifier();
            }
        } else {
            int last = ma.tokenAt(length - 1, m1).getIdentifier();
            int first = ma.tokenAt(-1, m1).getIdentifier();
            a = MOD * a - first * firstMod + last;
        }
        m1.setHashCode(a);
        lastFile = m1.getTokenSrcID();
        return;
    }

}