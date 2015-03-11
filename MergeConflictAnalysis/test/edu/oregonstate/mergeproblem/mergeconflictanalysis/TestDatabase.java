package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

public class TestDatabase {

	@Test
	public void testDatabase() throws Exception {
		Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
		Statement statement = connection.createStatement();
		statement.execute("create table test (id string, bla string)");
		statement.execute("insert into test values ('id', 'bla')");
		ResultSet result = statement.executeQuery("select * from test");
		assertEquals("id",result.getString("id"));
		assertEquals("bla",result.getString("bla"));
	}

}
