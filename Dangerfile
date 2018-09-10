require 'pmdtester'
require 'time'
require 'logger'

@logger = Logger.new(STDOUT)

def run_pmdtester
  Dir.chdir('..') do
    argv = ['-r', './pmd', '-b', "#{ENV['TRAVIS_BRANCH']}", '-p', 'FETCH_HEAD', '-m', 'online', '-a']
    Process.fork do
	  begin
        runner = PmdTester::Runner.new(argv)
        @new_errors, @removed_errors, @new_violations, @removed_violations = runner.run
	  rescue StandardError => e
	    warn("Running pmdtester failed, this message is mainly used to remind the maintainers of PMD.")
	    @logger.error "Running pmdtester failed: #{e.inspect}"
	    exit 1
	  end
	end
	Process.wait

    upload_report if $?.success?
  end
end

def upload_report
  Dir.chdir('target/reports') do
    tar_filename = "pr-#{ENV['TRAVIS_PULL_REQUEST']}-diff-report-#{Time.now.strftime("%Y-%m-%dT%H-%M-%SZ")}.tar"
    unless Dir.exist?('diff/')
      message("No java rules are changed!", sticky: true)
      return
    end

    `tar -cf #{tar_filename} diff/`
	report_url = `curl -u #{ENV['CHUNK_TOKEN']} -T #{tar_filename} chunk.io`
	if $?.success?
	  @logger.info "Successfully uploaded #{tar_filename} to chunk.io"

      # set value of sticky to true and the message is kept after new commits are submited to the PR
	  message(build_diff_summary_msg(report_url), sticky: true)
	else
      @logger.error "Error while uploading #{tar_filename} to chunk.io: #{report_url}"
      warn("Uploading the diff report failed, this message is mainly used to remind the maintainers of PMD.")
    end
  end
end

def build_diff_summary_msg(report_url)
  diff_summary_msg = <<-MSG
    The regression report summary:

    |   | Introduced size | Removed size |
    | - | - | - |
    | Violations | #{@new_violations} | #{@removed_violations} |
    | Errors | #{@new_errors} | #{@removed_errors} |

    [Full report](#{report_url.chomp}/diff/index.html)
  MSG
end

# Perform regression testing
can_merge = github.pr_json['mergeable']
if can_merge
  run_pmdtester
else
  warn("This PR cannot be merged yet.", sticky: false)
end

# vim: syntax=ruby
