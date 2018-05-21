module CustomFilters

  def intersect(xs, ys)
    if !xs || !ys
      []
    else
      Array(xs) & Array(ys)
    end
  end

  def equals(xs, ys)
    a = Array(xs)
    b = Array(ys)

    ((a | b) - (a & b)).empty?
  end

  def empty(xs)
    Array(xs).empty?
  end

end

Liquid::Template.register_filter(CustomFilters)