#!/bin/bash

sed 's#https://github.com/\([a-zA-Z0-9_\.\-]*\)/\([a-zA-Z0-9_\.\-]*\).git#\1,\2#g' < $1