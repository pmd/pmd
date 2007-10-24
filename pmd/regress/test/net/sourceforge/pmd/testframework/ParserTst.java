/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

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

public abstract class ParserTst {

    private class Collector<E> implements InvocationHandler {
        private Class<E> clazz = null;
        private Collection<E> collection;

        public Collector(Class<E> clazz) {
            this(clazz, new HashSet<E>());
        }

        public Collector(Class<E> clazz, Collection<E> coll) {
            this.clazz = clazz;
            this.collection = coll;
        }

        public Collection<E> getCollection() {
            return collection;
        }

        public Object invoke(Object proxy, Method method, Object params[]) throws Throwable {
            if (method.getName().equals("visit")) {
                if (clazz.isInstance(params[0])) {
                    collection.add((E) params[0]);
                }
            }

            Method childrenAccept = params[0].getClass().getMethod("childrenAccept", new Class[]{JavaParserVisitor.class, Object.class});
            childrenAccept.invoke(params[0], new Object[]{proxy, null});
            return null;
        }
    }

    public <E> Set<E> getNodes(Class<E> clazz, String javaCode) throws Throwable {
        return getNodes(TargetJDKVersion.DEFAULT_JDK_VERSION, clazz, javaCode);
    }

    public <E> Set<E> getNodes(TargetJDKVersion jdk, Class<E> clazz, String javaCode) throws Throwable {
        Collector<E> coll = new Collector<E>(clazz);
        JavaParser parser = jdk.createParser(new StringReader(javaCode));
        ASTCompilationUnit cu = parser.CompilationUnit();
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(), new Class[]{JavaParserVisitor.class}, coll);
        jpv.visit(cu, null);
        return (Set<E>) coll.getCollection();
    }

    public <E> List<E> getOrderedNodes(Class<E> clazz, String javaCode) throws Throwable {
        Collector<E> coll = new Collector<E>(clazz, new ArrayList<E>());
        JavaParser parser = TargetJDKVersion.DEFAULT_JDK_VERSION.createParser(new StringReader(javaCode));
        ASTCompilationUnit cu = parser.CompilationUnit();
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(), new Class[]{JavaParserVisitor.class}, coll);
        jpv.visit(cu, null);
        SymbolFacade sf = new SymbolFacade();
        sf.initializeWith(cu);
        DataFlowFacade dff = new DataFlowFacade();
        dff.initializeWith(cu);
        return (List<E>) coll.getCollection();
    }

    public ASTCompilationUnit buildDFA(String javaCode) throws Throwable {
        JavaParser parser = TargetJDKVersion.DEFAULT_JDK_VERSION.createParser(new StringReader(javaCode));
        ASTCompilationUnit cu = parser.CompilationUnit();
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(), new Class[]{JavaParserVisitor.class}, new Collector<ASTCompilationUnit>(ASTCompilationUnit.class));
        jpv.visit(cu, null);
        new SymbolFacade().initializeWith(cu);
        new DataFlowFacade().initializeWith(cu);
        return cu;
    }
}
