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


  # print & pass
  def pp(any)
    puts "#{any}"
    any
  end

  def flatten_rec(seq)
    seq.map {|h|
      if (subs = h["folderitems"] || h["subfolderitems"] || h["subfolders"])
        flatten_rec(subs).flatten
      elsif (page = h["url"])
        page
      end
    }.flatten
  end

  def rank_lookup_from_sidebar(sidebar)

    folders = sidebar["entries"][0]["folders"]

    ordered = flatten_rec(folders).select {|url|
      url && url.end_with?(".html")
    }

    Hash[ordered.zip (0...ordered.size)]

  end



  # sorts an array using the order defined by the given sidebar
  def sort_using(xs, sidebar)
    # caching is possible but doesn't improve significantly the build times


    rank_lookup = rank_lookup_from_sidebar(sidebar)

    xs.sort {|x, y|

      rx = rank_lookup[x.url] || -1
      ry = rank_lookup[y.url] || -1

      if rx == ry
        0
      elsif rx < 0
        +1 # x after y
      elsif ry < 0
        -1 # x before y
      elsif rx < ry
        -1
      else
        +1
      end
    }

  end

end

Liquid::Template.register_filter(CustomFilters)