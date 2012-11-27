# Name: Justin Alex Paramanandan
# This Bash script is mainly used to rename a project's attachments from its JIRA-attachment ID
# to its corresponding real-name value.
#
# Version 1.0 - 2012/11/26
# First release, still a bit rough off the edges, as the input of many files would need to be done manually.
#
# Refer to the README on how to edit and run this script.

#!/bin/bash
# Here is where you insert the path of a project's attachment
# normally '<JIRA_HOME>/data/attachments/<PROJECT_KEY>
MAIN=/home/jalex/Desktop/TEMP/DUCR

# adjust the values in the loop {1..925} to cater for the first directory to the last one
# in the setting below, it is set to start from <ISSUE_KEY>-1 to <ISSUE_KEY>-925
for i in {1..925}
do
    # this would change the directory to a specific issue key, based on the loop above
    # e.g: cd /home/jalex/Desktop/TEMP/DUCR/DUCR-1
    cd $MAIN/DUCR-$i
     
    # let's start looping the files in the <ISSUE_KEY>-NUM directory
    for f in $MAIN/DUCR-$i/*
    do
        # stripping the full path from the result above; just leaving the fileID
        filename=${f##*/}
        echo "Processing $filename file..."
        
        # start reading the CSV file, delimiting by ',' symbol 
        while IFS=, read col1 col2
        do
            # if the current fileID is similar to the current ID in the CSV, continue
            if [ $filename = "$col1" ]
            then
                # rename the file to its correspondence "real name"
                mv $filename "$col2"
            fi
        # the location of your CSV file
        done < /home/jalex/Desktop/TEMP/DUCR/attachment.csv
    done
    
    # just acting as a separator when we are processing sub-directories
    echo "-----"
    
    # make sure we change our directory back to the <PROJECT_KEY> level
    cd ..
done
