#!/usr/bin/env bash

#
# This script should check, that all needed commands are available
# and are in the correct version.
#

source logger.inc

set -e

ruby --version | grep "ruby 2.7" || (log_error "Ruby is missing"; exit 1)
