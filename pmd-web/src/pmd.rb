#
# pmd.rb - This is the basics of what we need
# to represent a project.  
#
module PMD

# add timeout thingy to the Thread class, thx to Rich Kilmer for the code
class MyThread < Thread
 def MyThread.ttl=(timeout)
  Thread.new(Thread.current) do |thread|
   sleep timeout
   if thread.alive?
    thread.exit
   end
  end
 end
end

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
   # note that we just export the source directory - we don't need all the other stuff
   # this saves time/bandwidth/processing load.  All good.
   #
   # use the Thread.ttl gizmo to end it after 2 minutes
   t = MyThread.new {
    MyThread.ttl = 120 
    `cvs -Q -d#{@cvsroot} export -D tomorrow "#{@sourceDirectory}"`
   }
   t.join  
  end
	def checkOutOK
		return File.exists?(@sourceDirectory)
	end
  def ncss
   cmd="/home/users/t/to/tomcopeland/javancss/javancss18.38/bin/javancss -ncss -recursive \"#{@sourceDirectory}\" > \"#{ncssReportFile}\""
   `#{cmd}`
  end
  def run_pmd
   cmd="java -jar pmd-1.1.jar \"#{@sourceDirectory}\" html rulesets/unusedcode.xml > \"#{reportFile}\""
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
  def run_cpd
   cmd="java -cp pmd-1.1.jar net.sourceforge.pmd.cpd.CPD 100 \"#{@sourceDirectory}\"  > \"#{cpdReportFile}\""
   `#{cmd}`
  end
  def reportFile 
   return "/home/groups/p/pm/pmd/htdocs/reports/#{@unixName}_#{@moduleDirectory.sub(" ", "")}.html"
  end
  def cpdReportFile 
   return "/home/groups/p/pm/pmd/htdocs/reports/cpd_#{@unixName}_#{@moduleDirectory.sub(" ", "")}.txt"
  end
  def ncssReportFile 
   return "/home/groups/p/pm/pmd/htdocs/reports/#{@unixName}_#{@moduleDirectory.sub(" ", "")}_ncss.txt"
  end
  def clear
  `rm -rf "#{@moduleDirectory}"`
  end
  def to_s
   return @location + ":" + @title + ":" + @unixName +":"+@moduleDirectory+":"+@sourceDirectory
  end
end
end

