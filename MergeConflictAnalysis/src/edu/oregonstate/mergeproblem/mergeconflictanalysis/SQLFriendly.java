package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.List;
import java.util.Map;

public interface SQLFriendly {
	
	public List<String> getInsertQueries();
	
	public List<String> getTableNames();
	
	public Map<String, String> getColumnNames();
}
