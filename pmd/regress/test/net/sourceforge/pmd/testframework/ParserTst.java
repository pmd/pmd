/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import junit.framework.TestCase;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.JavaParserVisitor;
import net.sourceforge.pmd.dfa.DataFlowFacade;
import net.sourceforge.pmd.symboltable.SymbolFacade;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParserTst extends TestCase {

    private class Collector implements InvocationHandler {
        private Class clazz = null;
        private Collection collection;

        public Collector(Class clazz) {
            this(clazz, new HashSet());
        }

        public Collector(Class clazz, Collection coll) {
            this.clazz = clazz;
            this.collection = coll;
        }

        public Collection getCollection() {
            return collection;
        }

        public Object invoke(Object proxy, Method method, Object params[]) throws Throwable {
            if (method.getName().equals("visit")) {
                if (clazz.isInstance(params[0])) {
                    collection.add(params[0]);
                }
            }

            Method childrenAccept = params[0].getClass().getMethod("childrenAccept", new Class[]{JavaParserVisitor.class, Object.class});
            childrenAccept.invoke(params[0], new Object[]{proxy, null});
            return null;
        }
    }

    public Set getNodes(Class clazz, String javaCode) throws Throwable {
        return getNodes(new TargetJDK1_4(), clazz, javaCode);
    }

    public Set getNodes(TargetJDKVersion jdk, Class clazz, String javaCode) throws Throwable {
        Collector coll = new Collector(clazz);
        JavaParser parser = jdk.createParser(new StringReader(javaCode));
        ASTCompilationUnit cu = parser.CompilationUnit();
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(), new Class[]{JavaParserVisitor.class}, coll);
        jpv.visit(cu, null);
        return (Set) coll.getCollection();
    }

    public List getOrderedNodes(Class clazz, String javaCode) throws Throwable {
        Collector coll = new Collector(clazz, new ArrayList());
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(javaCode));
        ASTCompilationUnit cu = parser.CompilationUnit();
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(), new Class[]{JavaParserVisitor.class}, coll);
        jpv.visit(cu, null);
        SymbolFacade sf = new SymbolFacade();
        sf.initializeWith(cu);
        DataFlowFacade dff = new DataFlowFacade();
        dff.initializeWith(cu);
        return (List) coll.getCollection();
    }

    public ASTCompilationUnit buildDFA(String javaCode) throws Throwable {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(javaCode));
        ASTCompilationUnit cu = parser.CompilationUnit();
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(), new Class[]{JavaParserVisitor.class}, new Collector(ASTCompilationUnit.class));
        jpv.visit(cu, null);
        new SymbolFacade().initializeWith(cu);
        new DataFlowFacade().initializeWith(cu);
        return cu;
    }
}
