package com.cloudera.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class ClouderaHiveJdbcExample {

	private static final String CONNECTION_URL_PROPERTY = "connection.url";
	private static final String JDBC_DRIVER_NAME_PROPERTY = "jdbc.driver.class.name";

	private static String connectionUrl;
	private static String jdbcDriverName;

	private static void loadConfiguration() throws IOException {
		InputStream input = null;
		try {
			String filename = ClouderaHiveJdbcExample.class.getSimpleName() + ".conf";
			input = ClouderaHiveJdbcExample.class.getClassLoader().getResourceAsStream(filename);
			Properties prop = new Properties();
			prop.load(input);

			connectionUrl = prop.getProperty(CONNECTION_URL_PROPERTY);
			jdbcDriverName = prop.getProperty(JDBC_DRIVER_NAME_PROPERTY);
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException e) {
				// nothing to do
			}
		}
	}

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.out.println("Syntax: ClouderaImpalaJdbcExample \"<SQL_query>\"");
			System.exit(1);
		}
		String sqlStatement = args[0];

		loadConfiguration();

		System.out.println("\n=============================================");
		System.out.println("Cloudera Impala JDBC Example");
		System.out.println("Using Connection URL: " + connectionUrl);
		System.out.println("Running Query: " + sqlStatement);

		Connection con = null;

		try {

			Class.forName(jdbcDriverName).newInstance();

			con = DriverManager.getConnection(connectionUrl);
//			con = DriverManager.getConnection(connectionUrl, "hdfs", "");
//			con.setAutoCommit(false);
//			con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			PreparedStatement stmt = con.prepareStatement("select * from student");

			ResultSet rs = stmt.executeQuery();

			System.out.println("\n== Begin Query Results ======================");

			// print the results to the console
			while (rs.next()) {
				// the example query returns one String column
				System.out.println(rs.getString(1)+"||"+rs.getString(2));
			}
			rs.close();
			stmt.close();
			stmt=con.prepareStatement("insert into student values(2,'习近平')");
			stmt.execute();
//			con.commit();
			System.out.println("== End Query Results =======================\n\n");

		} catch (Exception e) {
//			try {
//				con.rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e) {
				// swallow
			}
		}
	}
}
