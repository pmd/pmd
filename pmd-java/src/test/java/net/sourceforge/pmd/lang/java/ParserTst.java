/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.java.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;

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

        public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
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
        return getNodes(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion(), clazz, javaCode);
    }

    public <E> Set<E> getNodes(LanguageVersion languageVersion, Class<E> clazz, String javaCode) throws Throwable {
        Collector<E> coll = new Collector<E>(clazz);
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
	ASTCompilationUnit cu = (ASTCompilationUnit)languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(javaCode));
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(), new Class[]{JavaParserVisitor.class}, coll);
        jpv.visit(cu, null);
        return (Set<E>) coll.getCollection();
    }

    public <E> List<E> getOrderedNodes(Class<E> clazz, String javaCode) throws Throwable {
        Collector<E> coll = new Collector<E>(clazz, new ArrayList<E>());
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion().getLanguageVersionHandler();
        ASTCompilationUnit cu = (ASTCompilationUnit)languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(javaCode));
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(), new Class[]{JavaParserVisitor.class}, coll);
        jpv.visit(cu, null);
        SymbolFacade sf = new SymbolFacade();
        sf.initializeWith(cu);
        DataFlowFacade dff = new DataFlowFacade();
        dff.initializeWith(languageVersionHandler.getDataFlowHandler(), cu);

        return (List<E>) coll.getCollection();
    }
    
    public <E> String dumpNodes(List<E> list ) throws Throwable {
	    StringBuilder sb = new StringBuilder () ;
	    int index = 0;
	    for (E item : list) {
		    sb.append("\n node[").append(index).append(item.toString());
		    index ++;
	  }
	  return sb.toString();
    }

    public ASTCompilationUnit buildDFA(String javaCode) throws Throwable {
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion().getLanguageVersionHandler();
	ASTCompilationUnit cu = (ASTCompilationUnit)languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(javaCode));
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(), new Class[]{JavaParserVisitor.class}, new Collector<ASTCompilationUnit>(ASTCompilationUnit.class));
        jpv.visit(cu, null);
        new SymbolFacade().initializeWith(cu);
        new DataFlowFacade().initializeWith(languageVersionHandler.getDataFlowHandler(), cu);
        return cu;
    }
    
    public ASTCompilationUnit parseJava13(String code) {
        return parseJava(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.3"), code);
    }
    public ASTCompilationUnit parseJava14(String code) {
        return parseJava(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.4"), code);
    }
    public ASTCompilationUnit parseJava15(String code) {
        return parseJava(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"), code);
    }
    public ASTCompilationUnit parseJava17(String code) {
        return parseJava(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.7"), code);
    }
    public ASTCompilationUnit parseJava18(String code) {
        return parseJava(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.8"), code);
    }
    public ASTCompilationUnit parseJava(LanguageVersion languageVersion, String code) {
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        ASTCompilationUnit rootNode = (ASTCompilationUnit)languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(code));
        languageVersionHandler.getSymbolFacade().start(rootNode);
        return rootNode;
    }
}
