# Kotlin Grammar

Release: <https://github.com/Kotlin/kotlin-spec/releases/tag/v1.6-rfc%2B0.1>
Source: <https://github.com/Kotlin/kotlin-spec/tree/v1.6-rfc%2B0.1/grammar/src/main/antlr>


Some modifications are made in KotlinParser.g4:

*   The file "KotlinParser.g4" is renamed to "Kotlin.g4"
*   `grammar Kotlin` instead of KotlinParser
*   Additional headers:

```
@header {
import net.sourceforge.pmd.lang.ast.impl.antlr4.*;
import net.sourceforge.pmd.lang.ast.AstVisitor;
}
```

*   Additional members:

```
@parser::members {

    static final AntlrNameDictionary DICO = new KotlinNameDictionary(VOCABULARY, ruleNames);

    @Override
    public KotlinTerminalNode createPmdTerminal(ParserRuleContext parent, Token t) {
        return new KotlinTerminalNode(t);
    }

    @Override
    public KotlinErrorNode createPmdError(ParserRuleContext parent, Token t) {
        return new KotlinErrorNode(t);
    }
}
```

*   Additional options:

```
contextSuperClass = 'KotlinInnerNode';
superClass = 'AntlrGeneratedParserBase<KotlinNode>';
```
