
require 'c:\data\pmd\pmd-web\src\pmd.rb'

jobsDir = Dir.new("jobs")
jobsDir.each { |candidate| 
 begin 	
  if candidate[".txt"] 
   name,moduleDir,srcDir = File.new("jobs/#{candidate}").read.split(":") 
   job = PMD::Job.new(name,moduleDir,srcDir)
   job.checkout_code
   job.run_pmd
  end
 rescue
  puts "Exiting with error: #{$!}"
 end
}


