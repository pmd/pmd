/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterMain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitMain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitMain(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterFeature(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitFeature(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitFeature(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterInstructionLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitInstructionLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitInstructionLine(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitInstruction(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterStepInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitStepInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitStepInstruction(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterBackground(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitBackground(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitBackground(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterRulex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitRulex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitRulex(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterScenario(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitScenario(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitScenario(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterScenarioOutline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitScenarioOutline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitScenarioOutline(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterStep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitStep(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitStep(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterStepItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitStepItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitStepItem(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterTagline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitTagline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitTagline(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitAnd(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterAnystep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitAnystep(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitAnystep(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterBut(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitBut(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitBut(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterDatatable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitDatatable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitDatatable(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterGiven(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitGiven(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitGiven(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterThen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitThen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitThen(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterWhen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitWhen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitWhen(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterExamples(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitExamples(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitExamples(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterInstructionDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitInstructionDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitInstructionDescription(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterStepDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitStepDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitStepDescription(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitDescription(this);
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
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).enterText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GherkinListener ) ((GherkinListener)listener).exitText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GherkinVisitor ) return ((GherkinVisitor<? extends T>)visitor).visitText(this);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\32\u0108\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\5\2\64\n\2\3\2\3\2\7\28\n\2\f\2\16\2;\13\2\3\2\7\2>\n\2\f\2\16\2"+
		"A\13\2\3\2\7\2D\n\2\f\2\16\2G\13\2\3\2\3\2\3\3\7\3L\n\3\f\3\16\3O\13\3"+
		"\3\3\7\3R\n\3\f\3\16\3U\13\3\3\3\7\3X\n\3\f\3\16\3[\13\3\3\3\5\3^\n\3"+
		"\3\4\6\4a\n\4\r\4\16\4b\3\4\3\4\5\4g\n\4\3\5\3\5\7\5k\n\5\f\5\16\5n\13"+
		"\5\3\5\3\5\7\5r\n\5\f\5\16\5u\13\5\3\5\6\5x\n\5\r\5\16\5y\3\5\3\5\7\5"+
		"~\n\5\f\5\16\5\u0081\13\5\7\5\u0083\n\5\f\5\16\5\u0086\13\5\3\5\6\5\u0089"+
		"\n\5\r\5\16\5\u008a\3\5\7\5\u008e\n\5\f\5\16\5\u0091\13\5\3\5\3\5\3\5"+
		"\7\5\u0096\n\5\f\5\16\5\u0099\13\5\5\5\u009b\n\5\3\6\3\6\3\6\5\6\u00a0"+
		"\n\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\7\13\u00ac\n\13\f\13\16"+
		"\13\u00af\13\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\7\f\u00b9\n\f\f\f\16\f"+
		"\u00bc\13\f\3\f\7\f\u00bf\n\f\f\f\16\f\u00c2\13\f\3\f\7\f\u00c5\n\f\f"+
		"\f\16\f\u00c8\13\f\3\f\5\f\u00cb\n\f\3\r\6\r\u00ce\n\r\r\r\16\r\u00cf"+
		"\3\16\3\16\3\17\3\17\3\20\3\20\3\21\6\21\u00d9\n\21\r\21\16\21\u00da\3"+
		"\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3"+
		"\26\3\26\3\26\5\26\u00ee\n\26\3\27\3\27\5\27\u00f2\n\27\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u0101\n\30\3\31"+
		"\6\31\u0104\n\31\r\31\16\31\u0105\3\31\2\2\32\2\4\6\b\n\f\16\20\22\24"+
		"\26\30\32\34\36 \"$&(*,.\60\2\2\2\u0129\2\63\3\2\2\2\4S\3\2\2\2\6`\3\2"+
		"\2\2\b\u009a\3\2\2\2\n\u009f\3\2\2\2\f\u00a1\3\2\2\2\16\u00a3\3\2\2\2"+
		"\20\u00a5\3\2\2\2\22\u00a7\3\2\2\2\24\u00a9\3\2\2\2\26\u00ca\3\2\2\2\30"+
		"\u00cd\3\2\2\2\32\u00d1\3\2\2\2\34\u00d3\3\2\2\2\36\u00d5\3\2\2\2 \u00d8"+
		"\3\2\2\2\"\u00dc\3\2\2\2$\u00de\3\2\2\2&\u00e0\3\2\2\2(\u00e2\3\2\2\2"+
		"*\u00ed\3\2\2\2,\u00f1\3\2\2\2.\u0100\3\2\2\2\60\u0103\3\2\2\2\62\64\7"+
		"\7\2\2\63\62\3\2\2\2\63\64\3\2\2\2\64\65\3\2\2\2\659\5\4\3\2\668\5.\30"+
		"\2\67\66\3\2\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:?\3\2\2\2;9\3\2\2\2<"+
		">\5\6\4\2=<\3\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@E\3\2\2\2A?\3\2\2\2"+
		"BD\7\31\2\2CB\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3\2\2\2FH\3\2\2\2GE\3\2\2"+
		"\2HI\7\2\2\3I\3\3\2\2\2JL\7\31\2\2KJ\3\2\2\2LO\3\2\2\2MK\3\2\2\2MN\3\2"+
		"\2\2NP\3\2\2\2OM\3\2\2\2PR\5\30\r\2QM\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3"+
		"\2\2\2TY\3\2\2\2US\3\2\2\2VX\7\31\2\2WV\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ"+
		"\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2\\^\7\f\2\2]\\\3\2\2\2]^\3\2\2\2^\5\3\2\2"+
		"\2_a\7\31\2\2`_\3\2\2\2ab\3\2\2\2b`\3\2\2\2bc\3\2\2\2cf\3\2\2\2dg\5\b"+
		"\5\2eg\5 \21\2fd\3\2\2\2fe\3\2\2\2g\7\3\2\2\2hl\5\16\b\2ik\5.\30\2ji\3"+
		"\2\2\2kn\3\2\2\2lj\3\2\2\2lm\3\2\2\2m\u009b\3\2\2\2nl\3\2\2\2os\5\n\6"+
		"\2pr\5.\30\2qp\3\2\2\2ru\3\2\2\2sq\3\2\2\2st\3\2\2\2t\u0084\3\2\2\2us"+
		"\3\2\2\2vx\7\31\2\2wv\3\2\2\2xy\3\2\2\2yw\3\2\2\2yz\3\2\2\2z{\3\2\2\2"+
		"{\177\5,\27\2|~\5.\30\2}|\3\2\2\2~\u0081\3\2\2\2\177}\3\2\2\2\177\u0080"+
		"\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0082w\3\2\2\2\u0083\u0086"+
		"\3\2\2\2\u0084\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u008f\3\2\2\2\u0086"+
		"\u0084\3\2\2\2\u0087\u0089\7\31\2\2\u0088\u0087\3\2\2\2\u0089\u008a\3"+
		"\2\2\2\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008c\3\2\2\2\u008c"+
		"\u008e\5\24\13\2\u008d\u0088\3\2\2\2\u008e\u0091\3\2\2\2\u008f\u008d\3"+
		"\2\2\2\u008f\u0090\3\2\2\2\u0090\u009b\3\2\2\2\u0091\u008f\3\2\2\2\u0092"+
		"\u009b\5\30\r\2\u0093\u0097\5*\26\2\u0094\u0096\5.\30\2\u0095\u0094\3"+
		"\2\2\2\u0096\u0099\3\2\2\2\u0097\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098"+
		"\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u009ah\3\2\2\2\u009ao\3\2\2\2\u009a"+
		"\u0092\3\2\2\2\u009a\u0093\3\2\2\2\u009b\t\3\2\2\2\u009c\u00a0\5\f\7\2"+
		"\u009d\u00a0\5\20\t\2\u009e\u00a0\5\22\n\2\u009f\u009c\3\2\2\2\u009f\u009d"+
		"\3\2\2\2\u009f\u009e\3\2\2\2\u00a0\13\3\2\2\2\u00a1\u00a2\7\n\2\2\u00a2"+
		"\r\3\2\2\2\u00a3\u00a4\7\r\2\2\u00a4\17\3\2\2\2\u00a5\u00a6\7\16\2\2\u00a6"+
		"\21\3\2\2\2\u00a7\u00a8\7\17\2\2\u00a8\23\3\2\2\2\u00a9\u00ad\5\26\f\2"+
		"\u00aa\u00ac\5.\30\2\u00ab\u00aa\3\2\2\2\u00ac\u00af\3\2\2\2\u00ad\u00ab"+
		"\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\25\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0"+
		"\u00cb\5\32\16\2\u00b1\u00cb\5\34\17\2\u00b2\u00cb\5\36\20\2\u00b3\u00cb"+
		"\5 \21\2\u00b4\u00cb\5\"\22\2\u00b5\u00cb\5$\23\2\u00b6\u00cb\5&\24\2"+
		"\u00b7\u00b9\7\31\2\2\u00b8\u00b7\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00b8"+
		"\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bd\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bd"+
		"\u00bf\5\30\r\2\u00be\u00ba\3\2\2\2\u00bf\u00c2\3\2\2\2\u00c0\u00be\3"+
		"\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00c6\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c3"+
		"\u00c5\7\31\2\2\u00c4\u00c3\3\2\2\2\u00c5\u00c8\3\2\2\2\u00c6\u00c4\3"+
		"\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00c9\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c9"+
		"\u00cb\5(\25\2\u00ca\u00b0\3\2\2\2\u00ca\u00b1\3\2\2\2\u00ca\u00b2\3\2"+
		"\2\2\u00ca\u00b3\3\2\2\2\u00ca\u00b4\3\2\2\2\u00ca\u00b5\3\2\2\2\u00ca"+
		"\u00b6\3\2\2\2\u00ca\u00c0\3\2\2\2\u00cb\27\3\2\2\2\u00cc\u00ce\7\27\2"+
		"\2\u00cd\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0"+
		"\3\2\2\2\u00d0\31\3\2\2\2\u00d1\u00d2\7\20\2\2\u00d2\33\3\2\2\2\u00d3"+
		"\u00d4\7\21\2\2\u00d4\35\3\2\2\2\u00d5\u00d6\7\22\2\2\u00d6\37\3\2\2\2"+
		"\u00d7\u00d9\7\23\2\2\u00d8\u00d7\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00d8"+
		"\3\2\2\2\u00da\u00db\3\2\2\2\u00db!\3\2\2\2\u00dc\u00dd\7\24\2\2\u00dd"+
		"#\3\2\2\2\u00de\u00df\7\25\2\2\u00df%\3\2\2\2\u00e0\u00e1\7\26\2\2\u00e1"+
		"\'\3\2\2\2\u00e2\u00e3\7\13\2\2\u00e3)\3\2\2\2\u00e4\u00ee\5\60\31\2\u00e5"+
		"\u00ee\7\30\2\2\u00e6\u00ee\7\20\2\2\u00e7\u00ee\7\21\2\2\u00e8\u00ee"+
		"\7\22\2\2\u00e9\u00ee\7\24\2\2\u00ea\u00ee\7\25\2\2\u00eb\u00ee\7\26\2"+
		"\2\u00ec\u00ee\7\16\2\2\u00ed\u00e4\3\2\2\2\u00ed\u00e5\3\2\2\2\u00ed"+
		"\u00e6\3\2\2\2\u00ed\u00e7\3\2\2\2\u00ed\u00e8\3\2\2\2\u00ed\u00e9\3\2"+
		"\2\2\u00ed\u00ea\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ec\3\2\2\2\u00ee"+
		"+\3\2\2\2\u00ef\u00f2\5\60\31\2\u00f0\u00f2\7\30\2\2\u00f1\u00ef\3\2\2"+
		"\2\u00f1\u00f0\3\2\2\2\u00f2-\3\2\2\2\u00f3\u0101\5\60\31\2\u00f4\u0101"+
		"\7\30\2\2\u00f5\u0101\7\27\2\2\u00f6\u0101\7\20\2\2\u00f7\u0101\7\21\2"+
		"\2\u00f8\u0101\7\22\2\2\u00f9\u0101\7\23\2\2\u00fa\u0101\7\24\2\2\u00fb"+
		"\u0101\7\25\2\2\u00fc\u0101\7\26\2\2\u00fd\u0101\7\16\2\2\u00fe\u0101"+
		"\7\17\2\2\u00ff\u0101\7\7\2\2\u0100\u00f3\3\2\2\2\u0100\u00f4\3\2\2\2"+
		"\u0100\u00f5\3\2\2\2\u0100\u00f6\3\2\2\2\u0100\u00f7\3\2\2\2\u0100\u00f8"+
		"\3\2\2\2\u0100\u00f9\3\2\2\2\u0100\u00fa\3\2\2\2\u0100\u00fb\3\2\2\2\u0100"+
		"\u00fc\3\2\2\2\u0100\u00fd\3\2\2\2\u0100\u00fe\3\2\2\2\u0100\u00ff\3\2"+
		"\2\2\u0101/\3\2\2\2\u0102\u0104\7\32\2\2\u0103\u0102\3\2\2\2\u0104\u0105"+
		"\3\2\2\2\u0105\u0103\3\2\2\2\u0105\u0106\3\2\2\2\u0106\61\3\2\2\2!\63"+
		"9?EMSY]bflsy\177\u0084\u008a\u008f\u0097\u009a\u009f\u00ad\u00ba\u00c0"+
		"\u00c6\u00ca\u00cf\u00da\u00ed\u00f1\u0100\u0105";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
