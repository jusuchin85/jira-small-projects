package jira.user.rename;

import jira.user.rename.utils.SQLCreate;

public class Main {

    public static void main(String args[]) {
        String inputFile, outputFile, dbType;
        if (args.length > 0) {
            inputFile = args[0];
            outputFile = args[1];
            dbType = args[2];
            SQLCreate createSQL = new SQLCreate(inputFile, outputFile, dbType);
            createSQL.generateSQLScript();
        }
    }
}
