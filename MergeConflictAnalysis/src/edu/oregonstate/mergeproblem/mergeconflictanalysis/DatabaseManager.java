package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	
	private Statement statement;

	public DatabaseManager() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
		statement = connection.createStatement();
	}
	
	public void execute(String query) throws SQLException {
		statement.execute(query);
	}

}
