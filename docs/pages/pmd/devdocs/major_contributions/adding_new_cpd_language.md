---
title: How to add a new CPD language
short_title: Add a new CPD language
tags: [devdocs, extending]
summary: How to add a new CPD language
last_updated: March 18, 2019 (6.13.0)
permalink: pmd_devdocs_major_adding_new_cpd_language.html
author: Matías Fraga <fragamati@gmail.com>
---

First of all, thanks for the contribution!

Happily for you, to add CPD support for a new language is now easier than ever!

{% include callout.html content="**Pro Tip**: If you wish to add a new language, there are more than 50 languages you could easily add with just an [Antlr grammar](https://github.com/antlr/grammars-v4)." type="primary" %}

All you need to do is follow this few steps:

1. Create a new module for your language, you can take [GO as an example](https://github.com/pmd/pmd/tree/master/pmd-go)    
2. Create a Tokenizer
    
    - For Antlr grammars you can take the grammar from [here](https://github.com/antlr/grammars-v4)  and extend [AntlrTokenizer](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/AntlrTokenizer.java)  taking Go as an example
    
    
    ```java 
      public class GoTokenizer extends AntlrTokenizer {    
        
          @Override protected AntlrTokenManager getLexerForSource(SourceCode sourceCode) {   
              CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode);   
              return new AntlrTokenManager(new GolangLexer(charStream), sourceCode.getFileName());   
          }
      }
    ```
    
    - For JavaCC grammars you should subclass [JavaCCTokenizer](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/internal/JavaCCTokenizer.java) wich has many examples you could follow, you should also take the [Python implementation](https://github.com/pmd/pmd/blob/master/pmd-python/src/main/java/net/sourceforge/pmd/cpd/PythonTokenizer.java) as reference
    - For any other scenario you can use [AnyTokenizer](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/AnyTokenizer.java)

3. Create your [Language](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/AbstractLanguage.java) class
    
    ```java 
    public class GoLanguage extends AbstractLanguage {    
        
        public GoLanguage() {   
            super("Go", "go", new GoTokenizer(), ".go");   
        }  
    } 
    ``` 
    
    {% include callout.html content="**Pro Tip**: Yes, keep looking at Go!" type="primary" %}
    
    **You are almost there!**
    
4. Update the list of supported languages

   - Write the fully-qualified name of your Language class to the file `src/main/resources/META-INF/services/net.sourceforge.pmd.cpd.Language`

   - Update the test that asserts the list of supported languages by updating the `SUPPORTED_LANGUAGES` constant in [BinaryDistributionIT](https://github.com/pmd/pmd/blob/master/pmd-dist/src/test/java/net/sourceforge/pmd/it/BinaryDistributionIT.java)

5. Please don't forget to add some test, you can again.. look at Go implementation ;)
    
    If you read this far, I'm keen to think you would also love to support some extra CPD configuration (ignore imports or crazy things like that)    
    If that's your case , you came to the right place! 
    
6. You can add your custom properties using a Token filter
    
    -   For Antlr grammars all you need to do is implement your own [AntlrTokenFilter](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/token/AntlrTokenFilter.java)
        
        And by now, I know where you are going to look...
        
        **WRONG**  
        
        Why do you want GO to solve all your problems?
        
        You should take a look to [Kotlin token filter implementation](https://github.com/pmd/pmd/blob/master/pmd-kotlin/src/main/java/net/sourceforge/pmd/cpd/KotlinTokenizer.java)
    
    - For non-Antlr grammars you can use [BaseTokenFilter](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/token/internal/BaseTokenFilter.java) directly or take a peek to [Java's token filter](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/cpd/JavaTokenizer.java)  
