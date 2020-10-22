require 'pmdtester'
require 'time'
require 'logger'
require 'fileutils'

@logger = Logger.new(STDOUT)

def get_args(base_branch)
  ['--local-git-repo', './pmd',
   '--base-branch', base_branch,
   '--patch-branch', 'HEAD',
   '--patch-config', './pmd/.travis/all-java.xml',
   '--mode', 'online',
   '--auto-gen-config',
   '--keep-reports',
   # '--debug',
   ]
end

def run_pmdtester
  Dir.chdir('..') do
    begin
      @base_branch = ENV['TRAVIS_BRANCH']
      @logger.info "Run against PR base #{@base_branch}"
      runner = PmdTester::Runner.new(get_args(@base_branch))
      @new_errors, @removed_errors, @new_violations, @removed_violations, @new_configerrors, @removed_configerrors = runner.run

      unless Dir.exist?('target/reports/diff')
        message("No java rules are changed!", sticky: true)
        return
      end

      # move the generated report out of the way
      FileUtils.mv 'target/reports/diff', 'target/diff1'
      message1 = create_message

      # run against master branch (if the PR is not already against master)
      unless ENV['TRAVIS_BRANCH'] == 'master'
        @base_branch = 'master'
        @logger.info "Run against #{@base_branch}"
        runner = PmdTester::Runner.new(get_args(@base_branch))
        @new_errors, @removed_errors, @new_violations, @removed_violations, @new_configerrors, @removed_configerrors = runner.run
        # move the generated report out of the way
        FileUtils.mv 'target/reports/diff', 'target/diff2'
        message2 = create_message
      end

      report_url = upload_report

      if report_url
        message1 += "[Full report](#{report_url}/diff1/index.html)"
        # set value of sticky to true and the message is kept after new commits are submitted to the PR
        message(message1, sticky: true)

        if message2
          message2 += "[Full report](#{report_url}/diff2/index.html)"
          # set value of sticky to true and the message is kept after new commits are submitted to the PR
          message(message2, sticky: true)
        end
      end

    rescue StandardError => e
      warn("Running pmdtester failed, this message is mainly used to remind the maintainers of PMD.")
      @logger.error "Running pmdtester failed: #{e.inspect}"
    end
  end
end

def create_message
  "Compared to #{@base_branch}:\n"\
  "This changeset introduces "\
  "#{@new_violations} new violations, #{@new_errors} new errors and "\
  "#{@new_configerrors} new configuration errors,\n"\
  "removes #{@removed_violations} violations, #{@removed_errors} errors and "\
  "#{@removed_configerrors} configuration errors.\n"
end

def upload_report
  Dir.chdir('target') do
    tar_filename = "pr-#{ENV['TRAVIS_PULL_REQUEST']}-diff-report-#{Time.now.strftime("%Y-%m-%dT%H-%M-%SZ")}.tar"

    `tar -cf #{tar_filename} diff1/ diff2/`
    report_url = `curl -u #{ENV['CHUNK_TOKEN']} -T #{tar_filename} https://chunk.io`
    if $?.success?
      @logger.info "Successfully uploaded #{tar_filename} to chunk.io"
      report_url.chomp
    else
      @logger.error "Error while uploading #{tar_filename} to chunk.io: #{report_url}"
      warn("Uploading the diff report failed, this message is mainly used to remind the maintainers of PMD.")
      nil
    end
  end
end

# Perform regression testing
run_pmdtester

# vim: syntax=ruby
