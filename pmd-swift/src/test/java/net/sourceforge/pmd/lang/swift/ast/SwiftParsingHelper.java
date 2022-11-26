/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.swift.SwiftLanguageModule;
import net.sourceforge.pmd.lang.swift.ast.SwiftParser.SwTopLevel;

public class SwiftParsingHelper extends BaseParsingHelper<SwiftParsingHelper, SwTopLevel> {

    public static final SwiftParsingHelper DEFAULT = new SwiftParsingHelper(Params.getDefault());


    public SwiftParsingHelper(@NonNull Params params) {
        super(SwiftLanguageModule.NAME, SwTopLevel.class, params);
    }

    @NonNull
    @Override
    protected SwiftParsingHelper clone(@NonNull Params params) {
        return new SwiftParsingHelper(params);
    }
}
