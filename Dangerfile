require 'pmdtester'
require 'time'
require 'logger'

@logger = Logger.new(STDOUT)

def run_pmdtester
  Dir.chdir('..') do
    branch_name = "#{ENV['PMD_CI_BRANCH']}"
    argv = ['--local-git-repo', './pmd',
            '--list-of-project', './pmd/.ci/files/project-list.xml',
            '--base-branch', branch_name,
            '--patch-branch', 'HEAD',
            '--patch-config', './pmd/.ci/files/all-java.xml',
            '--mode', 'online',
            '--auto-gen-config',
            '--error-recovery',
            '--baseline-download-url', 'https://pmd-code.org/pmd-regression-tester/',
            # '--debug',
            ]
    begin
      @summary = PmdTester::Runner.new(argv).run
      upload_report
    rescue StandardError => e
      warn("Running pmdtester failed, this message is mainly used to remind the maintainers of PMD.")
      @logger.error "Running pmdtester failed: #{e.inspect}"
    end
  end
end

def upload_report
  Dir.chdir('target/reports') do
    tar_filename = "pr-#{ENV['PMD_CI_PULL_REQUEST_NUMBER']}-diff-report-#{Time.now.strftime("%Y-%m-%dT%H-%M-%SZ")}.tar"
    unless Dir.exist?('diff/')
      message("No java rules are changed!", sticky: true)
      return
    end

    `tar -cf #{tar_filename} diff/`
    report_url = `curl -u #{ENV['PMD_CI_CHUNK_TOKEN']} -T #{tar_filename} https://chunk.io`
    if $?.success?
      @logger.info "Successfully uploaded #{tar_filename} to #{report_url}"

      # set value of sticky to true and the message is kept after new commits are submitted to the PR
      message("This changeset " \
              "changes #{@summary[:violations][:changed]} violations,\n" \
              "introduces #{@summary[:violations][:new]} new violations, " \
              "#{@summary[:errors][:new]} new errors and " \
              "#{@summary[:configerrors][:new]} new configuration errors,\n" \
              "removes #{@summary[:violations][:removed]} violations, "\
              "#{@summary[:errors][:removed]} errors and " \
              "#{@summary[:configerrors][:removed]} configuration errors.\n" \
              "[Full report](#{report_url.chomp}/diff/index.html)", sticky: true)
    else
      @logger.error "Error while uploading #{tar_filename} to chunk.io: #{report_url}"
      warn("Uploading the diff report failed, this message is mainly used to remind the maintainers of PMD.")
    end
  end
end

# Perform regression testing
run_pmdtester

# vim: syntax=ruby
