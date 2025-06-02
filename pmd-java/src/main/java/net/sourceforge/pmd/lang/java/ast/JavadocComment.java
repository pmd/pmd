/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;

/**
 * A {@link JavaComment} that has Javadoc content.
 */
public final class JavadocComment extends JavaComment {

    private JavadocCommentOwner owner;

    // markdown comments consist of multiple single line comments
    private final List<JavaccToken> tokens;


    JavadocComment(JavaccToken t) {
        super(t);
        assert t.kind == JavaTokenKinds.FORMAL_COMMENT || JavaAstUtils.isMarkdownComment(t);
        this.tokens = Collections.emptyList();
    }

    JavadocComment(List<JavaComment> currentMarkdownBlock) {
        super(currentMarkdownBlock.get(0).getToken());
        assert currentMarkdownBlock.stream().map(JavaComment::getToken).allMatch(JavaAstUtils::isMarkdownComment);
        this.tokens = currentMarkdownBlock
                .stream()
                .map(JavaComment::getToken)
                .collect(Collectors.toList());
    }

    @Override
    public Chars getText() {
        if (tokens.isEmpty()) {
            return super.getText();
        }

        StringBuilder markdownBlock = new StringBuilder(tokens.size() * 80);
        for (JavaccToken token : tokens) {
            token.getText().appendChars(markdownBlock);
        }
        return Chars.wrap(markdownBlock.toString());
    }

    @Override
    public FileLocation getReportLocation() {
        if (tokens.isEmpty()) {
            return super.getReportLocation();
        }

        JavaccToken firstToken = tokens.get(0);
        JavaccToken lastToken = tokens.get(tokens.size() - 1);
        TextRegion region = TextRegion.fromBothOffsets(firstToken.getRegion().getStartOffset(), lastToken.getRegion().getEndOffset());
        return firstToken.getDocument().getTextDocument().toLocation(region);
    }

    @Override
    public boolean isSingleLine() {
        if (tokens.isEmpty()) {
            return super.isSingleLine();
        }
        return tokens.size() == 1;
    }

    void setOwner(JavadocCommentOwner owner) {
        this.owner = owner;
    }

    /**
     * Returns the owner of this comment. Null if this comment is 
     * misplaced.
     */
    public @Nullable JavadocCommentOwner getOwner() {
        return owner;
    }

}
