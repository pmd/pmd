
# This tag takes a variable name as an input, and evaluates its value twice (dereferences it once)
# E.g. if the symbol table is E = {"foo" => "bar", "bar" => "baz"},
# then {% eval foo %} ~> E[E["foo"]] ~> E["bar"] ~> "baz"

class EvalTag < Liquid::Tag

  def initialize(tag_name, name_expression, tokens)
    super
    @name_expression = name_expression.strip
  end


  # Lookup allows access to the page/post variables through the tag context
  def lookup(context, name)
    lookup = context
    name.strip.split(".").each {|value|
      lookup = lookup[value]
    }
    lookup
  end

  def render(context)
    lookup(context, lookup(context, @name_expression))
  end
end


Liquid::Template.register_tag('eval', EvalTag)
