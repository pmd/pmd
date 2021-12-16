/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.util.document.TextDocument;

import apex.jorje.semantic.ast.compilation.Compilation;
import apex.jorje.semantic.compiler.ApexCompiler;
import apex.jorje.semantic.compiler.CompilationInput;
import apex.jorje.semantic.compiler.CompilerStage;
import apex.jorje.semantic.compiler.SourceFile;
import apex.jorje.semantic.compiler.sfdc.AccessEvaluator;
import apex.jorje.semantic.compiler.sfdc.NoopCompilerProgressCallback;
import apex.jorje.semantic.compiler.sfdc.QueryValidator;
import apex.jorje.semantic.compiler.sfdc.SymbolProvider;
import apex.jorje.services.exception.CompilationException;
import apex.jorje.services.exception.ParseException;

/**
 * Central point for interfacing with the compiler. Based on <a href=
 * "https://github.com/forcedotcom/idecore/blob/master/com.salesforce.ide.apex.core/src/com/salesforce/ide/apex/internal/core/CompilerService.java"
 * > CompilerService</a> but with Eclipse dependencies removed.
 *
 * @author nchen
 *
 */
class CompilerService {
    public static final CompilerService INSTANCE = new CompilerService();
    private final SymbolProvider symbolProvider;
    private final AccessEvaluator accessEvaluator;
    private final QueryValidator queryValidator;

    /**
     * Configure a compiler with the default configurations:
     */
    CompilerService() {
        this(EmptySymbolProvider.get(), new TestAccessEvaluator(), new TestQueryValidators.Noop());
    }

    /**
     * Configure a compiler with the following configurations:
     *
     * @param symbolProvider
     *            A way to retrieve symbols, where symbols are names of types.
     * @param accessEvaluator
     *            A way to check for accesses to certain fields in types.
     * @param queryValidator
     *            A way to validate your queries.
     */
    CompilerService(SymbolProvider symbolProvider, AccessEvaluator accessEvaluator, QueryValidator queryValidator) {
        this.symbolProvider = symbolProvider;
        this.accessEvaluator = accessEvaluator;
        this.queryValidator = queryValidator;
    }


    /** @throws ParseException If the code is unparsable */
    public Compilation parseApex(TextDocument document) {
        SourceFile sourceFile = SourceFile.builder()
                                          .setBody(document.getText().toString())
                                          .setKnownName(document.getDisplayName())
                                          .build();
        ApexCompiler compiler = ApexCompiler.builder().setInput(createCompilationInput(Collections.singletonList(sourceFile))).build();
        compiler.compile(CompilerStage.POST_TYPE_RESOLVE);
        throwParseErrorIfAny(compiler);
        return compiler.getCodeUnits().get(0).getNode();
    }

    private void throwParseErrorIfAny(ApexCompiler compiler) {
        // this ignores semantic errors

        ParseException parseError = null;
        for (CompilationException error : compiler.getErrors()) {
            if (error instanceof ParseException) {
                if (parseError == null) {
                    parseError = (ParseException) error;
                } else {
                    parseError.addSuppressed(error);
                }
            }
        }
        if (parseError != null) {
            throw parseError;
        }
    }

    private CompilationInput createCompilationInput(List<SourceFile> sourceFiles) {
        return new CompilationInput(sourceFiles, symbolProvider, accessEvaluator, queryValidator, null,
                                    NoopCompilerProgressCallback.get());
    }
}
