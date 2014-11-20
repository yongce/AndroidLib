#!/bin/sh

LATEST_COMMIT=$(git log --format='%H' -1)
FILE=$1
echo "latest_commit: "$LATEST_COMMIT >> $FILE
echo `date` >> $FILE

