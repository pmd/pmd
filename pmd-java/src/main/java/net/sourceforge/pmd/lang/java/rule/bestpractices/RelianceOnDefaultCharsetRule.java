/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;

/**
 * Detects constructors and methods that use the JVM's default character set
 * instead of explicitly specifying a charset. This rule helps ensure
 * platform-independent text encoding behavior.
 * 
 * <p>The rule only flags violations when the appropriate replacement method
 * with explicit charset parameter is available in the target Java version.
 *
 * @since 7.17.0
 */
public class RelianceOnDefaultCharsetRule extends AbstractJavaRulechainRule {

    /**
     * Maps each violating method/constructor to the minimum Java version
     * where its charset-aware replacement is available.
     */
    private static final Map<InvocationMatcher, String> METHOD_TO_MIN_VERSION = new HashMap<>();
    
    static {
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.InputStreamReader#new(java.io.InputStream)"), "1.3");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.OutputStreamWriter#new(java.io.OutputStream)"), "1.3");

        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.net.URLEncoder#encode(java.lang.String)"), "1.4");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.net.URLDecoder#decode(java.lang.String)"), "1.4");
        
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.PrintStream#new(java.io.OutputStream)"), "1.4");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.PrintStream#new(java.io.OutputStream,boolean)"), "1.4");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.PrintStream#new(java.lang.String)"), "1.5");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.PrintStream#new(java.io.File)"), "1.5");

        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.PrintWriter#new(java.lang.String)"), "1.5");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.PrintWriter#new(java.io.File)"), "1.5");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.PrintWriter#new(java.io.OutputStream)"), "10");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.PrintWriter#new(java.io.OutputStream,boolean)"), "10");

        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.util.Scanner#new(java.io.InputStream)"), "1.5");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.util.Scanner#new(java.io.File)"), "1.5");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.util.Scanner#new(java.nio.file.Path)"), "1.7");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.util.Scanner#new(java.nio.channels.ReadableByteChannel)"), "1.10");

        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.util.Formatter#new()"), "1.5");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.util.Formatter#new(java.io.OutputStream)"), "1.5");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.util.Formatter#new(java.io.PrintStream)"), "1.5");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.util.Formatter#new(java.lang.String)"), "1.5");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.util.Formatter#new(java.io.File)"), "1.5");
        
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.lang.String#new(byte[])"), "1.6");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.lang.String#new(byte[],int,int)"), "1.6");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.lang.String#getBytes()"), "1.6");

        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.ByteArrayOutputStream#toString()"), "10");
        
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.FileReader#new(java.lang.String)"), "11");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.FileReader#new(java.io.File)"), "11");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.FileReader#new(java.io.FileDescriptor)"), "11");

        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.FileWriter#new(java.lang.String)"), "11");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.FileWriter#new(java.lang.String,boolean)"), "11");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.FileWriter#new(java.io.File)"), "11");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.FileWriter#new(java.io.File,boolean)"), "11");
        METHOD_TO_MIN_VERSION.put(InvocationMatcher.parse("java.io.FileWriter#new(java.io.FileDescriptor)"), "11");
    }

    public RelianceOnDefaultCharsetRule() {
        super(ASTConstructorCall.class, ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        checkInvocation(node, data);
        return data;
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        checkInvocation(node, data);
        return data;
    }

    private void checkInvocation(net.sourceforge.pmd.lang.java.ast.JavaNode node, Object data) {
        for (Map.Entry<InvocationMatcher, String> entry : METHOD_TO_MIN_VERSION.entrySet()) {
            InvocationMatcher matcher = entry.getKey();
            String minVersion = entry.getValue();
            
            // Only flag violation if replacement method is available in current Java version
            if (node.getLanguageVersion().compareToVersion(minVersion) >= 0 && matcher.matchesCall(node)) {
                asCtx(data).addViolation(node);
            }
        }
    }
}
