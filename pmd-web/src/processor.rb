#!/usr/local/bin/ruby

require '/home/tom/data/pmd/pmd-web/src/ikko.rb'

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
	attr_reader :unix_name, :mod, :title
	def initialize(location, title, unix_name, mod, src )
		@location = location
		@title = title
		@unix_name = unix_name
		@cvsroot = ':pserver:anonymous@cvs.sourceforge.net:/cvsroot/' + unix_name
		@mod = mod
		@src = src.strip
	end
	def checkout_code
		t = MyThread.new {
			MyThread.ttl = 120 
			`cvs -Q -d#{@cvsroot} export -D tomorrow "#{@src}"`
		}
		t.join  
	end
	def checkOutOK
		File.exists?(@src)
	end
  def run_ncss
   cmd="#{JAVANCSS_BINARY} -ncss -recursive \"#{@src}\" > \"#{ncss_report}\""
   `#{cmd}`
  end
  def run_pmd
   cmd="java -Xmx512m -jar pmd-1.2.2.jar \"" + ROOT + "/" + @src + "\" html rulesets/unusedcode.xml -shortnames > " + reportFile
   `#{cmd}`
   arr = IO.readlines(reportFile())
   newFile=File.open(reportFile(), "w")
   arr.each do | line | 
    if line["Error while parsing"] == nil 
     newFile << line
    end
   end
   newFile.close
  end
  def run_cpd
   cmd="java -Xmx512m -cp pmd-1.2.2.jar net.sourceforge.pmd.cpd.CPD 100 " + @src + " > " + cpdReportFile
   `#{cmd}`
  end
	def copy_up
		`scp #{reportFile} #{cpdReportFile} #{ncss_report} tomcopeland@pmd.sf.net:#{REMOTE_REPORT_DIR}`
	end
	def reportFile 
		"reports/" + @unix_name + "_" + @mod.sub(/ /, '') + ".html"
	end
	def cpdReportFile 
		"reports/cpd_" + @unix_name + "_" + @mod.sub(/ /, '') + ".txt"
	end
	def ncss_report 
		"reports/" + @unix_name + "_" + @mod.sub(/ /, '') + "_ncss.txt"
	end
	def clear
		`rm -rf "#{@mod}" reports/#{reportFile} reports/#{cpdReportFile} reports/#{ncss_report}`
	end
	def homepage_html
		"<a href=\"http://" + @unix_name + ".sf.net/\">http://" + @unix_name + ".sf.net/</a>"
	end
	def ncss
		File.read(ncss_report).split(":")[1].strip.chomp	
	end	
	def pmd_html
		
	end
	def pmd_lines
    count = 0
    File.read(reportFile).each {|line| count += 1 if line["</td>"] }
   	count = count == 0 ? 0 : (count/4).to_i
		return count
	end
	def to_s
		return @location + ":" + @title + ":" + @unix_name +":"+@mod+":"+@src
	end
end

if __FILE__ == $0
	Dir.chdir(Job::ROOT)
	ENV['JAVA_HOME']="/usr/local/java"
	ENV['PATH']="#{ENV['PATH']}:#{ENV['JAVA_HOME']}/bin"
	jobs = []
	File.read("jobs.txt").each_line {|jobtext|
		job = Job.new(*jobtext.split(":"))
		if ARGV.length != 0 && ARGV[0] != job.mod
			next
		end
		jobs << job
		puts "Processing #{job}"
		next
		job.checkout_code
		if job.checkOutOK
			job.run_pmd
			job.run_cpd
			job.run_ncss
			job.copy_up
			job.clear
		end
	}
	fm = Ikko::FragmentManager.new
	fm.base_path="./"
	out = fm["header.frag", {"lastruntime"=>Time.now}]
	jobs.each {|j|
		out << fm["row.frag", {	
			"title"=>fm["title.frag", {"file"=>j.reportFile, "title"=>j.title}],
			"homepage"=>fm["homepage.frag", {"name"=>j.unix_name}],
			"ncss"=>j.ncss, 
			"pmd"=>j.pmd_lines.to_s
		}]	
	}
	File.open("index.html", "w") {|f| f.syswrite(out)}
end

