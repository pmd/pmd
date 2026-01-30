document.addEventListener("DOMContentLoaded", (event) => {

let pmd_doc_search_index = [
{
  "type": "page",
  "source": "index.md",
  "title": "Documentation Index",
  "tags": "",
  "keywords": "java",
  "url": "index.html",
  "summary": "Welcome to the documentation site for PMD and CPD! <br/><br/>"
},{
  "type": "page",
  "source": "pages/license.md",
  "title": "License",
  "tags": "",
  "keywords": "",
  "url": "license.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/about/help.md",
  "title": "Getting Help",
  "tags": "",
  "keywords": "",
  "url": "pmd_about_help.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/about/release_policies.md",
  "title": "Release schedule and version policies",
  "tags": "",
  "keywords": "",
  "url": "pmd_about_release_policies.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/about/security.md",
  "title": "PMD Security",
  "tags": "",
  "keywords": "",
  "url": "pmd_about_security.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/about/support_lifecycle.md",
  "title": "Support lifecycle",
  "tags": "",
  "keywords": "",
  "url": "pmd_about_support_lifecycle.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/building/building_eclipse.md",
  "title": "Building PMD with Eclipse",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_building_eclipse.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/building/building_from_source.md",
  "title": "Building PMD from source",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_building.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/building/building_general.md",
  "title": "Building PMD General Info",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_building_general.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/building/building_intellij.md",
  "title": "Building PMD with IntelliJ IDEA",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_building_intellij.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/building/building_netbeans.md",
  "title": "Building PMD with Netbeans",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_building_netbeans.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/building/building_vscode.md",
  "title": "Building PMD with VS Code",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_building_vscode.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/contributing/contributing.md",
  "title": "Contributor&#39;s Guide",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_contributing.html",
  "summary": "How to contribute to PMD"
},{
  "type": "page",
  "source": "pages/pmd/devdocs/contributing/development.md",
  "title": "Developer Resources",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_development.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/contributing/newcomers_guide.md",
  "title": "Newcomers&#39; Guide",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_contributing_newcomers_guide.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/contributing/writing_documentation.md",
  "title": "Writing documentation",
  "tags": "devdocs",
  "keywords": "documentation, jekyll, markdown",
  "url": "pmd_devdocs_writing_documentation.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/github_actions_workflows.md",
  "title": "GitHub Actions Workflows",
  "tags": "",
  "keywords": "",
  "url": "pmd_devdocs_github_actions_workflows.html",
  "summary": "PMD uses GitHub Actions as the CI/CD infrastructure to build and release new versions.\nThis page gives an overview of how these workflows work and how to use them."
},{
  "type": "page",
  "source": "pages/pmd/devdocs/how_pmd_works.md",
  "title": "How PMD Works",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_how_pmd_works.html",
  "summary": "Processing overview of the different steps taken by PMD."
},{
  "type": "page",
  "source": "pages/pmd/devdocs/logging.md",
  "title": "Logging",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_logging.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/major_contributions/adding_a_dialect.md",
  "title": "Adding PMD support for a new dialect for an already existing language",
  "tags": "devdocsextendingexperimental",
  "keywords": "",
  "url": "pmd_devdocs_major_adding_dialect.html",
  "summary": "How to add a new dialect."
},{
  "type": "page",
  "source": "pages/pmd/devdocs/major_contributions/adding_a_new_antlr_based_language.md",
  "title": "Adding PMD support for a new ANTLR grammar based language",
  "tags": "devdocsextending",
  "keywords": "",
  "url": "pmd_devdocs_major_adding_new_language_antlr.html",
  "summary": "How to add a new language to PMD using ANTLR grammar."
},{
  "type": "page",
  "source": "pages/pmd/devdocs/major_contributions/adding_a_new_javacc_based_language.md",
  "title": "Adding PMD support for a new JavaCC grammar based language",
  "tags": "devdocsextending",
  "keywords": "",
  "url": "pmd_devdocs_major_adding_new_language_javacc.html",
  "summary": "How to add a new language to PMD using JavaCC grammar."
},{
  "type": "page",
  "source": "pages/pmd/devdocs/major_contributions/adding_new_cpd_language.md",
  "title": "How to add a new CPD language",
  "tags": "devdocsextending",
  "keywords": "",
  "url": "pmd_devdocs_major_adding_new_cpd_language.html",
  "summary": "How to add a new language module with CPD support."
},{
  "type": "page",
  "source": "pages/pmd/devdocs/major_contributions/rule_guidelines.md",
  "title": "Guidelines for standard rules",
  "tags": "devdocsextending",
  "keywords": "",
  "url": "pmd_devdocs_major_rule_guidelines.html",
  "summary": "Guidelines for rules that are included in the standard distribution"
},{
  "type": "page",
  "source": "pages/pmd/devdocs/pmdtester.md",
  "title": "Pmdtester",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_pmdtester.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/roadmap.md",
  "title": "Roadmap",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_roadmap.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/devdocs/rule_deprecation.md",
  "title": "Rule deprecation policy",
  "tags": "devdocs",
  "keywords": "",
  "url": "pmd_devdocs_rule_deprecation_policy.html",
  "summary": "Describes when and how rules are deprecated"
},{
  "type": "page",
  "source": "pages/pmd/languages/apex.md",
  "title": "Apex support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_apex.html",
  "summary": "Apex-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/coco.md",
  "title": "Coco support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_coco.html",
  "summary": "Coco features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/cpp.md",
  "title": "C/C++ support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_cpp.html",
  "summary": "C/C++ features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/cs.md",
  "title": "C# support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_cs.html",
  "summary": "C#-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/css.md",
  "title": "CSS support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_css.html",
  "summary": "CSS-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/dart.md",
  "title": "Dart support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_dart.html",
  "summary": "Dart-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/fortran.md",
  "title": "Fortran support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_fortran.html",
  "summary": "Fortran features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/gherkin.md",
  "title": "Gherkin support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_gherkin.html",
  "summary": "Gherkin features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/go.md",
  "title": "Go support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_go.html",
  "summary": "Go features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/groovy.md",
  "title": "Groovy support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_groovy.html",
  "summary": "Groovy-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/html.md",
  "title": "HTML support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_html.html",
  "summary": "HTML-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/index.md",
  "title": "Overview",
  "tags": "languages",
  "keywords": "",
  "url": "pmd_languages_index.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/languages/java.md",
  "title": "Java support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "auxclasspathauxiliaryclasspathtype resolution",
  "url": "pmd_languages_java.html",
  "summary": "Java-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/js_ts.md",
  "title": "JavaScript and TypeScript support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_js_ts.html",
  "summary": "JavaScript- and TypeScript-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/jsp.md",
  "title": "JSP Support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_jsp.html",
  "summary": "JSP-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/julia.md",
  "title": "Julia support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_julia.html",
  "summary": "Julia-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/kotlin.md",
  "title": "Kotlin Support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_kotlin.html",
  "summary": "Kotlin-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/language_properties.md",
  "title": "Language configuration",
  "tags": "languages",
  "keywords": "pmdcpdoptionscommandauxclasspathlanguageproperties",
  "url": "pmd_languages_configuration.html",
  "summary": "Summary of language configuration options and properties"
},{
  "type": "page",
  "source": "pages/pmd/languages/lua.md",
  "title": "Lua support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_lua.html",
  "summary": "Lua-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/matlab.md",
  "title": "Matlab support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_matlab.html",
  "summary": "Matlab-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/modelica.md",
  "title": "Modelica support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_modelica.html",
  "summary": "Modelica-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/objectivec.md",
  "title": "Objective-C support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_objectivec.html",
  "summary": "Objective-C-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/perl.md",
  "title": "Perl support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_perl.html",
  "summary": "Perl-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/php.md",
  "title": "PHP support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_php.html",
  "summary": "PHP-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/plsql.md",
  "title": "PL/SQL Support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_plsql.html",
  "summary": "PL/SQL-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/python.md",
  "title": "Python support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_python.html",
  "summary": "Python-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/ruby.md",
  "title": "Ruby support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_ruby.html",
  "summary": "Ruby-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/rust.md",
  "title": "Rust",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_rust.html",
  "summary": "Rust features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/scala.md",
  "title": "Scala support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_scala.html",
  "summary": "Scala-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/swift.md",
  "title": "Swift support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_swift.html",
  "summary": "Swift-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/tsql.md",
  "title": "T-SQL support",
  "tags": "languagesCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_tsql.html",
  "summary": "T-SQL-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/velocity.md",
  "title": "Velocity Template Language (VTL) support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_velocity.html",
  "summary": "VTL-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/visualforce.md",
  "title": "Visualforce Support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_visualforce.html",
  "summary": "Visualforce-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/languages/xml.md",
  "title": "XML support",
  "tags": "languagesPmdCapableLanguageCpdCapableLanguage",
  "keywords": "",
  "url": "pmd_languages_xml.html",
  "summary": "XML-specific features and guidance"
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/committers/infrastructure.md",
  "title": "Infrastructure",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_committers_infrastructure.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/committers/main_landing_page.md",
  "title": "Main Landing Page",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_committers_main_landing_page.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/committers/merging_pull_requests.md",
  "title": "Merging pull requests",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_committers_merging_pull_requests.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/committers/releasing.md",
  "title": "Release process",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_committers_releasing.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/credits.md",
  "title": "Credits",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_credits.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/decisions.md",
  "title": "Architecture Decisions",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_decisions.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/decisions/adr-1.md",
  "title": "ADR 1 - Use architecture decision records",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_decisions_adr_1.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/decisions/adr-2.md",
  "title": "ADR 2 - Policy on the use of Kotlin for development",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_decisions_adr_2.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/decisions/adr-3.md",
  "title": "ADR 3 - API evolution principles",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_decisions_adr_3.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/decisions/adr-NNN.md",
  "title": "ADR NNN - Template",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_decisions_adr_NNN.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/faq.md",
  "title": "FAQ",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_faq.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/logo.md",
  "title": "Logo",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_logo.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/trivia/meaning.md",
  "title": "What does &#39;PMD&#39; mean?",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_trivia_meaning.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/trivia/news.md",
  "title": "PMD in the press",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_trivia_news.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/trivia/products.md",
  "title": "Products/books related to PMD",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_trivia_products.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/projectdocs/trivia/similarprojects.md",
  "title": "Similar projects",
  "tags": "",
  "keywords": "",
  "url": "pmd_projectdocs_trivia_similarprojects.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/rules/apex.md",
  "title": "Apex Rules",
  "tags": "rule_referencesapex",
  "keywords": "",
  "url": "pmd_rules_apex.html",
  "summary": "Index of all built-in rules available for Apex"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "Best Practices (Apex, Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_apex_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "ApexAssertionsShouldIncludeMessage (Apex, Best Practices)",
  "tags": "",
  "keywords": "Apex Assertions Should Include Message",
  "url": "pmd_rules_apex_bestpractices.html#apexassertionsshouldincludemessage",
  "summary": "The second parameter of System.assert/third parameter of System.assertEquals/System.assertNotEquals is a message. Having a second/third parameter..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "ApexUnitTestClassShouldHaveAsserts (Apex, Best Practices)",
  "tags": "",
  "keywords": "Apex Unit Test Class Should Have Asserts",
  "url": "pmd_rules_apex_bestpractices.html#apexunittestclassshouldhaveasserts",
  "summary": "Apex unit tests should include at least one assertion. This makes the tests more robust,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "ApexUnitTestClassShouldHaveRunAs (Apex, Best Practices)",
  "tags": "",
  "keywords": "Apex Unit Test Class Should Have Run As",
  "url": "pmd_rules_apex_bestpractices.html#apexunittestclassshouldhaverunas",
  "summary": "Apex unit tests should include at least one runAs method. This makes the tests more..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "ApexUnitTestMethodShouldHaveIsTestAnnotation (Apex, Best Practices)",
  "tags": "",
  "keywords": "Apex Unit Test Method Should Have Is Test Annotation",
  "url": "pmd_rules_apex_bestpractices.html#apexunittestmethodshouldhaveistestannotation",
  "summary": "Apex test methods should have `@isTest` annotation instead of the `testMethod` keyword, as `testMethod` is..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "ApexUnitTestShouldNotUseSeeAllDataTrue (Apex, Best Practices)",
  "tags": "",
  "keywords": "Apex Unit Test Should Not Use See All Data True",
  "url": "pmd_rules_apex_bestpractices.html#apexunittestshouldnotuseseealldatatrue",
  "summary": "Apex unit tests should not use @isTest(seeAllData=true) because it opens up the existing database data..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "AvoidFutureAnnotation (Apex, Best Practices)",
  "tags": "",
  "keywords": "Avoid Future Annotation",
  "url": "pmd_rules_apex_bestpractices.html#avoidfutureannotation",
  "summary": "Usage of the `@Future` annotation should be limited. The `@Future` annotation is a legacy way..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "AvoidGlobalModifier (Apex, Best Practices)",
  "tags": "",
  "keywords": "Avoid Global Modifier",
  "url": "pmd_rules_apex_bestpractices.html#avoidglobalmodifier",
  "summary": "Global classes should be avoided (especially in managed packages) as they can never be deleted..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "AvoidLogicInTrigger (Apex, Best Practices)",
  "tags": "",
  "keywords": "Avoid Logic In Trigger",
  "url": "pmd_rules_apex_bestpractices.html#avoidlogicintrigger",
  "summary": "As triggers do not allow methods like regular classes they are less flexible and suited..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "DebugsShouldUseLoggingLevel (Apex, Best Practices)",
  "tags": "",
  "keywords": "Debugs Should Use Logging Level",
  "url": "pmd_rules_apex_bestpractices.html#debugsshoulduselogginglevel",
  "summary": "The first parameter of System.debug, when using the signature with two parameters, is a LoggingLevel..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "QueueableWithoutFinalizer (Apex, Best Practices)",
  "tags": "",
  "keywords": "Queueable Without Finalizer",
  "url": "pmd_rules_apex_bestpractices.html#queueablewithoutfinalizer",
  "summary": "Detects when the Queueable interface is used but a Finalizer is not attached. It is..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/bestpractices.md",
  "title": "UnusedLocalVariable (Apex, Best Practices)",
  "tags": "",
  "keywords": "Unused Local Variable",
  "url": "pmd_rules_apex_bestpractices.html#unusedlocalvariable",
  "summary": "Detects when a local variable is declared and/or assigned but not used."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "Code Style (Apex, Code Style)",
  "tags": "",
  "keywords": "Code Style",
  "url": "pmd_rules_apex_codestyle.html",
  "summary": "Rules which enforce a specific coding style."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "AnnotationsNamingConventions (Apex, Code Style)",
  "tags": "",
  "keywords": "Annotations Naming Conventions",
  "url": "pmd_rules_apex_codestyle.html#annotationsnamingconventions",
  "summary": "Apex, while case-insensitive, benefits from a consistent code style to improve readability and maintainability. Enforcing..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "ClassNamingConventions (Apex, Code Style)",
  "tags": "",
  "keywords": "Class Naming Conventions",
  "url": "pmd_rules_apex_codestyle.html#classnamingconventions",
  "summary": "Configurable naming conventions for type declarations. This rule reports type declarations which do not match..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "FieldDeclarationsShouldBeAtStart (Apex, Code Style)",
  "tags": "",
  "keywords": "Field Declarations Should Be At Start",
  "url": "pmd_rules_apex_codestyle.html#fielddeclarationsshouldbeatstart",
  "summary": "Field declarations should appear before method declarations within a class. Note: Since PMD 7.21.0, properties..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "FieldNamingConventions (Apex, Code Style)",
  "tags": "",
  "keywords": "Field Naming Conventions",
  "url": "pmd_rules_apex_codestyle.html#fieldnamingconventions",
  "summary": "Configurable naming conventions for field declarations. This rule reports variable declarations which do not match..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "ForLoopsMustUseBraces (Apex, Code Style)",
  "tags": "",
  "keywords": "For Loops Must Use Braces",
  "url": "pmd_rules_apex_codestyle.html#forloopsmustusebraces",
  "summary": "Avoid using 'for' statements without using surrounding braces. If the code formatting or indentation is..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "FormalParameterNamingConventions (Apex, Code Style)",
  "tags": "",
  "keywords": "Formal Parameter Naming Conventions",
  "url": "pmd_rules_apex_codestyle.html#formalparameternamingconventions",
  "summary": "Configurable naming conventions for formal parameters of methods. This rule reports formal parameters which do..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "IfElseStmtsMustUseBraces (Apex, Code Style)",
  "tags": "",
  "keywords": "If Else Stmts Must Use Braces",
  "url": "pmd_rules_apex_codestyle.html#ifelsestmtsmustusebraces",
  "summary": "Avoid using if..else statements without using surrounding braces. If the code formatting or indentation is..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "IfStmtsMustUseBraces (Apex, Code Style)",
  "tags": "",
  "keywords": "If Stmts Must Use Braces",
  "url": "pmd_rules_apex_codestyle.html#ifstmtsmustusebraces",
  "summary": "Avoid using if statements without using braces to surround the code block. If the code..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "LocalVariableNamingConventions (Apex, Code Style)",
  "tags": "",
  "keywords": "Local Variable Naming Conventions",
  "url": "pmd_rules_apex_codestyle.html#localvariablenamingconventions",
  "summary": "Configurable naming conventions for local variable declarations. This rule reports variable declarations which do not..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "MethodNamingConventions (Apex, Code Style)",
  "tags": "",
  "keywords": "Method Naming Conventions",
  "url": "pmd_rules_apex_codestyle.html#methodnamingconventions",
  "summary": "Configurable naming conventions for method declarations. This rule reports method declarations which do not match..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "OneDeclarationPerLine (Apex, Code Style)",
  "tags": "",
  "keywords": "One Declaration Per Line",
  "url": "pmd_rules_apex_codestyle.html#onedeclarationperline",
  "summary": "Apex allows the use of several variables declaration of the same type on one line...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "PropertyNamingConventions (Apex, Code Style)",
  "tags": "",
  "keywords": "Property Naming Conventions",
  "url": "pmd_rules_apex_codestyle.html#propertynamingconventions",
  "summary": "Configurable naming conventions for property declarations. This rule reports property declarations which do not match..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/codestyle.md",
  "title": "WhileLoopsMustUseBraces (Apex, Code Style)",
  "tags": "",
  "keywords": "While Loops Must Use Braces",
  "url": "pmd_rules_apex_codestyle.html#whileloopsmustusebraces",
  "summary": "Avoid using 'while' statements without using braces to surround the code block. If the code..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "Design (Apex, Design)",
  "tags": "",
  "keywords": "Design",
  "url": "pmd_rules_apex_design.html",
  "summary": "Rules that help you discover design issues."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "AvoidBooleanMethodParameters (Apex, Design)",
  "tags": "",
  "keywords": "Avoid Boolean Method Parameters",
  "url": "pmd_rules_apex_design.html#avoidbooleanmethodparameters",
  "summary": "Boolean parameters in a system's API can make method calls difficult to understand and maintain...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "AvoidDeeplyNestedIfStmts (Apex, Design)",
  "tags": "",
  "keywords": "Avoid Deeply Nested If Stmts",
  "url": "pmd_rules_apex_design.html#avoiddeeplynestedifstmts",
  "summary": "Avoid creating deeply nested if-then statements since they are harder to read and error-prone to..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "CognitiveComplexity (Apex, Design)",
  "tags": "",
  "keywords": "Cognitive Complexity",
  "url": "pmd_rules_apex_design.html#cognitivecomplexity",
  "summary": "Methods that are highly complex are difficult to read and more costly to maintain. If..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "CyclomaticComplexity (Apex, Design)",
  "tags": "",
  "keywords": "Cyclomatic Complexity",
  "url": "pmd_rules_apex_design.html#cyclomaticcomplexity",
  "summary": "The complexity of methods directly affects maintenance costs and readability. Concentrating too much decisional logic..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "ExcessiveClassLength (Apex, Design)",
  "tags": "",
  "keywords": "Excessive Class Length",
  "url": "pmd_rules_apex_design.html#excessiveclasslength",
  "summary": "Excessive class file lengths are usually indications that the class may be burdened with excessive..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "ExcessiveParameterList (Apex, Design)",
  "tags": "",
  "keywords": "Excessive Parameter List",
  "url": "pmd_rules_apex_design.html#excessiveparameterlist",
  "summary": "Methods with numerous parameters are a challenge to maintain, especially if most of them share..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "ExcessivePublicCount (Apex, Design)",
  "tags": "",
  "keywords": "Excessive Public Count",
  "url": "pmd_rules_apex_design.html#excessivepubliccount",
  "summary": "Classes with large numbers of public methods, attributes, and properties require disproportionate testing efforts since..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "NcssConstructorCount (Apex, Design)",
  "tags": "",
  "keywords": "Ncss Constructor Count",
  "url": "pmd_rules_apex_design.html#ncssconstructorcount",
  "summary": "This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "NcssCount (Apex, Design)",
  "tags": "",
  "keywords": "Ncss Count",
  "url": "pmd_rules_apex_design.html#ncsscount",
  "summary": "This rule uses the NCSS (Non-Commenting Source Statements) metric to determine the number of lines..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "NcssMethodCount (Apex, Design)",
  "tags": "",
  "keywords": "Ncss Method Count",
  "url": "pmd_rules_apex_design.html#ncssmethodcount",
  "summary": "This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "NcssTypeCount (Apex, Design)",
  "tags": "",
  "keywords": "Ncss Type Count",
  "url": "pmd_rules_apex_design.html#ncsstypecount",
  "summary": "This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "StdCyclomaticComplexity (Apex, Design)",
  "tags": "",
  "keywords": "Std Cyclomatic Complexity",
  "url": "pmd_rules_apex_design.html#stdcyclomaticcomplexity",
  "summary": "Complexity directly affects maintenance costs is determined by the number of decision points in a..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "TooManyFields (Apex, Design)",
  "tags": "",
  "keywords": "Too Many Fields",
  "url": "pmd_rules_apex_design.html#toomanyfields",
  "summary": "Classes that have too many fields can become unwieldy and could be redesigned to have..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/design.md",
  "title": "UnusedMethod (Apex, Design)",
  "tags": "",
  "keywords": "Unused Method",
  "url": "pmd_rules_apex_design.html#unusedmethod",
  "summary": "Avoid having unused methods since they make understanding and maintaining code harder. This rule finds..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/apex/documentation.md",
  "title": "Documentation (Apex, Documentation)",
  "tags": "",
  "keywords": "Documentation",
  "url": "pmd_rules_apex_documentation.html",
  "summary": "Rules that are related to code documentation."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/documentation.md",
  "title": "ApexDoc (Apex, Documentation)",
  "tags": "",
  "keywords": "Apex Doc",
  "url": "pmd_rules_apex_documentation.html#apexdoc",
  "summary": "This rule validates that: * ApexDoc comments are present for classes, methods, and properties that..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "Error Prone (Apex, Error Prone)",
  "tags": "",
  "keywords": "Error Prone",
  "url": "pmd_rules_apex_errorprone.html",
  "summary": "Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "ApexCSRF (Apex, Error Prone)",
  "tags": "",
  "keywords": "ApexCSRF",
  "url": "pmd_rules_apex_errorprone.html#apexcsrf",
  "summary": "Having DML operations in Apex class constructor or initializers can have unexpected side effects: By..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "AvoidDirectAccessTriggerMap (Apex, Error Prone)",
  "tags": "",
  "keywords": "Avoid Direct Access Trigger Map",
  "url": "pmd_rules_apex_errorprone.html#avoiddirectaccesstriggermap",
  "summary": "Avoid directly accessing Trigger.old and Trigger.new as it can lead to a bug. Triggers should..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "AvoidHardcodingId (Apex, Error Prone)",
  "tags": "",
  "keywords": "Avoid Hardcoding Id",
  "url": "pmd_rules_apex_errorprone.html#avoidhardcodingid",
  "summary": "When deploying Apex code between sandbox and production environments, or installing Force.com AppExchange packages, it..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "AvoidNonExistentAnnotations (Apex, Error Prone)",
  "tags": "",
  "keywords": "Avoid Non Existent Annotations",
  "url": "pmd_rules_apex_errorprone.html#avoidnonexistentannotations",
  "summary": "Apex supported non existent annotations for legacy reasons. In the future, use of such non-existent..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "AvoidStatefulDatabaseResult (Apex, Error Prone)",
  "tags": "",
  "keywords": "Avoid Stateful Database Result",
  "url": "pmd_rules_apex_errorprone.html#avoidstatefuldatabaseresult",
  "summary": "Using instance variables of the following types (or collections of these types) within a stateful..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "EmptyCatchBlock (Apex, Error Prone)",
  "tags": "",
  "keywords": "Empty Catch Block",
  "url": "pmd_rules_apex_errorprone.html#emptycatchblock",
  "summary": "Empty Catch Block finds instances where an exception is caught, but nothing is done. In..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "EmptyIfStmt (Apex, Error Prone)",
  "tags": "",
  "keywords": "Empty If Stmt",
  "url": "pmd_rules_apex_errorprone.html#emptyifstmt",
  "summary": "Empty If Statement finds instances where a condition is checked but nothing is done about..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "EmptyStatementBlock (Apex, Error Prone)",
  "tags": "",
  "keywords": "Empty Statement Block",
  "url": "pmd_rules_apex_errorprone.html#emptystatementblock",
  "summary": "Empty block statements serve no purpose and should be removed."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "EmptyTryOrFinallyBlock (Apex, Error Prone)",
  "tags": "",
  "keywords": "Empty Try Or Finally Block",
  "url": "pmd_rules_apex_errorprone.html#emptytryorfinallyblock",
  "summary": "Avoid empty try or finally blocks - what's the point?"
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "EmptyWhileStmt (Apex, Error Prone)",
  "tags": "",
  "keywords": "Empty While Stmt",
  "url": "pmd_rules_apex_errorprone.html#emptywhilestmt",
  "summary": "Empty While Statement finds all instances where a while statement does nothing. If it is..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "InaccessibleAuraEnabledGetter (Apex, Error Prone)",
  "tags": "",
  "keywords": "Inaccessible Aura Enabled Getter",
  "url": "pmd_rules_apex_errorprone.html#inaccessibleauraenabledgetter",
  "summary": "In the Summer '21 release, a mandatory security update enforces access modifiers on Apex properties..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "MethodWithSameNameAsEnclosingClass (Apex, Error Prone)",
  "tags": "",
  "keywords": "Method With Same Name As Enclosing Class",
  "url": "pmd_rules_apex_errorprone.html#methodwithsamenameasenclosingclass",
  "summary": "Non-constructor methods should not have the same name as the enclosing class."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "OverrideBothEqualsAndHashcode (Apex, Error Prone)",
  "tags": "",
  "keywords": "Override Both Equals And Hashcode",
  "url": "pmd_rules_apex_errorprone.html#overridebothequalsandhashcode",
  "summary": "Override both `public Boolean equals(Object obj)`, and `public Integer hashCode()`, or override neither. Even if..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "TestMethodsMustBeInTestClasses (Apex, Error Prone)",
  "tags": "",
  "keywords": "Test Methods Must Be In Test Classes",
  "url": "pmd_rules_apex_errorprone.html#testmethodsmustbeintestclasses",
  "summary": "Test methods marked as a testMethod or annotated with @IsTest, but not residing in a..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/errorprone.md",
  "title": "TypeShadowsBuiltInNamespace (Apex, Error Prone)",
  "tags": "",
  "keywords": "Type Shadows Built In Namespace",
  "url": "pmd_rules_apex_errorprone.html#typeshadowsbuiltinnamespace",
  "summary": "This rule finds Apex classes, enums, and interfaces that have the same name as a..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/apex/performance.md",
  "title": "Performance (Apex, Performance)",
  "tags": "",
  "keywords": "Performance",
  "url": "pmd_rules_apex_performance.html",
  "summary": "Rules that flag suboptimal code."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/performance.md",
  "title": "AvoidDebugStatements (Apex, Performance)",
  "tags": "",
  "keywords": "Avoid Debug Statements",
  "url": "pmd_rules_apex_performance.html#avoiddebugstatements",
  "summary": "Debug statements contribute to longer transactions and consume Apex CPU time even when debug logs..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/performance.md",
  "title": "AvoidNonRestrictiveQueries (Apex, Performance)",
  "tags": "",
  "keywords": "Avoid Non Restrictive Queries",
  "url": "pmd_rules_apex_performance.html#avoidnonrestrictivequeries",
  "summary": "When working with very large amounts of data, unfiltered SOQL or SOSL queries can quickly..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/performance.md",
  "title": "EagerlyLoadedDescribeSObjectResult (Apex, Performance)",
  "tags": "",
  "keywords": "Eagerly Loaded DescribeS Object Result",
  "url": "pmd_rules_apex_performance.html#eagerlyloadeddescribesobjectresult",
  "summary": "This rule finds `DescribeSObjectResult`s which could have been loaded eagerly via `SObjectType.getDescribe()`. When using `SObjectType.getDescribe()`..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/performance.md",
  "title": "OperationWithHighCostInLoop (Apex, Performance)",
  "tags": "",
  "keywords": "Operation With High Cost In Loop",
  "url": "pmd_rules_apex_performance.html#operationwithhighcostinloop",
  "summary": "This rule finds method calls inside loops that are known to be likely a performance..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/performance.md",
  "title": "OperationWithLimitsInLoop (Apex, Performance)",
  "tags": "",
  "keywords": "Operation With Limits In Loop",
  "url": "pmd_rules_apex_performance.html#operationwithlimitsinloop",
  "summary": "Database class methods, DML operations, SOQL queries, SOSL queries, Approval class methods, Email sending, async..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "Security (Apex, Security)",
  "tags": "",
  "keywords": "Security",
  "url": "pmd_rules_apex_security.html",
  "summary": "Rules that flag potential security flaws."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "ApexBadCrypto (Apex, Security)",
  "tags": "",
  "keywords": "Apex Bad Crypto",
  "url": "pmd_rules_apex_security.html#apexbadcrypto",
  "summary": "The rule makes sure you are using randomly generated IVs and keys for `Crypto` calls...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "ApexCRUDViolation (Apex, Security)",
  "tags": "",
  "keywords": "ApexCRUD Violation",
  "url": "pmd_rules_apex_security.html#apexcrudviolation",
  "summary": "The rule validates you are checking for access permissions before a SOQL/SOSL/DML operation. Since Apex..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "ApexDangerousMethods (Apex, Security)",
  "tags": "",
  "keywords": "Apex Dangerous Methods",
  "url": "pmd_rules_apex_security.html#apexdangerousmethods",
  "summary": "Checks against calling dangerous methods. For the time being, it reports: * Against `FinancialForce`'s `Configuration.disableTriggerCRUDSecurity()`...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "ApexInsecureEndpoint (Apex, Security)",
  "tags": "",
  "keywords": "Apex Insecure Endpoint",
  "url": "pmd_rules_apex_security.html#apexinsecureendpoint",
  "summary": "Checks against accessing endpoints under plain **http**. You should always use\n**https** for security."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "ApexOpenRedirect (Apex, Security)",
  "tags": "",
  "keywords": "Apex Open Redirect",
  "url": "pmd_rules_apex_security.html#apexopenredirect",
  "summary": "Checks against redirects to user-controlled locations. This prevents attackers from redirecting users to phishing sites...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "ApexSharingViolations (Apex, Security)",
  "tags": "",
  "keywords": "Apex Sharing Violations",
  "url": "pmd_rules_apex_security.html#apexsharingviolations",
  "summary": "Detect classes declared without explicit sharing mode if DML methods are used. This forces the..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "ApexSOQLInjection (Apex, Security)",
  "tags": "",
  "keywords": "ApexSOQL Injection",
  "url": "pmd_rules_apex_security.html#apexsoqlinjection",
  "summary": "Detects the usage of untrusted / unescaped variables in DML queries."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "ApexSuggestUsingNamedCred (Apex, Security)",
  "tags": "",
  "keywords": "Apex Suggest Using Named Cred",
  "url": "pmd_rules_apex_security.html#apexsuggestusingnamedcred",
  "summary": "Detects hardcoded credentials used in requests to an endpoint. You should refrain from hardcoding credentials:..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "ApexXSSFromEscapeFalse (Apex, Security)",
  "tags": "",
  "keywords": "ApexXSS From Escape False",
  "url": "pmd_rules_apex_security.html#apexxssfromescapefalse",
  "summary": "Reports on calls to `addError` with disabled escaping. The message passed to `addError` will be..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/apex/security.md",
  "title": "ApexXSSFromURLParam (Apex, Security)",
  "tags": "",
  "keywords": "ApexXSS FromURL Param",
  "url": "pmd_rules_apex_security.html#apexxssfromurlparam",
  "summary": "Makes sure that all values obtained from URL parameters are properly escaped / sanitized to..."
},{
  "type": "page",
  "source": "pages/pmd/rules/ecmascript.md",
  "title": "JavaScript Rules",
  "tags": "rule_referencesecmascript",
  "keywords": "",
  "url": "pmd_rules_ecmascript.html",
  "summary": "Index of all built-in rules available for JavaScript"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/ecmascript/bestpractices.md",
  "title": "Best Practices (JavaScript, Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_ecmascript_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/bestpractices.md",
  "title": "AvoidWithStatement (JavaScript, Best Practices)",
  "tags": "",
  "keywords": "Avoid With Statement",
  "url": "pmd_rules_ecmascript_bestpractices.html#avoidwithstatement",
  "summary": "Avoid using with - it's bad news"
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/bestpractices.md",
  "title": "ConsistentReturn (JavaScript, Best Practices)",
  "tags": "",
  "keywords": "Consistent Return",
  "url": "pmd_rules_ecmascript_bestpractices.html#consistentreturn",
  "summary": "ECMAScript does provide for return types on functions, and therefore there is no solid rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/bestpractices.md",
  "title": "GlobalVariable (JavaScript, Best Practices)",
  "tags": "",
  "keywords": "Global Variable",
  "url": "pmd_rules_ecmascript_bestpractices.html#globalvariable",
  "summary": "This rule helps to avoid using accidentally global variables by simply missing the &quot;var&quot; declaration...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/bestpractices.md",
  "title": "ScopeForInVariable (JavaScript, Best Practices)",
  "tags": "",
  "keywords": "Scope For In Variable",
  "url": "pmd_rules_ecmascript_bestpractices.html#scopeforinvariable",
  "summary": "A for-in loop in which the variable name is not explicitly scoped to the enclosing..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/bestpractices.md",
  "title": "UseBaseWithParseInt (JavaScript, Best Practices)",
  "tags": "",
  "keywords": "Use Base With Parse Int",
  "url": "pmd_rules_ecmascript_bestpractices.html#usebasewithparseint",
  "summary": "This rule checks for usages of parseInt. While the second parameter is optional and usually..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/ecmascript/codestyle.md",
  "title": "Code Style (JavaScript, Code Style)",
  "tags": "",
  "keywords": "Code Style",
  "url": "pmd_rules_ecmascript_codestyle.html",
  "summary": "Rules which enforce a specific coding style."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/codestyle.md",
  "title": "AssignmentInOperand (JavaScript, Code Style)",
  "tags": "",
  "keywords": "Assignment In Operand",
  "url": "pmd_rules_ecmascript_codestyle.html#assignmentinoperand",
  "summary": "Avoid assignments in operands; this can make code more complicated and harder to read. This..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/codestyle.md",
  "title": "ForLoopsMustUseBraces (JavaScript, Code Style)",
  "tags": "",
  "keywords": "For Loops Must Use Braces",
  "url": "pmd_rules_ecmascript_codestyle.html#forloopsmustusebraces",
  "summary": "Avoid using 'for' statements without using curly braces."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/codestyle.md",
  "title": "IfElseStmtsMustUseBraces (JavaScript, Code Style)",
  "tags": "",
  "keywords": "If Else Stmts Must Use Braces",
  "url": "pmd_rules_ecmascript_codestyle.html#ifelsestmtsmustusebraces",
  "summary": "Avoid using if..else statements without using curly braces."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/codestyle.md",
  "title": "IfStmtsMustUseBraces (JavaScript, Code Style)",
  "tags": "",
  "keywords": "If Stmts Must Use Braces",
  "url": "pmd_rules_ecmascript_codestyle.html#ifstmtsmustusebraces",
  "summary": "Avoid using if statements without using curly braces."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/codestyle.md",
  "title": "NoElseReturn (JavaScript, Code Style)",
  "tags": "",
  "keywords": "No Else Return",
  "url": "pmd_rules_ecmascript_codestyle.html#noelsereturn",
  "summary": "The else block in a if-else-construct is unnecessary if the `if` block contains a return...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/codestyle.md",
  "title": "UnnecessaryBlock (JavaScript, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Block",
  "url": "pmd_rules_ecmascript_codestyle.html#unnecessaryblock",
  "summary": "An unnecessary Block is present. Such Blocks are often used in other languages to introduce..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/codestyle.md",
  "title": "UnnecessaryParentheses (JavaScript, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Parentheses",
  "url": "pmd_rules_ecmascript_codestyle.html#unnecessaryparentheses",
  "summary": "Unnecessary parentheses should be removed."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/codestyle.md",
  "title": "UnreachableCode (JavaScript, Code Style)",
  "tags": "",
  "keywords": "Unreachable Code",
  "url": "pmd_rules_ecmascript_codestyle.html#unreachablecode",
  "summary": "A 'return', 'break', 'continue', or 'throw' statement should be the last in a block. Statements..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/codestyle.md",
  "title": "WhileLoopsMustUseBraces (JavaScript, Code Style)",
  "tags": "",
  "keywords": "While Loops Must Use Braces",
  "url": "pmd_rules_ecmascript_codestyle.html#whileloopsmustusebraces",
  "summary": "Avoid using 'while' statements without using curly braces."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/ecmascript/errorprone.md",
  "title": "Error Prone (JavaScript, Error Prone)",
  "tags": "",
  "keywords": "Error Prone",
  "url": "pmd_rules_ecmascript_errorprone.html",
  "summary": "Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/errorprone.md",
  "title": "AvoidTrailingComma (JavaScript, Error Prone)",
  "tags": "",
  "keywords": "Avoid Trailing Comma",
  "url": "pmd_rules_ecmascript_errorprone.html#avoidtrailingcomma",
  "summary": "This rule helps improve code portability due to differences in browser treatment of trailing commas..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/errorprone.md",
  "title": "EqualComparison (JavaScript, Error Prone)",
  "tags": "",
  "keywords": "Equal Comparison",
  "url": "pmd_rules_ecmascript_errorprone.html#equalcomparison",
  "summary": "Using == in condition may lead to unexpected results, as the variables are automatically casted..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/errorprone.md",
  "title": "InaccurateNumericLiteral (JavaScript, Error Prone)",
  "tags": "",
  "keywords": "Inaccurate Numeric Literal",
  "url": "pmd_rules_ecmascript_errorprone.html#inaccuratenumericliteral",
  "summary": "The numeric literal will have a different value at runtime, which can happen if you..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/errorprone.md",
  "title": "InnaccurateNumericLiteral (JavaScript, Error Prone)",
  "tags": "",
  "keywords": "Innaccurate Numeric Literal",
  "url": "pmd_rules_ecmascript_errorprone.html#innaccuratenumericliteral",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/ecmascript/performance.md",
  "title": "Performance (JavaScript, Performance)",
  "tags": "",
  "keywords": "Performance",
  "url": "pmd_rules_ecmascript_performance.html",
  "summary": "Rules that flag suboptimal code."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/ecmascript/performance.md",
  "title": "AvoidConsoleStatements (JavaScript, Performance)",
  "tags": "",
  "keywords": "Avoid Console Statements",
  "url": "pmd_rules_ecmascript_performance.html#avoidconsolestatements",
  "summary": "Using the console for logging in production might negatively impact performance. In addition, logging could..."
},{
  "type": "page",
  "source": "pages/pmd/rules/html.md",
  "title": "HTML Rules",
  "tags": "rule_referenceshtml",
  "keywords": "",
  "url": "pmd_rules_html.html",
  "summary": "Index of all built-in rules available for HTML"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/html/bestpractices.md",
  "title": "Best Practices (HTML, Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_html_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/html/bestpractices.md",
  "title": "AvoidInlineStyles (HTML, Best Practices)",
  "tags": "",
  "keywords": "Avoid Inline Styles",
  "url": "pmd_rules_html_bestpractices.html#avoidinlinestyles",
  "summary": "Don't mix content and style. Use separate CSS-files for the style and introduce classes. This..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/html/bestpractices.md",
  "title": "UnnecessaryTypeAttribute (HTML, Best Practices)",
  "tags": "",
  "keywords": "Unnecessary Type Attribute",
  "url": "pmd_rules_html_bestpractices.html#unnecessarytypeattribute",
  "summary": "In HTML5 the explicit type attribute for link and script elements is not needed. Modern..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/html/bestpractices.md",
  "title": "UseAltAttributeForImages (HTML, Best Practices)",
  "tags": "",
  "keywords": "Use Alt Attribute For Images",
  "url": "pmd_rules_html_bestpractices.html#usealtattributeforimages",
  "summary": "Always use an &quot;alt&quot; attribute for images. This provides an alternative text and is extensively..."
},{
  "type": "page",
  "source": "pages/pmd/rules/java.md",
  "title": "Java Rules",
  "tags": "rule_referencesjava",
  "keywords": "",
  "url": "pmd_rules_java.html",
  "summary": "Index of all built-in rules available for Java"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "Best Practices (Java, Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_java_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "AbstractClassWithoutAbstractMethod (Java, Best Practices)",
  "tags": "",
  "keywords": "Abstract Class Without Abstract Method",
  "url": "pmd_rules_java_bestpractices.html#abstractclasswithoutabstractmethod",
  "summary": "The abstract class does not contain any abstract methods. An abstract class suggests an incomplete..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "AccessorClassGeneration (Java, Best Practices)",
  "tags": "",
  "keywords": "Accessor Class Generation",
  "url": "pmd_rules_java_bestpractices.html#accessorclassgeneration",
  "summary": "Instantiation by way of private constructors from outside the constructor's class often causes the generation..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "AccessorMethodGeneration (Java, Best Practices)",
  "tags": "",
  "keywords": "Accessor Method Generation",
  "url": "pmd_rules_java_bestpractices.html#accessormethodgeneration",
  "summary": "When accessing private fields / methods from another class, the Java compiler will generate accessor..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "ArrayIsStoredDirectly (Java, Best Practices)",
  "tags": "",
  "keywords": "Array Is Stored Directly",
  "url": "pmd_rules_java_bestpractices.html#arrayisstoreddirectly",
  "summary": "Constructors and methods receiving arrays should clone objects and store the copy. This prevents future..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "AvoidMessageDigestField (Java, Best Practices)",
  "tags": "",
  "keywords": "Avoid Message Digest Field",
  "url": "pmd_rules_java_bestpractices.html#avoidmessagedigestfield",
  "summary": "Declaring a MessageDigest instance as a field make this instance directly available to multiple threads...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "AvoidPrintStackTrace (Java, Best Practices)",
  "tags": "",
  "keywords": "Avoid Print Stack Trace",
  "url": "pmd_rules_java_bestpractices.html#avoidprintstacktrace",
  "summary": "Avoid printStackTrace(); use a logger call instead."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "AvoidReassigningCatchVariables (Java, Best Practices)",
  "tags": "",
  "keywords": "Avoid Reassigning Catch Variables",
  "url": "pmd_rules_java_bestpractices.html#avoidreassigningcatchvariables",
  "summary": "Reassigning exception variables caught in a catch statement should be avoided because of: 1) If..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "AvoidReassigningLoopVariables (Java, Best Practices)",
  "tags": "",
  "keywords": "Avoid Reassigning Loop Variables",
  "url": "pmd_rules_java_bestpractices.html#avoidreassigningloopvariables",
  "summary": "Reassigning loop variables can lead to hard-to-find bugs. Prevent or limit how these variables can..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "AvoidReassigningParameters (Java, Best Practices)",
  "tags": "",
  "keywords": "Avoid Reassigning Parameters",
  "url": "pmd_rules_java_bestpractices.html#avoidreassigningparameters",
  "summary": "Reassigning values to incoming parameters of a method or constructor is not recommended, as this..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "AvoidStringBufferField (Java, Best Practices)",
  "tags": "",
  "keywords": "Avoid String Buffer Field",
  "url": "pmd_rules_java_bestpractices.html#avoidstringbufferfield",
  "summary": "StringBuffers/StringBuilders can grow considerably, and so may become a source of memory leaks if held..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "AvoidUsingHardCodedIP (Java, Best Practices)",
  "tags": "",
  "keywords": "Avoid Using Hard CodedIP",
  "url": "pmd_rules_java_bestpractices.html#avoidusinghardcodedip",
  "summary": "Application with hard-coded IP addresses can become impossible to deploy in some cases. Externalizing IP..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "CheckResultSet (Java, Best Practices)",
  "tags": "",
  "keywords": "Check Result Set",
  "url": "pmd_rules_java_bestpractices.html#checkresultset",
  "summary": "Always check the return values of navigation methods (next, previous, first, last) of a ResultSet...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "ConstantsInInterface (Java, Best Practices)",
  "tags": "",
  "keywords": "Constants In Interface",
  "url": "pmd_rules_java_bestpractices.html#constantsininterface",
  "summary": "Using constants in interfaces is a bad practice. Interfaces define types, constants are implementation details..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "DefaultLabelNotLastInSwitch (Java, Best Practices)",
  "tags": "",
  "keywords": "Default Label Not Last In Switch",
  "url": "pmd_rules_java_bestpractices.html#defaultlabelnotlastinswitch",
  "summary": "By convention, the default label should be the last label in a switch statement or..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "DefaultLabelNotLastInSwitchStmt (Java, Best Practices)",
  "tags": "",
  "keywords": "Default Label Not Last In Switch Stmt",
  "url": "pmd_rules_java_bestpractices.html#defaultlabelnotlastinswitchstmt",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "DoubleBraceInitialization (Java, Best Practices)",
  "tags": "",
  "keywords": "Double Brace Initialization",
  "url": "pmd_rules_java_bestpractices.html#doublebraceinitialization",
  "summary": "Double brace initialisation is a pattern to initialise eg collections concisely. But it implicitly generates..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "EnumComparison (Java, Best Practices)",
  "tags": "",
  "keywords": "Enum Comparison",
  "url": "pmd_rules_java_bestpractices.html#enumcomparison",
  "summary": "When comparing enums, `equals()` should be avoided and `==` should be preferred. Using `==` has..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "ExhaustiveSwitchHasDefault (Java, Best Practices)",
  "tags": "",
  "keywords": "Exhaustive Switch Has Default",
  "url": "pmd_rules_java_bestpractices.html#exhaustiveswitchhasdefault",
  "summary": "When switching over an enum or sealed class, the compiler will ensure that all possible..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "ForLoopCanBeForeach (Java, Best Practices)",
  "tags": "",
  "keywords": "For Loop Can Be Foreach",
  "url": "pmd_rules_java_bestpractices.html#forloopcanbeforeach",
  "summary": "Reports loops that can be safely replaced with the foreach syntax. The rule considers loops..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "ForLoopVariableCount (Java, Best Practices)",
  "tags": "",
  "keywords": "For Loop Variable Count",
  "url": "pmd_rules_java_bestpractices.html#forloopvariablecount",
  "summary": "Having a lot of control variables in a 'for' loop makes it harder to see..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "GuardLogStatement (Java, Best Practices)",
  "tags": "",
  "keywords": "Guard Log Statement",
  "url": "pmd_rules_java_bestpractices.html#guardlogstatement",
  "summary": "Whenever using a log level, one should check if it is actually enabled, or otherwise..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "ImplicitFunctionalInterface (Java, Best Practices)",
  "tags": "",
  "keywords": "Implicit Functional Interface",
  "url": "pmd_rules_java_bestpractices.html#implicitfunctionalinterface",
  "summary": "Reports functional interfaces that were not explicitly declared as such with the annotation `@FunctionalInterface`. If..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "JUnit4SuitesShouldUseSuiteAnnotation (Java, Best Practices)",
  "tags": "",
  "keywords": "J Unit4 Suites Should Use Suite Annotation",
  "url": "pmd_rules_java_bestpractices.html#junit4suitesshouldusesuiteannotation",
  "summary": "In JUnit 3, test suites are indicated by the suite() method. In JUnit 4, suites..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "JUnit4TestShouldUseAfterAnnotation (Java, Best Practices)",
  "tags": "",
  "keywords": "J Unit4 Test Should Use After Annotation",
  "url": "pmd_rules_java_bestpractices.html#junit4testshoulduseafterannotation",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "JUnit4TestShouldUseBeforeAnnotation (Java, Best Practices)",
  "tags": "",
  "keywords": "J Unit4 Test Should Use Before Annotation",
  "url": "pmd_rules_java_bestpractices.html#junit4testshouldusebeforeannotation",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "JUnit4TestShouldUseTestAnnotation (Java, Best Practices)",
  "tags": "",
  "keywords": "J Unit4 Test Should Use Test Annotation",
  "url": "pmd_rules_java_bestpractices.html#junit4testshouldusetestannotation",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "JUnit5TestShouldBePackagePrivate (Java, Best Practices)",
  "tags": "",
  "keywords": "J Unit5 Test Should Be Package Private",
  "url": "pmd_rules_java_bestpractices.html#junit5testshouldbepackageprivate",
  "summary": "Reports JUnit 5 test classes and methods that are not package-private. Contrary to JUnit 4..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "JUnitAssertionsShouldIncludeMessage (Java, Best Practices)",
  "tags": "",
  "keywords": "J Unit Assertions Should Include Message",
  "url": "pmd_rules_java_bestpractices.html#junitassertionsshouldincludemessage",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "JUnitTestContainsTooManyAsserts (Java, Best Practices)",
  "tags": "",
  "keywords": "J Unit Test Contains Too Many Asserts",
  "url": "pmd_rules_java_bestpractices.html#junittestcontainstoomanyasserts",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "JUnitTestsShouldIncludeAssert (Java, Best Practices)",
  "tags": "",
  "keywords": "J Unit Tests Should Include Assert",
  "url": "pmd_rules_java_bestpractices.html#junittestsshouldincludeassert",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "JUnitUseExpected (Java, Best Practices)",
  "tags": "",
  "keywords": "J Unit Use Expected",
  "url": "pmd_rules_java_bestpractices.html#junituseexpected",
  "summary": "In JUnit4, use the @Test(expected) annotation to denote tests that should throw exceptions."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "LabeledStatement (Java, Best Practices)",
  "tags": "",
  "keywords": "Labeled Statement",
  "url": "pmd_rules_java_bestpractices.html#labeledstatement",
  "summary": "This rule detects the use of labeled statements. By default, it allows labeled loops so..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "LiteralsFirstInComparisons (Java, Best Practices)",
  "tags": "",
  "keywords": "Literals First In Comparisons",
  "url": "pmd_rules_java_bestpractices.html#literalsfirstincomparisons",
  "summary": "Position literals first in all String comparisons, if the second argument is null then NullPointerExceptions..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "LooseCoupling (Java, Best Practices)",
  "tags": "",
  "keywords": "Loose Coupling",
  "url": "pmd_rules_java_bestpractices.html#loosecoupling",
  "summary": "Excessive coupling to implementation types (e.g., `HashSet`) limits your ability to use alternate implementations in..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "MethodReturnsInternalArray (Java, Best Practices)",
  "tags": "",
  "keywords": "Method Returns Internal Array",
  "url": "pmd_rules_java_bestpractices.html#methodreturnsinternalarray",
  "summary": "Exposing internal arrays to the caller violates object encapsulation since elements can be removed or..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "MissingOverride (Java, Best Practices)",
  "tags": "",
  "keywords": "Missing Override",
  "url": "pmd_rules_java_bestpractices.html#missingoverride",
  "summary": "Annotating overridden methods with @Override ensures at compile time that the method really overrides one,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "NonExhaustiveSwitch (Java, Best Practices)",
  "tags": "",
  "keywords": "Non Exhaustive Switch",
  "url": "pmd_rules_java_bestpractices.html#nonexhaustiveswitch",
  "summary": "Switch statements should be exhaustive, to make their control flow easier to follow. This can..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "OneDeclarationPerLine (Java, Best Practices)",
  "tags": "",
  "keywords": "One Declaration Per Line",
  "url": "pmd_rules_java_bestpractices.html#onedeclarationperline",
  "summary": "Java allows the use of several variables declaration of the same type on one line...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "PreserveStackTrace (Java, Best Practices)",
  "tags": "",
  "keywords": "Preserve Stack Trace",
  "url": "pmd_rules_java_bestpractices.html#preservestacktrace",
  "summary": "Reports exceptions that are thrown from within a catch block, yet don't refer to the..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "PrimitiveWrapperInstantiation (Java, Best Practices)",
  "tags": "",
  "keywords": "Primitive Wrapper Instantiation",
  "url": "pmd_rules_java_bestpractices.html#primitivewrapperinstantiation",
  "summary": "Reports usages of primitive wrapper constructors. They are deprecated since Java 9 and should not..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "RelianceOnDefaultCharset (Java, Best Practices)",
  "tags": "",
  "keywords": "Reliance On Default Charset",
  "url": "pmd_rules_java_bestpractices.html#relianceondefaultcharset",
  "summary": "Be sure to specify a character set for APIs that use the JVM's default character..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "ReplaceEnumerationWithIterator (Java, Best Practices)",
  "tags": "",
  "keywords": "Replace Enumeration With Iterator",
  "url": "pmd_rules_java_bestpractices.html#replaceenumerationwithiterator",
  "summary": "Consider replacing Enumeration usages with the newer java.util.Iterator"
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "ReplaceHashtableWithMap (Java, Best Practices)",
  "tags": "",
  "keywords": "Replace Hashtable With Map",
  "url": "pmd_rules_java_bestpractices.html#replacehashtablewithmap",
  "summary": "Consider replacing Hashtable usage with the newer java.util.Map if thread safety is not required."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "ReplaceVectorWithList (Java, Best Practices)",
  "tags": "",
  "keywords": "Replace Vector With List",
  "url": "pmd_rules_java_bestpractices.html#replacevectorwithlist",
  "summary": "Consider replacing Vector usages with the newer java.util.ArrayList if expensive thread-safe operations are not required...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "SimplifiableTestAssertion (Java, Best Practices)",
  "tags": "",
  "keywords": "Simplifiable Test Assertion",
  "url": "pmd_rules_java_bestpractices.html#simplifiabletestassertion",
  "summary": "Reports test assertions that may be simplified using a more specific assertion method. This enables..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "SwitchStmtsShouldHaveDefault (Java, Best Practices)",
  "tags": "",
  "keywords": "Switch Stmts Should Have Default",
  "url": "pmd_rules_java_bestpractices.html#switchstmtsshouldhavedefault",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "SystemPrintln (Java, Best Practices)",
  "tags": "",
  "keywords": "System Println",
  "url": "pmd_rules_java_bestpractices.html#systemprintln",
  "summary": "References to System.(out|err).print are usually intended for debugging purposes and can remain in the codebase..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnitTestAssertionsShouldIncludeMessage (Java, Best Practices)",
  "tags": "",
  "keywords": "Unit Test Assertions Should Include Message",
  "url": "pmd_rules_java_bestpractices.html#unittestassertionsshouldincludemessage",
  "summary": "Unit assertions should include an informative message - i.e., use the three-argument version of `assertEquals()`,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnitTestContainsTooManyAsserts (Java, Best Practices)",
  "tags": "",
  "keywords": "Unit Test Contains Too Many Asserts",
  "url": "pmd_rules_java_bestpractices.html#unittestcontainstoomanyasserts",
  "summary": "Unit tests should not contain too many asserts. Many asserts are indicative of a complex..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnitTestShouldIncludeAssert (Java, Best Practices)",
  "tags": "",
  "keywords": "Unit Test Should Include Assert",
  "url": "pmd_rules_java_bestpractices.html#unittestshouldincludeassert",
  "summary": "Unit tests should include at least one assertion. This makes the tests more robust, and..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnitTestShouldUseAfterAnnotation (Java, Best Practices)",
  "tags": "",
  "keywords": "Unit Test Should Use After Annotation",
  "url": "pmd_rules_java_bestpractices.html#unittestshoulduseafterannotation",
  "summary": "This rule detects methods called `tearDown()` that are not properly annotated as a cleanup method...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnitTestShouldUseBeforeAnnotation (Java, Best Practices)",
  "tags": "",
  "keywords": "Unit Test Should Use Before Annotation",
  "url": "pmd_rules_java_bestpractices.html#unittestshouldusebeforeannotation",
  "summary": "This rule detects methods called `setUp()` that are not properly annotated as a setup method...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnitTestShouldUseTestAnnotation (Java, Best Practices)",
  "tags": "",
  "keywords": "Unit Test Should Use Test Annotation",
  "url": "pmd_rules_java_bestpractices.html#unittestshouldusetestannotation",
  "summary": "The rule will detect any test method starting with &quot;test&quot; that is not properly annotated,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnnecessaryVarargsArrayCreation (Java, Best Practices)",
  "tags": "",
  "keywords": "Unnecessary Varargs Array Creation",
  "url": "pmd_rules_java_bestpractices.html#unnecessaryvarargsarraycreation",
  "summary": "Reports explicit array creation when a varargs is expected. For instance: ```java Arrays.asList(new String[] {..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnnecessaryWarningSuppression (Java, Best Practices)",
  "tags": "",
  "keywords": "Unnecessary Warning Suppression",
  "url": "pmd_rules_java_bestpractices.html#unnecessarywarningsuppression",
  "summary": "This rule reports suppression comments and annotations that did not suppress any PMD violation. Note..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnusedAssignment (Java, Best Practices)",
  "tags": "",
  "keywords": "Unused Assignment",
  "url": "pmd_rules_java_bestpractices.html#unusedassignment",
  "summary": "Reports assignments to variables that are never used before the variable is overwritten, or goes..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnusedFormalParameter (Java, Best Practices)",
  "tags": "",
  "keywords": "Unused Formal Parameter",
  "url": "pmd_rules_java_bestpractices.html#unusedformalparameter",
  "summary": "Reports parameters of methods and constructors that are not referenced them in the method body...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnusedLabel (Java, Best Practices)",
  "tags": "",
  "keywords": "Unused Label",
  "url": "pmd_rules_java_bestpractices.html#unusedlabel",
  "summary": "Unused labeled are unnecessary and may be confusing as you might be wondering what this..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnusedLocalVariable (Java, Best Practices)",
  "tags": "",
  "keywords": "Unused Local Variable",
  "url": "pmd_rules_java_bestpractices.html#unusedlocalvariable",
  "summary": "Detects when a local variable is declared and/or assigned, but not used. Variables whose name..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnusedPrivateField (Java, Best Practices)",
  "tags": "",
  "keywords": "Unused Private Field",
  "url": "pmd_rules_java_bestpractices.html#unusedprivatefield",
  "summary": "Detects when a private field is declared and/or assigned a value, but not used. Since..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UnusedPrivateMethod (Java, Best Practices)",
  "tags": "",
  "keywords": "Unused Private Method",
  "url": "pmd_rules_java_bestpractices.html#unusedprivatemethod",
  "summary": "Unused Private Method detects when a private method is declared but is unused."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UseCollectionIsEmpty (Java, Best Practices)",
  "tags": "",
  "keywords": "Use Collection Is Empty",
  "url": "pmd_rules_java_bestpractices.html#usecollectionisempty",
  "summary": "The isEmpty() method on java.util.Collection is provided to determine if a collection has any elements...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UseEnumCollections (Java, Best Practices)",
  "tags": "",
  "keywords": "Use Enum Collections",
  "url": "pmd_rules_java_bestpractices.html#useenumcollections",
  "summary": "Wherever possible, use `EnumSet` or `EnumMap` instead of `HashSet` and `HashMap` when the keys are..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UseStandardCharsets (Java, Best Practices)",
  "tags": "",
  "keywords": "Use Standard Charsets",
  "url": "pmd_rules_java_bestpractices.html#usestandardcharsets",
  "summary": "Starting with Java 7, StandardCharsets provides constants for common Charset objects, such as UTF-8. Using..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UseTryWithResources (Java, Best Practices)",
  "tags": "",
  "keywords": "Use Try With Resources",
  "url": "pmd_rules_java_bestpractices.html#usetrywithresources",
  "summary": "Java 7 introduced the try-with-resources statement. This statement ensures that each resource is closed at..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "UseVarargs (Java, Best Practices)",
  "tags": "",
  "keywords": "Use Varargs",
  "url": "pmd_rules_java_bestpractices.html#usevarargs",
  "summary": "Java 5 introduced the varargs parameter declaration for methods and constructors. This syntactic sugar provides..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/bestpractices.md",
  "title": "WhileLoopWithLiteralBoolean (Java, Best Practices)",
  "tags": "",
  "keywords": "While Loop With Literal Boolean",
  "url": "pmd_rules_java_bestpractices.html#whileloopwithliteralboolean",
  "summary": "`do {} while (true);` requires reading the end of the statement before it is apparent..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "Code Style (Java, Code Style)",
  "tags": "",
  "keywords": "Code Style",
  "url": "pmd_rules_java_codestyle.html",
  "summary": "Rules which enforce a specific coding style."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "AtLeastOneConstructor (Java, Code Style)",
  "tags": "",
  "keywords": "At Least One Constructor",
  "url": "pmd_rules_java_codestyle.html#atleastoneconstructor",
  "summary": "Each non-static class should declare at least one constructor. Classes with solely static members are..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "AvoidDollarSigns (Java, Code Style)",
  "tags": "",
  "keywords": "Avoid Dollar Signs",
  "url": "pmd_rules_java_codestyle.html#avoiddollarsigns",
  "summary": "Avoid using dollar signs in variable/method/class/interface names."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "AvoidProtectedFieldInFinalClass (Java, Code Style)",
  "tags": "",
  "keywords": "Avoid Protected Field In Final Class",
  "url": "pmd_rules_java_codestyle.html#avoidprotectedfieldinfinalclass",
  "summary": "Do not use protected fields in final classes since they cannot be subclassed. Clarify your..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "AvoidProtectedMethodInFinalClassNotExtending (Java, Code Style)",
  "tags": "",
  "keywords": "Avoid Protected Method In Final Class Not Extending",
  "url": "pmd_rules_java_codestyle.html#avoidprotectedmethodinfinalclassnotextending",
  "summary": "Do not use protected methods in most final classes since they cannot be subclassed. This..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "AvoidUsingNativeCode (Java, Code Style)",
  "tags": "",
  "keywords": "Avoid Using Native Code",
  "url": "pmd_rules_java_codestyle.html#avoidusingnativecode",
  "summary": "Unnecessary reliance on Java Native Interface (JNI) calls directly reduces application portability and increases the..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "BooleanGetMethodName (Java, Code Style)",
  "tags": "",
  "keywords": "Boolean Get Method Name",
  "url": "pmd_rules_java_codestyle.html#booleangetmethodname",
  "summary": "Methods that return boolean or Boolean results should be named as predicate statements to denote..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "CallSuperInConstructor (Java, Code Style)",
  "tags": "",
  "keywords": "Call Super In Constructor",
  "url": "pmd_rules_java_codestyle.html#callsuperinconstructor",
  "summary": "It is a good practice to call super() in a constructor. If super() is not..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "ClassNamingConventions (Java, Code Style)",
  "tags": "",
  "keywords": "Class Naming Conventions",
  "url": "pmd_rules_java_codestyle.html#classnamingconventions",
  "summary": "Configurable naming conventions for type declarations. This rule reports type declarations which do not match..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "CommentDefaultAccessModifier (Java, Code Style)",
  "tags": "",
  "keywords": "Comment Default Access Modifier",
  "url": "pmd_rules_java_codestyle.html#commentdefaultaccessmodifier",
  "summary": "To avoid mistakes if we want that an Annotation, Class, Enum, Method, Constructor or Field..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "ConfusingTernary (Java, Code Style)",
  "tags": "",
  "keywords": "Confusing Ternary",
  "url": "pmd_rules_java_codestyle.html#confusingternary",
  "summary": "Avoid negation within an &quot;if&quot; expression with an &quot;else&quot; clause. For example, rephrase: `if (x..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "ControlStatementBraces (Java, Code Style)",
  "tags": "",
  "keywords": "Control Statement Braces",
  "url": "pmd_rules_java_codestyle.html#controlstatementbraces",
  "summary": "Enforce a policy for braces on control statements. It is recommended to use braces on..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "EmptyControlStatement (Java, Code Style)",
  "tags": "",
  "keywords": "Empty Control Statement",
  "url": "pmd_rules_java_codestyle.html#emptycontrolstatement",
  "summary": "Reports control statements whose body is empty, as well as empty initializers. The checked code..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "EmptyMethodInAbstractClassShouldBeAbstract (Java, Code Style)",
  "tags": "",
  "keywords": "Empty Method In Abstract Class Should Be Abstract",
  "url": "pmd_rules_java_codestyle.html#emptymethodinabstractclassshouldbeabstract",
  "summary": "Empty or auto-generated methods in an abstract class should be tagged as abstract. This helps..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "ExtendsObject (Java, Code Style)",
  "tags": "",
  "keywords": "Extends Object",
  "url": "pmd_rules_java_codestyle.html#extendsobject",
  "summary": "No need to explicitly extend Object."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "FieldDeclarationsShouldBeAtStartOfClass (Java, Code Style)",
  "tags": "",
  "keywords": "Field Declarations Should Be At Start Of Class",
  "url": "pmd_rules_java_codestyle.html#fielddeclarationsshouldbeatstartofclass",
  "summary": "Fields should be declared at the top of the class, before any method declarations, constructors,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "FieldNamingConventions (Java, Code Style)",
  "tags": "",
  "keywords": "Field Naming Conventions",
  "url": "pmd_rules_java_codestyle.html#fieldnamingconventions",
  "summary": "Configurable naming conventions for field declarations. This rule reports variable declarations which do not match..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "FinalParameterInAbstractMethod (Java, Code Style)",
  "tags": "",
  "keywords": "Final Parameter In Abstract Method",
  "url": "pmd_rules_java_codestyle.html#finalparameterinabstractmethod",
  "summary": "Declaring a method parameter as final for an interface method is useless because the implementation..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "ForLoopShouldBeWhileLoop (Java, Code Style)",
  "tags": "",
  "keywords": "For Loop Should Be While Loop",
  "url": "pmd_rules_java_codestyle.html#forloopshouldbewhileloop",
  "summary": "Some for loops can be simplified to while loops, this makes them more concise."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "FormalParameterNamingConventions (Java, Code Style)",
  "tags": "",
  "keywords": "Formal Parameter Naming Conventions",
  "url": "pmd_rules_java_codestyle.html#formalparameternamingconventions",
  "summary": "Configurable naming conventions for formal parameters of methods and lambdas. This rule reports formal parameters..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "GenericsNaming (Java, Code Style)",
  "tags": "",
  "keywords": "Generics Naming",
  "url": "pmd_rules_java_codestyle.html#genericsnaming",
  "summary": "Names for references to generic values should be limited to a single uppercase letter. **Deprecated:**..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "IdenticalCatchBranches (Java, Code Style)",
  "tags": "",
  "keywords": "Identical Catch Branches",
  "url": "pmd_rules_java_codestyle.html#identicalcatchbranches",
  "summary": "Identical `catch` branches use up vertical space and increase the complexity of code without adding..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "LambdaCanBeMethodReference (Java, Code Style)",
  "tags": "",
  "keywords": "Lambda Can Be Method Reference",
  "url": "pmd_rules_java_codestyle.html#lambdacanbemethodreference",
  "summary": "This rule reports lambda expressions that can be written more succinctly as a method reference...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "LinguisticNaming (Java, Code Style)",
  "tags": "",
  "keywords": "Linguistic Naming",
  "url": "pmd_rules_java_codestyle.html#linguisticnaming",
  "summary": "This rule finds Linguistic Naming Antipatterns. It checks for fields, that are named, as if..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "LocalHomeNamingConvention (Java, Code Style)",
  "tags": "",
  "keywords": "Local Home Naming Convention",
  "url": "pmd_rules_java_codestyle.html#localhomenamingconvention",
  "summary": "The Local Home interface of a Session EJB should be suffixed by 'LocalHome'."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "LocalInterfaceSessionNamingConvention (Java, Code Style)",
  "tags": "",
  "keywords": "Local Interface Session Naming Convention",
  "url": "pmd_rules_java_codestyle.html#localinterfacesessionnamingconvention",
  "summary": "The Local Interface of a Session EJB should be suffixed by 'Local'."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "LocalVariableCouldBeFinal (Java, Code Style)",
  "tags": "",
  "keywords": "Local Variable Could Be Final",
  "url": "pmd_rules_java_codestyle.html#localvariablecouldbefinal",
  "summary": "A local variable assigned only once can be declared final."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "LocalVariableNamingConventions (Java, Code Style)",
  "tags": "",
  "keywords": "Local Variable Naming Conventions",
  "url": "pmd_rules_java_codestyle.html#localvariablenamingconventions",
  "summary": "Configurable naming conventions for local variable declarations and other locally-scoped variables. This rule reports variable..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "LongVariable (Java, Code Style)",
  "tags": "",
  "keywords": "Long Variable",
  "url": "pmd_rules_java_codestyle.html#longvariable",
  "summary": "Fields, formal arguments, or local variable names that are too long can make the code..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "MDBAndSessionBeanNamingConvention (Java, Code Style)",
  "tags": "",
  "keywords": "MDB And Session Bean Naming Convention",
  "url": "pmd_rules_java_codestyle.html#mdbandsessionbeannamingconvention",
  "summary": "The EJB Specification states that any MessageDrivenBean or SessionBean should be suffixed by 'Bean'."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "MethodArgumentCouldBeFinal (Java, Code Style)",
  "tags": "",
  "keywords": "Method Argument Could Be Final",
  "url": "pmd_rules_java_codestyle.html#methodargumentcouldbefinal",
  "summary": "Reports method and constructor parameters that can be made final because they are never reassigned..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "MethodNamingConventions (Java, Code Style)",
  "tags": "",
  "keywords": "Method Naming Conventions",
  "url": "pmd_rules_java_codestyle.html#methodnamingconventions",
  "summary": "Configurable naming conventions for method declarations. This rule reports method declarations which do not match..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "ModifierOrder (Java, Code Style)",
  "tags": "",
  "keywords": "Modifier Order",
  "url": "pmd_rules_java_codestyle.html#modifierorder",
  "summary": "Enforces the modifier order recommended by the JLS. Apart from sorting modifiers, this rule also..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "NoPackage (Java, Code Style)",
  "tags": "",
  "keywords": "No Package",
  "url": "pmd_rules_java_codestyle.html#nopackage",
  "summary": "Detects when a class, interface, enum or annotation does not have a package definition."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "OnlyOneReturn (Java, Code Style)",
  "tags": "",
  "keywords": "Only One Return",
  "url": "pmd_rules_java_codestyle.html#onlyonereturn",
  "summary": "A method should have only one exit point, and that should be the last statement..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "PackageCase (Java, Code Style)",
  "tags": "",
  "keywords": "Package Case",
  "url": "pmd_rules_java_codestyle.html#packagecase",
  "summary": "Detects when a package definition contains uppercase characters."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "PrematureDeclaration (Java, Code Style)",
  "tags": "",
  "keywords": "Premature Declaration",
  "url": "pmd_rules_java_codestyle.html#prematuredeclaration",
  "summary": "Checks for variables that are defined before they might be used. A declaration is deemed..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "RemoteInterfaceNamingConvention (Java, Code Style)",
  "tags": "",
  "keywords": "Remote Interface Naming Convention",
  "url": "pmd_rules_java_codestyle.html#remoteinterfacenamingconvention",
  "summary": "Remote Interface of a Session EJB should not have a suffix."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "RemoteSessionInterfaceNamingConvention (Java, Code Style)",
  "tags": "",
  "keywords": "Remote Session Interface Naming Convention",
  "url": "pmd_rules_java_codestyle.html#remotesessioninterfacenamingconvention",
  "summary": "A Remote Home interface type of a Session EJB should be suffixed by 'Home'."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "ShortClassName (Java, Code Style)",
  "tags": "",
  "keywords": "Short Class Name",
  "url": "pmd_rules_java_codestyle.html#shortclassname",
  "summary": "Short Classnames with fewer than e.g. five characters are not recommended."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "ShortMethodName (Java, Code Style)",
  "tags": "",
  "keywords": "Short Method Name",
  "url": "pmd_rules_java_codestyle.html#shortmethodname",
  "summary": "Method names that are very short are not helpful to the reader."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "ShortVariable (Java, Code Style)",
  "tags": "",
  "keywords": "Short Variable",
  "url": "pmd_rules_java_codestyle.html#shortvariable",
  "summary": "Fields, local variables, enum constant names or parameter names that are very short are not..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "TooManyStaticImports (Java, Code Style)",
  "tags": "",
  "keywords": "Too Many Static Imports",
  "url": "pmd_rules_java_codestyle.html#toomanystaticimports",
  "summary": "If you overuse the static import feature, it can make your program unreadable and unmaintainable,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "TypeParameterNamingConventions (Java, Code Style)",
  "tags": "",
  "keywords": "Type Parameter Naming Conventions",
  "url": "pmd_rules_java_codestyle.html#typeparameternamingconventions",
  "summary": "Configurable naming conventions for type parameters in generic types and methods. This rule reports type..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UnnecessaryAnnotationValueElement (Java, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Annotation Value Element",
  "url": "pmd_rules_java_codestyle.html#unnecessaryannotationvalueelement",
  "summary": "Avoid the use of value in annotations when it's the only element."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UnnecessaryBoxing (Java, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Boxing",
  "url": "pmd_rules_java_codestyle.html#unnecessaryboxing",
  "summary": "Reports explicit boxing and unboxing conversions that may safely be removed, either because they would..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UnnecessaryCast (Java, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Cast",
  "url": "pmd_rules_java_codestyle.html#unnecessarycast",
  "summary": "Detects casts which could be removed as the operand of the cast is already suitable..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UnnecessaryConstructor (Java, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Constructor",
  "url": "pmd_rules_java_codestyle.html#unnecessaryconstructor",
  "summary": "This rule detects when a constructor is not necessary; i.e., when there is only one..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UnnecessaryFullyQualifiedName (Java, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Fully Qualified Name",
  "url": "pmd_rules_java_codestyle.html#unnecessaryfullyqualifiedname",
  "summary": "Import statements allow the use of non-fully qualified names. The use of a fully qualified..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UnnecessaryImport (Java, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Import",
  "url": "pmd_rules_java_codestyle.html#unnecessaryimport",
  "summary": "Reports import statements that can be removed. They are either unused, duplicated, or the members..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UnnecessaryLocalBeforeReturn (Java, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Local Before Return",
  "url": "pmd_rules_java_codestyle.html#unnecessarylocalbeforereturn",
  "summary": "Avoid the creation of unnecessary local variables. This rule has been deprecated since 7.17.0. Use..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UnnecessaryModifier (Java, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Modifier",
  "url": "pmd_rules_java_codestyle.html#unnecessarymodifier",
  "summary": "Fields in interfaces and annotations are automatically `public static final`, and methods are `public abstract`...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UnnecessaryReturn (Java, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Return",
  "url": "pmd_rules_java_codestyle.html#unnecessaryreturn",
  "summary": "Avoid the use of unnecessary return statements. A return is unnecessary when no instructions follow..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UnnecessarySemicolon (Java, Code Style)",
  "tags": "",
  "keywords": "Unnecessary Semicolon",
  "url": "pmd_rules_java_codestyle.html#unnecessarysemicolon",
  "summary": "Reports unnecessary semicolons (so called &quot;empty statements&quot; and &quot;empty declarations&quot;). These can be removed without..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UseDiamondOperator (Java, Code Style)",
  "tags": "",
  "keywords": "Use Diamond Operator",
  "url": "pmd_rules_java_codestyle.html#usediamondoperator",
  "summary": "In some cases, explicit type arguments in a constructor call for a generic type may..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UseExplicitTypes (Java, Code Style)",
  "tags": "",
  "keywords": "Use Explicit Types",
  "url": "pmd_rules_java_codestyle.html#useexplicittypes",
  "summary": "Java 10 introduced the `var` keyword. This reduces the amount of code written because java..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UselessParentheses (Java, Code Style)",
  "tags": "",
  "keywords": "Useless Parentheses",
  "url": "pmd_rules_java_codestyle.html#uselessparentheses",
  "summary": "Parenthesized expressions are used to override the default operator precedence rules. Parentheses whose removal would..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UselessQualifiedThis (Java, Code Style)",
  "tags": "",
  "keywords": "Useless Qualified This",
  "url": "pmd_rules_java_codestyle.html#uselessqualifiedthis",
  "summary": "Reports qualified this usages in the same class."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UseShortArrayInitializer (Java, Code Style)",
  "tags": "",
  "keywords": "Use Short Array Initializer",
  "url": "pmd_rules_java_codestyle.html#useshortarrayinitializer",
  "summary": "When declaring and initializing array fields or variables, it is not necessary to explicitly create..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "UseUnderscoresInNumericLiterals (Java, Code Style)",
  "tags": "",
  "keywords": "Use Underscores In Numeric Literals",
  "url": "pmd_rules_java_codestyle.html#useunderscoresinnumericliterals",
  "summary": "Since Java 1.7, numeric literals can use underscores to separate digits. This rule enforces that..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/codestyle.md",
  "title": "VariableCanBeInlined (Java, Code Style)",
  "tags": "",
  "keywords": "Variable Can Be Inlined",
  "url": "pmd_rules_java_codestyle.html#variablecanbeinlined",
  "summary": "Local variables should not be declared and then immediately returned or thrown. Such variable declarations..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/java/design.md",
  "title": "Design (Java, Design)",
  "tags": "",
  "keywords": "Design",
  "url": "pmd_rules_java_design.html",
  "summary": "Rules that help you discover design issues."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "AbstractClassWithoutAnyMethod (Java, Design)",
  "tags": "",
  "keywords": "Abstract Class Without Any Method",
  "url": "pmd_rules_java_design.html#abstractclasswithoutanymethod",
  "summary": "If an abstract class does not provide any methods, it may be acting as a..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "AvoidCatchingGenericException (Java, Design)",
  "tags": "",
  "keywords": "Avoid Catching Generic Exception",
  "url": "pmd_rules_java_design.html#avoidcatchinggenericexception",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> The rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "AvoidDeeplyNestedIfStmts (Java, Design)",
  "tags": "",
  "keywords": "Avoid Deeply Nested If Stmts",
  "url": "pmd_rules_java_design.html#avoiddeeplynestedifstmts",
  "summary": "Avoid creating deeply nested if-then statements since they are harder to read and error-prone to..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "AvoidRethrowingException (Java, Design)",
  "tags": "",
  "keywords": "Avoid Rethrowing Exception",
  "url": "pmd_rules_java_design.html#avoidrethrowingexception",
  "summary": "Catch blocks that merely rethrow a caught exception only add to code size and runtime..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "AvoidThrowingNewInstanceOfSameException (Java, Design)",
  "tags": "",
  "keywords": "Avoid Throwing New Instance Of Same Exception",
  "url": "pmd_rules_java_design.html#avoidthrowingnewinstanceofsameexception",
  "summary": "Catch blocks that merely rethrow a caught exception wrapped inside a new instance of the..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "AvoidThrowingNullPointerException (Java, Design)",
  "tags": "",
  "keywords": "Avoid Throwing Null Pointer Exception",
  "url": "pmd_rules_java_design.html#avoidthrowingnullpointerexception",
  "summary": "Avoid throwing NullPointerExceptions manually. These are confusing because most people will assume that the virtual..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "AvoidThrowingRawExceptionTypes (Java, Design)",
  "tags": "",
  "keywords": "Avoid Throwing Raw Exception Types",
  "url": "pmd_rules_java_design.html#avoidthrowingrawexceptiontypes",
  "summary": "Avoid throwing certain exception types. Rather than throw a raw RuntimeException, Throwable, Exception, or Error,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "AvoidUncheckedExceptionsInSignatures (Java, Design)",
  "tags": "",
  "keywords": "Avoid Unchecked Exceptions In Signatures",
  "url": "pmd_rules_java_design.html#avoiduncheckedexceptionsinsignatures",
  "summary": "Reports unchecked exceptions in the `throws` clause of a method or constructor. Java doesn't force..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "ClassWithOnlyPrivateConstructorsShouldBeFinal (Java, Design)",
  "tags": "",
  "keywords": "Class With Only Private Constructors Should Be Final",
  "url": "pmd_rules_java_design.html#classwithonlyprivateconstructorsshouldbefinal",
  "summary": "Reports classes that may be made final because they cannot be extended from outside their..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "CognitiveComplexity (Java, Design)",
  "tags": "",
  "keywords": "Cognitive Complexity",
  "url": "pmd_rules_java_design.html#cognitivecomplexity",
  "summary": "Methods that are highly complex are difficult to read and more costly to maintain. If..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "CollapsibleIfStatements (Java, Design)",
  "tags": "",
  "keywords": "Collapsible If Statements",
  "url": "pmd_rules_java_design.html#collapsibleifstatements",
  "summary": "Reports nested 'if' statements that can be merged together by joining their conditions with a..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "CouplingBetweenObjects (Java, Design)",
  "tags": "",
  "keywords": "Coupling Between Objects",
  "url": "pmd_rules_java_design.html#couplingbetweenobjects",
  "summary": "This rule counts the number of unique attributes, local variables, and return types within an..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "CyclomaticComplexity (Java, Design)",
  "tags": "",
  "keywords": "Cyclomatic Complexity",
  "url": "pmd_rules_java_design.html#cyclomaticcomplexity",
  "summary": "The complexity of methods directly affects maintenance costs and readability. Concentrating too much decisional logic..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "DataClass (Java, Design)",
  "tags": "",
  "keywords": "Data Class",
  "url": "pmd_rules_java_design.html#dataclass",
  "summary": "Data Classes are simple data holders, which reveal most of their state, and without complex..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "DoNotExtendJavaLangError (Java, Design)",
  "tags": "",
  "keywords": "Do Not Extend Java Lang Error",
  "url": "pmd_rules_java_design.html#donotextendjavalangerror",
  "summary": "Errors are system exceptions. Do not extend them."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "ExceptionAsFlowControl (Java, Design)",
  "tags": "",
  "keywords": "Exception As Flow Control",
  "url": "pmd_rules_java_design.html#exceptionasflowcontrol",
  "summary": "This rule reports exceptions thrown and caught in an enclosing try statement. This use of..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "ExcessiveImports (Java, Design)",
  "tags": "",
  "keywords": "Excessive Imports",
  "url": "pmd_rules_java_design.html#excessiveimports",
  "summary": "A high number of imports can indicate a high degree of coupling within an object...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "ExcessiveParameterList (Java, Design)",
  "tags": "",
  "keywords": "Excessive Parameter List",
  "url": "pmd_rules_java_design.html#excessiveparameterlist",
  "summary": "Methods with numerous parameters are a challenge to maintain, especially if most of them share..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "ExcessivePublicCount (Java, Design)",
  "tags": "",
  "keywords": "Excessive Public Count",
  "url": "pmd_rules_java_design.html#excessivepubliccount",
  "summary": "Classes with large numbers of public methods and attributes require disproportionate testing efforts since combinational..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "FinalFieldCouldBeStatic (Java, Design)",
  "tags": "",
  "keywords": "Final Field Could Be Static",
  "url": "pmd_rules_java_design.html#finalfieldcouldbestatic",
  "summary": "If a final field is assigned to a compile-time constant, it could be made static,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "GodClass (Java, Design)",
  "tags": "",
  "keywords": "God Class",
  "url": "pmd_rules_java_design.html#godclass",
  "summary": "The God Class rule detects the God Class design flaw using metrics. God classes do..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "ImmutableField (Java, Design)",
  "tags": "",
  "keywords": "Immutable Field",
  "url": "pmd_rules_java_design.html#immutablefield",
  "summary": "Reports non-final fields whose value never changes once object initialization ends, and hence may be..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "InvalidJavaBean (Java, Design)",
  "tags": "",
  "keywords": "Invalid Java Bean",
  "url": "pmd_rules_java_design.html#invalidjavabean",
  "summary": "Identifies beans, that don't follow the [JavaBeans API specification](https://download.oracle.com/otndocs/jcp/7224-javabeans-1.01-fr-spec-oth-JSpec/). Each non-static field should have both..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "LawOfDemeter (Java, Design)",
  "tags": "",
  "keywords": "Law Of Demeter",
  "url": "pmd_rules_java_design.html#lawofdemeter",
  "summary": "The law of Demeter is a simple rule that says &quot;only talk to friends&quot;. It..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "LogicInversion (Java, Design)",
  "tags": "",
  "keywords": "Logic Inversion",
  "url": "pmd_rules_java_design.html#logicinversion",
  "summary": "Use opposite operator instead of negating the whole expression with a logic complement operator."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "LoosePackageCoupling (Java, Design)",
  "tags": "",
  "keywords": "Loose Package Coupling",
  "url": "pmd_rules_java_design.html#loosepackagecoupling",
  "summary": "Avoid using classes from the configured package hierarchy outside of the package hierarchy, except when..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "MutableStaticState (Java, Design)",
  "tags": "",
  "keywords": "Mutable Static State",
  "url": "pmd_rules_java_design.html#mutablestaticstate",
  "summary": "Non-private static fields should be made constants (or immutable references) by declaring them final. Non-private..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "NcssCount (Java, Design)",
  "tags": "",
  "keywords": "Ncss Count",
  "url": "pmd_rules_java_design.html#ncsscount",
  "summary": "This rule uses the NCSS (Non-Commenting Source Statements) metric to determine the number of lines..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "NPathComplexity (Java, Design)",
  "tags": "",
  "keywords": "N Path Complexity",
  "url": "pmd_rules_java_design.html#npathcomplexity",
  "summary": "The NPath complexity of a method is the number of acyclic execution paths through that..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "PublicMemberInNonPublicType (Java, Design)",
  "tags": "",
  "keywords": "Public Member In Non Public Type",
  "url": "pmd_rules_java_design.html#publicmemberinnonpublictype",
  "summary": "A non-public type should not declare its own members as public, as their visibility is..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "SignatureDeclareThrowsException (Java, Design)",
  "tags": "",
  "keywords": "Signature Declare Throws Exception",
  "url": "pmd_rules_java_design.html#signaturedeclarethrowsexception",
  "summary": "A method/constructor shouldn't explicitly throw the generic java.lang.Exception, since it is unclear which exceptions that..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "SimplifiedTernary (Java, Design)",
  "tags": "",
  "keywords": "Simplified Ternary",
  "url": "pmd_rules_java_design.html#simplifiedternary",
  "summary": "Reports ternary expression with the form `condition ? literalBoolean : foo` or `condition ? foo..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "SimplifyBooleanExpressions (Java, Design)",
  "tags": "",
  "keywords": "Simplify Boolean Expressions",
  "url": "pmd_rules_java_design.html#simplifybooleanexpressions",
  "summary": "Avoid unnecessary comparisons in boolean expressions, they serve no purpose and impacts readability."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "SimplifyBooleanReturns (Java, Design)",
  "tags": "",
  "keywords": "Simplify Boolean Returns",
  "url": "pmd_rules_java_design.html#simplifybooleanreturns",
  "summary": "Avoid unnecessary if-then-else statements when returning a boolean. The result of the conditional test can..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "SimplifyConditional (Java, Design)",
  "tags": "",
  "keywords": "Simplify Conditional",
  "url": "pmd_rules_java_design.html#simplifyconditional",
  "summary": "No need to check for null before an instanceof; the instanceof keyword returns false when..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "SingularField (Java, Design)",
  "tags": "",
  "keywords": "Singular Field",
  "url": "pmd_rules_java_design.html#singularfield",
  "summary": "Reports fields which may be converted to a local variable. This is so because in..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "SwitchDensity (Java, Design)",
  "tags": "",
  "keywords": "Switch Density",
  "url": "pmd_rules_java_design.html#switchdensity",
  "summary": "A high ratio of statements to labels in a switch statement implies that the switch..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "TooManyFields (Java, Design)",
  "tags": "",
  "keywords": "Too Many Fields",
  "url": "pmd_rules_java_design.html#toomanyfields",
  "summary": "Classes that have too many fields can become unwieldy and could be redesigned to have..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "TooManyMethods (Java, Design)",
  "tags": "",
  "keywords": "Too Many Methods",
  "url": "pmd_rules_java_design.html#toomanymethods",
  "summary": "A class with too many methods is probably a good suspect for refactoring, in order..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "UselessOverridingMethod (Java, Design)",
  "tags": "",
  "keywords": "Useless Overriding Method",
  "url": "pmd_rules_java_design.html#uselessoverridingmethod",
  "summary": "The overriding method merely calls the same method defined in a superclass."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "UseObjectForClearerAPI (Java, Design)",
  "tags": "",
  "keywords": "Use Object For ClearerAPI",
  "url": "pmd_rules_java_design.html#useobjectforclearerapi",
  "summary": "When you write a public method, you should be thinking in terms of an API...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/design.md",
  "title": "UseUtilityClass (Java, Design)",
  "tags": "",
  "keywords": "Use Utility Class",
  "url": "pmd_rules_java_design.html#useutilityclass",
  "summary": "For classes that only have static methods, consider making them utility classes. Note that this..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/java/documentation.md",
  "title": "Documentation (Java, Documentation)",
  "tags": "",
  "keywords": "Documentation",
  "url": "pmd_rules_java_documentation.html",
  "summary": "Rules that are related to code documentation."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/documentation.md",
  "title": "CommentContent (Java, Documentation)",
  "tags": "",
  "keywords": "Comment Content",
  "url": "pmd_rules_java_documentation.html#commentcontent",
  "summary": "A rule for the politically correct... we don't want to offend anyone."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/documentation.md",
  "title": "CommentRequired (Java, Documentation)",
  "tags": "",
  "keywords": "Comment Required",
  "url": "pmd_rules_java_documentation.html#commentrequired",
  "summary": "Denotes whether javadoc (formal) comments are required (or unwanted) for specific language elements."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/documentation.md",
  "title": "CommentSize (Java, Documentation)",
  "tags": "",
  "keywords": "Comment Size",
  "url": "pmd_rules_java_documentation.html#commentsize",
  "summary": "Determines whether the dimensions of non-header comments found are within the specified limits."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/documentation.md",
  "title": "DanglingJavadoc (Java, Documentation)",
  "tags": "",
  "keywords": "Dangling Javadoc",
  "url": "pmd_rules_java_documentation.html#danglingjavadoc",
  "summary": "Javadoc comments that do not belong to a class, method or field are ignored by..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/documentation.md",
  "title": "UncommentedEmptyConstructor (Java, Documentation)",
  "tags": "",
  "keywords": "Uncommented Empty Constructor",
  "url": "pmd_rules_java_documentation.html#uncommentedemptyconstructor",
  "summary": "Uncommented Empty Constructor finds instances where a constructor does not contain statements, but there is..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/documentation.md",
  "title": "UncommentedEmptyMethodBody (Java, Documentation)",
  "tags": "",
  "keywords": "Uncommented Empty Method Body",
  "url": "pmd_rules_java_documentation.html#uncommentedemptymethodbody",
  "summary": "Uncommented Empty Method Body finds instances where a method body does not contain statements, but..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "Error Prone (Java, Error Prone)",
  "tags": "",
  "keywords": "Error Prone",
  "url": "pmd_rules_java_errorprone.html",
  "summary": "Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AssignmentInOperand (Java, Error Prone)",
  "tags": "",
  "keywords": "Assignment In Operand",
  "url": "pmd_rules_java_errorprone.html#assignmentinoperand",
  "summary": "Avoid assignments in operands; this can make code more complicated and harder to read."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AssignmentToNonFinalStatic (Java, Error Prone)",
  "tags": "",
  "keywords": "Assignment To Non Final Static",
  "url": "pmd_rules_java_errorprone.html#assignmenttononfinalstatic",
  "summary": "Identifies a possible unsafe usage of a static field."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidAccessibilityAlteration (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Accessibility Alteration",
  "url": "pmd_rules_java_errorprone.html#avoidaccessibilityalteration",
  "summary": "Methods such as `getDeclaredConstructors()`, `getDeclaredMethods()`, and `getDeclaredFields()` also return private constructors, methods and fields. These..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidAssertAsIdentifier (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Assert As Identifier",
  "url": "pmd_rules_java_errorprone.html#avoidassertasidentifier",
  "summary": "Use of the term `assert` will conflict with newer versions of Java since it is..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidBranchingStatementAsLastInLoop (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Branching Statement As Last In Loop",
  "url": "pmd_rules_java_errorprone.html#avoidbranchingstatementaslastinloop",
  "summary": "Using a branching statement as the last part of a loop may be a bug,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidCallingFinalize (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Calling Finalize",
  "url": "pmd_rules_java_errorprone.html#avoidcallingfinalize",
  "summary": "The method Object.finalize() is called by the garbage collector on an object when garbage collection..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidCatchingGenericException (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Catching Generic Exception",
  "url": "pmd_rules_java_errorprone.html#avoidcatchinggenericexception",
  "summary": "Avoid catching generic exceptions in try-catch blocks. Catching overly broad exception types makes it difficult..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidCatchingNPE (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid CatchingNPE",
  "url": "pmd_rules_java_errorprone.html#avoidcatchingnpe",
  "summary": "Code should never throw NullPointerExceptions under normal circumstances. A catch block may hide the original..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidCatchingThrowable (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Catching Throwable",
  "url": "pmd_rules_java_errorprone.html#avoidcatchingthrowable",
  "summary": "Catching Throwable errors is not recommended since its scope is very broad. It includes runtime..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidDecimalLiteralsInBigDecimalConstructor (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Decimal Literals In Big Decimal Constructor",
  "url": "pmd_rules_java_errorprone.html#avoiddecimalliteralsinbigdecimalconstructor",
  "summary": "One might assume that the result of &quot;new BigDecimal(0.1)&quot; is exactly equal to 0.1, but..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidDuplicateLiterals (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Duplicate Literals",
  "url": "pmd_rules_java_errorprone.html#avoidduplicateliterals",
  "summary": "Code containing duplicate String literals can usually be improved by declaring the String as a..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidEnumAsIdentifier (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Enum As Identifier",
  "url": "pmd_rules_java_errorprone.html#avoidenumasidentifier",
  "summary": "Use of the term `enum` will conflict with newer versions of Java since it is..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidFieldNameMatchingMethodName (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Field Name Matching Method Name",
  "url": "pmd_rules_java_errorprone.html#avoidfieldnamematchingmethodname",
  "summary": "It can be confusing to have a field name with the same name as a..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidFieldNameMatchingTypeName (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Field Name Matching Type Name",
  "url": "pmd_rules_java_errorprone.html#avoidfieldnamematchingtypename",
  "summary": "It is somewhat confusing to have a field name matching the declaring type name. This..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidInstanceofChecksInCatchClause (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Instanceof Checks In Catch Clause",
  "url": "pmd_rules_java_errorprone.html#avoidinstanceofchecksincatchclause",
  "summary": "Each caught exception type should be handled in its own catch clause."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidLiteralsInIfCondition (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Literals In If Condition",
  "url": "pmd_rules_java_errorprone.html#avoidliteralsinifcondition",
  "summary": "Avoid using hard-coded literals in conditional statements. By declaring them as static variables or private..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidLosingExceptionInformation (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Losing Exception Information",
  "url": "pmd_rules_java_errorprone.html#avoidlosingexceptioninformation",
  "summary": "Statements in a catch block that invoke accessors on the exception without using the information..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidMultipleUnaryOperators (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Multiple Unary Operators",
  "url": "pmd_rules_java_errorprone.html#avoidmultipleunaryoperators",
  "summary": "The use of multiple unary operators may be problematic, and/or confusing. Ensure that the intended..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "AvoidUsingOctalValues (Java, Error Prone)",
  "tags": "",
  "keywords": "Avoid Using Octal Values",
  "url": "pmd_rules_java_errorprone.html#avoidusingoctalvalues",
  "summary": "Integer literals that start with zero are interpreted as octal (base-8) values in Java, which..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "BrokenNullCheck (Java, Error Prone)",
  "tags": "",
  "keywords": "Broken Null Check",
  "url": "pmd_rules_java_errorprone.html#brokennullcheck",
  "summary": "The null check is broken since it will throw a NullPointerException itself. It is likely..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "CallSuperFirst (Java, Error Prone)",
  "tags": "",
  "keywords": "Call Super First",
  "url": "pmd_rules_java_errorprone.html#callsuperfirst",
  "summary": "Super should be called at the start of the method"
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "CallSuperLast (Java, Error Prone)",
  "tags": "",
  "keywords": "Call Super Last",
  "url": "pmd_rules_java_errorprone.html#callsuperlast",
  "summary": "Super should be called at the end of the method"
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "CheckSkipResult (Java, Error Prone)",
  "tags": "",
  "keywords": "Check Skip Result",
  "url": "pmd_rules_java_errorprone.html#checkskipresult",
  "summary": "The skip() method may skip a smaller number of bytes than requested. Check the returned..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ClassCastExceptionWithToArray (Java, Error Prone)",
  "tags": "",
  "keywords": "Class Cast Exception With To Array",
  "url": "pmd_rules_java_errorprone.html#classcastexceptionwithtoarray",
  "summary": "When deriving an array of a specific class from your Collection, one should provide an..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "CloneMethodMustBePublic (Java, Error Prone)",
  "tags": "",
  "keywords": "Clone Method Must Be Public",
  "url": "pmd_rules_java_errorprone.html#clonemethodmustbepublic",
  "summary": "The java manual says &quot;By convention, classes that implement this interface should override Object.clone (which..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "CloneMethodMustImplementCloneable (Java, Error Prone)",
  "tags": "",
  "keywords": "Clone Method Must Implement Cloneable",
  "url": "pmd_rules_java_errorprone.html#clonemethodmustimplementcloneable",
  "summary": "The method clone() should only be implemented if the class implements the Cloneable interface with..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "CloneMethodReturnTypeMustMatchClassName (Java, Error Prone)",
  "tags": "",
  "keywords": "Clone Method Return Type Must Match Class Name",
  "url": "pmd_rules_java_errorprone.html#clonemethodreturntypemustmatchclassname",
  "summary": "If a class implements `Cloneable` the return type of the method `clone()` must be the..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "CloseResource (Java, Error Prone)",
  "tags": "",
  "keywords": "Close Resource",
  "url": "pmd_rules_java_errorprone.html#closeresource",
  "summary": "Ensure that resources (like `java.sql.Connection`, `java.sql.Statement`, and `java.sql.ResultSet` objects and any subtype of `java.lang.AutoCloseable`) are..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "CollectionTypeMismatch (Java, Error Prone)",
  "tags": "",
  "keywords": "Collection Type Mismatch",
  "url": "pmd_rules_java_errorprone.html#collectiontypemismatch",
  "summary": "Detects method calls on collections where the passed object cannot possibly be in the collection..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "CompareObjectsWithEquals (Java, Error Prone)",
  "tags": "",
  "keywords": "Compare Objects With Equals",
  "url": "pmd_rules_java_errorprone.html#compareobjectswithequals",
  "summary": "Use `equals()` to compare object references; avoid comparing them with `==`. Since comparing objects with..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ComparisonWithNaN (Java, Error Prone)",
  "tags": "",
  "keywords": "Comparison With NaN",
  "url": "pmd_rules_java_errorprone.html#comparisonwithnan",
  "summary": "Reports comparisons with double and float `NaN` (Not-a-Number) values. These are [specified](https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.21.1) to have unintuitive..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ConfusingArgumentToVarargsMethod (Java, Error Prone)",
  "tags": "",
  "keywords": "Confusing Argument To Varargs Method",
  "url": "pmd_rules_java_errorprone.html#confusingargumenttovarargsmethod",
  "summary": "Reports a confusing argument passed to a varargs method. This can occur when an array..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ConstructorCallsOverridableMethod (Java, Error Prone)",
  "tags": "",
  "keywords": "Constructor Calls Overridable Method",
  "url": "pmd_rules_java_errorprone.html#constructorcallsoverridablemethod",
  "summary": "Reports calls to overridable methods on `this` during object initialization. These are invoked on an..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "DetachedTestCase (Java, Error Prone)",
  "tags": "",
  "keywords": "Detached Test Case",
  "url": "pmd_rules_java_errorprone.html#detachedtestcase",
  "summary": "The method appears to be a test case since it has public or default visibility,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "DoNotCallGarbageCollectionExplicitly (Java, Error Prone)",
  "tags": "",
  "keywords": "Do Not Call Garbage Collection Explicitly",
  "url": "pmd_rules_java_errorprone.html#donotcallgarbagecollectionexplicitly",
  "summary": "Calls to `System.gc()`, `Runtime.getRuntime().gc()`, and `System.runFinalization()` are not advised. Code should have the same behavior..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "DoNotExtendJavaLangThrowable (Java, Error Prone)",
  "tags": "",
  "keywords": "Do Not Extend Java Lang Throwable",
  "url": "pmd_rules_java_errorprone.html#donotextendjavalangthrowable",
  "summary": "Extend Exception or RuntimeException instead of Throwable."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "DoNotHardCodeSDCard (Java, Error Prone)",
  "tags": "",
  "keywords": "Do Not Hard CodeSD Card",
  "url": "pmd_rules_java_errorprone.html#donothardcodesdcard",
  "summary": "Use Environment.getExternalStorageDirectory() instead of &quot;/sdcard&quot;"
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "DoNotTerminateVM (Java, Error Prone)",
  "tags": "",
  "keywords": "Do Not TerminateVM",
  "url": "pmd_rules_java_errorprone.html#donotterminatevm",
  "summary": "Web applications should not call `System.exit()`, since only the web container or the application server..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "DoNotThrowExceptionInFinally (Java, Error Prone)",
  "tags": "",
  "keywords": "Do Not Throw Exception In Finally",
  "url": "pmd_rules_java_errorprone.html#donotthrowexceptioninfinally",
  "summary": "Throwing exceptions within a 'finally' block is confusing since they may mask other exceptions or..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "DontImportSun (Java, Error Prone)",
  "tags": "",
  "keywords": "Dont Import Sun",
  "url": "pmd_rules_java_errorprone.html#dontimportsun",
  "summary": "Avoid importing anything from the 'sun.*' packages. These packages are not portable and are likely..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "DontUseFloatTypeForLoopIndices (Java, Error Prone)",
  "tags": "",
  "keywords": "Dont Use Float Type For Loop Indices",
  "url": "pmd_rules_java_errorprone.html#dontusefloattypeforloopindices",
  "summary": "Don't use floating point for loop indices. If you must use floating point, use double..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "EmptyCatchBlock (Java, Error Prone)",
  "tags": "",
  "keywords": "Empty Catch Block",
  "url": "pmd_rules_java_errorprone.html#emptycatchblock",
  "summary": "Empty Catch Block finds instances where an exception is caught, but nothing is done. In..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "EmptyFinalizer (Java, Error Prone)",
  "tags": "",
  "keywords": "Empty Finalizer",
  "url": "pmd_rules_java_errorprone.html#emptyfinalizer",
  "summary": "Empty finalize methods serve no purpose and should be removed. Note that Oracle has declared..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "EqualsNull (Java, Error Prone)",
  "tags": "",
  "keywords": "Equals Null",
  "url": "pmd_rules_java_errorprone.html#equalsnull",
  "summary": "Tests for null should not use the equals() method. The '==' operator should be used..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "FinalizeDoesNotCallSuperFinalize (Java, Error Prone)",
  "tags": "",
  "keywords": "Finalize Does Not Call Super Finalize",
  "url": "pmd_rules_java_errorprone.html#finalizedoesnotcallsuperfinalize",
  "summary": "If the finalize() is implemented, its last action should be to call super.finalize. Note that..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "FinalizeOnlyCallsSuperFinalize (Java, Error Prone)",
  "tags": "",
  "keywords": "Finalize Only Calls Super Finalize",
  "url": "pmd_rules_java_errorprone.html#finalizeonlycallssuperfinalize",
  "summary": "If the finalize() is implemented, it should do something besides just calling super.finalize(). Note that..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "FinalizeOverloaded (Java, Error Prone)",
  "tags": "",
  "keywords": "Finalize Overloaded",
  "url": "pmd_rules_java_errorprone.html#finalizeoverloaded",
  "summary": "Methods named finalize() should not have parameters. It is confusing and most likely an attempt..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "FinalizeShouldBeProtected (Java, Error Prone)",
  "tags": "",
  "keywords": "Finalize Should Be Protected",
  "url": "pmd_rules_java_errorprone.html#finalizeshouldbeprotected",
  "summary": "When overriding the finalize(), the new method should be set as protected. If made public,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "IdempotentOperations (Java, Error Prone)",
  "tags": "",
  "keywords": "Idempotent Operations",
  "url": "pmd_rules_java_errorprone.html#idempotentoperations",
  "summary": "Avoid idempotent operations - they have no effect."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "IdenticalConditionalBranches (Java, Error Prone)",
  "tags": "",
  "keywords": "Identical Conditional Branches",
  "url": "pmd_rules_java_errorprone.html#identicalconditionalbranches",
  "summary": "Conditional statement that does the same thing when the condition is true and false is..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ImplicitSwitchFallThrough (Java, Error Prone)",
  "tags": "",
  "keywords": "Implicit Switch Fall Through",
  "url": "pmd_rules_java_errorprone.html#implicitswitchfallthrough",
  "summary": "Switch statements without break or return statements for each case option may indicate problematic behaviour...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "InstantiationToGetClass (Java, Error Prone)",
  "tags": "",
  "keywords": "Instantiation To Get Class",
  "url": "pmd_rules_java_errorprone.html#instantiationtogetclass",
  "summary": "Avoid instantiating an object just to call getClass() on it; use the .class public member..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "InvalidLogMessageFormat (Java, Error Prone)",
  "tags": "",
  "keywords": "Invalid Log Message Format",
  "url": "pmd_rules_java_errorprone.html#invalidlogmessageformat",
  "summary": "Check for messages in slf4j and log4j2 (since 6.19.0) loggers with non matching number of..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "JumbledIncrementer (Java, Error Prone)",
  "tags": "",
  "keywords": "Jumbled Incrementer",
  "url": "pmd_rules_java_errorprone.html#jumbledincrementer",
  "summary": "Avoid jumbled loop incrementers - it's usually a mistake, and is confusing even if intentional...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "JUnitSpelling (Java, Error Prone)",
  "tags": "",
  "keywords": "J Unit Spelling",
  "url": "pmd_rules_java_errorprone.html#junitspelling",
  "summary": "In JUnit 3, the setUp method is used to set up all data entities required..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "JUnitStaticSuite (Java, Error Prone)",
  "tags": "",
  "keywords": "J Unit Static Suite",
  "url": "pmd_rules_java_errorprone.html#junitstaticsuite",
  "summary": "The suite() method in a JUnit test needs to be both public and static."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "MethodWithSameNameAsEnclosingClass (Java, Error Prone)",
  "tags": "",
  "keywords": "Method With Same Name As Enclosing Class",
  "url": "pmd_rules_java_errorprone.html#methodwithsamenameasenclosingclass",
  "summary": "A method should not have the same name as its containing class. This would be..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "MisplacedNullCheck (Java, Error Prone)",
  "tags": "",
  "keywords": "Misplaced Null Check",
  "url": "pmd_rules_java_errorprone.html#misplacednullcheck",
  "summary": "The null check here is misplaced. If the variable is null a `NullPointerException` will be..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "MissingSerialVersionUID (Java, Error Prone)",
  "tags": "",
  "keywords": "Missing Serial VersionUID",
  "url": "pmd_rules_java_errorprone.html#missingserialversionuid",
  "summary": "Serializable classes should provide a serialVersionUID field. The serialVersionUID field is also needed for abstract..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "MissingStaticMethodInNonInstantiatableClass (Java, Error Prone)",
  "tags": "",
  "keywords": "Missing Static Method In Non Instantiatable Class",
  "url": "pmd_rules_java_errorprone.html#missingstaticmethodinnoninstantiatableclass",
  "summary": "A class that has private constructors and does not have any static methods or fields..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "MoreThanOneLogger (Java, Error Prone)",
  "tags": "",
  "keywords": "More Than One Logger",
  "url": "pmd_rules_java_errorprone.html#morethanonelogger",
  "summary": "Normally only one logger is used in each class. This rule supports slf4j, log4j, Java..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "NonCaseLabelInSwitch (Java, Error Prone)",
  "tags": "",
  "keywords": "Non Case Label In Switch",
  "url": "pmd_rules_java_errorprone.html#noncaselabelinswitch",
  "summary": "A non-case label (e.g. a named break/continue label) was present in a switch statement or..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "NonCaseLabelInSwitchStatement (Java, Error Prone)",
  "tags": "",
  "keywords": "Non Case Label In Switch Statement",
  "url": "pmd_rules_java_errorprone.html#noncaselabelinswitchstatement",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "NonSerializableClass (Java, Error Prone)",
  "tags": "",
  "keywords": "Non Serializable Class",
  "url": "pmd_rules_java_errorprone.html#nonserializableclass",
  "summary": "If a class is marked as `Serializable`, then all fields need to be serializable as..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "NonStaticInitializer (Java, Error Prone)",
  "tags": "",
  "keywords": "Non Static Initializer",
  "url": "pmd_rules_java_errorprone.html#nonstaticinitializer",
  "summary": "A non-static initializer block will be called any time a constructor is invoked (just prior..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "NullAssignment (Java, Error Prone)",
  "tags": "",
  "keywords": "Null Assignment",
  "url": "pmd_rules_java_errorprone.html#nullassignment",
  "summary": "Assigning a &quot;null&quot; to a variable (outside of its declaration) is usually bad form. Sometimes,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "OverrideBothEqualsAndHashcode (Java, Error Prone)",
  "tags": "",
  "keywords": "Override Both Equals And Hashcode",
  "url": "pmd_rules_java_errorprone.html#overridebothequalsandhashcode",
  "summary": "Override both `public boolean Object.equals(Object other)` and `public int Object.hashCode()` or override neither. Even if..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "OverrideBothEqualsAndHashCodeOnComparable (Java, Error Prone)",
  "tags": "",
  "keywords": "Override Both Equals And Hash Code On Comparable",
  "url": "pmd_rules_java_errorprone.html#overridebothequalsandhashcodeoncomparable",
  "summary": "Classes that implement `Comparable` should override both `equals()` and `hashCode()` if instances of these classes..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ProperCloneImplementation (Java, Error Prone)",
  "tags": "",
  "keywords": "Proper Clone Implementation",
  "url": "pmd_rules_java_errorprone.html#propercloneimplementation",
  "summary": "Object clone() should be implemented with super.clone()."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ProperLogger (Java, Error Prone)",
  "tags": "",
  "keywords": "Proper Logger",
  "url": "pmd_rules_java_errorprone.html#properlogger",
  "summary": "A logger should normally be defined private static final and be associated with the correct..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ReplaceJavaUtilCalendar (Java, Error Prone)",
  "tags": "",
  "keywords": "Replace Java Util Calendar",
  "url": "pmd_rules_java_errorprone.html#replacejavautilcalendar",
  "summary": "The legacy `java.util.Calendar` API is error-prone, mutable, and not thread-safe. It has confusing month indexing..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ReplaceJavaUtilDate (Java, Error Prone)",
  "tags": "",
  "keywords": "Replace Java Util Date",
  "url": "pmd_rules_java_errorprone.html#replacejavautildate",
  "summary": "The legacy `java.util.Date` class is mutable, not thread-safe, and has a confusing API. Many of..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ReturnEmptyCollectionRatherThanNull (Java, Error Prone)",
  "tags": "",
  "keywords": "Return Empty Collection Rather Than Null",
  "url": "pmd_rules_java_errorprone.html#returnemptycollectionratherthannull",
  "summary": "For any method that returns an collection (such as an array, Collection or Map), it..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "ReturnFromFinallyBlock (Java, Error Prone)",
  "tags": "",
  "keywords": "Return From Finally Block",
  "url": "pmd_rules_java_errorprone.html#returnfromfinallyblock",
  "summary": "Avoid returning from a finally block, this can discard exceptions."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "SimpleDateFormatNeedsLocale (Java, Error Prone)",
  "tags": "",
  "keywords": "Simple Date Format Needs Locale",
  "url": "pmd_rules_java_errorprone.html#simpledateformatneedslocale",
  "summary": "Be sure to specify a Locale when creating SimpleDateFormat instances to ensure that locale-appropriate formatting..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "SingleMethodSingleton (Java, Error Prone)",
  "tags": "",
  "keywords": "Single Method Singleton",
  "url": "pmd_rules_java_errorprone.html#singlemethodsingleton",
  "summary": "Some classes contain overloaded getInstance. The problem with overloaded getInstance methods is that the instance..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "SingletonClassReturningNewInstance (Java, Error Prone)",
  "tags": "",
  "keywords": "Singleton Class Returning New Instance",
  "url": "pmd_rules_java_errorprone.html#singletonclassreturningnewinstance",
  "summary": "A singleton class should only ever have one instance. Failure to check whether an instance..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "StaticEJBFieldShouldBeFinal (Java, Error Prone)",
  "tags": "",
  "keywords": "StaticEJB Field Should Be Final",
  "url": "pmd_rules_java_errorprone.html#staticejbfieldshouldbefinal",
  "summary": "According to the J2EE specification, an EJB should not have any static fields with write..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "StringBufferInstantiationWithChar (Java, Error Prone)",
  "tags": "",
  "keywords": "String Buffer Instantiation With Char",
  "url": "pmd_rules_java_errorprone.html#stringbufferinstantiationwithchar",
  "summary": "Individual character values provided as initialization arguments will be converted into integers. This can lead..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "SuspiciousEqualsMethodName (Java, Error Prone)",
  "tags": "",
  "keywords": "Suspicious Equals Method Name",
  "url": "pmd_rules_java_errorprone.html#suspiciousequalsmethodname",
  "summary": "The method name and parameter number are suspiciously close to `Object.equals`, which can denote an..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "SuspiciousHashcodeMethodName (Java, Error Prone)",
  "tags": "",
  "keywords": "Suspicious Hashcode Method Name",
  "url": "pmd_rules_java_errorprone.html#suspicioushashcodemethodname",
  "summary": "The method name and return type are suspiciously close to hashCode(), which may denote an..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "SuspiciousOctalEscape (Java, Error Prone)",
  "tags": "",
  "keywords": "Suspicious Octal Escape",
  "url": "pmd_rules_java_errorprone.html#suspiciousoctalescape",
  "summary": "A suspicious octal escape sequence was found inside a String literal. The Java language specification..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "TestClassWithoutTestCases (Java, Error Prone)",
  "tags": "",
  "keywords": "Test Class Without Test Cases",
  "url": "pmd_rules_java_errorprone.html#testclasswithouttestcases",
  "summary": "Test classes typically start or end with the affix &quot;Test&quot;, &quot;Tests&quot; or &quot;TestCase&quot;. Having a..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UnconditionalIfStatement (Java, Error Prone)",
  "tags": "",
  "keywords": "Unconditional If Statement",
  "url": "pmd_rules_java_errorprone.html#unconditionalifstatement",
  "summary": "Do not use &quot;if&quot; statements whose conditionals are always true or always false."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UnnecessaryBooleanAssertion (Java, Error Prone)",
  "tags": "",
  "keywords": "Unnecessary Boolean Assertion",
  "url": "pmd_rules_java_errorprone.html#unnecessarybooleanassertion",
  "summary": "A JUnit test assertion with a boolean literal is unnecessary since it always will evaluate..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UnnecessaryCaseChange (Java, Error Prone)",
  "tags": "",
  "keywords": "Unnecessary Case Change",
  "url": "pmd_rules_java_errorprone.html#unnecessarycasechange",
  "summary": "Using equalsIgnoreCase() is faster than using toUpperCase/toLowerCase().equals()"
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UnnecessaryConversionTemporary (Java, Error Prone)",
  "tags": "",
  "keywords": "Unnecessary Conversion Temporary",
  "url": "pmd_rules_java_errorprone.html#unnecessaryconversiontemporary",
  "summary": "Avoid the use temporary objects when converting primitives to Strings. Use the static conversion methods..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UnsupportedJdkApiUsage (Java, Error Prone)",
  "tags": "",
  "keywords": "Unsupported Jdk Api Usage",
  "url": "pmd_rules_java_errorprone.html#unsupportedjdkapiusage",
  "summary": "Avoid importing classes or using APIs from the `sun.*` or `jdk.internal.*` packages, including `sun.misc.Unsafe` or..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UnusedNullCheckInEquals (Java, Error Prone)",
  "tags": "",
  "keywords": "Unused Null Check In Equals",
  "url": "pmd_rules_java_errorprone.html#unusednullcheckinequals",
  "summary": "After checking an object reference for null, you should invoke equals() on that object rather..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UseCorrectExceptionLogging (Java, Error Prone)",
  "tags": "",
  "keywords": "Use Correct Exception Logging",
  "url": "pmd_rules_java_errorprone.html#usecorrectexceptionlogging",
  "summary": "To make sure the full stacktrace is printed out, use the logging statement with two..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UseEqualsToCompareStrings (Java, Error Prone)",
  "tags": "",
  "keywords": "Use Equals To Compare Strings",
  "url": "pmd_rules_java_errorprone.html#useequalstocomparestrings",
  "summary": "Using '==' or '!=' to compare strings is only reliable if the interned string (`String#intern()`)..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UselessOperationOnImmutable (Java, Error Prone)",
  "tags": "",
  "keywords": "Useless Operation On Immutable",
  "url": "pmd_rules_java_errorprone.html#uselessoperationonimmutable",
  "summary": "An operation on an immutable object will not change the object itself since the result..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UselessPureMethodCall (Java, Error Prone)",
  "tags": "",
  "keywords": "Useless Pure Method Call",
  "url": "pmd_rules_java_errorprone.html#uselesspuremethodcall",
  "summary": "This rule detects method calls of pure methods whose result is unused. A pure method..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UseLocaleWithCaseConversions (Java, Error Prone)",
  "tags": "",
  "keywords": "Use Locale With Case Conversions",
  "url": "pmd_rules_java_errorprone.html#uselocalewithcaseconversions",
  "summary": "When doing `String::toLowerCase()/toUpperCase()` conversions, use an explicit locale argument to specify the case transformation rules...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/errorprone.md",
  "title": "UseProperClassLoader (Java, Error Prone)",
  "tags": "",
  "keywords": "Use Proper Class Loader",
  "url": "pmd_rules_java_errorprone.html#useproperclassloader",
  "summary": "In J2EE, the getClassLoader() method might not work as expected. Use\nThread.currentThread().getContextClassLoader() instead."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "Multithreading (Java, Multithreading)",
  "tags": "",
  "keywords": "Multithreading",
  "url": "pmd_rules_java_multithreading.html",
  "summary": "Rules that flag issues when dealing with multiple threads of execution."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "AvoidSynchronizedAtMethodLevel (Java, Multithreading)",
  "tags": "",
  "keywords": "Avoid Synchronized At Method Level",
  "url": "pmd_rules_java_multithreading.html#avoidsynchronizedatmethodlevel",
  "summary": "Method-level synchronization will pin virtual threads and can cause performance problems. Additionally, it can cause..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "AvoidSynchronizedStatement (Java, Multithreading)",
  "tags": "",
  "keywords": "Avoid Synchronized Statement",
  "url": "pmd_rules_java_multithreading.html#avoidsynchronizedstatement",
  "summary": "Synchronization will pin virtual threads and can cause performance problems."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "AvoidThreadGroup (Java, Multithreading)",
  "tags": "",
  "keywords": "Avoid Thread Group",
  "url": "pmd_rules_java_multithreading.html#avoidthreadgroup",
  "summary": "Avoid using java.lang.ThreadGroup; although it is intended to be used in a threaded environment it..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "AvoidUsingVolatile (Java, Multithreading)",
  "tags": "",
  "keywords": "Avoid Using Volatile",
  "url": "pmd_rules_java_multithreading.html#avoidusingvolatile",
  "summary": "Use of the keyword 'volatile' is generally used to fine tune a Java application, and..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "DoNotUseThreads (Java, Multithreading)",
  "tags": "",
  "keywords": "Do Not Use Threads",
  "url": "pmd_rules_java_multithreading.html#donotusethreads",
  "summary": "The J2EE specification explicitly forbids the use of threads. Threads are resources, that should be..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "DontCallThreadRun (Java, Multithreading)",
  "tags": "",
  "keywords": "Dont Call Thread Run",
  "url": "pmd_rules_java_multithreading.html#dontcallthreadrun",
  "summary": "Explicitly calling Thread.run() method will execute in the caller's thread of control. Instead, call Thread.start()..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "DoubleCheckedLocking (Java, Multithreading)",
  "tags": "",
  "keywords": "Double Checked Locking",
  "url": "pmd_rules_java_multithreading.html#doublecheckedlocking",
  "summary": "Partially created objects can be returned by the Double Checked Locking pattern when used in..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "NonThreadSafeSingleton (Java, Multithreading)",
  "tags": "",
  "keywords": "Non Thread Safe Singleton",
  "url": "pmd_rules_java_multithreading.html#nonthreadsafesingleton",
  "summary": "Non-thread safe singletons can result in bad state changes. Eliminate static singletons if possible by..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "UnsynchronizedStaticFormatter (Java, Multithreading)",
  "tags": "",
  "keywords": "Unsynchronized Static Formatter",
  "url": "pmd_rules_java_multithreading.html#unsynchronizedstaticformatter",
  "summary": "Instances of `java.text.Format` are generally not synchronized. Sun recommends using separate format instances for each..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "UseConcurrentHashMap (Java, Multithreading)",
  "tags": "",
  "keywords": "Use Concurrent Hash Map",
  "url": "pmd_rules_java_multithreading.html#useconcurrenthashmap",
  "summary": "Since Java5 brought a new implementation of the Map designed for multi-threaded access, you can..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/multithreading.md",
  "title": "UseNotifyAllInsteadOfNotify (Java, Multithreading)",
  "tags": "",
  "keywords": "Use Notify All Instead Of Notify",
  "url": "pmd_rules_java_multithreading.html#usenotifyallinsteadofnotify",
  "summary": "Thread.notify() awakens a thread monitoring the object. If more than one thread is monitoring, then..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "Performance (Java, Performance)",
  "tags": "",
  "keywords": "Performance",
  "url": "pmd_rules_java_performance.html",
  "summary": "Rules that flag suboptimal code."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "AddEmptyString (Java, Performance)",
  "tags": "",
  "keywords": "Add Empty String",
  "url": "pmd_rules_java_performance.html#addemptystring",
  "summary": "The conversion of literals to strings by concatenating them with empty strings is inefficient. It..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "AppendCharacterWithChar (Java, Performance)",
  "tags": "",
  "keywords": "Append Character With Char",
  "url": "pmd_rules_java_performance.html#appendcharacterwithchar",
  "summary": "Avoid concatenating characters as strings in StringBuffer/StringBuilder.append methods."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "AvoidArrayLoops (Java, Performance)",
  "tags": "",
  "keywords": "Avoid Array Loops",
  "url": "pmd_rules_java_performance.html#avoidarrayloops",
  "summary": "Instead of manually copying data between two arrays, use the more efficient `Arrays.copyOf` or `System.arraycopy`..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "AvoidCalendarDateCreation (Java, Performance)",
  "tags": "",
  "keywords": "Avoid Calendar Date Creation",
  "url": "pmd_rules_java_performance.html#avoidcalendardatecreation",
  "summary": "Problem: `java.util.Calendar` is a heavyweight object and expensive to create. It should only be used,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "AvoidFileStream (Java, Performance)",
  "tags": "",
  "keywords": "Avoid File Stream",
  "url": "pmd_rules_java_performance.html#avoidfilestream",
  "summary": "The FileInputStream and FileOutputStream classes contains a finalizer method which will cause garbage collection pauses...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "AvoidInstantiatingObjectsInLoops (Java, Performance)",
  "tags": "",
  "keywords": "Avoid Instantiating Objects In Loops",
  "url": "pmd_rules_java_performance.html#avoidinstantiatingobjectsinloops",
  "summary": "New objects created within loops should be checked to see if they can created outside..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "BigIntegerInstantiation (Java, Performance)",
  "tags": "",
  "keywords": "Big Integer Instantiation",
  "url": "pmd_rules_java_performance.html#bigintegerinstantiation",
  "summary": "Don't create instances of already existing BigInteger (`BigInteger.ZERO`, `BigInteger.ONE`), for Java 1.5 onwards, BigInteger.TEN and..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "ConsecutiveAppendsShouldReuse (Java, Performance)",
  "tags": "",
  "keywords": "Consecutive Appends Should Reuse",
  "url": "pmd_rules_java_performance.html#consecutiveappendsshouldreuse",
  "summary": "Consecutive calls to StringBuffer/StringBuilder .append should be chained, reusing the target object. This can improve..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "ConsecutiveLiteralAppends (Java, Performance)",
  "tags": "",
  "keywords": "Consecutive Literal Appends",
  "url": "pmd_rules_java_performance.html#consecutiveliteralappends",
  "summary": "Consecutively calling StringBuffer/StringBuilder.append(...) with literals should be avoided. Since the literals are constants, they can..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "InefficientEmptyStringCheck (Java, Performance)",
  "tags": "",
  "keywords": "Inefficient Empty String Check",
  "url": "pmd_rules_java_performance.html#inefficientemptystringcheck",
  "summary": "Checking the length of `string.trim()` or `string.strip()` is an inefficient way to decide if a..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "InefficientStringBuffering (Java, Performance)",
  "tags": "",
  "keywords": "Inefficient String Buffering",
  "url": "pmd_rules_java_performance.html#inefficientstringbuffering",
  "summary": "Avoid concatenating non-literals in a StringBuffer constructor or append() since intermediate buffers will need to..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "InsufficientStringBufferDeclaration (Java, Performance)",
  "tags": "",
  "keywords": "Insufficient String Buffer Declaration",
  "url": "pmd_rules_java_performance.html#insufficientstringbufferdeclaration",
  "summary": "Failing to pre-size a StringBuffer or StringBuilder properly could cause it to re-size many times..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "OptimizableToArrayCall (Java, Performance)",
  "tags": "",
  "keywords": "Optimizable To Array Call",
  "url": "pmd_rules_java_performance.html#optimizabletoarraycall",
  "summary": "Calls to a collection's `toArray(E[])` method should specify a target array of zero size. This..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "RedundantFieldInitializer (Java, Performance)",
  "tags": "",
  "keywords": "Redundant Field Initializer",
  "url": "pmd_rules_java_performance.html#redundantfieldinitializer",
  "summary": "Java will initialize fields with known default values so any explicit initialization of those same..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "StringInstantiation (Java, Performance)",
  "tags": "",
  "keywords": "String Instantiation",
  "url": "pmd_rules_java_performance.html#stringinstantiation",
  "summary": "Avoid instantiating String objects; this is usually unnecessary since they are immutable and can be..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "StringToString (Java, Performance)",
  "tags": "",
  "keywords": "String To String",
  "url": "pmd_rules_java_performance.html#stringtostring",
  "summary": "Avoid calling toString() on objects already known to be string instances; this is unnecessary."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "TooFewBranchesForASwitchStatement (Java, Performance)",
  "tags": "",
  "keywords": "Too Few Branches ForA Switch Statement",
  "url": "pmd_rules_java_performance.html#toofewbranchesforaswitchstatement",
  "summary": "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "TooFewBranchesForSwitch (Java, Performance)",
  "tags": "",
  "keywords": "Too Few Branches For Switch",
  "url": "pmd_rules_java_performance.html#toofewbranchesforswitch",
  "summary": "Switch statements are intended to be used to support complex branching behaviour. Using a switch..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "UseArrayListInsteadOfVector (Java, Performance)",
  "tags": "",
  "keywords": "Use Array List Instead Of Vector",
  "url": "pmd_rules_java_performance.html#usearraylistinsteadofvector",
  "summary": "ArrayList is a much better Collection implementation than Vector if thread-safe operation is not required...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "UseArraysAsList (Java, Performance)",
  "tags": "",
  "keywords": "Use Arrays As List",
  "url": "pmd_rules_java_performance.html#usearraysaslist",
  "summary": "The `java.util.Arrays` class has a `asList()` method that should be used when you want to..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "UseIndexOfChar (Java, Performance)",
  "tags": "",
  "keywords": "Use Index Of Char",
  "url": "pmd_rules_java_performance.html#useindexofchar",
  "summary": "Use String.indexOf(char) when checking for the index of a single character; it executes faster."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "UseIOStreamsWithApacheCommonsFileItem (Java, Performance)",
  "tags": "",
  "keywords": "UseIO Streams With Apache Commons File Item",
  "url": "pmd_rules_java_performance.html#useiostreamswithapachecommonsfileitem",
  "summary": "Problem: Use of [FileItem.get()](https://javadoc.io/static/commons-fileupload/commons-fileupload/1.5/org/apache/commons/fileupload/FileItem.html#get--) and [FileItem.getString()](https://javadoc.io/static/commons-fileupload/commons-fileupload/1.5/org/apache/commons/fileupload/FileItem.html#getString--) could exhaust memory since they load the entire file..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "UselessStringValueOf (Java, Performance)",
  "tags": "",
  "keywords": "Useless String Value Of",
  "url": "pmd_rules_java_performance.html#uselessstringvalueof",
  "summary": "No need to call String.valueOf to append to a string; just use the valueOf() argument..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "UseStringBufferForStringAppends (Java, Performance)",
  "tags": "",
  "keywords": "Use String Buffer For String Appends",
  "url": "pmd_rules_java_performance.html#usestringbufferforstringappends",
  "summary": "The use of the '+=' operator for appending strings causes the JVM to create and..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/performance.md",
  "title": "UseStringBufferLength (Java, Performance)",
  "tags": "",
  "keywords": "Use String Buffer Length",
  "url": "pmd_rules_java_performance.html#usestringbufferlength",
  "summary": "Use StringBuffer.length() to determine StringBuffer length rather than using StringBuffer.toString().equals(&quot;&quot;)\nor StringBuffer.toString().length() == ..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/java/security.md",
  "title": "Security (Java, Security)",
  "tags": "",
  "keywords": "Security",
  "url": "pmd_rules_java_security.html",
  "summary": "Rules that flag potential security flaws."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/security.md",
  "title": "HardCodedCryptoKey (Java, Security)",
  "tags": "",
  "keywords": "Hard Coded Crypto Key",
  "url": "pmd_rules_java_security.html#hardcodedcryptokey",
  "summary": "Do not use hard coded values for cryptographic operations. Please store keys outside of source..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/java/security.md",
  "title": "InsecureCryptoIv (Java, Security)",
  "tags": "",
  "keywords": "Insecure Crypto Iv",
  "url": "pmd_rules_java_security.html#insecurecryptoiv",
  "summary": "Do not use hard coded initialization vector in cryptographic operations. Please use a randomly generated..."
},{
  "type": "page",
  "source": "pages/pmd/rules/jsp.md",
  "title": "Java Server Pages Rules",
  "tags": "rule_referencesjsp",
  "keywords": "",
  "url": "pmd_rules_jsp.html",
  "summary": "Index of all built-in rules available for Java Server Pages"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/jsp/bestpractices.md",
  "title": "Best Practices (Java Server Pages, Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_jsp_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/bestpractices.md",
  "title": "DontNestJsfInJstlIteration (Java Server Pages, Best Practices)",
  "tags": "",
  "keywords": "Dont Nest Jsf In Jstl Iteration",
  "url": "pmd_rules_jsp_bestpractices.html#dontnestjsfinjstliteration",
  "summary": "Do not nest JSF component custom actions inside a custom action that iterates over its..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/bestpractices.md",
  "title": "NoClassAttribute (Java Server Pages, Best Practices)",
  "tags": "",
  "keywords": "No Class Attribute",
  "url": "pmd_rules_jsp_bestpractices.html#noclassattribute",
  "summary": "Do not use an attribute called 'class'. Use &quot;styleclass&quot; for CSS styles."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/bestpractices.md",
  "title": "NoHtmlComments (Java Server Pages, Best Practices)",
  "tags": "",
  "keywords": "No Html Comments",
  "url": "pmd_rules_jsp_bestpractices.html#nohtmlcomments",
  "summary": "In a production system, HTML comments increase the payload between the application server to the..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/bestpractices.md",
  "title": "NoJspForward (Java Server Pages, Best Practices)",
  "tags": "",
  "keywords": "No Jsp Forward",
  "url": "pmd_rules_jsp_bestpractices.html#nojspforward",
  "summary": "Do not do a forward from within a JSP file."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/jsp/codestyle.md",
  "title": "Code Style (Java Server Pages, Code Style)",
  "tags": "",
  "keywords": "Code Style",
  "url": "pmd_rules_jsp_codestyle.html",
  "summary": "Rules which enforce a specific coding style."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/codestyle.md",
  "title": "DuplicateJspImports (Java Server Pages, Code Style)",
  "tags": "",
  "keywords": "Duplicate Jsp Imports",
  "url": "pmd_rules_jsp_codestyle.html#duplicatejspimports",
  "summary": "Avoid duplicate import statements inside JSP's."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/jsp/design.md",
  "title": "Design (Java Server Pages, Design)",
  "tags": "",
  "keywords": "Design",
  "url": "pmd_rules_jsp_design.html",
  "summary": "Rules that help you discover design issues."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/design.md",
  "title": "NoInlineScript (Java Server Pages, Design)",
  "tags": "",
  "keywords": "No Inline Script",
  "url": "pmd_rules_jsp_design.html#noinlinescript",
  "summary": "Avoid inlining HTML script content. Consider externalizing the HTML script using the 'src' attribute on..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/design.md",
  "title": "NoInlineStyleInformation (Java Server Pages, Design)",
  "tags": "",
  "keywords": "No Inline Style Information",
  "url": "pmd_rules_jsp_design.html#noinlinestyleinformation",
  "summary": "Style information should be put in CSS files, not in JSPs. Therefore, don't use &lt;B&gt;..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/design.md",
  "title": "NoLongScripts (Java Server Pages, Design)",
  "tags": "",
  "keywords": "No Long Scripts",
  "url": "pmd_rules_jsp_design.html#nolongscripts",
  "summary": "Scripts should be part of Tag Libraries, rather than part of JSP pages."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/design.md",
  "title": "NoScriptlets (Java Server Pages, Design)",
  "tags": "",
  "keywords": "No Scriptlets",
  "url": "pmd_rules_jsp_design.html#noscriptlets",
  "summary": "Scriptlets should be factored into Tag Libraries or JSP declarations, rather than being part of..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/jsp/errorprone.md",
  "title": "Error Prone (Java Server Pages, Error Prone)",
  "tags": "",
  "keywords": "Error Prone",
  "url": "pmd_rules_jsp_errorprone.html",
  "summary": "Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/errorprone.md",
  "title": "JspEncoding (Java Server Pages, Error Prone)",
  "tags": "",
  "keywords": "Jsp Encoding",
  "url": "pmd_rules_jsp_errorprone.html#jspencoding",
  "summary": "A missing 'meta' tag or page directive will trigger this rule, as well as a..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/jsp/security.md",
  "title": "Security (Java Server Pages, Security)",
  "tags": "",
  "keywords": "Security",
  "url": "pmd_rules_jsp_security.html",
  "summary": "Rules that flag potential security flaws."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/security.md",
  "title": "IframeMissingSrcAttribute (Java Server Pages, Security)",
  "tags": "",
  "keywords": "Iframe Missing Src Attribute",
  "url": "pmd_rules_jsp_security.html#iframemissingsrcattribute",
  "summary": "IFrames which are missing a src element can cause security information popups in IE if..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/jsp/security.md",
  "title": "NoUnsanitizedJSPExpression (Java Server Pages, Security)",
  "tags": "",
  "keywords": "No UnsanitizedJSP Expression",
  "url": "pmd_rules_jsp_security.html#nounsanitizedjspexpression",
  "summary": "Avoid using expressions without escaping / sanitizing. This could lead to cross site scripting -..."
},{
  "type": "page",
  "source": "pages/pmd/rules/kotlin.md",
  "title": "Kotlin Rules",
  "tags": "rule_referenceskotlin",
  "keywords": "",
  "url": "pmd_rules_kotlin.html",
  "summary": "Index of all built-in rules available for Kotlin"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/kotlin/bestpractices.md",
  "title": "Best Practices (Kotlin, Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_kotlin_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/kotlin/bestpractices.md",
  "title": "FunctionNameTooShort (Kotlin, Best Practices)",
  "tags": "",
  "keywords": "Function Name Too Short",
  "url": "pmd_rules_kotlin_bestpractices.html#functionnametooshort",
  "summary": "Function names should be easy to understand and describe the intention. Makes developers happy."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/kotlin/errorprone.md",
  "title": "Error Prone (Kotlin, Error Prone)",
  "tags": "",
  "keywords": "Error Prone",
  "url": "pmd_rules_kotlin_errorprone.html",
  "summary": "Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/kotlin/errorprone.md",
  "title": "OverrideBothEqualsAndHashcode (Kotlin, Error Prone)",
  "tags": "",
  "keywords": "Override Both Equals And Hashcode",
  "url": "pmd_rules_kotlin_errorprone.html#overridebothequalsandhashcode",
  "summary": "Override both public boolean Object.equals(Object other), and public int Object.hashCode(), or override neither. Even if..."
},{
  "type": "page",
  "source": "pages/pmd/rules/modelica.md",
  "title": "Modelica Rules",
  "tags": "rule_referencesmodelica",
  "keywords": "",
  "url": "pmd_rules_modelica.html",
  "summary": "Index of all built-in rules available for Modelica"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/modelica/bestpractices.md",
  "title": "Best Practices (Modelica, Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_modelica_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/modelica/bestpractices.md",
  "title": "AmbiguousResolution (Modelica, Best Practices)",
  "tags": "",
  "keywords": "Ambiguous Resolution",
  "url": "pmd_rules_modelica_bestpractices.html#ambiguousresolution",
  "summary": "There is multiple candidates for this type resolution. While generally this is not an error,..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/modelica/bestpractices.md",
  "title": "ClassStartNameEqualsEndName (Modelica, Best Practices)",
  "tags": "",
  "keywords": "Class Start Name Equals End Name",
  "url": "pmd_rules_modelica_bestpractices.html#classstartnameequalsendname",
  "summary": "Having a class starting with some name and some *different* name in its end clause..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/modelica/bestpractices.md",
  "title": "ConnectUsingNonConnector (Modelica, Best Practices)",
  "tags": "",
  "keywords": "Connect Using Non Connector",
  "url": "pmd_rules_modelica_bestpractices.html#connectusingnonconnector",
  "summary": "Modelica specification requires passing connectors to the `connect` clause, while some implementations tolerate using it..."
},{
  "type": "page",
  "source": "pages/pmd/rules/plsql.md",
  "title": "PLSQL Rules",
  "tags": "rule_referencesplsql",
  "keywords": "",
  "url": "pmd_rules_plsql.html",
  "summary": "Index of all built-in rules available for PLSQL"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/plsql/bestpractices.md",
  "title": "Best Practices (PLSQL, Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_plsql_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/bestpractices.md",
  "title": "TomKytesDespair (PLSQL, Best Practices)",
  "tags": "",
  "keywords": "Tom Kytes Despair",
  "url": "pmd_rules_plsql_bestpractices.html#tomkytesdespair",
  "summary": "&quot;WHEN OTHERS THEN NULL&quot; hides all errors - (Re)RAISE an exception or call RAISE_APPLICATION_ERROR"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/plsql/codestyle.md",
  "title": "Code Style (PLSQL, Code Style)",
  "tags": "",
  "keywords": "Code Style",
  "url": "pmd_rules_plsql_codestyle.html",
  "summary": "Rules which enforce a specific coding style."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/codestyle.md",
  "title": "AvoidTabCharacter (PLSQL, Code Style)",
  "tags": "",
  "keywords": "Avoid Tab Character",
  "url": "pmd_rules_plsql_codestyle.html#avoidtabcharacter",
  "summary": "This rule checks, that there are no tab characters (`\\t`) in the source file. It..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/codestyle.md",
  "title": "CodeFormat (PLSQL, Code Style)",
  "tags": "",
  "keywords": "Code Format",
  "url": "pmd_rules_plsql_codestyle.html#codeformat",
  "summary": "This rule verifies that the PLSQL code is properly formatted. The following checks are executed:..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/codestyle.md",
  "title": "ForLoopNaming (PLSQL, Code Style)",
  "tags": "",
  "keywords": "For Loop Naming",
  "url": "pmd_rules_plsql_codestyle.html#forloopnaming",
  "summary": "In case you have loops please name the loop variables more meaningful."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/codestyle.md",
  "title": "LineLength (PLSQL, Code Style)",
  "tags": "",
  "keywords": "Line Length",
  "url": "pmd_rules_plsql_codestyle.html#linelength",
  "summary": "This rule checks for long lines. Please note that comments are not ignored. This rule..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/codestyle.md",
  "title": "MisplacedPragma (PLSQL, Code Style)",
  "tags": "",
  "keywords": "Misplaced Pragma",
  "url": "pmd_rules_plsql_codestyle.html#misplacedpragma",
  "summary": "Oracle states that the PRAQMA AUTONOMOUS_TRANSACTION must be in the declaration block, but the code..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "Design (PLSQL, Design)",
  "tags": "",
  "keywords": "Design",
  "url": "pmd_rules_plsql_design.html",
  "summary": "Rules that help you discover design issues."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "CyclomaticComplexity (PLSQL, Design)",
  "tags": "",
  "keywords": "Cyclomatic Complexity",
  "url": "pmd_rules_plsql_design.html#cyclomaticcomplexity",
  "summary": "Complexity directly affects maintenance costs is determined by the number of decision points in a..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "ExcessiveMethodLength (PLSQL, Design)",
  "tags": "",
  "keywords": "Excessive Method Length",
  "url": "pmd_rules_plsql_design.html#excessivemethodlength",
  "summary": "When methods are excessively long this usually indicates that the method is doing more than..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "ExcessiveObjectLength (PLSQL, Design)",
  "tags": "",
  "keywords": "Excessive Object Length",
  "url": "pmd_rules_plsql_design.html#excessiveobjectlength",
  "summary": "Excessive object line lengths are usually indications that the object may be burdened with excessive..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "ExcessivePackageBodyLength (PLSQL, Design)",
  "tags": "",
  "keywords": "Excessive Package Body Length",
  "url": "pmd_rules_plsql_design.html#excessivepackagebodylength",
  "summary": "Excessive class file lengths are usually indications that the class may be burdened with excessive..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "ExcessivePackageSpecificationLength (PLSQL, Design)",
  "tags": "",
  "keywords": "Excessive Package Specification Length",
  "url": "pmd_rules_plsql_design.html#excessivepackagespecificationlength",
  "summary": "Excessive class file lengths are usually indications that the class may be burdened with excessive..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "ExcessiveParameterList (PLSQL, Design)",
  "tags": "",
  "keywords": "Excessive Parameter List",
  "url": "pmd_rules_plsql_design.html#excessiveparameterlist",
  "summary": "Methods with numerous parameters are a challenge to maintain, especially if most of them share..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "ExcessiveTypeLength (PLSQL, Design)",
  "tags": "",
  "keywords": "Excessive Type Length",
  "url": "pmd_rules_plsql_design.html#excessivetypelength",
  "summary": "Excessive class file lengths are usually indications that the class may be burdened with excessive..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "NcssCount (PLSQL, Design)",
  "tags": "",
  "keywords": "Ncss Count",
  "url": "pmd_rules_plsql_design.html#ncsscount",
  "summary": "This rule uses the NCSS (Non-Commenting Source Statements) metric to determine the number of lines..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "NcssMethodCount (PLSQL, Design)",
  "tags": "",
  "keywords": "Ncss Method Count",
  "url": "pmd_rules_plsql_design.html#ncssmethodcount",
  "summary": "This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "NcssObjectCount (PLSQL, Design)",
  "tags": "",
  "keywords": "Ncss Object Count",
  "url": "pmd_rules_plsql_design.html#ncssobjectcount",
  "summary": "This rule uses the NCSS (Non-Commenting Source Statements) algorithm to determine the number of lines..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "NPathComplexity (PLSQL, Design)",
  "tags": "",
  "keywords": "N Path Complexity",
  "url": "pmd_rules_plsql_design.html#npathcomplexity",
  "summary": "The NPath complexity of a method is the number of acyclic execution paths through that..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "TooManyFields (PLSQL, Design)",
  "tags": "",
  "keywords": "Too Many Fields",
  "url": "pmd_rules_plsql_design.html#toomanyfields",
  "summary": "Classes that have too many fields can become unwieldy and could be redesigned to have..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/design.md",
  "title": "TooManyMethods (PLSQL, Design)",
  "tags": "",
  "keywords": "Too Many Methods",
  "url": "pmd_rules_plsql_design.html#toomanymethods",
  "summary": "A package or type with too many methods is probably a good suspect for refactoring,..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/plsql/errorprone.md",
  "title": "Error Prone (PLSQL, Error Prone)",
  "tags": "",
  "keywords": "Error Prone",
  "url": "pmd_rules_plsql_errorprone.html",
  "summary": "Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/errorprone.md",
  "title": "TO_DATE_TO_CHAR (PLSQL, Error Prone)",
  "tags": "",
  "keywords": "TO_DATE_TO_CHAR",
  "url": "pmd_rules_plsql_errorprone.html#to_date_to_char",
  "summary": "TO_DATE(TO_CHAR(date-variable)) used to remove time component - use TRUNC(date-variable)"
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/errorprone.md",
  "title": "TO_DATEWithoutDateFormat (PLSQL, Error Prone)",
  "tags": "",
  "keywords": "TO_DATE Without Date Format",
  "url": "pmd_rules_plsql_errorprone.html#to_datewithoutdateformat",
  "summary": "TO_DATE without date format- use TO_DATE(expression, date-format)"
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/plsql/errorprone.md",
  "title": "TO_TIMESTAMPWithoutDateFormat (PLSQL, Error Prone)",
  "tags": "",
  "keywords": "TO_TIMESTAMP Without Date Format",
  "url": "pmd_rules_plsql_errorprone.html#to_timestampwithoutdateformat",
  "summary": "TO_TIMESTAMP without date format- use TO_TIMESTAMP(expression, date-format)"
},{
  "type": "page",
  "source": "pages/pmd/rules/pom.md",
  "title": "Maven POM Rules",
  "tags": "rule_referencespom",
  "keywords": "",
  "url": "pmd_rules_pom.html",
  "summary": "Index of all built-in rules available for Maven POM"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/pom/errorprone.md",
  "title": "Error Prone (Maven POM, Error Prone)",
  "tags": "",
  "keywords": "Error Prone",
  "url": "pmd_rules_pom_errorprone.html",
  "summary": "Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/pom/errorprone.md",
  "title": "InvalidDependencyTypes (Maven POM, Error Prone)",
  "tags": "",
  "keywords": "Invalid Dependency Types",
  "url": "pmd_rules_pom_errorprone.html#invaliddependencytypes",
  "summary": "If you use an invalid dependency type in the dependency management section, Maven doesn't fail...."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/pom/errorprone.md",
  "title": "ProjectVersionAsDependencyVersion (Maven POM, Error Prone)",
  "tags": "",
  "keywords": "Project Version As Dependency Version",
  "url": "pmd_rules_pom_errorprone.html#projectversionasdependencyversion",
  "summary": "Using that expression in dependency declarations seems like a shortcut, but it can go wrong...."
},{
  "type": "page",
  "source": "pages/pmd/rules/scala.md",
  "title": "Scala Rules",
  "tags": "rule_referencesscala",
  "keywords": "",
  "url": "pmd_rules_scala.html",
  "summary": "Index of all built-in rules available for Scala"
},{
  "type": "page",
  "source": "pages/pmd/rules/swift.md",
  "title": "Swift Rules",
  "tags": "rule_referencesswift",
  "keywords": "",
  "url": "pmd_rules_swift.html",
  "summary": "Index of all built-in rules available for Swift"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/swift/bestpractices.md",
  "title": "Best Practices (Swift, Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_swift_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/swift/bestpractices.md",
  "title": "ProhibitedInterfaceBuilder (Swift, Best Practices)",
  "tags": "",
  "keywords": "Prohibited Interface Builder",
  "url": "pmd_rules_swift_bestpractices.html#prohibitedinterfacebuilder",
  "summary": "Creating views using Interface Builder should be avoided. Defining views by code allows the compiler..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/swift/bestpractices.md",
  "title": "UnavailableFunction (Swift, Best Practices)",
  "tags": "",
  "keywords": "Unavailable Function",
  "url": "pmd_rules_swift_bestpractices.html#unavailablefunction",
  "summary": "Due to Objective-C and Swift interoperability some functions are often required to be implemented but..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/swift/errorprone.md",
  "title": "Error Prone (Swift, Error Prone)",
  "tags": "",
  "keywords": "Error Prone",
  "url": "pmd_rules_swift_errorprone.html",
  "summary": "Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/swift/errorprone.md",
  "title": "ForceCast (Swift, Error Prone)",
  "tags": "",
  "keywords": "Force Cast",
  "url": "pmd_rules_swift_errorprone.html#forcecast",
  "summary": "Force casts should be avoided. This may lead to a crash if it's not used..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/swift/errorprone.md",
  "title": "ForceTry (Swift, Error Prone)",
  "tags": "",
  "keywords": "Force Try",
  "url": "pmd_rules_swift_errorprone.html#forcetry",
  "summary": "Force tries should be avoided. If the code being wrapped happens to raise and exception,..."
},{
  "type": "page",
  "source": "pages/pmd/rules/velocity.md",
  "title": "Velocity Template Language (VTL) Rules",
  "tags": "rule_referencesvelocity",
  "keywords": "",
  "url": "pmd_rules_velocity.html",
  "summary": "Index of all built-in rules available for Velocity Template Language (VTL)"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/velocity/bestpractices.md",
  "title": "Best Practices (Velocity Template Language (VTL), Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_velocity_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/velocity/bestpractices.md",
  "title": "AvoidReassigningParameters (Velocity Template Language (VTL), Best Practices)",
  "tags": "",
  "keywords": "Avoid Reassigning Parameters",
  "url": "pmd_rules_velocity_bestpractices.html#avoidreassigningparameters",
  "summary": "Reassigning values to incoming parameters is not recommended.  Use temporary local variables instead."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/velocity/bestpractices.md",
  "title": "UnusedMacroParameter (Velocity Template Language (VTL), Best Practices)",
  "tags": "",
  "keywords": "Unused Macro Parameter",
  "url": "pmd_rules_velocity_bestpractices.html#unusedmacroparameter",
  "summary": "Avoid unused macro parameters. They should be deleted."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/velocity/design.md",
  "title": "Design (Velocity Template Language (VTL), Design)",
  "tags": "",
  "keywords": "Design",
  "url": "pmd_rules_velocity_design.html",
  "summary": "Rules that help you discover design issues."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/velocity/design.md",
  "title": "AvoidDeeplyNestedIfStmts (Velocity Template Language (VTL), Design)",
  "tags": "",
  "keywords": "Avoid Deeply Nested If Stmts",
  "url": "pmd_rules_velocity_design.html#avoiddeeplynestedifstmts",
  "summary": "Avoid creating deeply nested if-then statements since they are harder to read and error-prone to..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/velocity/design.md",
  "title": "CollapsibleIfStatements (Velocity Template Language (VTL), Design)",
  "tags": "",
  "keywords": "Collapsible If Statements",
  "url": "pmd_rules_velocity_design.html#collapsibleifstatements",
  "summary": "Sometimes two consecutive 'if' statements can be consolidated by separating their conditions with a boolean..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/velocity/design.md",
  "title": "ExcessiveTemplateLength (Velocity Template Language (VTL), Design)",
  "tags": "",
  "keywords": "Excessive Template Length",
  "url": "pmd_rules_velocity_design.html#excessivetemplatelength",
  "summary": "The template is too long. It should be broken up into smaller pieces."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/velocity/design.md",
  "title": "NoInlineJavaScript (Velocity Template Language (VTL), Design)",
  "tags": "",
  "keywords": "No Inline Java Script",
  "url": "pmd_rules_velocity_design.html#noinlinejavascript",
  "summary": "Avoid inline JavaScript. Import .js files instead."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/velocity/design.md",
  "title": "NoInlineStyles (Velocity Template Language (VTL), Design)",
  "tags": "",
  "keywords": "No Inline Styles",
  "url": "pmd_rules_velocity_design.html#noinlinestyles",
  "summary": "Avoid inline styles. Use css classes instead."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/velocity/errorprone.md",
  "title": "Error Prone (Velocity Template Language (VTL), Error Prone)",
  "tags": "",
  "keywords": "Error Prone",
  "url": "pmd_rules_velocity_errorprone.html",
  "summary": "Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/velocity/errorprone.md",
  "title": "EmptyForeachStmt (Velocity Template Language (VTL), Error Prone)",
  "tags": "",
  "keywords": "Empty Foreach Stmt",
  "url": "pmd_rules_velocity_errorprone.html#emptyforeachstmt",
  "summary": "Empty foreach statements should be deleted."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/velocity/errorprone.md",
  "title": "EmptyIfStmt (Velocity Template Language (VTL), Error Prone)",
  "tags": "",
  "keywords": "Empty If Stmt",
  "url": "pmd_rules_velocity_errorprone.html#emptyifstmt",
  "summary": "Empty if statements should be deleted."
},{
  "type": "page",
  "source": "pages/pmd/rules/visualforce.md",
  "title": "Salesforce Visualforce Rules",
  "tags": "rule_referencesvisualforce",
  "keywords": "",
  "url": "pmd_rules_visualforce.html",
  "summary": "Index of all built-in rules available for Salesforce Visualforce"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/visualforce/security.md",
  "title": "Security (Salesforce Visualforce, Security)",
  "tags": "",
  "keywords": "Security",
  "url": "pmd_rules_visualforce_security.html",
  "summary": "Rules that flag potential security flaws."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/visualforce/security.md",
  "title": "VfCsrf (Salesforce Visualforce, Security)",
  "tags": "",
  "keywords": "Vf Csrf",
  "url": "pmd_rules_visualforce_security.html#vfcsrf",
  "summary": "Avoid calling VF action upon page load as the action becomes vulnerable to CSRF."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/visualforce/security.md",
  "title": "VfHtmlStyleTagXss (Salesforce Visualforce, Security)",
  "tags": "",
  "keywords": "Vf Html Style Tag Xss",
  "url": "pmd_rules_visualforce_security.html#vfhtmlstyletagxss",
  "summary": "Checks for the correct encoding in `<style/>` tags in Visualforce pages. The rule is based..."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/visualforce/security.md",
  "title": "VfUnescapeEl (Salesforce Visualforce, Security)",
  "tags": "",
  "keywords": "Vf Unescape El",
  "url": "pmd_rules_visualforce_security.html#vfunescapeel",
  "summary": "Avoid unescaped user controlled content in EL as it results in XSS."
},{
  "type": "page",
  "source": "pages/pmd/rules/xml.md",
  "title": "XML Rules",
  "tags": "rule_referencesxml",
  "keywords": "",
  "url": "pmd_rules_xml.html",
  "summary": "Index of all built-in rules available for XML"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/xml/bestpractices.md",
  "title": "Best Practices (XML, Best Practices)",
  "tags": "",
  "keywords": "Best Practices",
  "url": "pmd_rules_xml_bestpractices.html",
  "summary": "Rules which enforce generally accepted best practices."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/xml/bestpractices.md",
  "title": "MissingEncoding (XML, Best Practices)",
  "tags": "",
  "keywords": "Missing Encoding",
  "url": "pmd_rules_xml_bestpractices.html#missingencoding",
  "summary": "When the character encoding is missing from the XML declaration, the parser may produce garbled..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/xml/errorprone.md",
  "title": "Error Prone (XML, Error Prone)",
  "tags": "",
  "keywords": "Error Prone",
  "url": "pmd_rules_xml_errorprone.html",
  "summary": "Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/xml/errorprone.md",
  "title": "MistypedCDATASection (XML, Error Prone)",
  "tags": "",
  "keywords": "MistypedCDATA Section",
  "url": "pmd_rules_xml_errorprone.html#mistypedcdatasection",
  "summary": "An XML CDATA section begins with a &lt;![CDATA[ marker, which has only one [, and..."
},{
  "type": "page",
  "source": "pages/pmd/rules/xsl.md",
  "title": "XSL Rules",
  "tags": "rule_referencesxsl",
  "keywords": "",
  "url": "pmd_rules_xsl.html",
  "summary": "Index of all built-in rules available for XSL"
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/xsl/codestyle.md",
  "title": "Code Style (XSL, Code Style)",
  "tags": "",
  "keywords": "Code Style",
  "url": "pmd_rules_xsl_codestyle.html",
  "summary": "Rules which enforce a specific coding style."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/xsl/codestyle.md",
  "title": "UseConcatOnce (XSL, Code Style)",
  "tags": "",
  "keywords": "Use Concat Once",
  "url": "pmd_rules_xsl_codestyle.html#useconcatonce",
  "summary": "The XPath concat() functions accepts as many arguments as required so you can have &quot;concat($a,'b',$c)&quot;..."
},{
  "type": "ruledoc ruleset",
  "source": "pages/pmd/rules/xsl/performance.md",
  "title": "Performance (XSL, Performance)",
  "tags": "",
  "keywords": "Performance",
  "url": "pmd_rules_xsl_performance.html",
  "summary": "Rules that flag suboptimal code."
},{
  "type": "ruledoc",
  "source": "pages/pmd/rules/xsl/performance.md",
  "title": "AvoidAxisNavigation (XSL, Performance)",
  "tags": "",
  "keywords": "Avoid Axis Navigation",
  "url": "pmd_rules_xsl_performance.html#avoidaxisnavigation",
  "summary": "Avoid using the 'following' or 'preceding' axes whenever possible, as these can cut through 100%..."
},{
  "type": "page",
  "source": "pages/pmd/userdocs/3rdpartyrulesets.md",
  "title": "3rd party rulesets",
  "tags": "rule_referencesuserdocs",
  "keywords": "",
  "url": "pmd_userdocs_3rdpartyrulesets.html",
  "summary": "Lists rulesets and rules from the community"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/best_practices.md",
  "title": "Best Practices",
  "tags": "userdocs",
  "keywords": "",
  "url": "pmd_userdocs_best_practices.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/userdocs/cli_reference.md",
  "title": "PMD CLI reference",
  "tags": "userdocs",
  "keywords": "commandlineoptionshelpformatsrenderers",
  "url": "pmd_userdocs_cli_reference.html",
  "summary": "Full reference for PMD's command-line interface, including options, output formats and supported languages"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/configuring_rules.md",
  "title": "Configuring rules",
  "tags": "userdocsgetting_started",
  "keywords": "propertypropertiesmessagepriority",
  "url": "pmd_userdocs_configuring_rules.html",
  "summary": "Learn how to configure your rules directly from the ruleset XML."
},{
  "type": "page",
  "source": "pages/pmd/userdocs/cpd/cpd.md",
  "title": "Finding duplicated code with CPD",
  "tags": "cpduserdocs",
  "keywords": "",
  "url": "pmd_userdocs_cpd.html",
  "summary": "Learn how to use CPD, the copy-paste detector shipped with PMD."
},{
  "type": "page",
  "source": "pages/pmd/userdocs/cpd/cpd_report_formats.md",
  "title": "Report formats for CPD",
  "tags": "cpduserdocs",
  "keywords": "formatsrenderers",
  "url": "pmd_userdocs_cpd_report_formats.html",
  "summary": "Overview of the built-in report formats for CPD"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/extending/ast_dump.md",
  "title": "Creating XML dump of the AST",
  "tags": "userdocs",
  "keywords": "",
  "url": "pmd_userdocs_extending_ast_dump.html",
  "summary": "Creating a XML representation of the AST allows to analyze the AST with other tools."
},{
  "type": "page",
  "source": "pages/pmd/userdocs/extending/defining_properties.md",
  "title": "Defining rule properties",
  "tags": "extendinguserdocs",
  "keywords": "",
  "url": "pmd_userdocs_extending_defining_properties.html",
  "summary": "Learn how to define your own properties both for Java and XPath rules."
},{
  "type": "page",
  "source": "pages/pmd/userdocs/extending/designer_reference.md",
  "title": "The rule designer",
  "tags": "extendinguserdocs",
  "keywords": "",
  "url": "pmd_userdocs_extending_designer_reference.html",
  "summary": "Learn about the usage and features of the rule designer."
},{
  "type": "page",
  "source": "pages/pmd/userdocs/extending/rule_guidelines.md",
  "title": "Rule guidelines",
  "tags": "extendinguserdocs",
  "keywords": "",
  "url": "pmd_userdocs_extending_rule_guidelines.html",
  "summary": "Rule Guidelines, or the last touches to a rule"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/extending/testing.md",
  "title": "Testing your rules",
  "tags": "extendinguserdocs",
  "keywords": "",
  "url": "pmd_userdocs_extending_testing.html",
  "summary": "Learn how to use PMD's simple test framework for unit testing rules."
},{
  "type": "page",
  "source": "pages/pmd/userdocs/extending/writing_java_rules.md",
  "title": "Writing a custom rule",
  "tags": "extendinguserdocs",
  "keywords": "",
  "url": "pmd_userdocs_extending_writing_java_rules.html",
  "summary": "Learn how to write a custom rule for PMD"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/extending/writing_pmd_rules.md",
  "title": "Writing a custom rule",
  "tags": "extendinguserdocs",
  "keywords": "",
  "url": "pmd_userdocs_extending_writing_pmd_rules.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/userdocs/extending/writing_rules_intro.md",
  "title": "Introduction to writing PMD rules",
  "tags": "extendinguserdocsgetting_started",
  "keywords": "",
  "url": "pmd_userdocs_extending_writing_rules_intro.html",
  "summary": "Writing your own PMD rules"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/extending/writing_xpath_rules.md",
  "title": "Writing XPath rules",
  "tags": "extendinguserdocs",
  "keywords": "",
  "url": "pmd_userdocs_extending_writing_xpath_rules.html",
  "summary": "This page describes XPath rule support in more details"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/extending/your_first_rule.md",
  "title": "Your first rule",
  "tags": "extendinguserdocs",
  "keywords": "",
  "url": "pmd_userdocs_extending_your_first_rule.html",
  "summary": "Introduction to rule writing through an example for a XPath rule."
},{
  "type": "page",
  "source": "pages/pmd/userdocs/incremental_analysis.md",
  "title": "Incremental Analysis",
  "tags": "userdocs",
  "keywords": "pmdoptionscommandincrementalanalysisperformance",
  "url": "pmd_userdocs_incremental_analysis.html",
  "summary": "Explains how to use incremental analysis to speed up analysis"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/installation.md",
  "title": "Installation and basic CLI usage",
  "tags": "getting_starteduserdocs",
  "keywords": "pmdcpdoptionscommandauxclasspath",
  "url": "pmd_userdocs_installation.html",
  "summary": "Sums up the first steps to set up a CLI installation and get started using PMD"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/making_rulesets.md",
  "title": "Making rulesets",
  "tags": "getting_starteduserdocs",
  "keywords": "rulesetsreferenceruleexcludeincludepatternfilter",
  "url": "pmd_userdocs_making_rulesets.html",
  "summary": "A ruleset is an XML configuration file, which describes a collection of rules to be executed in a PMD run. PMD includes built-in rulesets to run quick analyses with a default configuration, but users are encouraged to make their own rulesets from the start, because they allow for so much configurability. This page walk you through the creation of a ruleset and the multiple configuration features offered by rulesets."
},{
  "type": "page",
  "source": "pages/pmd/userdocs/migrating_to_pmd7.md",
  "title": "Migration Guide for PMD 7",
  "tags": "pmduserdocs",
  "keywords": "",
  "url": "pmd_userdocs_migrating_to_pmd7.html",
  "summary": "Migrating to PMD 7 from PMD 6.x"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/pmd_report_formats.md",
  "title": "Report formats for PMD",
  "tags": "pmduserdocs",
  "keywords": "formatsrenderers",
  "url": "pmd_userdocs_report_formats.html",
  "summary": "Overview of the built-in report formats for PMD"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/signed_releases.md",
  "title": "Signed Releases",
  "tags": "userdocs",
  "keywords": "",
  "url": "pmd_userdocs_signed_releases.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/userdocs/suppressing_warnings.md",
  "title": "Suppressing warnings",
  "tags": "userdocs",
  "keywords": "suppressingwarningssuppresswarningsnopmdviolationSuppressXPathviolationSuppressRegex",
  "url": "pmd_userdocs_suppressing_warnings.html",
  "summary": "Learn how to suppress some rule violations, from the source code using annotations or comments, or globally from the ruleset"
},{
  "type": "page",
  "source": "pages/pmd/userdocs/tools/ant.md",
  "title": "Ant Task Usage",
  "tags": "userdocstools",
  "keywords": "",
  "url": "pmd_userdocs_tools_ant.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/userdocs/tools/bld.md",
  "title": "bld PMD Extension",
  "tags": "userdocstools",
  "keywords": "",
  "url": "pmd_userdocs_tools_bld.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/userdocs/tools/ci.md",
  "title": "Continuous Integrations plugins",
  "tags": "userdocstools",
  "keywords": "",
  "url": "pmd_userdocs_tools_ci.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/userdocs/tools/gradle.md",
  "title": "Gradle",
  "tags": "userdocstools",
  "keywords": "",
  "url": "pmd_userdocs_tools_gradle.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/userdocs/tools/ide-plugins.md",
  "title": "IDE Plugins",
  "tags": "userdocstools",
  "keywords": "",
  "url": "pmd_userdocs_tools_ide_plugins.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/userdocs/tools/java-api.md",
  "title": "PMD Java API",
  "tags": "userdocstools",
  "keywords": "",
  "url": "pmd_userdocs_tools_java_api.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/userdocs/tools/maven.md",
  "title": "Maven PMD Plugin",
  "tags": "userdocstools",
  "keywords": "",
  "url": "pmd_userdocs_tools_maven.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/pmd/userdocs/tools/tools.md",
  "title": "Tools / Integrations",
  "tags": "userdocstools",
  "keywords": "",
  "url": "pmd_userdocs_tools.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/release_notes.md",
  "title": "PMD Release Notes",
  "tags": "",
  "keywords": "changelog, release notes",
  "url": "pmd_release_notes.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/release_notes_old.md",
  "title": "Old Release Notes",
  "tags": "",
  "keywords": "",
  "url": "pmd_release_notes_old.html",
  "summary": " "
},{
  "type": "page",
  "source": "pages/release_notes_pmd7.md",
  "title": "Detailed Release Notes for PMD 7",
  "tags": "",
  "keywords": "changelog, release notes",
  "url": "pmd_release_notes_pmd7.html",
  "summary": "These are the detailed release notes for PMD 7."
}
];


    // Initialize jekyll search in topnav.
    SimpleJekyllSearch({
        searchInput: document.getElementById('search-input'),
        resultsContainer: document.getElementById('results-container'),
        json: pmd_doc_search_index,
        searchResultTemplate: '<li><a href="{url}"><strong>{title}</strong><br>{summary}</a></li>',
        noResultsText: '<li>No results found.</li>',
        limit: 20,
        fuzzy: false,
    });
    // Make sure to close and empty the search results after clicking one result item.
    // This is necessary, if we don't switch the page but only jump to a anchor on the
    // same page.
    document.getElementById('results-container').addEventListener('click', e => {
        document.getElementById('search-input').value = '';
        e.target.innerHTML = '';
    });
    // simple keyboard control of search results
    document.querySelectorAll('#search-input, body').forEach(element => {
        element.addEventListener('keyup', e => {
            if (e.key !== 'ArrowDown' && e.key !== 'ArrowUp') {
                return;
            }
            if (document.querySelectorAll('#results-container li').length === 0) {
                return;
            }

            let current = document.querySelector('#results-container li.selected');
            if (!current) {
                current = document.querySelector('#results-container li');
            } else {
                current.classList.remove('selected');
                if (e.key === 'ArrowDown') {
                    if (current.nextSibling != null) {
                        current = current.nextSibling;
                    }
                } else if (e.key === 'ArrowUp') {
                    if (current.previousSibling !== null) {
                        current = current.previousSibling;
                    }
                }
            }
            current.classList.add('selected');
            current.querySelector('a').focus();
            e.preventDefault();
            e.stopImmediatePropagation(); // avoid triggering another search and rerender the results
        });
    });
    document.getElementById('results-container').addEventListener('mouseover', e => {
        let selected = document.getElementById('results-container').querySelector('li.selected')
        if (selected) {
            selected.classList.remove('selected');
        }
        let newSelected = e.target.closest('li');
        if (newSelected) {
            newSelected.classList.add('selected');
            if (document.activeElement !== document.getElementById('search-input')) {
                newSelected.querySelector('a').focus();
            }
        }
    });
    document.body.addEventListener('keyup', e => {
        if (e.key === 's') {
            document.getElementById('search-input').focus();
        }
        if (e.key === 'Escape') {
            document.getElementById('results-container').innerHTML = '';
        }
    });
    document.body.addEventListener('click', e => {
        const resultsContainer = document.getElementById('results-container');
        if (resultsContainer.querySelectorAll('li').length > 0) {
            resultsContainer.innerHTML = '';
        }
    });

});
