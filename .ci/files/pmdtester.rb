require 'pmdtester'
require 'time'
require 'logger'
require 'fileutils'
require 'etc'

@logger = Logger.new(STDOUT)

def get_args(base_branch, autogen = true, patch_config = './pmd/.ci/files/all-regression-rules.xml')
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
   '--threads', Etc.nprocessors.to_s,
   # '--debug',
   ]
end

def run_pmdtester
  Dir.chdir('..') do
    begin
      @base_branch = ENV['PMD_CI_BRANCH']
      @logger.info "Run against PR base #{@base_branch}"
      @summary = PmdTester::Runner.new(get_args(@base_branch)).run

      if Dir.exist?('target/reports/diff')
        message = create_message
        conclusion = determine_conclusion
      else
        message = "No regression tested rules have been changed."
        conclusion = "skipped"
      end

    rescue StandardError => e
      message = "⚠️ Running pmdtester failed, this message is mainly used to remind the maintainers of PMD."
      conclusion = "failure"
      @logger.error "Running pmdtester failed: #{e.inspect}"
    end

    unless Dir.exist?('target/reports/diff')
      Dir.mkdir('target/reports/diff')
    end
    summary_file = 'target/reports/diff/summary.txt'
    File.write(summary_file, message)
    @logger.info "Wrote summary file #{summary_file}:\n\n#{message}"
    conclusion_file = 'target/reports/diff/conclusion.txt'
    File.write(conclusion_file, conclusion)
    @logger.info "Wrote conclusion file #{conclusion_file}: #{conclusion}"
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

def determine_conclusion
  total_changes = @summary[:violations][:changed]
    + @summary[:violations][:new]
    + @summary[:violations][:removed]
    + @summary[:errors][:new]
    + @summary[:configerrors][:new]
  if total_changes > 0
    "neutral"
  else
    "success"
  end
end

# Perform regression testing
run_pmdtester

# vim: syntax=ruby
