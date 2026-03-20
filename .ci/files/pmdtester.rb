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
   '--debug',
   ]
end

@summary_file = 'target/reports/diff/summary.txt'
@conclusion_file = 'target/reports/diff/conclusion.txt'

def run_pmdtester
  Dir.chdir('..') do
    begin
      @base_branch = ENV['PMD_CI_BRANCH']
      @logger.info "Run against PR base #{@base_branch}"
      @summary = PmdTester::Runner.new(get_args(@base_branch)).run

      unless File.exist?(@summary_file) && File.exist?(@conclusion_file)
        write_error_result
        @logger.error "Running pmdtester failed: summary or conclusion file not found."
      end

    rescue StandardError => e
      write_error_result
      @logger.error "Running pmdtester failed: #{e.inspect}\n\n#{e.backtrace.join("\n")}"
    end
  end
end

def write_error_result
  unless Dir.exist?('target/reports/diff')
    Dir.mkdir('target/reports/diff')
  end

  message = "⚠️ Running pmdtester failed, this message is mainly used to remind the maintainers of PMD."
  File.write(@summary_file, message)
  @logger.info "Wrote summary file #{@summary_file}:\n\n#{message}"

  conclusion = "failure"
  File.write(@conclusion_file, conclusion)
  @logger.info "Wrote conclusion file #{@conclusion_file}: #{conclusion}"
end

# Perform regression testing
run_pmdtester

# vim: syntax=ruby
