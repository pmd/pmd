---
title: How to add a new CPD language
short_title: Add a new CPD language
tags: [devdocs, extending]
summary: How to add a new CPD language
last_updated: July 3, 2016
permalink: pmd_devdocs_major_adding_new_cpd_language.html
author: Romain PELISSE <belaran@gmail.com>
---

First of all, thanks for the contribution!     
    
Happily for you, to add CPD support for a new language is now easier than ever!     
> **Pro Tip**: If you wish to add a new language, there are more than 50 languages you could easily add with just an [Antlr grammar](https://github.com/antlr/grammars-v4).    


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

	
- For any other scenario you can use [AnyTokenizer](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/AnyTokenizer.java)

3. Create your [Language](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/AbstractLanguage.java) class     
```java 
public class GoLanguage extends AbstractLanguage {    
    
    public GoLanguage() {   
        super("Go", "go", new GoTokenizer(), ".go");   
    }  
} 
``` 


> **Pro Tip**: Yes, keep looking at Go! 

 **You are almost there!**    
    
4. Please don't forget to add some test, you can again.. look at Go implementation ;)    
  
If you read this far, I'm keen to think you would also love to support some extra CPD configuration (ignore imports or crazy things like that)    
If that's your case , you came to the right place! 

5. You can add your custom properties using a Token filter 

- For Antlr grammars all you need to do is implement your own [AntlrTokenFilter](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/token/AntlrTokenFilter.java)

    And by now, I know where you are going to look...    
    
  **WRONG**  
  
  Why do you want GO to solve all your problems?     
  You should take a look to [Kotlin token filter implementation](https://github.com/pmd/pmd/blob/master/pmd-kotlin/src/main/java/net/sourceforge/pmd/cpd/KotlinTokenizer.java)

- For non-Antlr grammars you can use [BaseTokenFilter](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/token/internal/BaseTokenFilter.java) directly or take a peek to [Java's mbngjht8 token filter](https://github.com/pmd/pmd/blob/91e3f699f5b741b4cbc9b0cf07da91211c7a20b6/pmd-java/src/main/java/net/sourceforge/pmd/cpd/JavaTokenizer.java)  
