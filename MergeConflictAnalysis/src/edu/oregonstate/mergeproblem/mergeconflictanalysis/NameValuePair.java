package edu.oregonstate.mergeproblem.mergeconflictanalysis;

public class NameValuePair<N, V> {
	
	private N name;
	private V value;

	public NameValuePair(N name, V value) {
		this.name = name;
		this.value = value;
	}
	
	public N getName() {
		return name;
	}
	
	public V getValue() {
		return value;
	}

}
