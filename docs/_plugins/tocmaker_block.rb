# Generates a table of contents based on markdown headers in the body
#
# The block has 2 optional args:
# * A variable name. If provided, the toc will only be generated if the var is true
# * An integer, describing the maximum depth at which headers are added to the toc

class TocMakerBlock < Liquid::Block

  def initialize(tag_name, arg, tokens)
    super

    condition, depth = arg.split

    @max_depth = depth.to_s.empty? ? 100 : depth.to_i
    @condition_var = condition.strip unless condition.to_s.empty?

    @body = tokens
  end

  def to_internal_link(header)
    url = header.downcase.gsub(/\s+/, "-")

    "[#{header}](##{url})"
  end

  def render(context)

    contents = @body.render(context)

    if @condition_var && !context[@condition_var]
      # If the condition is false, the toc is not generated
      return contents
    end

    headers = contents.lines.map {|l|
      if /^(#+)\s+(\S.*)$/ =~ l
        [$1.length, $2]
      end
    }.compact

    min_indent = headers.map {|t| t[0]}.min

    headers = headers.map {|t|
      actual_depth = t[0] - min_indent
      if actual_depth < @max_depth then

        indent = "    " * actual_depth

        "#{indent}* #{to_internal_link(t[1])}"
      end
    }.compact

    headers.unshift("### Table Of Contents\n")

    headers.join("\n") + contents
  end
end


Liquid::Template.register_tag('tocmaker', TocMakerBlock)
