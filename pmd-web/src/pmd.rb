#
# pmd.rb - This is the basics of what we need
# to represent a project.  
#

module PMD

class Project
  def initialize( name, source )
    @name = name
    @source = source
  end

  def updateSource
  end

  def runAnt
    `/usr/local/bin/ant -f pmd-web.xml -Dproject.name=#{@name} -Dproject.source=#{@source} -Dlib.repo=/home/dpeugh/projects/lib-repo`
  end
end

class CVSProject < Project
  def initialize( name, cvsroot, mod, source )
    @name = name 
    @cvsroot = cvsroot
    @mod = mod
    @source = name + '/' + mod + '/' + source
  end

  def updateSource
    `rm -rf #{@name}`
    `mkdir #{@name}`
    `cd #{@name}; cvs -Q -r -d#{@cvsroot} co #{@mod}`
  end
end

class SFProject < CVSProject
  def initialize( name, mod, source )
    @name = name
    @cvsroot = ':pserver:anonymous@cvs.' + name + '.sourceforge.net:/cvsroot/' + name
    @mod = mod
    @source = name + '/' + mod + '/' + source
  end
end


class Job
  def initialize(title, unixName, moduleDirectory, sourceDirectory )
    @title = title
    @unixName = unixName
    @cvsroot = ':pserver:anonymous@cvs.' + unixName + '.sourceforge.net:/cvsroot/' + unixName
    @moduleDirectory = moduleDirectory
    @sourceDirectory = sourceDirectory.strip
  end
  
  def checkout_code
  `cvs -Q -d#{@cvsroot} co #{@moduleDirectory}`
  end
  
  def run_pmd
   cmd="java -jar pmd-1.0rc2.jar #{@sourceDirectory} html rulesets/unusedcode.xml > ../htdocs/reports/#{@unixName}_#{@moduleDirectory}.html"
   `#{cmd}`
  end
  
  def clear
  `rm -rf #{@moduleDirectory}`
  end
  
  def to_s
   return @title + ":" + @unixName +":"+@moduleDirectory+":"+@sourceDirectory
  end
end


end

