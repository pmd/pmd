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

	JAVANCSS_BINARY="/usr/local/javancss/bin/javancss"
	ROOT="/home/tom/data/pmd/pmd-web/src"
	REMOTE_REPORT_DIR="/home/groups/p/pm/pmd/htdocs/reports/"
	REMOTE_CGI_DIR="/home/groups/p/pm/pmd/cgi-bin/"

	attr_reader :unixName
	def initialize(location, title, unixName, moduleDirectory, sourceDirectory )
		@location = location
		@title = title
		@unixName = unixName
		@cvsroot = ':pserver:anonymous@cvs.sourceforge.net:/cvsroot/' + unixName
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
   cmd="#{JAVANCSS_BINARY} -ncss -recursive \"#{@sourceDirectory}\" > \"#{ncssReportFile}\""
   `#{cmd}`
  end
  def run_pmd
   cmd="java -Xmx512m -jar pmd-1.2.2.jar \"#{ROOT}/#{@sourceDirectory}\" html rulesets/unusedcode.xml -shortnames > \"#{reportFile}\""
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
   cmd="java -Xmx512m -cp pmd-1.2.2.jar net.sourceforge.pmd.cpd.CPD 100 \"#{@sourceDirectory}\"  > \"#{cpdReportFile}\""
   `#{cmd}`
  end
	def copy_up
		`scp #{ROOT}/#{reportFile} #{ROOT}/#{cpdReportFile} #{ROOT}/#{ncssReportFile} tomcopeland@pmd.sf.net:#{REMOTE_REPORT_DIR}`
		if File.exists?("lastruntime.txt")
			`scp #{ROOT}/lastruntime.txt tomcopeland@pmd.sf.net:#{REMOTE_CGI_DIR}`
		end
	end
	def reportFile 
		return "#{@unixName}_#{@moduleDirectory.sub(/ /, '')}.html"
	end
	def cpdReportFile 
		return "cpd_#{@unixName}_#{@moduleDirectory.sub(/ /, '')}.txt"
	end
	def ncssReportFile 
		return "#{@unixName}_#{@moduleDirectory.sub(/ /, '')}_ncss.txt"
	end
	def clear
		`rm -rf "#{ROOT}/#{@moduleDirectory}" #{ROOT}/#{reportFile} #{ROOT}/#{cpdReportFile} #{ROOT}/#{ncssReportFile}`
		if File.exists?("#{ROOT}/lastruntime.txt")
			`rm -rf #{ROOT}/lastruntime.txt`
		end
	end
	def to_s
		return @location + ":" + @title + ":" + @unixName +":"+@moduleDirectory+":"+@sourceDirectory
	end
end
end
