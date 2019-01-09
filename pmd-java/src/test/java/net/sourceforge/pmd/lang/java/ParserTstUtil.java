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
import java.nio.charset.StandardCharsets;
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
import net.sourceforge.pmd.lang.java.qname.QualifiedNameResolver;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;

public class ParserTstUtil {

    private ParserTstUtil() {

    }

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

    // TODO provide a configurable api to choose which visitors to invoke
    // it makes no sense

    public static <E> Set<E> getNodes(Class<E> clazz, String javaCode) {
        return getNodes(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion(), clazz, javaCode);
    }

    public static <E> Set<E> getNodes(LanguageVersion languageVersion, Class<E> clazz, String javaCode) {
        Collector<E> coll = new Collector<>(clazz);
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        ASTCompilationUnit cu = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(javaCode));
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(),
                new Class[] { JavaParserVisitor.class }, coll);
        jpv.visit(cu, null);
        return (Set<E>) coll.getCollection();
    }

    public static <E> List<E> getOrderedNodes(Class<E> clazz, String javaCode) {
        Collector<E> coll = new Collector<>(clazz, new ArrayList<E>());
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        ASTCompilationUnit cu = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(javaCode));
        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(),
                new Class[] { JavaParserVisitor.class }, coll);
        jpv.visit(cu, null);
        new QualifiedNameResolver().initializeWith(ParserTstUtil.class.getClassLoader(), cu);
        new SymbolFacade().initializeWith(cu);
        new DataFlowFacade().initializeWith(languageVersionHandler.getDataFlowHandler(), cu);

        return (List<E>) coll.getCollection();
    }

    public static <E> String dumpNodes(List<E> list) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (E item : list) {
            sb.append("\n node[").append(index).append(item.toString());
            index++;
        }
        return sb.toString();
    }

    public static ASTCompilationUnit buildDFA(String javaCode) {
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

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava13(String code) {
        return parseJava(getLanguageVersionHandler("1.3"), code);
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava14(String code) {
        return parseJava(getLanguageVersionHandler("1.4"), code);
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava15(String code) {
        return parseJava(getLanguageVersionHandler("1.5"), code);
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava17(String code) {
        return parseJava(getLanguageVersionHandler("1.7"), code);
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava18(String code) {
        return parseJava(getLanguageVersionHandler("1.8"), code);
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava9(String code) {
        return parseJava(getLanguageVersionHandler("9"), code);
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava10(String code) {
        return parseJava(getLanguageVersionHandler("10"), code);
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava13(Class<?> source) {
        return parseJava13(getSourceFromClass(source));
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava14(Class<?> source) {
        return parseJava14(getSourceFromClass(source));
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava15(Class<?> source) {
        return parseJava15(getSourceFromClass(source));
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava17(Class<?> source) {
        return parseJava17(getSourceFromClass(source));
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava18(Class<?> source) {
        return parseJava18(getSourceFromClass(source));
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava9(Class<?> source) {
        return parseJava9(getSourceFromClass(source));
    }

    /** @see #parseJava(LanguageVersionHandler, String)  */
    public static ASTCompilationUnit parseJava10(Class<?> source) {
        return parseJava10(getSourceFromClass(source));
    }

    /** @see #parseJava(LanguageVersionHandler, String) */
    public static ASTCompilationUnit parseJavaDefaultVersion(String source) {
        return parseJava(getDefaultLanguageVersionHandler(), source);
    }

    /** @see #parseJava(LanguageVersionHandler, String) */
    public static ASTCompilationUnit parseJavaDefaultVersion(Class<?> source) {
        return parseJavaDefaultVersion(getSourceFromClass(source));
    }


    public static AbstractJavaHandler getLanguageVersionHandler(String version) {
        return (AbstractJavaHandler) LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion(version).getLanguageVersionHandler();
    }

    public static AbstractJavaHandler getDefaultLanguageVersionHandler() {
        return (AbstractJavaHandler) LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion().getLanguageVersionHandler();
    }


    /**
     * Parses Java code and executes the symbol table visitor.
     *
     * @param languageVersionHandler The version handler for the wanted version
     * @param code                   The source code
     *
     * @return The compilation unit
     */
    public static ASTCompilationUnit parseJava(LanguageVersionHandler languageVersionHandler, String code) {
        ASTCompilationUnit rootNode = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(code));
        languageVersionHandler.getQualifiedNameResolutionFacade(ParserTstUtil.class.getClassLoader()).start(rootNode);
        languageVersionHandler.getSymbolFacade().start(rootNode);
        return rootNode;
    }

    public static String getSourceFromClass(Class<?> clazz) {
        String sourceFile = clazz.getName().replace('.', '/') + ".java";
        InputStream is = ParserTstUtil.class.getClassLoader().getResourceAsStream(sourceFile);
        if (is == null) {
            throw new IllegalArgumentException(
                "Unable to find source file " + sourceFile + " for " + clazz);
        }
        String source;
        try {
            source = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return source;
    }

    public static ASTCompilationUnit parseAndTypeResolveJava(String javaVersion, String sourceCode) {
        LanguageVersionHandler languageVersionHandler = getLanguageVersionHandler(javaVersion);
        ASTCompilationUnit rootNode = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions())
                    .parse(null, new StringReader(sourceCode));
        languageVersionHandler.getQualifiedNameResolutionFacade(ParserTstUtil.class.getClassLoader()).start(rootNode);
        languageVersionHandler.getSymbolFacade().start(rootNode);
        languageVersionHandler.getDataFlowFacade().start(rootNode);
        languageVersionHandler.getTypeResolutionFacade(ParserTstUtil.class.getClassLoader()).start(rootNode);
        languageVersionHandler.getMultifileFacade().start(rootNode);
        return rootNode;
    }
}
