#!/bin/bash

COL_GREEN="\e[32m"
COL_RED="\e[31m"
COL_RESET="\e[0m"
COL_YELLOW="\e[33;1m"

function echo_red() {
    echo -e "${COL_RED}$*${COL_RESET}"
}

function echo_yellow() {
    echo -e "${COL_YELLOW}$*${COL_RESET}"
}

function echo_green() {
    echo -e "${COL_GREEN}$*${COL_RESET}"
}
