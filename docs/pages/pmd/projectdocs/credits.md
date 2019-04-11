---
title: Credits
permalink: pmd_projectdocs_credits.html
author: Tom Copeland <tom@infoether.org>
---

## Committers

*   David Dixon-Peugh - PMD core, much of the early work on the grammar, initial Emacs plugin
*   Philippe Herlin - Eclipse plugin, fixed bugs in RuleSetFactory
*   Nascif Abousalh Neto - Emacs plugin
*   [Tom Copeland](http://tomcopeland.blogs.com/) - PMD core, lead developer, JDeveloper plugin, initial Gel plugin,
    initial jEdit plugin, IDEAJ integration, BlueJ extension
*   Jiger Patel - jEdit plugin
*   Alan Ezust - jEdit plugin
*   Ole-Martin Mork - NetBeans plugin
*   Miguel Griffa - PMD core, over a dozen rules, lots of documentation, and other improvements all over the place
*   Allan Caplan - PMD core, six rules, lots of bugfixes and improvements to the PMD core
*   Radim Kubacki - Netbeans plugin, OptimizableToArrayCallRule suggestion, bug reports
*   Tomasz Slota - Netbeans plugin
*   Andrey Lumyanski - Gel plugin
*   Johan Nagels - PMD core, JSP support
*   Brian Remedios - PMD core, properties subsystem, lots of CPD UI improvements, Eclipse plugin improvements
*   Xavier Le Vourch - PMD core, numerous bug fixes, UselessStringValueOf, UnnecessaryWrapperObjectCreation,
    SimplifyBooleanAssertion
*   Sven Jacob - DFA subsystem, Eclipse plugin
*   Wouter Zelle - Lots of bugfixes and cleanups, JUnit test XML design, introduced java.util.logging, BrokenNullCheck,
    NonThreadSafeSingleton, DefaultPackage rule, UselessOverridingMethod, ProperLogger, AvoidPrintStackTrace,
    SimplifyConditional
*   Ryan Gustafson - PMD core, assists with Eclipse plugin
*   Torsten Kleiber - JDeveloper plugin
*   Romain Pelisse - Various bugfix patches, restructured CPD tokenizers, patch to remove redundant rule,
    added Fortran tokenizer, DoNotExtendJavaLangError, JspEncoding, MDBAndSessionBeanNamingConvention,
    RemoteSessionInterfaceNamingConvention, LocalInterfaceSessionNamingConvention, LocalHomeNamingConvention,
    RemoteInterfaceNamingConvention, AvoidFinalLocalVariable, ClassWithOnlyPrivateConstructorsShouldBeFinal,
    TooManyStaticImports, DoNotCallSystemExit, StaticEJBFieldShouldBeFinal

## Committers emeritus

*   Gunnlaugur Thor Briem - NetBeans plugin, Maven build script fixes, bug report on JavaCC parser's use
    of java.lang.Error
*   [David Craine](http://dcraine.blogspot.com/) - JBuilder plugin
*   Tom Burke - Eclipse plugin
*   Alex Chaffee - various bugfixes and features
*   Siegfried Goeschl - original Maven plugin, various bugfixes and features
*   Don Leckie - The PMD GUI
*   [Rich Kilmer](http://richkilmer.blogs.com/) - logo design
*   Paul Kendall - various bugfixes and features
*   Colin Wilson-Salt - NetBeans plugin team
*   [Brant Gurganus](http://gurganus.name/brant/) - JCreator integration, Swing GUI work

## Significant contributors

*   Pieter Vanraemdonck - JSP grammar/integration/documentation, DontNestJsfInJstlIteration, NoLongScripts,
    NoScriptlets, NoInlineStyleInformation, NoClassAttribute, NoJspForward
*   Raik Schroeder - data flow analysis layer, YAHTMLRenderer
*   Steve Hawkins - rewrite of CPD based on Karp-Rabin string matching
*   Daniel Sheppard - XPath engine integration concept and implementation, advice on Jaxen extension function naming
*   Brian Ewins - complete rewrite of CPD based on the Burrows-Wheeler transform, fixed DocumentNavigator bug

## Contributors

*   Andy Throgmorton - New XPath getCommentOn function, new rule DontCallThreadRun, fix for rule UseArraysAsList
*   Nicolas Dordet - Fixed an issue on CloseResource
*   Juan Jesús García de Soria - Rework CPD algorithm
*   Sergey Pariev - Fixed an ugly ArrayIndexOutOfBoundsException in CPD for Ruby
*   Chris Heister - Reported and noted proper fix for bug in IDEAJ renderer operations
*   Ralf Wagner - Reported bug in UselessOperationOnImmutable, reported and noted proper fix for broken XSLT
*   Caroline Rioux - Reported bug in ImmutableField
*   Miroslav Šulc - Reported bug in CloneMethodMustImplementCloneable
*   Thomas Steininger - Noticed redundant rule
*   Thomas Leplus - Contributed new rules LogicInversion, ExtendsObject, UselessParentheses, EmptyInitializer,
    EmptyStatementBlock, CheckSkipResult.Rewrote UselessStringValueOf, nice patch for ClassCastExceptionWithToArray
*   Paul Sundling - A nice documentation patch for ruleset links
*   Matt Koch - Added more detail to PMD XML report
*   Richard Hands - Fixed CPD symlink confusion
*   Oleg Skrypnyuk - reported a Java 1.5 grammar bug
*   Jeff Campbell - Found bug and suggested fix for problem with XMLRenderer and SuppressWarnings("PMD") annotations
*   Kris Jurka - CPD patch to accept ".C" as a filename extension for C/C++ files
*   Florian Deissenboeck - reported several Java 1.5 grammar bugs
*   Maarten ter Huurne - BooleanGetMethodName, AddEmptyString, Noticed misspelling in AvoidArrayLoops rule
*   Lukas Theussl - Patch to bring Maven configuration files up to date
*   Jason Bennett - Rewrite of annotation-based warning suppression to allow for rule-specific suppression,
    noticed useless line in XSLT scripts, fix for UnnecessaryLocalBeforeReturn, wrote NPathComplexity rule,
    patches to improve CyclomaticComplexity rule, Implemented: UseCollectionIsEmpty, NcssTypeCount, NcssMethodCount,
    NcssConstructor, Patch to detect comparison with new Object
*   Brent Fisher - Fixed report backslash bug, SummaryHTML report improvements
*   Larry Brigman - Reported symlink bug in CPD
*   Harald Rohan - Reported bug in CPD GUI
*   Stephan Classens - Patch for file closing bug, noted missing varargs setting in ASTFormalParameter
*   piair - Implemented StringBufferInstantiationWithChar, AvoidUsingOctalValues
*   Christopher Eagan - Reported bug in VariableNamingConventions
*   [Fabio Insaccanebbia](http://www.livejournal.com/users/insac/) - Improvement for UseArraysAsList,
    UnusedNullCheckInEquals, MisplacedNullCheck, UselessOperationOnImmutable, AvoidArrayLoops, UseArraysAsList,
    AvoidConstantsInterface, AvoidDecimalLiteralsInBigDecimalConstructor, ClassCastExceptionWithToArray,
    BigIntegerInstantiation
*   Stefan Seidel - Reported Java 1.5 parsing bug
*   Aaron Optimizer Digulla - Tweaks to pmd.bat
*   Peter Van de Voorde - Rewrote the 'create rule XML' functionality in the designer utility
*   Josh Devins - Reported bug with annotation parsing
*   Alan Berg - Reported bug in Ant task
*   George Thomas - Wrote AvoidRethrowingException rule, new AvoidLosingExceptionInformation rule
*   Robert Simmons - Reported bug in optimizations package along with suggestions for fix
*   Brian Remedios - display cleanup of CPD GUI, code cleanup of StringUtil and various rules,
    cleanup of rule designer, code cleanup of net.sourceforge.pmd.ant.Formatter.java,
    code improvements to Eclipse plugin, created AbstractPoorMethodCall and refactored UseIndexOfChar
*   Max Tardiveau - A nice XML to HTML stylesheet for CPD.
*   Ernst Reissner - reported IdempotentOperations bug, reported CloneThrowsCloneNotSupportedException bug,
    reported Java 1.5 parsing bug, suggested InstantiationToGetClass, bug reports for
    UnusedPrivateField/CloseConnectionRule/ConstructorCallsOverridableMethodRule,
    and bug report and documentation suggestions for UseSingletonRule
*   Maarten Coene - bug report for UnnecessaryConversionTemporary
*   Jorn Stampehl - Reported bug in UnusedModifier, reported and fixed bugs in
    JUnitTestsShouldContainAsserts/CyclomaticComplexity/TooManyFields, noticed redundancy of ExplicitCallToFinalize,
    reported bug in AvoidCallingFinalize, reported bug in JUnitAssertionsShouldIncludeMessage,
    reported bug in bug report on JUnitTestsShouldContainAsserts
*   Ulrich Kriegel - reported Ant task documentation bug
*   Jarkko Hietaniemin - rewrote most of cpd.sh, many C grammar improvements, several CPD documentation suggestions,
    noted missing CPD scripts in binary release
*   Adam Zell - Reported bug in UselessOverridingMethod
*   Daniel Serodio - Reported bug in ExceptionSignatureDeclaration
*   John Redford - Reported bug in AvoidProtectedFieldInFinalClass
*   D'Arcy Smith - Reported bug in UncommentedEmptyConstructor, reported missing RuleViolation methods
*   Paul Field - Fixed bug in MissingBreakInSwitch, reported a bug in DontImportJavaLang
*   Attila Korompai - A nice patch to add messages to the NOPMD feature
*   Levent Gurses - Suggested JSP support for the copy/paste detector
*   Neil Cafferkey - Reported a typo in AssignmentInOperand
*   Noel Grandin - bug report for ImmutableField, bug report for MissingStaticMethodInNonInstantiatableClass,
    bug report for MissingBreakInSwitch, EqualsNull rule, bug report for IfElseStmtsMustUseBracesRule
*   Olaf Heimburger - wrote the UseProperClassLoader rule, code changes to get JDeveloper plugin working
    under JDev 10.1.3 EA, reported a possible NPE in ReportTree
*   Mohammad Farooq - Reported new JavaNCSS URL
*   Jeff Jensen - Reported missing XML schema references in documentation, wrote new XML schema, reported missing
    schema refs in example rulesets, suggested posting XML schema on PMD site, discussion of
    'comments in catch block' feature, suggested description attribute in property element
*   Christopher Stach - bug report for VariableNamingConventions, bug report for CallSuperInConstructor,
    many bug reports for rules that didn't handle Java 1.5 constructs
*   Matthew Harrah - noticed missing element in UseCorrectExceptionLogging definition, script bug report
*   Mike Kaufman - Reported abug in UnnecessaryCaseChange
*   [Elliotte Rusty Harold](http://www.cafeaulait.org/) - reported bug in UseAssertSameInsteadOfAssertTrue,
    suggested creating a new ruleset containing rules in each release, UncommentedEmptyConstructor suggestions,
    noted missed case for UnusedFormalParameter, documentation suggestions,
    reported mistake in UnnecessaryLocalBeforeReturn message,
    bug report 1371757 for misleading AvoidSynchronizedAtMethodLevel example,
    bug report 1293277 for duplicated rule messages, bug report for ConstructorCallsOverridableMethod,
    suggestion for improving command line interface, misspelling report, suggestion for improving Designer
    startup script, "how to make a ruleset" documentation suggestions, noticed outdated Xerces jars,
    script renaming suggestions, UseLocaleWithCaseConversions rule suggestion
*   David Karr - reported stale XPath documentation
*   Dawid Weiss - Reported bug in UnusedPrivateMethod
*   Shao Lo - Reported bug in CPD
*   Mathieu Champlon - Added language support to the CPD Ant task
*   Uroshnor - Reported bug in UseNotifyAllInsteadOfNotify
*   Jan Koops - Noted missing data in MemberValuePair nodes, bug report for JBuilder plugin
*   [Will Sargent](http://tersesystems.com/) - Implemented AvoidThreadGroup, AvoidThrowingCertainExceptionTypesRule,
    AvoidCatchingNPERule, ExceptionAsFlowControlRule, URL updates for 'Similar projects' page
*   Benoit Xhenseval - noted Maven plugin bug (http://jira.codehaus.org/browse/MPPMD-24),
    bug report for UnusedPrivateMethod, suggestion to add elapsed time to XML report,
    bug report for ImmutableField, many bug reports (with good failure cases!), Ant task patch and bug report,
    XSLT patch, suggestion for improving XML report
*   Barak Naveh - Reported and fixed bug in CallSuperInConstructor
*   Bhatia Saurabh - Reported a grammar bug, reported a bug in UseStringBufferLength
*   Chris Erskine - found bad link, documentation suggestions
*   mhilpert - Reported bugs in UseIndexOfChar and LoggerIsNotStaticFinal
*   David Corley - Priority filtering XSLT, reported release packaging problem, implemented nifty
    Javascript folding for XML report, [demo is here](http://tomcopeland.blogs.com/juniordeveloper/2005/12/demo_of_some_ni.html),
    suggestion for min priority on the command line
*   Jon Doh - Reported parser bug
*   Brian R - suggestions for improving UseIndexOfChar, documentation suggestion
*   Didier Duquennoy - bug reports for InefficientStringBuffering/ConsecutiveLiteralAppends/AppendCharacterWithChar,
    several bug reports for InefficientStringBuffering, bug report for ImmutableField, suggestions for
    improving Benchmark utility, bug report for InefficientStringBuffering, bug report for
    AvoidConcateningNonLiteralsInStringBuffer, reported a missed hit for EqualsNull, bug report for
    MissingStaticMethodInNonInstantiatableClass, pmd-netbeans feedback
*   Paul Smith - patch to fix Ant task 'minimum priority' attribute
*   Erik Thauvin - reported IDEA integration problem
*   John Kenny - reported bug in ConsecutiveLiteralAppends
*   Tom Judge - patch for fix to C/C++ multiline literal support for CPD, patch for including .cc files in
    C++ CPD checks, patch for JDK compatibility problems
*   Sean Mountcastle - reported documentation bug
*   Greg Broderick - provided patch for 'minimum priority' support
*   George Sexton - Bug report 1379701 for CompareObjectsWithEquals, suggested new rule for Additional String
    Concatenation Warnings in StringBuffer.
*   Johan Stuyts - improvements to UncommentedEmptyConstructor, nice patch for UncommentedEmptyConstructor and
    UncommentedEmptyMethod, patch to allow empty catch blocks with comments in them, patch to clean up build environment
*   Bruce Kelly - bug report 1378358 for StringInstantiation, bug report 1376756 for UselessOverridingMethod,
    bug report 1376760 for InefficientStringBuffering
*   Isaac Babsky - tweak for pmd.bat
*   Hendrik Maryns - reported bug 1375290 for SuppressWarnings facility
*   Wim Deblauwe - suggested UseAssertNullInsteadOfAssertTrue,
    bug report 1373510 for UseAssertSameInsteadOfAssertTrue, suggested putting property names/values in generated docs,
    UselessOverridingMethod, reported bug in JUnitTestsShouldContainAsserts,
    front page and "how to make a ruleset" patches, noted problems with web site rule index,
    bug report for JUnitTestsShouldContainAsserts, Clover license coordination and implementation,
    UseCorrectExceptionLogging, coordinated and coded a much nicer asXML() implementation,
    suggested cleanup of UnusedFormalParameter, Javadoc patch, SystemPrintln bug report,
    helped get Ant task and CLI squared away with Java 1.5 params, Java 1.5-specific bug reports,
    suggested improvements for ExceptionSignatureDeclaration
*   Sean Montgomery - bug report 1371980 for InefficientStringBuffering
*   Jean-Marc Vanel - suggested enhancements to the PMD scoreboard
*   Andriy Rozeluk - suggested UseStringBufferLength, bug report 1306180 for
    AvoidConcatenatingNonLiteralsInStringBuffer, reported bug 1293157 for UnusedPrivateMethod,
    suggested UnnecessaryCaseChange, bug report for SimplifyConditional, suggested UnnecessaryLocalBeforeReturn,
    suggestions for improving BooleanInstantiation, UnnecessaryReturn, AvoidDuplicateLiterals RFEs and bug reports,
    various other RFEs and thoughtful discussions as well
*   Bruno Juillet - suggested reporting suppressed warnings, bug report for missing package/class/method names,
    patch for Ant task's excludeMarker attribute, bug report on ruleset overrides
*   Derek Hofmann - suggestion for adding --skip-duplicate-files option for CPD, bug report for CPD skipping header
    files when in C/C++ mode
*   Mark Holczhammer - bug report for InefficientStringBuffering
*   Raja Rajan - 2 bug reports for CompareObjectswithEquals
*   Jeff Chamblee - suggested better message for UnnecessaryCaseChange, bug report for CompareObjectsWithEquals
*   Dave Brosius - suggested MisleadingVariableName rule, a couple of nice patches to clean up some string handling
    inefficiencies, non-static class usages, and unclosed streams/readers - found with Findbugs, I daresay :-)
*   Chris Grindstaff - fixed SWTException when Eclipse plugin is run on a file with syntax error
*   Eduard Naum - fixed JDK 1.3 runtime problems in Eclipse plugin
*   Jacques Lebourgeois - fix for UTF8 characters in Eclipse plugin
*   dvholten - suggestions for improving OverrideBothEqualsAndHashcode, formatting suggestions for HTML report,
    test cases for ConstructorCallsOverridableMethod, reported several NullAssignment bugs
*   Brian Duff - helped get Oracle JDeveloper plugin working
*   Sivakumar Mambakkam - bug report 1314086 for missing name in SimpleRuleSetNameMapper
*   Rodrigo Ruiz - bug report 1312723 for FieldDeclaration nodes inside interfaces, bug report 1312754 for
    pmd.bat option handling, bug report 1312720 (and code fix!) for DefaultPackage, bug report 1309235 for TooManyFields
*   Lori Olson - JBuilder plugin suggestions and prerelease tests,
    found copy/paste bug in rule descriptions
*   Thomas Dudziak - bug report 1304739 for StringInstantiation
*   Pieter Bloemendaal - reported JDK 1.3 parsing bug 1292609, command line docs bug report,
    bug report for UnusedPrivateMethod, found typo in ArrayIsStoredDirectly, bug report for
    AvoidReassigningParametersRule
*   shawn2005 - documentation bug report
*   Andrew Taylor - bug report for StringInstantiation
*   S. David Pullara - bug report for AvoidConcateningNonLiteralsInStringBuffer, bug report for ImmutableField
*   Maarten Bodewes - bug report for ImmutableField
*   Peter Frandsen - PackageCase rule, NoPackage rule
*   Noureddine Bekrar - French translation of some PMD documentation
* Martin Jost - bug report for JDeveloper plugin
*   Guillaume Boudreau - patches to fix problems with CPD's FileFinder and NTFS and SCCS
*   Sylvain Veyrie - bug report for MethodReturnsInternalArray
*   Randy Ray - bug report for ArrayIsStoredDirectly
*   Klaus - Suggestion for improving UseSingleton
*   Nicolai Czempin - Bug report for UnnecessaryParentheses, various rule suggestions, additional PMD backronyms
*   Kevin Routley - reported Ant task dependency problem, reported problems with RuleSetFactory unit tests
*   Dennis Klemann - noted that errors were missing from text report, reported Java 1.5 parsing bug with
    ExceptionSignatureDeclaration, reported fix for pmd.bat problem
*   Tor Norbye - Suggested CompareObjectsWithEquals
*   Thomas Skariah - bug reports for MethodArgumentCouldBeFinal and AvoidReassigningParameters
*   Tom Parker - bug report for MethodReturnsInternalArray, found missed case in NullAssignment, suggested addition
    to UnnecessaryBooleanAssertion, suggested splitting up AvoidThrowingCertainExceptionTypes,
    AvoidInstantiatingObjectsInLoops bug report, AtLeastOneConstructor bug report
*   Ian Flanigan - reported CPD JNLP breakage
*   Glen Cordrey - Reported bug involved JavaCC string handling
*   Oto 'tapik' Buchta - Patched XMLRenderer for UTF8 support
*   Arent-Jan Banck - Reported bug with Java 1.5 annotation handling
*   Fred Hartman - Reported exact location of bug in TooManyFields, fixed bug in UnnecessaryBooleanAssertion
*   Andreas Ehn - Reported bug with Java 1.5 generics parsing
*   Eric Olander - SingularField, SimplifyConditional fix, UseStringBufferForStringAppends, CollapsibleIfStatements,
    AvoidInstanceofChecksInCatchClause, AssignmentToNonFinalStatic rule, nice patch for DFAPanel cleanup, AvoidProtectedFieldInFinalClass, ImmutableFieldRule, noticed missing image in Postfix nodes
*   [Tomas Gustavsson](http://sourceforge.net/users/anatom/) - reported pmd-web breakage
*   Payal Subhash - Tweaks to CSVRenderer
*   Christophe Mourette - Reported JDK 1.3 problem with XMLRenderer
*   Alex Givant - caught documentation bug
*   [Luke Francl](http://justlooking.recursion.org/) - suggested UnnecessaryParentheses rule, numerous **high quality**
    feature suggestions and bug reports
*   David Hovemeyer - reported missing labelled stmt images, a nice patch to let PMD process code in jar/zip files
*   Peter 'Bruno' Kofler - reported bug #1146116 for JUnitTestsShouldIncludeAssert
*   Zev Blut - nice patch to add Ruby support to CPD
*   Christopher Judd - a nice patch to the XSLT that adds a summary
*   John Meagher - suggested the rule 'MissingSerialVersionUID' and provided the implementation for it
*   John Austin - patch to fix mispeling in Eclipse plugin message
*   Paddy Fagan - reported bug in StatisticalRule
*   Leszek Migdal - reported documentation mistake for Eclipse plugin
*   Hakan Civelek - an order-of-magnitude optimization to the SystemOutPrintln rule
*   John Heintz - Added "any language" support to CPD.
*   Harald Gurres - cleaned up the symbol table code with a very nice patch
*   Matthias Kerkhoff - CPD suggestions, several bug reports
*   Chris Riesbeck - identified some dead code in RuleSet
*   Lars Gregori - reported a bug in the Ant task docs
*   Todd Wright - reported bug in EmptyStatementNotInLoop, XPath port of
    AtLeastOneConstructorRule, ConfusingTernaryExpression rule, reported missing ASTUnaryExpressionNotPlusMinus nodes
*   [Conrad Roche](http://derupe.blogspot.com/) - UnusedModifier bug report, other bug reports
*   Mike Thome - NOPMD implementation concept, BadComparisonRule suggestion
*   Ken Foskey - C++ parser bug report, cpd.sh
*   ehowe - a nice patch to include rule priority in the XML report
*   aryanto - reported a broken reference in the 'favorites' ruleset.
*   Archimedes Trajano - suggested SimpleDateFormatNeedsLocale
*   Joerg Kurt Wegner - bug report for UnusedLocalVariable
*   Bruno - Reported bug with TooManyFields, SuspiciousEqualsMethodName
*   Philippe Couton - bug report for ExceptionAsFlowControl, OverrideBothEqualsAndHashcodeRule bug report,
    UseSingletonRule improvements, JUnitStaticSuiteRule improvements
*   Paul Rowe - suggestion for improving MethodWithSameNameAsEnclosingClass, bug reports for
    SimplifyBooleanExpressions and UnusedLocalVariable
*   Enno Derksen - enhancements to VariableNamingConventionsRule
*   Michael Haggerty - bug reports for FinalizeDoesNotCallSuperFinalize and UnusedModifier
*   Phil Shaw - documentation suggestions
*   Sreenivasa Viswanadha - reminded me to use BufferedInputStreams, grammar cleanup for Ctrl-Z problem
*   Austin Moore - Integration with Omnicore's CodeGuide IDE
*   Matt Inger - CloneMethodMustImplementCloneable, CloneThrowsCloneNotSupportedException
*   Morgan Schweers - Javascript highlighter for the PMD scoreboard
*   Brandon Franklin - bug report for BeanMembersShouldSerializeRule, many PMD scoreboard ideas
*   Bertrand Mollinier Toublet - Bug report which led to platform character set encoding enhancement
*   Choi Ki Soo - Found bug in XMLRenderer
*   [Gero Wedemann](http://www.fh-stralsund.de/mitarbeiter/powerslave,id,264,nodeid,75.html) - Found bug in
    RuleSetFactory XPath message variable substitution
*   Adrian Papari - Wrote the PapariTextRenderer
*   Curt Cox -   some additions to the 'Similar Projects' page
*   Michael Griffel - bug fix for XMLRenderer
*   Doug Tillman - correction to finalizers.xml examples
*   Luis Alberto Domínguez Ruiz - bug report for IfElseStmtsMustUseBracesRule
*   Chad Loder - SuspiciousOctalEscapeRule, EmptyStatementNotInLoop, SuspiciousHashcodeMethodName,
    NonCaseLabelInSwitchStatement, DefaultLabelNotLastInSwitchStmt, NonStaticInitializer, ExplicitCallToFinalize,
    MethodWithSameNameAsEnclosingClassRuleTest, FinalizeDoesNotCallSuperFinalize, FinalizeOverloaded,
    FinalizeOnlyCallsSuperFinalize, UnconditionalIfStatement, AvoidDollarSigns, EmptyStaticInitializer,
    EmptyFinalizerMethod rule, DontImportSun rule, improvements to ASTBooleanLiteral
*   Maik Schreiber - AccessNode bug report, other bug reports
*   Lokesh Gupta - improvements to the AST viewer
*   [Jesse Glick](http://www.oreillynet.com/cs/catalog/view/au/960?x-t=book.view) - improvements to
    VariableNamingConventionsRule, patch for UnusedModifierRule, bug fix for VariableNameDeclarations rule,
    an excellent discussion on the UnnecessaryConstructorRule
*   Nicolas Liochon - CloneShouldCallSuperCloneRule implementation
*   [Slava Pestov](http://factor-language.blogspot.com/) -  Suggestions for jEdit plugin enhancements.
*   Olivier Mengué - Diagnosed and patched XML report character encoding problems
*   Hariolf Häfele - PMD-JDeveloper plugin bug reports
*   Vladimir Bossicard - suggested AbstractNamingRule, test package
    organization suggestions, VBHTMLRenderer, numerous feature requests and bug reports, several rule suggestions
    derived from [JUnit-Addons](http://junit-addons.sf.net/), evangelism :-)
*   Ken Foskey - noticed bad link
*   [Stephan Janssen](http://www.bejug.org/confluenceBeJUG/display/BeJUG/Stephan+Janssen) -
    promoted PMD for [JJGuidelines](http://web.archive.org/web/20070701124257/https://jjguidelines.dev.java.net/)
*   Ron Sidi - bug reports
*   David Koontz - suggestions for tweaking PMD command line options
*   Jeff Epstein - TextPad integration and tests
*   Gabe Johnson - CloseConnectionRule
*   Roelof Vuurboom - posted report of QStudio analysis of PMD
*   Jeff Anderson - node finding utility code
*   Boris Gruschko - regression test suites, nifty AST/XPath viewer
*   Trevor Harmon - rewrote XSLT script
*   [Vadim Nasardinov](http://philip.greenspun.com/shared/community-member?user_id=174176) - xdocs cleanup,
    run.sh cleanup
*   Sigiswald Madou - bug report
*   Dan Tullis - bug report
*   George Menhorn - CPD bug reports and suggestions
*   Paul Roebuck - Ant build improvement, several bug reports
*   Jon A. Maxwell - Bug report
*   Erik Lee - Bug report
*   Joerg K. Wegner - PMD scoreboard suggestions
*   Chris Webster - fix (and test) for UnnecessaryConstructorRule, BooleanInstantiation rule
*   Colin Simmonds - detailed bug reports
*   [Trond Andersen](http://reassess.blogspot.com/) - AvoidCatchingThrowable, ExceptionSignatureDeclaration,
    ExceptionTypeChecking
*   Bernd Jansen - grammer modification
*   Jarle Naess - bug report
*   Jeff Anderson - VariableNamingConventionsRule, MethodNamingConventionsRule, ClassNamingConventionsRule
*   Frank van Puffelen - documentation suggestions
*   mcclain looney - patch for CPD GUI, bug reports
*   Ralf Hauser - Various documentation suggestions, cygwin-run.sh
*   Pablo Casado - Bug report for UseSingletonRule
*   Frank Hardisty - BeanMembersShouldSerializeRule
*   Randall Schulz - bug report for LooseCouplingRule
*   Wim Bervoets - bug report for the PMD Ant task
*   Niels Peter Strandberg - various tweaks to the PMD Swing UI
*   Ian Shef - documentation updates, bug reports on the Gel IDE plugin
*   Astro Jetson Jr - a tweak for the ShortVariableNameRule
*   Paul King - a complete rewrite of the Gel plugin
*   Gael Marziou - "exclude" rule feature request, bug reports
*   Philippe T'Seyen - refactoring and cleanup of the CPD Ant task, an XML renderer (with unit tests!) for CPD
*   Michael Montuori - bug reports on the Gel IDE plugin
*   Michael Hosier - bug reports on the Gel IDE plugin
*   Richard Jenson - CPD on Win32 troubleshooting
*   Daniel Bruguier - CPD on Win32 troubleshooting
*   Mario Claerhout - CPD optimizations and suggestions
*   Sameer Nanda - CPD Ant task bug report
*   Nanne Baars - grammar suggestions, rule suggestions
*   Adam Nemeth - bug fixes for UnnecessaryConstructorRule
*   [Andrew Glover](http://www.oreillynet.com/pub/au/1425) - the CPDTask, ExcessivePublicCountRule,
    CouplingBetweenObjectsRule, ExcessiveImportsRule, documentation tweaks
*   Robert Leland - bug report
*   Carl Gilbert - AccessorClassGenerationRule, DoubleCheckedLockingRule, ConstructorCallsOverridableMethodRule,
    bug reports, feature requests, and documentation improvements
*   Dave Fuller - improved resource loading code (packaged in a nice diff, too!)
*   David Whitmore - parser bug report
*   David Campbell - detailed bugs reports, Ant task refactoring, documentation tweaks
*   Michael Sutherland - bug report in IfStmtMustUseBracesRule
*   Egon Willighagen - PMD scoreboard suggestion
*   Adam Nemeth - bug report on missing final attribute for local variable declarations
*   Frederic Harper - bug report and subsequent troubleshooting
*   [Mats Henricson](http://www.henricson.se/mats/) - an XSLT script and several bug reports
*   Martin Cooper - feature suggestions
*   Bruce Mayhew - feedback on the jEdit plugin
*   Juergen Ebert - feature suggestions and pmd-netbeans feedback
*   J.D. Fagan - feature suggestions
*   William McArthur - ForLoopShouldBeWhileLoop rule
*   Ales Bukovsky - pmd-netbeans feedback
*   [Stefan Bodewig](http://stefan.samaflost.de/blog) - bug report
*   Sean Sullivan - rule suggestions
*   Dale Vissar - rule suggestions
*   [Alina Copeland](http://www.informatik.uni-trier.de/~ley/db/indices/a-tree/c/Copeland:Alina.html) -
    PMD scoreboard formulas, pmd-dcpd optimizations
*   Vincent Massol - bug reports, design suggestions, feature suggestions, Maven guidance
*   Peter Donald - design suggestions
*   Liam Holohan - bug reports
*   Ralph Schaer - bug reports and verification
*   Damian O'Neill - Ant task patches
*   Sebastian Raffel - Great job on the Eclipse PMD perspective, new views and dataflow analysis support
*   Ebu - Eclipse smoothed icons
*   Jacques Lebourgeois - Eclipse fix malformed UTF-8 characters
*   Chris Grindstaff - Eclipse fix SWTException when PMD is run on a file with syntax error
*   jmichelberger - wrote Byte/Short/Long Instantiation migration rules
*   Edwin Chan - Support for -auxclasspath for use with Type Resolution
*   Jared Bunting - Patch to add ASTAnnotationMethodDeclaration to Java AST
*   Lucian Ciufudean - RedundantFieldInitializerRule
*   Andreas Dangel - GodClass and LawOfDemeter rules, several bugfixes and cleanup
*   Riku Nykanen - patch improving TooManyMethods rule
*   Tammo van Lessen - new rule GuardDebugLogging for Jakarta Commons Logging ruleset.
*   Steven Christou - patch improving DoNotCallSystemExit rule
*   Cd-Man - patch to improve CPD performance
*   Suresh - new rule DontUseFloatTypeForLoopIndices
*   Dinesh Bolkensteyn and SonarSource - Java 7 grammar support
*   Tom Wheeler - contribute a launch script for CPD GUI
*   Remi Delmas - change CPD CLI to return a non null value when code duplication is found.
*   Victor Bucutea - Improved JSP parser to be less strict with not valid XML documents (like HTML).
*   Prabhjot Singh - Fixed bug 3484404: Invalid NPath calculation in return statement.
*   Roman - Fixed bug 3546093: Type resolution very slow for big project.
*   Florian Bauer - Add C# support for CPD.
*   Matthew Short - Support in CPD for IgnoreAnnotations and SuppressWarnings("CPD-START").
*   Simon Gijsen - contributing a PMD logo with a modern look.
*   Yiannis Paschalidis - Fixed bug #968 Issues with JUnit4 @Test annotation with expected exception
*   Jaroslav Snajberk - Make the comment required rule working.
*   Mat Booth - #1109 Patch to build with Javacc 5.0
*   Stuart Turton - for PLSQL support. See also [pldoc](http://pldoc.sourceforge.net/)
*   Andrey Utis - for adding Apache Velocity as a new language and writing up a
    [howto for adding new languages](pmd_devdocs_major_adding_new_language.html).
*   Alan Hohn - for adding Standard and modified cyclomatic complexity rules
*   Jan van Nunen - for adding CPD support for Matlab, Objective-C, Python, Scala and various bug fixes
*   Juan Martín Sotuyo Dodero - for many bugfixes/pull requests improving Java grammar and performance

## Organizations

<table>
    <tr>
        <td><center><img src="images/credits/MD_logo4c_120x120.png"/></center></td>
        <td>
            <a href="https://www.microdoc.com">MicroDoc</a> for sponsoring PMD development.
            MicroDoc is a software business serving an international customer base. Since 1991 MicroDoc
            has grown into a technology oriented software engineering and professional services company.
            Our focus on complex software technology and software infrastructure made us a well
            respected partner for large corporations and even for other software businesses.
        </td>
    </tr>
    <tr>
        <td><center><img src="images/credits/ae-logo.gif"/></center></td>
        <td>
            <a href="http://www.ae.be/">AE</a> for the JSP integration and especially for writing the JSP grammar.
        </td>
    </tr>
    <tr>
        <td><center><img src="images/credits/darpa.jpg"/></center></td>
        <td>
            <a href="http://www.darpa.mil/">DARPA</a> for funding
            the Ultra*Log and <a href="http://cougaar.org/">Cougaar</a>
            effort which spawned PMD.
        </td>
    </tr>
    <tr>
        <td><center><img src="http://sourceforge.net/sflogo.php?group_id=56262"/></center></td>
        <td>
            <a href="http://sourceforge.net/">SourceForge</a> for providing hosting services for PMD.
        </td>
    </tr>
    <tr>
        <td><center><img src="images/credits/refactorit_logo.gif"/></center></td>
        <td>
            <a href="http://www.refactorit.com/">RefactorIT</a> for letting
            their software be used free-of-charge on PMD code
            (<a href="http://www.refactorit.com/index.html?id=649">OpenSource
            Community License</a>)
        </td>
    </tr>
    <tr>
        <td><center><img src="images/credits/qasystems_logo.jpg"/></center></td>
        <td>
            <a href="http://www.qa-systems.com/products/qstudioforjava/">QA-Systems</a> for sending in some
            handy utilities for PMD and shipping PMD inside their QStudio product
        </td>
    </tr>
    <tr>
        <td></td>
        <td>
            <a href="http://www.vanwardtechnologies.com/products.php">Vanward Technologies</a> for
            using PMD inside their Convergence product
        </td>
    </tr>
    <tr>
        <td><center><img src="images/credits/atlassian-cenqua-logo.png"/></center></td>
        <td>
            <a href="http://www.cenqua.com/">Cenqua</a> for
            giving us a free Clover license and doing a nice <a href="http://fisheye3.atlassian.com/browse/pmd">FishEye</a> run.
        </td>
    </tr>
    <tr>
        <td><center><img src="images/credits/yjp.gif"/></center></td>
        <td>
            YourKit is kindly supporting open source projects with its full-featured Java Profiler.
            YourKit, LLC is creator of innovative and intelligent tools for profiling
            Java and .NET applications. Take a look at YourKit's leading software products:
            <a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and
            <a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.
        </td>
    </tr>
    <tr>
        <td><center><img src="images/credits/bb-pmd.png"/></center></td>
        <td>
            <a href="http://www.bijzonderbezig.nl/">Bijzonder Bezig</a> for giving the PMD logo a modern look.
        </td>
    </tr>
</table>
