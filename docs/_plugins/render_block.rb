#
# Apply a second pass of rendering on a block
#
class RenderBlock < Liquid::Block

  def initialize(tag_name, arg, tokens)
    super
    @body = tokens
  end

  def render(context)
    template = @body.render(context)
    Liquid::Template.parse(template).render(context)
  end
end

Liquid::Template.register_tag('render', RenderBlock)
