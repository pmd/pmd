source 'https://rubygems.org/'

# bleeding edge from git
#gem 'pmdtester', :git => 'https://github.com/pmd/pmd-regression-tester.git'

gem 'pmdtester', '~> 1.0'
gem 'danger', '~> 5.6', '>= 5.6'

# This group is only needed for rendering release notes
# this happens during release (.travis/release.sh and do-release.sh)
# but also during regular builds (.travis/build-deploy.sh)
group :release_notes_preprocessing do
  gem 'liquid', '>=4.0.0'
  gem 'safe_yaml', '>=1.0'
  gem 'rouge', '>= 1.7', '< 4'
end

# vim: syntax=ruby
