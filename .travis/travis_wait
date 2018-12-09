#!/usr/bin/env bash

# The MIT License (MIT)

# Copyright (c) 2015
# m3t (96bd6c8bb869fe632b3650fb7156c797ef8c2a055d31dde634565f3edda485b) <mlt [at] posteo [dot] de>

# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:

# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

# Available from           https://github.com/m3t/travis_wait
# Please report bugs at    https://github.com/m3t/travis_wait/issues

# Coding (Style) Guidelines:
# https://www.chromium.org/chromium-os/shell-style-guidelines
# http://mywiki.wooledge.org/BashGuide/Practices
# http://wiki.bash-hackers.org/scripting/style


# bash available?
if [ -z "$BASH_VERSINFO" ]; then
  echo "Please make sure you're using bash!"
  exit 1
fi


# INITIALIZE CONSTANTS AND GLOBALS
# Only lower case, esp. for export!
# That ensures that system vars stay untouched in any case
readonly prog_name=$(basename "$0")


is_writeable() {
  local var="$1"

  is_writeable_empty "${var}" 0
}

is_writeable_empty() {
  local var="$1"
  local empty="$2"
  [[ -z "${empty}" ]] && empty=1

  # http://mywiki.wooledge.org/BashGuide/TestsAndConditionals
  # "touch" creates file, if it doesn't exist,
  # so further tests won't fail at the beginning
  if { touch -a "${var}" >/dev/null 2>&1; }; then
    if [[ ! -s "${var}" ]]; then
      if [[ ! -w "${var}" ]]; then
        #show_warning "${var} is not writeable"
        return 1
      fi
    else
      #show_warning "${var} is not empty"
      [[ "${empty}" -eq 1 ]] && return 1
    fi
  else
    #show_warning "Destination for ${var} is not accessible at all"
    return 1
  fi

  return 0
}

is_number() {
  local int="$1"
  # http://mywiki.wooledge.org/BashFAQ/054
  [[ "$int" != *[!0-9]* ]]
}

is_empty() {
  local var="$1"

  [[ -z "$var" ]]
}

show_error() {
  printf "\n%s\n" "${prog_name}: error: $*" >&2
  exit 1
}

show_warning() {
  printf "\n%s\n" "${prog_name}: $*" >&2
}

show_help() {
  # http://wiki.bash-hackers.org/syntax/redirection#here_documents
  cat <<- EOF

  Usage: ${prog_name} [options] <command> [<logfile>]

  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
  incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
  quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo
  consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse
  cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat
  non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

  Arguments:
    <command>       Slowpoke command
    <logfile>       Where <command>'s output will be saved
                    Default: \$RANDOM-output.log

  Options:
    -i, --interval <int>   Refresh interval in sec.
                           Default: 30

    -l, --limit <int>      Limit execution time in sec.
                           Default: 0 (Off)

    -x, --exit-code <int>  Force the exit code
                           Default: -1 (Off)

    -a, --append <int>     PRN append output to existing logfile
                           Off: 0 (Default)
                           On:  1

    -h                          This help screen


  Copyright (C) 2015 m3t
  The MIT License (MIT)

EOF

  exit 0
}

cleanup() {
  kill -0 ${pid_slowpoke} >/dev/null 2>&1 && kill ${pid_slowpoke} >/dev/null 2>&1
}

main() {

  # INITIALIZE LOCAL VARIABLES
  # Variables to be evaluated as shell arithmetic should be initialized
  # to a default or validated beforehand.
  # CAUTION: Arguments' (not options) default values will be overwritten here anyway
  #          So they are set in VALIDATE INPUT
  local i=0
  local msg=""
  local time_passed=0
  local pid_slowpoke=0
  local exit_slowpoke=0
  # Options:
  local interval=30
  local time_limit=0
  local exit_force=-1
  local append=0
  # Arguments:
  local cmd_slowpoke=""
  local file_log=""


  # SIGNAL HANDLING
  # http://mywiki.wooledge.org/SignalTrap
  # http://mywiki.wooledge.org/SignalTrap#Special_Note_On_SIGINT
  trap 'cleanup; trap - INT; kill -INT $$' INT QUIT # CTRL+C OR CTRL+\
  trap 'cleanup; exit 1' TERM # kill's default signal


  # COMMAND-LINE ARGUMENTS AND OPTIONS
  # http://mywiki.wooledge.org/BashFAQ/035
  msg="requires a non-empty option argument."
  while :; do
    case "$1" in
      -h|-\?|--help)
        show_help
        exit
        ;;
      -l|--limit)
        if [ -n "$2" ]; then
          time_limit="$2"
          shift 2
          continue
        else
          show_error "--limit ${msg}"
        fi
        ;;
      --limit=?*)
        time_limit="${1#*=}"
        ;;
      --limit=)
        show_error "--limit ${msg}"
        ;;
      -i|--interval)
        if [ -n "$2" ]; then
          interval="$2"
          shift 2
          continue
        else
          show_error "--interval ${msg}"
        fi
        ;;
      --interval=?*)
        interval="${1#*=}"
        ;;
      --interval=)
        show_error "--interval ${msg}"
        ;;
      -x|--exit-code)
        if [ -n "$2" ]; then
          exit_force="$2"
          shift 2
          continue
        else
          show_error "--exit-code ${msg}"
        fi
        ;;
      --exit-code=?*)
        exit_force="${1#*=}"
        ;;
      --exit-code=)
        show_error "--exit-code ${msg}"
        ;;
      -a|--append)
        if [ -n "$2" ]; then
          append="$2"
          shift 2
          continue
        else
          show_error "--append ${msg}"
        fi
        ;;
      --append=?*)
        append="${1#*=}"
        ;;
      --append=)
        show_error "--append ${msg}"
        ;;
      --) # End of all options.
        shift
        break
        ;;
      -?*)
        show_warning "Unknown option (ignored): $1"
        ;;
      *) # Default case: If no more options then break out of the loop.
        break
    esac

    shift
  done
  # Arguments following the options
  # will remain in the "$@" positional parameters.
  cmd_slowpoke="$1"
  file_log="$2"


  # VALIDATE INPUT
  is_number "${interval}" || show_error "Interval is not a valid number"
  is_number "${time_limit}" || show_error "Limit is not a valid number"
  is_empty "${cmd_slowpoke}" && show_error "Command to execute is not given. See --help."
  is_empty "${file_log}" && file_log="$RANDOM-output.log" # http://mywiki.wooledge.org/BashFAQ/062

  # START CMD
  # http://mywiki.wooledge.org/ProcessManagement
  if [[ "${append}" -ne 1 ]]; then
    is_writeable_empty "${file_log}" || show_error "${file_log} is not writeable or not empty."
    ${cmd_slowpoke} > "${file_log}" & pid_slowpoke=$!
  else
    is_writeable "${file_log}" || show_error "${file_log} is not writeable."
    ${cmd_slowpoke} >> "${file_log}" & pid_slowpoke=$!
  fi


  # WAIT
  # Terminates when $cmd_slowpoke is finished
  # OR
  # $time_limit has reached
  i=0
  while kill -0 ${pid_slowpoke} >/dev/null 2>&1; do
    : $(( time_passed = i * interval ))

    printf "%s\n" \
      "Still waiting for about ${time_passed} seconds" \
      "Used disk space: $(du -sh .)"

    # Output last line from $file_log
    tail -1 "${file_log}"

    # $time_limit
    if [[ "${time_limit}" -ne 0 ]] && [[ "${time_passed}" -ge "${time_limit}" ]]; then
      cleanup
      break
    fi

    sleep ${interval}

    : $(( i += 1 ))
  done


  # FINISHED
  # Shall I fake the exit code?
  if ! is_number "${exit_force}"; then
    # Get exit code from child process that is terminated already, see above
    wait ${pid_slowpoke}; exit_slowpoke=$?
  else
    exit_slowpoke=${exit_force}
  fi
  show_warning "Your given command has terminated with exit code $exit_slowpoke. So do I."
  exit ${exit_slowpoke}

}

main "$@"
