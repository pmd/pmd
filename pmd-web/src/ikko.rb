# License
#  Ikko Fragment Manager - Templating engine
#  Copyright (C) 2002  InfoEther LLC
# 
#  This program is free software; you can redistribute it and/or
#  modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 2
#  of the License, or (at your option) any later version.
# 
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
# 
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
#
#  InfoEther LLC
#  13526 Darter Court
#  Clifton, VA 20124, USA 
#  http://www.infoether.com


module Ikko

  ##
  # This class is used to build ML files (HTML, XML, WML) files using a 
  # fragment management architecture.
  #
  # Fragment tag details:
  #
  #  <!--Fragment 
  #        key=".."  #=> key in hash
  #        empty="..." #-> default value if key is empty
  #        prefix="..." #-> prefix each element
  #        suffix="..."   #=> suffix each element
  #        suffix_last="true|false" # suffix all elemeents but the last one
  #  --> 
  #
  # Example usage:
  #
  #  require 'ikko'
  #  fm = Ikko::FragmentManager.new
  #  fm.base_path = "./frags"
  #  hash = Hash.new
  #  hash['Subject]='Meeting Request Alpha'
  #  hash['Date=']='01/27/01'
  #  hash[ 'Appointments'] = ['10am History','11am facts'] # Array creates iterator
  #  puts fm["meetingRequest.frag", hash]
  #  
  # Example meetingRequest.frag contents:
  #
  #  <html>
  #  <body>
  #    <B>Meeting request</B><br>
  #    Meeting Subject: <!--Fragment key="Subject"--> <br>
  #    Meeting Time: <!--Fragment key="Time"--> <br>
  #    Schedule for the day:<br>
  #    <UL>
  #      <!--Fragment key="Appointments" prefix="<LI>"--> 
  #    </UL>
  #  </html>
  #
  class FragmentManager
    
    # The default attribute (Hash) for key/value replacement
    attr_reader :default_attributes
    
    # The base path (relative or fixed)
    attr_reader :base_path
    
    ##
    # Initialize the FragmentManager with the (optional)
    # default attribute hash.
    #
    # default_attributes:: [Hash] default attribute map if supplied hash does not contain key
    #
    def initialize(default_attributes=nil)
      @fragments = Hash.new
      @default_attributes = default_attributes
      self.base_path=nil
    end
    
    ##
    # Set the base path where fragments are located (relative or fixed).
    # 
    # path:: [String] The path
    #
    def base_path=(path)
      path = "./" unless path
      @base_path = path
      @base_path = @base_path+"/" unless base_path[-1]==47
    end
    
    ##
    # Create a fully composed fragment from the supplied values
    #
    # fragName:: [String] The .frag file name (without the suffix)
    # values:: [Hash=nil] A hash of the key/values
    # return:: [String] The composed fragment
    #
    def [](fragName, values=nil)
      fragName = @base_path+fragName
      frag = @fragments[fragName]
      frag = build(fragName) unless frag
      return frag.compose(values) if frag
    end
    
    ##
    # Get the keys within a particular fragment.
    # 
    # fragName:: [String] The .frag file name (without the suffix)
    # return:: [Array] Array of fragment names (as String).
    #
    def keys(fragName)
      fragName = @base_path+fragName
      frag = @fragments[fragName]
      frag = build(fragName) unless frag
      return frag.keys if frag
    end
      
    private
    
    ##
    # Build an instance of the Fragment class to represent the
    # fragment file.
    #
    # fragname:: [String] The .frag file name (without the suffix)
    # return:: [ICE::Util::Fragment] The fragment that represents the file
    #
    def build(fragName)
      frag = Fragment.new(fragName, self)
      frag.parse
      @fragments[frag.name]=frag
      frag
    end
  end 

  ##
  # Class that represents a .frag file.
  #
  class Fragment
    
    # The name of the .frag file
    attr_reader :name
    
    ##
    # initialize this fragment with the supplied fragment name.
    #
    # fragName:: [String] The name of the .frag file without the suffix
    # mgr: [ICE::Util::FragmentManager] The fragment manager creating this fragment
    #
    def initialize(fragName, mgr)
      @name = fragName
      @mgr = mgr
    end
    
    ##
    # Parse the .frag file and build internal fragment map
    #
    def parse
      first = true
      @tags = []
      @lines = []
      @timestamp = File.mtime(@name)
      IO.foreach(@name, "<!--Fragment ") do |line|
        line = line[0,line.length-13] if line.index('<!--Fragment')
        unless first 
          j = line.index('-->')
          @tags.push(FragmentTag.new(line[0,j]))
          line = line[j+3, line.length-(j+3)]
        end
        @lines.push(line)
        first = false
      end
    end
    
    ##
    # Compose a composite of the fragment merged with the keys supplied
    #
    # values:: [Hash] The values of the keys {key => value(.to_s)}
    # return:: [String] The composit fragment
    #
    def compose(values)
      parse unless @timestamp==File.mtime(@name)
      result = ""
      @tags.length.times {|i| result+=@lines[i]+@tags[i].fill(values, @mgr.default_attributes)}
      result+=@lines[-1]
      return result
    end
    
    ##
    # Generate a list of keys for this fragment.
    #
    # return:: [Array] Array of keys as Strings.
    #
    def keys
      result = []
      @tags.each {|tag| result.push(tag.key)}
      result
    end
  end #end ICE::Util::Fragment
  

  ##
  # A class representing a parsed fragment tag <tt><!--Fragment key="..."--></tt>
  #
  class FragmentTag
  
    # The key of the fragment
    attr_reader :key
  
    @@pattern = /\s?([\w\-_]+)="([^"]*)"/
    
    ##
    # Initialize the fragment tag from the supplied tag string
    #
    # tag:: [String] The tag (prefix="...", etc) to parse
    #
    def initialize(tag)
      @empty=""
      @suffix_last=true
      while not tag==""
        m = @@pattern.match(tag)
        value = m[2]
        value = true if value=="true"
        value = false if value=="false"
        eval "@#{m[1]}=value"
        tag = m.post_match
      end
    end
    
    ##
    # Composes a key replacment based on the supplied hash
    #
    # values:: [Hash] The values of the keys {key => value(.to_s)}
    # defaults:: [Hash] The default values for keys
    # return :: [String] A composite of the fragment key's value
    #
    def fill(values, defaults)
      return @empty unless (values and values[key]) or (defaults and defaults[key])
      value = values[key] if values
      value = defaults[key] unless value
      if value.kind_of? Array
        result = ""
        value.length.times do |i|
          result += @prefix if @prefix
          result += value[i]
          if @suffix and i!=(value.length-1)
            result += @suffix 
          elsif @suffix and i==(value.length-1) and @suffix_last
            result += @suffix
          end
        end
        return result
      end
      value = value.to_s
      value = @prefix + value if @prefix
      value += @suffix if @suffix
      return value
    end
  end

end 

if __FILE__ == $0
  fm = Ikko::FragmentManager.new
  puts fm["test.frag", {"message"=>"test", "message2"=>["one", "two", "three"]}]
end



