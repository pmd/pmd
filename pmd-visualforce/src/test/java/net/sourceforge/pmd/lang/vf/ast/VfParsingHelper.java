/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.vf.VFTestUtils;
import net.sourceforge.pmd.lang.vf.VfLanguageModule;

public final class VfParsingHelper extends BaseParsingHelper<VfParsingHelper, ASTCompilationUnit> {

    public static final VfParsingHelper DEFAULT = new VfParsingHelper(Params.getDefault());

    public VfParsingHelper(Params params) {
        super(VfLanguageModule.NAME, ASTCompilationUnit.class, params);
    }

    @Override
    protected @NonNull LanguageProcessorRegistry loadLanguages(@NonNull Params params) {
        // We need to register both apex and VF, the default is just to register VF
        return VFTestUtils.fakeLpRegistry();
    }

    @Override
    protected VfParsingHelper clone(Params params) {
        return new VfParsingHelper(params);
    }
}
