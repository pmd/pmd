package net.sourceforge.pmd.lang.apex;

/**
 * This simple example shows how to interface with the parser.
 * 
 * @author nchen
 *
 */
public class Main {
	public static final String APEX_SOURCE = "public class SimpleClass {\n" + 
			"    public void methodWithManyParams(String a, String b, String c, String d, String e, String f, String g) {\n" + 
			"        \n" + 
			"    }\n" + 
			"}";

	public static void main(String[] args) {
		CompilerService.INSTANCE.visitAstFromString(APEX_SOURCE, new SampleVisitor());
	}
}
