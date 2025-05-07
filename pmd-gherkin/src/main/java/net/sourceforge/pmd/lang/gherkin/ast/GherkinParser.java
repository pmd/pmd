/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// CHECKSTYLE:OFF
// Generated from net/sourceforge/pmd/lang/gherkin/ast/Gherkin.g4 by ANTLR 4.9.3
package net.sourceforge.pmd.lang.gherkin.ast;

// CPD-OFF

import java.util.List;

import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
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
 public class GherkinParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BOMUTF8=1, BOMUTF16=2, WHITESPACE=3, COMMENT=4, STARTCOMMENT=5, DOCSTRING1=6, 
		DOCSTRING2=7, BACKGROUND=8, EXAMPLES=9, FEATURE=10, RULEX=11, SCENARIO=12, 
		SCENARIOOUTLINE=13, AND=14, ANYSTEP=15, BUT=16, DATATABLE=17, GIVEN=18, 
		THEN=19, WHEN=20, TAG=21, PARAMETER=22, NL=23, TOKEN=24;
	public static final int
		RULE_main = 0, RULE_feature = 1, RULE_instructionLine = 2, RULE_instruction = 3, 
		RULE_stepInstruction = 4, RULE_background = 5, RULE_rulex = 6, RULE_scenario = 7, 
		RULE_scenarioOutline = 8, RULE_step = 9, RULE_stepItem = 10, RULE_tagline = 11, 
		RULE_and = 12, RULE_anystep = 13, RULE_but = 14, RULE_datatable = 15, 
		RULE_given = 16, RULE_then = 17, RULE_when = 18, RULE_examples = 19, RULE_instructionDescription = 20, 
		RULE_stepDescription = 21, RULE_description = 22, RULE_text = 23;
	private static String[] makeRuleNames() {
		return new String[] {
			"main", "feature", "instructionLine", "instruction", "stepInstruction", 
			"background", "rulex", "scenario", "scenarioOutline", "step", "stepItem", 
			"tagline", "and", "anystep", "but", "datatable", "given", "then", "when", 
			"examples", "instructionDescription", "stepDescription", "description", 
			"text"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'\u00EF\u00BB\u00BF'", "'\uFEFF'", null, null, null, null, null, 
			"'Background:'", null, "'Feature:'", "'Rule:'", null, null, "'And'", 
			"'*'", "'But'", null, "'Given'", "'Then'", "'When'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BOMUTF8", "BOMUTF16", "WHITESPACE", "COMMENT", "STARTCOMMENT", 
			"DOCSTRING1", "DOCSTRING2", "BACKGROUND", "EXAMPLES", "FEATURE", "RULEX", 
			"SCENARIO", "SCENARIOOUTLINE", "AND", "ANYSTEP", "BUT", "DATATABLE", 
			"GIVEN", "THEN", "WHEN", "TAG", "PARAMETER", "NL", "TOKEN"
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
	public String getGrammarFileName() { return "Gherkin.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public GherkinParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public static class MainContext extends ParserRuleContext {
		public FeatureContext feature() {
			return getRuleContext(FeatureContext.class,0);
		}
		public TerminalNode EOF() { return getToken(GherkinParser.EOF, 0); }
		public TerminalNode STARTCOMMENT() { return getToken(GherkinParser.STARTCOMMENT, 0); }
		public List<DescriptionContext> description() {
			return getRuleContexts(DescriptionContext.class);
		}
		public DescriptionContext description(int i) {
			return getRuleContext(DescriptionContext.class,i);
		}
		public List<InstructionLineContext> instructionLine() {
			return getRuleContexts(InstructionLineContext.class);
		}
		public InstructionLineContext instructionLine(int i) {
			return getRuleContext(InstructionLineContext.class,i);
		}
		public List<TerminalNode> NL() { return getTokens(GherkinParser.NL); }
		public TerminalNode NL(int i) {
			return getToken(GherkinParser.NL, i);
		}
		public MainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_main; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterMain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitMain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitMain(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MainContext main() throws RecognitionException {
		MainContext _localctx = new MainContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_main);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(48);
				match(STARTCOMMENT);
				}
				break;
			}
			setState(51);
			feature();
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STARTCOMMENT) | (1L << SCENARIO) | (1L << SCENARIOOUTLINE) | (1L << AND) | (1L << ANYSTEP) | (1L << BUT) | (1L << DATATABLE) | (1L << GIVEN) | (1L << THEN) | (1L << WHEN) | (1L << TAG) | (1L << PARAMETER) | (1L << TOKEN))) != 0)) {
				{
				{
				setState(52);
				description();
				}
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(61);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(58);
					instructionLine();
					}
					} 
				}
				setState(63);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NL) {
				{
				{
				setState(64);
				match(NL);
				}
				}
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(70);
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
 public static class FeatureContext extends ParserRuleContext {
		public List<TaglineContext> tagline() {
			return getRuleContexts(TaglineContext.class);
		}
		public TaglineContext tagline(int i) {
			return getRuleContext(TaglineContext.class,i);
		}
		public List<TerminalNode> NL() { return getTokens(GherkinParser.NL); }
		public TerminalNode NL(int i) {
			return getToken(GherkinParser.NL, i);
		}
		public TerminalNode FEATURE() { return getToken(GherkinParser.FEATURE, 0); }
		public FeatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_feature; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterFeature(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitFeature(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitFeature(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FeatureContext feature() throws RecognitionException {
		FeatureContext _localctx = new FeatureContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_feature);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(75);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==NL) {
						{
						{
						setState(72);
						match(NL);
						}
						}
						setState(77);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(78);
					tagline();
					}
					} 
				}
				setState(83);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			setState(87);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(84);
					match(NL);
					}
					} 
				}
				setState(89);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FEATURE) {
				{
				setState(90);
				match(FEATURE);
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
 public static class InstructionLineContext extends ParserRuleContext {
		public InstructionContext instruction() {
			return getRuleContext(InstructionContext.class,0);
		}
		public DatatableContext datatable() {
			return getRuleContext(DatatableContext.class,0);
		}
		public List<TerminalNode> NL() { return getTokens(GherkinParser.NL); }
		public TerminalNode NL(int i) {
			return getToken(GherkinParser.NL, i);
		}
		public InstructionLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instructionLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterInstructionLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitInstructionLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitInstructionLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstructionLineContext instructionLine() throws RecognitionException {
		InstructionLineContext _localctx = new InstructionLineContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_instructionLine);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(93);
				match(NL);
				}
				}
				setState(96); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NL );
			setState(100);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BACKGROUND:
			case RULEX:
			case SCENARIO:
			case SCENARIOOUTLINE:
			case AND:
			case ANYSTEP:
			case BUT:
			case GIVEN:
			case THEN:
			case WHEN:
			case TAG:
			case PARAMETER:
			case TOKEN:
				{
				setState(98);
				instruction();
				}
				break;
			case DATATABLE:
				{
				setState(99);
				datatable();
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
 public static class InstructionContext extends ParserRuleContext {
		public RulexContext rulex() {
			return getRuleContext(RulexContext.class,0);
		}
		public List<DescriptionContext> description() {
			return getRuleContexts(DescriptionContext.class);
		}
		public DescriptionContext description(int i) {
			return getRuleContext(DescriptionContext.class,i);
		}
		public StepInstructionContext stepInstruction() {
			return getRuleContext(StepInstructionContext.class,0);
		}
		public List<StepDescriptionContext> stepDescription() {
			return getRuleContexts(StepDescriptionContext.class);
		}
		public StepDescriptionContext stepDescription(int i) {
			return getRuleContext(StepDescriptionContext.class,i);
		}
		public List<StepContext> step() {
			return getRuleContexts(StepContext.class);
		}
		public StepContext step(int i) {
			return getRuleContext(StepContext.class,i);
		}
		public List<TerminalNode> NL() { return getTokens(GherkinParser.NL); }
		public TerminalNode NL(int i) {
			return getToken(GherkinParser.NL, i);
		}
		public TaglineContext tagline() {
			return getRuleContext(TaglineContext.class,0);
		}
		public InstructionDescriptionContext instructionDescription() {
			return getRuleContext(InstructionDescriptionContext.class,0);
		}
		public InstructionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instruction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitInstruction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstructionContext instruction() throws RecognitionException {
		InstructionContext _localctx = new InstructionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_instruction);
		int _la;
		try {
			int _alt;
			setState(152);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(102);
				rulex();
				setState(106);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STARTCOMMENT) | (1L << SCENARIO) | (1L << SCENARIOOUTLINE) | (1L << AND) | (1L << ANYSTEP) | (1L << BUT) | (1L << DATATABLE) | (1L << GIVEN) | (1L << THEN) | (1L << WHEN) | (1L << TAG) | (1L << PARAMETER) | (1L << TOKEN))) != 0)) {
					{
					{
					setState(103);
					description();
					}
					}
					setState(108);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(109);
				stepInstruction();
				setState(113);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STARTCOMMENT) | (1L << SCENARIO) | (1L << SCENARIOOUTLINE) | (1L << AND) | (1L << ANYSTEP) | (1L << BUT) | (1L << DATATABLE) | (1L << GIVEN) | (1L << THEN) | (1L << WHEN) | (1L << TAG) | (1L << PARAMETER) | (1L << TOKEN))) != 0)) {
					{
					{
					setState(110);
					description();
					}
					}
					setState(115);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(130);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(117); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(116);
							match(NL);
							}
							}
							setState(119); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==NL );
						setState(121);
						stepDescription();
						setState(125);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STARTCOMMENT) | (1L << SCENARIO) | (1L << SCENARIOOUTLINE) | (1L << AND) | (1L << ANYSTEP) | (1L << BUT) | (1L << DATATABLE) | (1L << GIVEN) | (1L << THEN) | (1L << WHEN) | (1L << TAG) | (1L << PARAMETER) | (1L << TOKEN))) != 0)) {
							{
							{
							setState(122);
							description();
							}
							}
							setState(127);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						}
						} 
					}
					setState(132);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
				}
				setState(141);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(134); 
						_errHandler.sync(this);
						_alt = 1;
						do {
							switch (_alt) {
							case 1:
								{
								{
								setState(133);
								match(NL);
								}
								}
								break;
							default:
								throw new NoViableAltException(this);
							}
							setState(136); 
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
						} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
						setState(138);
						step();
						}
						} 
					}
					setState(143);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(144);
				tagline();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(145);
				instructionDescription();
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STARTCOMMENT) | (1L << SCENARIO) | (1L << SCENARIOOUTLINE) | (1L << AND) | (1L << ANYSTEP) | (1L << BUT) | (1L << DATATABLE) | (1L << GIVEN) | (1L << THEN) | (1L << WHEN) | (1L << TAG) | (1L << PARAMETER) | (1L << TOKEN))) != 0)) {
					{
					{
					setState(146);
					description();
					}
					}
					setState(151);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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
 public static class StepInstructionContext extends ParserRuleContext {
		public BackgroundContext background() {
			return getRuleContext(BackgroundContext.class,0);
		}
		public ScenarioContext scenario() {
			return getRuleContext(ScenarioContext.class,0);
		}
		public ScenarioOutlineContext scenarioOutline() {
			return getRuleContext(ScenarioOutlineContext.class,0);
		}
		public StepInstructionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stepInstruction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterStepInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitStepInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitStepInstruction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StepInstructionContext stepInstruction() throws RecognitionException {
		StepInstructionContext _localctx = new StepInstructionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_stepInstruction);
		try {
			setState(157);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BACKGROUND:
				enterOuterAlt(_localctx, 1);
				{
				setState(154);
				background();
				}
				break;
			case SCENARIO:
				enterOuterAlt(_localctx, 2);
				{
				setState(155);
				scenario();
				}
				break;
			case SCENARIOOUTLINE:
				enterOuterAlt(_localctx, 3);
				{
				setState(156);
				scenarioOutline();
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
 public static class BackgroundContext extends ParserRuleContext {
		public TerminalNode BACKGROUND() { return getToken(GherkinParser.BACKGROUND, 0); }
		public BackgroundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_background; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterBackground(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitBackground(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitBackground(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BackgroundContext background() throws RecognitionException {
		BackgroundContext _localctx = new BackgroundContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_background);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(BACKGROUND);
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
 public static class RulexContext extends ParserRuleContext {
		public TerminalNode RULEX() { return getToken(GherkinParser.RULEX, 0); }
		public RulexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rulex; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterRulex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitRulex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitRulex(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RulexContext rulex() throws RecognitionException {
		RulexContext _localctx = new RulexContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_rulex);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(161);
			match(RULEX);
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
 public static class ScenarioContext extends ParserRuleContext {
		public TerminalNode SCENARIO() { return getToken(GherkinParser.SCENARIO, 0); }
		public ScenarioContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scenario; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterScenario(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitScenario(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitScenario(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScenarioContext scenario() throws RecognitionException {
		ScenarioContext _localctx = new ScenarioContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_scenario);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			match(SCENARIO);
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
 public static class ScenarioOutlineContext extends ParserRuleContext {
		public TerminalNode SCENARIOOUTLINE() { return getToken(GherkinParser.SCENARIOOUTLINE, 0); }
		public ScenarioOutlineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scenarioOutline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterScenarioOutline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitScenarioOutline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitScenarioOutline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScenarioOutlineContext scenarioOutline() throws RecognitionException {
		ScenarioOutlineContext _localctx = new ScenarioOutlineContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_scenarioOutline);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165);
			match(SCENARIOOUTLINE);
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
 public static class StepContext extends ParserRuleContext {
		public StepItemContext stepItem() {
			return getRuleContext(StepItemContext.class,0);
		}
		public List<DescriptionContext> description() {
			return getRuleContexts(DescriptionContext.class);
		}
		public DescriptionContext description(int i) {
			return getRuleContext(DescriptionContext.class,i);
		}
		public StepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_step; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterStep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitStep(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitStep(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StepContext step() throws RecognitionException {
		StepContext _localctx = new StepContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_step);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			stepItem();
			setState(171);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STARTCOMMENT) | (1L << SCENARIO) | (1L << SCENARIOOUTLINE) | (1L << AND) | (1L << ANYSTEP) | (1L << BUT) | (1L << DATATABLE) | (1L << GIVEN) | (1L << THEN) | (1L << WHEN) | (1L << TAG) | (1L << PARAMETER) | (1L << TOKEN))) != 0)) {
				{
				{
				setState(168);
				description();
				}
				}
				setState(173);
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
 public static class StepItemContext extends ParserRuleContext {
		public AndContext and() {
			return getRuleContext(AndContext.class,0);
		}
		public AnystepContext anystep() {
			return getRuleContext(AnystepContext.class,0);
		}
		public ButContext but() {
			return getRuleContext(ButContext.class,0);
		}
		public DatatableContext datatable() {
			return getRuleContext(DatatableContext.class,0);
		}
		public GivenContext given() {
			return getRuleContext(GivenContext.class,0);
		}
		public ThenContext then() {
			return getRuleContext(ThenContext.class,0);
		}
		public WhenContext when() {
			return getRuleContext(WhenContext.class,0);
		}
		public ExamplesContext examples() {
			return getRuleContext(ExamplesContext.class,0);
		}
		public List<TaglineContext> tagline() {
			return getRuleContexts(TaglineContext.class);
		}
		public TaglineContext tagline(int i) {
			return getRuleContext(TaglineContext.class,i);
		}
		public List<TerminalNode> NL() { return getTokens(GherkinParser.NL); }
		public TerminalNode NL(int i) {
			return getToken(GherkinParser.NL, i);
		}
		public StepItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stepItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterStepItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitStepItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitStepItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StepItemContext stepItem() throws RecognitionException {
		StepItemContext _localctx = new StepItemContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_stepItem);
		int _la;
		try {
			int _alt;
			setState(200);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AND:
				enterOuterAlt(_localctx, 1);
				{
				setState(174);
				and();
				}
				break;
			case ANYSTEP:
				enterOuterAlt(_localctx, 2);
				{
				setState(175);
				anystep();
				}
				break;
			case BUT:
				enterOuterAlt(_localctx, 3);
				{
				setState(176);
				but();
				}
				break;
			case DATATABLE:
				enterOuterAlt(_localctx, 4);
				{
				setState(177);
				datatable();
				}
				break;
			case GIVEN:
				enterOuterAlt(_localctx, 5);
				{
				setState(178);
				given();
				}
				break;
			case THEN:
				enterOuterAlt(_localctx, 6);
				{
				setState(179);
				then();
				}
				break;
			case WHEN:
				enterOuterAlt(_localctx, 7);
				{
				setState(180);
				when();
				}
				break;
			case EXAMPLES:
			case TAG:
			case NL:
				enterOuterAlt(_localctx, 8);
				{
				setState(190);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(184);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==NL) {
							{
							{
							setState(181);
							match(NL);
							}
							}
							setState(186);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(187);
						tagline();
						}
						} 
					}
					setState(192);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
				}
				setState(196);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NL) {
					{
					{
					setState(193);
					match(NL);
					}
					}
					setState(198);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(199);
				examples();
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
 public static class TaglineContext extends ParserRuleContext {
		public List<TerminalNode> TAG() { return getTokens(GherkinParser.TAG); }
		public TerminalNode TAG(int i) {
			return getToken(GherkinParser.TAG, i);
		}
		public TaglineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tagline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterTagline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitTagline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitTagline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TaglineContext tagline() throws RecognitionException {
		TaglineContext _localctx = new TaglineContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_tagline);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(203); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(202);
					match(TAG);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(205); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
 public static class AndContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(GherkinParser.AND, 0); }
		public AndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_and; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitAnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AndContext and() throws RecognitionException {
		AndContext _localctx = new AndContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_and);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(AND);
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
 public static class AnystepContext extends ParserRuleContext {
		public TerminalNode ANYSTEP() { return getToken(GherkinParser.ANYSTEP, 0); }
		public AnystepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_anystep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterAnystep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitAnystep(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitAnystep(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnystepContext anystep() throws RecognitionException {
		AnystepContext _localctx = new AnystepContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_anystep);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			match(ANYSTEP);
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
 public static class ButContext extends ParserRuleContext {
		public TerminalNode BUT() { return getToken(GherkinParser.BUT, 0); }
		public ButContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_but; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterBut(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitBut(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitBut(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ButContext but() throws RecognitionException {
		ButContext _localctx = new ButContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_but);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(BUT);
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
 public static class DatatableContext extends ParserRuleContext {
		public List<TerminalNode> DATATABLE() { return getTokens(GherkinParser.DATATABLE); }
		public TerminalNode DATATABLE(int i) {
			return getToken(GherkinParser.DATATABLE, i);
		}
		public DatatableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datatable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterDatatable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitDatatable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitDatatable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DatatableContext datatable() throws RecognitionException {
		DatatableContext _localctx = new DatatableContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_datatable);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(214); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(213);
					match(DATATABLE);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(216); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
 public static class GivenContext extends ParserRuleContext {
		public TerminalNode GIVEN() { return getToken(GherkinParser.GIVEN, 0); }
		public GivenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_given; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterGiven(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitGiven(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitGiven(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GivenContext given() throws RecognitionException {
		GivenContext _localctx = new GivenContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_given);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
			match(GIVEN);
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
 public static class ThenContext extends ParserRuleContext {
		public TerminalNode THEN() { return getToken(GherkinParser.THEN, 0); }
		public ThenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_then; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterThen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitThen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitThen(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ThenContext then() throws RecognitionException {
		ThenContext _localctx = new ThenContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_then);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			match(THEN);
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
 public static class WhenContext extends ParserRuleContext {
		public TerminalNode WHEN() { return getToken(GherkinParser.WHEN, 0); }
		public WhenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_when; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterWhen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitWhen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitWhen(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhenContext when() throws RecognitionException {
		WhenContext _localctx = new WhenContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_when);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			match(WHEN);
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
 public static class ExamplesContext extends ParserRuleContext {
		public TerminalNode EXAMPLES() { return getToken(GherkinParser.EXAMPLES, 0); }
		public ExamplesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_examples; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterExamples(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitExamples(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitExamples(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExamplesContext examples() throws RecognitionException {
		ExamplesContext _localctx = new ExamplesContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_examples);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			match(EXAMPLES);
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
 public static class InstructionDescriptionContext extends ParserRuleContext {
		public TextContext text() {
			return getRuleContext(TextContext.class,0);
		}
		public TerminalNode PARAMETER() { return getToken(GherkinParser.PARAMETER, 0); }
		public TerminalNode AND() { return getToken(GherkinParser.AND, 0); }
		public TerminalNode ANYSTEP() { return getToken(GherkinParser.ANYSTEP, 0); }
		public TerminalNode BUT() { return getToken(GherkinParser.BUT, 0); }
		public TerminalNode GIVEN() { return getToken(GherkinParser.GIVEN, 0); }
		public TerminalNode THEN() { return getToken(GherkinParser.THEN, 0); }
		public TerminalNode WHEN() { return getToken(GherkinParser.WHEN, 0); }
		public TerminalNode SCENARIO() { return getToken(GherkinParser.SCENARIO, 0); }
		public InstructionDescriptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instructionDescription; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterInstructionDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitInstructionDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitInstructionDescription(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstructionDescriptionContext instructionDescription() throws RecognitionException {
		InstructionDescriptionContext _localctx = new InstructionDescriptionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_instructionDescription);
		try {
			setState(235);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TOKEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(226);
				text();
				}
				break;
			case PARAMETER:
				enterOuterAlt(_localctx, 2);
				{
				setState(227);
				match(PARAMETER);
				}
				break;
			case AND:
				enterOuterAlt(_localctx, 3);
				{
				setState(228);
				match(AND);
				}
				break;
			case ANYSTEP:
				enterOuterAlt(_localctx, 4);
				{
				setState(229);
				match(ANYSTEP);
				}
				break;
			case BUT:
				enterOuterAlt(_localctx, 5);
				{
				setState(230);
				match(BUT);
				}
				break;
			case GIVEN:
				enterOuterAlt(_localctx, 6);
				{
				setState(231);
				match(GIVEN);
				}
				break;
			case THEN:
				enterOuterAlt(_localctx, 7);
				{
				setState(232);
				match(THEN);
				}
				break;
			case WHEN:
				enterOuterAlt(_localctx, 8);
				{
				setState(233);
				match(WHEN);
				}
				break;
			case SCENARIO:
				enterOuterAlt(_localctx, 9);
				{
				setState(234);
				match(SCENARIO);
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
 public static class StepDescriptionContext extends ParserRuleContext {
		public TextContext text() {
			return getRuleContext(TextContext.class,0);
		}
		public TerminalNode PARAMETER() { return getToken(GherkinParser.PARAMETER, 0); }
		public StepDescriptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stepDescription; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterStepDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitStepDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitStepDescription(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StepDescriptionContext stepDescription() throws RecognitionException {
		StepDescriptionContext _localctx = new StepDescriptionContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_stepDescription);
		try {
			setState(239);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TOKEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(237);
				text();
				}
				break;
			case PARAMETER:
				enterOuterAlt(_localctx, 2);
				{
				setState(238);
				match(PARAMETER);
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
 public static class DescriptionContext extends ParserRuleContext {
		public TextContext text() {
			return getRuleContext(TextContext.class,0);
		}
		public TerminalNode PARAMETER() { return getToken(GherkinParser.PARAMETER, 0); }
		public TerminalNode TAG() { return getToken(GherkinParser.TAG, 0); }
		public TerminalNode AND() { return getToken(GherkinParser.AND, 0); }
		public TerminalNode ANYSTEP() { return getToken(GherkinParser.ANYSTEP, 0); }
		public TerminalNode BUT() { return getToken(GherkinParser.BUT, 0); }
		public TerminalNode DATATABLE() { return getToken(GherkinParser.DATATABLE, 0); }
		public TerminalNode GIVEN() { return getToken(GherkinParser.GIVEN, 0); }
		public TerminalNode THEN() { return getToken(GherkinParser.THEN, 0); }
		public TerminalNode WHEN() { return getToken(GherkinParser.WHEN, 0); }
		public TerminalNode SCENARIO() { return getToken(GherkinParser.SCENARIO, 0); }
		public TerminalNode SCENARIOOUTLINE() { return getToken(GherkinParser.SCENARIOOUTLINE, 0); }
		public TerminalNode STARTCOMMENT() { return getToken(GherkinParser.STARTCOMMENT, 0); }
		public DescriptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_description; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitDescription(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescriptionContext description() throws RecognitionException {
		DescriptionContext _localctx = new DescriptionContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_description);
		try {
			setState(254);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TOKEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(241);
				text();
				}
				break;
			case PARAMETER:
				enterOuterAlt(_localctx, 2);
				{
				setState(242);
				match(PARAMETER);
				}
				break;
			case TAG:
				enterOuterAlt(_localctx, 3);
				{
				setState(243);
				match(TAG);
				}
				break;
			case AND:
				enterOuterAlt(_localctx, 4);
				{
				setState(244);
				match(AND);
				}
				break;
			case ANYSTEP:
				enterOuterAlt(_localctx, 5);
				{
				setState(245);
				match(ANYSTEP);
				}
				break;
			case BUT:
				enterOuterAlt(_localctx, 6);
				{
				setState(246);
				match(BUT);
				}
				break;
			case DATATABLE:
				enterOuterAlt(_localctx, 7);
				{
				setState(247);
				match(DATATABLE);
				}
				break;
			case GIVEN:
				enterOuterAlt(_localctx, 8);
				{
				setState(248);
				match(GIVEN);
				}
				break;
			case THEN:
				enterOuterAlt(_localctx, 9);
				{
				setState(249);
				match(THEN);
				}
				break;
			case WHEN:
				enterOuterAlt(_localctx, 10);
				{
				setState(250);
				match(WHEN);
				}
				break;
			case SCENARIO:
				enterOuterAlt(_localctx, 11);
				{
				setState(251);
				match(SCENARIO);
				}
				break;
			case SCENARIOOUTLINE:
				enterOuterAlt(_localctx, 12);
				{
				setState(252);
				match(SCENARIOOUTLINE);
				}
				break;
			case STARTCOMMENT:
				enterOuterAlt(_localctx, 13);
				{
				setState(253);
				match(STARTCOMMENT);
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
 public static class TextContext extends ParserRuleContext {
		public List<TerminalNode> TOKEN() { return getTokens(GherkinParser.TOKEN); }
		public TerminalNode TOKEN(int i) {
			return getToken(GherkinParser.TOKEN, i);
		}
		public TextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_text; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.enterText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener gherkinListener ) gherkinListener.exitText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor<? extends T> gherkinVisitor ) return gherkinVisitor.visitText(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TextContext text() throws RecognitionException {
		TextContext _localctx = new TextContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_text);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(257); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(256);
					match(TOKEN);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(259); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static final String _serializedATN =
		"""
        悋Ꜫ脳맭䅼㯧瞆奤Ĉ	\
        									
        	
        \
        								\
        							\
        4
        8
        ;>
        \
        AD
        GL
        O\
        R
        UX
        [^
        \
        a
        bg
        k
        n\
        r
        ux
        y\
        ~
        
        
        
        \
        
        
         
        		
        
        ¬
        \
        ¯¹
        \
        ¼¿
        ÂÅ
        \
        ÈË
        Î
        Ï\
        Ù
        Ú\
        \
        î
        ò
        \
        ā
        \
        Ą
        ą
        \
         "$&(*,.0ĩ3S`\
        
        ¡£\
        ¥§©Ê\
        ÍÑÓÕ Ø\
        "Ü$Þ&à(â\
        *í,ñ.Ā0ă24\
        3234455968.\
        768;979::?;9<\
        >=<>A?=?@@EA?\
        BDCBDGECEFFHGE\
        HIIJLKJLOMKMN\
        NPOMPRQMRUSQST\
        TYUSVXWVX[YWYZ\
        Z][Y\\^]\\]^^\
        _a`_abb`bccfdg\
        eg fdfeghlik.ji\
        knljlmmnlos
        \
        pr.qprusqsttus\
        vxwvxyywyzz{\
        {,|~.}|~}\
        w\
        \
        \
        \
        \
        \
        *.\
        \
        ho\
        	 \
         	 
        \
         ¡¢
        ¢\
        £¤¤¥¦¦\
        §¨¨©­\
        ª¬.«ª¬¯­«\
        ­®®¯­°\
        Ë±Ë²Ë³Ë\
         ´Ë"µË$¶Ë&\
        ·¹¸·¹¼º¸\
        º»»½¼º½\
        ¿¾º¿ÂÀ¾\
        ÀÁÁÆÂÀÃ\
        ÅÄÃÅÈÆÄ\
        ÆÇÇÉÈÆÉ\
        Ë(Ê°Ê±Ê²\
        Ê³Ê´ÊµÊ\
        ¶ÊÀËÌÎ\
        ÍÌÎÏÏÍÏÐ\
        ÐÑÒÒÓ\
        ÔÔÕÖÖ\
        ×ÙØ×ÙÚÚØ\
        ÚÛÛ!ÜÝÝ\
        #Þßß%àáá\
        'âãã)äî0å\
        îæîçîèî\
        éîêîëî\
        ìîíäíåí\
        æíçíèíé\
        íêíëíìî\
        +ïò0ðòñï\
        ñðò-óā0ôā\
        õāöā÷ā\
        øāùāúāû\
        āüāýāþā\
        ÿāĀóĀô\
        ĀõĀöĀ÷Āø\
        ĀùĀúĀûĀ\
        üĀýĀþĀÿ\
        ā/ĂĄăĂĄą\
        ąăąĆĆ1!3\
        9?EMSY]bflsy­ºÀ\
        ÆÊÏÚíñĀą""";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
