/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.JavaParserVisitor;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

public class ParserTst extends TestCase {
    private class Collector implements InvocationHandler {
        private Class clazz = null;
        private Set collection = new HashSet();

        public Collector(Class clazz) {
            this.clazz = clazz;
        }

        public Set getCollection() {
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
        Collector coll = new Collector(clazz);
        JavaParser parser = new JavaParser(new StringReader(javaCode));

        ASTCompilationUnit cu = parser.CompilationUnit();

        JavaParserVisitor jpv = (JavaParserVisitor) Proxy.newProxyInstance(JavaParserVisitor.class.getClassLoader(), new Class[]{JavaParserVisitor.class}, coll);
        jpv.visit(cu, null);
        return coll.getCollection();
    }

}
