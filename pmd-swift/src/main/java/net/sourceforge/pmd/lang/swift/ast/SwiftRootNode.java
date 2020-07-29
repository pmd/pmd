/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;

// package private base class
abstract class SwiftRootNode extends SwiftInnerNode implements RootNode {

    private String filename;
    private LanguageVersion languageVersion;

    SwiftRootNode() {
        super();
    }

    SwiftRootNode(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }


    @Override
    public String getSourceCodeFile() {
        return filename;
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    void addTaskInfo(ParserTask languageVersion) {
        this.languageVersion = languageVersion.getLanguageVersion();
        this.filename = languageVersion.getFileDisplayName();
    }

}
