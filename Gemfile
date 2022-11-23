source 'https://rubygems.org/'

# bleeding edge from git
gem 'pmdtester', :git => 'https://github.com/pmd/pmd-regression-tester.git', branch: 'master'

#gem 'pmdtester'
gem 'danger'

# This group is only needed for rendering release notes (docs/render_release_notes.rb)
# this happens during release (.ci/build.sh and do-release.sh)
# but also during regular builds (.ci/build.sh)
group :release_notes_preprocessing do
  gem 'liquid'
  gem 'safe_yaml'
  gem 'rouge'
end

# vim: syntax=ruby
