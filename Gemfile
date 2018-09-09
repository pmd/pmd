source 'https://rubygems.org/'

gem 'pmdtester', '~> 1.0.0.pre.beta3'
gem 'danger', '~> 5.6', '>= 5.6'

# This group is only needed for rendering release notes
# this happens during release (.travis/release.sh and do-release.sh)
# but also during regular builds (.travis/build-deploy.sh)
group :release_notes_preprocessing do
  gem 'liquid', '>=4.0.0'
  gem 'safe_yaml', '>=1.0'
end

# vim: syntax=ruby
