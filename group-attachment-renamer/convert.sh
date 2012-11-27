#!/bin/bash
MAIN=/home/jalex/Desktop/TEMP/DUCR

for i in {1..925}
do
    cd $MAIN/DUCR-$i
    
    for f in $MAIN/DUCR-$i/*
    do
        filename=${f##*/}
        echo "Processing $filename file..."
        # take action on each file. $f store current file name
        while IFS=, read col1 col2
        do
            if [ $filename = "$col1" ]
            then
                mv $filename "$col2"
            fi
        done < /home/jalex/Desktop/TEMP/DUCR/attachment.csv
    done
    
    echo "-----"
    cd ..
done