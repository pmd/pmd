/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.util.document.TextDocument;

// package private base class
abstract class SwiftRootNode extends SwiftInnerNode implements RootNode {

    private TextDocument textDocument;

    SwiftRootNode() {
        super();
    }

    SwiftRootNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }


    @Override
    public @NonNull TextDocument getTextDocument() {
        return textDocument;
    }

    SwiftRootNode addTaskInfo(ParserTask task) {
        textDocument = task.getTextDocument();
        return this;
    }


}
