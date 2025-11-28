#!/bin/bash

bash format.sh

if [ $? != 0 ]; then
    echo "Failed to format."

    exit 1
fi

bash build.sh

if [ $? != 0 ]; then
    echo "Failed to build."

    exit 2
fi

bash encode-sample.sh

if [ $? != 0 ]; then
    echo "Failed to run."

    exit 3
fi

exit 0
