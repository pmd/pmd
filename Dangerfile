require 'pmdtester'
require 'time'
require 'logger'
require 'fileutils'

@logger = Logger.new(STDOUT)

def get_args(base_branch, autogen = TRUE, patch_config = './pmd/.ci/files/all-java.xml')
  ['--local-git-repo', './pmd',
   '--list-of-project', './pmd/.ci/files/project-list.xml',
   '--base-branch', base_branch,
   '--patch-branch', 'HEAD',
   '--patch-config', patch_config,
   '--mode', 'online',
   autogen ? '--auto-gen-config' : '--filter-with-patch-config',
   '--keep-reports',
   '--error-recovery',
   '--baseline-download-url', 'https://pmd-code.org/pmd-regression-tester/',
   # '--debug',
   ]
end

def run_pmdtester
  Dir.chdir('..') do
    begin
      @base_branch = ENV['PMD_CI_BRANCH']
      @logger.info "\n\n--------------------------------------"
      @logger.info "Run against PR base #{@base_branch}"
      @summary = PmdTester::Runner.new(get_args(@base_branch)).run

      unless Dir.exist?('target/reports/diff')
        message("No java rules are changed!", sticky: true)
        return
      end

      # move the generated report out of the way
      FileUtils.mv 'target/reports/diff', 'target/diff1'
      message1 = create_message

      # run against master branch (if the PR is not already against master)
      unless ENV['PMD_CI_BRANCH'] == 'master'
        @base_branch = 'master'
        @logger.info "\n\n--------------------------------------"
        @logger.info "Run against #{@base_branch}"
        @summary = PmdTester::Runner.new(get_args(@base_branch, FALSE, 'target/diff1/patch_config.xml')).run

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
  "This changeset " \
  "changes #{@summary[:violations][:changed]} violations,\n" \
  "introduces #{@summary[:violations][:new]} new violations, " \
  "#{@summary[:errors][:new]} new errors and " \
  "#{@summary[:configerrors][:new]} new configuration errors,\n" \
  "removes #{@summary[:violations][:removed]} violations, "\
  "#{@summary[:errors][:removed]} errors and " \
  "#{@summary[:configerrors][:removed]} configuration errors.\n"
end

def upload_report
  Dir.chdir('target') do
    tar_filename = "pr-#{ENV['PMD_CI_PULL_REQUEST_NUMBER']}-diff-report-#{Time.now.strftime("%Y-%m-%dT%H-%M-%SZ")}.tar"

    `tar -cf #{tar_filename} diff1/ diff2/`
    @logger.info "Uploading file #{tar_filename} now..."
    report_url = `curl -u #{ENV['PMD_CI_CHUNK_TOKEN']} -T #{tar_filename} https://chunk.io`
    if $?.success?
      report_url.chomp!
      @logger.info "Successfully uploaded #{tar_filename} to #{report_url}"
      report_url
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
