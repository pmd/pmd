/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import java.io.IOException;
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
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitor;
import net.sourceforge.pmd.lang.plsql.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.plsql.symboltable.SymbolFacade;

public abstract class AbstractPLSQLParserTst {

    private class Collector<E> implements InvocationHandler {
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
                    collection.add((E) params[0]);
                }
            }

            Method childrenAccept = params[0].getClass().getMethod("childrenAccept",
                    new Class[] { PLSQLParserVisitor.class, Object.class });
            childrenAccept.invoke(params[0], new Object[] { proxy, null });
            return null;
        }
    }

    public <E> Set<E> getNodes(Class<E> clazz, String plsqlCode) {
        return getNodes(LanguageRegistry.getLanguage(PLSQLLanguageModule.NAME).getDefaultVersion(), clazz, plsqlCode);
    }

    public <E> Set<E> getNodes(LanguageVersion languageVersion, Class<E> clazz, String plsqlCode) {
        Collector<E> coll = new Collector<>(clazz);
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        ASTInput cu = (ASTInput) languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions())
                .parse(null, new StringReader(plsqlCode));
        PLSQLParserVisitor jpv = (PLSQLParserVisitor) Proxy.newProxyInstance(PLSQLParserVisitor.class.getClassLoader(),
                new Class[] { PLSQLParserVisitor.class }, coll);
        jpv.visit(cu, null);
        return (Set<E>) coll.getCollection();
    }

    public <E> List<E> getOrderedNodes(Class<E> clazz, String plsqlCode) {
        Collector<E> coll = new Collector<>(clazz, new ArrayList<E>());
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(PLSQLLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        ASTInput cu = (ASTInput) languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions())
                .parse(null, new StringReader(plsqlCode));
        PLSQLParserVisitor jpv = (PLSQLParserVisitor) Proxy.newProxyInstance(PLSQLParserVisitor.class.getClassLoader(),
                new Class[] { PLSQLParserVisitor.class }, coll);
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

    public ASTInput buildDFA(String plsqlCode) {
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(PLSQLLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        ASTInput cu = (ASTInput) languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions())
                .parse(null, new StringReader(plsqlCode));
        PLSQLParserVisitor jpv = (PLSQLParserVisitor) Proxy.newProxyInstance(PLSQLParserVisitor.class.getClassLoader(),
                new Class[] { PLSQLParserVisitor.class }, new Collector<>(ASTInput.class));
        jpv.visit(cu, null);
        new SymbolFacade().initializeWith(cu);
        new DataFlowFacade().initializeWith(languageVersionHandler.getDataFlowHandler(), cu);
        return cu;
    }

    public ASTInput parsePLSQL(LanguageVersion languageVersion, String code) {
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        return (ASTInput) languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).parse(null,
                new StringReader(code));
    }

    public ASTInput parsePLSQL(String code) {
        return parsePLSQL(LanguageRegistry.getLanguage(PLSQLLanguageModule.NAME).getDefaultVersion(), code);
    }

    public Node parseLanguage(LanguageVersion languageVersion, String code) {
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        return languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).parse(null,
                new StringReader(code));
    }

    public String loadTestResource(String name) {
        try {
            return IOUtils.toString(this.getClass().getResourceAsStream(name), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
