/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Support for compile-time type resolution on the AST.
 */
package net.sourceforge.pmd.lang.java.types;
/*
TODO:  inference of `throws` clause
    -> pretty hard, see throws constraint formulas in JLS 18

import java.io.IOException;

@FunctionalInterface
interface ThrowingRunnable<E extends Throwable> {

    void run() throws E;
}

class Scratch {

    static <E extends Throwable> void wrap(ThrowingRunnable<? extends E> runnable) throws E {
        runnable.run();
    }

    static void runThrowing() throws IOException {
        throw new IOException();
    }

    {
        try {
            wrap(Scratch::runThrowing); // throws IOException
        } catch (IOException e) {

        }
    }

}
 */


/*
TODO: qualified super ctor call
   -> Update CtorInvocMirror.ExplicitCtorInvocMirror#getNewType


class Outer {
    class Inner<T> {
        public Inner(T value) { }
    }
}


class Scratch extends Outer.Inner<String> {

    public Scratch(Outer o) {
        o.super("value");
    }
}

 */

/* TODO: an array initializer is an assignment context
    -> see PolyResolution to fix it

    class Scratch {

        final Runnable r[] = {
            () -> { } // is a Runnable
        }

    }
 */

/* TODO: bug with visibility and override

At:   /home/clifrr/Bureau/jdk13-src/java.base/java/util/Date.java:1174 :30..1174:71
Expr: ((ZoneInfo)tz).getOffsets(fastTime, null)
[WARNING] Ambiguity error: both methods are maximally specific
    sun.util.calendar.ZoneInfo.getOffsets(long, int[]) -> int
    java.util.TimeZone.getOffsets(long, int[]) -> int


package java.util;

public class TimeZone implements Serializable, Cloneable {


    // package visibility
    int getOffsets(long date, int[] offsets) {
        int rawoffset = getRawOffset();
        int dstoffset = 0;
        if (inDaylightTime(new Date(date))) {
            dstoffset = getDSTSavings();
        }
        if (offsets != null) {
            offsets[0] = rawoffset;
            offsets[1] = dstoffset;
        }
        return rawoffset + dstoffset;
    }

}


package sun.util.calendar;

public class ZoneInfo extends TimeZone {
    // doesn't override, because the super method is not accessible
    public int getOffsets(long utc, int[] offsets) {
        return 0;
    }
}

package java.util;

public class Date {
    {
        TimeZone tz = TimeZone.getDefaultRef();
        if (tz instanceof ZoneInfo) {
            // the one of ZoneInfo is selected
            zoneOffset = ((ZoneInfo)tz).getOffsets(fastTime, null);
        }
    }
}

 */

/* TODO possibly, the type node for a diamond should have the parameterized
    type, for now it's a raw type
    See TypesFromAst

import java.util.ArrayList;

class O {
    {
        List<String> l = new ArrayList<>();
        //                   -----------
        //                   this node has a raw type, maybe it should have type ArrayList<String>

        // Note that the whole expression already has type ArrayList<String> after inference
    }
}

 */

/* TODO test bridge method execution filtering
    In AsmLoaderTest


 */


/* TODO finish NamedReferenceExpr by patching LazyTypeResolver

 */


/* TODO test explicitly typed lambda (in ExplicitTypesTest)

 */


/* TODO real anonymous types
    - define hooks on JClassType (isAnonymous/projectAnonymous)
    - handle this in projectUpwards

class Scratch {

    public static void main(String[] args) {
        new Object() {
            void def() {}
        }.def(); // ok

        // type is Object, not Scratch$2
        var foo = new Object() {
            void def() {}
        };
    }
}



 */


/* TODO qualified anonymous constructor

class Scratch {

    class Inner {}

    public static void main(String[] args) {
        new Scratch().new Inner() {

        };
    }
}



 */

/*

In: /home/clifrr/Bureau/jdk13-src/jdk.hotspot.agent/sun/jvm/hotspot/gc/z/ZPageTable.java:74:23
org.apache.commons.lang3.exception.ContextedRuntimeException: java.lang.ClassCastException: class net.sourceforge.pmd.lang.java.types.SentinelType cannot be cast to class net.sourceforge.pmd.lang.java.types.JClassType (net.sourceforge.pmd.lang.java.types.SentinelType and net.sourceforge.pmd.lang.java.types.JClassType are in unnamed module of loader 'app')
Exception Context:
	[1:Resolving type of=[ConstructorCall:74:23]map().new Iterator()]
---------------------------------
	at net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode.addContextValue(AbstractJavaTypeNode.java:49)
	at net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode.getTypeMirror(AbstractJavaTypeNode.java:41)
	at net.sourceforge.pmd.lang.java.ast.ASTConstructorCall.getTypeMirror(ASTConstructorCall.java:25)
	at net.sourceforge.pmd.lang.java.rule.security.TypeResTestRule.visit(TypeResTestRule.java:83)
	at net.sourceforge.pmd.lang.java.rule.security.TypeResTestRule.visit(TypeResTestRule.java:73)
	at net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit.acceptVisitor(ASTCompilationUnit.java:44)
	at net.sourceforge.pmd.lang.java.ast.AbstractJavaNode.acceptVisitor(AbstractJavaNode.java:40)
	at net.sourceforge.pmd.lang.java.ast.JavaNode.jjtAccept(JavaNode.java:34)
	at net.sourceforge.pmd.lang.java.rule.AbstractJavaRule.apply(AbstractJavaRule.java:67)
	at net.sourceforge.pmd.lang.rule.internal.RuleApplicator.applyOnIndex(RuleApplicator.java:61)
	at net.sourceforge.pmd.lang.rule.internal.RuleApplicator.apply(RuleApplicator.java:47)
	at net.sourceforge.pmd.RuleSets.apply(RuleSets.java:145)
	at net.sourceforge.pmd.SourceCodeProcessor.processSource(SourceCodeProcessor.java:165)
	at net.sourceforge.pmd.SourceCodeProcessor.processSourceCodeWithoutCache(SourceCodeProcessor.java:108)
	at net.sourceforge.pmd.SourceCodeProcessor.processSourceCode(SourceCodeProcessor.java:90)
	at net.sourceforge.pmd.SourceCodeProcessor.processSourceCode(SourceCodeProcessor.java:52)
	at net.sourceforge.pmd.processor.PmdRunnable.call(PmdRunnable.java:78)
	at net.sourceforge.pmd.processor.PmdRunnable.call(PmdRunnable.java:24)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:830)
Caused by: java.lang.ClassCastException: class net.sourceforge.pmd.lang.java.types.SentinelType cannot be cast to class net.sourceforge.pmd.lang.java.types.JClassType (net.sourceforge.pmd.lang.java.types.SentinelType and net.sourceforge.pmd.lang.java.types.JClassType are in unnamed module of loader 'app')
	at net.sourceforge.pmd.lang.java.types.internal.infer.ast.CtorInvocMirror.getNewType(CtorInvocMirror.java:81)
	at net.sourceforge.pmd.lang.java.types.internal.infer.ast.CtorInvocMirror.getVisibleCandidates(CtorInvocMirror.java:64)
	at net.sourceforge.pmd.lang.java.types.internal.infer.ast.CtorInvocMirror.getAccessibleCandidates(CtorInvocMirror.java:76)
	at net.sourceforge.pmd.lang.java.types.internal.infer.Infer.computeCompileTimeDecl(Infer.java:262)
	at net.sourceforge.pmd.lang.java.types.internal.infer.Infer.getCompileTimeDecl(Infer.java:240)
	at net.sourceforge.pmd.lang.java.types.internal.infer.Infer.goToInvocationWithFallback(Infer.java:173)
	at net.sourceforge.pmd.lang.java.types.internal.infer.Infer.inferInvocationRecursively(Infer.java:162)
	at net.sourceforge.pmd.lang.java.ast.PolyResolution.inferInvocation(PolyResolution.java:176)
	at net.sourceforge.pmd.lang.java.ast.PolyResolution.computePolyType(PolyResolution.java:111)
	at net.sourceforge.pmd.lang.java.ast.LazyTypeResolver.handlePoly(LazyTypeResolver.java:200)
	at net.sourceforge.pmd.lang.java.ast.LazyTypeResolver.visit(LazyTypeResolver.java:230)
	at net.sourceforge.pmd.lang.java.ast.LazyTypeResolver.visit(LazyTypeResolver.java:43)
	at net.sourceforge.pmd.lang.java.ast.ASTConstructorCall.acceptVisitor(ASTConstructorCall.java:38)
	at net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode.getTypeMirror(AbstractJavaTypeNode.java:37)
	... 22 more
 */
