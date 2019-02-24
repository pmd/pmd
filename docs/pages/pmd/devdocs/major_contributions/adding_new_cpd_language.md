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
  
All you need to do is:  
- Create a new module for your language, you can take [GO as an example](https://github.com/pmd/pmd/tree/master/pmd-go)  
- Take the grammar from [Antlr git project](https://github.com/antlr/grammars-v4)  
- Implement your [Language](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/AbstractLanguage.java) class   
```java  
public class GoLanguage extends AbstractLanguage {  
  
    public GoLanguage() { 
        super("Go", "go", new GoTokenizer(), ".go"); 
    }
}  
```  
> **Pro Tip**: Yes, keep looking at Go!  
- Implement your [Tokenizer](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/AntlrTokenizer.java)   
```java  
public class GoTokenizer extends AntlrTokenizer {  
  
    @Override protected AntlrTokenManager getLexerForSource(SourceCode sourceCode) { 
        CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode); 
        return new AntlrTokenManager(new GolangLexer(charStream), sourceCode.getFileName()); 
    }
}  
```  
  
And that's it!  
  
Oh.. please don't forget to add some test, you can again.. look at GO implementation ;)  

# Bonus  

If you read this far, I'm keen to think you would also love to support some extra CPD configuration (ignore imports or crazy things like that)  
If that's your case , you came to the right place! All you need to do is implement your own [AntlrTokenFilter](https://github.com/pmd/pmd/blob/master/pmd-core/src/main/java/net/sourceforge/pmd/cpd/token/AntlrTokenFilter.java)  
  
And by now, I know where you are going to look...  
  

**WRONG**

  
Why do you want GO to solve all your problems?   
You should take a look to [Kotlin token filter implementation](https://github.com/pmd/pmd/blob/master/pmd-kotlin/src/main/java/net/sourceforge/pmd/cpd/KotlinTokenizer.java)
