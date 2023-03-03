/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.test

import net.sourceforge.pmd.*
import net.sourceforge.pmd.internal.util.IOUtil
import net.sourceforge.pmd.lang.*
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.Parser.ParserTask
import net.sourceforge.pmd.lang.ast.RootNode
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter
import net.sourceforge.pmd.lang.document.TextDocument
import net.sourceforge.pmd.lang.document.TextFile
import net.sourceforge.pmd.lang.rule.XPathRule
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion
import net.sourceforge.pmd.reporting.GlobalAnalysisListener
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Language-independent base for a parser utils class.
 * Implementations are language-specific.
 */
abstract class BaseParsingHelper<Self : BaseParsingHelper<Self, T>, T : RootNode>(
        protected val langName: String,
        private val rootClass: Class<T>,
        protected val params: Params
) {

    data class Params(
        val doProcess: Boolean,
        val defaultVerString: String?,
        val resourceLoader: Class<*>?,
        val resourcePrefix: String,
        val languageRegistry: LanguageRegistry = LanguageRegistry.PMD,
        val suppressMarker: String = PMD.SUPPRESS_MARKER,
    ) {
        companion object {

            @JvmStatic
            val default = Params(true, null, null, "")

        }
    }

    internal val resourceLoader: Class<*>
        get() = params.resourceLoader ?: javaClass

    internal val resourcePrefix: String get() = params.resourcePrefix

    /**
     * Returns the language version with the given version string.
     * If null, this defaults to the default language version for
     * this instance (not necessarily the default language version
     * defined by the language module).
     */
    fun getVersion(version: String?): LanguageVersion {
        val language = language
        return if (version == null) language.defaultVersion
        else language.getVersion(version)
            ?: throw AssertionError("Unsupported version $version for language $language")
    }

    val language: Language
        get() =
            params.languageRegistry.getLanguageByFullName(langName)
                ?: run {
                    val langNames = params.languageRegistry.commaSeparatedList { it.name }
                    throw AssertionError("'$langName' is not a supported language (available $langNames)")
                }


    val defaultVersion: LanguageVersion
        get() = getVersion(params.defaultVerString)


    protected abstract fun clone(params: Params): Self

    @JvmOverloads
    fun withProcessing(doProcess: Boolean = true): Self =
        clone(params.copy(doProcess = doProcess))

    /**
     * Returns an instance of [Self] for which all parsing methods
     * default their language version to the provided [version]
     * If the [version] is null, then the default language version
     * defined by the language module is used instead.
     */
    fun withDefaultVersion(version: String?): Self =
        clone(params.copy(defaultVerString = version))

    /**
     * Returns an instance of [Self] for which [parseResource] uses
     * the provided [contextClass] and [resourcePrefix] to load resources.
     */
    @JvmOverloads
    fun withResourceContext(contextClass: Class<*>, resourcePrefix: String = ""): Self =
            clone(params.copy(resourceLoader = contextClass, resourcePrefix = resourcePrefix))


    fun withSuppressMarker(marker: String): Self =
            clone(params.copy(suppressMarker = marker))


    @JvmOverloads
    fun <R : Node> getNodes(target: Class<R>, source: String, version: String? = null): List<R> =
                parse(source, version).descendants(target).crossFindBoundaries(true).toList()

    /**
     * Parses the [sourceCode] with the given [version]. This may execute
     * additional processing passes if this instance is configured to do
     * so.
     */
    @JvmOverloads
    fun parse(
        sourceCode: String,
        version: String? = null,
        fileName: String = TextFile.UNKNOWN_FILENAME
    ): T {
        val lversion = if (version == null) defaultVersion else getVersion(version)
        val params = params.copy(defaultVerString = lversion.version)
        val textDoc = TextDocument.readOnlyString(sourceCode, fileName, lversion)
        return loadLanguages(params).use { reg ->
            val task = ParserTask(textDoc, SemanticErrorReporter.noop(), reg)
            doParse(reg.getProcessor(language), params, task)
        }
    }

    // override if lang has dependencies
    // todo maybe do that automatically
    protected open fun loadLanguages(params: Params): LanguageProcessorRegistry =
        LanguageProcessorRegistry.singleton(newProcessor(params))


    protected open fun doParse(processor: LanguageProcessor, params: Params, task: ParserTask): T {
        val root = parseImpl(params, processor, task)
        return rootClass.cast(root)
    }

    @JvmOverloads
    fun newProcessor(params: Params = this.params): LanguageProcessor {
        val props = language.newPropertyBundle().apply {
            setLanguageVersion(params.defaultVerString ?: defaultVersion.version)
            setProperty(LanguagePropertyBundle.SUPPRESS_MARKER, params.suppressMarker)
        }
        return language.createProcessor(props)
    }

    protected open fun parseImpl(params: Params, processor: LanguageProcessor, task: ParserTask): RootNode =
        processor.services().parser.parse(task)

    /**
     * Fetches and [parse]s the [resource] using the context defined for this
     * instance (by default uses this class' classloader, but can be configured
     * with [withResourceContext]).
     */
    @JvmOverloads
    open fun parseResource(resource: String, version: String? = null): T =
        parse(readResource(resource), version, fileName = resource)

    /**
     * Fetches and [parse]s the [path].
     */
    @JvmOverloads
    open fun parseFile(path: Path, version: String? = null): T =
            parse(IOUtil.readToString(Files.newBufferedReader(path)), version, fileName = path.toAbsolutePath().toString())

    /**
     * Fetches the source of the given [clazz].
     */
    @JvmOverloads
    open fun parseClass(clazz: Class<*>, version: String? = null): T =
            parse(readClassSource(clazz), version)

    fun readResource(resourceName: String): String {

        val input = resourceLoader.getResourceAsStream(params.resourcePrefix + resourceName)
                ?: throw IllegalArgumentException("Unable to find resource file ${params.resourcePrefix + resourceName} from $resourceLoader")

        return consume(input)
    }

    private fun consume(input: InputStream) =
            IOUtil.readToString(input, StandardCharsets.UTF_8)
                    .replace(Regex("\\R"), "\n")  // normalize line-endings

    /**
     * Gets the source from the source file in which the class was declared.
     * Returns the source of the whole file even it it is not a top-level type.
     *
     * @param clazz Class to find the source for
     *
     * @return The source
     *
     * @throws IllegalArgumentException if the source file wasn't found
     */
    fun readClassSource(clazz: Class<*>): String {
        var sourceFile = clazz.name.replace('.', '/') + ".java"
        // Consider nested classes
        if (clazz.name.contains("$")) {
            sourceFile = sourceFile.substring(0, clazz.name.indexOf('$')) + ".java"
        }
        val input = (params.resourceLoader ?: javaClass).classLoader.getResourceAsStream(sourceFile)
                ?: throw IllegalArgumentException("Unable to find source file $sourceFile for $clazz")

        return consume(input)
    }

    @JvmOverloads
    fun newXpathRule(expr: String, version: XPathVersion = XPathVersion.DEFAULT) =
        XPathRule(version, expr).apply {
            language = this@BaseParsingHelper.language
            message = "XPath Rule Failed"
        }

    /**
     * Execute the given [rule] on the [code]. Produce a report with the violations
     * found by the rule. The language version of the piece of code is determined by the [params].
     */
    @JvmOverloads
    fun executeRule(
        rule: Rule,
        code: String,
        fileName: String = "testfile.${language.extensions[0]}"
    ): Report {
        if (rule.language == null)
            rule.language = language

        val config = PMDConfiguration().apply {
            suppressMarker = params.suppressMarker
            forceLanguageVersion = defaultVersion
            isIgnoreIncrementalAnalysis = true
            threads = 1
        }

        return PmdAnalysis.create(config).use { pmd ->
            pmd.addListener(GlobalAnalysisListener.exceptionThrower())
            pmd.addRuleSet(RuleSet.forSingleRule(rule))
            pmd.files().addSourceFile(fileName, code)
            pmd.performAnalysisAndCollectReport()
        }
    }

    fun executeRuleOnResource(rule: Rule, resourcePath: String): Report =
        executeRule(rule, code = readResource(resourcePath))

    fun executeRuleOnFile(rule: Rule, path: Path): Report =
        executeRule(
            rule,
            code = Files.newBufferedReader(path).readText(),
            fileName = path.toString()
        )
}
