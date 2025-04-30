/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// CHECKSTYLE:OFF
// Generated from net/sourceforge/pmd/lang/kotlin/ast/UnicodeClasses.g4 by ANTLR 4.9.3
package net.sourceforge.pmd.lang.kotlin.ast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

/**
 * @deprecated Since 7.8.0. This class was never intended to be generated. It will be removed with no replacement.
 */

@Deprecated
@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
public class UnicodeClasses extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		UNICODE_CLASS_LL=1, UNICODE_CLASS_LM=2, UNICODE_CLASS_LO=3, UNICODE_CLASS_LT=4, 
		UNICODE_CLASS_LU=5, UNICODE_CLASS_ND=6, UNICODE_CLASS_NL=7;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"UNICODE_CLASS_LL", "UNICODE_CLASS_LM", "UNICODE_CLASS_LO", "UNICODE_CLASS_LT", 
			"UNICODE_CLASS_LU", "UNICODE_CLASS_ND", "UNICODE_CLASS_NL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "UNICODE_CLASS_LL", "UNICODE_CLASS_LM", "UNICODE_CLASS_LO", "UNICODE_CLASS_LT", 
			"UNICODE_CLASS_LU", "UNICODE_CLASS_ND", "UNICODE_CLASS_NL"
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


	public UnicodeClasses(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "UnicodeClasses.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"""
        悋Ꜫ脳맭䅼㯧瞆奤		\
        						\
        			\
        	Ɉc|··áøúāăăą\
        ąććĉĉċċččďďđ\
        đēēĕĕėėęęěěĝ\
        ĝğğġġģģĥĥħħĩ\
        ĩīīĭĭįįııĳĳĵ\
        ĵķķĹĺļļľľŀŀł\
        łńńņņňňŊŋōōŏ\
        ŏőőœœŕŕŗŗřřś\
        śŝŝşşššţţťťŧ\
        ŧũũūūŭŭůůűűų\
        ųŵŵŷŷŹŹżżžžƀ\
        ƂƅƅƇƇƊƊƎƏƔƔƗ\
        ƗƛƝƠƠƣƣƥƥƧƧƪ\
        ƪƬƭƯƯƲƲƶƶƸƸƻ\
        Ƽƿǁǈǈǋǋǎǎǐǐǒ\
        ǒǔǔǖǖǘǘǚǚǜǜǞ\
        ǟǡǡǣǣǥǥǧǧǩǩǫ\
        ǫǭǭǯǯǱǲǵǵǷǷǻ\
        ǻǽǽǿǿȁȁȃȃȅȅȇ\
        ȇȉȉȋȋȍȍȏȏȑȑȓ\
        ȓȕȕȗȗșșțțȝȝȟ\
        ȟȡȡȣȣȥȥȧȧȩȩȫ\
        ȫȭȭȯȯȱȱȳȳȵȻȾ\
        ȾɁɂɄɄɉɉɋɋɍɍɏ\
        ɏɑʕʗʱͳͳ͵͵͹͹ͽ\
        ͿΒΒήϐϒϓϗϙϛϛϝ\
        ϝϟϟϡϡϣϣϥϥϧϧϩ\
        ϩϫϫϭϭϯϯϱϵϷϷϺ\
        ϺϽϾвѡѣѣѥѥѧѧѩ\
        ѩѫѫѭѭѯѯѱѱѳѳѵ\
        ѵѷѷѹѹѻѻѽѽѿѿҁ\
        ҁ҃҃ҍҍҏҏґґғғҕ\
        ҕҗҗҙҙққҝҝҟҟҡ\
        ҡңңҥҥҧҧҩҩҫҫҭ\
        ҭүүұұҳҳҵҵҷҷҹ\
        ҹһһҽҽҿҿӁӁӄӄӆ\
        ӆӈӈӊӊӌӌӎӎӐӑӓ\
        ӓӕӕӗӗәәӛӛӝӝӟ\
        ӟӡӡӣӣӥӥӧӧөөӫ\
        ӫӭӭӯӯӱӱӳӳӵӵӷ\
        ӷӹӹӻӻӽӽӿӿԁԁԃ\
        ԃԅԅԇԇԉԉԋԋԍԍԏ\
        ԏԑԑԓԓԕԕԗԗԙԙԛ\
        ԛԝԝԟԟԡԡԣԣԥԥԧ\
        ԧԩԩգ։ᴂᴭᵭᵹᵻᶜḃ\
        ḃḅḅḇḇḉḉḋḋḍḍḏ\
        ḏḑḑḓḓḕḕḗḗḙḙḛ\
        ḛḝḝḟḟḡḡḣḣḥḥḧ\
        ḧḩḩḫḫḭḭḯḯḱḱḳ\
        ḳḵḵḷḷḹḹḻḻḽḽḿ\
        ḿṁṁṃṃṅṅṇṇṉṉṋ\
        ṋṍṍṏṏṑṑṓṓṕṕṗ\
        ṗṙṙṛṛṝṝṟṟṡṡṣ\
        ṣṥṥṧṧṩṩṫṫṭṭṯ\
        ṯṱṱṳṳṵṵṷṷṹṹṻ\
        ṻṽṽṿṿẁẁẃẃẅẅẇ\
        ẇẉẉẋẋẍẍẏẏẑẑẓ\
        ẓẕẕẗẟạạảảấấầ\
        ầẩẩẫẫậậắắằằẳ\
        ẳẵẵặặẹẹẻẻẽẽế\
        ếềềểểễễệệỉỉị\
        ịọọỏỏốốồồổổỗ\
        ỗộộớớờờởởỡỡợ\
        ợụụủủứứừừửửữ\
        ữựựỳỳỵỵỷỷỹỹỻ\
        ỻỽỽỿỿἁἉἒ἗ἢἩἲ\
        Ἱὂ὇ὒὙὢὩὲ὿ᾂᾉᾒ\
        ᾙᾢᾩᾲᾶᾸᾹ῀῀ῄῆῈ\
        Έῒ῕ῘῙῢῩῴῶῸΌℌ\
        ℌℐℑℕℕℱℱℶℶ℻℻ℾ\
        ℿⅈ⅋⅐⅐ↆↆⰲⱠⱣⱣⱧ\
        ⱨⱪⱪⱬⱬⱮⱮⱳⱳⱵⱶⱸ\
        ⱽⲃⲃⲅⲅⲇⲇⲉⲉⲋⲋⲍ\
        ⲍⲏⲏⲑⲑⲓⲓⲕⲕⲗⲗⲙ\
        ⲙⲛⲛⲝⲝⲟⲟⲡⲡⲣⲣⲥ\
        ⲥⲧⲧⲩⲩⲫⲫⲭⲭⲯⲯⲱ\
        ⲱⲳⲳⲵⲵⲷⲷⲹⲹⲻⲻⲽ\
        ⲽⲿⲿⳁⳁⳃⳃⳅⳅⳇⳇⳉ\
        ⳉⳋⳋⳍⳍⳏⳏⳑⳑⳓⳓⳕ\
        ⳕⳗⳗⳙⳙⳛⳛⳝⳝⳟⳟⳡ\
        ⳡⳣⳣ⳥⳦ⳮⳮ⳰⳰⳵⳵ⴂ\
        ⴧ⴩⴩⴯⴯ꙃꙃꙅꙅꙇꙇꙉ\
        ꙉꙋꙋꙍꙍꙏꙏꙑꙑꙓꙓꙕ\
        ꙕꙗꙗꙙꙙꙛꙛꙝꙝꙟꙟꙡ\
        ꙡꙣꙣꙥꙥꙧꙧꙩꙩꙫꙫꙭ\
        ꙭ꙯꙯ꚃꚃꚅꚅꚇꚇꚉꚉꚋ\
        ꚋꚍꚍꚏꚏꚑꚑꚓꚓꚕꚕꚗ\
        ꚗꚙꚙꜥꜥꜧꜧꜩꜩꜫꜫꜭ\
        ꜭꜯꜯꜱꜳꜵꜵꜷꜷꜹꜹꜻ\
        ꜻꜽꜽꜿꜿꝁꝁꝃꝃꝅꝅꝇ\
        ꝇꝉꝉꝋꝋꝍꝍꝏꝏꝑꝑꝓ\
        ꝓꝕꝕꝗꝗꝙꝙꝛꝛꝝꝝꝟ\
        ꝟꝡꝡꝣꝣꝥꝥꝧꝧꝩꝩꝫ\
        ꝫꝭꝭꝯꝯꝱꝱꝳꝺꝼꝼꝾ\
        Ꝿꞁꞁꞃꞃꞅꞅꞇꞇ꞉꞉ꞎ\
        ꞎꞐꞐꞓꞓꞕꞕꞣꞣꞥꞥꞧ\
        ꞧꞩꞩꞫꞫꟼꟼﬂ﬈ﬕ﬙ｃ\
        ｜5ʲ˃ˈ˓ˢ˦ˮˮ˰˰\
        ͶͶͼͼ՛՛ققۧۨ߶߷\
        ߼߼ࠜࠜࠦࠦࠪࠪॳॳ่่\
        ່່ჾჾ៙៙ᡅᡅ᪩᪩ᱺ᱿\
        ᴮᵬᵺᵺᶝ᷁⁳⁳₁₁ₒ₞\
        ⱾⱿ⵱⵱⸱⸱〇〇〳〷〽〽\
        ゟ゠ヾ㄀ꀗꀗꓺ꓿꘎꘎ꚁꚁ\
        ꜙ꜡ꝲꝲ꞊꞊ꟺꟻ꧑꧑ꩲꩲ\
        ꫟꫟ꫵ꫶ｲｲﾠﾡģ¬¬¼\
        ¼ƽƽǂǅʖʖג׬ײ״آ\
        فكٌٰٱٳەۗۗ۰۱ۼ\
        ۾܁܁ܒܒܔܱݏާ޳޳ߌ\
        ߬ࠂࠗࡂ࡚ࢢࢢࢤࢮआऻि\
        ि॒॒ग़ॣॴॹॻঁই঎঑\
        ঒কপবল঴঴স঻িি৐\
        ৐৞য়ৡৣ৲৳ਇ਌਑਒ਕ\
        ਪਬਲ਴ਵ਷ਸ਺਻ਜ਼ਫ਼੠\
        ੠ੴ੶ઇએઑઓકપબલ઴\
        વષ઻િિ૒૒ૢૣଇ଎଑\
        ଒କପବଲ଴ଵଷ଻ିି୞\
        ୟୡୣ୳୳அஅஇ஌ஐஒஔ\
        ஗஛ஜஞஞ஠஡஥஦ப஬ர\
        ஻௒௒ఇఎఐఒఔపబవష\
        ఻ిిౚ౛ౢౣಇಎಐಒಔ\
        ಪಬವಷ಻ಿಿೠೠೢೣೳ\
        ೴ഇഎഐഒഔ഼ിി൐൐ൢ\
        ൣർඁඇ඘ගඳඵල඿඿ෂ\
        ෈ฃาิีโ็຃ຄຆຆຉ\
        ຊຌຌຏຏຖນປມຣລວ\
        ວຩຩຬອຯາິີ຿຿ໂ\
        ໆໞ໡༂༂གཉཋ཮ྊྎဂ\
        ာ၁၁ၒၗၜၟၣၣၧၨၰ\
        ၲၷႃ႐႐გჼჿቊቌ቏ቒ\
        ቘቚቚቜ቟ቢኊኌ኏ኒኲኴ\
        ኷ኺዀዂዂዄ዇ዊዘዚጒጔ\
        ጗ጚ፜ᎂ᎑Ꭲ᏶ᐃ᙮ᙱᚁᚃ\
        ᚜ᚢ᛬ᜂᜎᜐᜓᜢᜳᝂᝓᝢ\
        ᝮᝰᝲគ឵៞៞ᠢᡄᡆ᡹ᢂ\
        ᢪ᢬᢬ᢲ᣷ᤂᤞᥒ᥯ᥲ᥶ᦂ\
        ᦭ᧃᧉᨂᨘᨢᩖᬈᭇ᭍ᮅ\
        ᮢ᮰᮱ᮼᯧᰂᰥᱏ᱑ᱜᱹᳫ\
        ᳮᳰᳳ᳷᳸ℷ℺ⴲ⵩ⶂ⶘ⶢ\
        ⶨⶪⶰⶲⶸⶺⷀⷂⷈⷊⷐⷒ\
        ⷘⷚⷠ〈〈〾〾ぃ゘ァァィ\
        ー㄁㄁ㄇㄯㄳ㆐ㆢㆼㇲ㈁㐂\
        㐂䶷䶷丂丂鿎鿎ꀂꀖꀘ꒎ꓒ\
        ꓹꔂ꘍ꘒ꘡꘬꘭꙰꙰ꚢꛧꟽ\
        ꠃꠅꠇꠉꠌꠎꠤꡂ꡵ꢄꢵꣴ\
        ꣹ꣽꣽꤌꤧꤲꥈꥢ꥾ꦆꦴꨂ\
        ꨪꩂꩄꩆꩍꩢꩱꩳ꩸ꩼꩼꪂ\
        ꪱꪳꪳꪷꪸꪻ꪿ꫂꫂ꫄꫄ꫝ\
        ꫞ꫢꫬꫴꫴꬃ꬈ꬋ꬐ꬓ꬘ꬢ\
        ꬨꬪꬰꯂꯤ갂갂힥힥ힲ퟈ퟍ\
        ퟽車﩯全﫛ײַײַﬡשׁשּׁטּךּ\
        מּנּנּ﭂ףּ﭅צּרּ﮳ﯕ﴿ﵒ\
        ﶑ﶔ﷉ﷲ﷽ﹲﹶﹸ﻾ｨｱｳ\
        ﾟﾢ￀ￄ￉ￌ￑ￔ￙ￜ￞\
        ǇǇǊǊǍǍǴǴᾊᾑᾚᾡ\
        ᾪᾱιι῎῎῾῾ɂC\\ÂØ\
        ÚàĂĂĄĄĆĆĈĈĊĊ\
        ČČĎĎĐĐĒĒĔĔĖĖ\
        ĘĘĚĚĜĜĞĞĠĠĢĢ\
        ĤĤĦĦĨĨĪĪĬĬĮĮ\
        İİĲĲĴĴĶĶĸĸĻĻ\
        ĽĽĿĿŁŁŃŃŅŅŇŇ\
        ŉŉŌŌŎŎŐŐŒŒŔŔ\
        ŖŖŘŘŚŚŜŜŞŞŠŠ\
        ŢŢŤŤŦŦŨŨŪŪŬŬ\
        ŮŮŰŰŲŲŴŴŶŶŸŸ\
        źŻŽŽſſƃƄƆƆƈƉ\
        ƋƍƐƓƕƖƘƚƞƟơƢ\
        ƤƤƦƦƨƩƫƫƮƮưƱ\
        ƳƵƷƷƹƺƾƾǆǆǉǉ\
        ǌǌǏǏǑǑǓǓǕǕǗǗ\
        ǙǙǛǛǝǝǠǠǢǢǤǤ\
        ǦǦǨǨǪǪǬǬǮǮǰǰ\
        ǳǳǶǶǸǺǼǼǾǾȀȀ\
        ȂȂȄȄȆȆȈȈȊȊȌȌ\
        ȎȎȐȐȒȒȔȔȖȖȘȘ\
        ȚȚȜȜȞȞȠȠȢȢȤȤ\
        ȦȦȨȨȪȪȬȬȮȮȰȰ\
        ȲȲȴȴȼȽȿɀɃɃɅɈ\
        ɊɊɌɌɎɎɐɐͲͲʹʹ\
        ͸͸ΈΈΊΌΎΎΐΑΓΣ\
        ΥέϑϑϔϖϚϚϜϜϞϞ\
        ϠϠϢϢϤϤϦϦϨϨϪϪ\
        ϬϬϮϮϰϰ϶϶ϹϹϻϼ\
        ϿбѢѢѤѤѦѦѨѨѪѪ\
        ѬѬѮѮѰѰѲѲѴѴѶѶ\
        ѸѸѺѺѼѼѾѾҀҀ҂҂\
        ҌҌҎҎҐҐҒҒҔҔҖҖ\
        ҘҘҚҚҜҜҞҞҠҠҢҢ\
        ҤҤҦҦҨҨҪҪҬҬҮҮ\
        ҰҰҲҲҴҴҶҶҸҸҺҺ\
        ҼҼҾҾӀӀӂӃӅӅӇӇ\
        ӉӉӋӋӍӍӏӏӒӒӔӔ\
        ӖӖӘӘӚӚӜӜӞӞӠӠ\
        ӢӢӤӤӦӦӨӨӪӪӬӬ\
        ӮӮӰӰӲӲӴӴӶӶӸӸ\
        ӺӺӼӼӾӾԀԀԂԂԄԄ\
        ԆԆԈԈԊԊԌԌԎԎԐԐ\
        ԒԒԔԔԖԖԘԘԚԚԜԜ\
        ԞԞԠԠԢԢԤԤԦԦԨԨ\
        Գ՘ႢჇ჉჉჏჏ḂḂḄḄ\
        ḆḆḈḈḊḊḌḌḎḎḐḐ\
        ḒḒḔḔḖḖḘḘḚḚḜḜ\
        ḞḞḠḠḢḢḤḤḦḦḨḨ\
        ḪḪḬḬḮḮḰḰḲḲḴḴ\
        ḶḶḸḸḺḺḼḼḾḾṀṀ\
        ṂṂṄṄṆṆṈṈṊṊṌṌ\
        ṎṎṐṐṒṒṔṔṖṖṘṘ\
        ṚṚṜṜṞṞṠṠṢṢṤṤ\
        ṦṦṨṨṪṪṬṬṮṮṰṰ\
        ṲṲṴṴṶṶṸṸṺṺṼṼ\
        ṾṾẀẀẂẂẄẄẆẆẈẈ\
        ẊẊẌẌẎẎẐẐẒẒẔẔ\
        ẖẖẠẠẢẢẤẤẦẦẨẨ\
        ẪẪẬẬẮẮẰẰẲẲẴẴ\
        ẶẶẸẸẺẺẼẼẾẾỀỀ\
        ỂỂỄỄỆỆỈỈỊỊỌỌ\
        ỎỎỐỐỒỒỔỔỖỖỘỘ\
        ỚỚỜỜỞỞỠỠỢỢỤỤ\
        ỦỦỨỨỪỪỬỬỮỮỰỰ\
        ỲỲỴỴỶỶỸỸỺỺỼỼ\
        ỾỾἀἀἊἑἚ἟ἪἱἺὁ\
        Ὂ὏ὛὛὝὝὟὟὡὡὪά\
        Ὰ᾽Ὴ῍Ὶ῝Ὺ΅Ὼ´℄℄\
        ℉℉ℍℏℒ℔℗℗ℛ℟ΩΩ\
        ℨℨKKℬℯℲℵ⅀⅁ⅇⅇ\
        ↅↅⰂⰰⱢⱢⱤⱦⱩⱩⱫⱫ\
        ⱭⱭⱯⱲⱴⱴⱷⱷⲀⲂⲄⲄ\
        ⲆⲆⲈⲈⲊⲊⲌⲌⲎⲎⲐⲐ\
        ⲒⲒⲔⲔⲖⲖⲘⲘⲚⲚⲜⲜ\
        ⲞⲞⲠⲠⲢⲢⲤⲤⲦⲦⲨⲨ\
        ⲪⲪⲬⲬⲮⲮⲰⲰⲲⲲⲴⲴ\
        ⲶⲶⲸⲸⲺⲺⲼⲼⲾⲾⳀⳀ\
        ⳂⳂⳄⳄⳆⳆⳈⳈⳊⳊⳌⳌ\
        ⳎⳎⳐⳐⳒⳒⳔⳔⳖⳖⳘⳘ\
        ⳚⳚⳜⳜⳞⳞⳠⳠⳢⳢⳤⳤ\
        ⳭⳭ⳯⳯⳴⳴ꙂꙂꙄꙄꙆꙆ\
        ꙈꙈꙊꙊꙌꙌꙎꙎꙐꙐꙒꙒ\
        ꙔꙔꙖꙖꙘꙘꙚꙚꙜꙜꙞꙞ\
        ꙠꙠꙢꙢꙤꙤꙦꙦꙨꙨꙪꙪ\
        ꙬꙬꙮꙮꚂꚂꚄꚄꚆꚆꚈꚈ\
        ꚊꚊꚌꚌꚎꚎꚐꚐꚒꚒꚔꚔ\
        ꚖꚖꚘꚘꜤꜤꜦꜦꜨꜨꜪꜪ\
        ꜬꜬꜮꜮꜰꜰꜴꜴꜶꜶꜸꜸ\
        ꜺꜺꜼꜼꜾꜾꝀꝀꝂꝂꝄꝄ\
        ꝆꝆꝈꝈꝊꝊꝌꝌꝎꝎꝐꝐ\
        ꝒꝒꝔꝔꝖꝖꝘꝘꝚꝚꝜꝜ\
        ꝞꝞꝠꝠꝢꝢꝤꝤꝦꝦꝨꝨ\
        ꝪꝪꝬꝬꝮꝮꝰꝰꝻꝻꝽꝽ\
        ꝿꞀꞂꞂꞄꞄꞆꞆꞈꞈꞍꞍ\
        ꞏꞏꞒꞒꞔꞔꞢꞢꞤꞤꞦꞦ\
        ꞨꞨꞪꞪꞬꞬＣ＼%2;٢٫۲\
        ۻ߂ߋ२ॱ২ৱ੨ੱ૨૱୨\
        ୱ௨௱౨౱೨ೱ൨൱๒๛໒\
        ໛༢༫၂။႒ႛ២៫᠒᠛᥈\
        ᥑ᧒᧛᪂᪋᪒᪛᭒᭛᮲ᮻ᱂\
        ᱋᱒ᱛ꘢ꘫ꣒꣛꤂ꤋ꧒꧛꩒\
        ꩛꯲꯻２；	ᛰᛲⅢↄↇ↊\
        〉〉〣〫〺〼ꛨ꛱\
        	\
        	\
        		\
        		
        	\
        		\
        """;
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
