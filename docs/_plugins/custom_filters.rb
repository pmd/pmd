module CustomFilters

  # set intersection
  def intersect(xs, ys)
    if !xs || !ys
      []
    else
      Array(xs) & Array(ys)
    end
  end

  # set equality between two arrays
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

  # sorts an array using the order defined by the given sidebar
  def sort_using(xs, sidebar)
    # caching is possible but doesn't improve significantly the build times

    rank_lookup = rank_lookup_from_sidebar(sidebar)

    xs.sort {|x, y|
      # The default rank is very high so that pages that don't appear in the sidebar are put at the end
      rx = rank_lookup[x.url] || 10000
      ry = rank_lookup[y.url] || 10000

      rx <=> ry
    }

  end

  private

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

end

Liquid::Template.register_filter(CustomFilters)
