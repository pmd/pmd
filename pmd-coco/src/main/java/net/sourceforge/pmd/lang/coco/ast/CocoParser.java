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
    static {
        RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
    public static final int AFTER = 1,
            AS = 2,
            ASSERT = 3,
            ATTRIBUTE = 4,
            BECOME = 5,
            BREAK = 6,
            CASE = 7,
            COMPONENT = 8,
            CONTINUE = 9,
            ELSE = 10,
            ENUM = 11,
            ENTRY = 12,
            EXECUTION = 13,
            EXIT = 14,
            EXTERNAL = 15,
            FINAL = 16,
            FOR = 17,
            FUNCTION = 18,
            IF = 19,
            ILLEGAL = 20,
            IMPORT = 21,
            IN = 22,
            INIT = 23,
            INSTANCE = 24,
            MACHINE = 25,
            MATCH = 26,
            MUT = 27,
            MUTATING = 28,
            NONDET = 29,
            OFFER = 30,
            OPTIONAL = 31,
            OTHERWISE = 32,
            OUT = 33,
            OUTGOING = 34,
            PERIODIC = 35,
            PORT = 36,
            PRIVATE = 37,
            RETURN = 38,
            SIGNAL = 39,
            SPONTANEOUS = 40,
            STATE = 41,
            STATIC = 42,
            STRUCT = 43,
            TYPE = 44,
            UNQUALIFIED = 45,
            VAL = 46,
            VAR = 47,
            WHERE = 48,
            WHILE = 49,
            IDENTIFIER = 50,
            AT = 51,
            ASSIGN = 52,
            COLON = 53,
            LP = 54,
            RP = 55,
            LC = 56,
            RC = 57,
            LB = 58,
            RB = 59,
            COMMA = 60,
            SEMI = 61,
            DOT = 62,
            LT = 63,
            GT = 64,
            MUL = 65,
            DIV = 66,
            MINUS = 67,
            MOD = 68,
            PLUS = 69,
            IMPL = 70,
            ARROW = 71,
            AMP = 72,
            QM = 73,
            PIPE = 74,
            EXCL = 75,
            ELLIP = 76,
            EQ = 77,
            NE = 78,
            OR = 79,
            AND = 80,
            LE = 81,
            GE = 82,
            WHITESPACE = 83,
            NEWLINE = 84,
            LINE_COMMENT = 85,
            BLOCK_COMMENT = 86,
            INTEGER = 87,
            BACKTICK_LITERAL = 88,
            CHAR_LITERAL = 89,
            STRING_LITERAL = 90;
    public static final int RULE_module = 0,
            RULE_declaration = 1,
            RULE_attribute = 2,
            RULE_attributeDeclaration = 3,
            RULE_importDeclaration = 4,
            RULE_variableDeclaration = 5,
            RULE_enumDeclaration = 6,
            RULE_structDeclaration = 7,
            RULE_typeAliasDeclaration = 8,
            RULE_functionDeclaration = 9,
            RULE_instanceDeclaration = 10,
            RULE_portDeclaration = 11,
            RULE_componentDeclaration = 12,
            RULE_externalConstantDeclaration = 13,
            RULE_externalTypeDeclaration = 14,
            RULE_externalTypeElement = 15,
            RULE_externalFunctionDeclaration = 16,
            RULE_genericTypeDeclaration = 17,
            RULE_genericTypes = 18,
            RULE_genericType = 19,
            RULE_enumElement = 20,
            RULE_enumCase = 21,
            RULE_caseParameters = 22,
            RULE_caseParameter = 23,
            RULE_structElement = 24,
            RULE_fieldDeclaration = 25,
            RULE_componentElement = 26,
            RULE_staticMemberDeclaration = 27,
            RULE_constructorDeclaration = 28,
            RULE_expression = 29,
            RULE_blockExpression_ = 30,
            RULE_ifExpression_ = 31,
            RULE_matchExpression_ = 32,
            RULE_nondetExpression_ = 33,
            RULE_fieldAssignments = 34,
            RULE_fieldAssignment = 35,
            RULE_nondetClauses = 36,
            RULE_nondetClause = 37,
            RULE_matchClauses = 38,
            RULE_matchClause = 39,
            RULE_pattern = 40,
            RULE_enumCasePattern = 41,
            RULE_idParameterPatterns = 42,
            RULE_idParameterPattern = 43,
            RULE_variableDeclarationPattern = 44,
            RULE_parameterPatterns = 45,
            RULE_parameterPattern = 46,
            RULE_expressions = 47,
            RULE_statement = 48,
            RULE_declarationStatement = 49,
            RULE_returnStatement = 50,
            RULE_becomeStatement = 51,
            RULE_whileStatement = 52,
            RULE_forStatement = 53,
            RULE_breakStatement = 54,
            RULE_continueStatement = 55,
            RULE_portElement = 56,
            RULE_functionInterfaceDeclaration = 57,
            RULE_signalDeclaration = 58,
            RULE_stateMachineDeclaration = 59,
            RULE_stateMachineElement = 60,
            RULE_stateDeclaration = 61,
            RULE_eventStateDeclaration = 62,
            RULE_executionStateDeclaration = 63,
            RULE_eventStateElement = 64,
            RULE_entryFunctionDeclaration = 65,
            RULE_exitFunctionDeclaration = 66,
            RULE_stateInvariant = 67,
            RULE_transitionDeclaration = 68,
            RULE_eventTransition = 69,
            RULE_eventSource = 70,
            RULE_spontaneousTransition = 71,
            RULE_timerTransition = 72,
            RULE_eventHandler = 73,
            RULE_offer = 74,
            RULE_offerClauses = 75,
            RULE_offerClause = 76,
            RULE_parameters = 77,
            RULE_parameter = 78,
            RULE_literalExpression_ = 79,
            RULE_type = 80,
            RULE_types = 81,
            RULE_dotIdentifierList = 82;

    private static String[] makeRuleNames() {
        return new String[] {
            "module",
            "declaration",
            "attribute",
            "attributeDeclaration",
            "importDeclaration",
            "variableDeclaration",
            "enumDeclaration",
            "structDeclaration",
            "typeAliasDeclaration",
            "functionDeclaration",
            "instanceDeclaration",
            "portDeclaration",
            "componentDeclaration",
            "externalConstantDeclaration",
            "externalTypeDeclaration",
            "externalTypeElement",
            "externalFunctionDeclaration",
            "genericTypeDeclaration",
            "genericTypes",
            "genericType",
            "enumElement",
            "enumCase",
            "caseParameters",
            "caseParameter",
            "structElement",
            "fieldDeclaration",
            "componentElement",
            "staticMemberDeclaration",
            "constructorDeclaration",
            "expression",
            "blockExpression_",
            "ifExpression_",
            "matchExpression_",
            "nondetExpression_",
            "fieldAssignments",
            "fieldAssignment",
            "nondetClauses",
            "nondetClause",
            "matchClauses",
            "matchClause",
            "pattern",
            "enumCasePattern",
            "idParameterPatterns",
            "idParameterPattern",
            "variableDeclarationPattern",
            "parameterPatterns",
            "parameterPattern",
            "expressions",
            "statement",
            "declarationStatement",
            "returnStatement",
            "becomeStatement",
            "whileStatement",
            "forStatement",
            "breakStatement",
            "continueStatement",
            "portElement",
            "functionInterfaceDeclaration",
            "signalDeclaration",
            "stateMachineDeclaration",
            "stateMachineElement",
            "stateDeclaration",
            "eventStateDeclaration",
            "executionStateDeclaration",
            "eventStateElement",
            "entryFunctionDeclaration",
            "exitFunctionDeclaration",
            "stateInvariant",
            "transitionDeclaration",
            "eventTransition",
            "eventSource",
            "spontaneousTransition",
            "timerTransition",
            "eventHandler",
            "offer",
            "offerClauses",
            "offerClause",
            "parameters",
            "parameter",
            "literalExpression_",
            "type",
            "types",
            "dotIdentifierList"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[] {
            null,
            "'after'",
            "'as'",
            "'assert'",
            "'attribute'",
            "'become'",
            "'break'",
            "'case'",
            "'component'",
            "'continue'",
            "'else'",
            "'enum'",
            "'entry'",
            "'execution'",
            "'exit'",
            "'external'",
            "'final'",
            "'for'",
            "'function'",
            "'if'",
            "'illegal'",
            "'import'",
            "'in'",
            "'init'",
            "'instance'",
            "'machine'",
            "'match'",
            "'mut'",
            "'mutating'",
            "'nondet'",
            "'offer'",
            "'optional'",
            "'otherwise'",
            "'out'",
            "'outgoing'",
            "'periodic'",
            "'port'",
            "'private'",
            "'return'",
            "'signal'",
            "'spontaneous'",
            "'state'",
            "'static'",
            "'struct'",
            "'type'",
            "'unqualified'",
            "'val'",
            "'var'",
            "'where'",
            "'while'",
            null,
            "'@'",
            "'='",
            "':'",
            "'('",
            "')'",
            "'{'",
            "'}'",
            "'['",
            "']'",
            "','",
            "';'",
            "'.'",
            "'<'",
            "'>'",
            "'*'",
            "'/'",
            "'-'",
            "'%'",
            "'+'",
            "'=>'",
            "'->'",
            "'&'",
            "'?'",
            "'|'",
            "'!'",
            "'...'",
            "'=='",
            "'!='",
            "'||'",
            "'&&'",
            "'<='",
            "'>='"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[] {
            null,
            "AFTER",
            "AS",
            "ASSERT",
            "ATTRIBUTE",
            "BECOME",
            "BREAK",
            "CASE",
            "COMPONENT",
            "CONTINUE",
            "ELSE",
            "ENUM",
            "ENTRY",
            "EXECUTION",
            "EXIT",
            "EXTERNAL",
            "FINAL",
            "FOR",
            "FUNCTION",
            "IF",
            "ILLEGAL",
            "IMPORT",
            "IN",
            "INIT",
            "INSTANCE",
            "MACHINE",
            "MATCH",
            "MUT",
            "MUTATING",
            "NONDET",
            "OFFER",
            "OPTIONAL",
            "OTHERWISE",
            "OUT",
            "OUTGOING",
            "PERIODIC",
            "PORT",
            "PRIVATE",
            "RETURN",
            "SIGNAL",
            "SPONTANEOUS",
            "STATE",
            "STATIC",
            "STRUCT",
            "TYPE",
            "UNQUALIFIED",
            "VAL",
            "VAR",
            "WHERE",
            "WHILE",
            "IDENTIFIER",
            "AT",
            "ASSIGN",
            "COLON",
            "LP",
            "RP",
            "LC",
            "RC",
            "LB",
            "RB",
            "COMMA",
            "SEMI",
            "DOT",
            "LT",
            "GT",
            "MUL",
            "DIV",
            "MINUS",
            "MOD",
            "PLUS",
            "IMPL",
            "ARROW",
            "AMP",
            "QM",
            "PIPE",
            "EXCL",
            "ELLIP",
            "EQ",
            "NE",
            "OR",
            "AND",
            "LE",
            "GE",
            "WHITESPACE",
            "NEWLINE",
            "LINE_COMMENT",
            "BLOCK_COMMENT",
            "INTEGER",
            "BACKTICK_LITERAL",
            "CHAR_LITERAL",
            "STRING_LITERAL"
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
    public String getGrammarFileName() {
        return "Coco.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public CocoParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ModuleContext extends ParserRuleContext {
        public TerminalNode EOF() {
            return getToken(CocoParser.EOF, 0);
        }

        public List<DeclarationContext> declaration() {
            return getRuleContexts(DeclarationContext.class);
        }

        public DeclarationContext declaration(int i) {
            return getRuleContext(DeclarationContext.class, i);
        }

        public ModuleContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_module;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterModule(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitModule(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitModule(this);
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
                while ((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                        & ((1L << ATTRIBUTE)
                                                | (1L << COMPONENT)
                                                | (1L << ENUM)
                                                | (1L << EXTERNAL)
                                                | (1L << FUNCTION)
                                                | (1L << IMPORT)
                                                | (1L << INSTANCE)
                                                | (1L << PORT)
                                                | (1L << PRIVATE)
                                                | (1L << STRUCT)
                                                | (1L << TYPE)
                                                | (1L << VAL)
                                                | (1L << VAR)
                                                | (1L << AT)))
                                != 0)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class DeclarationContext extends ParserRuleContext {
        public ImportDeclarationContext importDeclaration() {
            return getRuleContext(ImportDeclarationContext.class, 0);
        }

        public VariableDeclarationContext variableDeclaration() {
            return getRuleContext(VariableDeclarationContext.class, 0);
        }

        public EnumDeclarationContext enumDeclaration() {
            return getRuleContext(EnumDeclarationContext.class, 0);
        }

        public StructDeclarationContext structDeclaration() {
            return getRuleContext(StructDeclarationContext.class, 0);
        }

        public TypeAliasDeclarationContext typeAliasDeclaration() {
            return getRuleContext(TypeAliasDeclarationContext.class, 0);
        }

        public FunctionDeclarationContext functionDeclaration() {
            return getRuleContext(FunctionDeclarationContext.class, 0);
        }

        public InstanceDeclarationContext instanceDeclaration() {
            return getRuleContext(InstanceDeclarationContext.class, 0);
        }

        public PortDeclarationContext portDeclaration() {
            return getRuleContext(PortDeclarationContext.class, 0);
        }

        public ComponentDeclarationContext componentDeclaration() {
            return getRuleContext(ComponentDeclarationContext.class, 0);
        }

        public ExternalConstantDeclarationContext externalConstantDeclaration() {
            return getRuleContext(ExternalConstantDeclarationContext.class, 0);
        }

        public ExternalFunctionDeclarationContext externalFunctionDeclaration() {
            return getRuleContext(ExternalFunctionDeclarationContext.class, 0);
        }

        public ExternalTypeDeclarationContext externalTypeDeclaration() {
            return getRuleContext(ExternalTypeDeclarationContext.class, 0);
        }

        public AttributeDeclarationContext attributeDeclaration() {
            return getRuleContext(AttributeDeclarationContext.class, 0);
        }

        public List<AttributeContext> attribute() {
            return getRuleContexts(AttributeContext.class);
        }

        public AttributeContext attribute(int i) {
            return getRuleContext(AttributeContext.class, i);
        }

        public DeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_declaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitDeclaration(this);
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
                while (_la == AT) {
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
                switch (getInterpreter().adaptivePredict(_input, 2, _ctx)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class AttributeContext extends ParserRuleContext {
        public TerminalNode AT() {
            return getToken(CocoParser.AT, 0);
        }

        public DotIdentifierListContext dotIdentifierList() {
            return getRuleContext(DotIdentifierListContext.class, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public ExpressionsContext expressions() {
            return getRuleContext(ExpressionsContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public AttributeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_attribute;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterAttribute(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitAttribute(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitAttribute(this);
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
                switch (getInterpreter().adaptivePredict(_input, 3, _ctx)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class AttributeDeclarationContext extends ParserRuleContext {
        public TerminalNode ATTRIBUTE() {
            return getToken(CocoParser.ATTRIBUTE, 0);
        }

        public TerminalNode AT() {
            return getToken(CocoParser.AT, 0);
        }

        public DotIdentifierListContext dotIdentifierList() {
            return getRuleContext(DotIdentifierListContext.class, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public ParametersContext parameters() {
            return getRuleContext(ParametersContext.class, 0);
        }

        public AttributeDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_attributeDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterAttributeDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitAttributeDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitAttributeDeclaration(this);
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
                if (_la == LP) {
                    {
                        setState(206);
                        match(LP);
                        setState(208);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == VAR || _la == IDENTIFIER) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ImportDeclarationContext extends ParserRuleContext {
        public TerminalNode IMPORT() {
            return getToken(CocoParser.IMPORT, 0);
        }

        public DotIdentifierListContext dotIdentifierList() {
            return getRuleContext(DotIdentifierListContext.class, 0);
        }

        public TerminalNode UNQUALIFIED() {
            return getToken(CocoParser.UNQUALIFIED, 0);
        }

        public TerminalNode AS() {
            return getToken(CocoParser.AS, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public ImportDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_importDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterImportDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitImportDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitImportDeclaration(this);
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
                if (_la == UNQUALIFIED) {
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
                if (_la == AS) {
                    {
                        setState(218);
                        match(AS);
                        setState(219);
                        match(IDENTIFIER);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class VariableDeclarationContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode VAR() {
            return getToken(CocoParser.VAR, 0);
        }

        public TerminalNode VAL() {
            return getToken(CocoParser.VAL, 0);
        }

        public TerminalNode PRIVATE() {
            return getToken(CocoParser.PRIVATE, 0);
        }

        public GenericTypeDeclarationContext genericTypeDeclaration() {
            return getRuleContext(GenericTypeDeclarationContext.class, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_variableDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterVariableDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitVariableDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitVariableDeclaration(this);
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
                if (_la == PRIVATE) {
                    {
                        setState(222);
                        match(PRIVATE);
                    }
                }

                setState(225);
                _la = _input.LA(1);
                if (!(_la == VAL || _la == VAR)) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
                setState(226);
                match(IDENTIFIER);
                setState(228);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == LT) {
                    {
                        setState(227);
                        genericTypeDeclaration();
                    }
                }

                setState(232);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
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
                if (_la == ASSIGN) {
                    {
                        setState(234);
                        match(ASSIGN);
                        setState(235);
                        expression(0);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class EnumDeclarationContext extends ParserRuleContext {
        public TerminalNode ENUM() {
            return getToken(CocoParser.ENUM, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public GenericTypeDeclarationContext genericTypeDeclaration() {
            return getRuleContext(GenericTypeDeclarationContext.class, 0);
        }

        public List<EnumElementContext> enumElement() {
            return getRuleContexts(EnumElementContext.class);
        }

        public EnumElementContext enumElement(int i) {
            return getRuleContext(EnumElementContext.class, i);
        }

        public EnumDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_enumDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterEnumDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitEnumDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitEnumDeclaration(this);
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
                if (_la == LT) {
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
                while ((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                        & ((1L << CASE)
                                                | (1L << FUNCTION)
                                                | (1L << MUT)
                                                | (1L << MUTATING)
                                                | (1L << STATIC)))
                                != 0)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class StructDeclarationContext extends ParserRuleContext {
        public TerminalNode STRUCT() {
            return getToken(CocoParser.STRUCT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public GenericTypeDeclarationContext genericTypeDeclaration() {
            return getRuleContext(GenericTypeDeclarationContext.class, 0);
        }

        public List<StructElementContext> structElement() {
            return getRuleContexts(StructElementContext.class);
        }

        public StructElementContext structElement(int i) {
            return getRuleContext(StructElementContext.class, i);
        }

        public StructDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_structDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterStructDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitStructDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitStructDeclaration(this);
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
                if (_la == LT) {
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
                while ((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                        & ((1L << FUNCTION)
                                                | (1L << MUT)
                                                | (1L << MUTATING)
                                                | (1L << STATIC)
                                                | (1L << VAL)
                                                | (1L << VAR)
                                                | (1L << IDENTIFIER)))
                                != 0)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class TypeAliasDeclarationContext extends ParserRuleContext {
        public TerminalNode TYPE() {
            return getToken(CocoParser.TYPE, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public GenericTypeDeclarationContext genericTypeDeclaration() {
            return getRuleContext(GenericTypeDeclarationContext.class, 0);
        }

        public TypeAliasDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_typeAliasDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterTypeAliasDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitTypeAliasDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitTypeAliasDeclaration(this);
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
                if (_la == LT) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class FunctionDeclarationContext extends ParserRuleContext {
        public TerminalNode FUNCTION() {
            return getToken(CocoParser.FUNCTION, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public GenericTypeDeclarationContext genericTypeDeclaration() {
            return getRuleContext(GenericTypeDeclarationContext.class, 0);
        }

        public ParametersContext parameters() {
            return getRuleContext(ParametersContext.class, 0);
        }

        public FunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_functionDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterFunctionDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitFunctionDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitFunctionDeclaration(this);
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
                if (_la == LT) {
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
                if (_la == VAR || _la == IDENTIFIER) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class InstanceDeclarationContext extends ParserRuleContext {
        public TerminalNode INSTANCE() {
            return getToken(CocoParser.INSTANCE, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public ParametersContext parameters() {
            return getRuleContext(ParametersContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public BlockExpression_Context blockExpression_() {
            return getRuleContext(BlockExpression_Context.class, 0);
        }

        public GenericTypeDeclarationContext genericTypeDeclaration() {
            return getRuleContext(GenericTypeDeclarationContext.class, 0);
        }

        public InstanceDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_instanceDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterInstanceDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitInstanceDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitInstanceDeclaration(this);
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
                if (_la == LT) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class PortDeclarationContext extends ParserRuleContext {
        public TerminalNode PORT() {
            return getToken(CocoParser.PORT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypesContext types() {
            return getRuleContext(TypesContext.class, 0);
        }

        public TerminalNode FINAL() {
            return getToken(CocoParser.FINAL, 0);
        }

        public List<PortElementContext> portElement() {
            return getRuleContexts(PortElementContext.class);
        }

        public PortElementContext portElement(int i) {
            return getRuleContext(PortElementContext.class, i);
        }

        public PortDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_portDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterPortDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitPortDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitPortDeclaration(this);
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
                if (_la == COLON) {
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
                if (_la == FINAL) {
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
                while ((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                        & ((1L << ENUM)
                                                | (1L << EXTERNAL)
                                                | (1L << FUNCTION)
                                                | (1L << MACHINE)
                                                | (1L << OUTGOING)
                                                | (1L << PORT)
                                                | (1L << STATIC)
                                                | (1L << STRUCT)
                                                | (1L << TYPE)
                                                | (1L << VAL)
                                                | (1L << VAR)
                                                | (1L << IDENTIFIER)
                                                | (1L << AT)))
                                != 0)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ComponentDeclarationContext extends ParserRuleContext {
        public TerminalNode COMPONENT() {
            return getToken(CocoParser.COMPONENT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public TerminalNode EXTERNAL() {
            return getToken(CocoParser.EXTERNAL, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public List<ComponentElementContext> componentElement() {
            return getRuleContexts(ComponentElementContext.class);
        }

        public ComponentElementContext componentElement(int i) {
            return getRuleContext(ComponentElementContext.class, i);
        }

        public ComponentDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_componentDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterComponentDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitComponentDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitComponentDeclaration(this);
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
                if (_la == EXTERNAL) {
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
                if (_la == COLON) {
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
                while ((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                        & ((1L << INIT)
                                                | (1L << MACHINE)
                                                | (1L << PRIVATE)
                                                | (1L << STATIC)
                                                | (1L << VAL)
                                                | (1L << VAR)
                                                | (1L << IDENTIFIER)
                                                | (1L << AT)))
                                != 0)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ExternalConstantDeclarationContext extends ParserRuleContext {
        public TerminalNode EXTERNAL() {
            return getToken(CocoParser.EXTERNAL, 0);
        }

        public TerminalNode VAL() {
            return getToken(CocoParser.VAL, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ExternalConstantDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_externalConstantDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterExternalConstantDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitExternalConstantDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitExternalConstantDeclaration(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ExternalTypeDeclarationContext extends ParserRuleContext {
        public TerminalNode EXTERNAL() {
            return getToken(CocoParser.EXTERNAL, 0);
        }

        public TerminalNode TYPE() {
            return getToken(CocoParser.TYPE, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public List<ExternalTypeElementContext> externalTypeElement() {
            return getRuleContexts(ExternalTypeElementContext.class);
        }

        public ExternalTypeElementContext externalTypeElement(int i) {
            return getRuleContext(ExternalTypeElementContext.class, i);
        }

        public ExternalTypeDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_externalTypeDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterExternalTypeDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitExternalTypeDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitExternalTypeDeclaration(this);
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
                if (_la == LC) {
                    {
                        setState(346);
                        match(LC);
                        setState(350);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << EXTERNAL)
                                                        | (1L << MUT)
                                                        | (1L << MUTATING)
                                                        | (1L << PRIVATE)
                                                        | (1L << STATIC)
                                                        | (1L << VAL)
                                                        | (1L << VAR)))
                                        != 0)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ExternalTypeElementContext extends ParserRuleContext {
        public StaticMemberDeclarationContext staticMemberDeclaration() {
            return getRuleContext(StaticMemberDeclarationContext.class, 0);
        }

        public VariableDeclarationContext variableDeclaration() {
            return getRuleContext(VariableDeclarationContext.class, 0);
        }

        public ExternalFunctionDeclarationContext externalFunctionDeclaration() {
            return getRuleContext(ExternalFunctionDeclarationContext.class, 0);
        }

        public TerminalNode MUT() {
            return getToken(CocoParser.MUT, 0);
        }

        public TerminalNode MUTATING() {
            return getToken(CocoParser.MUTATING, 0);
        }

        public ExternalTypeElementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_externalTypeElement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterExternalTypeElement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitExternalTypeElement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitExternalTypeElement(this);
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
                        if (_la == MUT || _la == MUTATING) {
                            {
                                setState(358);
                                _la = _input.LA(1);
                                if (!(_la == MUT || _la == MUTATING)) {
                                    _errHandler.recoverInline(this);
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ExternalFunctionDeclarationContext extends ParserRuleContext {
        public TerminalNode EXTERNAL() {
            return getToken(CocoParser.EXTERNAL, 0);
        }

        public TerminalNode FUNCTION() {
            return getToken(CocoParser.FUNCTION, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ParametersContext parameters() {
            return getRuleContext(ParametersContext.class, 0);
        }

        public ExternalFunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_externalFunctionDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterExternalFunctionDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitExternalFunctionDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitExternalFunctionDeclaration(this);
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
                if (_la == VAR || _la == IDENTIFIER) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class GenericTypeDeclarationContext extends ParserRuleContext {
        public TerminalNode LT() {
            return getToken(CocoParser.LT, 0);
        }

        public GenericTypesContext genericTypes() {
            return getRuleContext(GenericTypesContext.class, 0);
        }

        public TerminalNode GT() {
            return getToken(CocoParser.GT, 0);
        }

        public TerminalNode WHERE() {
            return getToken(CocoParser.WHERE, 0);
        }

        public ExpressionsContext expressions() {
            return getRuleContext(ExpressionsContext.class, 0);
        }

        public GenericTypeDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_genericTypeDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterGenericTypeDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitGenericTypeDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitGenericTypeDeclaration(this);
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
                if (_la == WHERE) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(GenericTypeContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(CocoParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(CocoParser.COMMA, i);
        }

        public GenericTypesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_genericTypes;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterGenericTypes(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitGenericTypes(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitGenericTypes(this);
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
                while (_la == COMMA) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class GenericTypeContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode VAL() {
            return getToken(CocoParser.VAL, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public GenericTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_genericType;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterGenericType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitGenericType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitGenericType(this);
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
                if (_la == VAL) {
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
                if (_la == COLON) {
                    {
                        setState(397);
                        match(COLON);
                        setState(398);
                        type(0);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class EnumElementContext extends ParserRuleContext {
        public EnumCaseContext enumCase() {
            return getRuleContext(EnumCaseContext.class, 0);
        }

        public FunctionDeclarationContext functionDeclaration() {
            return getRuleContext(FunctionDeclarationContext.class, 0);
        }

        public TerminalNode MUT() {
            return getToken(CocoParser.MUT, 0);
        }

        public TerminalNode MUTATING() {
            return getToken(CocoParser.MUTATING, 0);
        }

        public StaticMemberDeclarationContext staticMemberDeclaration() {
            return getRuleContext(StaticMemberDeclarationContext.class, 0);
        }

        public EnumElementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_enumElement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterEnumElement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitEnumElement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitEnumElement(this);
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
                        if (_la == MUT || _la == MUTATING) {
                            {
                                setState(402);
                                _la = _input.LA(1);
                                if (!(_la == MUT || _la == MUTATING)) {
                                    _errHandler.recoverInline(this);
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class EnumCaseContext extends ParserRuleContext {
        public TerminalNode CASE() {
            return getToken(CocoParser.CASE, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public CaseParametersContext caseParameters() {
            return getRuleContext(CaseParametersContext.class, 0);
        }

        public EnumCaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_enumCase;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterEnumCase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitEnumCase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitEnumCase(this);
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
                if (_la == LP) {
                    {
                        setState(411);
                        match(LP);
                        setState(413);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == IDENTIFIER) {
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
                if (_la == ASSIGN) {
                    {
                        setState(418);
                        match(ASSIGN);
                        setState(419);
                        expression(0);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(CaseParameterContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(CocoParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(CocoParser.COMMA, i);
        }

        public CaseParametersContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_caseParameters;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterCaseParameters(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitCaseParameters(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitCaseParameters(this);
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
                while (_la == COMMA) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class CaseParameterContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public CaseParameterContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_caseParameter;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterCaseParameter(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitCaseParameter(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitCaseParameter(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class StructElementContext extends ParserRuleContext {
        public FieldDeclarationContext fieldDeclaration() {
            return getRuleContext(FieldDeclarationContext.class, 0);
        }

        public FunctionDeclarationContext functionDeclaration() {
            return getRuleContext(FunctionDeclarationContext.class, 0);
        }

        public StaticMemberDeclarationContext staticMemberDeclaration() {
            return getRuleContext(StaticMemberDeclarationContext.class, 0);
        }

        public TerminalNode SEMI() {
            return getToken(CocoParser.SEMI, 0);
        }

        public TerminalNode MUT() {
            return getToken(CocoParser.MUT, 0);
        }

        public TerminalNode MUTATING() {
            return getToken(CocoParser.MUTATING, 0);
        }

        public StructElementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_structElement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterStructElement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitStructElement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitStructElement(this);
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
                            if (_la == MUT || _la == MUTATING) {
                                {
                                    setState(435);
                                    _la = _input.LA(1);
                                    if (!(_la == MUT || _la == MUTATING)) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
                if (_la == SEMI) {
                    {
                        setState(442);
                        match(SEMI);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class FieldDeclarationContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode VAL() {
            return getToken(CocoParser.VAL, 0);
        }

        public TerminalNode VAR() {
            return getToken(CocoParser.VAR, 0);
        }

        public FieldDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_fieldDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterFieldDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitFieldDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitFieldDeclaration(this);
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
                if (_la == VAL || _la == VAR) {
                    {
                        setState(445);
                        _la = _input.LA(1);
                        if (!(_la == VAL || _la == VAR)) {
                            _errHandler.recoverInline(this);
                        } else {
                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
                if (_la == ASSIGN) {
                    {
                        setState(451);
                        match(ASSIGN);
                        setState(452);
                        expression(0);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ComponentElementContext extends ParserRuleContext {
        public FieldDeclarationContext fieldDeclaration() {
            return getRuleContext(FieldDeclarationContext.class, 0);
        }

        public VariableDeclarationContext variableDeclaration() {
            return getRuleContext(VariableDeclarationContext.class, 0);
        }

        public ConstructorDeclarationContext constructorDeclaration() {
            return getRuleContext(ConstructorDeclarationContext.class, 0);
        }

        public StateMachineDeclarationContext stateMachineDeclaration() {
            return getRuleContext(StateMachineDeclarationContext.class, 0);
        }

        public StaticMemberDeclarationContext staticMemberDeclaration() {
            return getRuleContext(StaticMemberDeclarationContext.class, 0);
        }

        public List<AttributeContext> attribute() {
            return getRuleContexts(AttributeContext.class);
        }

        public AttributeContext attribute(int i) {
            return getRuleContext(AttributeContext.class, i);
        }

        public TerminalNode SEMI() {
            return getToken(CocoParser.SEMI, 0);
        }

        public ComponentElementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_componentElement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterComponentElement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitComponentElement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitComponentElement(this);
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
                while (_la == AT) {
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
                switch (getInterpreter().adaptivePredict(_input, 47, _ctx)) {
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
                if (_la == SEMI) {
                    {
                        setState(468);
                        match(SEMI);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class StaticMemberDeclarationContext extends ParserRuleContext {
        public TerminalNode STATIC() {
            return getToken(CocoParser.STATIC, 0);
        }

        public TerminalNode VAL() {
            return getToken(CocoParser.VAL, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public StaticMemberDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_staticMemberDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterStaticMemberDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitStaticMemberDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitStaticMemberDeclaration(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ConstructorDeclarationContext extends ParserRuleContext {
        public TerminalNode INIT() {
            return getToken(CocoParser.INIT, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public BlockExpression_Context blockExpression_() {
            return getRuleContext(BlockExpression_Context.class, 0);
        }

        public ParametersContext parameters() {
            return getRuleContext(ParametersContext.class, 0);
        }

        public ConstructorDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_constructorDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterConstructorDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitConstructorDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitConstructorDeclaration(this);
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
                if (_la == VAR || _la == IDENTIFIER) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ExpressionContext extends ParserRuleContext {
        public ExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expression;
        }

        public ExpressionContext() {}

        public void copyFrom(ExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class IfExpressionContext extends ExpressionContext {
        public IfExpression_Context ifExpression_() {
            return getRuleContext(IfExpression_Context.class, 0);
        }

        public IfExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterIfExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitIfExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitIfExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class TryOperatorExpressionContext extends ExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode QM() {
            return getToken(CocoParser.QM, 0);
        }

        public TryOperatorExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterTryOperatorExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitTryOperatorExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitTryOperatorExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class UnaryOperatorExpressionContext extends ExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode MINUS() {
            return getToken(CocoParser.MINUS, 0);
        }

        public TerminalNode EXCL() {
            return getToken(CocoParser.EXCL, 0);
        }

        public TerminalNode AMP() {
            return getToken(CocoParser.AMP, 0);
        }

        public UnaryOperatorExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterUnaryOperatorExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitUnaryOperatorExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitUnaryOperatorExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class OptionalExpressionContext extends ExpressionContext {
        public TerminalNode OPTIONAL() {
            return getToken(CocoParser.OPTIONAL, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public OptionalExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterOptionalExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitOptionalExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitOptionalExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ArithmicOrLogicalExpressionContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode MUL() {
            return getToken(CocoParser.MUL, 0);
        }

        public TerminalNode DIV() {
            return getToken(CocoParser.DIV, 0);
        }

        public TerminalNode MOD() {
            return getToken(CocoParser.MOD, 0);
        }

        public TerminalNode PLUS() {
            return getToken(CocoParser.PLUS, 0);
        }

        public TerminalNode MINUS() {
            return getToken(CocoParser.MINUS, 0);
        }

        public TerminalNode EQ() {
            return getToken(CocoParser.EQ, 0);
        }

        public TerminalNode NE() {
            return getToken(CocoParser.NE, 0);
        }

        public TerminalNode OR() {
            return getToken(CocoParser.OR, 0);
        }

        public TerminalNode AND() {
            return getToken(CocoParser.AND, 0);
        }

        public TerminalNode LT() {
            return getToken(CocoParser.LT, 0);
        }

        public TerminalNode LE() {
            return getToken(CocoParser.LE, 0);
        }

        public TerminalNode GT() {
            return getToken(CocoParser.GT, 0);
        }

        public TerminalNode GE() {
            return getToken(CocoParser.GE, 0);
        }

        public ArithmicOrLogicalExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterArithmicOrLogicalExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitArithmicOrLogicalExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitArithmicOrLogicalExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class LiteralExpressionContext extends ExpressionContext {
        public LiteralExpression_Context literalExpression_() {
            return getRuleContext(LiteralExpression_Context.class, 0);
        }

        public LiteralExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterLiteralExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitLiteralExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitLiteralExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ArrayLiteralExpressionContext extends ExpressionContext {
        public TerminalNode LB() {
            return getToken(CocoParser.LB, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RB() {
            return getToken(CocoParser.RB, 0);
        }

        public ArrayLiteralExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterArrayLiteralExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitArrayLiteralExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitArrayLiteralExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class NondetExpressionContext extends ExpressionContext {
        public NondetExpression_Context nondetExpression_() {
            return getRuleContext(NondetExpression_Context.class, 0);
        }

        public NondetExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterNondetExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitNondetExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitNondetExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class GroupedExpressionContext extends ExpressionContext {
        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public ExpressionsContext expressions() {
            return getRuleContext(ExpressionsContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public GroupedExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterGroupedExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitGroupedExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitGroupedExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class BlockExpressionContext extends ExpressionContext {
        public BlockExpression_Context blockExpression_() {
            return getRuleContext(BlockExpression_Context.class, 0);
        }

        public BlockExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterBlockExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitBlockExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitBlockExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class MatchExpressionContext extends ExpressionContext {
        public MatchExpression_Context matchExpression_() {
            return getRuleContext(MatchExpression_Context.class, 0);
        }

        public MatchExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterMatchExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitMatchExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitMatchExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class StructLiteralExpressionContext extends ExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public FieldAssignmentsContext fieldAssignments() {
            return getRuleContext(FieldAssignmentsContext.class, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public StructLiteralExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterStructLiteralExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitStructLiteralExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitStructLiteralExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class MemberReferenceExpressionContext extends ExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode DOT() {
            return getToken(CocoParser.DOT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public MemberReferenceExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterMemberReferenceExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitMemberReferenceExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitMemberReferenceExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class AssignmentExpressionContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public AssignmentExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterAssignmentExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitAssignmentExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitAssignmentExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class VariableReferenceExpressionContext extends ExpressionContext {
        public DotIdentifierListContext dotIdentifierList() {
            return getRuleContext(DotIdentifierListContext.class, 0);
        }

        public TerminalNode LT() {
            return getToken(CocoParser.LT, 0);
        }

        public GenericTypesContext genericTypes() {
            return getRuleContext(GenericTypesContext.class, 0);
        }

        public TerminalNode GT() {
            return getToken(CocoParser.GT, 0);
        }

        public VariableReferenceExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterVariableReferenceExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitVariableReferenceExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitVariableReferenceExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ImplicitMemberExpressionContext extends ExpressionContext {
        public TerminalNode DOT() {
            return getToken(CocoParser.DOT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public ImplicitMemberExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterImplicitMemberExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitImplicitMemberExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitImplicitMemberExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ExternalFunctionContext extends ExpressionContext {
        public ExternalFunctionDeclarationContext externalFunctionDeclaration() {
            return getRuleContext(ExternalFunctionDeclarationContext.class, 0);
        }

        public ExternalFunctionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterExternalFunction(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitExternalFunction(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitExternalFunction(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class CastExpressionContext extends ExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode AS() {
            return getToken(CocoParser.AS, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public CastExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterCastExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitCastExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitCastExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class StateInvariantExpressionContext extends ExpressionContext {
        public StateInvariantContext stateInvariant() {
            return getRuleContext(StateInvariantContext.class, 0);
        }

        public StateInvariantExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterStateInvariantExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitStateInvariantExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitStateInvariantExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class CallExpressionContext extends ExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public ExpressionsContext expressions() {
            return getRuleContext(ExpressionsContext.class, 0);
        }

        public CallExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterCallExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitCallExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitCallExpression(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ExternalLiteralContext extends ExpressionContext {
        public TerminalNode EXTERNAL() {
            return getToken(CocoParser.EXTERNAL, 0);
        }

        public TerminalNode BACKTICK_LITERAL() {
            return getToken(CocoParser.BACKTICK_LITERAL, 0);
        }

        public ExternalLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterExternalLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitExternalLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitExternalLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ArraySubscriptExpressionContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode LB() {
            return getToken(CocoParser.LB, 0);
        }

        public TerminalNode RB() {
            return getToken(CocoParser.RB, 0);
        }

        public ArraySubscriptExpressionContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterArraySubscriptExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitArraySubscriptExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitArraySubscriptExpression(this);
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
                switch (getInterpreter().adaptivePredict(_input, 51, _ctx)) {
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
                            switch (getInterpreter().adaptivePredict(_input, 50, _ctx)) {
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
                            if (!(((((_la - 67)) & ~0x3f) == 0
                                    && ((1L << (_la - 67))
                                                    & ((1L << (MINUS - 67)) | (1L << (AMP - 67)) | (1L << (EXCL - 67))))
                                            != 0))) {
                                _errHandler.recoverInline(this);
                            } else {
                                if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
                _alt = getInterpreter().adaptivePredict(_input, 54, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(557);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 53, _ctx)) {
                                case 1:
                                    {
                                        _localctx = new ArithmicOrLogicalExpressionContext(
                                                new ExpressionContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                        setState(521);
                                        if (!(precpred(_ctx, 12)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 12)");
                                        setState(522);
                                        _la = _input.LA(1);
                                        if (!(((((_la - 65)) & ~0x3f) == 0
                                                && ((1L << (_la - 65))
                                                                & ((1L << (MUL - 65))
                                                                        | (1L << (DIV - 65))
                                                                        | (1L << (MOD - 65))))
                                                        != 0))) {
                                            _errHandler.recoverInline(this);
                                        } else {
                                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                            _errHandler.reportMatch(this);
                                            consume();
                                        }
                                        setState(523);
                                        expression(13);
                                    }
                                    break;
                                case 2:
                                    {
                                        _localctx = new ArithmicOrLogicalExpressionContext(
                                                new ExpressionContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                        setState(524);
                                        if (!(precpred(_ctx, 11)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 11)");
                                        setState(525);
                                        _la = _input.LA(1);
                                        if (!(_la == MINUS || _la == PLUS)) {
                                            _errHandler.recoverInline(this);
                                        } else {
                                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                            _errHandler.reportMatch(this);
                                            consume();
                                        }
                                        setState(526);
                                        expression(12);
                                    }
                                    break;
                                case 3:
                                    {
                                        _localctx = new ArithmicOrLogicalExpressionContext(
                                                new ExpressionContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                        setState(527);
                                        if (!(precpred(_ctx, 10)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 10)");
                                        setState(528);
                                        _la = _input.LA(1);
                                        if (!(((((_la - 63)) & ~0x3f) == 0
                                                && ((1L << (_la - 63))
                                                                & ((1L << (LT - 63))
                                                                        | (1L << (GT - 63))
                                                                        | (1L << (EQ - 63))
                                                                        | (1L << (NE - 63))
                                                                        | (1L << (OR - 63))
                                                                        | (1L << (AND - 63))
                                                                        | (1L << (LE - 63))
                                                                        | (1L << (GE - 63))))
                                                        != 0))) {
                                            _errHandler.recoverInline(this);
                                        } else {
                                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                            _errHandler.reportMatch(this);
                                            consume();
                                        }
                                        setState(529);
                                        expression(11);
                                    }
                                    break;
                                case 4:
                                    {
                                        _localctx = new AssignmentExpressionContext(
                                                new ExpressionContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                        setState(530);
                                        if (!(precpred(_ctx, 9)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 9)");
                                        setState(531);
                                        match(ASSIGN);
                                        setState(532);
                                        expression(10);
                                    }
                                    break;
                                case 5:
                                    {
                                        _localctx = new MemberReferenceExpressionContext(
                                                new ExpressionContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                        setState(533);
                                        if (!(precpred(_ctx, 20)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 20)");
                                        setState(534);
                                        match(DOT);
                                        setState(535);
                                        match(IDENTIFIER);
                                    }
                                    break;
                                case 6:
                                    {
                                        _localctx = new CallExpressionContext(
                                                new ExpressionContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                        setState(536);
                                        if (!(precpred(_ctx, 18)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 18)");
                                        setState(537);
                                        match(LP);
                                        setState(539);
                                        _errHandler.sync(this);
                                        _la = _input.LA(1);
                                        if ((((_la) & ~0x3f) == 0
                                                        && ((1L << _la)
                                                                        & ((1L << ASSERT)
                                                                                | (1L << EXTERNAL)
                                                                                | (1L << IF)
                                                                                | (1L << MATCH)
                                                                                | (1L << NONDET)
                                                                                | (1L << OPTIONAL)
                                                                                | (1L << IDENTIFIER)
                                                                                | (1L << LP)
                                                                                | (1L << LC)
                                                                                | (1L << LB)
                                                                                | (1L << DOT)))
                                                                != 0)
                                                || ((((_la - 67)) & ~0x3f) == 0
                                                        && ((1L << (_la - 67))
                                                                        & ((1L << (MINUS - 67))
                                                                                | (1L << (AMP - 67))
                                                                                | (1L << (EXCL - 67))
                                                                                | (1L << (INTEGER - 67))
                                                                                | (1L << (CHAR_LITERAL - 67))
                                                                                | (1L << (STRING_LITERAL - 67))))
                                                                != 0)) {
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
                                        _localctx = new ArraySubscriptExpressionContext(
                                                new ExpressionContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                        setState(542);
                                        if (!(precpred(_ctx, 17)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 17)");
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
                                        _localctx = new StructLiteralExpressionContext(
                                                new ExpressionContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                        setState(547);
                                        if (!(precpred(_ctx, 16)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 16)");
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
                                        _localctx = new TryOperatorExpressionContext(
                                                new ExpressionContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                        setState(552);
                                        if (!(precpred(_ctx, 15)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 15)");
                                        setState(553);
                                        match(QM);
                                    }
                                    break;
                                case 10:
                                    {
                                        _localctx = new CastExpressionContext(
                                                new ExpressionContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                        setState(554);
                                        if (!(precpred(_ctx, 13)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 13)");
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
                    _alt = getInterpreter().adaptivePredict(_input, 54, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class BlockExpression_Context extends ParserRuleContext {
        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public List<StatementContext> statement() {
            return getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return getRuleContext(StatementContext.class, i);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public BlockExpression_Context(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_blockExpression_;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterBlockExpression_(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitBlockExpression_(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitBlockExpression_(this);
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
                _alt = getInterpreter().adaptivePredict(_input, 55, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(563);
                                statement();
                            }
                        }
                    }
                    setState(568);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 55, _ctx);
                }
                setState(570);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << ASSERT)
                                                        | (1L << EXTERNAL)
                                                        | (1L << IF)
                                                        | (1L << MATCH)
                                                        | (1L << NONDET)
                                                        | (1L << OPTIONAL)
                                                        | (1L << IDENTIFIER)
                                                        | (1L << LP)
                                                        | (1L << LC)
                                                        | (1L << LB)
                                                        | (1L << DOT)))
                                        != 0)
                        || ((((_la - 67)) & ~0x3f) == 0
                                && ((1L << (_la - 67))
                                                & ((1L << (MINUS - 67))
                                                        | (1L << (AMP - 67))
                                                        | (1L << (EXCL - 67))
                                                        | (1L << (INTEGER - 67))
                                                        | (1L << (CHAR_LITERAL - 67))
                                                        | (1L << (STRING_LITERAL - 67))))
                                        != 0)) {
                    {
                        setState(569);
                        expression(0);
                    }
                }

                setState(572);
                match(RC);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class IfExpression_Context extends ParserRuleContext {
        public TerminalNode IF() {
            return getToken(CocoParser.IF, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode ELSE() {
            return getToken(CocoParser.ELSE, 0);
        }

        public IfExpression_Context(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_ifExpression_;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterIfExpression_(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitIfExpression_(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitIfExpression_(this);
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
                switch (getInterpreter().adaptivePredict(_input, 57, _ctx)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class MatchExpression_Context extends ParserRuleContext {
        public TerminalNode MATCH() {
            return getToken(CocoParser.MATCH, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public MatchClausesContext matchClauses() {
            return getRuleContext(MatchClausesContext.class, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public MatchExpression_Context(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matchExpression_;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterMatchExpression_(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitMatchExpression_(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitMatchExpression_(this);
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
                if (_la == IDENTIFIER) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class NondetExpression_Context extends ParserRuleContext {
        public TerminalNode NONDET() {
            return getToken(CocoParser.NONDET, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public NondetClausesContext nondetClauses() {
            return getRuleContext(NondetClausesContext.class, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public TerminalNode OTHERWISE() {
            return getToken(CocoParser.OTHERWISE, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode COMMA() {
            return getToken(CocoParser.COMMA, 0);
        }

        public TerminalNode SEMI() {
            return getToken(CocoParser.SEMI, 0);
        }

        public NondetExpression_Context(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_nondetExpression_;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterNondetExpression_(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitNondetExpression_(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitNondetExpression_(this);
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
                if (_la == OTHERWISE) {
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
                if (_la == COMMA || _la == SEMI) {
                    {
                        setState(602);
                        _la = _input.LA(1);
                        if (!(_la == COMMA || _la == SEMI)) {
                            _errHandler.recoverInline(this);
                        } else {
                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
                            _errHandler.reportMatch(this);
                            consume();
                        }
                    }
                }

                setState(605);
                match(RC);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(FieldAssignmentContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(CocoParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(CocoParser.COMMA, i);
        }

        public FieldAssignmentsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_fieldAssignments;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterFieldAssignments(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitFieldAssignments(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitFieldAssignments(this);
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
                _alt = getInterpreter().adaptivePredict(_input, 61, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
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
                    _alt = getInterpreter().adaptivePredict(_input, 61, _ctx);
                }
                setState(616);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COMMA) {
                    {
                        setState(615);
                        match(COMMA);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class FieldAssignmentContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public FieldAssignmentContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_fieldAssignment;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterFieldAssignment(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitFieldAssignment(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitFieldAssignment(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(NondetClauseContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(CocoParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(CocoParser.COMMA, i);
        }

        public List<TerminalNode> SEMI() {
            return getTokens(CocoParser.SEMI);
        }

        public TerminalNode SEMI(int i) {
            return getToken(CocoParser.SEMI, i);
        }

        public NondetClausesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_nondetClauses;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterNondetClauses(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitNondetClauses(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitNondetClauses(this);
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
                _alt = getInterpreter().adaptivePredict(_input, 63, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(623);
                                _la = _input.LA(1);
                                if (!(_la == COMMA || _la == SEMI)) {
                                    _errHandler.recoverInline(this);
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
                    _alt = getInterpreter().adaptivePredict(_input, 63, _ctx);
                }
                setState(631);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 64, _ctx)) {
                    case 1:
                        {
                            setState(630);
                            _la = _input.LA(1);
                            if (!(_la == COMMA || _la == SEMI)) {
                                _errHandler.recoverInline(this);
                            } else {
                                if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                _errHandler.reportMatch(this);
                                consume();
                            }
                        }
                        break;
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode IF() {
            return getToken(CocoParser.IF, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public NondetClauseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_nondetClause;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterNondetClause(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitNondetClause(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitNondetClause(this);
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
                switch (getInterpreter().adaptivePredict(_input, 65, _ctx)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(MatchClauseContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(CocoParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(CocoParser.COMMA, i);
        }

        public List<TerminalNode> SEMI() {
            return getTokens(CocoParser.SEMI);
        }

        public TerminalNode SEMI(int i) {
            return getToken(CocoParser.SEMI, i);
        }

        public MatchClausesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matchClauses;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterMatchClauses(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitMatchClauses(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitMatchClauses(this);
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
                _alt = getInterpreter().adaptivePredict(_input, 66, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(643);
                                _la = _input.LA(1);
                                if (!(_la == COMMA || _la == SEMI)) {
                                    _errHandler.recoverInline(this);
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
                    _alt = getInterpreter().adaptivePredict(_input, 66, _ctx);
                }
                setState(651);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COMMA || _la == SEMI) {
                    {
                        setState(650);
                        _la = _input.LA(1);
                        if (!(_la == COMMA || _la == SEMI)) {
                            _errHandler.recoverInline(this);
                        } else {
                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
                            _errHandler.reportMatch(this);
                            consume();
                        }
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class MatchClauseContext extends ParserRuleContext {
        public PatternContext pattern() {
            return getRuleContext(PatternContext.class, 0);
        }

        public TerminalNode IMPL() {
            return getToken(CocoParser.IMPL, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public MatchClauseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_matchClause;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterMatchClause(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitMatchClause(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitMatchClause(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class PatternContext extends ParserRuleContext {
        public EnumCasePatternContext enumCasePattern() {
            return getRuleContext(EnumCasePatternContext.class, 0);
        }

        public LiteralExpression_Context literalExpression_() {
            return getRuleContext(LiteralExpression_Context.class, 0);
        }

        public VariableDeclarationPatternContext variableDeclarationPattern() {
            return getRuleContext(VariableDeclarationPatternContext.class, 0);
        }

        public PatternContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_pattern;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterPattern(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitPattern(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitPattern(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PatternContext pattern() throws RecognitionException {
        PatternContext _localctx = new PatternContext(_ctx, getState());
        enterRule(_localctx, 80, RULE_pattern);
        try {
            setState(660);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 68, _ctx)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class EnumCasePatternContext extends ParserRuleContext {
        public IdParameterPatternsContext idParameterPatterns() {
            return getRuleContext(IdParameterPatternsContext.class, 0);
        }

        public TerminalNode IF() {
            return getToken(CocoParser.IF, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public EnumCasePatternContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_enumCasePattern;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterEnumCasePattern(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitEnumCasePattern(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitEnumCasePattern(this);
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
                if (_la == IF) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(IdParameterPatternContext.class, i);
        }

        public List<TerminalNode> DOT() {
            return getTokens(CocoParser.DOT);
        }

        public TerminalNode DOT(int i) {
            return getToken(CocoParser.DOT, i);
        }

        public IdParameterPatternsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_idParameterPatterns;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterIdParameterPatterns(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitIdParameterPatterns(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitIdParameterPatterns(this);
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
                        while (_la == DOT) {
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
                        while (_la == DOT) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class IdParameterPatternContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public ParameterPatternsContext parameterPatterns() {
            return getRuleContext(ParameterPatternsContext.class, 0);
        }

        public IdParameterPatternContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_idParameterPattern;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterIdParameterPattern(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitIdParameterPattern(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitIdParameterPattern(this);
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
                if (_la == LP) {
                    {
                        setState(690);
                        match(LP);
                        setState(692);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == VAR || _la == IDENTIFIER) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class VariableDeclarationPatternContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode VAL() {
            return getToken(CocoParser.VAL, 0);
        }

        public TerminalNode DOT() {
            return getToken(CocoParser.DOT, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public VariableDeclarationPatternContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_variableDeclarationPattern;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterVariableDeclarationPattern(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitVariableDeclarationPattern(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitVariableDeclarationPattern(this);
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
                if (!(_la == VAL || _la == DOT)) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
                setState(698);
                match(IDENTIFIER);
                setState(701);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(699);
                        match(COLON);
                        setState(700);
                        type(0);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(ParameterPatternContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(CocoParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(CocoParser.COMMA, i);
        }

        public ParameterPatternsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_parameterPatterns;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterParameterPatterns(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitParameterPatterns(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitParameterPatterns(this);
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
                while (_la == COMMA) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ParameterPatternContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode VAR() {
            return getToken(CocoParser.VAR, 0);
        }

        public ParameterPatternContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_parameterPattern;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterParameterPattern(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitParameterPattern(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitParameterPattern(this);
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
                if (_la == VAR) {
                    {
                        setState(711);
                        match(VAR);
                    }
                }

                setState(714);
                match(IDENTIFIER);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(ExpressionContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(CocoParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(CocoParser.COMMA, i);
        }

        public ExpressionsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expressions;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterExpressions(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitExpressions(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitExpressions(this);
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
                while (_la == COMMA) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class StatementContext extends ParserRuleContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public StateInvariantContext stateInvariant() {
            return getRuleContext(StateInvariantContext.class, 0);
        }

        public DeclarationStatementContext declarationStatement() {
            return getRuleContext(DeclarationStatementContext.class, 0);
        }

        public ReturnStatementContext returnStatement() {
            return getRuleContext(ReturnStatementContext.class, 0);
        }

        public BecomeStatementContext becomeStatement() {
            return getRuleContext(BecomeStatementContext.class, 0);
        }

        public WhileStatementContext whileStatement() {
            return getRuleContext(WhileStatementContext.class, 0);
        }

        public ForStatementContext forStatement() {
            return getRuleContext(ForStatementContext.class, 0);
        }

        public BreakStatementContext breakStatement() {
            return getRuleContext(BreakStatementContext.class, 0);
        }

        public ContinueStatementContext continueStatement() {
            return getRuleContext(ContinueStatementContext.class, 0);
        }

        public List<AttributeContext> attribute() {
            return getRuleContexts(AttributeContext.class);
        }

        public AttributeContext attribute(int i) {
            return getRuleContext(AttributeContext.class, i);
        }

        public TerminalNode SEMI() {
            return getToken(CocoParser.SEMI, 0);
        }

        public StatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_statement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitStatement(this);
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
                while (_la == AT) {
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
                switch (getInterpreter().adaptivePredict(_input, 82, _ctx)) {
                    case 1:
                        {
                            setState(730);
                            expression(0);
                            setState(732);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == SEMI) {
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
                            if (_la == SEMI) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class DeclarationStatementContext extends ParserRuleContext {
        public VariableDeclarationContext variableDeclaration() {
            return getRuleContext(VariableDeclarationContext.class, 0);
        }

        public TerminalNode SEMI() {
            return getToken(CocoParser.SEMI, 0);
        }

        public DeclarationStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_declarationStatement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterDeclarationStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitDeclarationStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitDeclarationStatement(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ReturnStatementContext extends ParserRuleContext {
        public TerminalNode RETURN() {
            return getToken(CocoParser.RETURN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode SEMI() {
            return getToken(CocoParser.SEMI, 0);
        }

        public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_returnStatement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterReturnStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitReturnStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitReturnStatement(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class BecomeStatementContext extends ParserRuleContext {
        public TerminalNode BECOME() {
            return getToken(CocoParser.BECOME, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode SEMI() {
            return getToken(CocoParser.SEMI, 0);
        }

        public BecomeStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_becomeStatement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterBecomeStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitBecomeStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitBecomeStatement(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class WhileStatementContext extends ParserRuleContext {
        public TerminalNode WHILE() {
            return getToken(CocoParser.WHILE, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public BlockExpression_Context blockExpression_() {
            return getRuleContext(BlockExpression_Context.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public WhileStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_whileStatement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterWhileStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitWhileStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitWhileStatement(this);
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
                if (_la == IDENTIFIER) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ForStatementContext extends ParserRuleContext {
        public TerminalNode FOR() {
            return getToken(CocoParser.FOR, 0);
        }

        public List<TerminalNode> IDENTIFIER() {
            return getTokens(CocoParser.IDENTIFIER);
        }

        public TerminalNode IDENTIFIER(int i) {
            return getToken(CocoParser.IDENTIFIER, i);
        }

        public TerminalNode IN() {
            return getToken(CocoParser.IN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public BlockExpression_Context blockExpression_() {
            return getRuleContext(BlockExpression_Context.class, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(CocoParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(CocoParser.COLON, i);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public ForStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_forStatement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterForStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitForStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitForStatement(this);
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
                if (_la == IDENTIFIER) {
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
                if (_la == COLON) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class BreakStatementContext extends ParserRuleContext {
        public TerminalNode BREAK() {
            return getToken(CocoParser.BREAK, 0);
        }

        public TerminalNode SEMI() {
            return getToken(CocoParser.SEMI, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public BreakStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_breakStatement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterBreakStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitBreakStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitBreakStatement(this);
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
                if (_la == IDENTIFIER) {
                    {
                        setState(783);
                        match(IDENTIFIER);
                    }
                }

                setState(786);
                match(SEMI);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ContinueStatementContext extends ParserRuleContext {
        public TerminalNode CONTINUE() {
            return getToken(CocoParser.CONTINUE, 0);
        }

        public TerminalNode SEMI() {
            return getToken(CocoParser.SEMI, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_continueStatement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterContinueStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitContinueStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitContinueStatement(this);
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
                if (_la == IDENTIFIER) {
                    {
                        setState(789);
                        match(IDENTIFIER);
                    }
                }

                setState(792);
                match(SEMI);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class PortElementContext extends ParserRuleContext {
        public EnumDeclarationContext enumDeclaration() {
            return getRuleContext(EnumDeclarationContext.class, 0);
        }

        public FunctionInterfaceDeclarationContext functionInterfaceDeclaration() {
            return getRuleContext(FunctionInterfaceDeclarationContext.class, 0);
        }

        public SignalDeclarationContext signalDeclaration() {
            return getRuleContext(SignalDeclarationContext.class, 0);
        }

        public FieldDeclarationContext fieldDeclaration() {
            return getRuleContext(FieldDeclarationContext.class, 0);
        }

        public StateMachineDeclarationContext stateMachineDeclaration() {
            return getRuleContext(StateMachineDeclarationContext.class, 0);
        }

        public PortDeclarationContext portDeclaration() {
            return getRuleContext(PortDeclarationContext.class, 0);
        }

        public StaticMemberDeclarationContext staticMemberDeclaration() {
            return getRuleContext(StaticMemberDeclarationContext.class, 0);
        }

        public StructDeclarationContext structDeclaration() {
            return getRuleContext(StructDeclarationContext.class, 0);
        }

        public TypeAliasDeclarationContext typeAliasDeclaration() {
            return getRuleContext(TypeAliasDeclarationContext.class, 0);
        }

        public ExternalTypeDeclarationContext externalTypeDeclaration() {
            return getRuleContext(ExternalTypeDeclarationContext.class, 0);
        }

        public List<AttributeContext> attribute() {
            return getRuleContexts(AttributeContext.class);
        }

        public AttributeContext attribute(int i) {
            return getRuleContext(AttributeContext.class, i);
        }

        public PortElementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_portElement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterPortElement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitPortElement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitPortElement(this);
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
                while (_la == AT) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class FunctionInterfaceDeclarationContext extends ParserRuleContext {
        public TerminalNode FUNCTION() {
            return getToken(CocoParser.FUNCTION, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public ParametersContext parameters() {
            return getRuleContext(ParametersContext.class, 0);
        }

        public FunctionInterfaceDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_functionInterfaceDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterFunctionInterfaceDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitFunctionInterfaceDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitFunctionInterfaceDeclaration(this);
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
                if (_la == VAR || _la == IDENTIFIER) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class SignalDeclarationContext extends ParserRuleContext {
        public TerminalNode OUTGOING() {
            return getToken(CocoParser.OUTGOING, 0);
        }

        public TerminalNode SIGNAL() {
            return getToken(CocoParser.SIGNAL, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public ParametersContext parameters() {
            return getRuleContext(ParametersContext.class, 0);
        }

        public SignalDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_signalDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterSignalDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitSignalDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitSignalDeclaration(this);
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
                if (_la == VAR || _la == IDENTIFIER) {
                    {
                        setState(826);
                        parameters();
                    }
                }

                setState(829);
                match(RP);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class StateMachineDeclarationContext extends ParserRuleContext {
        public TerminalNode MACHINE() {
            return getToken(CocoParser.MACHINE, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public List<TerminalNode> IDENTIFIER() {
            return getTokens(CocoParser.IDENTIFIER);
        }

        public TerminalNode IDENTIFIER(int i) {
            return getToken(CocoParser.IDENTIFIER, i);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public List<StateMachineElementContext> stateMachineElement() {
            return getRuleContexts(StateMachineElementContext.class);
        }

        public StateMachineElementContext stateMachineElement(int i) {
            return getRuleContext(StateMachineElementContext.class, i);
        }

        public StateMachineDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_stateMachineDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterStateMachineDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitStateMachineDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitStateMachineDeclaration(this);
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
                if (_la == IDENTIFIER) {
                    {
                        setState(832);
                        match(IDENTIFIER);
                    }
                }

                setState(837);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
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
                while ((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                        & ((1L << AFTER)
                                                | (1L << ASSERT)
                                                | (1L << ENUM)
                                                | (1L << ENTRY)
                                                | (1L << EXECUTION)
                                                | (1L << EXIT)
                                                | (1L << FUNCTION)
                                                | (1L << IF)
                                                | (1L << PERIODIC)
                                                | (1L << PRIVATE)
                                                | (1L << SPONTANEOUS)
                                                | (1L << STATE)
                                                | (1L << STATIC)
                                                | (1L << TYPE)
                                                | (1L << VAL)
                                                | (1L << VAR)
                                                | (1L << IDENTIFIER)
                                                | (1L << AT)))
                                != 0)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class StateMachineElementContext extends ParserRuleContext {
        public EnumDeclarationContext enumDeclaration() {
            return getRuleContext(EnumDeclarationContext.class, 0);
        }

        public EntryFunctionDeclarationContext entryFunctionDeclaration() {
            return getRuleContext(EntryFunctionDeclarationContext.class, 0);
        }

        public ExitFunctionDeclarationContext exitFunctionDeclaration() {
            return getRuleContext(ExitFunctionDeclarationContext.class, 0);
        }

        public FunctionDeclarationContext functionDeclaration() {
            return getRuleContext(FunctionDeclarationContext.class, 0);
        }

        public StateInvariantContext stateInvariant() {
            return getRuleContext(StateInvariantContext.class, 0);
        }

        public StateDeclarationContext stateDeclaration() {
            return getRuleContext(StateDeclarationContext.class, 0);
        }

        public StaticMemberDeclarationContext staticMemberDeclaration() {
            return getRuleContext(StaticMemberDeclarationContext.class, 0);
        }

        public TypeAliasDeclarationContext typeAliasDeclaration() {
            return getRuleContext(TypeAliasDeclarationContext.class, 0);
        }

        public VariableDeclarationContext variableDeclaration() {
            return getRuleContext(VariableDeclarationContext.class, 0);
        }

        public TransitionDeclarationContext transitionDeclaration() {
            return getRuleContext(TransitionDeclarationContext.class, 0);
        }

        public List<AttributeContext> attribute() {
            return getRuleContexts(AttributeContext.class);
        }

        public AttributeContext attribute(int i) {
            return getRuleContext(AttributeContext.class, i);
        }

        public StateMachineElementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_stateMachineElement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterStateMachineElement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitStateMachineElement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitStateMachineElement(this);
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
                while (_la == AT) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class StateDeclarationContext extends ParserRuleContext {
        public EventStateDeclarationContext eventStateDeclaration() {
            return getRuleContext(EventStateDeclarationContext.class, 0);
        }

        public ExecutionStateDeclarationContext executionStateDeclaration() {
            return getRuleContext(ExecutionStateDeclarationContext.class, 0);
        }

        public StateDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_stateDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterStateDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitStateDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitStateDeclaration(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class EventStateDeclarationContext extends ParserRuleContext {
        public TerminalNode STATE() {
            return getToken(CocoParser.STATE, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public List<EventStateElementContext> eventStateElement() {
            return getRuleContexts(EventStateElementContext.class);
        }

        public EventStateElementContext eventStateElement(int i) {
            return getRuleContext(EventStateElementContext.class, i);
        }

        public ParametersContext parameters() {
            return getRuleContext(ParametersContext.class, 0);
        }

        public EventStateDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_eventStateDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterEventStateDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitEventStateDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitEventStateDeclaration(this);
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
                if (_la == LP) {
                    {
                        setState(872);
                        match(LP);
                        setState(874);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == VAR || _la == IDENTIFIER) {
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
                while ((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                        & ((1L << AFTER)
                                                | (1L << ASSERT)
                                                | (1L << ENUM)
                                                | (1L << ENTRY)
                                                | (1L << EXECUTION)
                                                | (1L << EXIT)
                                                | (1L << FUNCTION)
                                                | (1L << IF)
                                                | (1L << PERIODIC)
                                                | (1L << PRIVATE)
                                                | (1L << SPONTANEOUS)
                                                | (1L << STATE)
                                                | (1L << STATIC)
                                                | (1L << STRUCT)
                                                | (1L << TYPE)
                                                | (1L << VAL)
                                                | (1L << VAR)
                                                | (1L << IDENTIFIER)
                                                | (1L << AT)))
                                != 0)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ExecutionStateDeclarationContext extends ParserRuleContext {
        public TerminalNode EXECUTION() {
            return getToken(CocoParser.EXECUTION, 0);
        }

        public TerminalNode STATE() {
            return getToken(CocoParser.STATE, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public BlockExpression_Context blockExpression_() {
            return getRuleContext(BlockExpression_Context.class, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public ParametersContext parameters() {
            return getRuleContext(ParametersContext.class, 0);
        }

        public ExecutionStateDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_executionStateDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterExecutionStateDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitExecutionStateDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitExecutionStateDeclaration(this);
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
                if (_la == LP) {
                    {
                        setState(891);
                        match(LP);
                        setState(893);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == VAR || _la == IDENTIFIER) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class EventStateElementContext extends ParserRuleContext {
        public EnumDeclarationContext enumDeclaration() {
            return getRuleContext(EnumDeclarationContext.class, 0);
        }

        public EntryFunctionDeclarationContext entryFunctionDeclaration() {
            return getRuleContext(EntryFunctionDeclarationContext.class, 0);
        }

        public ExitFunctionDeclarationContext exitFunctionDeclaration() {
            return getRuleContext(ExitFunctionDeclarationContext.class, 0);
        }

        public FunctionDeclarationContext functionDeclaration() {
            return getRuleContext(FunctionDeclarationContext.class, 0);
        }

        public StateDeclarationContext stateDeclaration() {
            return getRuleContext(StateDeclarationContext.class, 0);
        }

        public StateInvariantContext stateInvariant() {
            return getRuleContext(StateInvariantContext.class, 0);
        }

        public StaticMemberDeclarationContext staticMemberDeclaration() {
            return getRuleContext(StaticMemberDeclarationContext.class, 0);
        }

        public StructDeclarationContext structDeclaration() {
            return getRuleContext(StructDeclarationContext.class, 0);
        }

        public TransitionDeclarationContext transitionDeclaration() {
            return getRuleContext(TransitionDeclarationContext.class, 0);
        }

        public TypeAliasDeclarationContext typeAliasDeclaration() {
            return getRuleContext(TypeAliasDeclarationContext.class, 0);
        }

        public VariableDeclarationContext variableDeclaration() {
            return getRuleContext(VariableDeclarationContext.class, 0);
        }

        public List<AttributeContext> attribute() {
            return getRuleContexts(AttributeContext.class);
        }

        public AttributeContext attribute(int i) {
            return getRuleContext(AttributeContext.class, i);
        }

        public EventStateElementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_eventStateElement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterEventStateElement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitEventStateElement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitEventStateElement(this);
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
                while (_la == AT) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class EntryFunctionDeclarationContext extends ParserRuleContext {
        public TerminalNode ENTRY() {
            return getToken(CocoParser.ENTRY, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public EntryFunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_entryFunctionDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterEntryFunctionDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitEntryFunctionDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitEntryFunctionDeclaration(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ExitFunctionDeclarationContext extends ParserRuleContext {
        public TerminalNode EXIT() {
            return getToken(CocoParser.EXIT, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public ExitFunctionDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_exitFunctionDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterExitFunctionDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitExitFunctionDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitExitFunctionDeclaration(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class StateInvariantContext extends ParserRuleContext {
        public TerminalNode ASSERT() {
            return getToken(CocoParser.ASSERT, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public StateInvariantContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_stateInvariant;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterStateInvariant(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitStateInvariant(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitStateInvariant(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class TransitionDeclarationContext extends ParserRuleContext {
        public EventTransitionContext eventTransition() {
            return getRuleContext(EventTransitionContext.class, 0);
        }

        public SpontaneousTransitionContext spontaneousTransition() {
            return getRuleContext(SpontaneousTransitionContext.class, 0);
        }

        public TimerTransitionContext timerTransition() {
            return getRuleContext(TimerTransitionContext.class, 0);
        }

        public TransitionDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_transitionDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterTransitionDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitTransitionDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitTransitionDeclaration(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TransitionDeclarationContext transitionDeclaration() throws RecognitionException {
        TransitionDeclarationContext _localctx = new TransitionDeclarationContext(_ctx, getState());
        enterRule(_localctx, 136, RULE_transitionDeclaration);
        try {
            setState(939);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 105, _ctx)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class EventTransitionContext extends ParserRuleContext {
        public DotIdentifierListContext dotIdentifierList() {
            return getRuleContext(DotIdentifierListContext.class, 0);
        }

        public List<TerminalNode> LP() {
            return getTokens(CocoParser.LP);
        }

        public TerminalNode LP(int i) {
            return getToken(CocoParser.LP, i);
        }

        public List<TerminalNode> RP() {
            return getTokens(CocoParser.RP);
        }

        public TerminalNode RP(int i) {
            return getToken(CocoParser.RP, i);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public EventHandlerContext eventHandler() {
            return getRuleContext(EventHandlerContext.class, 0);
        }

        public TerminalNode IF() {
            return getToken(CocoParser.IF, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public List<EventSourceContext> eventSource() {
            return getRuleContexts(EventSourceContext.class);
        }

        public EventSourceContext eventSource(int i) {
            return getRuleContext(EventSourceContext.class, i);
        }

        public List<TerminalNode> DOT() {
            return getTokens(CocoParser.DOT);
        }

        public TerminalNode DOT(int i) {
            return getToken(CocoParser.DOT, i);
        }

        public ParametersContext parameters() {
            return getRuleContext(ParametersContext.class, 0);
        }

        public EventTransitionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_eventTransition;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterEventTransition(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitEventTransition(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitEventTransition(this);
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
                if (_la == IF) {
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
                _alt = getInterpreter().adaptivePredict(_input, 107, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
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
                    _alt = getInterpreter().adaptivePredict(_input, 107, _ctx);
                }
                setState(956);
                dotIdentifierList();
                setState(957);
                match(LP);
                setState(959);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == VAR || _la == IDENTIFIER) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class EventSourceContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode LB() {
            return getToken(CocoParser.LB, 0);
        }

        public PatternContext pattern() {
            return getRuleContext(PatternContext.class, 0);
        }

        public TerminalNode RB() {
            return getToken(CocoParser.RB, 0);
        }

        public TerminalNode PIPE() {
            return getToken(CocoParser.PIPE, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public EventSourceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_eventSource;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterEventSource(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitEventSource(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitEventSource(this);
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
                if (_la == LB) {
                    {
                        setState(966);
                        match(LB);
                        setState(967);
                        pattern();
                        setState(970);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == PIPE) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class SpontaneousTransitionContext extends ParserRuleContext {
        public TerminalNode SPONTANEOUS() {
            return getToken(CocoParser.SPONTANEOUS, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode IF() {
            return getToken(CocoParser.IF, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public SpontaneousTransitionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_spontaneousTransition;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterSpontaneousTransition(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitSpontaneousTransition(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitSpontaneousTransition(this);
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
                if (_la == IF) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class TimerTransitionContext extends ParserRuleContext {
        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(CocoParser.ASSIGN, 0);
        }

        public TerminalNode AFTER() {
            return getToken(CocoParser.AFTER, 0);
        }

        public TerminalNode PERIODIC() {
            return getToken(CocoParser.PERIODIC, 0);
        }

        public TimerTransitionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_timerTransition;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterTimerTransition(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitTimerTransition(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitTimerTransition(this);
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
                if (!(_la == AFTER || _la == PERIODIC)) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class EventHandlerContext extends ParserRuleContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode ILLEGAL() {
            return getToken(CocoParser.ILLEGAL, 0);
        }

        public OfferContext offer() {
            return getRuleContext(OfferContext.class, 0);
        }

        public EventHandlerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_eventHandler;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterEventHandler(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitEventHandler(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitEventHandler(this);
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class OfferContext extends ParserRuleContext {
        public TerminalNode OFFER() {
            return getToken(CocoParser.OFFER, 0);
        }

        public TerminalNode LC() {
            return getToken(CocoParser.LC, 0);
        }

        public OfferClausesContext offerClauses() {
            return getRuleContext(OfferClausesContext.class, 0);
        }

        public TerminalNode RC() {
            return getToken(CocoParser.RC, 0);
        }

        public TerminalNode OTHERWISE() {
            return getToken(CocoParser.OTHERWISE, 0);
        }

        public EventHandlerContext eventHandler() {
            return getRuleContext(EventHandlerContext.class, 0);
        }

        public TerminalNode COMMA() {
            return getToken(CocoParser.COMMA, 0);
        }

        public OfferContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_offer;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterOffer(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitOffer(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitOffer(this);
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
                if (_la == OTHERWISE) {
                    {
                        setState(1002);
                        match(OTHERWISE);
                        setState(1003);
                        eventHandler();
                        setState(1005);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COMMA) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(OfferClauseContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(CocoParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(CocoParser.COMMA, i);
        }

        public OfferClausesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_offerClauses;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterOfferClauses(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitOfferClauses(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitOfferClauses(this);
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
                _alt = getInterpreter().adaptivePredict(_input, 115, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
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
                    _alt = getInterpreter().adaptivePredict(_input, 115, _ctx);
                }
                setState(1020);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COMMA) {
                    {
                        setState(1019);
                        match(COMMA);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class OfferClauseContext extends ParserRuleContext {
        public EventHandlerContext eventHandler() {
            return getRuleContext(EventHandlerContext.class, 0);
        }

        public List<AttributeContext> attribute() {
            return getRuleContexts(AttributeContext.class);
        }

        public AttributeContext attribute(int i) {
            return getRuleContext(AttributeContext.class, i);
        }

        public TerminalNode IF() {
            return getToken(CocoParser.IF, 0);
        }

        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public OfferClauseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_offerClause;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterOfferClause(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitOfferClause(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitOfferClause(this);
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
                while (_la == AT) {
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
                switch (getInterpreter().adaptivePredict(_input, 118, _ctx)) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(ParameterContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(CocoParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(CocoParser.COMMA, i);
        }

        public ParametersContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_parameters;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterParameters(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitParameters(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitParameters(this);
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
                while (_la == COMMA) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ParameterContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TerminalNode VAR() {
            return getToken(CocoParser.VAR, 0);
        }

        public TerminalNode ELLIP() {
            return getToken(CocoParser.ELLIP, 0);
        }

        public TerminalNode COLON() {
            return getToken(CocoParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public GenericTypeDeclarationContext genericTypeDeclaration() {
            return getRuleContext(GenericTypeDeclarationContext.class, 0);
        }

        public ParameterContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_parameter;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterParameter(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitParameter(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitParameter(this);
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
            switch (getInterpreter().adaptivePredict(_input, 123, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(1046);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == VAR) {
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
                        if (_la == ELLIP) {
                            {
                                setState(1049);
                                match(ELLIP);
                            }
                        }

                        setState(1054);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COLON) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class LiteralExpression_Context extends ParserRuleContext {
        public TerminalNode INTEGER() {
            return getToken(CocoParser.INTEGER, 0);
        }

        public TerminalNode CHAR_LITERAL() {
            return getToken(CocoParser.CHAR_LITERAL, 0);
        }

        public TerminalNode STRING_LITERAL() {
            return getToken(CocoParser.STRING_LITERAL, 0);
        }

        public LiteralExpression_Context(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_literalExpression_;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterLiteralExpression_(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitLiteralExpression_(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitLiteralExpression_(this);
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
                if (!(((((_la - 87)) & ~0x3f) == 0
                        && ((1L << (_la - 87))
                                        & ((1L << (INTEGER - 87))
                                                | (1L << (CHAR_LITERAL - 87))
                                                | (1L << (STRING_LITERAL - 87))))
                                != 0))) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class TypeContext extends ParserRuleContext {
        public TypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_type;
        }

        public TypeContext() {}

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
            return getRuleContext(TypeContext.class, i);
        }

        public TerminalNode MUL() {
            return getToken(CocoParser.MUL, 0);
        }

        public TerminalNode DIV() {
            return getToken(CocoParser.DIV, 0);
        }

        public TerminalNode MOD() {
            return getToken(CocoParser.MOD, 0);
        }

        public TerminalNode PLUS() {
            return getToken(CocoParser.PLUS, 0);
        }

        public TerminalNode MINUS() {
            return getToken(CocoParser.MINUS, 0);
        }

        public BinaryTypeContext(TypeContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterBinaryType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitBinaryType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitBinaryType(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class GroupTypeContext extends TypeContext {
        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public GroupTypeContext(TypeContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterGroupType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitGroupType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitGroupType(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class FunctionTypeContext extends TypeContext {
        public TerminalNode LP() {
            return getToken(CocoParser.LP, 0);
        }

        public TypesContext types() {
            return getRuleContext(TypesContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(CocoParser.RP, 0);
        }

        public TerminalNode ARROW() {
            return getToken(CocoParser.ARROW, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public FunctionTypeContext(TypeContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterFunctionType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitFunctionType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitFunctionType(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class UnaryTypeContext extends TypeContext {
        public TerminalNode MINUS() {
            return getToken(CocoParser.MINUS, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public UnaryTypeContext(TypeContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterUnaryType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitUnaryType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitUnaryType(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class LiteralTypeContext extends TypeContext {
        public LiteralExpression_Context literalExpression_() {
            return getRuleContext(LiteralExpression_Context.class, 0);
        }

        public LiteralTypeContext(TypeContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterLiteralType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitLiteralType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitLiteralType(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class TypeReferenceContext extends TypeContext {
        public DotIdentifierListContext dotIdentifierList() {
            return getRuleContext(DotIdentifierListContext.class, 0);
        }

        public TerminalNode LT() {
            return getToken(CocoParser.LT, 0);
        }

        public TypesContext types() {
            return getRuleContext(TypesContext.class, 0);
        }

        public TerminalNode GT() {
            return getToken(CocoParser.GT, 0);
        }

        public TerminalNode DOT() {
            return getToken(CocoParser.DOT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(CocoParser.IDENTIFIER, 0);
        }

        public TypeReferenceContext(TypeContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterTypeReference(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitTypeReference(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitTypeReference(this);
            else return visitor.visitChildren(this);
        }
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class ReferenceTypeContext extends TypeContext {
        public TerminalNode AMP() {
            return getToken(CocoParser.AMP, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode MUT() {
            return getToken(CocoParser.MUT, 0);
        }

        public TerminalNode OUT() {
            return getToken(CocoParser.OUT, 0);
        }

        public ReferenceTypeContext(TypeContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterReferenceType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitReferenceType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitReferenceType(this);
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
                switch (getInterpreter().adaptivePredict(_input, 127, _ctx)) {
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
                            switch (getInterpreter().adaptivePredict(_input, 124, _ctx)) {
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
                            switch (getInterpreter().adaptivePredict(_input, 125, _ctx)) {
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
                            if (_la == MUT || _la == OUT) {
                                {
                                    setState(1086);
                                    _la = _input.LA(1);
                                    if (!(_la == MUT || _la == OUT)) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
                _alt = getInterpreter().adaptivePredict(_input, 129, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(1100);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 128, _ctx)) {
                                case 1:
                                    {
                                        _localctx = new BinaryTypeContext(new TypeContext(_parentctx, _parentState));
                                        pushNewRecursionContext(_localctx, _startState, RULE_type);
                                        setState(1094);
                                        if (!(precpred(_ctx, 8)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 8)");
                                        setState(1095);
                                        _la = _input.LA(1);
                                        if (!(((((_la - 65)) & ~0x3f) == 0
                                                && ((1L << (_la - 65))
                                                                & ((1L << (MUL - 65))
                                                                        | (1L << (DIV - 65))
                                                                        | (1L << (MOD - 65))))
                                                        != 0))) {
                                            _errHandler.recoverInline(this);
                                        } else {
                                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
                                        if (!(precpred(_ctx, 7)))
                                            throw new FailedPredicateException(this, "precpred(_ctx, 7)");
                                        setState(1098);
                                        _la = _input.LA(1);
                                        if (!(_la == MINUS || _la == PLUS)) {
                                            _errHandler.recoverInline(this);
                                        } else {
                                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
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
                    _alt = getInterpreter().adaptivePredict(_input, 129, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(TypeContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(CocoParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(CocoParser.COMMA, i);
        }

        public TypesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_types;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterTypes(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitTypes(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor) return ((CocoVisitor<? extends T>) visitor).visitTypes(this);
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
                while (_la == COMMA) {
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
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
    public static class DotIdentifierListContext extends ParserRuleContext {
        public List<TerminalNode> IDENTIFIER() {
            return getTokens(CocoParser.IDENTIFIER);
        }

        public TerminalNode IDENTIFIER(int i) {
            return getToken(CocoParser.IDENTIFIER, i);
        }

        public List<TerminalNode> DOT() {
            return getTokens(CocoParser.DOT);
        }

        public TerminalNode DOT(int i) {
            return getToken(CocoParser.DOT, i);
        }

        public DotIdentifierListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dotIdentifierList;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).enterDotIdentifierList(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CocoListener) ((CocoListener) listener).exitDotIdentifierList(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CocoVisitor)
                return ((CocoVisitor<? extends T>) visitor).visitDotIdentifierList(this);
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
                _alt = getInterpreter().adaptivePredict(_input, 131, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
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
                    _alt = getInterpreter().adaptivePredict(_input, 131, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 29:
                return expression_sempred((ExpressionContext) _localctx, predIndex);
            case 80:
                return type_sempred((TypeContext) _localctx, predIndex);
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

    public static final String _serializedATN = "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\\\u0464\4\2\t\2\4"
            + "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"
            + "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"
            + "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"
            + "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"
            + "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"
            + ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"
            + "\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="
            + "\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"
            + "\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"
            + "\3\2\7\2\u00aa\n\2\f\2\16\2\u00ad\13\2\3\2\3\2\3\3\7\3\u00b2\n\3\f\3\16"
            + "\3\u00b5\13\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3"
            + "\u00c4\n\3\3\4\3\4\3\4\3\4\3\4\3\4\5\4\u00cc\n\4\3\5\3\5\3\5\3\5\3\5\5"
            + "\5\u00d3\n\5\3\5\5\5\u00d6\n\5\3\6\3\6\5\6\u00da\n\6\3\6\3\6\3\6\5\6\u00df"
            + "\n\6\3\7\5\7\u00e2\n\7\3\7\3\7\3\7\5\7\u00e7\n\7\3\7\3\7\5\7\u00eb\n\7"
            + "\3\7\3\7\5\7\u00ef\n\7\3\b\3\b\3\b\5\b\u00f4\n\b\3\b\3\b\7\b\u00f8\n\b"
            + "\f\b\16\b\u00fb\13\b\3\b\3\b\3\t\3\t\3\t\5\t\u0102\n\t\3\t\3\t\7\t\u0106"
            + "\n\t\f\t\16\t\u0109\13\t\3\t\3\t\3\n\3\n\3\n\5\n\u0110\n\n\3\n\3\n\3\n"
            + "\3\13\3\13\3\13\5\13\u0118\n\13\3\13\3\13\5\13\u011c\n\13\3\13\3\13\3"
            + "\13\3\13\3\13\3\13\3\f\3\f\3\f\5\f\u0127\n\f\3\f\3\f\3\f\3\f\3\f\3\r\3"
            + "\r\3\r\3\r\5\r\u0132\n\r\3\r\5\r\u0135\n\r\3\r\3\r\7\r\u0139\n\r\f\r\16"
            + "\r\u013c\13\r\3\r\3\r\3\16\5\16\u0141\n\16\3\16\3\16\3\16\3\16\5\16\u0147"
            + "\n\16\3\16\3\16\7\16\u014b\n\16\f\16\16\16\u014e\13\16\3\16\3\16\3\17"
            + "\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\7\20\u015f"
            + "\n\20\f\20\16\20\u0162\13\20\3\20\5\20\u0165\n\20\3\21\3\21\3\21\5\21"
            + "\u016a\n\21\3\21\5\21\u016d\n\21\3\22\3\22\3\22\3\22\3\22\5\22\u0174\n"
            + "\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\5\23\u0180\n\23"
            + "\3\23\3\23\3\24\3\24\3\24\7\24\u0187\n\24\f\24\16\24\u018a\13\24\3\25"
            + "\5\25\u018d\n\25\3\25\3\25\3\25\5\25\u0192\n\25\3\26\3\26\5\26\u0196\n"
            + "\26\3\26\3\26\5\26\u019a\n\26\3\27\3\27\3\27\3\27\5\27\u01a0\n\27\3\27"
            + "\5\27\u01a3\n\27\3\27\3\27\5\27\u01a7\n\27\3\30\3\30\3\30\7\30\u01ac\n"
            + "\30\f\30\16\30\u01af\13\30\3\31\3\31\3\31\3\31\3\32\3\32\5\32\u01b7\n"
            + "\32\3\32\3\32\5\32\u01bb\n\32\3\32\5\32\u01be\n\32\3\33\5\33\u01c1\n\33"
            + "\3\33\3\33\3\33\3\33\3\33\5\33\u01c8\n\33\3\34\7\34\u01cb\n\34\f\34\16"
            + "\34\u01ce\13\34\3\34\3\34\3\34\3\34\3\34\5\34\u01d5\n\34\3\34\5\34\u01d8"
            + "\n\34\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\5\36\u01e5"
            + "\n\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"
            + "\3\37\5\37\u01f5\n\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"
            + "\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u020a\n\37\3\37\3\37"
            + "\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"
            + "\3\37\3\37\5\37\u021e\n\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"
            + "\3\37\3\37\3\37\3\37\3\37\3\37\3\37\7\37\u0230\n\37\f\37\16\37\u0233\13"
            + "\37\3 \3 \7 \u0237\n \f \16 \u023a\13 \3 \5 \u023d\n \3 \3 \3!\3!\3!\3"
            + "!\3!\3!\3!\5!\u0248\n!\3\"\3\"\5\"\u024c\n\"\3\"\3\"\3\"\3\"\3\"\3\"\3"
            + "\"\3\"\3#\3#\3#\3#\3#\5#\u025b\n#\3#\5#\u025e\n#\3#\3#\3$\3$\3$\7$\u0265"
            + "\n$\f$\16$\u0268\13$\3$\5$\u026b\n$\3%\3%\3%\3%\3&\3&\3&\7&\u0274\n&\f"
            + "&\16&\u0277\13&\3&\5&\u027a\n&\3\'\3\'\3\'\3\'\3\'\5\'\u0281\n\'\3\'\3"
            + "\'\3(\3(\3(\7(\u0288\n(\f(\16(\u028b\13(\3(\5(\u028e\n(\3)\3)\3)\3)\3"
            + "*\3*\3*\5*\u0297\n*\3+\3+\3+\3+\3+\3+\5+\u029f\n+\3,\3,\3,\7,\u02a4\n"
            + ",\f,\16,\u02a7\13,\3,\3,\3,\3,\7,\u02ad\n,\f,\16,\u02b0\13,\5,\u02b2\n"
            + ",\3-\3-\3-\5-\u02b7\n-\3-\5-\u02ba\n-\3.\3.\3.\3.\5.\u02c0\n.\3/\3/\3"
            + "/\7/\u02c5\n/\f/\16/\u02c8\13/\3\60\5\60\u02cb\n\60\3\60\3\60\3\61\3\61"
            + "\3\61\7\61\u02d2\n\61\f\61\16\61\u02d5\13\61\3\62\7\62\u02d8\n\62\f\62"
            + "\16\62\u02db\13\62\3\62\3\62\5\62\u02df\n\62\3\62\3\62\5\62\u02e3\n\62"
            + "\3\62\3\62\3\62\3\62\3\62\3\62\3\62\5\62\u02ec\n\62\3\63\3\63\3\63\3\64"
            + "\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\66\3\66\5\66\u02fb\n\66\3\66\3\66"
            + "\3\66\3\66\3\66\3\66\3\67\3\67\5\67\u0305\n\67\3\67\3\67\3\67\3\67\5\67"
            + "\u030b\n\67\3\67\3\67\3\67\3\67\38\38\58\u0313\n8\38\38\39\39\59\u0319"
            + "\n9\39\39\3:\7:\u031e\n:\f:\16:\u0321\13:\3:\3:\3:\3:\3:\3:\3:\3:\3:\3"
            + ":\5:\u032d\n:\3;\3;\3;\3;\5;\u0333\n;\3;\3;\3;\3;\3<\3<\3<\3<\3<\5<\u033e"
            + "\n<\3<\3<\3=\3=\5=\u0344\n=\3=\3=\5=\u0348\n=\3=\3=\7=\u034c\n=\f=\16"
            + "=\u034f\13=\3=\3=\3>\7>\u0354\n>\f>\16>\u0357\13>\3>\3>\3>\3>\3>\3>\3"
            + ">\3>\3>\3>\5>\u0363\n>\3?\3?\5?\u0367\n?\3@\3@\3@\3@\5@\u036d\n@\3@\5"
            + "@\u0370\n@\3@\3@\7@\u0374\n@\f@\16@\u0377\13@\3@\3@\3A\3A\3A\3A\3A\5A"
            + "\u0380\nA\3A\5A\u0383\nA\3A\3A\3B\7B\u0388\nB\fB\16B\u038b\13B\3B\3B\3"
            + "B\3B\3B\3B\3B\3B\3B\3B\3B\5B\u0398\nB\3C\3C\3C\3C\3C\3C\3D\3D\3D\3D\3"
            + "D\3D\3E\3E\3E\3E\3E\3F\3F\3F\5F\u03ae\nF\3G\3G\3G\3G\3G\5G\u03b5\nG\3"
            + "G\3G\3G\7G\u03ba\nG\fG\16G\u03bd\13G\3G\3G\3G\5G\u03c2\nG\3G\3G\3G\3G"
            + "\3H\3H\3H\3H\3H\5H\u03cd\nH\3H\3H\5H\u03d1\nH\3I\3I\3I\3I\3I\5I\u03d8"
            + "\nI\3I\3I\3I\3I\3J\3J\3J\3J\3J\3J\3J\3K\3K\3K\5K\u03e8\nK\3L\3L\3L\3L"
            + "\3L\3L\5L\u03f0\nL\5L\u03f2\nL\3L\3L\3M\3M\3M\7M\u03f9\nM\fM\16M\u03fc"
            + "\13M\3M\5M\u03ff\nM\3N\7N\u0402\nN\fN\16N\u0405\13N\3N\3N\3N\3N\3N\5N"
            + "\u040c\nN\3N\3N\3O\3O\3O\7O\u0413\nO\fO\16O\u0416\13O\3P\5P\u0419\nP\3"
            + "P\3P\5P\u041d\nP\3P\3P\5P\u0421\nP\3P\3P\5P\u0425\nP\3Q\3Q\3R\3R\3R\3"
            + "R\3R\3R\3R\3R\3R\3R\5R\u0433\nR\3R\3R\5R\u0437\nR\3R\3R\3R\3R\3R\3R\3"
            + "R\3R\3R\5R\u0442\nR\3R\3R\3R\5R\u0447\nR\3R\3R\3R\3R\3R\3R\7R\u044f\n"
            + "R\fR\16R\u0452\13R\3S\3S\3S\7S\u0457\nS\fS\16S\u045a\13S\3T\3T\3T\7T\u045f"
            + "\nT\fT\16T\u0462\13T\3T\2\4<\u00a2U\2\4\6\b\n\f\16\20\22\24\26\30\32\34"
            + "\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082"
            + "\u0084\u0086\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a"
            + "\u009c\u009e\u00a0\u00a2\u00a4\u00a6\2\r\3\2\60\61\3\2\35\36\5\2EEJJM"
            + "M\4\2CDFF\4\2EEGG\4\2ABOT\3\2>?\4\2\60\60@@\4\2\3\3%%\4\2YY[\\\4\2\35"
            + "\35##\2\u04e0\2\u00ab\3\2\2\2\4\u00b3\3\2\2\2\6\u00c5\3\2\2\2\b\u00cd"
            + "\3\2\2\2\n\u00d7\3\2\2\2\f\u00e1\3\2\2\2\16\u00f0\3\2\2\2\20\u00fe\3\2"
            + "\2\2\22\u010c\3\2\2\2\24\u0114\3\2\2\2\26\u0123\3\2\2\2\30\u012d\3\2\2"
            + "\2\32\u0140\3\2\2\2\34\u0151\3\2\2\2\36\u0159\3\2\2\2 \u016c\3\2\2\2\""
            + "\u016e\3\2\2\2$\u017b\3\2\2\2&\u0183\3\2\2\2(\u018c\3\2\2\2*\u0199\3\2"
            + "\2\2,\u019b\3\2\2\2.\u01a8\3\2\2\2\60\u01b0\3\2\2\2\62\u01ba\3\2\2\2\64"
            + "\u01c0\3\2\2\2\66\u01cc\3\2\2\28\u01d9\3\2\2\2:\u01e1\3\2\2\2<\u0209\3"
            + "\2\2\2>\u0234\3\2\2\2@\u0240\3\2\2\2B\u024b\3\2\2\2D\u0255\3\2\2\2F\u0261"
            + "\3\2\2\2H\u026c\3\2\2\2J\u0270\3\2\2\2L\u0280\3\2\2\2N\u0284\3\2\2\2P"
            + "\u028f\3\2\2\2R\u0296\3\2\2\2T\u0298\3\2\2\2V\u02b1\3\2\2\2X\u02b3\3\2"
            + "\2\2Z\u02bb\3\2\2\2\\\u02c1\3\2\2\2^\u02ca\3\2\2\2`\u02ce\3\2\2\2b\u02d9"
            + "\3\2\2\2d\u02ed\3\2\2\2f\u02f0\3\2\2\2h\u02f4\3\2\2\2j\u02fa\3\2\2\2l"
            + "\u0304\3\2\2\2n\u0310\3\2\2\2p\u0316\3\2\2\2r\u031f\3\2\2\2t\u032e\3\2"
            + "\2\2v\u0338\3\2\2\2x\u0341\3\2\2\2z\u0355\3\2\2\2|\u0366\3\2\2\2~\u0368"
            + "\3\2\2\2\u0080\u037a\3\2\2\2\u0082\u0389\3\2\2\2\u0084\u0399\3\2\2\2\u0086"
            + "\u039f\3\2\2\2\u0088\u03a5\3\2\2\2\u008a\u03ad\3\2\2\2\u008c\u03b4\3\2"
            + "\2\2\u008e\u03c7\3\2\2\2\u0090\u03d7\3\2\2\2\u0092\u03dd\3\2\2\2\u0094"
            + "\u03e7\3\2\2\2\u0096\u03e9\3\2\2\2\u0098\u03f5\3\2\2\2\u009a\u0403\3\2"
            + "\2\2\u009c\u040f\3\2\2\2\u009e\u0424\3\2\2\2\u00a0\u0426\3\2\2\2\u00a2"
            + "\u0446\3\2\2\2\u00a4\u0453\3\2\2\2\u00a6\u045b\3\2\2\2\u00a8\u00aa\5\4"
            + "\3\2\u00a9\u00a8\3\2\2\2\u00aa\u00ad\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab"
            + "\u00ac\3\2\2\2\u00ac\u00ae\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00af\7\2"
            + "\2\3\u00af\3\3\2\2\2\u00b0\u00b2\5\6\4\2\u00b1\u00b0\3\2\2\2\u00b2\u00b5"
            + "\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00c3\3\2\2\2\u00b5"
            + "\u00b3\3\2\2\2\u00b6\u00c4\5\n\6\2\u00b7\u00c4\5\f\7\2\u00b8\u00c4\5\16"
            + "\b\2\u00b9\u00c4\5\20\t\2\u00ba\u00c4\5\22\n\2\u00bb\u00c4\5\24\13\2\u00bc"
            + "\u00c4\5\26\f\2\u00bd\u00c4\5\30\r\2\u00be\u00c4\5\32\16\2\u00bf\u00c4"
            + "\5\34\17\2\u00c0\u00c4\5\"\22\2\u00c1\u00c4\5\36\20\2\u00c2\u00c4\5\b"
            + "\5\2\u00c3\u00b6\3\2\2\2\u00c3\u00b7\3\2\2\2\u00c3\u00b8\3\2\2\2\u00c3"
            + "\u00b9\3\2\2\2\u00c3\u00ba\3\2\2\2\u00c3\u00bb\3\2\2\2\u00c3\u00bc\3\2"
            + "\2\2\u00c3\u00bd\3\2\2\2\u00c3\u00be\3\2\2\2\u00c3\u00bf\3\2\2\2\u00c3"
            + "\u00c0\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c2\3\2\2\2\u00c4\5\3\2\2\2"
            + "\u00c5\u00c6\7\65\2\2\u00c6\u00cb\5\u00a6T\2\u00c7\u00c8\78\2\2\u00c8"
            + "\u00c9\5`\61\2\u00c9\u00ca\79\2\2\u00ca\u00cc\3\2\2\2\u00cb\u00c7\3\2"
            + "\2\2\u00cb\u00cc\3\2\2\2\u00cc\7\3\2\2\2\u00cd\u00ce\7\6\2\2\u00ce\u00cf"
            + "\7\65\2\2\u00cf\u00d5\5\u00a6T\2\u00d0\u00d2\78\2\2\u00d1\u00d3\5\u009c"
            + "O\2\u00d2\u00d1\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4"
            + "\u00d6\79\2\2\u00d5\u00d0\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\t\3\2\2\2"
            + "\u00d7\u00d9\7\27\2\2\u00d8\u00da\7/\2\2\u00d9\u00d8\3\2\2\2\u00d9\u00da"
            + "\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00de\5\u00a6T\2\u00dc\u00dd\7\4\2"
            + "\2\u00dd\u00df\7\64\2\2\u00de\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df"
            + "\13\3\2\2\2\u00e0\u00e2\7\'\2\2\u00e1\u00e0\3\2\2\2\u00e1\u00e2\3\2\2"
            + "\2\u00e2\u00e3\3\2\2\2\u00e3\u00e4\t\2\2\2\u00e4\u00e6\7\64\2\2\u00e5"
            + "\u00e7\5$\23\2\u00e6\u00e5\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00ea\3\2"
            + "\2\2\u00e8\u00e9\7\67\2\2\u00e9\u00eb\5\u00a2R\2\u00ea\u00e8\3\2\2\2\u00ea"
            + "\u00eb\3\2\2\2\u00eb\u00ee\3\2\2\2\u00ec\u00ed\7\66\2\2\u00ed\u00ef\5"
            + "<\37\2\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\r\3\2\2\2\u00f0\u00f1"
            + "\7\r\2\2\u00f1\u00f3\7\64\2\2\u00f2\u00f4\5$\23\2\u00f3\u00f2\3\2\2\2"
            + "\u00f3\u00f4\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5\u00f9\7:\2\2\u00f6\u00f8"
            + "\5*\26\2\u00f7\u00f6\3\2\2\2\u00f8\u00fb\3\2\2\2\u00f9\u00f7\3\2\2\2\u00f9"
            + "\u00fa\3\2\2\2\u00fa\u00fc\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fc\u00fd\7;"
            + "\2\2\u00fd\17\3\2\2\2\u00fe\u00ff\7-\2\2\u00ff\u0101\7\64\2\2\u0100\u0102"
            + "\5$\23\2\u0101\u0100\3\2\2\2\u0101\u0102\3\2\2\2\u0102\u0103\3\2\2\2\u0103"
            + "\u0107\7:\2\2\u0104\u0106\5\62\32\2\u0105\u0104\3\2\2\2\u0106\u0109\3"
            + "\2\2\2\u0107\u0105\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u010a\3\2\2\2\u0109"
            + "\u0107\3\2\2\2\u010a\u010b\7;\2\2\u010b\21\3\2\2\2\u010c\u010d\7.\2\2"
            + "\u010d\u010f\7\64\2\2\u010e\u0110\5$\23\2\u010f\u010e\3\2\2\2\u010f\u0110"
            + "\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0112\7\66\2\2\u0112\u0113\5\u00a2"
            + "R\2\u0113\23\3\2\2\2\u0114\u0115\7\24\2\2\u0115\u0117\7\64\2\2\u0116\u0118"
            + "\5$\23\2\u0117\u0116\3\2\2\2\u0117\u0118\3\2\2\2\u0118\u0119\3\2\2\2\u0119"
            + "\u011b\78\2\2\u011a\u011c\5\u009cO\2\u011b\u011a\3\2\2\2\u011b\u011c\3"
            + "\2\2\2\u011c\u011d\3\2\2\2\u011d\u011e\79\2\2\u011e\u011f\7\67\2\2\u011f"
            + "\u0120\5\u00a2R\2\u0120\u0121\7\66\2\2\u0121\u0122\5<\37\2\u0122\25\3"
            + "\2\2\2\u0123\u0124\7\32\2\2\u0124\u0126\7\64\2\2\u0125\u0127\5$\23\2\u0126"
            + "\u0125\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0129\78"
            + "\2\2\u0129\u012a\5\u009cO\2\u012a\u012b\79\2\2\u012b\u012c\5> \2\u012c"
            + "\27\3\2\2\2\u012d\u012e\7&\2\2\u012e\u0131\7\64\2\2\u012f\u0130\7\67\2"
            + "\2\u0130\u0132\5\u00a4S\2\u0131\u012f\3\2\2\2\u0131\u0132\3\2\2\2\u0132"
            + "\u0134\3\2\2\2\u0133\u0135\7\22\2\2\u0134\u0133\3\2\2\2\u0134\u0135\3"
            + "\2\2\2\u0135\u0136\3\2\2\2\u0136\u013a\7:\2\2\u0137\u0139\5r:\2\u0138"
            + "\u0137\3\2\2\2\u0139\u013c\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2"
            + "\2\2\u013b\u013d\3\2\2\2\u013c\u013a\3\2\2\2\u013d\u013e\7;\2\2\u013e"
            + "\31\3\2\2\2\u013f\u0141\7\21\2\2\u0140\u013f\3\2\2\2\u0140\u0141\3\2\2"
            + "\2\u0141\u0142\3\2\2\2\u0142\u0143\7\n\2\2\u0143\u0146\7\64\2\2\u0144"
            + "\u0145\7\67\2\2\u0145\u0147\5\u00a2R\2\u0146\u0144\3\2\2\2\u0146\u0147"
            + "\3\2\2\2\u0147\u0148\3\2\2\2\u0148\u014c\7:\2\2\u0149\u014b\5\66\34\2"
            + "\u014a\u0149\3\2\2\2\u014b\u014e\3\2\2\2\u014c\u014a\3\2\2\2\u014c\u014d"
            + "\3\2\2\2\u014d\u014f\3\2\2\2\u014e\u014c\3\2\2\2\u014f\u0150\7;\2\2\u0150"
            + "\33\3\2\2\2\u0151\u0152\7\21\2\2\u0152\u0153\7\60\2\2\u0153\u0154\7\64"
            + "\2\2\u0154\u0155\7\67\2\2\u0155\u0156\5\u00a2R\2\u0156\u0157\7\66\2\2"
            + "\u0157\u0158\5<\37\2\u0158\35\3\2\2\2\u0159\u015a\7\21\2\2\u015a\u015b"
            + "\7.\2\2\u015b\u0164\7\64\2\2\u015c\u0160\7:\2\2\u015d\u015f\5 \21\2\u015e"
            + "\u015d\3\2\2\2\u015f\u0162\3\2\2\2\u0160\u015e\3\2\2\2\u0160\u0161\3\2"
            + "\2\2\u0161\u0163\3\2\2\2\u0162\u0160\3\2\2\2\u0163\u0165\7;\2\2\u0164"
            + "\u015c\3\2\2\2\u0164\u0165\3\2\2\2\u0165\37\3\2\2\2\u0166\u016d\58\35"
            + "\2\u0167\u016d\5\f\7\2\u0168\u016a\t\3\2\2\u0169\u0168\3\2\2\2\u0169\u016a"
            + "\3\2\2\2\u016a\u016b\3\2\2\2\u016b\u016d\5\"\22\2\u016c\u0166\3\2\2\2"
            + "\u016c\u0167\3\2\2\2\u016c\u0169\3\2\2\2\u016d!\3\2\2\2\u016e\u016f\7"
            + "\21\2\2\u016f\u0170\7\24\2\2\u0170\u0171\7\64\2\2\u0171\u0173\78\2\2\u0172"
            + "\u0174\5\u009cO\2\u0173\u0172\3\2\2\2\u0173\u0174\3\2\2\2\u0174\u0175"
            + "\3\2\2\2\u0175\u0176\79\2\2\u0176\u0177\7\67\2\2\u0177\u0178\5\u00a2R"
            + "\2\u0178\u0179\7\66\2\2\u0179\u017a\5<\37\2\u017a#\3\2\2\2\u017b\u017c"
            + "\7A\2\2\u017c\u017f\5&\24\2\u017d\u017e\7\62\2\2\u017e\u0180\5`\61\2\u017f"
            + "\u017d\3\2\2\2\u017f\u0180\3\2\2\2\u0180\u0181\3\2\2\2\u0181\u0182\7B"
            + "\2\2\u0182%\3\2\2\2\u0183\u0188\5(\25\2\u0184\u0185\7>\2\2\u0185\u0187"
            + "\5(\25\2\u0186\u0184\3\2\2\2\u0187\u018a\3\2\2\2\u0188\u0186\3\2\2\2\u0188"
            + "\u0189\3\2\2\2\u0189\'\3\2\2\2\u018a\u0188\3\2\2\2\u018b\u018d\7\60\2"
            + "\2\u018c\u018b\3\2\2\2\u018c\u018d\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u0191"
            + "\7\64\2\2\u018f\u0190\7\67\2\2\u0190\u0192\5\u00a2R\2\u0191\u018f\3\2"
            + "\2\2\u0191\u0192\3\2\2\2\u0192)\3\2\2\2\u0193\u019a\5,\27\2\u0194\u0196"
            + "\t\3\2\2\u0195\u0194\3\2\2\2\u0195\u0196\3\2\2\2\u0196\u0197\3\2\2\2\u0197"
            + "\u019a\5\24\13\2\u0198\u019a\58\35\2\u0199\u0193\3\2\2\2\u0199\u0195\3"
            + "\2\2\2\u0199\u0198\3\2\2\2\u019a+\3\2\2\2\u019b\u019c\7\t\2\2\u019c\u01a2"
            + "\7\64\2\2\u019d\u019f\78\2\2\u019e\u01a0\5.\30\2\u019f\u019e\3\2\2\2\u019f"
            + "\u01a0\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1\u01a3\79\2\2\u01a2\u019d\3\2"
            + "\2\2\u01a2\u01a3\3\2\2\2\u01a3\u01a6\3\2\2\2\u01a4\u01a5\7\66\2\2\u01a5"
            + "\u01a7\5<\37\2\u01a6\u01a4\3\2\2\2\u01a6\u01a7\3\2\2\2\u01a7-\3\2\2\2"
            + "\u01a8\u01ad\5\60\31\2\u01a9\u01aa\7>\2\2\u01aa\u01ac\5\60\31\2\u01ab"
            + "\u01a9\3\2\2\2\u01ac\u01af\3\2\2\2\u01ad\u01ab\3\2\2\2\u01ad\u01ae\3\2"
            + "\2\2\u01ae/\3\2\2\2\u01af\u01ad\3\2\2\2\u01b0\u01b1\7\64\2\2\u01b1\u01b2"
            + "\7\67\2\2\u01b2\u01b3\5\u00a2R\2\u01b3\61\3\2\2\2\u01b4\u01bb\5\64\33"
            + "\2\u01b5\u01b7\t\3\2\2\u01b6\u01b5\3\2\2\2\u01b6\u01b7\3\2\2\2\u01b7\u01b8"
            + "\3\2\2\2\u01b8\u01bb\5\24\13\2\u01b9\u01bb\58\35\2\u01ba\u01b4\3\2\2\2"
            + "\u01ba\u01b6\3\2\2\2\u01ba\u01b9\3\2\2\2\u01bb\u01bd\3\2\2\2\u01bc\u01be"
            + "\7?\2\2\u01bd\u01bc\3\2\2\2\u01bd\u01be\3\2\2\2\u01be\63\3\2\2\2\u01bf"
            + "\u01c1\t\2\2\2\u01c0\u01bf\3\2\2\2\u01c0\u01c1\3\2\2\2\u01c1\u01c2\3\2"
            + "\2\2\u01c2\u01c3\7\64\2\2\u01c3\u01c4\7\67\2\2\u01c4\u01c7\5\u00a2R\2"
            + "\u01c5\u01c6\7\66\2\2\u01c6\u01c8\5<\37\2\u01c7\u01c5\3\2\2\2\u01c7\u01c8"
            + "\3\2\2\2\u01c8\65\3\2\2\2\u01c9\u01cb\5\6\4\2\u01ca\u01c9\3\2\2\2\u01cb"
            + "\u01ce\3\2\2\2\u01cc\u01ca\3\2\2\2\u01cc\u01cd\3\2\2\2\u01cd\u01d4\3\2"
            + "\2\2\u01ce\u01cc\3\2\2\2\u01cf\u01d5\5\64\33\2\u01d0\u01d5\5\f\7\2\u01d1"
            + "\u01d5\5:\36\2\u01d2\u01d5\5x=\2\u01d3\u01d5\58\35\2\u01d4\u01cf\3\2\2"
            + "\2\u01d4\u01d0\3\2\2\2\u01d4\u01d1\3\2\2\2\u01d4\u01d2\3\2\2\2\u01d4\u01d3"
            + "\3\2\2\2\u01d5\u01d7\3\2\2\2\u01d6\u01d8\7?\2\2\u01d7\u01d6\3\2\2\2\u01d7"
            + "\u01d8\3\2\2\2\u01d8\67\3\2\2\2\u01d9\u01da\7,\2\2\u01da\u01db\7\60\2"
            + "\2\u01db\u01dc\7\64\2\2\u01dc\u01dd\7\67\2\2\u01dd\u01de\5\u00a2R\2\u01de"
            + "\u01df\7\66\2\2\u01df\u01e0\5<\37\2\u01e09\3\2\2\2\u01e1\u01e2\7\31\2"
            + "\2\u01e2\u01e4\78\2\2\u01e3\u01e5\5\u009cO\2\u01e4\u01e3\3\2\2\2\u01e4"
            + "\u01e5\3\2\2\2\u01e5\u01e6\3\2\2\2\u01e6\u01e7\79\2\2\u01e7\u01e8\7\66"
            + "\2\2\u01e8\u01e9\5> \2\u01e9;\3\2\2\2\u01ea\u01eb\b\37\1\2\u01eb\u020a"
            + "\5\u00a0Q\2\u01ec\u01ed\7\21\2\2\u01ed\u020a\7Z\2\2\u01ee\u020a\5\"\22"
            + "\2\u01ef\u01f4\5\u00a6T\2\u01f0\u01f1\7A\2\2\u01f1\u01f2\5&\24\2\u01f2"
            + "\u01f3\7B\2\2\u01f3\u01f5\3\2\2\2\u01f4\u01f0\3\2\2\2\u01f4\u01f5\3\2"
            + "\2\2\u01f5\u020a\3\2\2\2\u01f6\u020a\5\u0088E\2\u01f7\u01f8\t\4\2\2\u01f8"
            + "\u020a\5<\37\20\u01f9\u020a\5@!\2\u01fa\u020a\5B\"\2\u01fb\u01fc\7@\2"
            + "\2\u01fc\u020a\7\64\2\2\u01fd\u01fe\78\2\2\u01fe\u01ff\5`\61\2\u01ff\u0200"
            + "\79\2\2\u0200\u020a\3\2\2\2\u0201\u0202\7<\2\2\u0202\u0203\5<\37\2\u0203"
            + "\u0204\7=\2\2\u0204\u020a\3\2\2\2\u0205\u020a\5D#\2\u0206\u0207\7!\2\2"
            + "\u0207\u020a\5<\37\4\u0208\u020a\5> \2\u0209\u01ea\3\2\2\2\u0209\u01ec"
            + "\3\2\2\2\u0209\u01ee\3\2\2\2\u0209\u01ef\3\2\2\2\u0209\u01f6\3\2\2\2\u0209"
            + "\u01f7\3\2\2\2\u0209\u01f9\3\2\2\2\u0209\u01fa\3\2\2\2\u0209\u01fb\3\2"
            + "\2\2\u0209\u01fd\3\2\2\2\u0209\u0201\3\2\2\2\u0209\u0205\3\2\2\2\u0209"
            + "\u0206\3\2\2\2\u0209\u0208\3\2\2\2\u020a\u0231\3\2\2\2\u020b\u020c\f\16"
            + "\2\2\u020c\u020d\t\5\2\2\u020d\u0230\5<\37\17\u020e\u020f\f\r\2\2\u020f"
            + "\u0210\t\6\2\2\u0210\u0230\5<\37\16\u0211\u0212\f\f\2\2\u0212\u0213\t"
            + "\7\2\2\u0213\u0230\5<\37\r\u0214\u0215\f\13\2\2\u0215\u0216\7\66\2\2\u0216"
            + "\u0230\5<\37\f\u0217\u0218\f\26\2\2\u0218\u0219\7@\2\2\u0219\u0230\7\64"
            + "\2\2\u021a\u021b\f\24\2\2\u021b\u021d\78\2\2\u021c\u021e\5`\61\2\u021d"
            + "\u021c\3\2\2\2\u021d\u021e\3\2\2\2\u021e\u021f\3\2\2\2\u021f\u0230\79"
            + "\2\2\u0220\u0221\f\23\2\2\u0221\u0222\7<\2\2\u0222\u0223\5<\37\2\u0223"
            + "\u0224\7=\2\2\u0224\u0230\3\2\2\2\u0225\u0226\f\22\2\2\u0226\u0227\7:"
            + "\2\2\u0227\u0228\5F$\2\u0228\u0229\7;\2\2\u0229\u0230\3\2\2\2\u022a\u022b"
            + "\f\21\2\2\u022b\u0230\7K\2\2\u022c\u022d\f\17\2\2\u022d\u022e\7\4\2\2"
            + "\u022e\u0230\5\u00a2R\2\u022f\u020b\3\2\2\2\u022f\u020e\3\2\2\2\u022f"
            + "\u0211\3\2\2\2\u022f\u0214\3\2\2\2\u022f\u0217\3\2\2\2\u022f\u021a\3\2"
            + "\2\2\u022f\u0220\3\2\2\2\u022f\u0225\3\2\2\2\u022f\u022a\3\2\2\2\u022f"
            + "\u022c\3\2\2\2\u0230\u0233\3\2\2\2\u0231\u022f\3\2\2\2\u0231\u0232\3\2"
            + "\2\2\u0232=\3\2\2\2\u0233\u0231\3\2\2\2\u0234\u0238\7:\2\2\u0235\u0237"
            + "\5b\62\2\u0236\u0235\3\2\2\2\u0237\u023a\3\2\2\2\u0238\u0236\3\2\2\2\u0238"
            + "\u0239\3\2\2\2\u0239\u023c\3\2\2\2\u023a\u0238\3\2\2\2\u023b\u023d\5<"
            + "\37\2\u023c\u023b\3\2\2\2\u023c\u023d\3\2\2\2\u023d\u023e\3\2\2\2\u023e"
            + "\u023f\7;\2\2\u023f?\3\2\2\2\u0240\u0241\7\25\2\2\u0241\u0242\78\2\2\u0242"
            + "\u0243\5<\37\2\u0243\u0244\79\2\2\u0244\u0247\5<\37\2\u0245\u0246\7\f"
            + "\2\2\u0246\u0248\5<\37\2\u0247\u0245\3\2\2\2\u0247\u0248\3\2\2\2\u0248"
            + "A\3\2\2\2\u0249\u024a\7\64\2\2\u024a\u024c\7\67\2\2\u024b\u0249\3\2\2"
            + "\2\u024b\u024c\3\2\2\2\u024c\u024d\3\2\2\2\u024d\u024e\7\34\2\2\u024e"
            + "\u024f\78\2\2\u024f\u0250\5<\37\2\u0250\u0251\79\2\2\u0251\u0252\7:\2"
            + "\2\u0252\u0253\5N(\2\u0253\u0254\7;\2\2\u0254C\3\2\2\2\u0255\u0256\7\37"
            + "\2\2\u0256\u0257\7:\2\2\u0257\u025a\5J&\2\u0258\u0259\7\"\2\2\u0259\u025b"
            + "\5<\37\2\u025a\u0258\3\2\2\2\u025a\u025b\3\2\2\2\u025b\u025d\3\2\2\2\u025c"
            + "\u025e\t\b\2\2\u025d\u025c\3\2\2\2\u025d\u025e\3\2\2\2\u025e\u025f\3\2"
            + "\2\2\u025f\u0260\7;\2\2\u0260E\3\2\2\2\u0261\u0266\5H%\2\u0262\u0263\7"
            + ">\2\2\u0263\u0265\5H%\2\u0264\u0262\3\2\2\2\u0265\u0268\3\2\2\2\u0266"
            + "\u0264\3\2\2\2\u0266\u0267\3\2\2\2\u0267\u026a\3\2\2\2\u0268\u0266\3\2"
            + "\2\2\u0269\u026b\7>\2\2\u026a\u0269\3\2\2\2\u026a\u026b\3\2\2\2\u026b"
            + "G\3\2\2\2\u026c\u026d\7\64\2\2\u026d\u026e\7\66\2\2\u026e\u026f\5<\37"
            + "\2\u026fI\3\2\2\2\u0270\u0275\5L\'\2\u0271\u0272\t\b\2\2\u0272\u0274\5"
            + "L\'\2\u0273\u0271\3\2\2\2\u0274\u0277\3\2\2\2\u0275\u0273\3\2\2\2\u0275"
            + "\u0276\3\2\2\2\u0276\u0279\3\2\2\2\u0277\u0275\3\2\2\2\u0278\u027a\t\b"
            + "\2\2\u0279\u0278\3\2\2\2\u0279\u027a\3\2\2\2\u027aK\3\2\2\2\u027b\u027c"
            + "\7\25\2\2\u027c\u027d\78\2\2\u027d\u027e\5<\37\2\u027e\u027f\79\2\2\u027f"
            + "\u0281\3\2\2\2\u0280\u027b\3\2\2\2\u0280\u0281\3\2\2\2\u0281\u0282\3\2"
            + "\2\2\u0282\u0283\5<\37\2\u0283M\3\2\2\2\u0284\u0289\5P)\2\u0285\u0286"
            + "\t\b\2\2\u0286\u0288\5P)\2\u0287\u0285\3\2\2\2\u0288\u028b\3\2\2\2\u0289"
            + "\u0287\3\2\2\2\u0289\u028a\3\2\2\2\u028a\u028d\3\2\2\2\u028b\u0289\3\2"
            + "\2\2\u028c\u028e\t\b\2\2\u028d\u028c\3\2\2\2\u028d\u028e\3\2\2\2\u028e"
            + "O\3\2\2\2\u028f\u0290\5R*\2\u0290\u0291\7H\2\2\u0291\u0292\5<\37\2\u0292"
            + "Q\3\2\2\2\u0293\u0297\5T+\2\u0294\u0297\5\u00a0Q\2\u0295\u0297\5Z.\2\u0296"
            + "\u0293\3\2\2\2\u0296\u0294\3\2\2\2\u0296\u0295\3\2\2\2\u0297S\3\2\2\2"
            + "\u0298\u029e\5V,\2\u0299\u029a\7\25\2\2\u029a\u029b\78\2\2\u029b\u029c"
            + "\5<\37\2\u029c\u029d\79\2\2\u029d\u029f\3\2\2\2\u029e\u0299\3\2\2\2\u029e"
            + "\u029f\3\2\2\2\u029fU\3\2\2\2\u02a0\u02a5\5X-\2\u02a1\u02a2\7@\2\2\u02a2"
            + "\u02a4\5X-\2\u02a3\u02a1\3\2\2\2\u02a4\u02a7\3\2\2\2\u02a5\u02a3\3\2\2"
            + "\2\u02a5\u02a6\3\2\2\2\u02a6\u02b2\3\2\2\2\u02a7\u02a5\3\2\2\2\u02a8\u02a9"
            + "\7@\2\2\u02a9\u02ae\5X-\2\u02aa\u02ab\7@\2\2\u02ab\u02ad\5X-\2\u02ac\u02aa"
            + "\3\2\2\2\u02ad\u02b0\3\2\2\2\u02ae\u02ac\3\2\2\2\u02ae\u02af\3\2\2\2\u02af"
            + "\u02b2\3\2\2\2\u02b0\u02ae\3\2\2\2\u02b1\u02a0\3\2\2\2\u02b1\u02a8\3\2"
            + "\2\2\u02b2W\3\2\2\2\u02b3\u02b9\7\64\2\2\u02b4\u02b6\78\2\2\u02b5\u02b7"
            + "\5\\/\2\u02b6\u02b5\3\2\2\2\u02b6\u02b7\3\2\2\2\u02b7\u02b8\3\2\2\2\u02b8"
            + "\u02ba\79\2\2\u02b9\u02b4\3\2\2\2\u02b9\u02ba\3\2\2\2\u02baY\3\2\2\2\u02bb"
            + "\u02bc\t\t\2\2\u02bc\u02bf\7\64\2\2\u02bd\u02be\7\67\2\2\u02be\u02c0\5"
            + "\u00a2R\2\u02bf\u02bd\3\2\2\2\u02bf\u02c0\3\2\2\2\u02c0[\3\2\2\2\u02c1"
            + "\u02c6\5^\60\2\u02c2\u02c3\7>\2\2\u02c3\u02c5\5^\60\2\u02c4\u02c2\3\2"
            + "\2\2\u02c5\u02c8\3\2\2\2\u02c6\u02c4\3\2\2\2\u02c6\u02c7\3\2\2\2\u02c7"
            + "]\3\2\2\2\u02c8\u02c6\3\2\2\2\u02c9\u02cb\7\61\2\2\u02ca\u02c9\3\2\2\2"
            + "\u02ca\u02cb\3\2\2\2\u02cb\u02cc\3\2\2\2\u02cc\u02cd\7\64\2\2\u02cd_\3"
            + "\2\2\2\u02ce\u02d3\5<\37\2\u02cf\u02d0\7>\2\2\u02d0\u02d2\5<\37\2\u02d1"
            + "\u02cf\3\2\2\2\u02d2\u02d5\3\2\2\2\u02d3\u02d1\3\2\2\2\u02d3\u02d4\3\2"
            + "\2\2\u02d4a\3\2\2\2\u02d5\u02d3\3\2\2\2\u02d6\u02d8\5\6\4\2\u02d7\u02d6"
            + "\3\2\2\2\u02d8\u02db\3\2\2\2\u02d9\u02d7\3\2\2\2\u02d9\u02da\3\2\2\2\u02da"
            + "\u02eb\3\2\2\2\u02db\u02d9\3\2\2\2\u02dc\u02de\5<\37\2\u02dd\u02df\7?"
            + "\2\2\u02de\u02dd\3\2\2\2\u02de\u02df\3\2\2\2\u02df\u02ec\3\2\2\2\u02e0"
            + "\u02e2\5\u0088E\2\u02e1\u02e3\7?\2\2\u02e2\u02e1\3\2\2\2\u02e2\u02e3\3"
            + "\2\2\2\u02e3\u02ec\3\2\2\2\u02e4\u02ec\5d\63\2\u02e5\u02ec\5f\64\2\u02e6"
            + "\u02ec\5h\65\2\u02e7\u02ec\5j\66\2\u02e8\u02ec\5l\67\2\u02e9\u02ec\5n"
            + "8\2\u02ea\u02ec\5p9\2\u02eb\u02dc\3\2\2\2\u02eb\u02e0\3\2\2\2\u02eb\u02e4"
            + "\3\2\2\2\u02eb\u02e5\3\2\2\2\u02eb\u02e6\3\2\2\2\u02eb\u02e7\3\2\2\2\u02eb"
            + "\u02e8\3\2\2\2\u02eb\u02e9\3\2\2\2\u02eb\u02ea\3\2\2\2\u02ecc\3\2\2\2"
            + "\u02ed\u02ee\5\f\7\2\u02ee\u02ef\7?\2\2\u02efe\3\2\2\2\u02f0\u02f1\7("
            + "\2\2\u02f1\u02f2\5<\37\2\u02f2\u02f3\7?\2\2\u02f3g\3\2\2\2\u02f4\u02f5"
            + "\7\7\2\2\u02f5\u02f6\5<\37\2\u02f6\u02f7\7?\2\2\u02f7i\3\2\2\2\u02f8\u02f9"
            + "\7\64\2\2\u02f9\u02fb\7\67\2\2\u02fa\u02f8\3\2\2\2\u02fa\u02fb\3\2\2\2"
            + "\u02fb\u02fc\3\2\2\2\u02fc\u02fd\7\63\2\2\u02fd\u02fe\78\2\2\u02fe\u02ff"
            + "\5<\37\2\u02ff\u0300\79\2\2\u0300\u0301\5> \2\u0301k\3\2\2\2\u0302\u0303"
            + "\7\64\2\2\u0303\u0305\7\67\2\2\u0304\u0302\3\2\2\2\u0304\u0305\3\2\2\2"
            + "\u0305\u0306\3\2\2\2\u0306\u0307\7\23\2\2\u0307\u030a\7\64\2\2\u0308\u0309"
            + "\7\67\2\2\u0309\u030b\5\u00a2R\2\u030a\u0308\3\2\2\2\u030a\u030b\3\2\2"
            + "\2\u030b\u030c\3\2\2\2\u030c\u030d\7\30\2\2\u030d\u030e\5<\37\2\u030e"
            + "\u030f\5> \2\u030fm\3\2\2\2\u0310\u0312\7\b\2\2\u0311\u0313\7\64\2\2\u0312"
            + "\u0311\3\2\2\2\u0312\u0313\3\2\2\2\u0313\u0314\3\2\2\2\u0314\u0315\7?"
            + "\2\2\u0315o\3\2\2\2\u0316\u0318\7\13\2\2\u0317\u0319\7\64\2\2\u0318\u0317"
            + "\3\2\2\2\u0318\u0319\3\2\2\2\u0319\u031a\3\2\2\2\u031a\u031b\7?\2\2\u031b"
            + "q\3\2\2\2\u031c\u031e\5\6\4\2\u031d\u031c\3\2\2\2\u031e\u0321\3\2\2\2"
            + "\u031f\u031d\3\2\2\2\u031f\u0320\3\2\2\2\u0320\u032c\3\2\2\2\u0321\u031f"
            + "\3\2\2\2\u0322\u032d\5\16\b\2\u0323\u032d\5t;\2\u0324\u032d\5v<\2\u0325"
            + "\u032d\5\64\33\2\u0326\u032d\5x=\2\u0327\u032d\5\30\r\2\u0328\u032d\5"
            + "8\35\2\u0329\u032d\5\20\t\2\u032a\u032d\5\22\n\2\u032b\u032d\5\36\20\2"
            + "\u032c\u0322\3\2\2\2\u032c\u0323\3\2\2\2\u032c\u0324\3\2\2\2\u032c\u0325"
            + "\3\2\2\2\u032c\u0326\3\2\2\2\u032c\u0327\3\2\2\2\u032c\u0328\3\2\2\2\u032c"
            + "\u0329\3\2\2\2\u032c\u032a\3\2\2\2\u032c\u032b\3\2\2\2\u032ds\3\2\2\2"
            + "\u032e\u032f\7\24\2\2\u032f\u0330\7\64\2\2\u0330\u0332\78\2\2\u0331\u0333"
            + "\5\u009cO\2\u0332\u0331\3\2\2\2\u0332\u0333\3\2\2\2\u0333\u0334\3\2\2"
            + "\2\u0334\u0335\79\2\2\u0335\u0336\7\67\2\2\u0336\u0337\5\u00a2R\2\u0337"
            + "u\3\2\2\2\u0338\u0339\7$\2\2\u0339\u033a\7)\2\2\u033a\u033b\7\64\2\2\u033b"
            + "\u033d\78\2\2\u033c\u033e\5\u009cO\2\u033d\u033c\3\2\2\2\u033d\u033e\3"
            + "\2\2\2\u033e\u033f\3\2\2\2\u033f\u0340\79\2\2\u0340w\3\2\2\2\u0341\u0343"
            + "\7\33\2\2\u0342\u0344\7\64\2\2\u0343\u0342\3\2\2\2\u0343\u0344\3\2\2\2"
            + "\u0344\u0347\3\2\2\2\u0345\u0346\7\67\2\2\u0346\u0348\7\64\2\2\u0347\u0345"
            + "\3\2\2\2\u0347\u0348\3\2\2\2\u0348\u0349\3\2\2\2\u0349\u034d\7:\2\2\u034a"
            + "\u034c\5z>\2\u034b\u034a\3\2\2\2\u034c\u034f\3\2\2\2\u034d\u034b\3\2\2"
            + "\2\u034d\u034e\3\2\2\2\u034e\u0350\3\2\2\2\u034f\u034d\3\2\2\2\u0350\u0351"
            + "\7;\2\2\u0351y\3\2\2\2\u0352\u0354\5\6\4\2\u0353\u0352\3\2\2\2\u0354\u0357"
            + "\3\2\2\2\u0355\u0353\3\2\2\2\u0355\u0356\3\2\2\2\u0356\u0362\3\2\2\2\u0357"
            + "\u0355\3\2\2\2\u0358\u0363\5\16\b\2\u0359\u0363\5\u0084C\2\u035a\u0363"
            + "\5\u0086D\2\u035b\u0363\5\24\13\2\u035c\u0363\5\u0088E\2\u035d\u0363\5"
            + "|?\2\u035e\u0363\58\35\2\u035f\u0363\5\22\n\2\u0360\u0363\5\f\7\2\u0361"
            + "\u0363\5\u008aF\2\u0362\u0358\3\2\2\2\u0362\u0359\3\2\2\2\u0362\u035a"
            + "\3\2\2\2\u0362\u035b\3\2\2\2\u0362\u035c\3\2\2\2\u0362\u035d\3\2\2\2\u0362"
            + "\u035e\3\2\2\2\u0362\u035f\3\2\2\2\u0362\u0360\3\2\2\2\u0362\u0361\3\2"
            + "\2\2\u0363{\3\2\2\2\u0364\u0367\5~@\2\u0365\u0367\5\u0080A\2\u0366\u0364"
            + "\3\2\2\2\u0366\u0365\3\2\2\2\u0367}\3\2\2\2\u0368\u0369\7+\2\2\u0369\u036f"
            + "\7\64\2\2\u036a\u036c\78\2\2\u036b\u036d\5\u009cO\2\u036c\u036b\3\2\2"
            + "\2\u036c\u036d\3\2\2\2\u036d\u036e\3\2\2\2\u036e\u0370\79\2\2\u036f\u036a"
            + "\3\2\2\2\u036f\u0370\3\2\2\2\u0370\u0371\3\2\2\2\u0371\u0375\7:\2\2\u0372"
            + "\u0374\5\u0082B\2\u0373\u0372\3\2\2\2\u0374\u0377\3\2\2\2\u0375\u0373"
            + "\3\2\2\2\u0375\u0376\3\2\2\2\u0376\u0378\3\2\2\2\u0377\u0375\3\2\2\2\u0378"
            + "\u0379\7;\2\2\u0379\177\3\2\2\2\u037a\u037b\7\17\2\2\u037b\u037c\7+\2"
            + "\2\u037c\u0382\7\64\2\2\u037d\u037f\78\2\2\u037e\u0380\5\u009cO\2\u037f"
            + "\u037e\3\2\2\2\u037f\u0380\3\2\2\2\u0380\u0381\3\2\2\2\u0381\u0383\79"
            + "\2\2\u0382\u037d\3\2\2\2\u0382\u0383\3\2\2\2\u0383\u0384\3\2\2\2\u0384"
            + "\u0385\5> \2\u0385\u0081\3\2\2\2\u0386\u0388\5\6\4\2\u0387\u0386\3\2\2"
            + "\2\u0388\u038b\3\2\2\2\u0389\u0387\3\2\2\2\u0389\u038a\3\2\2\2\u038a\u0397"
            + "\3\2\2\2\u038b\u0389\3\2\2\2\u038c\u0398\5\16\b\2\u038d\u0398\5\u0084"
            + "C\2\u038e\u0398\5\u0086D\2\u038f\u0398\5\24\13\2\u0390\u0398\5|?\2\u0391"
            + "\u0398\5\u0088E\2\u0392\u0398\58\35\2\u0393\u0398\5\20\t\2\u0394\u0398"
            + "\5\u008aF\2\u0395\u0398\5\22\n\2\u0396\u0398\5\f\7\2\u0397\u038c\3\2\2"
            + "\2\u0397\u038d\3\2\2\2\u0397\u038e\3\2\2\2\u0397\u038f\3\2\2\2\u0397\u0390"
            + "\3\2\2\2\u0397\u0391\3\2\2\2\u0397\u0392\3\2\2\2\u0397\u0393\3\2\2\2\u0397"
            + "\u0394\3\2\2\2\u0397\u0395\3\2\2\2\u0397\u0396\3\2\2\2\u0398\u0083\3\2"
            + "\2\2\u0399\u039a\7\16\2\2\u039a\u039b\78\2\2\u039b\u039c\79\2\2\u039c"
            + "\u039d\7\66\2\2\u039d\u039e\5<\37\2\u039e\u0085\3\2\2\2\u039f\u03a0\7"
            + "\20\2\2\u03a0\u03a1\78\2\2\u03a1\u03a2\79\2\2\u03a2\u03a3\7\66\2\2\u03a3"
            + "\u03a4\5<\37\2\u03a4\u0087\3\2\2\2\u03a5\u03a6\7\5\2\2\u03a6\u03a7\78"
            + "\2\2\u03a7\u03a8\5<\37\2\u03a8\u03a9\79\2\2\u03a9\u0089\3\2\2\2\u03aa"
            + "\u03ae\5\u008cG\2\u03ab\u03ae\5\u0090I\2\u03ac\u03ae\5\u0092J\2\u03ad"
            + "\u03aa\3\2\2\2\u03ad\u03ab\3\2\2\2\u03ad\u03ac\3\2\2\2\u03ae\u008b\3\2"
            + "\2\2\u03af\u03b0\7\25\2\2\u03b0\u03b1\78\2\2\u03b1\u03b2\5<\37\2\u03b2"
            + "\u03b3\79\2\2\u03b3\u03b5\3\2\2\2\u03b4\u03af\3\2\2\2\u03b4\u03b5\3\2"
            + "\2\2\u03b5\u03bb\3\2\2\2\u03b6\u03b7\5\u008eH\2\u03b7\u03b8\7@\2\2\u03b8"
            + "\u03ba\3\2\2\2\u03b9\u03b6\3\2\2\2\u03ba\u03bd\3\2\2\2\u03bb\u03b9\3\2"
            + "\2\2\u03bb\u03bc\3\2\2\2\u03bc\u03be\3\2\2\2\u03bd\u03bb\3\2\2\2\u03be"
            + "\u03bf\5\u00a6T\2\u03bf\u03c1\78\2\2\u03c0\u03c2\5\u009cO\2\u03c1\u03c0"
            + "\3\2\2\2\u03c1\u03c2\3\2\2\2\u03c2\u03c3\3\2\2\2\u03c3\u03c4\79\2\2\u03c4"
            + "\u03c5\7\66\2\2\u03c5\u03c6\5\u0094K\2\u03c6\u008d\3\2\2\2\u03c7\u03d0"
            + "\7\64\2\2\u03c8\u03c9\7<\2\2\u03c9\u03cc\5R*\2\u03ca\u03cb\7L\2\2\u03cb"
            + "\u03cd\5<\37\2\u03cc\u03ca\3\2\2\2\u03cc\u03cd\3\2\2\2\u03cd\u03ce\3\2"
            + "\2\2\u03ce\u03cf\7=\2\2\u03cf\u03d1\3\2\2\2\u03d0\u03c8\3\2\2\2\u03d0"
            + "\u03d1\3\2\2\2\u03d1\u008f\3\2\2\2\u03d2\u03d3\7\25\2\2\u03d3\u03d4\7"
            + "8\2\2\u03d4\u03d5\5<\37\2\u03d5\u03d6\79\2\2\u03d6\u03d8\3\2\2\2\u03d7"
            + "\u03d2\3\2\2\2\u03d7\u03d8\3\2\2\2\u03d8\u03d9\3\2\2\2\u03d9\u03da\7*"
            + "\2\2\u03da\u03db\7\66\2\2\u03db\u03dc\5<\37\2\u03dc\u0091\3\2\2\2\u03dd"
            + "\u03de\t\n\2\2\u03de\u03df\78\2\2\u03df\u03e0\5<\37\2\u03e0\u03e1\79\2"
            + "\2\u03e1\u03e2\7\66\2\2\u03e2\u03e3\5<\37\2\u03e3\u0093\3\2\2\2\u03e4"
            + "\u03e8\5<\37\2\u03e5\u03e8\7\26\2\2\u03e6\u03e8\5\u0096L\2\u03e7\u03e4"
            + "\3\2\2\2\u03e7\u03e5\3\2\2\2\u03e7\u03e6\3\2\2\2\u03e8\u0095\3\2\2\2\u03e9"
            + "\u03ea\7 \2\2\u03ea\u03eb\7:\2\2\u03eb\u03f1\5\u0098M\2\u03ec\u03ed\7"
            + "\"\2\2\u03ed\u03ef\5\u0094K\2\u03ee\u03f0\7>\2\2\u03ef\u03ee\3\2\2\2\u03ef"
            + "\u03f0\3\2\2\2\u03f0\u03f2\3\2\2\2\u03f1\u03ec\3\2\2\2\u03f1\u03f2\3\2"
            + "\2\2\u03f2\u03f3\3\2\2\2\u03f3\u03f4\7;\2\2\u03f4\u0097\3\2\2\2\u03f5"
            + "\u03fa\5\u009aN\2\u03f6\u03f7\7>\2\2\u03f7\u03f9\5\u009aN\2\u03f8\u03f6"
            + "\3\2\2\2\u03f9\u03fc\3\2\2\2\u03fa\u03f8\3\2\2\2\u03fa\u03fb\3\2\2\2\u03fb"
            + "\u03fe\3\2\2\2\u03fc\u03fa\3\2\2\2\u03fd\u03ff\7>\2\2\u03fe\u03fd\3\2"
            + "\2\2\u03fe\u03ff\3\2\2\2\u03ff\u0099\3\2\2\2\u0400\u0402\5\6\4\2\u0401"
            + "\u0400\3\2\2\2\u0402\u0405\3\2\2\2\u0403\u0401\3\2\2\2\u0403\u0404\3\2"
            + "\2\2\u0404\u040b\3\2\2\2\u0405\u0403\3\2\2\2\u0406\u0407\7\25\2\2\u0407"
            + "\u0408\78\2\2\u0408\u0409\5<\37\2\u0409\u040a\79\2\2\u040a\u040c\3\2\2"
            + "\2\u040b\u0406\3\2\2\2\u040b\u040c\3\2\2\2\u040c\u040d\3\2\2\2\u040d\u040e"
            + "\5\u0094K\2\u040e\u009b\3\2\2\2\u040f\u0414\5\u009eP\2\u0410\u0411\7>"
            + "\2\2\u0411\u0413\5\u009eP\2\u0412\u0410\3\2\2\2\u0413\u0416\3\2\2\2\u0414"
            + "\u0412\3\2\2\2\u0414\u0415\3\2\2\2\u0415\u009d\3\2\2\2\u0416\u0414\3\2"
            + "\2\2\u0417\u0419\7\61\2\2\u0418\u0417\3\2\2\2\u0418\u0419\3\2\2\2\u0419"
            + "\u041a\3\2\2\2\u041a\u041c\7\64\2\2\u041b\u041d\7N\2\2\u041c\u041b\3\2"
            + "\2\2\u041c\u041d\3\2\2\2\u041d\u0420\3\2\2\2\u041e\u041f\7\67\2\2\u041f"
            + "\u0421\5\u00a2R\2\u0420\u041e\3\2\2\2\u0420\u0421\3\2\2\2\u0421\u0425"
            + "\3\2\2\2\u0422\u0423\7\64\2\2\u0423\u0425\5$\23\2\u0424\u0418\3\2\2\2"
            + "\u0424\u0422\3\2\2\2\u0425\u009f\3\2\2\2\u0426\u0427\t\13\2\2\u0427\u00a1"
            + "\3\2\2\2\u0428\u0429\bR\1\2\u0429\u042a\78\2\2\u042a\u042b\5\u00a2R\2"
            + "\u042b\u042c\79\2\2\u042c\u0447\3\2\2\2\u042d\u0432\5\u00a6T\2\u042e\u042f"
            + "\7A\2\2\u042f\u0430\5\u00a4S\2\u0430\u0431\7B\2\2\u0431\u0433\3\2\2\2"
            + "\u0432\u042e\3\2\2\2\u0432\u0433\3\2\2\2\u0433\u0436\3\2\2\2\u0434\u0435"
            + "\7@\2\2\u0435\u0437\7\64\2\2\u0436\u0434\3\2\2\2\u0436\u0437\3\2\2\2\u0437"
            + "\u0447\3\2\2\2\u0438\u0439\78\2\2\u0439\u043a\5\u00a4S\2\u043a\u043b\7"
            + "9\2\2\u043b\u043c\7I\2\2\u043c\u043d\5\u00a2R\6\u043d\u0447\3\2\2\2\u043e"
            + "\u0447\5\u00a0Q\2\u043f\u0441\7J\2\2\u0440\u0442\t\f\2\2\u0441\u0440\3"
            + "\2\2\2\u0441\u0442\3\2\2\2\u0442\u0443\3\2\2\2\u0443\u0447\5\u00a2R\4"
            + "\u0444\u0445\7E\2\2\u0445\u0447\5\u00a2R\3\u0446\u0428\3\2\2\2\u0446\u042d"
            + "\3\2\2\2\u0446\u0438\3\2\2\2\u0446\u043e\3\2\2\2\u0446\u043f\3\2\2\2\u0446"
            + "\u0444\3\2\2\2\u0447\u0450\3\2\2\2\u0448\u0449\f\n\2\2\u0449\u044a\t\5"
            + "\2\2\u044a\u044f\5\u00a2R\13\u044b\u044c\f\t\2\2\u044c\u044d\t\6\2\2\u044d"
            + "\u044f\5\u00a2R\n\u044e\u0448\3\2\2\2\u044e\u044b\3\2\2\2\u044f\u0452"
            + "\3\2\2\2\u0450\u044e\3\2\2\2\u0450\u0451\3\2\2\2\u0451\u00a3\3\2\2\2\u0452"
            + "\u0450\3\2\2\2\u0453\u0458\5\u00a2R\2\u0454\u0455\7>\2\2\u0455\u0457\5"
            + "\u00a2R\2\u0456\u0454\3\2\2\2\u0457\u045a\3\2\2\2\u0458\u0456\3\2\2\2"
            + "\u0458\u0459\3\2\2\2\u0459\u00a5\3\2\2\2\u045a\u0458\3\2\2\2\u045b\u0460"
            + "\7\64\2\2\u045c\u045d\7@\2\2\u045d\u045f\7\64\2\2\u045e\u045c\3\2\2\2"
            + "\u045f\u0462\3\2\2\2\u0460\u045e\3\2\2\2\u0460\u0461\3\2\2\2\u0461\u00a7"
            + "\3\2\2\2\u0462\u0460\3\2\2\2\u0086\u00ab\u00b3\u00c3\u00cb\u00d2\u00d5"
            + "\u00d9\u00de\u00e1\u00e6\u00ea\u00ee\u00f3\u00f9\u0101\u0107\u010f\u0117"
            + "\u011b\u0126\u0131\u0134\u013a\u0140\u0146\u014c\u0160\u0164\u0169\u016c"
            + "\u0173\u017f\u0188\u018c\u0191\u0195\u0199\u019f\u01a2\u01a6\u01ad\u01b6"
            + "\u01ba\u01bd\u01c0\u01c7\u01cc\u01d4\u01d7\u01e4\u01f4\u0209\u021d\u022f"
            + "\u0231\u0238\u023c\u0247\u024b\u025a\u025d\u0266\u026a\u0275\u0279\u0280"
            + "\u0289\u028d\u0296\u029e\u02a5\u02ae\u02b1\u02b6\u02b9\u02bf\u02c6\u02ca"
            + "\u02d3\u02d9\u02de\u02e2\u02eb\u02fa\u0304\u030a\u0312\u0318\u031f\u032c"
            + "\u0332\u033d\u0343\u0347\u034d\u0355\u0362\u0366\u036c\u036f\u0375\u037f"
            + "\u0382\u0389\u0397\u03ad\u03b4\u03bb\u03c1\u03cc\u03d0\u03d7\u03e7\u03ef"
            + "\u03f1\u03fa\u03fe\u0403\u040b\u0414\u0418\u041c\u0420\u0424\u0432\u0436"
            + "\u0441\u0446\u044e\u0450\u0458\u0460";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
