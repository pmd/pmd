/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.swift.SwiftLanguageModule;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.TopLevelContext;

/**
 *
 */
public class SwiftParsingHelper extends BaseParsingHelper<SwiftParsingHelper, TopLevelContext> {

    public static final SwiftParsingHelper DEFAULT = new SwiftParsingHelper(Params.getDefaultNoProcess());


    public SwiftParsingHelper(@NotNull Params params) {
        super(SwiftLanguageModule.NAME, TopLevelContext.class, params);
    }

    @NotNull
    @Override
    protected SwiftParsingHelper clone(@NotNull Params params) {
        return new SwiftParsingHelper(params);
    }
}
