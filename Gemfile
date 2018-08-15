source 'https://rubygems.org/'

gem 'pmdtester', '~> 1.0.0.pre.beta3'
gem 'danger', '~> 5.6', '>= 5.6'

# I think we could skip installing these if we're not a release build
# https://docs.travis-ci.com/user/languages/ruby/#speeding-up-your-build-by-excluding-non-essential-dependencies
# I don't know where to put that in .travis.yml though...
group :release_notes_preprocessing do
  gem 'liquid', '>=4.0.0'
  gem 'safe_yaml', '>=1.0'
end

# vim: syntax=ruby
