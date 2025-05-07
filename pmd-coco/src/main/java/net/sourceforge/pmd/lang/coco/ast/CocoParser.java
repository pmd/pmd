/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// CPD-OFF
// CHECKSTYLE:OFF
// Generated from net/sourceforge/pmd/lang/coco/ast/Coco.g4 by ANTLR 4.9.3
package net.sourceforge.pmd.lang.coco.ast;

import java.util.List;

import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * @deprecated Since 7.8.0. This class was never intended to be generated. It will be removed with no replacement.
 */
@Deprecated
@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public class CocoParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		AFTER=1, AS=2, ASSERT=3, ATTRIBUTE=4, BECOME=5, BREAK=6, CASE=7, COMPONENT=8, 
		CONTINUE=9, ELSE=10, ENUM=11, ENTRY=12, EXECUTION=13, EXIT=14, EXTERNAL=15, 
		FINAL=16, FOR=17, FUNCTION=18, IF=19, ILLEGAL=20, IMPORT=21, IN=22, INIT=23, 
		INSTANCE=24, MACHINE=25, MATCH=26, MUT=27, MUTATING=28, NONDET=29, OFFER=30, 
		OPTIONAL=31, OTHERWISE=32, OUT=33, OUTGOING=34, PERIODIC=35, PORT=36, 
		PRIVATE=37, RETURN=38, SIGNAL=39, SPONTANEOUS=40, STATE=41, STATIC=42, 
		STRUCT=43, TYPE=44, UNQUALIFIED=45, VAL=46, VAR=47, WHERE=48, WHILE=49, 
		IDENTIFIER=50, AT=51, ASSIGN=52, COLON=53, LP=54, RP=55, LC=56, RC=57, 
		LB=58, RB=59, COMMA=60, SEMI=61, DOT=62, LT=63, GT=64, MUL=65, DIV=66, 
		MINUS=67, MOD=68, PLUS=69, IMPL=70, ARROW=71, AMP=72, QM=73, PIPE=74, 
		EXCL=75, ELLIP=76, EQ=77, NE=78, OR=79, AND=80, LE=81, GE=82, WHITESPACE=83, 
		NEWLINE=84, LINE_COMMENT=85, BLOCK_COMMENT=86, INTEGER=87, BACKTICK_LITERAL=88, 
		CHAR_LITERAL=89, STRING_LITERAL=90;
	public static final int
		RULE_module = 0, RULE_declaration = 1, RULE_attribute = 2, RULE_attributeDeclaration = 3, 
		RULE_importDeclaration = 4, RULE_variableDeclaration = 5, RULE_enumDeclaration = 6, 
		RULE_structDeclaration = 7, RULE_typeAliasDeclaration = 8, RULE_functionDeclaration = 9, 
		RULE_instanceDeclaration = 10, RULE_portDeclaration = 11, RULE_componentDeclaration = 12, 
		RULE_externalConstantDeclaration = 13, RULE_externalTypeDeclaration = 14, 
		RULE_externalTypeElement = 15, RULE_externalFunctionDeclaration = 16, 
		RULE_genericTypeDeclaration = 17, RULE_genericTypes = 18, RULE_genericType = 19, 
		RULE_enumElement = 20, RULE_enumCase = 21, RULE_caseParameters = 22, RULE_caseParameter = 23, 
		RULE_structElement = 24, RULE_fieldDeclaration = 25, RULE_componentElement = 26, 
		RULE_staticMemberDeclaration = 27, RULE_constructorDeclaration = 28, RULE_expression = 29, 
		RULE_blockExpression_ = 30, RULE_ifExpression_ = 31, RULE_matchExpression_ = 32, 
		RULE_nondetExpression_ = 33, RULE_fieldAssignments = 34, RULE_fieldAssignment = 35, 
		RULE_nondetClauses = 36, RULE_nondetClause = 37, RULE_matchClauses = 38, 
		RULE_matchClause = 39, RULE_pattern = 40, RULE_enumCasePattern = 41, RULE_idParameterPatterns = 42, 
		RULE_idParameterPattern = 43, RULE_variableDeclarationPattern = 44, RULE_parameterPatterns = 45, 
		RULE_parameterPattern = 46, RULE_expressions = 47, RULE_statement = 48, 
		RULE_declarationStatement = 49, RULE_returnStatement = 50, RULE_becomeStatement = 51, 
		RULE_whileStatement = 52, RULE_forStatement = 53, RULE_breakStatement = 54, 
		RULE_continueStatement = 55, RULE_portElement = 56, RULE_functionInterfaceDeclaration = 57, 
		RULE_signalDeclaration = 58, RULE_stateMachineDeclaration = 59, RULE_stateMachineElement = 60, 
		RULE_stateDeclaration = 61, RULE_eventStateDeclaration = 62, RULE_executionStateDeclaration = 63, 
		RULE_eventStateElement = 64, RULE_entryFunctionDeclaration = 65, RULE_exitFunctionDeclaration = 66, 
		RULE_stateInvariant = 67, RULE_transitionDeclaration = 68, RULE_eventTransition = 69, 
		RULE_eventSource = 70, RULE_spontaneousTransition = 71, RULE_timerTransition = 72, 
		RULE_eventHandler = 73, RULE_offer = 74, RULE_offerClauses = 75, RULE_offerClause = 76, 
		RULE_parameters = 77, RULE_parameter = 78, RULE_literalExpression_ = 79, 
		RULE_type = 80, RULE_types = 81, RULE_dotIdentifierList = 82;
	private static String[] makeRuleNames() {
		return new String[] {
			"module", "declaration", "attribute", "attributeDeclaration", "importDeclaration", 
			"variableDeclaration", "enumDeclaration", "structDeclaration", "typeAliasDeclaration", 
			"functionDeclaration", "instanceDeclaration", "portDeclaration", "componentDeclaration", 
			"externalConstantDeclaration", "externalTypeDeclaration", "externalTypeElement", 
			"externalFunctionDeclaration", "genericTypeDeclaration", "genericTypes", 
			"genericType", "enumElement", "enumCase", "caseParameters", "caseParameter", 
			"structElement", "fieldDeclaration", "componentElement", "staticMemberDeclaration", 
			"constructorDeclaration", "expression", "blockExpression_", "ifExpression_", 
			"matchExpression_", "nondetExpression_", "fieldAssignments", "fieldAssignment", 
			"nondetClauses", "nondetClause", "matchClauses", "matchClause", "pattern", 
			"enumCasePattern", "idParameterPatterns", "idParameterPattern", "variableDeclarationPattern", 
			"parameterPatterns", "parameterPattern", "expressions", "statement", 
			"declarationStatement", "returnStatement", "becomeStatement", "whileStatement", 
			"forStatement", "breakStatement", "continueStatement", "portElement", 
			"functionInterfaceDeclaration", "signalDeclaration", "stateMachineDeclaration", 
			"stateMachineElement", "stateDeclaration", "eventStateDeclaration", "executionStateDeclaration", 
			"eventStateElement", "entryFunctionDeclaration", "exitFunctionDeclaration", 
			"stateInvariant", "transitionDeclaration", "eventTransition", "eventSource", 
			"spontaneousTransition", "timerTransition", "eventHandler", "offer", 
			"offerClauses", "offerClause", "parameters", "parameter", "literalExpression_", 
			"type", "types", "dotIdentifierList"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'after'", "'as'", "'assert'", "'attribute'", "'become'", "'break'", 
			"'case'", "'component'", "'continue'", "'else'", "'enum'", "'entry'", 
			"'execution'", "'exit'", "'external'", "'final'", "'for'", "'function'", 
			"'if'", "'illegal'", "'import'", "'in'", "'init'", "'instance'", "'machine'", 
			"'match'", "'mut'", "'mutating'", "'nondet'", "'offer'", "'optional'", 
			"'otherwise'", "'out'", "'outgoing'", "'periodic'", "'port'", "'private'", 
			"'return'", "'signal'", "'spontaneous'", "'state'", "'static'", "'struct'", 
			"'type'", "'unqualified'", "'val'", "'var'", "'where'", "'while'", null, 
			"'@'", "'='", "':'", "'('", "')'", "'{'", "'}'", "'['", "']'", "','", 
			"';'", "'.'", "'<'", "'>'", "'*'", "'/'", "'-'", "'%'", "'+'", "'=>'", 
			"'->'", "'&'", "'?'", "'|'", "'!'", "'...'", "'=='", "'!='", "'||'", 
			"'&&'", "'<='", "'>='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "AFTER", "AS", "ASSERT", "ATTRIBUTE", "BECOME", "BREAK", "CASE", 
			"COMPONENT", "CONTINUE", "ELSE", "ENUM", "ENTRY", "EXECUTION", "EXIT", 
			"EXTERNAL", "FINAL", "FOR", "FUNCTION", "IF", "ILLEGAL", "IMPORT", "IN", 
			"INIT", "INSTANCE", "MACHINE", "MATCH", "MUT", "MUTATING", "NONDET", 
			"OFFER", "OPTIONAL", "OTHERWISE", "OUT", "OUTGOING", "PERIODIC", "PORT", 
			"PRIVATE", "RETURN", "SIGNAL", "SPONTANEOUS", "STATE", "STATIC", "STRUCT", 
			"TYPE", "UNQUALIFIED", "VAL", "VAR", "WHERE", "WHILE", "IDENTIFIER", 
			"AT", "ASSIGN", "COLON", "LP", "RP", "LC", "RC", "LB", "RB", "COMMA", 
			"SEMI", "DOT", "LT", "GT", "MUL", "DIV", "MINUS", "MOD", "PLUS", "IMPL", 
			"ARROW", "AMP", "QM", "PIPE", "EXCL", "ELLIP", "EQ", "NE", "OR", "AND", 
			"LE", "GE", "WHITESPACE", "NEWLINE", "LINE_COMMENT", "BLOCK_COMMENT", 
			"INTEGER", "BACKTICK_LITERAL", "CHAR_LITERAL", "STRING_LITERAL"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Coco.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CocoParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ModuleContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(CocoParser.EOF, 0); }
		public List<DeclarationContext> declaration() {
			return getRuleContexts(DeclarationContext.class);
		}
		public DeclarationContext declaration(int i) {
			return getRuleContext(DeclarationContext.class,i);
		}
		public ModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitModule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitModule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleContext module() throws RecognitionException {
		ModuleContext _localctx = new ModuleContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_module);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ATTRIBUTE) | (1L << COMPONENT) | (1L << ENUM) | (1L << EXTERNAL) | (1L << FUNCTION) | (1L << IMPORT) | (1L << INSTANCE) | (1L << PORT) | (1L << PRIVATE) | (1L << STRUCT) | (1L << TYPE) | (1L << VAL) | (1L << VAR) | (1L << AT))) != 0)) {
				{
				{
				setState(166);
				declaration();
				}
				}
				setState(171);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(172);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class DeclarationContext extends ParserRuleContext {
		public ImportDeclarationContext importDeclaration() {
			return getRuleContext(ImportDeclarationContext.class,0);
		}
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public EnumDeclarationContext enumDeclaration() {
			return getRuleContext(EnumDeclarationContext.class,0);
		}
		public StructDeclarationContext structDeclaration() {
			return getRuleContext(StructDeclarationContext.class,0);
		}
		public TypeAliasDeclarationContext typeAliasDeclaration() {
			return getRuleContext(TypeAliasDeclarationContext.class,0);
		}
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public InstanceDeclarationContext instanceDeclaration() {
			return getRuleContext(InstanceDeclarationContext.class,0);
		}
		public PortDeclarationContext portDeclaration() {
			return getRuleContext(PortDeclarationContext.class,0);
		}
		public ComponentDeclarationContext componentDeclaration() {
			return getRuleContext(ComponentDeclarationContext.class,0);
		}
		public ExternalConstantDeclarationContext externalConstantDeclaration() {
			return getRuleContext(ExternalConstantDeclarationContext.class,0);
		}
		public ExternalFunctionDeclarationContext externalFunctionDeclaration() {
			return getRuleContext(ExternalFunctionDeclarationContext.class,0);
		}
		public ExternalTypeDeclarationContext externalTypeDeclaration() {
			return getRuleContext(ExternalTypeDeclarationContext.class,0);
		}
		public AttributeDeclarationContext attributeDeclaration() {
			return getRuleContext(AttributeDeclarationContext.class,0);
		}
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(174);
				attribute();
				}
				}
				setState(179);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(193);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(180);
				importDeclaration();
				}
				break;
			case 2:
				{
				setState(181);
				variableDeclaration();
				}
				break;
			case 3:
				{
				setState(182);
				enumDeclaration();
				}
				break;
			case 4:
				{
				setState(183);
				structDeclaration();
				}
				break;
			case 5:
				{
				setState(184);
				typeAliasDeclaration();
				}
				break;
			case 6:
				{
				setState(185);
				functionDeclaration();
				}
				break;
			case 7:
				{
				setState(186);
				instanceDeclaration();
				}
				break;
			case 8:
				{
				setState(187);
				portDeclaration();
				}
				break;
			case 9:
				{
				setState(188);
				componentDeclaration();
				}
				break;
			case 10:
				{
				setState(189);
				externalConstantDeclaration();
				}
				break;
			case 11:
				{
				setState(190);
				externalFunctionDeclaration();
				}
				break;
			case 12:
				{
				setState(191);
				externalTypeDeclaration();
				}
				break;
			case 13:
				{
				setState(192);
				attributeDeclaration();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class AttributeContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(CocoParser.AT, 0); }
		public DotIdentifierListContext dotIdentifierList() {
			return getRuleContext(DotIdentifierListContext.class,0);
		}
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public ExpressionsContext expressions() {
			return getRuleContext(ExpressionsContext.class,0);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitAttribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_attribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			match(AT);
			setState(196);
			dotIdentifierList();
			setState(201);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(197);
				match(LP);
				setState(198);
				expressions();
				setState(199);
				match(RP);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class AttributeDeclarationContext extends ParserRuleContext {
		public TerminalNode ATTRIBUTE() { return getToken(CocoParser.ATTRIBUTE, 0); }
		public TerminalNode AT() { return getToken(CocoParser.AT, 0); }
		public DotIdentifierListContext dotIdentifierList() {
			return getRuleContext(DotIdentifierListContext.class,0);
		}
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public AttributeDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterAttributeDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitAttributeDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitAttributeDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeDeclarationContext attributeDeclaration() throws RecognitionException {
		AttributeDeclarationContext _localctx = new AttributeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_attributeDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			match(ATTRIBUTE);
			setState(204);
			match(AT);
			setState(205);
			dotIdentifierList();
			setState(211);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LP) {
				{
				setState(206);
				match(LP);
				setState(208);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR || _la==IDENTIFIER) {
					{
					setState(207);
					parameters();
					}
				}

				setState(210);
				match(RP);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ImportDeclarationContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(CocoParser.IMPORT, 0); }
		public DotIdentifierListContext dotIdentifierList() {
			return getRuleContext(DotIdentifierListContext.class,0);
		}
		public TerminalNode UNQUALIFIED() { return getToken(CocoParser.UNQUALIFIED, 0); }
		public TerminalNode AS() { return getToken(CocoParser.AS, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public ImportDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterImportDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitImportDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitImportDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportDeclarationContext importDeclaration() throws RecognitionException {
		ImportDeclarationContext _localctx = new ImportDeclarationContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_importDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(IMPORT);
			setState(215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==UNQUALIFIED) {
				{
				setState(214);
				match(UNQUALIFIED);
				}
			}

			setState(217);
			dotIdentifierList();
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(218);
				match(AS);
				setState(219);
				match(IDENTIFIER);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class VariableDeclarationContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode VAR() { return getToken(CocoParser.VAR, 0); }
		public TerminalNode VAL() { return getToken(CocoParser.VAL, 0); }
		public TerminalNode PRIVATE() { return getToken(CocoParser.PRIVATE, 0); }
		public GenericTypeDeclarationContext genericTypeDeclaration() {
			return getRuleContext(GenericTypeDeclarationContext.class,0);
		}
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitVariableDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitVariableDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
		VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_variableDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PRIVATE) {
				{
				setState(222);
				match(PRIVATE);
				}
			}

			setState(225);
			_la = _input.LA(1);
			if ( !(_la==VAL || _la==VAR) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(226);
			match(IDENTIFIER);
			setState(228);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(227);
				genericTypeDeclaration();
				}
			}

			setState(232);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(230);
				match(COLON);
				setState(231);
				type(0);
				}
			}

			setState(236);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(234);
				match(ASSIGN);
				setState(235);
				expression(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class EnumDeclarationContext extends ParserRuleContext {
		public TerminalNode ENUM() { return getToken(CocoParser.ENUM, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public GenericTypeDeclarationContext genericTypeDeclaration() {
			return getRuleContext(GenericTypeDeclarationContext.class,0);
		}
		public List<EnumElementContext> enumElement() {
			return getRuleContexts(EnumElementContext.class);
		}
		public EnumElementContext enumElement(int i) {
			return getRuleContext(EnumElementContext.class,i);
		}
		public EnumDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterEnumDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitEnumDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitEnumDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnumDeclarationContext enumDeclaration() throws RecognitionException {
		EnumDeclarationContext _localctx = new EnumDeclarationContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_enumDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
			match(ENUM);
			setState(239);
			match(IDENTIFIER);
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(240);
				genericTypeDeclaration();
				}
			}

			setState(243);
			match(LC);
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CASE) | (1L << FUNCTION) | (1L << MUT) | (1L << MUTATING) | (1L << STATIC))) != 0)) {
				{
				{
				setState(244);
				enumElement();
				}
				}
				setState(249);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(250);
			match(RC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class StructDeclarationContext extends ParserRuleContext {
		public TerminalNode STRUCT() { return getToken(CocoParser.STRUCT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public GenericTypeDeclarationContext genericTypeDeclaration() {
			return getRuleContext(GenericTypeDeclarationContext.class,0);
		}
		public List<StructElementContext> structElement() {
			return getRuleContexts(StructElementContext.class);
		}
		public StructElementContext structElement(int i) {
			return getRuleContext(StructElementContext.class,i);
		}
		public StructDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterStructDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitStructDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitStructDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructDeclarationContext structDeclaration() throws RecognitionException {
		StructDeclarationContext _localctx = new StructDeclarationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_structDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(252);
			match(STRUCT);
			setState(253);
			match(IDENTIFIER);
			setState(255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(254);
				genericTypeDeclaration();
				}
			}

			setState(257);
			match(LC);
			setState(261);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNCTION) | (1L << MUT) | (1L << MUTATING) | (1L << STATIC) | (1L << VAL) | (1L << VAR) | (1L << IDENTIFIER))) != 0)) {
				{
				{
				setState(258);
				structElement();
				}
				}
				setState(263);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(264);
			match(RC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class TypeAliasDeclarationContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(CocoParser.TYPE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public GenericTypeDeclarationContext genericTypeDeclaration() {
			return getRuleContext(GenericTypeDeclarationContext.class,0);
		}
		public TypeAliasDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeAliasDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterTypeAliasDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitTypeAliasDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitTypeAliasDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeAliasDeclarationContext typeAliasDeclaration() throws RecognitionException {
		TypeAliasDeclarationContext _localctx = new TypeAliasDeclarationContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_typeAliasDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			match(TYPE);
			setState(267);
			match(IDENTIFIER);
			setState(269);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(268);
				genericTypeDeclaration();
				}
			}

			setState(271);
			match(ASSIGN);
			setState(272);
			type(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class FunctionDeclarationContext extends ParserRuleContext {
		public TerminalNode FUNCTION() { return getToken(CocoParser.FUNCTION, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public GenericTypeDeclarationContext genericTypeDeclaration() {
			return getRuleContext(GenericTypeDeclarationContext.class,0);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public FunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitFunctionDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitFunctionDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionDeclarationContext functionDeclaration() throws RecognitionException {
		FunctionDeclarationContext _localctx = new FunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_functionDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			match(FUNCTION);
			setState(275);
			match(IDENTIFIER);
			setState(277);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(276);
				genericTypeDeclaration();
				}
			}

			setState(279);
			match(LP);
			setState(281);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR || _la==IDENTIFIER) {
				{
				setState(280);
				parameters();
				}
			}

			setState(283);
			match(RP);
			setState(284);
			match(COLON);
			setState(285);
			type(0);
			setState(286);
			match(ASSIGN);
			setState(287);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class InstanceDeclarationContext extends ParserRuleContext {
		public TerminalNode INSTANCE() { return getToken(CocoParser.INSTANCE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public BlockExpression_Context blockExpression_() {
			return getRuleContext(BlockExpression_Context.class,0);
		}
		public GenericTypeDeclarationContext genericTypeDeclaration() {
			return getRuleContext(GenericTypeDeclarationContext.class,0);
		}
		public InstanceDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instanceDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterInstanceDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitInstanceDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitInstanceDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstanceDeclarationContext instanceDeclaration() throws RecognitionException {
		InstanceDeclarationContext _localctx = new InstanceDeclarationContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_instanceDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(289);
			match(INSTANCE);
			setState(290);
			match(IDENTIFIER);
			setState(292);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(291);
				genericTypeDeclaration();
				}
			}

			setState(294);
			match(LP);
			setState(295);
			parameters();
			setState(296);
			match(RP);
			setState(297);
			blockExpression_();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class PortDeclarationContext extends ParserRuleContext {
		public TerminalNode PORT() { return getToken(CocoParser.PORT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypesContext types() {
			return getRuleContext(TypesContext.class,0);
		}
		public TerminalNode FINAL() { return getToken(CocoParser.FINAL, 0); }
		public List<PortElementContext> portElement() {
			return getRuleContexts(PortElementContext.class);
		}
		public PortElementContext portElement(int i) {
			return getRuleContext(PortElementContext.class,i);
		}
		public PortDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_portDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterPortDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitPortDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitPortDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PortDeclarationContext portDeclaration() throws RecognitionException {
		PortDeclarationContext _localctx = new PortDeclarationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_portDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(299);
			match(PORT);
			setState(300);
			match(IDENTIFIER);
			setState(303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(301);
				match(COLON);
				setState(302);
				types();
				}
			}

			setState(306);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FINAL) {
				{
				setState(305);
				match(FINAL);
				}
			}

			setState(308);
			match(LC);
			setState(312);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ENUM) | (1L << EXTERNAL) | (1L << FUNCTION) | (1L << MACHINE) | (1L << OUTGOING) | (1L << PORT) | (1L << STATIC) | (1L << STRUCT) | (1L << TYPE) | (1L << VAL) | (1L << VAR) | (1L << IDENTIFIER) | (1L << AT))) != 0)) {
				{
				{
				setState(309);
				portElement();
				}
				}
				setState(314);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(315);
			match(RC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ComponentDeclarationContext extends ParserRuleContext {
		public TerminalNode COMPONENT() { return getToken(CocoParser.COMPONENT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public TerminalNode EXTERNAL() { return getToken(CocoParser.EXTERNAL, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<ComponentElementContext> componentElement() {
			return getRuleContexts(ComponentElementContext.class);
		}
		public ComponentElementContext componentElement(int i) {
			return getRuleContext(ComponentElementContext.class,i);
		}
		public ComponentDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_componentDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterComponentDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitComponentDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitComponentDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComponentDeclarationContext componentDeclaration() throws RecognitionException {
		ComponentDeclarationContext _localctx = new ComponentDeclarationContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_componentDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(318);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTERNAL) {
				{
				setState(317);
				match(EXTERNAL);
				}
			}

			setState(320);
			match(COMPONENT);
			setState(321);
			match(IDENTIFIER);
			setState(324);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(322);
				match(COLON);
				setState(323);
				type(0);
				}
			}

			setState(326);
			match(LC);
			setState(330);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INIT) | (1L << MACHINE) | (1L << PRIVATE) | (1L << STATIC) | (1L << VAL) | (1L << VAR) | (1L << IDENTIFIER) | (1L << AT))) != 0)) {
				{
				{
				setState(327);
				componentElement();
				}
				}
				setState(332);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(333);
			match(RC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ExternalConstantDeclarationContext extends ParserRuleContext {
		public TerminalNode EXTERNAL() { return getToken(CocoParser.EXTERNAL, 0); }
		public TerminalNode VAL() { return getToken(CocoParser.VAL, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExternalConstantDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_externalConstantDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterExternalConstantDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitExternalConstantDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitExternalConstantDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExternalConstantDeclarationContext externalConstantDeclaration() throws RecognitionException {
		ExternalConstantDeclarationContext _localctx = new ExternalConstantDeclarationContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_externalConstantDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			match(EXTERNAL);
			setState(336);
			match(VAL);
			setState(337);
			match(IDENTIFIER);
			setState(338);
			match(COLON);
			setState(339);
			type(0);
			setState(340);
			match(ASSIGN);
			setState(341);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ExternalTypeDeclarationContext extends ParserRuleContext {
		public TerminalNode EXTERNAL() { return getToken(CocoParser.EXTERNAL, 0); }
		public TerminalNode TYPE() { return getToken(CocoParser.TYPE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public List<ExternalTypeElementContext> externalTypeElement() {
			return getRuleContexts(ExternalTypeElementContext.class);
		}
		public ExternalTypeElementContext externalTypeElement(int i) {
			return getRuleContext(ExternalTypeElementContext.class,i);
		}
		public ExternalTypeDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_externalTypeDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterExternalTypeDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitExternalTypeDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitExternalTypeDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExternalTypeDeclarationContext externalTypeDeclaration() throws RecognitionException {
		ExternalTypeDeclarationContext _localctx = new ExternalTypeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_externalTypeDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(343);
			match(EXTERNAL);
			setState(344);
			match(TYPE);
			setState(345);
			match(IDENTIFIER);
			setState(354);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LC) {
				{
				setState(346);
				match(LC);
				setState(350);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXTERNAL) | (1L << MUT) | (1L << MUTATING) | (1L << PRIVATE) | (1L << STATIC) | (1L << VAL) | (1L << VAR))) != 0)) {
					{
					{
					setState(347);
					externalTypeElement();
					}
					}
					setState(352);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(353);
				match(RC);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ExternalTypeElementContext extends ParserRuleContext {
		public StaticMemberDeclarationContext staticMemberDeclaration() {
			return getRuleContext(StaticMemberDeclarationContext.class,0);
		}
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public ExternalFunctionDeclarationContext externalFunctionDeclaration() {
			return getRuleContext(ExternalFunctionDeclarationContext.class,0);
		}
		public TerminalNode MUT() { return getToken(CocoParser.MUT, 0); }
		public TerminalNode MUTATING() { return getToken(CocoParser.MUTATING, 0); }
		public ExternalTypeElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_externalTypeElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterExternalTypeElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitExternalTypeElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitExternalTypeElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExternalTypeElementContext externalTypeElement() throws RecognitionException {
		ExternalTypeElementContext _localctx = new ExternalTypeElementContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_externalTypeElement);
		int _la;
		try {
			setState(362);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STATIC:
				enterOuterAlt(_localctx, 1);
				{
				setState(356);
				staticMemberDeclaration();
				}
				break;
			case PRIVATE:
			case VAL:
			case VAR:
				enterOuterAlt(_localctx, 2);
				{
				setState(357);
				variableDeclaration();
				}
				break;
			case EXTERNAL:
			case MUT:
			case MUTATING:
				enterOuterAlt(_localctx, 3);
				{
				setState(359);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MUT || _la==MUTATING) {
					{
					setState(358);
					_la = _input.LA(1);
					if ( !(_la==MUT || _la==MUTATING) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(361);
				externalFunctionDeclaration();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ExternalFunctionDeclarationContext extends ParserRuleContext {
		public TerminalNode EXTERNAL() { return getToken(CocoParser.EXTERNAL, 0); }
		public TerminalNode FUNCTION() { return getToken(CocoParser.FUNCTION, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public ExternalFunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_externalFunctionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterExternalFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitExternalFunctionDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitExternalFunctionDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExternalFunctionDeclarationContext externalFunctionDeclaration() throws RecognitionException {
		ExternalFunctionDeclarationContext _localctx = new ExternalFunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_externalFunctionDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(364);
			match(EXTERNAL);
			setState(365);
			match(FUNCTION);
			setState(366);
			match(IDENTIFIER);
			setState(367);
			match(LP);
			setState(369);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR || _la==IDENTIFIER) {
				{
				setState(368);
				parameters();
				}
			}

			setState(371);
			match(RP);
			setState(372);
			match(COLON);
			setState(373);
			type(0);
			setState(374);
			match(ASSIGN);
			setState(375);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class GenericTypeDeclarationContext extends ParserRuleContext {
		public TerminalNode LT() { return getToken(CocoParser.LT, 0); }
		public GenericTypesContext genericTypes() {
			return getRuleContext(GenericTypesContext.class,0);
		}
		public TerminalNode GT() { return getToken(CocoParser.GT, 0); }
		public TerminalNode WHERE() { return getToken(CocoParser.WHERE, 0); }
		public ExpressionsContext expressions() {
			return getRuleContext(ExpressionsContext.class,0);
		}
		public GenericTypeDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_genericTypeDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterGenericTypeDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitGenericTypeDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitGenericTypeDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericTypeDeclarationContext genericTypeDeclaration() throws RecognitionException {
		GenericTypeDeclarationContext _localctx = new GenericTypeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_genericTypeDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(377);
			match(LT);
			setState(378);
			genericTypes();
			setState(381);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(379);
				match(WHERE);
				setState(380);
				expressions();
				}
			}

			setState(383);
			match(GT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class GenericTypesContext extends ParserRuleContext {
		public List<GenericTypeContext> genericType() {
			return getRuleContexts(GenericTypeContext.class);
		}
		public GenericTypeContext genericType(int i) {
			return getRuleContext(GenericTypeContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CocoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CocoParser.COMMA, i);
		}
		public GenericTypesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_genericTypes; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterGenericTypes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitGenericTypes(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitGenericTypes(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericTypesContext genericTypes() throws RecognitionException {
		GenericTypesContext _localctx = new GenericTypesContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_genericTypes);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			genericType();
			setState(390);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(386);
				match(COMMA);
				setState(387);
				genericType();
				}
				}
				setState(392);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class GenericTypeContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode VAL() { return getToken(CocoParser.VAL, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public GenericTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_genericType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterGenericType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitGenericType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitGenericType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericTypeContext genericType() throws RecognitionException {
		GenericTypeContext _localctx = new GenericTypeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_genericType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(394);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAL) {
				{
				setState(393);
				match(VAL);
				}
			}

			setState(396);
			match(IDENTIFIER);
			setState(399);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(397);
				match(COLON);
				setState(398);
				type(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class EnumElementContext extends ParserRuleContext {
		public EnumCaseContext enumCase() {
			return getRuleContext(EnumCaseContext.class,0);
		}
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public TerminalNode MUT() { return getToken(CocoParser.MUT, 0); }
		public TerminalNode MUTATING() { return getToken(CocoParser.MUTATING, 0); }
		public StaticMemberDeclarationContext staticMemberDeclaration() {
			return getRuleContext(StaticMemberDeclarationContext.class,0);
		}
		public EnumElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterEnumElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitEnumElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitEnumElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnumElementContext enumElement() throws RecognitionException {
		EnumElementContext _localctx = new EnumElementContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_enumElement);
		int _la;
		try {
			setState(407);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CASE:
				enterOuterAlt(_localctx, 1);
				{
				setState(401);
				enumCase();
				}
				break;
			case FUNCTION:
			case MUT:
			case MUTATING:
				enterOuterAlt(_localctx, 2);
				{
				setState(403);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MUT || _la==MUTATING) {
					{
					setState(402);
					_la = _input.LA(1);
					if ( !(_la==MUT || _la==MUTATING) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(405);
				functionDeclaration();
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 3);
				{
				setState(406);
				staticMemberDeclaration();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class EnumCaseContext extends ParserRuleContext {
		public TerminalNode CASE() { return getToken(CocoParser.CASE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public CaseParametersContext caseParameters() {
			return getRuleContext(CaseParametersContext.class,0);
		}
		public EnumCaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumCase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterEnumCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitEnumCase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitEnumCase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnumCaseContext enumCase() throws RecognitionException {
		EnumCaseContext _localctx = new EnumCaseContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_enumCase);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(409);
			match(CASE);
			setState(410);
			match(IDENTIFIER);
			setState(416);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LP) {
				{
				setState(411);
				match(LP);
				setState(413);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENTIFIER) {
					{
					setState(412);
					caseParameters();
					}
				}

				setState(415);
				match(RP);
				}
			}

			setState(420);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(418);
				match(ASSIGN);
				setState(419);
				expression(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class CaseParametersContext extends ParserRuleContext {
		public List<CaseParameterContext> caseParameter() {
			return getRuleContexts(CaseParameterContext.class);
		}
		public CaseParameterContext caseParameter(int i) {
			return getRuleContext(CaseParameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CocoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CocoParser.COMMA, i);
		}
		public CaseParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterCaseParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitCaseParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitCaseParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CaseParametersContext caseParameters() throws RecognitionException {
		CaseParametersContext _localctx = new CaseParametersContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_caseParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(422);
			caseParameter();
			setState(427);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(423);
				match(COMMA);
				setState(424);
				caseParameter();
				}
				}
				setState(429);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class CaseParameterContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public CaseParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterCaseParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitCaseParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitCaseParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CaseParameterContext caseParameter() throws RecognitionException {
		CaseParameterContext _localctx = new CaseParameterContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_caseParameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(430);
			match(IDENTIFIER);
			setState(431);
			match(COLON);
			setState(432);
			type(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class StructElementContext extends ParserRuleContext {
		public FieldDeclarationContext fieldDeclaration() {
			return getRuleContext(FieldDeclarationContext.class,0);
		}
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public StaticMemberDeclarationContext staticMemberDeclaration() {
			return getRuleContext(StaticMemberDeclarationContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(CocoParser.SEMI, 0); }
		public TerminalNode MUT() { return getToken(CocoParser.MUT, 0); }
		public TerminalNode MUTATING() { return getToken(CocoParser.MUTATING, 0); }
		public StructElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterStructElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitStructElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitStructElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructElementContext structElement() throws RecognitionException {
		StructElementContext _localctx = new StructElementContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_structElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(440);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAL:
			case VAR:
			case IDENTIFIER:
				{
				setState(434);
				fieldDeclaration();
				}
				break;
			case FUNCTION:
			case MUT:
			case MUTATING:
				{
				setState(436);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MUT || _la==MUTATING) {
					{
					setState(435);
					_la = _input.LA(1);
					if ( !(_la==MUT || _la==MUTATING) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(438);
				functionDeclaration();
				}
				break;
			case STATIC:
				{
				setState(439);
				staticMemberDeclaration();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(443);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(442);
				match(SEMI);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class FieldDeclarationContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode VAL() { return getToken(CocoParser.VAL, 0); }
		public TerminalNode VAR() { return getToken(CocoParser.VAR, 0); }
		public FieldDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterFieldDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitFieldDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitFieldDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldDeclarationContext fieldDeclaration() throws RecognitionException {
		FieldDeclarationContext _localctx = new FieldDeclarationContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_fieldDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(446);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAL || _la==VAR) {
				{
				setState(445);
				_la = _input.LA(1);
				if ( !(_la==VAL || _la==VAR) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(448);
			match(IDENTIFIER);
			setState(449);
			match(COLON);
			setState(450);
			type(0);
			setState(453);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(451);
				match(ASSIGN);
				setState(452);
				expression(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ComponentElementContext extends ParserRuleContext {
		public FieldDeclarationContext fieldDeclaration() {
			return getRuleContext(FieldDeclarationContext.class,0);
		}
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public ConstructorDeclarationContext constructorDeclaration() {
			return getRuleContext(ConstructorDeclarationContext.class,0);
		}
		public StateMachineDeclarationContext stateMachineDeclaration() {
			return getRuleContext(StateMachineDeclarationContext.class,0);
		}
		public StaticMemberDeclarationContext staticMemberDeclaration() {
			return getRuleContext(StaticMemberDeclarationContext.class,0);
		}
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public TerminalNode SEMI() { return getToken(CocoParser.SEMI, 0); }
		public ComponentElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_componentElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterComponentElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitComponentElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitComponentElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComponentElementContext componentElement() throws RecognitionException {
		ComponentElementContext _localctx = new ComponentElementContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_componentElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(458);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(455);
				attribute();
				}
				}
				setState(460);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(466);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				{
				setState(461);
				fieldDeclaration();
				}
				break;
			case 2:
				{
				setState(462);
				variableDeclaration();
				}
				break;
			case 3:
				{
				setState(463);
				constructorDeclaration();
				}
				break;
			case 4:
				{
				setState(464);
				stateMachineDeclaration();
				}
				break;
			case 5:
				{
				setState(465);
				staticMemberDeclaration();
				}
				break;
			}
			setState(469);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(468);
				match(SEMI);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class StaticMemberDeclarationContext extends ParserRuleContext {
		public TerminalNode STATIC() { return getToken(CocoParser.STATIC, 0); }
		public TerminalNode VAL() { return getToken(CocoParser.VAL, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StaticMemberDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_staticMemberDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterStaticMemberDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitStaticMemberDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitStaticMemberDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StaticMemberDeclarationContext staticMemberDeclaration() throws RecognitionException {
		StaticMemberDeclarationContext _localctx = new StaticMemberDeclarationContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_staticMemberDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(471);
			match(STATIC);
			setState(472);
			match(VAL);
			setState(473);
			match(IDENTIFIER);
			setState(474);
			match(COLON);
			setState(475);
			type(0);
			setState(476);
			match(ASSIGN);
			setState(477);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ConstructorDeclarationContext extends ParserRuleContext {
		public TerminalNode INIT() { return getToken(CocoParser.INIT, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public BlockExpression_Context blockExpression_() {
			return getRuleContext(BlockExpression_Context.class,0);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public ConstructorDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterConstructorDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitConstructorDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitConstructorDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstructorDeclarationContext constructorDeclaration() throws RecognitionException {
		ConstructorDeclarationContext _localctx = new ConstructorDeclarationContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_constructorDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(479);
			match(INIT);
			setState(480);
			match(LP);
			setState(482);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR || _la==IDENTIFIER) {
				{
				setState(481);
				parameters();
				}
			}

			setState(484);
			match(RP);
			setState(485);
			match(ASSIGN);
			setState(486);
			blockExpression_();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class IfExpressionContext extends ExpressionContext {
		public IfExpression_Context ifExpression_() {
			return getRuleContext(IfExpression_Context.class,0);
		}
		public IfExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterIfExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitIfExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitIfExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class TryOperatorExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode QM() { return getToken(CocoParser.QM, 0); }
		public TryOperatorExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterTryOperatorExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitTryOperatorExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitTryOperatorExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class UnaryOperatorExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode MINUS() { return getToken(CocoParser.MINUS, 0); }
		public TerminalNode EXCL() { return getToken(CocoParser.EXCL, 0); }
		public TerminalNode AMP() { return getToken(CocoParser.AMP, 0); }
		public UnaryOperatorExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterUnaryOperatorExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitUnaryOperatorExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitUnaryOperatorExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class OptionalExpressionContext extends ExpressionContext {
		public TerminalNode OPTIONAL() { return getToken(CocoParser.OPTIONAL, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public OptionalExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterOptionalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitOptionalExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitOptionalExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ArithmicOrLogicalExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode MUL() { return getToken(CocoParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(CocoParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(CocoParser.MOD, 0); }
		public TerminalNode PLUS() { return getToken(CocoParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(CocoParser.MINUS, 0); }
		public TerminalNode EQ() { return getToken(CocoParser.EQ, 0); }
		public TerminalNode NE() { return getToken(CocoParser.NE, 0); }
		public TerminalNode OR() { return getToken(CocoParser.OR, 0); }
		public TerminalNode AND() { return getToken(CocoParser.AND, 0); }
		public TerminalNode LT() { return getToken(CocoParser.LT, 0); }
		public TerminalNode LE() { return getToken(CocoParser.LE, 0); }
		public TerminalNode GT() { return getToken(CocoParser.GT, 0); }
		public TerminalNode GE() { return getToken(CocoParser.GE, 0); }
		public ArithmicOrLogicalExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterArithmicOrLogicalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitArithmicOrLogicalExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitArithmicOrLogicalExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class LiteralExpressionContext extends ExpressionContext {
		public LiteralExpression_Context literalExpression_() {
			return getRuleContext(LiteralExpression_Context.class,0);
		}
		public LiteralExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitLiteralExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitLiteralExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ArrayLiteralExpressionContext extends ExpressionContext {
		public TerminalNode LB() { return getToken(CocoParser.LB, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RB() { return getToken(CocoParser.RB, 0); }
		public ArrayLiteralExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterArrayLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitArrayLiteralExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitArrayLiteralExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class NondetExpressionContext extends ExpressionContext {
		public NondetExpression_Context nondetExpression_() {
			return getRuleContext(NondetExpression_Context.class,0);
		}
		public NondetExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterNondetExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitNondetExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitNondetExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class GroupedExpressionContext extends ExpressionContext {
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public ExpressionsContext expressions() {
			return getRuleContext(ExpressionsContext.class,0);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public GroupedExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterGroupedExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitGroupedExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitGroupedExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class BlockExpressionContext extends ExpressionContext {
		public BlockExpression_Context blockExpression_() {
			return getRuleContext(BlockExpression_Context.class,0);
		}
		public BlockExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterBlockExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitBlockExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitBlockExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class MatchExpressionContext extends ExpressionContext {
		public MatchExpression_Context matchExpression_() {
			return getRuleContext(MatchExpression_Context.class,0);
		}
		public MatchExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterMatchExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitMatchExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitMatchExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class StructLiteralExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public FieldAssignmentsContext fieldAssignments() {
			return getRuleContext(FieldAssignmentsContext.class,0);
		}
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public StructLiteralExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterStructLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitStructLiteralExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitStructLiteralExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class MemberReferenceExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode DOT() { return getToken(CocoParser.DOT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public MemberReferenceExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterMemberReferenceExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitMemberReferenceExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitMemberReferenceExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class AssignmentExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public AssignmentExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterAssignmentExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitAssignmentExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitAssignmentExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class VariableReferenceExpressionContext extends ExpressionContext {
		public DotIdentifierListContext dotIdentifierList() {
			return getRuleContext(DotIdentifierListContext.class,0);
		}
		public TerminalNode LT() { return getToken(CocoParser.LT, 0); }
		public GenericTypesContext genericTypes() {
			return getRuleContext(GenericTypesContext.class,0);
		}
		public TerminalNode GT() { return getToken(CocoParser.GT, 0); }
		public VariableReferenceExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterVariableReferenceExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitVariableReferenceExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitVariableReferenceExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ImplicitMemberExpressionContext extends ExpressionContext {
		public TerminalNode DOT() { return getToken(CocoParser.DOT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public ImplicitMemberExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterImplicitMemberExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitImplicitMemberExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitImplicitMemberExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ExternalFunctionContext extends ExpressionContext {
		public ExternalFunctionDeclarationContext externalFunctionDeclaration() {
			return getRuleContext(ExternalFunctionDeclarationContext.class,0);
		}
		public ExternalFunctionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterExternalFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitExternalFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitExternalFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class CastExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode AS() { return getToken(CocoParser.AS, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public CastExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterCastExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitCastExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitCastExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class StateInvariantExpressionContext extends ExpressionContext {
		public StateInvariantContext stateInvariant() {
			return getRuleContext(StateInvariantContext.class,0);
		}
		public StateInvariantExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterStateInvariantExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitStateInvariantExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitStateInvariantExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class CallExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public ExpressionsContext expressions() {
			return getRuleContext(ExpressionsContext.class,0);
		}
		public CallExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterCallExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitCallExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitCallExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ExternalLiteralContext extends ExpressionContext {
		public TerminalNode EXTERNAL() { return getToken(CocoParser.EXTERNAL, 0); }
		public TerminalNode BACKTICK_LITERAL() { return getToken(CocoParser.BACKTICK_LITERAL, 0); }
		public ExternalLiteralContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterExternalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitExternalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitExternalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ArraySubscriptExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode LB() { return getToken(CocoParser.LB, 0); }
		public TerminalNode RB() { return getToken(CocoParser.RB, 0); }
		public ArraySubscriptExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterArraySubscriptExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitArraySubscriptExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitArraySubscriptExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 58;
		enterRecursionRule(_localctx, 58, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(519);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				{
				_localctx = new LiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(489);
				literalExpression_();
				}
				break;
			case 2:
				{
				_localctx = new ExternalLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(490);
				match(EXTERNAL);
				setState(491);
				match(BACKTICK_LITERAL);
				}
				break;
			case 3:
				{
				_localctx = new ExternalFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(492);
				externalFunctionDeclaration();
				}
				break;
			case 4:
				{
				_localctx = new VariableReferenceExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(493);
				dotIdentifierList();
				setState(498);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
				case 1:
					{
					setState(494);
					match(LT);
					setState(495);
					genericTypes();
					setState(496);
					match(GT);
					}
					break;
				}
				}
				break;
			case 5:
				{
				_localctx = new StateInvariantExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(500);
				stateInvariant();
				}
				break;
			case 6:
				{
				_localctx = new UnaryOperatorExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(501);
				_la = _input.LA(1);
				if ( !(((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (MINUS - 67)) | (1L << (AMP - 67)) | (1L << (EXCL - 67)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(502);
				expression(14);
				}
				break;
			case 7:
				{
				_localctx = new IfExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(503);
				ifExpression_();
				}
				break;
			case 8:
				{
				_localctx = new MatchExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(504);
				matchExpression_();
				}
				break;
			case 9:
				{
				_localctx = new ImplicitMemberExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(505);
				match(DOT);
				setState(506);
				match(IDENTIFIER);
				}
				break;
			case 10:
				{
				_localctx = new GroupedExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(507);
				match(LP);
				setState(508);
				expressions();
				setState(509);
				match(RP);
				}
				break;
			case 11:
				{
				_localctx = new ArrayLiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(511);
				match(LB);
				setState(512);
				expression(0);
				setState(513);
				match(RB);
				}
				break;
			case 12:
				{
				_localctx = new NondetExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(515);
				nondetExpression_();
				}
				break;
			case 13:
				{
				_localctx = new OptionalExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(516);
				match(OPTIONAL);
				setState(517);
				expression(2);
				}
				break;
			case 14:
				{
				_localctx = new BlockExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(518);
				blockExpression_();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(559);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(557);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmicOrLogicalExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(521);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(522);
						_la = _input.LA(1);
						if ( !(((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (MUL - 65)) | (1L << (DIV - 65)) | (1L << (MOD - 65)))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(523);
						expression(13);
						}
						break;
					case 2:
						{
						_localctx = new ArithmicOrLogicalExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(524);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(525);
						_la = _input.LA(1);
						if ( !(_la==MINUS || _la==PLUS) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(526);
						expression(12);
						}
						break;
					case 3:
						{
						_localctx = new ArithmicOrLogicalExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(527);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(528);
						_la = _input.LA(1);
						if ( !(((((_la - 63)) & ~0x3f) == 0 && ((1L << (_la - 63)) & ((1L << (LT - 63)) | (1L << (GT - 63)) | (1L << (EQ - 63)) | (1L << (NE - 63)) | (1L << (OR - 63)) | (1L << (AND - 63)) | (1L << (LE - 63)) | (1L << (GE - 63)))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(529);
						expression(11);
						}
						break;
					case 4:
						{
						_localctx = new AssignmentExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(530);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(531);
						match(ASSIGN);
						setState(532);
						expression(10);
						}
						break;
					case 5:
						{
						_localctx = new MemberReferenceExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(533);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(534);
						match(DOT);
						setState(535);
						match(IDENTIFIER);
						}
						break;
					case 6:
						{
						_localctx = new CallExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(536);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(537);
						match(LP);
						setState(539);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSERT) | (1L << EXTERNAL) | (1L << IF) | (1L << MATCH) | (1L << NONDET) | (1L << OPTIONAL) | (1L << IDENTIFIER) | (1L << LP) | (1L << LC) | (1L << LB) | (1L << DOT))) != 0) || ((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (MINUS - 67)) | (1L << (AMP - 67)) | (1L << (EXCL - 67)) | (1L << (INTEGER - 67)) | (1L << (CHAR_LITERAL - 67)) | (1L << (STRING_LITERAL - 67)))) != 0)) {
							{
							setState(538);
							expressions();
							}
						}

						setState(541);
						match(RP);
						}
						break;
					case 7:
						{
						_localctx = new ArraySubscriptExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(542);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(543);
						match(LB);
						setState(544);
						expression(0);
						setState(545);
						match(RB);
						}
						break;
					case 8:
						{
						_localctx = new StructLiteralExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(547);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(548);
						match(LC);
						setState(549);
						fieldAssignments();
						setState(550);
						match(RC);
						}
						break;
					case 9:
						{
						_localctx = new TryOperatorExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(552);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(553);
						match(QM);
						}
						break;
					case 10:
						{
						_localctx = new CastExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(554);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(555);
						match(AS);
						setState(556);
						type(0);
						}
						break;
					}
					} 
				}
				setState(561);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class BlockExpression_Context extends ParserRuleContext {
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public BlockExpression_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockExpression_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterBlockExpression_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitBlockExpression_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitBlockExpression_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockExpression_Context blockExpression_() throws RecognitionException {
		BlockExpression_Context _localctx = new BlockExpression_Context(_ctx, getState());
		enterRule(_localctx, 60, RULE_blockExpression_);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(562);
			match(LC);
			setState(566);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(563);
					statement();
					}
					} 
				}
				setState(568);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
			}
			setState(570);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSERT) | (1L << EXTERNAL) | (1L << IF) | (1L << MATCH) | (1L << NONDET) | (1L << OPTIONAL) | (1L << IDENTIFIER) | (1L << LP) | (1L << LC) | (1L << LB) | (1L << DOT))) != 0) || ((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (MINUS - 67)) | (1L << (AMP - 67)) | (1L << (EXCL - 67)) | (1L << (INTEGER - 67)) | (1L << (CHAR_LITERAL - 67)) | (1L << (STRING_LITERAL - 67)))) != 0)) {
				{
				setState(569);
				expression(0);
				}
			}

			setState(572);
			match(RC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class IfExpression_Context extends ParserRuleContext {
		public TerminalNode IF() { return getToken(CocoParser.IF, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode ELSE() { return getToken(CocoParser.ELSE, 0); }
		public IfExpression_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifExpression_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterIfExpression_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitIfExpression_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitIfExpression_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfExpression_Context ifExpression_() throws RecognitionException {
		IfExpression_Context _localctx = new IfExpression_Context(_ctx, getState());
		enterRule(_localctx, 62, RULE_ifExpression_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(574);
			match(IF);
			setState(575);
			match(LP);
			setState(576);
			expression(0);
			setState(577);
			match(RP);
			setState(578);
			expression(0);
			setState(581);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(579);
				match(ELSE);
				setState(580);
				expression(0);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class MatchExpression_Context extends ParserRuleContext {
		public TerminalNode MATCH() { return getToken(CocoParser.MATCH, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public MatchClausesContext matchClauses() {
			return getRuleContext(MatchClausesContext.class,0);
		}
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public MatchExpression_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchExpression_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterMatchExpression_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitMatchExpression_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitMatchExpression_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchExpression_Context matchExpression_() throws RecognitionException {
		MatchExpression_Context _localctx = new MatchExpression_Context(_ctx, getState());
		enterRule(_localctx, 64, RULE_matchExpression_);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(585);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(583);
				match(IDENTIFIER);
				setState(584);
				match(COLON);
				}
			}

			setState(587);
			match(MATCH);
			setState(588);
			match(LP);
			setState(589);
			expression(0);
			setState(590);
			match(RP);
			setState(591);
			match(LC);
			setState(592);
			matchClauses();
			setState(593);
			match(RC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class NondetExpression_Context extends ParserRuleContext {
		public TerminalNode NONDET() { return getToken(CocoParser.NONDET, 0); }
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public NondetClausesContext nondetClauses() {
			return getRuleContext(NondetClausesContext.class,0);
		}
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public TerminalNode OTHERWISE() { return getToken(CocoParser.OTHERWISE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(CocoParser.COMMA, 0); }
		public TerminalNode SEMI() { return getToken(CocoParser.SEMI, 0); }
		public NondetExpression_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nondetExpression_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterNondetExpression_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitNondetExpression_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitNondetExpression_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NondetExpression_Context nondetExpression_() throws RecognitionException {
		NondetExpression_Context _localctx = new NondetExpression_Context(_ctx, getState());
		enterRule(_localctx, 66, RULE_nondetExpression_);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(595);
			match(NONDET);
			setState(596);
			match(LC);
			setState(597);
			nondetClauses();
			setState(600);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OTHERWISE) {
				{
				setState(598);
				match(OTHERWISE);
				setState(599);
				expression(0);
				}
			}

			setState(603);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA || _la==SEMI) {
				{
				setState(602);
				_la = _input.LA(1);
				if ( !(_la==COMMA || _la==SEMI) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(605);
			match(RC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class FieldAssignmentsContext extends ParserRuleContext {
		public List<FieldAssignmentContext> fieldAssignment() {
			return getRuleContexts(FieldAssignmentContext.class);
		}
		public FieldAssignmentContext fieldAssignment(int i) {
			return getRuleContext(FieldAssignmentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CocoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CocoParser.COMMA, i);
		}
		public FieldAssignmentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldAssignments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterFieldAssignments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitFieldAssignments(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitFieldAssignments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldAssignmentsContext fieldAssignments() throws RecognitionException {
		FieldAssignmentsContext _localctx = new FieldAssignmentsContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_fieldAssignments);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(607);
			fieldAssignment();
			setState(612);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,61,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(608);
					match(COMMA);
					setState(609);
					fieldAssignment();
					}
					} 
				}
				setState(614);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,61,_ctx);
			}
			setState(616);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(615);
				match(COMMA);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class FieldAssignmentContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public FieldAssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldAssignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterFieldAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitFieldAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitFieldAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldAssignmentContext fieldAssignment() throws RecognitionException {
		FieldAssignmentContext _localctx = new FieldAssignmentContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_fieldAssignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(618);
			match(IDENTIFIER);
			setState(619);
			match(ASSIGN);
			setState(620);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class NondetClausesContext extends ParserRuleContext {
		public List<NondetClauseContext> nondetClause() {
			return getRuleContexts(NondetClauseContext.class);
		}
		public NondetClauseContext nondetClause(int i) {
			return getRuleContext(NondetClauseContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CocoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CocoParser.COMMA, i);
		}
		public List<TerminalNode> SEMI() { return getTokens(CocoParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(CocoParser.SEMI, i);
		}
		public NondetClausesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nondetClauses; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterNondetClauses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitNondetClauses(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitNondetClauses(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NondetClausesContext nondetClauses() throws RecognitionException {
		NondetClausesContext _localctx = new NondetClausesContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_nondetClauses);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(622);
			nondetClause();
			setState(627);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,63,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(623);
					_la = _input.LA(1);
					if ( !(_la==COMMA || _la==SEMI) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(624);
					nondetClause();
					}
					} 
				}
				setState(629);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,63,_ctx);
			}
			setState(631);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				{
				setState(630);
				_la = _input.LA(1);
				if ( !(_la==COMMA || _la==SEMI) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class NondetClauseContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode IF() { return getToken(CocoParser.IF, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public NondetClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nondetClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterNondetClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitNondetClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitNondetClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NondetClauseContext nondetClause() throws RecognitionException {
		NondetClauseContext _localctx = new NondetClauseContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_nondetClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(638);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				{
				setState(633);
				match(IF);
				setState(634);
				match(LP);
				setState(635);
				expression(0);
				setState(636);
				match(RP);
				}
				break;
			}
			setState(640);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class MatchClausesContext extends ParserRuleContext {
		public List<MatchClauseContext> matchClause() {
			return getRuleContexts(MatchClauseContext.class);
		}
		public MatchClauseContext matchClause(int i) {
			return getRuleContext(MatchClauseContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CocoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CocoParser.COMMA, i);
		}
		public List<TerminalNode> SEMI() { return getTokens(CocoParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(CocoParser.SEMI, i);
		}
		public MatchClausesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchClauses; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterMatchClauses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitMatchClauses(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitMatchClauses(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchClausesContext matchClauses() throws RecognitionException {
		MatchClausesContext _localctx = new MatchClausesContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_matchClauses);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(642);
			matchClause();
			setState(647);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(643);
					_la = _input.LA(1);
					if ( !(_la==COMMA || _la==SEMI) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(644);
					matchClause();
					}
					} 
				}
				setState(649);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
			}
			setState(651);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA || _la==SEMI) {
				{
				setState(650);
				_la = _input.LA(1);
				if ( !(_la==COMMA || _la==SEMI) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class MatchClauseContext extends ParserRuleContext {
		public PatternContext pattern() {
			return getRuleContext(PatternContext.class,0);
		}
		public TerminalNode IMPL() { return getToken(CocoParser.IMPL, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public MatchClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterMatchClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitMatchClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitMatchClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchClauseContext matchClause() throws RecognitionException {
		MatchClauseContext _localctx = new MatchClauseContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_matchClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(653);
			pattern();
			setState(654);
			match(IMPL);
			setState(655);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class PatternContext extends ParserRuleContext {
		public EnumCasePatternContext enumCasePattern() {
			return getRuleContext(EnumCasePatternContext.class,0);
		}
		public LiteralExpression_Context literalExpression_() {
			return getRuleContext(LiteralExpression_Context.class,0);
		}
		public VariableDeclarationPatternContext variableDeclarationPattern() {
			return getRuleContext(VariableDeclarationPatternContext.class,0);
		}
		public PatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PatternContext pattern() throws RecognitionException {
		PatternContext _localctx = new PatternContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_pattern);
		try {
			setState(660);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(657);
				enumCasePattern();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(658);
				literalExpression_();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(659);
				variableDeclarationPattern();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class EnumCasePatternContext extends ParserRuleContext {
		public IdParameterPatternsContext idParameterPatterns() {
			return getRuleContext(IdParameterPatternsContext.class,0);
		}
		public TerminalNode IF() { return getToken(CocoParser.IF, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public EnumCasePatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumCasePattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterEnumCasePattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitEnumCasePattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitEnumCasePattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnumCasePatternContext enumCasePattern() throws RecognitionException {
		EnumCasePatternContext _localctx = new EnumCasePatternContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_enumCasePattern);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(662);
			idParameterPatterns();
			setState(668);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IF) {
				{
				setState(663);
				match(IF);
				setState(664);
				match(LP);
				setState(665);
				expression(0);
				setState(666);
				match(RP);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class IdParameterPatternsContext extends ParserRuleContext {
		public List<IdParameterPatternContext> idParameterPattern() {
			return getRuleContexts(IdParameterPatternContext.class);
		}
		public IdParameterPatternContext idParameterPattern(int i) {
			return getRuleContext(IdParameterPatternContext.class,i);
		}
		public List<TerminalNode> DOT() { return getTokens(CocoParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(CocoParser.DOT, i);
		}
		public IdParameterPatternsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_idParameterPatterns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterIdParameterPatterns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitIdParameterPatterns(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitIdParameterPatterns(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdParameterPatternsContext idParameterPatterns() throws RecognitionException {
		IdParameterPatternsContext _localctx = new IdParameterPatternsContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_idParameterPatterns);
		int _la;
		try {
			setState(687);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(670);
				idParameterPattern();
				setState(675);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(671);
					match(DOT);
					setState(672);
					idParameterPattern();
					}
					}
					setState(677);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case DOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(678);
				match(DOT);
				setState(679);
				idParameterPattern();
				setState(684);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(680);
					match(DOT);
					setState(681);
					idParameterPattern();
					}
					}
					setState(686);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class IdParameterPatternContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public ParameterPatternsContext parameterPatterns() {
			return getRuleContext(ParameterPatternsContext.class,0);
		}
		public IdParameterPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_idParameterPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterIdParameterPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitIdParameterPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitIdParameterPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdParameterPatternContext idParameterPattern() throws RecognitionException {
		IdParameterPatternContext _localctx = new IdParameterPatternContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_idParameterPattern);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(689);
			match(IDENTIFIER);
			setState(695);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LP) {
				{
				setState(690);
				match(LP);
				setState(692);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR || _la==IDENTIFIER) {
					{
					setState(691);
					parameterPatterns();
					}
				}

				setState(694);
				match(RP);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class VariableDeclarationPatternContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode VAL() { return getToken(CocoParser.VAL, 0); }
		public TerminalNode DOT() { return getToken(CocoParser.DOT, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public VariableDeclarationPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclarationPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterVariableDeclarationPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitVariableDeclarationPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitVariableDeclarationPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclarationPatternContext variableDeclarationPattern() throws RecognitionException {
		VariableDeclarationPatternContext _localctx = new VariableDeclarationPatternContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_variableDeclarationPattern);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(697);
			_la = _input.LA(1);
			if ( !(_la==VAL || _la==DOT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(698);
			match(IDENTIFIER);
			setState(701);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(699);
				match(COLON);
				setState(700);
				type(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ParameterPatternsContext extends ParserRuleContext {
		public List<ParameterPatternContext> parameterPattern() {
			return getRuleContexts(ParameterPatternContext.class);
		}
		public ParameterPatternContext parameterPattern(int i) {
			return getRuleContext(ParameterPatternContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CocoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CocoParser.COMMA, i);
		}
		public ParameterPatternsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterPatterns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterParameterPatterns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitParameterPatterns(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitParameterPatterns(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterPatternsContext parameterPatterns() throws RecognitionException {
		ParameterPatternsContext _localctx = new ParameterPatternsContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_parameterPatterns);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(703);
			parameterPattern();
			setState(708);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(704);
				match(COMMA);
				setState(705);
				parameterPattern();
				}
				}
				setState(710);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ParameterPatternContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode VAR() { return getToken(CocoParser.VAR, 0); }
		public ParameterPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterParameterPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitParameterPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitParameterPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterPatternContext parameterPattern() throws RecognitionException {
		ParameterPatternContext _localctx = new ParameterPatternContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_parameterPattern);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(712);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR) {
				{
				setState(711);
				match(VAR);
				}
			}

			setState(714);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ExpressionsContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CocoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CocoParser.COMMA, i);
		}
		public ExpressionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterExpressions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitExpressions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitExpressions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionsContext expressions() throws RecognitionException {
		ExpressionsContext _localctx = new ExpressionsContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_expressions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(716);
			expression(0);
			setState(721);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(717);
				match(COMMA);
				setState(718);
				expression(0);
				}
				}
				setState(723);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class StatementContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StateInvariantContext stateInvariant() {
			return getRuleContext(StateInvariantContext.class,0);
		}
		public DeclarationStatementContext declarationStatement() {
			return getRuleContext(DeclarationStatementContext.class,0);
		}
		public ReturnStatementContext returnStatement() {
			return getRuleContext(ReturnStatementContext.class,0);
		}
		public BecomeStatementContext becomeStatement() {
			return getRuleContext(BecomeStatementContext.class,0);
		}
		public WhileStatementContext whileStatement() {
			return getRuleContext(WhileStatementContext.class,0);
		}
		public ForStatementContext forStatement() {
			return getRuleContext(ForStatementContext.class,0);
		}
		public BreakStatementContext breakStatement() {
			return getRuleContext(BreakStatementContext.class,0);
		}
		public ContinueStatementContext continueStatement() {
			return getRuleContext(ContinueStatementContext.class,0);
		}
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public TerminalNode SEMI() { return getToken(CocoParser.SEMI, 0); }
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(727);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(724);
				attribute();
				}
				}
				setState(729);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(745);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				setState(730);
				expression(0);
				setState(732);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEMI) {
					{
					setState(731);
					match(SEMI);
					}
				}

				}
				break;
			case 2:
				{
				setState(734);
				stateInvariant();
				setState(736);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEMI) {
					{
					setState(735);
					match(SEMI);
					}
				}

				}
				break;
			case 3:
				{
				setState(738);
				declarationStatement();
				}
				break;
			case 4:
				{
				setState(739);
				returnStatement();
				}
				break;
			case 5:
				{
				setState(740);
				becomeStatement();
				}
				break;
			case 6:
				{
				setState(741);
				whileStatement();
				}
				break;
			case 7:
				{
				setState(742);
				forStatement();
				}
				break;
			case 8:
				{
				setState(743);
				breakStatement();
				}
				break;
			case 9:
				{
				setState(744);
				continueStatement();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class DeclarationStatementContext extends ParserRuleContext {
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(CocoParser.SEMI, 0); }
		public DeclarationStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declarationStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterDeclarationStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitDeclarationStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitDeclarationStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclarationStatementContext declarationStatement() throws RecognitionException {
		DeclarationStatementContext _localctx = new DeclarationStatementContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_declarationStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(747);
			variableDeclaration();
			setState(748);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ReturnStatementContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(CocoParser.RETURN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(CocoParser.SEMI, 0); }
		public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitReturnStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitReturnStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_returnStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(750);
			match(RETURN);
			setState(751);
			expression(0);
			setState(752);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class BecomeStatementContext extends ParserRuleContext {
		public TerminalNode BECOME() { return getToken(CocoParser.BECOME, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(CocoParser.SEMI, 0); }
		public BecomeStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_becomeStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterBecomeStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitBecomeStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitBecomeStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BecomeStatementContext becomeStatement() throws RecognitionException {
		BecomeStatementContext _localctx = new BecomeStatementContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_becomeStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(754);
			match(BECOME);
			setState(755);
			expression(0);
			setState(756);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class WhileStatementContext extends ParserRuleContext {
		public TerminalNode WHILE() { return getToken(CocoParser.WHILE, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public BlockExpression_Context blockExpression_() {
			return getRuleContext(BlockExpression_Context.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public WhileStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterWhileStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitWhileStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitWhileStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhileStatementContext whileStatement() throws RecognitionException {
		WhileStatementContext _localctx = new WhileStatementContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_whileStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(760);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(758);
				match(IDENTIFIER);
				setState(759);
				match(COLON);
				}
			}

			setState(762);
			match(WHILE);
			setState(763);
			match(LP);
			setState(764);
			expression(0);
			setState(765);
			match(RP);
			setState(766);
			blockExpression_();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ForStatementContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(CocoParser.FOR, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(CocoParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(CocoParser.IDENTIFIER, i);
		}
		public TerminalNode IN() { return getToken(CocoParser.IN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public BlockExpression_Context blockExpression_() {
			return getRuleContext(BlockExpression_Context.class,0);
		}
		public List<TerminalNode> COLON() { return getTokens(CocoParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(CocoParser.COLON, i);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ForStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterForStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitForStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitForStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForStatementContext forStatement() throws RecognitionException {
		ForStatementContext _localctx = new ForStatementContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_forStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(770);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(768);
				match(IDENTIFIER);
				setState(769);
				match(COLON);
				}
			}

			setState(772);
			match(FOR);
			setState(773);
			match(IDENTIFIER);
			setState(776);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(774);
				match(COLON);
				setState(775);
				type(0);
				}
			}

			setState(778);
			match(IN);
			setState(779);
			expression(0);
			setState(780);
			blockExpression_();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class BreakStatementContext extends ParserRuleContext {
		public TerminalNode BREAK() { return getToken(CocoParser.BREAK, 0); }
		public TerminalNode SEMI() { return getToken(CocoParser.SEMI, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public BreakStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterBreakStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitBreakStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitBreakStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BreakStatementContext breakStatement() throws RecognitionException {
		BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_breakStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(782);
			match(BREAK);
			setState(784);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(783);
				match(IDENTIFIER);
				}
			}

			setState(786);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ContinueStatementContext extends ParserRuleContext {
		public TerminalNode CONTINUE() { return getToken(CocoParser.CONTINUE, 0); }
		public TerminalNode SEMI() { return getToken(CocoParser.SEMI, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterContinueStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitContinueStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitContinueStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContinueStatementContext continueStatement() throws RecognitionException {
		ContinueStatementContext _localctx = new ContinueStatementContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_continueStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(788);
			match(CONTINUE);
			setState(790);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(789);
				match(IDENTIFIER);
				}
			}

			setState(792);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class PortElementContext extends ParserRuleContext {
		public EnumDeclarationContext enumDeclaration() {
			return getRuleContext(EnumDeclarationContext.class,0);
		}
		public FunctionInterfaceDeclarationContext functionInterfaceDeclaration() {
			return getRuleContext(FunctionInterfaceDeclarationContext.class,0);
		}
		public SignalDeclarationContext signalDeclaration() {
			return getRuleContext(SignalDeclarationContext.class,0);
		}
		public FieldDeclarationContext fieldDeclaration() {
			return getRuleContext(FieldDeclarationContext.class,0);
		}
		public StateMachineDeclarationContext stateMachineDeclaration() {
			return getRuleContext(StateMachineDeclarationContext.class,0);
		}
		public PortDeclarationContext portDeclaration() {
			return getRuleContext(PortDeclarationContext.class,0);
		}
		public StaticMemberDeclarationContext staticMemberDeclaration() {
			return getRuleContext(StaticMemberDeclarationContext.class,0);
		}
		public StructDeclarationContext structDeclaration() {
			return getRuleContext(StructDeclarationContext.class,0);
		}
		public TypeAliasDeclarationContext typeAliasDeclaration() {
			return getRuleContext(TypeAliasDeclarationContext.class,0);
		}
		public ExternalTypeDeclarationContext externalTypeDeclaration() {
			return getRuleContext(ExternalTypeDeclarationContext.class,0);
		}
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public PortElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_portElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterPortElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitPortElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitPortElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PortElementContext portElement() throws RecognitionException {
		PortElementContext _localctx = new PortElementContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_portElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(797);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(794);
				attribute();
				}
				}
				setState(799);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(810);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ENUM:
				{
				setState(800);
				enumDeclaration();
				}
				break;
			case FUNCTION:
				{
				setState(801);
				functionInterfaceDeclaration();
				}
				break;
			case OUTGOING:
				{
				setState(802);
				signalDeclaration();
				}
				break;
			case VAL:
			case VAR:
			case IDENTIFIER:
				{
				setState(803);
				fieldDeclaration();
				}
				break;
			case MACHINE:
				{
				setState(804);
				stateMachineDeclaration();
				}
				break;
			case PORT:
				{
				setState(805);
				portDeclaration();
				}
				break;
			case STATIC:
				{
				setState(806);
				staticMemberDeclaration();
				}
				break;
			case STRUCT:
				{
				setState(807);
				structDeclaration();
				}
				break;
			case TYPE:
				{
				setState(808);
				typeAliasDeclaration();
				}
				break;
			case EXTERNAL:
				{
				setState(809);
				externalTypeDeclaration();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class FunctionInterfaceDeclarationContext extends ParserRuleContext {
		public TerminalNode FUNCTION() { return getToken(CocoParser.FUNCTION, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public FunctionInterfaceDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionInterfaceDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterFunctionInterfaceDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitFunctionInterfaceDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitFunctionInterfaceDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionInterfaceDeclarationContext functionInterfaceDeclaration() throws RecognitionException {
		FunctionInterfaceDeclarationContext _localctx = new FunctionInterfaceDeclarationContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_functionInterfaceDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(812);
			match(FUNCTION);
			setState(813);
			match(IDENTIFIER);
			setState(814);
			match(LP);
			setState(816);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR || _la==IDENTIFIER) {
				{
				setState(815);
				parameters();
				}
			}

			setState(818);
			match(RP);
			setState(819);
			match(COLON);
			setState(820);
			type(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class SignalDeclarationContext extends ParserRuleContext {
		public TerminalNode OUTGOING() { return getToken(CocoParser.OUTGOING, 0); }
		public TerminalNode SIGNAL() { return getToken(CocoParser.SIGNAL, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public SignalDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_signalDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterSignalDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitSignalDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitSignalDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SignalDeclarationContext signalDeclaration() throws RecognitionException {
		SignalDeclarationContext _localctx = new SignalDeclarationContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_signalDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(822);
			match(OUTGOING);
			setState(823);
			match(SIGNAL);
			setState(824);
			match(IDENTIFIER);
			setState(825);
			match(LP);
			setState(827);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR || _la==IDENTIFIER) {
				{
				setState(826);
				parameters();
				}
			}

			setState(829);
			match(RP);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class StateMachineDeclarationContext extends ParserRuleContext {
		public TerminalNode MACHINE() { return getToken(CocoParser.MACHINE, 0); }
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public List<TerminalNode> IDENTIFIER() { return getTokens(CocoParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(CocoParser.IDENTIFIER, i);
		}
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public List<StateMachineElementContext> stateMachineElement() {
			return getRuleContexts(StateMachineElementContext.class);
		}
		public StateMachineElementContext stateMachineElement(int i) {
			return getRuleContext(StateMachineElementContext.class,i);
		}
		public StateMachineDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stateMachineDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterStateMachineDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitStateMachineDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitStateMachineDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StateMachineDeclarationContext stateMachineDeclaration() throws RecognitionException {
		StateMachineDeclarationContext _localctx = new StateMachineDeclarationContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_stateMachineDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(831);
			match(MACHINE);
			setState(833);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(832);
				match(IDENTIFIER);
				}
			}

			setState(837);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(835);
				match(COLON);
				setState(836);
				match(IDENTIFIER);
				}
			}

			setState(839);
			match(LC);
			setState(843);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AFTER) | (1L << ASSERT) | (1L << ENUM) | (1L << ENTRY) | (1L << EXECUTION) | (1L << EXIT) | (1L << FUNCTION) | (1L << IF) | (1L << PERIODIC) | (1L << PRIVATE) | (1L << SPONTANEOUS) | (1L << STATE) | (1L << STATIC) | (1L << TYPE) | (1L << VAL) | (1L << VAR) | (1L << IDENTIFIER) | (1L << AT))) != 0)) {
				{
				{
				setState(840);
				stateMachineElement();
				}
				}
				setState(845);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(846);
			match(RC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class StateMachineElementContext extends ParserRuleContext {
		public EnumDeclarationContext enumDeclaration() {
			return getRuleContext(EnumDeclarationContext.class,0);
		}
		public EntryFunctionDeclarationContext entryFunctionDeclaration() {
			return getRuleContext(EntryFunctionDeclarationContext.class,0);
		}
		public ExitFunctionDeclarationContext exitFunctionDeclaration() {
			return getRuleContext(ExitFunctionDeclarationContext.class,0);
		}
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public StateInvariantContext stateInvariant() {
			return getRuleContext(StateInvariantContext.class,0);
		}
		public StateDeclarationContext stateDeclaration() {
			return getRuleContext(StateDeclarationContext.class,0);
		}
		public StaticMemberDeclarationContext staticMemberDeclaration() {
			return getRuleContext(StaticMemberDeclarationContext.class,0);
		}
		public TypeAliasDeclarationContext typeAliasDeclaration() {
			return getRuleContext(TypeAliasDeclarationContext.class,0);
		}
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public TransitionDeclarationContext transitionDeclaration() {
			return getRuleContext(TransitionDeclarationContext.class,0);
		}
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public StateMachineElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stateMachineElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterStateMachineElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitStateMachineElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitStateMachineElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StateMachineElementContext stateMachineElement() throws RecognitionException {
		StateMachineElementContext _localctx = new StateMachineElementContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_stateMachineElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(851);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(848);
				attribute();
				}
				}
				setState(853);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(864);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ENUM:
				{
				setState(854);
				enumDeclaration();
				}
				break;
			case ENTRY:
				{
				setState(855);
				entryFunctionDeclaration();
				}
				break;
			case EXIT:
				{
				setState(856);
				exitFunctionDeclaration();
				}
				break;
			case FUNCTION:
				{
				setState(857);
				functionDeclaration();
				}
				break;
			case ASSERT:
				{
				setState(858);
				stateInvariant();
				}
				break;
			case EXECUTION:
			case STATE:
				{
				setState(859);
				stateDeclaration();
				}
				break;
			case STATIC:
				{
				setState(860);
				staticMemberDeclaration();
				}
				break;
			case TYPE:
				{
				setState(861);
				typeAliasDeclaration();
				}
				break;
			case PRIVATE:
			case VAL:
			case VAR:
				{
				setState(862);
				variableDeclaration();
				}
				break;
			case AFTER:
			case IF:
			case PERIODIC:
			case SPONTANEOUS:
			case IDENTIFIER:
				{
				setState(863);
				transitionDeclaration();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class StateDeclarationContext extends ParserRuleContext {
		public EventStateDeclarationContext eventStateDeclaration() {
			return getRuleContext(EventStateDeclarationContext.class,0);
		}
		public ExecutionStateDeclarationContext executionStateDeclaration() {
			return getRuleContext(ExecutionStateDeclarationContext.class,0);
		}
		public StateDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stateDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterStateDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitStateDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitStateDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StateDeclarationContext stateDeclaration() throws RecognitionException {
		StateDeclarationContext _localctx = new StateDeclarationContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_stateDeclaration);
		try {
			setState(868);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STATE:
				enterOuterAlt(_localctx, 1);
				{
				setState(866);
				eventStateDeclaration();
				}
				break;
			case EXECUTION:
				enterOuterAlt(_localctx, 2);
				{
				setState(867);
				executionStateDeclaration();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class EventStateDeclarationContext extends ParserRuleContext {
		public TerminalNode STATE() { return getToken(CocoParser.STATE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public List<EventStateElementContext> eventStateElement() {
			return getRuleContexts(EventStateElementContext.class);
		}
		public EventStateElementContext eventStateElement(int i) {
			return getRuleContext(EventStateElementContext.class,i);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public EventStateDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventStateDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterEventStateDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitEventStateDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitEventStateDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventStateDeclarationContext eventStateDeclaration() throws RecognitionException {
		EventStateDeclarationContext _localctx = new EventStateDeclarationContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_eventStateDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(870);
			match(STATE);
			setState(871);
			match(IDENTIFIER);
			setState(877);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LP) {
				{
				setState(872);
				match(LP);
				setState(874);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR || _la==IDENTIFIER) {
					{
					setState(873);
					parameters();
					}
				}

				setState(876);
				match(RP);
				}
			}

			setState(879);
			match(LC);
			setState(883);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AFTER) | (1L << ASSERT) | (1L << ENUM) | (1L << ENTRY) | (1L << EXECUTION) | (1L << EXIT) | (1L << FUNCTION) | (1L << IF) | (1L << PERIODIC) | (1L << PRIVATE) | (1L << SPONTANEOUS) | (1L << STATE) | (1L << STATIC) | (1L << STRUCT) | (1L << TYPE) | (1L << VAL) | (1L << VAR) | (1L << IDENTIFIER) | (1L << AT))) != 0)) {
				{
				{
				setState(880);
				eventStateElement();
				}
				}
				setState(885);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(886);
			match(RC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ExecutionStateDeclarationContext extends ParserRuleContext {
		public TerminalNode EXECUTION() { return getToken(CocoParser.EXECUTION, 0); }
		public TerminalNode STATE() { return getToken(CocoParser.STATE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public BlockExpression_Context blockExpression_() {
			return getRuleContext(BlockExpression_Context.class,0);
		}
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public ExecutionStateDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_executionStateDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterExecutionStateDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitExecutionStateDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitExecutionStateDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExecutionStateDeclarationContext executionStateDeclaration() throws RecognitionException {
		ExecutionStateDeclarationContext _localctx = new ExecutionStateDeclarationContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_executionStateDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(888);
			match(EXECUTION);
			setState(889);
			match(STATE);
			setState(890);
			match(IDENTIFIER);
			setState(896);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LP) {
				{
				setState(891);
				match(LP);
				setState(893);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR || _la==IDENTIFIER) {
					{
					setState(892);
					parameters();
					}
				}

				setState(895);
				match(RP);
				}
			}

			setState(898);
			blockExpression_();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class EventStateElementContext extends ParserRuleContext {
		public EnumDeclarationContext enumDeclaration() {
			return getRuleContext(EnumDeclarationContext.class,0);
		}
		public EntryFunctionDeclarationContext entryFunctionDeclaration() {
			return getRuleContext(EntryFunctionDeclarationContext.class,0);
		}
		public ExitFunctionDeclarationContext exitFunctionDeclaration() {
			return getRuleContext(ExitFunctionDeclarationContext.class,0);
		}
		public FunctionDeclarationContext functionDeclaration() {
			return getRuleContext(FunctionDeclarationContext.class,0);
		}
		public StateDeclarationContext stateDeclaration() {
			return getRuleContext(StateDeclarationContext.class,0);
		}
		public StateInvariantContext stateInvariant() {
			return getRuleContext(StateInvariantContext.class,0);
		}
		public StaticMemberDeclarationContext staticMemberDeclaration() {
			return getRuleContext(StaticMemberDeclarationContext.class,0);
		}
		public StructDeclarationContext structDeclaration() {
			return getRuleContext(StructDeclarationContext.class,0);
		}
		public TransitionDeclarationContext transitionDeclaration() {
			return getRuleContext(TransitionDeclarationContext.class,0);
		}
		public TypeAliasDeclarationContext typeAliasDeclaration() {
			return getRuleContext(TypeAliasDeclarationContext.class,0);
		}
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public EventStateElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventStateElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterEventStateElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitEventStateElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitEventStateElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventStateElementContext eventStateElement() throws RecognitionException {
		EventStateElementContext _localctx = new EventStateElementContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_eventStateElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(903);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(900);
				attribute();
				}
				}
				setState(905);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(917);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ENUM:
				{
				setState(906);
				enumDeclaration();
				}
				break;
			case ENTRY:
				{
				setState(907);
				entryFunctionDeclaration();
				}
				break;
			case EXIT:
				{
				setState(908);
				exitFunctionDeclaration();
				}
				break;
			case FUNCTION:
				{
				setState(909);
				functionDeclaration();
				}
				break;
			case EXECUTION:
			case STATE:
				{
				setState(910);
				stateDeclaration();
				}
				break;
			case ASSERT:
				{
				setState(911);
				stateInvariant();
				}
				break;
			case STATIC:
				{
				setState(912);
				staticMemberDeclaration();
				}
				break;
			case STRUCT:
				{
				setState(913);
				structDeclaration();
				}
				break;
			case AFTER:
			case IF:
			case PERIODIC:
			case SPONTANEOUS:
			case IDENTIFIER:
				{
				setState(914);
				transitionDeclaration();
				}
				break;
			case TYPE:
				{
				setState(915);
				typeAliasDeclaration();
				}
				break;
			case PRIVATE:
			case VAL:
			case VAR:
				{
				setState(916);
				variableDeclaration();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class EntryFunctionDeclarationContext extends ParserRuleContext {
		public TerminalNode ENTRY() { return getToken(CocoParser.ENTRY, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public EntryFunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entryFunctionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterEntryFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitEntryFunctionDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitEntryFunctionDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntryFunctionDeclarationContext entryFunctionDeclaration() throws RecognitionException {
		EntryFunctionDeclarationContext _localctx = new EntryFunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_entryFunctionDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(919);
			match(ENTRY);
			setState(920);
			match(LP);
			setState(921);
			match(RP);
			setState(922);
			match(ASSIGN);
			setState(923);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ExitFunctionDeclarationContext extends ParserRuleContext {
		public TerminalNode EXIT() { return getToken(CocoParser.EXIT, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExitFunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exitFunctionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterExitFunctionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitExitFunctionDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitExitFunctionDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExitFunctionDeclarationContext exitFunctionDeclaration() throws RecognitionException {
		ExitFunctionDeclarationContext _localctx = new ExitFunctionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_exitFunctionDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(925);
			match(EXIT);
			setState(926);
			match(LP);
			setState(927);
			match(RP);
			setState(928);
			match(ASSIGN);
			setState(929);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class StateInvariantContext extends ParserRuleContext {
		public TerminalNode ASSERT() { return getToken(CocoParser.ASSERT, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public StateInvariantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stateInvariant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterStateInvariant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitStateInvariant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitStateInvariant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StateInvariantContext stateInvariant() throws RecognitionException {
		StateInvariantContext _localctx = new StateInvariantContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_stateInvariant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(931);
			match(ASSERT);
			setState(932);
			match(LP);
			setState(933);
			expression(0);
			setState(934);
			match(RP);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class TransitionDeclarationContext extends ParserRuleContext {
		public EventTransitionContext eventTransition() {
			return getRuleContext(EventTransitionContext.class,0);
		}
		public SpontaneousTransitionContext spontaneousTransition() {
			return getRuleContext(SpontaneousTransitionContext.class,0);
		}
		public TimerTransitionContext timerTransition() {
			return getRuleContext(TimerTransitionContext.class,0);
		}
		public TransitionDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transitionDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterTransitionDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitTransitionDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitTransitionDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TransitionDeclarationContext transitionDeclaration() throws RecognitionException {
		TransitionDeclarationContext _localctx = new TransitionDeclarationContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_transitionDeclaration);
		try {
			setState(939);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,105,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(936);
				eventTransition();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(937);
				spontaneousTransition();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(938);
				timerTransition();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class EventTransitionContext extends ParserRuleContext {
		public DotIdentifierListContext dotIdentifierList() {
			return getRuleContext(DotIdentifierListContext.class,0);
		}
		public List<TerminalNode> LP() { return getTokens(CocoParser.LP); }
		public TerminalNode LP(int i) {
			return getToken(CocoParser.LP, i);
		}
		public List<TerminalNode> RP() { return getTokens(CocoParser.RP); }
		public TerminalNode RP(int i) {
			return getToken(CocoParser.RP, i);
		}
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public EventHandlerContext eventHandler() {
			return getRuleContext(EventHandlerContext.class,0);
		}
		public TerminalNode IF() { return getToken(CocoParser.IF, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<EventSourceContext> eventSource() {
			return getRuleContexts(EventSourceContext.class);
		}
		public EventSourceContext eventSource(int i) {
			return getRuleContext(EventSourceContext.class,i);
		}
		public List<TerminalNode> DOT() { return getTokens(CocoParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(CocoParser.DOT, i);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public EventTransitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventTransition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterEventTransition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitEventTransition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitEventTransition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventTransitionContext eventTransition() throws RecognitionException {
		EventTransitionContext _localctx = new EventTransitionContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_eventTransition);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(946);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IF) {
				{
				setState(941);
				match(IF);
				setState(942);
				match(LP);
				setState(943);
				expression(0);
				setState(944);
				match(RP);
				}
			}

			setState(953);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,107,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(948);
					eventSource();
					setState(949);
					match(DOT);
					}
					} 
				}
				setState(955);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,107,_ctx);
			}
			setState(956);
			dotIdentifierList();
			setState(957);
			match(LP);
			setState(959);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR || _la==IDENTIFIER) {
				{
				setState(958);
				parameters();
				}
			}

			setState(961);
			match(RP);
			setState(962);
			match(ASSIGN);
			setState(963);
			eventHandler();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class EventSourceContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode LB() { return getToken(CocoParser.LB, 0); }
		public PatternContext pattern() {
			return getRuleContext(PatternContext.class,0);
		}
		public TerminalNode RB() { return getToken(CocoParser.RB, 0); }
		public TerminalNode PIPE() { return getToken(CocoParser.PIPE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public EventSourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventSource; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterEventSource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitEventSource(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitEventSource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventSourceContext eventSource() throws RecognitionException {
		EventSourceContext _localctx = new EventSourceContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_eventSource);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(965);
			match(IDENTIFIER);
			setState(974);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LB) {
				{
				setState(966);
				match(LB);
				setState(967);
				pattern();
				setState(970);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==PIPE) {
					{
					setState(968);
					match(PIPE);
					setState(969);
					expression(0);
					}
				}

				setState(972);
				match(RB);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class SpontaneousTransitionContext extends ParserRuleContext {
		public TerminalNode SPONTANEOUS() { return getToken(CocoParser.SPONTANEOUS, 0); }
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode IF() { return getToken(CocoParser.IF, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public SpontaneousTransitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spontaneousTransition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterSpontaneousTransition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitSpontaneousTransition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitSpontaneousTransition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpontaneousTransitionContext spontaneousTransition() throws RecognitionException {
		SpontaneousTransitionContext _localctx = new SpontaneousTransitionContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_spontaneousTransition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(981);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IF) {
				{
				setState(976);
				match(IF);
				setState(977);
				match(LP);
				setState(978);
				expression(0);
				setState(979);
				match(RP);
				}
			}

			setState(983);
			match(SPONTANEOUS);
			setState(984);
			match(ASSIGN);
			setState(985);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class TimerTransitionContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode ASSIGN() { return getToken(CocoParser.ASSIGN, 0); }
		public TerminalNode AFTER() { return getToken(CocoParser.AFTER, 0); }
		public TerminalNode PERIODIC() { return getToken(CocoParser.PERIODIC, 0); }
		public TimerTransitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_timerTransition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterTimerTransition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitTimerTransition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitTimerTransition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimerTransitionContext timerTransition() throws RecognitionException {
		TimerTransitionContext _localctx = new TimerTransitionContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_timerTransition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(987);
			_la = _input.LA(1);
			if ( !(_la==AFTER || _la==PERIODIC) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(988);
			match(LP);
			setState(989);
			expression(0);
			setState(990);
			match(RP);
			setState(991);
			match(ASSIGN);
			setState(992);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class EventHandlerContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ILLEGAL() { return getToken(CocoParser.ILLEGAL, 0); }
		public OfferContext offer() {
			return getRuleContext(OfferContext.class,0);
		}
		public EventHandlerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventHandler; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterEventHandler(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitEventHandler(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitEventHandler(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventHandlerContext eventHandler() throws RecognitionException {
		EventHandlerContext _localctx = new EventHandlerContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_eventHandler);
		try {
			setState(997);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ASSERT:
			case EXTERNAL:
			case IF:
			case MATCH:
			case NONDET:
			case OPTIONAL:
			case IDENTIFIER:
			case LP:
			case LC:
			case LB:
			case DOT:
			case MINUS:
			case AMP:
			case EXCL:
			case INTEGER:
			case CHAR_LITERAL:
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(994);
				expression(0);
				}
				break;
			case ILLEGAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(995);
				match(ILLEGAL);
				}
				break;
			case OFFER:
				enterOuterAlt(_localctx, 3);
				{
				setState(996);
				offer();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class OfferContext extends ParserRuleContext {
		public TerminalNode OFFER() { return getToken(CocoParser.OFFER, 0); }
		public TerminalNode LC() { return getToken(CocoParser.LC, 0); }
		public OfferClausesContext offerClauses() {
			return getRuleContext(OfferClausesContext.class,0);
		}
		public TerminalNode RC() { return getToken(CocoParser.RC, 0); }
		public TerminalNode OTHERWISE() { return getToken(CocoParser.OTHERWISE, 0); }
		public EventHandlerContext eventHandler() {
			return getRuleContext(EventHandlerContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(CocoParser.COMMA, 0); }
		public OfferContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_offer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterOffer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitOffer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitOffer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OfferContext offer() throws RecognitionException {
		OfferContext _localctx = new OfferContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_offer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(999);
			match(OFFER);
			setState(1000);
			match(LC);
			setState(1001);
			offerClauses();
			setState(1007);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OTHERWISE) {
				{
				setState(1002);
				match(OTHERWISE);
				setState(1003);
				eventHandler();
				setState(1005);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(1004);
					match(COMMA);
					}
				}

				}
			}

			setState(1009);
			match(RC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class OfferClausesContext extends ParserRuleContext {
		public List<OfferClauseContext> offerClause() {
			return getRuleContexts(OfferClauseContext.class);
		}
		public OfferClauseContext offerClause(int i) {
			return getRuleContext(OfferClauseContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CocoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CocoParser.COMMA, i);
		}
		public OfferClausesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_offerClauses; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterOfferClauses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitOfferClauses(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitOfferClauses(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OfferClausesContext offerClauses() throws RecognitionException {
		OfferClausesContext _localctx = new OfferClausesContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_offerClauses);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1011);
			offerClause();
			setState(1016);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,115,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1012);
					match(COMMA);
					setState(1013);
					offerClause();
					}
					} 
				}
				setState(1018);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,115,_ctx);
			}
			setState(1020);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1019);
				match(COMMA);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class OfferClauseContext extends ParserRuleContext {
		public EventHandlerContext eventHandler() {
			return getRuleContext(EventHandlerContext.class,0);
		}
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public TerminalNode IF() { return getToken(CocoParser.IF, 0); }
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public OfferClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_offerClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterOfferClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitOfferClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitOfferClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OfferClauseContext offerClause() throws RecognitionException {
		OfferClauseContext _localctx = new OfferClauseContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_offerClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1025);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1022);
				attribute();
				}
				}
				setState(1027);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1033);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,118,_ctx) ) {
			case 1:
				{
				setState(1028);
				match(IF);
				setState(1029);
				match(LP);
				setState(1030);
				expression(0);
				setState(1031);
				match(RP);
				}
				break;
			}
			setState(1035);
			eventHandler();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ParametersContext extends ParserRuleContext {
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CocoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CocoParser.COMMA, i);
		}
		public ParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParametersContext parameters() throws RecognitionException {
		ParametersContext _localctx = new ParametersContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_parameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1037);
			parameter();
			setState(1042);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1038);
				match(COMMA);
				setState(1039);
				parameter();
				}
				}
				setState(1044);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ParameterContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TerminalNode VAR() { return getToken(CocoParser.VAR, 0); }
		public TerminalNode ELLIP() { return getToken(CocoParser.ELLIP, 0); }
		public TerminalNode COLON() { return getToken(CocoParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public GenericTypeDeclarationContext genericTypeDeclaration() {
			return getRuleContext(GenericTypeDeclarationContext.class,0);
		}
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_parameter);
		int _la;
		try {
			setState(1058);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,123,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1046);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR) {
					{
					setState(1045);
					match(VAR);
					}
				}

				setState(1048);
				match(IDENTIFIER);
				setState(1050);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELLIP) {
					{
					setState(1049);
					match(ELLIP);
					}
				}

				setState(1054);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(1052);
					match(COLON);
					setState(1053);
					type(0);
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1056);
				match(IDENTIFIER);
				setState(1057);
				genericTypeDeclaration();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class LiteralExpression_Context extends ParserRuleContext {
		public TerminalNode INTEGER() { return getToken(CocoParser.INTEGER, 0); }
		public TerminalNode CHAR_LITERAL() { return getToken(CocoParser.CHAR_LITERAL, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(CocoParser.STRING_LITERAL, 0); }
		public LiteralExpression_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literalExpression_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterLiteralExpression_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitLiteralExpression_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitLiteralExpression_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralExpression_Context literalExpression_() throws RecognitionException {
		LiteralExpression_Context _localctx = new LiteralExpression_Context(_ctx, getState());
		enterRule(_localctx, 158, RULE_literalExpression_);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1060);
			_la = _input.LA(1);
			if ( !(((((_la - 87)) & ~0x3f) == 0 && ((1L << (_la - 87)) & ((1L << (INTEGER - 87)) | (1L << (CHAR_LITERAL - 87)) | (1L << (STRING_LITERAL - 87)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
	 
		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class BinaryTypeContext extends TypeContext {
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public TerminalNode MUL() { return getToken(CocoParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(CocoParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(CocoParser.MOD, 0); }
		public TerminalNode PLUS() { return getToken(CocoParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(CocoParser.MINUS, 0); }
		public BinaryTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterBinaryType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitBinaryType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitBinaryType(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class GroupTypeContext extends TypeContext {
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public GroupTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterGroupType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitGroupType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitGroupType(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class FunctionTypeContext extends TypeContext {
		public TerminalNode LP() { return getToken(CocoParser.LP, 0); }
		public TypesContext types() {
			return getRuleContext(TypesContext.class,0);
		}
		public TerminalNode RP() { return getToken(CocoParser.RP, 0); }
		public TerminalNode ARROW() { return getToken(CocoParser.ARROW, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FunctionTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterFunctionType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitFunctionType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitFunctionType(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class UnaryTypeContext extends TypeContext {
		public TerminalNode MINUS() { return getToken(CocoParser.MINUS, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public UnaryTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterUnaryType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitUnaryType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitUnaryType(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class LiteralTypeContext extends TypeContext {
		public LiteralExpression_Context literalExpression_() {
			return getRuleContext(LiteralExpression_Context.class,0);
		}
		public LiteralTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterLiteralType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitLiteralType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitLiteralType(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class TypeReferenceContext extends TypeContext {
		public DotIdentifierListContext dotIdentifierList() {
			return getRuleContext(DotIdentifierListContext.class,0);
		}
		public TerminalNode LT() { return getToken(CocoParser.LT, 0); }
		public TypesContext types() {
			return getRuleContext(TypesContext.class,0);
		}
		public TerminalNode GT() { return getToken(CocoParser.GT, 0); }
		public TerminalNode DOT() { return getToken(CocoParser.DOT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(CocoParser.IDENTIFIER, 0); }
		public TypeReferenceContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterTypeReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitTypeReference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitTypeReference(this);
			else return visitor.visitChildren(this);
		}
	}
	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class ReferenceTypeContext extends TypeContext {
		public TerminalNode AMP() { return getToken(CocoParser.AMP, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode MUT() { return getToken(CocoParser.MUT, 0); }
		public TerminalNode OUT() { return getToken(CocoParser.OUT, 0); }
		public ReferenceTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterReferenceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitReferenceType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitReferenceType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		return type(0);
	}

	private TypeContext type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeContext _localctx = new TypeContext(_ctx, _parentState);
		TypeContext _prevctx = _localctx;
		int _startState = 160;
		enterRecursionRule(_localctx, 160, RULE_type, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1092);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,127,_ctx) ) {
			case 1:
				{
				_localctx = new GroupTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(1063);
				match(LP);
				setState(1064);
				type(0);
				setState(1065);
				match(RP);
				}
				break;
			case 2:
				{
				_localctx = new TypeReferenceContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1067);
				dotIdentifierList();
				setState(1072);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,124,_ctx) ) {
				case 1:
					{
					setState(1068);
					match(LT);
					setState(1069);
					types();
					setState(1070);
					match(GT);
					}
					break;
				}
				setState(1076);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,125,_ctx) ) {
				case 1:
					{
					setState(1074);
					match(DOT);
					setState(1075);
					match(IDENTIFIER);
					}
					break;
				}
				}
				break;
			case 3:
				{
				_localctx = new FunctionTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1078);
				match(LP);
				setState(1079);
				types();
				setState(1080);
				match(RP);
				setState(1081);
				match(ARROW);
				setState(1082);
				type(4);
				}
				break;
			case 4:
				{
				_localctx = new LiteralTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1084);
				literalExpression_();
				}
				break;
			case 5:
				{
				_localctx = new ReferenceTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1085);
				match(AMP);
				setState(1087);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MUT || _la==OUT) {
					{
					setState(1086);
					_la = _input.LA(1);
					if ( !(_la==MUT || _la==OUT) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(1089);
				type(2);
				}
				break;
			case 6:
				{
				_localctx = new UnaryTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(1090);
				match(MINUS);
				setState(1091);
				type(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(1102);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,129,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1100);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,128,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryTypeContext(new TypeContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_type);
						setState(1094);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(1095);
						_la = _input.LA(1);
						if ( !(((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (MUL - 65)) | (1L << (DIV - 65)) | (1L << (MOD - 65)))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1096);
						type(9);
						}
						break;
					case 2:
						{
						_localctx = new BinaryTypeContext(new TypeContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_type);
						setState(1097);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(1098);
						_la = _input.LA(1);
						if ( !(_la==MINUS || _la==PLUS) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1099);
						type(8);
						}
						break;
					}
					} 
				}
				setState(1104);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,129,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class TypesContext extends ParserRuleContext {
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(CocoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(CocoParser.COMMA, i);
		}
		public TypesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_types; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterTypes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitTypes(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitTypes(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypesContext types() throws RecognitionException {
		TypesContext _localctx = new TypesContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_types);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1105);
			type(0);
			setState(1110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1106);
				match(COMMA);
				setState(1107);
				type(0);
				}
				}
				setState(1112);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class DotIdentifierListContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(CocoParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(CocoParser.IDENTIFIER, i);
		}
		public List<TerminalNode> DOT() { return getTokens(CocoParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(CocoParser.DOT, i);
		}
		public DotIdentifierListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dotIdentifierList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.enterDotIdentifierList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CocoListener cocoListener ) cocoListener.exitDotIdentifierList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CocoVisitor<? extends T> cocoVisitor ) return cocoVisitor.visitDotIdentifierList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DotIdentifierListContext dotIdentifierList() throws RecognitionException {
		DotIdentifierListContext _localctx = new DotIdentifierListContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_dotIdentifierList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1113);
			match(IDENTIFIER);
			setState(1118);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,131,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1114);
					match(DOT);
					setState(1115);
					match(IDENTIFIER);
					}
					} 
				}
				setState(1120);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,131,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 29:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 80:
			return type_sempred((TypeContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 12);
		case 1:
			return precpred(_ctx, 11);
		case 2:
			return precpred(_ctx, 10);
		case 3:
			return precpred(_ctx, 9);
		case 4:
			return precpred(_ctx, 20);
		case 5:
			return precpred(_ctx, 18);
		case 6:
			return precpred(_ctx, 17);
		case 7:
			return precpred(_ctx, 16);
		case 8:
			return precpred(_ctx, 15);
		case 9:
			return precpred(_ctx, 13);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 10:
			return precpred(_ctx, 8);
		case 11:
			return precpred(_ctx, 7);
		}
		return true;
	}

	public static final String _serializedATN =
		"""
        悋Ꜫ脳맭䅼㯧瞆奤\\Ѥ	\
        									
        	
        	\
        							\
        							\
        						 	 !\
        	!"	"#	#$	$%	%&	&'	'(	()	)*	*+	+\
        ,	,-	-.	./	/0	01	12	23	34	\
        45	56	67	78	89	9:	:;	;<	<=	=\
        >	>?	?@	@A	AB	BC	CD	DE	EF	FG	GH	HI\
        	IJ	JK	KL	LM	MN	NO	OP	PQ	QR	RS	ST	T\
        ª
        ­²
        \
        µ\
        Ä
        Ì
        \
        Ó
        Ö
        Ú
        ß
        â
        ç
        ë
        \
        ï
        ô
        ø
        \
        û				Ă
        				Ć
        			ĉ			
        
        
        
        Đ
        
        
        
        
        Ę
        Ĝ
        \
        ħ
        \
        Ĳ
        ĵ
        Ĺ
        \
        ļŁ
        Ň
        ŋ
        Ŏ\
        ş
        Ţť
        \
        Ū
        ŭ
        Ŵ
        ƀ
        \
        Ƈ
        Ɗ\
        ƍ
        ƒ
        Ɩ
        ƚ
        Ơ
        \
        ƣ
        Ƨ
        Ƭ
        ƯƷ
        ƻ
        ƾ
        ǁ
        \
        ǈ
        ǋ
        \
        ǎǕ
        ǘ
        ǥ
        \
        ǵ
        \
        Ȋ
        \
        \
        Ȟ
        \
        Ȱ
        ȳ\
           ȷ
           Ⱥ   Ƚ
           !!!\
        !!!!!Ɉ
        !"""Ɍ
        """""""\
        ""######ɛ
        ###ɞ
        ###$$$$ɥ
        $$$ɨ$$$ɫ
        $%%%%&&&&ɴ
        &\
        &&ɷ&&&ɺ
        &''''''ʁ
        ''\
        '((((ʈ
        (((ʋ(((ʎ
        ())))\
        ****ʗ
        *+++++++ʟ
        +,,,,ʤ
        ,,,ʧ,,,,,,ʭ
        ,,,ʰ,,ʲ
        ,----ʷ
        ---ʺ
        -.....ˀ
        .//\
        //˅
        ///ˈ/00ˋ
        00011\
        11˒
        111˕122˘
        22\
        2˛2222˟
        2222ˣ
        2\
        22222222ˬ
        23334\
        4445555666˻
        666\
        6666777̅
        777777\
        ̋
        77777888̓
        888999̙
        999::̞
        :::̡::::::::::\
        ::̭
        :;;;;;̳
        ;;;;;<<<<<<̾
        <<<===̈́
        ====͈
        ====͌
        ==\
        =͏===>>͔
        >>>͗>>>>>>>\
        >>>>>ͣ
        >???ͧ
        ?@@@@@ͭ
        @@\
        @Ͱ
        @@@@ʹ
        @@@ͷ@@@AAAAAA\
        ΀
        AAA΃
        AAABBΈ
        BBB΋BBB\
        BBBBBBBBBBΘ
        BCCCCCCDDDD\
        DDEEEEEFFFFή
        FGGGGGGε
        G\
        GGGGκ
        GGGνGGGGGς
        GGGGG\
        HHHHHHύ
        HHHHϑ
        HIIIIIIϘ
        IIIIIJJJJJJJKKKKϨ
        KLLLL\
        LLLϰ
        LLϲ
        LLLMMMMϹ
        MMMϼ\
        MMMϿ
        MNNЂ
        NNNЅNNNNNNN\
        Ќ
        NNNOOOOГ
        OOOЖOPPЙ
        P\
        PPPН
        PPPPС
        PPPPХ
        PQQRRR\
        RRRRRRRRг
        RRRRз
        RRRRRRR\
        RRRRт
        RRRRRч
        RRRRRRRRя
        RRRђRSSSSї
        SSSњSTTTTџ
        TTTѢTT<¢U
        \
         "$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\
        \
         ¢¤¦01EEJJM\
        MCDFFEEGGABOT>?00@@%%YY[\\\
        ##Ӡ«³ÅÍ\
        
        ×áðþ\
        ČĔģĭ\
        ŀőř Ŭ"\
        Ů$Ż&ƃ(ƌ*ƙ\
        ,ƛ.ƨ0ư2ƺ4\
        ǀ6ǌ8Ǚ:ǡ<ȉ\
        >ȴ@ɀBɋDɕFɡ\
        HɬJɰLʀNʄP\
        ʏRʖTʘVʱXʳ\
        Zʻ\\ˁ^ˊ`ˎb˙\
        d˭f˰h˴j˺l\
        ̄n̐p̖r̟t̮\
        v̸x́z͕|ͦ~ͨ\
        ͺΉΙ\
        ΟΥέδ\
        χϗϝ\
        ϧϩϵЃ\
        ЏФ Ц¢\
        ц¤ѓ¦ћ¨ª\
        ©¨ª­«©«\
        ¬¬®­«®¯\
        ¯°²±°²µ\
        ³±³´´Ãµ\
        ³¶Ä
        ·Ä¸Ä\
        ¹Ä	ºÄ
        »Ä¼\
        Ä½Ä¾Ä¿Ä\
        ÀÄ"ÁÄÂÄ\
        Ã¶Ã·Ã¸Ã\
        ¹ÃºÃ»Ã¼\
        Ã½Ã¾Ã¿Ã\
        ÀÃÁÃÂÄ\
        ÅÆ5ÆË¦TÇÈ8È\
        É`1ÉÊ9ÊÌËÇ\
        ËÌÌÍÎÎÏ\
        5ÏÕ¦TÐÒ8ÑÓ\
        OÒÑÒÓÓÔÔ\
        Ö9ÕÐÕÖÖ	\
        ×ÙØÚ/ÙØÙÚ\
        ÚÛÛÞ¦TÜÝ\
        Ýß4ÞÜÞßß\
        àâ'áàáâ\
        âããä	äæ4å\
        ç$æåæççê\
        èé7éë¢Rêèê\
        ëëîìí6íï\
        <îìîïïðñ\
        ñó4òô$óò\
        óôôõõù:öø\
        *÷öøûù÷ù\
        úúüûùüý;\
        ýþÿ-ÿā4ĀĂ\
        $āĀāĂĂăă\
        ć:ĄĆ2ąĄĆĉ\
        ćąćĈĈĊĉ\
        ćĊċ;ċČč.\
        čď4ĎĐ$ďĎďĐ\
        ĐđđĒ6Ēē¢\
        RēĔĕĕė4ĖĘ\
        $ėĖėĘĘęę\
        ě8ĚĜOěĚěĜ\
        ĜĝĝĞ9Ğğ7ğ\
        Ġ¢RĠġ6ġĢ<Ģ\
        ģĤĤĦ4ĥħ$Ħ\
        ĥĦħħĨĨĩ8\
        ĩĪOĪī9īĬ> Ĭ\
        ĭĮ&Įı4įİ7\
        İĲ¤SıįıĲĲ\
        ĴĳĵĴĳĴĵ\
        ĵĶĶĺ:ķĹr:ĸ\
        ķĹļĺĸĺĻ\
        ĻĽļĺĽľ;ľ\
        ĿŁŀĿŀŁ\
        ŁłłŃ
        Ńņ4ń\
        Ņ7ŅŇ¢RņńņŇ\
        ŇňňŌ:ŉŋ6\
        ŊŉŋŎŌŊŌō\
        ōŏŎŌŏŐ;Ő\
        őŒŒœ0œŔ4\
        Ŕŕ7ŕŖ¢RŖŗ6\
        ŗŘ<ŘřŚŚś\
        .śŤ4ŜŠ:ŝş Ş\
        ŝşŢŠŞŠš\
        šţŢŠţť;Ť\
        ŜŤťťŦŭ8\
        ŧŭŨŪ	ũŨũŪ\
        Ūūūŭ"ŬŦ\
        ŬŧŬũŭ!Ůů\
        ůŰŰű4űų8Ų\
        ŴOųŲųŴŴŵ\
        ŵŶ9Ŷŷ7ŷŸ¢R\
        ŸŹ6Źź<ź#Żż\
        Ażſ&Žž2žƀ`1ſ\
        ŽſƀƀƁƁƂB\
        Ƃ%ƃƈ(Ƅƅ>ƅƇ\
        (ƆƄƇƊƈƆƈ\
        ƉƉ'ƊƈƋƍ0\
        ƌƋƌƍƍƎƎƑ\
        4ƏƐ7Ɛƒ¢RƑƏ\
        Ƒƒƒ)Ɠƚ,ƔƖ\
        	ƕƔƕƖƖƗƗ\
        ƚƘƚ8ƙƓƙƕ\
        ƙƘƚ+ƛƜ	ƜƢ\
        4ƝƟ8ƞƠ.ƟƞƟ\
        ƠƠơơƣ9ƢƝ\
        ƢƣƣƦƤƥ6ƥ\
        Ƨ<ƦƤƦƧƧ-\
        ƨƭ0Ʃƪ>ƪƬ0ƫ\
        ƩƬƯƭƫƭƮ\
        Ʈ/ƯƭưƱ4ƱƲ\
        7ƲƳ¢RƳ1ƴƻ4\
        ƵƷ	ƶƵƶƷƷƸ\
        Ƹƻƹƻ8ƺƴ\
        ƺƶƺƹƻƽƼƾ\
        ?ƽƼƽƾƾ3ƿ\
        ǁ	ǀƿǀǁǁǂ\
        ǂǃ4ǃǄ7ǄǇ¢R\
        ǅǆ6ǆǈ<ǇǅǇǈ\
        ǈ5ǉǋǊǉǋ\
        ǎǌǊǌǍǍǔ\
        ǎǌǏǕ4ǐǕǑ\
        Ǖ:ǒǕx=ǓǕ8ǔǏ\
        ǔǐǔǑǔǒǔǓ\
        ǕǗǖǘ?ǗǖǗ\
        ǘǘ7Ǚǚ,ǚǛ0\
        Ǜǜ4ǜǝ7ǝǞ¢RǞ\
        ǟ6ǟǠ<Ǡ9ǡǢ\
        ǢǤ8ǣǥOǤǣǤ\
        ǥǥǦǦǧ9ǧǨ6\
        Ǩǩ> ǩ;ǪǫǫȊ\
         QǬǭǭȊZǮȊ"\
        ǯǴ¦TǰǱAǱǲ&ǲ\
        ǳBǳǵǴǰǴǵ\
        ǵȊǶȊEǷǸ	Ǹ\
        Ȋ<ǹȊ@!ǺȊB"ǻǼ@\
        ǼȊ4ǽǾ8Ǿǿ`1ǿȀ\
        9ȀȊȁȂ<Ȃȃ<ȃ\
        Ȅ=ȄȊȅȊD#Ȇȇ!\
        ȇȊ<ȈȊ> ȉǪȉǬ\
        ȉǮȉǯȉǶȉ\
        ǷȉǹȉǺȉǻ\
        ȉǽȉȁȉȅȉ\
        ȆȉȈȊȱȋȌ\
        Ȍȍ	ȍȰ<Ȏȏȏ\
        Ȑ	ȐȰ<ȑȒȒȓ	\
        ȓȰ<ȔȕȕȖ6Ȗ\
        Ȱ<ȗȘȘș@șȰ4\
        Țțțȝ8ȜȞ`1ȝ\
        ȜȝȞȞȟȟȰ9\
        ȠȡȡȢ<Ȣȣ<ȣ\
        Ȥ=ȤȰȥȦȦȧ:\
        ȧȨF$Ȩȩ;ȩȰȪȫ\
        ȫȰKȬȭȭȮ\
        ȮȰ¢RȯȋȯȎȯ\
        ȑȯȔȯȗȯȚ\
        ȯȠȯȥȯȪȯ\
        ȬȰȳȱȯȱȲ\
        Ȳ=ȳȱȴȸ:ȵȷ\
        b2ȶȵȷȺȸȶȸ\
        ȹȹȼȺȸȻȽ<\
        ȼȻȼȽȽȾȾ\
        ȿ;ȿ?ɀɁɁɂ8ɂ\
        Ƀ<ɃɄ9Ʉɇ<ɅɆ\
        ɆɈ<ɇɅɇɈɈ\
        AɉɊ4ɊɌ7ɋɉ\
        ɋɌɌɍɍɎɎ\
        ɏ8ɏɐ<ɐɑ9ɑɒ:\
        ɒɓN(ɓɔ;ɔCɕɖ\
        ɖɗ:ɗɚJ&ɘə"əɛ\
        <ɚɘɚɛɛɝɜ\
        ɞ	ɝɜɝɞɞɟ\
        ɟɠ;ɠEɡɦH%ɢɣ\
        >ɣɥH%ɤɢɥɨɦ\
        ɤɦɧɧɪɨɦ\
        ɩɫ>ɪɩɪɫɫ\
        Gɬɭ4ɭɮ6ɮɯ<\
        ɯIɰɵL'ɱɲ	ɲɴ\
        L'ɳɱɴɷɵɳɵ\
        ɶɶɹɷɵɸɺ	\
        ɹɸɹɺɺKɻɼ\
        ɼɽ8ɽɾ<ɾɿ9ɿ\
        ʁʀɻʀʁʁʂ\
        ʂʃ<ʃMʄʉP)ʅʆ\
        	ʆʈP)ʇʅʈʋʉ\
        ʇʉʊʊʍʋʉ\
        ʌʎ	ʍʌʍʎʎ\
        OʏʐR*ʐʑHʑʒ<ʒ\
        QʓʗT+ʔʗ QʕʗZ.ʖ\
        ʓʖʔʖʕʗS\
        ʘʞV,ʙʚʚʛ8ʛʜ\
        <ʜʝ9ʝʟʞʙʞ\
        ʟʟUʠʥX-ʡʢ@ʢ\
        ʤX-ʣʡʤʧʥʣ\
        ʥʦʦʲʧʥʨʩ\
        @ʩʮX-ʪʫ@ʫʭX-ʬʪ\
        ʭʰʮʬʮʯʯ\
        ʲʰʮʱʠʱʨ\
        ʲWʳʹ4ʴʶ8ʵʷ\
        \\/ʶʵʶʷʷʸʸ\
        ʺ9ʹʴʹʺʺYʻ\
        ʼ		ʼʿ4ʽʾ7ʾˀ\
        ¢Rʿʽʿˀˀ[ˁ\
        ˆ^0˂˃>˃˅^0˄˂\
        ˅ˈˆ˄ˆˇˇ\
        ]ˈˆˉˋ1ˊˉ\
        ˊˋˋˌˌˍ4ˍ_\
        ˎ˓<ˏː>ː˒<ˑ\
        ˏ˒˕˓ˑ˓˔\
        ˔a˕˓˖˘˗˖\
        ˘˛˙˗˙˚˚\
        ˫˛˙˜˞<˝˟?\
        ˞˝˞˟˟ˬˠ\
        ˢEˡˣ?ˢˡˢˣ\
        ˣˬˤˬd3˥ˬf4˦\
        ˬh5˧ˬj6˨ˬl7˩ˬn\
        8˪ˬp9˫˜˫ˠ˫ˤ\
        ˫˥˫˦˫˧˫\
        ˨˫˩˫˪ˬc\
        ˭ˮˮ˯?˯e˰˱(\
        ˱˲<˲˳?˳g˴˵\
        ˵˶<˶˷?˷i˸˹\
        4˹˻7˺˸˺˻\
        ˻˼˼˽3˽˾8˾˿\
        <˿̀9̀́> ́k̂̃\
        4̃̅7̄̂̄̅\
        ̅̆̆̇̇̊4̈̉\
        7̉̋¢R̊̈̊̋\
        ̋̌̌̍̍̎<̎\
        ̏> ̏m̐̒̑̓4̒\
        ̑̒̓̓̔̔̕?\
        ̕o̖̘̗̙4̘̗\
        ̘̙̙̚̛̚?̛\
        q̜̞̝̜̡̞\
        ̟̝̟̠̠̬̡̟\
        ̢̭̣̭t;̤̭v<̥\
        ̭4̦̭x=̧̭̨̭\
        8̩̭	̪̭
        ̫̭\
        ̢̬̬̣̬̤̬̥\
        ̬̦̧̬̨̬̬\
        ̩̬̪̬̫̭s\
        ̮̯̯̰4̰̲8̱̳\
        O̲̱̲̳̴̳\
        ̴̵9̵̶7̶̷¢R̷\
        u̸̹$̹̺)̺̻4̻\
        ̽8̼̾O̼̽̽̾\
        ̾̿̿̀9̀w́̓\
        ͂̈́4̓͂̓̈́\
        ͇̈́͆ͅ7͈͆4͇ͅ\
        ͇͈͈͉͉͍:͊\
        ͌z>͋͊͌͏͍͋\
        ͍͎͎͐͏͍͐͑\
        ;͑y͔͒͓͒͔͗\
        ͕͓͕͖͖͢͗\
        ͕ͣ͘͙ͣC͚ͣ\
        D͛ͣͣ͜Eͣ͝\
        |?ͣ͞8ͣ͟
        ͣ͠͡\
        ͣF͘͢͙͢͚͢\
        ͛͢͢͜͢͝͢\
        ͞͢͟͢͠͢͡\
        ͣ{ͤͧ~@ͥͧAͦͤ\
        ͦͥͧ}ͨͩ+ͩͯ\
        4ͪͬ8ͫͭOͬͫ\
        ͬͭͭͮͮͰ9ͯͪ\
        ͯͰͰͱͱ͵:Ͳ\
        ʹBͳͲʹͷ͵ͳ\
        ͵ͶͶ͸ͷ͵͸\
        ͹;͹ͺͻͻͼ+\
        ͼ΂4ͽͿ8;΀OͿ\
        ;Ϳ΀΀΁΁΃9\
        ΂ͽ΂΃΃΄΄\
        ΅> ΅ΆΈ·Ά\
        Έ΋Ή·ΉΊΊΗ\
        ΋ΉΌΘ΍Θ\
        CΎΘDΏΘΐΘ|?Α\
        ΘEΒΘ8ΓΘ	ΔΘ\
        FΕΘ
        ΖΘΗΌ\
        Η΍ΗΎΗΏΗΐ\
        ΗΑΗΒΗΓΗ\
        ΔΗΕΗΖΘ\
        ΙΚΚΛ8ΛΜ9Μ\
        Ν6ΝΞ<ΞΟΠ\
        ΠΡ8Ρ΢9΢Σ6Σ\
        Τ<ΤΥΦΦΧ8\
        ΧΨ<ΨΩ9ΩΪ\
        ήGΫήIάήJέ\
        ΪέΫέάή\
        ίΰΰα8αβ<β\
        γ9γεδίδε\
        ελζηHηθ@θ\
        κιζκνλι\
        λμμξνλξ\
        ο¦Tορ8πςOρπ\
        ρςςσστ9τ\
        υ6υφKφχϐ\
        4ψω<ωόR*ϊϋLϋ\
        ύ<όϊόύύώ\
        ώϏ=Ϗϑϐψϐ\
        ϑϑϒϓϓϔ\
        8ϔϕ<ϕϖ9ϖϘϗ\
        ϒϗϘϘϙϙϚ*\
        Ϛϛ6ϛϜ<Ϝϝ\
        Ϟ	
        Ϟϟ8ϟϠ<Ϡϡ9\
        ϡϢ6Ϣϣ<ϣϤ\
        Ϩ<ϥϨϦϨLϧϤ\
        ϧϥϧϦϨϩ\
        Ϫ Ϫϫ:ϫϱMϬϭ\
        "ϭϯKϮϰ>ϯϮϯ\
        ϰϰϲϱϬϱϲ\
        ϲϳϳϴ;ϴϵ\
        ϺN϶Ϸ>ϷϹNϸ϶\
        ϹϼϺϸϺϻϻ\
        ϾϼϺϽϿ>ϾϽ\
        ϾϿϿЀЂЁ\
        ЀЂЅЃЁЃЄ\
        ЄЋЅЃІЇЇ\
        Ј8ЈЉ<ЉЊ9ЊЌ\
        ЋІЋЌЌЍЍЎ\
        KЎЏДPАБ>\
        БГPВАГЖД\
        ВДЕЕЖД\
        ЗЙ1ИЗИЙЙ\
        ККМ4ЛНNМЛ\
        МННРОП7П\
        С¢RРОРССХ\
        ТУ4УХ$ФИ\
        ФТХЦЧ	Ч¡\
        ШЩRЩЪ8ЪЫ¢R\
        ЫЬ9ЬчЭв¦TЮЯ\
        AЯа¤SабBбг\
        вЮвггжде\
        @ез4жджзз\
        чий8йк¤Sкл\
        9лмIмн¢Rнчо\
        ч QпсJрт	ср\
        сттууч¢R\
        фхEхч¢RцШцЭ\
        цицоцпц\
        фчѐшщ
        щъ	\
        ъя¢Rыь	ьэ	э\
        я¢R
        юшюыяђ\
        ѐюѐёё£ђ\
        ѐѓј¢Rєѕ>ѕї\
        ¢Rієїњјі\
        јљљ¥њјћѠ\
        4ќѝ@ѝџ4ўќ\
        џѢѠўѠѡѡ§\
        ѢѠ«³ÃËÒÕ\
        ÙÞáæêîóùāćďė\
        ěĦıĴĺŀņŌŠŤũŬ\
        ųſƈƌƑƕƙƟƢƦƭƶ\
        ƺƽǀǇǌǔǗǤǴȉȝȯ\
        ȱȸȼɇɋɚɝɦɪɵɹʀ\
        ʉʍʖʞʥʮʱʶʹʿˆˊ\
        ˓˙˞ˢ˫˺̘̟̬̄̊̒\
        ̲͇͍͕̽̓ͦͬͯ͢͵Ϳ\
        ΂ΉΗέδλρόϐϗϧϯ\
        ϱϺϾЃЋДИМРФвж\
        сцюѐјѠ""";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
