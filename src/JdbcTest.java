import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.pool.OracleDataSource;

/*
 * Use this class to test JDBC connections to Oracle databases
 * ===========================================================
 *
 * Usage: java <class-path> [<tnsnames-path>] JdbcTest <user-name> <password> <database-connection-string>
 * -----
 * <class-path>
 *     For Oracle client 10.2:
 *        -cp .:$ORACLE_HOME/jdbc/lib/ojdbc14.jar
 *     For Oracle client 11.2:
 *        -cp .:$ORACLE_HOME/jdbc/lib/ojdbc6.jar
 *     For Oracle client 12.1:
 *       for JDK 6
 *         -cp .:$ORACLE_HOME/jdbc/lib/ojdbc6.jar
 *       for JDK 7
 *         -cp .:$ORACLE_HOME/jdbc/lib/ojdbc7.jar
 * 
 * <tnsnames-path>
 *     When using a tns-name with the thin driver in the connection string, you need to add the property 
 *     oracle.net.tns_admin pointing to the directory containing the tnsnames.ora file.
 *     Example:
 *     -Doracle.net.tns_admin=$ORACLE_HOME/network/admin
 *     
 *     NOTE: the tnsnames.ora file must be formatted according to Oracle's peculiar taste. See details in:
 *     http://stackoverflow.com/questions/5902150/java-lang-arrayindexoutofboundsexception-when-creating-a-connection-to-an-oracle/5925799#5925799
 *     http://download.oracle.com/docs/cd/A57673_01/DOC/net/doc/NWUS233/apb.htm
 *     Connections will fail if the tnsnames.ora is not formatted "correctly", while sqlplus and tnsping will not fail.
 * 
 * Examples: (1) java <class-path> JdbcTest lydpappcon secret "jdbc:oracle:oci:@RDDB175V"
 *           (2) java <class-path> JdbcTest lydpappcon secret "jdbc:oracle:thin:@RDDB175V"
 *           (3) java <class-path> JdbcTest lydpappcon secret "jdbc:oracle:oci:@(DESCRIPTION=(FAILOVER=on)(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=vm-rd-db-01.hzlab.lab.emc.com)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=RDDB175V)(FAILOVER_MODE=(TYPE=session)(METHOD=basic))))"
 *           (4) java <class-path> JdbcTest lydpappcon secret "jdbc:oracle:thin:@(DESCRIPTION=(FAILOVER=on)(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=vm-rd-db-01.hzlab.lab.emc.com)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=RDDB175V)(FAILOVER_MODE=(TYPE=session)(METHOD=basic))))"
 *           (5) java <class-path> JdbcTest lydpappcon secret "jdbc:oracle:oci:@vm-rd-db-01.hzlab.lab.emc.com:1521:RDDB175V"
 *           (6) java <class-path> JdbcTest lydpappcon secret "jdbc:oracle:thin:@vm-rd-db-01.hzlab.lab.emc.com:1521:RDDB175V"
 *           (7) java <class-path> JdbcTest lydpappcon secret "jdbc:oracle:thin:@//vm-rd-db-01.hzlab.lab.emc.com:1521/RDDB175V"
 *
 * See "Oracle Database JDBC Developer's Guide" section "Database URLs and Database Specifiers" for details description
 * of JDBC connection specification.
 * https://docs.oracle.com/cd/E11882_01/java.112/e16548/urls.htm#JJDBC08200
 *  
 * Usage (1) takes advantage of the Oracle oci driver, enabling you to connect using a tns-name.
 * 
 * Usage (2) also uses the tns-name, with the Oracle thin driver.
 * This requires the property oracle.net.tns_admin pointing to the directory containing the tnsnames.ora file.
 *
 * Usages (3), (4) contain a connection string as appears in $ORACLE_HOME/network/admin/tnsnames.ora
 * You should remove spaces from the string or the driver may get confused.
 * Usage (3) makes no sense, if you can use oci then usage (1) is much simpler.
 *
 * Usages (5), (6) and (7) contain the explicit db-server and port.
 * Usage (5) makes no sense, if you can use oci then usage (1) is much simpler.
 * Usage (6) contains the "classic" URL format.
 * Usage (7) contains the URL format from the "Oracle Database JDBC Developer's Guide.
 */
public class JdbcTest
{
	/**
	 * Executes an SQL query and writes its result to standard output. The SQL is expected to return 1 (one)
	 * string column
	 * 
	 * @param conn
	 *            Connection to database.
	 * @param sql
	 *            SQL string to execute.
	 * @param resultDesc
	 *            Description of SQL result.
	 * @throws Exception
	 */
	static void DoSql(Connection conn, String sql, String resultDesc) throws Exception
	{
		// try with resource statement, ensures we close the Statement and ResultSet resources
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(sql))
		{
			// Get string result - first column
			while (rset.next())
			{
				System.out.printf("%s : [%s]%n", resultDesc, rset.getString(1));
			}
		}
		catch (SQLException e)
		{
			throw new Exception("Error in DoSql", e);
		}
	}
	
	/**
	 * Body of class
	 * 
	 * @param args
	 *            user-name, password, connection-URL
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception
	{
		if (args.length != 3)
			throw new Exception("Wrong number of arguments. Expected: user-name password connection-url");
		
		OracleDataSource ods;
		
		try
		{
			ods = new OracleDataSource();
			ods.setUser(args[0]);
			ods.setPassword(args[1]);
			ods.setURL(args[2]);
		}
		catch (SQLException e)
		{
			throw new Exception("Failed to create OracleDataSource", e);
		}
		
		// Connect to database
		System.out.printf("%nConnecting to database using URL: [%s]%n", args[2]);
		
		// try with resources, ensures we close the connection
		try (Connection conn = ods.getConnection())
		{
			// Display metadata about the database
			DatabaseMetaData dmd = conn.getMetaData();
			
			System.out.println();
			System.out.println("Database metadata");
			System.out.println("=================");
			System.out.printf("DatabaseProductName:    [%s]%n", dmd.getDatabaseProductName());
			System.out.printf("DatabaseProductVersion: [%s]%n", dmd.getDatabaseProductVersion());
			System.out.printf("DriverName:             [%s]%n", dmd.getDriverName());
			System.out.printf("DriverVersion:          [%s]%n", dmd.getDriverVersion());
			System.out.printf("URL:                    [%s]%n", dmd.getURL());
			
			// Run some queries to get and display info about the session
			System.out.println();
			System.out.println("Parameters from namespace: USERENV");
			System.out.println("==================================");
			
			DoSql(conn, "select SYS_CONTEXT('USERENV', 'DB_NAME') from dual", "DB_NAME      ");
			DoSql(conn, "select SYS_CONTEXT('USERENV', 'INSTANCE_NAME') from dual", "INSTANCE_NAME");
			DoSql(conn, "select SYS_CONTEXT('USERENV', 'SERVER_HOST') from dual", "SERVER_HOST  ");
			DoSql(conn, "select SYS_CONTEXT('USERENV', 'SERVICE_NAME') from dual", "SERVICE_NAME ");
			
			System.out.println();
			
		}
		catch (SQLException eSql)
		{
			throw new Exception("Caught an SQLException in main()", eSql);
		}
		catch (Exception e)
		{
			throw new Exception("Caught an Exception in main()", e);
		}
	}
}
