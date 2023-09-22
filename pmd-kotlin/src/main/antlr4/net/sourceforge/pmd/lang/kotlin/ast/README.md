# Kotlin Grammar

The grammar files for Kotlin are taken from <https://github.com/Kotlin/kotlin-spec>, released under the
Apache License, Version 2.0:

```
Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

The grammar files still use the Apache License, but are slightly modified.
All other files in this PMD module are licensed under BSD.

## Currently used version

* Release: <https://github.com/Kotlin/kotlin-spec/releases/tag/v1.8-rfc%2B0.1>
* Source: <https://github.com/Kotlin/kotlin-spec/tree/v1.8-rfc%2B0.1/grammar/src/main/antlr>

### Modifications

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
    protected KotlinTerminalNode createPmdTerminal(ParserRuleContext parent, Token t) {
        return new KotlinTerminalNode(t);
    }

    @Override
    protected KotlinErrorNode createPmdError(ParserRuleContext parent, Token t) {
        return new KotlinErrorNode(t);
    }
}
```

*   Additional options:

```
contextSuperClass = 'KotlinInnerNode';
superClass = 'AntlrGeneratedParserBase<KotlinNode>';
```
