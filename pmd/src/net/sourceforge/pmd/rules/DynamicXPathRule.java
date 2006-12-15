package net.sourceforge.pmd.rules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import net.sourceforge.pmd.jaxen.MatchesFunction;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.SimpleVariableContext;
import org.jaxen.XPath;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class DynamicXPathRule extends AbstractRule implements Opcodes {

    protected DynamicXPathRule() {
    }

    private static HashMap classes = new HashMap();

    public static synchronized Class loadClass(ClassLoader classloader, String type) {
        Class c = (Class) classes.get(type);
        if (c == null) {
            byte bytecode[] = buildClass(type);
            c = new ByteArrayClassLoader(classloader).loadClass(bytecode);

            classes.put(type, c);
        }

        return c;
    }

    private static class ByteArrayClassLoader extends ClassLoader {
        ByteArrayClassLoader(ClassLoader parent) {
            super(parent);
        }
        
        Class loadClass(byte[] data) {
            return defineClass(null, data, 0, data.length, null);
        }
    }

    private static byte[] buildClass(String type) {
        String className = "net/sourceforge/pmd/rules/" + type + "XPathRule";
        String methodSig = "(Lnet/sourceforge/pmd/ast/AST" + type + ";Ljava/lang/Object;)Ljava/lang/Object;";

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit(V1_4, ACC_PUBLIC + ACC_SUPER, className, null, "net/sourceforge/pmd/rules/DynamicXPathRule", null);

        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "net/sourceforge/pmd/rules/DynamicXPathRule", "<init>", "()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC, "visit", methodSig, null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "evaluate", "(Lnet/sourceforge/pmd/ast/Node;Ljava/lang/Object;)V");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, "net/sourceforge/pmd/rules/DynamicXPathRule", "visit", methodSig);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();

        cw.visitEnd();

        return cw.toByteArray();
    }


    private XPath xpath;

    private boolean regexpFunctionRegistered;

    /**
     * Evaluate the AST with compilationUnit as root-node, against
     * the XPath expression found as property with name "xpath".
     * All matches are reported as violations.
     *
     * @param compilationUnit the Node that is the root of the AST to be checked
     * @param data
     */
    public void evaluate(Node compilationUnit, Object data) {
        try {
            initializeXPathExpression();
            List results = xpath.selectNodes(compilationUnit);
            for (Iterator i = results.iterator(); i.hasNext();) {
                SimpleNode n = (SimpleNode) i.next();
                if (n instanceof ASTVariableDeclaratorId && getBooleanProperty("pluginname")) {
                    addViolation(data, n, n.getImage());
                } else {
                    addViolation(data, (SimpleNode) n, getMessage());
                }
            }
        } catch (JaxenException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void initializeXPathExpression() throws JaxenException {
        if (xpath != null) {
            return;
        }

        if (!regexpFunctionRegistered) {
            MatchesFunction.registerSelfInSimpleContext();
            regexpFunctionRegistered = true;
        }

        String prop = getStringProperty("xpath");

        String tail = prop.trim().replaceFirst("^//\\w+", "");
        String subquery = '.' + tail.trim();
        
        xpath = new BaseXPath(subquery, new DocumentNavigator());
        if (properties.size() > 1) {
            SimpleVariableContext vc = new SimpleVariableContext();
            for (Iterator i = properties.entrySet().iterator(); i.hasNext();) {
                Entry e = (Entry) i.next();
                if (!"xpath".equals(e.getKey())) {
                    vc.setVariableValue((String) e.getKey(), e.getValue());
                }
            }
            xpath.setVariableContext(vc);
        }
    }

}

