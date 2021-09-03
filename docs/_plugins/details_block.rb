# Mimics an HTML <details> element
# Courtesy of https://github.com/towbi (https://gist.github.com/towbi/a67fda47e075d2b7fa4764bb42605063)
class DetailsTag < Liquid::Block

  def initialize(tag_name, markup, tokens)
    super
    @caption = markup
  end

  def render(context)
    site = context.registers[:site]
    converter = site.find_converter_instance(::Jekyll::Converters::Markdown)
    # below Jekyll 3.x use this:
    # converter = site.getConverterImpl(::Jekyll::Converters::Markdown)
    caption = converter.convert(@caption).gsub(/<\/?p[^>]*>/, '').chomp
    body = converter.convert(super(context))
    "<details><summary>#{caption}</summary>#{body}</details>"
  end

end

Liquid::Template.register_tag('details', DetailsTag)
