package net.sourceforge.pmd.util.designer;

import java.io.PrintStream;

public class MyPrintStream extends PrintStream {

    public MyPrintStream() {
        super(System.out);
    }

    private StringBuffer buf = new StringBuffer();

    public void println(String s) {
        super.println(s);
        buf.append(s);
        buf.append(System.getProperty("line.separator"));
    }

    public String getString() {
        return buf.toString();
    }
}

