package net.sourceforge.pmd.util.designer;

import java.io.PrintStream;

public class MyPrintStream extends PrintStream {

    private StringBuffer buf = new StringBuffer();
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
    public MyPrintStream() {
        super(System.out);
    }

    public void println(String s) {
        super.println(s);
        buf.append(s);
        buf.append(LINE_SEPARATOR);
    }

    public String getString() {
        return buf.toString();
    }
}

