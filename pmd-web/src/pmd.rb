#
# pmd.rb - This is the basics of what we need
# to represent a project.  
#

module PMD

class Job

  attr_reader :unixName

  def initialize(location, title, unixName, moduleDirectory, sourceDirectory )
    @location = location
    @title = title
    @unixName = unixName
    if (@location == "Sourceforge")
     @cvsroot = ':pserver:anonymous@cvs1:/cvsroot/' + unixName
    else
     @cvsroot = ':pserver:anoncvs@cvs.apache.org:/home/cvspublic'
    end
    @moduleDirectory = moduleDirectory
    @sourceDirectory = sourceDirectory.strip
  end
  
  def checkout_code
  `cvs -Q -d#{@cvsroot} co "#{@moduleDirectory}"`
  end
  
  def run_pmd
   cmd="java -jar pmd-1.0.jar \"#{@sourceDirectory}\" html rulesets/unusedcode.xml > \"#{reportFile()}\""
   `#{cmd}`
   arr = IO.readlines(reportFile())
   newFile=File.open(reportFile(), "w")
   arr.each do | line | 
    if (line["Error while parsing"] == nil) 
     newFile << line
    end
   end
   newFile.close
  end
 
  def reportFile 
   return "/home/groups/p/pm/pmd/htdocs/reports/#{@unixName}_#{@moduleDirectory.sub(" ", "")}.html"
  end
  
  def clear
  `rm -rf "#{@moduleDirectory}"`
  end
  
  def to_s
   return @location + ":" + @title + ":" + @unixName +":"+@moduleDirectory+":"+@sourceDirectory
  end
end


end

