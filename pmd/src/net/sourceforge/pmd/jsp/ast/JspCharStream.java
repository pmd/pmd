package net.sourceforge.pmd.jsp.ast;

import java.io.InputStream;
import java.io.Reader;

import net.sourceforge.pmd.ast.JavaCharStream;

public class JspCharStream extends JavaCharStream implements CharStream {

	public JspCharStream(InputStream dstream, int startline, int startcolumn, int buffersize) {
		super(dstream, startline, startcolumn, buffersize);
		// TODO Auto-generated constructor stub
	}

	public JspCharStream(InputStream dstream, int startline, int startcolumn) {
		super(dstream, startline, startcolumn);
		// TODO Auto-generated constructor stub
	}

	public JspCharStream(InputStream dstream) {
		super(dstream);
		// TODO Auto-generated constructor stub
	}

	public JspCharStream(Reader dstream, int startline, int startcolumn, int buffersize) {
		super(dstream, startline, startcolumn, buffersize);
		// TODO Auto-generated constructor stub
	}

	public JspCharStream(Reader dstream, int startline, int startcolumn) {
		super(dstream, startline, startcolumn);
		// TODO Auto-generated constructor stub
	}

	public JspCharStream(Reader dstream) {
		super(dstream);
		// TODO Auto-generated constructor stub
	}

}
