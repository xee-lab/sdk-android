# Copyright 2017 Eliocity
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#!/bin/sh
# this hook is used to check if `xee-config.xml` file has been changed, which must not.
#
# It is automatically linked when the command `make install` has been ran.

echo "pre-commit started"

STAGED_FILES=`git diff --name-only --cached | grep xee-config`

if [[ $STAGED_FILES == *"xee-config.xml"* ]]
then
    echo "Looks like you staged xee-config modifications. You can't. Make sure to not modify this file"
    echo "To cancel changes on this file, please do: git checkout app/src/main/res/values/xee-config.xml"
    echo "!!!!!! Pre-commit FAILED !!!!!!"
    EXIT_CODE=1
else
    echo "Everything looks ok"
    EXIT_CODE=0
fi

echo "pre-commit ended"

exit $EXIT_CODE