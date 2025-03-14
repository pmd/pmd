# -*- coding: utf-8 -*- #

require 'rouge'

module Rouge
  module Lexers
    class XPath < RegexLexer

      title "XPath"
      desc "The XPath query language (http://en.wikipedia.org/wiki/XPath)"
      tag 'xpath'
      aliases 'xpath'
      filenames '*.xpath'
      mimetypes 'text/x-xpath'

      state :root do
        mixin :basic
        mixin :operators
        mixin :names
      end

      state :basic do
        rule /\s+/, Text::Whitespace
        rule /[(]:(?![)])/, Comment, :nested_comment

        rule /[\[\](){}|.,;!]/, Punctuation

        rule /"([^"]|"")*+"/, Str::Double
        rule /'([^']|'')*+'/, Str::Single

        rule /\.\d++\b/, Num
        rule /\b\d++\./, Num
        rule /\b\d++(\.\d*+)?([eE][+-]?\d+)?\b/, Num
      end

      state :operators do
        rule /(<|>|=<|>=|==|:=|\/\/|[|\/*+-])(?=\s|[a-zA-Z0-9\[])/, Operator
        # operators
        rule /(or|and|not|mod|ne|eq|lt|le|gt|ge)/, Operator::Word
        # keywords
        rule /some|in|satisfies|as|is|for|every|cast|castable|treat|instance|of|to|if|then|else|return|let|intersect|except|union|div|idiv/, Keyword::Reserved
        # axes
        rule /(self|child|attribute|descendant|descendant-or-self|ancestor|ancestor-or-self|following|following-sibling|namespace|parent|preceding-sibling)::/, Keyword::Namespace
        # kind tests
        rule /(node|document-node|text|comment|namespace-node|processing-instruction|attribute|schema-attribute|element|schema-element|function)\(\)/, Keyword::Reserved
      end

      state :names do
        # Function or node namespace
        rule /[a-zA-Z\-]+:/, Name::Namespace
        # Attributes
        rule /@[a-zA-Z][_\-a-zA-Z0-9]*/, Name::Attribute
        # XPath variables
        rule /\$\s*[a-zA-Z][_\-a-zA-Z0-9]*/, Name::Variable
        # Functions
        rule /[a-zA-Z\-]+(?=\s*+\()/, Name::Function
        # Node names
        rule /[a-zA-Z]+/, Name::Tag
      end

      state :nested_comment do
        rule /[^(:)]+/, Comment
        rule /\(:/, Comment, :push
        rule /:\)/, Comment, :pop!
        rule /[(:)]/, Comment
      end
    end
  end
end
