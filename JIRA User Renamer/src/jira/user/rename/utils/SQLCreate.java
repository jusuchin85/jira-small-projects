package jira.user.rename.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;

public class SQLCreate {

    private String inputFile, outputFile, dbType;

    public SQLCreate(String inputFile, String outputFile, String dbType) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.dbType = dbType;
    }

    /**
     * 
     */
    public void generateSQLScript() {
        try {

            // Declares the input file
            FileInputStream fstream = new FileInputStream(inputFile);

            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // Declares the output file
            Writer output = new BufferedWriter(new FileWriter(outputFile));

            // Writes "create table" SQL query
            output.write(generateTableCreationQuery(dbType));

            String strLine;
            int current = 1;

            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                /* Ignores the following from the file:
                 * a. "group_name"
                 * b. "----------"
                 * c. " x rows "
                 */
                if ((!strLine.contains("row"))
                        && (!strLine.contains("group_name"))
                        && (!strLine.contains("-------"))) {

                    StringBuilder initString = new StringBuilder();
                    StringBuilder finalString = new StringBuilder();

                    String[] result = strLine.split("\\s");
                    for (int i = 1; i < result.length; i++) {
                        
                        if (!result[i].isEmpty()) {
                            initString.append(result[i]);
                            finalString.append(result[i]);
                            if (!(i == result.length - 1)) {
                                initString.append(' ');
                                finalString.append('-');
                            }
                        }
                    }
                    String query = generateInsertQuery(current,
                            initString, finalString);
                    output.write(query);
                    current++;
                }
            }
            output.write(generateUpdateQueries(dbType));
            
            // Close the input stream
            in.close();
            output.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    private String generateTableCreationQuery(String dbType) {
        if (dbType.equalsIgnoreCase("mysql") || dbType.equalsIgnoreCase("psql")) {
            return "CREATE TABLE tab_groupmigration (id INT PRIMARY KEY, oldgroupname VARCHAR(50), newgroupname VARCHAR(50));\n\n";
        } else {
            return null;
        }
    }

    private String generateInsertQuery(int current, StringBuilder oldGroupName,
            StringBuilder newGroupName) {
        return "INSERT INTO tab_groupmigration VALUES (" + current + ",'"
                + oldGroupName + "','" + newGroupName + "');\n";
    }

    private String generateUpdateQueries(String dbType) {
        StringBuilder updateQueries = new StringBuilder();
        updateQueries.append("\n");
        if (dbType.equalsIgnoreCase("mysql")) {
            updateQueries
                    .append("update filtersubscription x inner join tab_groupmigration z on x.groupname = z.oldgroupname set groupname = z.newgroupname;\n");
            updateQueries
                    .append("update cwd_group x inner join tab_groupmigration z on x.group_name = z.oldgroupname AND x.lower_group_name = z.oldgroupname set group_name = z.newgroupname;\n");
            updateQueries
                    .append("update jiraaction x inner join tab_groupmigration z on x.actionlevel = z.oldgroupname set actionlevel = z.newgroupname;\n");
            updateQueries
                    .append("update jiraperms x inner join tab_groupmigration z on x.groupname = z.oldgroupname set groupname = z.newgroupname;\n");
            updateQueries
                    .append("update cwd_membership x inner join tab_groupmigration z on x.parent_name = z.oldgroupname set parent_name = z.newgroupname;\n");
            updateQueries
                    .append("update cwd_membership x inner join tab_groupmigration z on x.lower_parent_name = z.oldgroupname set lower_parent_name = z.newgroupname;\n");
            updateQueries
                    .append("update notification x inner join tab_groupmigration z on x.notif_parameter = z.oldgroupname set notif_parameter = z.newgroupname where notif_type = 'Group_Dropdown';\n");
            updateQueries
                    .append("update projectroleactor x inner join tab_groupmigration z on x.roletypeparameter = z.oldgroupname set roletypeparameter = z.newgroupname where roletype = 'atlassian-group-role-actor';\n");
            updateQueries
                    .append("update schemeissuesecurities x inner join tab_groupmigration z on x.sec_parameter = z.oldgroupname set sec_parameter = z.newgroupname where sec_type = 'group';\n");
            updateQueries
                    .append("update schemepermissions x inner join tab_groupmigration z on x.perm_parameter = z.oldgroupname set perm_parameter = z.newgroupname where perm_type = 'group';\n");
            updateQueries
                    .append("update searchrequest x inner join tab_groupmigration z on x.groupname = z.oldgroupname set groupname = z.newgroupname;\n");
            updateQueries
                    .append("update sharepermissions x inner join tab_groupmigration z on x.param1 = z.oldgroupname set x.param1=z.newgroupname;\n");
            updateQueries
                    .append("update worklog x inner join tab_groupmigration z on x.grouplevel = z.oldgroupname set grouplevel = z.newgroupname;\n");
        } else if (dbType.equalsIgnoreCase("psql")) {
            updateQueries
                    .append("update filtersubscription x set groupname = z.newgroupname from tab_groupmigration z where x.groupname = z.oldgroupname;\n");
            updateQueries
                    .append("update cwd_group x set group_name = z.newgroupname from tab_groupmigration z where x.group_name = z.oldgroupname;\n");
            updateQueries
                    .append("update jiraaction x set actionlevel = z.newgroupname from tab_groupmigration z where x.actionlevel = z.oldgroupname;\n");
            updateQueries
                    .append("update jiraperms x set groupname = z.newgroupname from tab_groupmigration z where x.groupname = z.oldgroupname;\n");
            updateQueries
                    .append("update cwd_membership x set parent_name = z.newgroupname from tab_groupmigration z where x.parent_name = z.oldgroupname;\n");
            updateQueries
                    .append("update cwd_membership x set lower_parent_name = z.newgroupname from tab_groupmigration z where x.lower_parent_name = z.oldgroupname;\n");
            updateQueries
                    .append("update notification x set notif_parameter = z.newgroupname from tab_groupmigration z where x.notif_parameter = z.oldgroupname AND notif_type = 'Group_Dropdown';\n");
            updateQueries
                    .append("update projectroleactor x set roletypeparameter = z.newgroupname from tab_groupmigration z where x.roletypeparameter = z.oldgroupname AND roletype = 'atlassian-group-role-actor';\n");
            updateQueries
                    .append("update schemeissuesecurities x set sec_parameter = z.newgroupname from tab_groupmigration z where x.sec_parameter = z.oldgroupname AND sec_type = 'group';\n");
            updateQueries
                    .append("update schemepermissions x set perm_parameter = z.newgroupname from tab_groupmigration z where x.perm_parameter = z.oldgroupname;\n");
            updateQueries
                    .append("update searchrequest x set groupname = z.newgroupname from tab_groupmigration z where x.groupname = z.oldgroupname;\n");
            updateQueries
                    .append("update sharepermissions x set param1 = z.newgroupname from tab_groupmigration z where x.param1 = z.oldgroupname;\n");
            updateQueries
                    .append("update worklog x set grouplevel = z.newgroupname from tab_groupmigration z where x.grouplevel = z.oldgroupname;\n");
        }
        return updateQueries.toString();
    }
}
