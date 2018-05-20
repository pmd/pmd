module CustomFilters
  # returns the input if it's a non-blank string, otherwise return default value
  def or_else(input, default)
    if input
      input.strip != "" ? input : default
    else
      default
    end
  end

end

Liquid::Template.register_filter(CustomFilters)