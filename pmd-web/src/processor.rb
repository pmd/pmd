
require 'c:\data\pmd\pmd-web\src\pmd.rb'

jobsDir = Dir.new("jobs")
jobsDir.each { |candidate| 
 if candidate[".txt"] 
  jobFile=File.new("jobs/#{candidate}")
  jobData = jobFile.read
  jobFile.close
  name,moduleDir,srcDir=jobData.split(":")
  job = PMD::Job.new(name, moduleDir, srcDir)
  puts job
 end
}


