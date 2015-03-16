package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.bson.Document;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDriverTest {
	
	@Test
	public void testSetupConnection() {
		MongoClient mongoClient = new MongoClient("localhost");
		MongoDatabase testDatabase = mongoClient.getDatabase("test");
		testDatabase.createCollection("test-collection");
		MongoCollection<Document> collection = testDatabase.getCollection("test-collection");
		collection.insertOne(new Document().append("test", "bla"));
		testDatabase.dropDatabase();
		mongoClient.close();
	}
	

}
