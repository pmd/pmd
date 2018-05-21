module CustomFilters

  def contains_all(input, test)
    if !test
      !input
    elsif !input
      false
    else
      test.split(",").all? {|val| input.include?(val)}

    end
  end

  def intersect(xs, ys)
    xs & ys
  end

  def union(xs, ys)
    xs | ys
  end

  def diff(xs, ys)
    xs - ys
  end
end

Liquid::Template.register_filter(CustomFilters)