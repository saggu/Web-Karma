package edu.isi.karma.view;

import java.util.ArrayList;

public class VHNode {
	private String id;
	private String columnName;
	
	private boolean hasNestedTable;
	private ArrayList<VHNode> nestedNodes;
	
	
	public VHNode(String id, String columnName) {
		this.id = id;
		this.columnName = columnName;
		this.hasNestedTable = false;
		this.nestedNodes = new ArrayList<>();
	}
	
	public String getId() {
		return id;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public boolean hasNestedTable() {
		return hasNestedTable;
	}
	
	
	public ArrayList<VHNode> getNestedNodes() {
		return nestedNodes;
	}
	
	public void addNestedNode(VHNode node) {
		this.nestedNodes.add(node);
		this.hasNestedTable = true;
	}
	
	
}
