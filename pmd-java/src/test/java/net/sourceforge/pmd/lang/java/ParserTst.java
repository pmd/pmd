/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.java.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;

public abstract class ParserTst {

    private static class Collector<E> implements InvocationHandler {
        private Class<E> clazz = null;
        private Collection<E> collection;

        Collector(Class<E> clazz) {
            this(clazz, new HashSet<E>());
        }

        Collector(Class<E> clazz, Collection<E> coll) {
            this.clazz = clazz;
            this.collection = coll;
        }

        public Collection<E> getCollection() {
            return collection;
        }

        public Object invoke(Object proxy, Method method, Object[] params) throws NoSuchMethodException,
                SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            if (method.getName().equals("visit")) {
                if (clazz.isInstance(params[0])) {
                    collection.add(clazz.cast(params[0]));
                }
            }

            Method childrenAccept = params[0].getClass().getMethod("childrenAccept",
                                                                   JavaParserVisitor.class, Object.class);
            childrenAccept.invoke(params[0], (JavaParserVisitor) proxy, null);
            return null;
        }
    }

    public <E> Set<E> getNodes(Class<E> clazz, String javaCode) {
        return getNodes(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion(), clazz, javaCode);
    }

    protected <E> Set<E> getNodes(LanguageVersion languageVersion, Class<E> clazz, String javaCode) {
        Collector<E> coll = new Collector<>(clazz);
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        ASTCompilationUnit cu = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(javaCode));
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(),
                new Class[] { JavaParserVisitor.class }, coll);
        jpv.visit(cu, null);
        return (Set<E>) coll.getCollection();
    }

    protected <E> List<E> getOrderedNodes(Class<E> clazz, String javaCode) {
        Collector<E> coll = new Collector<>(clazz, new ArrayList<E>());
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        ASTCompilationUnit cu = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(javaCode));
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(),
                new Class[] { JavaParserVisitor.class }, coll);
        jpv.visit(cu, null);
        SymbolFacade sf = new SymbolFacade();
        sf.initializeWith(cu);
        DataFlowFacade dff = new DataFlowFacade();
        dff.initializeWith(languageVersionHandler.getDataFlowHandler(), cu);

        return (List<E>) coll.getCollection();
    }

    public <E> String dumpNodes(List<E> list) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (E item : list) {
            sb.append("\n node[").append(index).append(item.toString());
            index++;
        }
        return sb.toString();
    }

    protected ASTCompilationUnit buildDFA(String javaCode) {
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        ASTCompilationUnit cu = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(javaCode));
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(),
                new Class[] { JavaParserVisitor.class }, new Collector<>(ASTCompilationUnit.class));
        jpv.visit(cu, null);
        new SymbolFacade().initializeWith(cu);
        new DataFlowFacade().initializeWith(languageVersionHandler.getDataFlowHandler(), cu);
        return cu;
    }

    /** @see #parseJava(String, String)  */
    protected ASTCompilationUnit parseJava13(String code) {
        return parseJava("1.3", code);
    }

    /** @see #parseJava(String, String)  */
    protected ASTCompilationUnit parseJava14(String code) {
        return parseJava("1.4", code);
    }

    /** @see #parseJava(String, String)  */
    protected ASTCompilationUnit parseJava15(String code) {
        return parseJava("1.5", code);
    }

    /** @see #parseJava(String, String)  */
    protected ASTCompilationUnit parseJava17(String code) {
        return parseJava("1.7", code);
    }

    /** @see #parseJava(String, String)  */
    protected ASTCompilationUnit parseJava18(String code) {
        return parseJava("1.8", code);
    }

    /** @see #parseJava(String, String)  */
    protected ASTCompilationUnit parseJava13(Class<?> source) {
        return parseJava13(getSourceFromClass(source));
    }

    /** @see #parseJava(String, String)  */
    protected ASTCompilationUnit parseJava14(Class<?> source) {
        return parseJava14(getSourceFromClass(source));
    }

    /** @see #parseJava(String, String)  */
    protected ASTCompilationUnit parseJava15(Class<?> source) {
        return parseJava15(getSourceFromClass(source));
    }

    /** @see #parseJava(String, String)  */
    protected ASTCompilationUnit parseJava17(Class<?> source) {
        return parseJava17(getSourceFromClass(source));
    }

    /** @see #parseJava(String, String)  */
    protected ASTCompilationUnit parseJava18(Class<?> source) {
        return parseJava18(getSourceFromClass(source));
    }


    /**
     * Parses Java code and executes the symbol table visitor.
     *
     * @param version The Java version to use
     * @param code    The source code
     *
     * @return The compilation unit
     */
    protected ASTCompilationUnit parseJava(String version, String code) {
        LanguageVersion languageVersion = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion(version);
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        ASTCompilationUnit rootNode = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(code));
        languageVersionHandler.getSymbolFacade().start(rootNode);
        return rootNode;
    }

    private String getSourceFromClass(Class<?> clazz) {
        String sourceFile = clazz.getName().replace('.', '/') + ".java";
        InputStream is = ParserTst.class.getClassLoader().getResourceAsStream(sourceFile);
        if (is == null) {
            throw new IllegalArgumentException(
                "Unable to find source file " + sourceFile + " for " + clazz);
        }
        String source;
        try {
            source = IOUtils.toString(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return source;
    }
}
