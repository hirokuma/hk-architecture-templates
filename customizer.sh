#!/bin/bash
#
# Copyright (C) 2022 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Verify bash version. macOS comes with bash 3 preinstalled.
if [[ ${BASH_VERSINFO[0]} -lt 4 ]]
then
  echo "You need at least bash 4 to run this script."
  exit 1
fi

# exit when any command fails
set -e

if [[ $# -lt 2 ]]; then
   echo "Usage: bash customizer.sh my.new.package AppName" >&2
   exit 2
fi

PACKAGE=$1
APPNAME=$2
SUBDIR=${PACKAGE//.//} # Replaces . with /

for n in $(find . -type d \( -path '*/src/androidTest' -or -path '*/src/main' -or -path '*/src/test' \) )
do
  echo "Creating $n/java/$SUBDIR"
  mkdir -p $n/java/$SUBDIR
  echo "Moving files to $n/java/$SUBDIR"
  mv $n/java/android/template/* $n/java/$SUBDIR
  echo "Removing old $n/java/android/template"
  rm -rf mv $n/java/android
done

# Rename application name
echo "Renaming application name to $APPNAME"
sed -i.bak "s/MyBLEApplication/$APPNAME/g" app/src/main/res/values/strings.xml
sed -i.bak "s/MyBLEApplication/$APPNAME/g" settings.gradle.kts

# Rename package and imports
echo "Renaming packages to $PACKAGE"
find ./ -type f -name "*.kt" -exec sed -i.bak "s/package android.template/package $PACKAGE/g" {} \;
find ./ -type f -name "*.kt" -exec sed -i.bak "s/import android.template/import $PACKAGE/g" {} \;

# Gradle files
find ./ -type f -name "*.kts" -exec sed -i.bak "s/android.template/$PACKAGE/g" {} \;

echo "Cleaning up"
find . -name "*.bak" -type f -delete

# Remove additional files
echo "Removing additional files"
rm -rf .google/
rm -rf .github/
rm -rf CONTRIBUTING.md LICENSE README.md customizer.sh
rm -rf .git/
echo "Done!"
