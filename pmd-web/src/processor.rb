#!/usr/local/bin/ruby

require 'rubygems'
require_gem 'ikko'
require 'yaml'
require '/home/tom/rubyforge/ruby-doom/lib/doom.rb'

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

class PMDMap
	MIN_NOOKS = 2
	MAX_NOOKS = 12
	def initialize(problems)
		@problems = problems
	end
	def nooks
		if @problems < MIN_NOOKS
			return MIN_NOOKS
		elsif @problems > 100
			return MAX_NOOKS
		end
		(@problems/10).to_i + MIN_NOOKS
	end
end

class Job
	JAVANCSS_BINARY="/usr/local/javancss/bin/javancss"
	ROOT="/home/tom/pmd/pmd-web/src"
	REMOTE_REPORT_DIR="/home/groups/p/pm/pmd/htdocs/reports/"
	attr_reader :unix_name, :mod, :title, :src
	attr_accessor :barrels
	def initialize(title, unix_name, mod, src, cvsroot)
		@title = title
		@unix_name = unix_name
		@mod = mod
		@src = src.strip
		@cvsroot = cvsroot
	end
	def checkout_code
		t = MyThread.new {
			MyThread.ttl = 120 
			cmd = "cvs -Q -d#{@cvsroot} export -D tomorrow \"#{@src}\""
      puts "running cmd: #{cmd}"
			`#{cmd}`
		}
		t.join  
	end
  def run_ncss
   cmd="#{JAVANCSS_BINARY} -ncss -recursive \"#{@src}\" > \"#{ncss_report}\""
   `#{cmd}`
  end
  def run_pmd
		cmd="java -Xmx512m -cp /home/tom/pmd/pmd/lib/jaxen-1.1-beta-7.jar:/home/tom/pmd/pmd-web/src/pmd-3.3.jar net.sourceforge.pmd.PMD \"#{ROOT}/#{@src}\" html unusedcode -shortnames > #{report}"
   `#{cmd}`
   arr = IO.readlines(report)
   File.read(report) {|f|
	   arr.each {|x| f << x if x =~ /Error while parsing/ }
		}
  end
  def run_cpd
   cmd="java -Xmx512m -cp /home/tom/pmd/pmd/lib/jaxen-1.1-beta-7.jar:/home/tom/pmd/pmd-web/src/pmd-3.3.jar net.sourceforge.pmd.cpd.CPD 100 " + @src + " > " + cpd_file
   `#{cmd}`
  end
	def copy_up
		`scp #{wad} #{report} #{cpd_file} #{ncss_report} tomcopeland@pmd.sf.net:#{REMOTE_REPORT_DIR}`
	end
	def report 
		"reports/" + @unix_name + "_" + @mod.sub(/ /, '') + ".html"
	end
	def wad 
		"reports/" + @unix_name + "_" + @mod.sub(/ /, '') + ".wad"
	end
	def cpd_file 
		"reports/cpd_" + @unix_name + "_" + @mod.sub(/ /, '') + ".txt"
	end
	def ncss_report 
		"reports/" + @unix_name + "_" + @mod.sub(/ /, '') + "_ncss.txt"
	end
	def clear
		`rm -rf "#{@mod}" reports/#{report} reports/#{cpd_file} reports/#{ncss_report}`
	end
	def homepage_html
		"<a href=\"http://" + @unix_name + ".sf.net/\">http://" + @unix_name + ".sf.net/</a>"
	end
	def ncss
		return 0 if File.size(ncss_report) < 5
		File.read(ncss_report).split(":")[1].strip.chomp	
	end
	def cpd_lines
    count = 0
    File.read(cpd_file).each {|line| count += 1 if line["================================="] }
		count
	end	
	def pmd_lines
		count = 0
    File.read(report).each {|line| count += 1 if line["</td>"] } unless !File.exists?(report)
   	count == 0 ? 0 : (count/4).to_i
	end
	def pctg
		sprintf("%.2f", (pmd_lines.to_f/(ncss == 0 ? 1 : ncss.to_i))*100)
	end
	def color
    if pctg.to_f < 0.2 
			"#00ff00"
		elsif pctg.to_f < 0.8
			"yellow"
		else
			"red"
		end
	end
	def to_s
		@title + ":" + @unix_name + ":" + @mod + ":" + @src
	end
end

class PreviousTracker
	def initialize(jobs)
		@jobs = jobs
		@counter = 0
	end
	def bump
		@counter += 1
	end
	def anchor_idx
		@counter
	end
	def previous_anchor_idx
		if (@counter + 2) > (@jobs.size-1)
			return @jobs.size - 1
		end
		@counter + 2
	end
end

if __FILE__ == $0
  puts "Starting at #{Time.now}"
	Dir.chdir(Job::ROOT)
	ENV['JAVA_HOME']="/usr/local/java"
	ENV['PATH']="#{ENV['PATH']}:#{ENV['JAVA_HOME']}/bin"
	jobs = []
  tree = YAML.load(File.open("jobs.yaml"))
  tree.keys.each {|key|
    jobs << Job.new(key, tree[key]["unix_name"], tree[key]["module"], tree[key]["srcdir"], tree[key]["cvsroot"])
  }
  
	if ARGV.include?("-build") 
		jobs.each do |job|
			if ARGV.include?("-job") && job.mod != ARGV.at(ARGV.index("-job")+1)
				puts "Skipping " + job.mod
				next
			end
			puts "Processing " + job.unix_name
			job.checkout_code
			if File.exists?(job.src) 
				if Dir.glob("#{job.src}/**/*.java").empty?
					puts "Skipping #{job} because no source files got checked out"
				else
					job.run_pmd
					job.run_cpd
					job.run_ncss
					job.copy_up
					job.clear
				end
			end
		end
	end

	jobs.sort! {|x,y| 
		if !File.exists?(x.ncss_report) || !File.exists?(y.ncss_report)
			-1
		else
			x.pctg <=> y.pctg 
		end
	}

	if ARGV.include?("-doom")
		jobs.each do |j|
			begin
				pmd = PMDMap.new(j.pmd_lines)
				p = Path.new(0, 1000)
				p.add("e200/n200/e200/s200/e200/", pmd.nooks)
				p.add("s400/")
				p.add("w200/s200/w200/n200/w200/", pmd.nooks)
				p.add("n400/")
				m = SimpleLineMap.new(p)
				m.set_player(Point.new(50,900))
				0.upto(pmd.nooks-1) do |x|
		        m.add_barrel(Point.new((x*600)+300, 1100))
		        m.add_barrel(Point.new((x*600)+300, 500))
				end
				j.barrels = pmd.nooks * 2
				m.create_wad(j.wad + ".tmp")
				cmd = "./bsp " + j.wad + ".tmp -o " + j.wad + " && rm -f " + j.wad + ".tmp"
				`#{cmd}`
			rescue 
			end
		end
	end
	
	jobs.each {|j| j.copy_up } if ARGV.include?("-copy")

	fm = Ikko::FragmentManager.new
	fm.base_path="./"
	pt = PreviousTracker.new(jobs)
	out = fm["header.frag", {"lastruntime"=>Time.now, "prev2header1"=>jobs[0].unix_name, "prev2header2"=>jobs[1].unix_name}]
	jobs.each {|j|
		out << fm["row.frag", {	
			"title"=>fm["title.frag", {"file"=>j.report, "title"=>j.title}],
			"homepage"=>fm["homepage.frag", {"name"=>j.unix_name}],
			"ncss"=>j.ncss, 
			"unix_name"=>j.unix_name, 
			"anchor"=>jobs[pt.anchor_idx].unix_name, 
			"previous_anchor"=>jobs[pt.previous_anchor_idx].unix_name, 
			"pmd"=>j.pmd_lines.to_s,
			"pctg"=>fm["pctg.frag", {"color"=>j.color, "pctg"=>j.pctg.to_s}], 
			"dupe"=>fm["cpd.frag", {"file"=>j.cpd_file, "dupes"=>j.cpd_lines.to_s}],
			"doom"=>fm["doom.frag", {"file"=>j.wad, "barrels"=>j.barrels}]
		}] unless !File.exists?(j.report) || !File.exists?(j.ncss_report) || j.ncss == 0
		pt.bump
	}
	File.open("scoreboard.html", "w") {|f| f.syswrite(out)}

	`scp scoreboard.html tomcopeland@pmd.sf.net:/home/groups/p/pm/pmd/htdocs/`

  puts "Done at #{Time.now}"
end

