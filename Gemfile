source 'https://rubygems.org/'

# bleeding edge from git
#gem 'pmdtester', :git => 'https://github.com/pmd/pmd-regression-tester.git', branch: 'master'

gem 'pmdtester', '~> 1'
gem 'danger', '~> 5.6', '>= 5.6'

# This group is only needed for rendering release notes (docs/render_release_notes.rb)
# this happens during release (.ci/build.sh and do-release.sh)
# but also during regular builds (.ci/build.sh)
group :release_notes_preprocessing do
  gem 'liquid', '>=4.0.0'
  gem 'safe_yaml', '>=1.0'
  gem 'rouge', '>= 1.7', '< 4'
end

# vim: syntax=ruby
