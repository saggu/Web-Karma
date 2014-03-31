/*******************************************************************************
 * Copyright 2012 University of Southern California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This code was developed by the Information Integration Group as part 
 * of the Karma project at the Information Sciences Institute of the 
 * University of Southern California.  For more information, publications, 
 * and related projects, please see: http://www.isi.edu/integration
 ******************************************************************************/
package edu.isi.karma.view;


import edu.isi.karma.controller.update.WorksheetListUpdate;
import edu.isi.karma.rep.HNode;
import edu.isi.karma.rep.HNodePath;
import edu.isi.karma.rep.HTable;
import edu.isi.karma.rep.Table;
import edu.isi.karma.rep.TablePager;
import edu.isi.karma.rep.Worksheet;
import edu.isi.karma.util.JSONUtil;
import edu.isi.karma.view.ViewPreferences.ViewPreference;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

public class VWorksheet extends ViewEntity {

	private final Worksheet worksheet;

	/**
	 * Marks whether the data in the view is consistent with the data in the
	 * memory representation. When false, it means that the view should be
	 * refreshed, and an indication should be shown to the user to indicate that
	 * an explicit refresh is needed.
	 */
	private boolean upToDate = true;

	/**
	 * When true, the view should show the worksheet collapsed so that the
	 * headers are visible but the data is hidden.
	 */
	private boolean collapsed = false;

	/**
	 * The column headers shown in this view. The hidden columns do not appear
	 * in this list. A roundtrip to the server is required to make hidden
	 * columns appear.
	 */
	
	private final List<HNodePath> columns;

	private ArrayList<VHNode> headerViewNodes;
	private HashMap<String, ArrayList<VHNode>> htableHeaderViewMap;
	/**
	 * The maximum number of rows to show in the nested tables.
	 */
	private int maxRowsToShowInNestedTables;


	/**
	 * We create a TablePager for the top level table and every nested table we
	 * see. It records how the table is scrolled.
	 */
	private Map<String, TablePager> tableId2TablePager = new HashMap<String, TablePager>();
	
	VWorksheet(String id, Worksheet worksheet, List<HNodePath> columns,
			VWorkspace vWorkspace) {
		super(id);
		this.worksheet = worksheet;
		this.columns = columns;
		this.maxRowsToShowInNestedTables = vWorkspace.getPreferences()
				.getIntViewPreferenceValue(
						ViewPreference.maxRowsToShowInNestedTables);

		// Force creation of the TablePager for the top table.
		getTablePager(worksheet.getDataTable(),
				vWorkspace.getPreferences().getIntViewPreferenceValue(
						ViewPreference.defaultRowsToShowInTopTables));
		
		this.htableHeaderViewMap = new HashMap<String, ArrayList<VHNode>>();
		this.headerViewNodes = initHeaderViewNodes(worksheet.getHeaders());
		this.htableHeaderViewMap.put(worksheet.getHeaders().getId(), this.headerViewNodes);
	}

	private ArrayList<VHNode> initHeaderViewNodes(HTable table) {
		ArrayList<VHNode> vNodes = new ArrayList<>();
		for(String hNodeId : table.getOrderedNodeIds()) {
			HNode node = table.getHNode(hNodeId);
			VHNode vNode = new VHNode(node.getId(), node.getColumnName());
			if(node.hasNestedTable()) {
				HTable nestedTable = node.getNestedTable();
				ArrayList<VHNode> nestedNodes = initHeaderViewNodes(nestedTable);
				for(VHNode nestedVNode : nestedNodes) {
					vNode.addNestedNode(nestedVNode);
				}
				this.htableHeaderViewMap.put(nestedTable.getId(), vNode.getNestedNodes());
			}
			vNodes.add(vNode);
		}
		return vNodes;
	}
	
	
	private TablePager getTablePager(Table table, int size) {
		TablePager tp = tableId2TablePager.get(table.getId());
		if (tp != null) {
			return tp;
		} else {
			tp = new TablePager(table, size);
			tableId2TablePager.put(table.getId(), tp);
			return tp;
		}
	}

	public TablePager getTopTablePager() {
		return tableId2TablePager.get(worksheet.getDataTable().getId());
	}

	public TablePager getNestedTablePager(Table table) {
		return getTablePager(table, maxRowsToShowInNestedTables);
	}

	public TablePager getTablePager(String tableId) {
		return tableId2TablePager.get(tableId);
	}

	
	
	public Map<String, TablePager> getTableId2TablePager() {
		return tableId2TablePager;
	}

	public void setTableId2TablePager(Map<String, TablePager> tableId2TablePager) {
		this.tableId2TablePager = tableId2TablePager;
	}

	public String getWorksheetId() {
		return worksheet.getId();
	}

	public Worksheet getWorksheet() {
		return worksheet;
	}

	public boolean isUpToDate() {
		return upToDate;
	}

	public void setUpToDate(boolean upToDate) {
		this.upToDate = upToDate;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	public int getMaxRowsToShowInNestedTables() {
		return maxRowsToShowInNestedTables;
	}

	public void setMaxRowsToShowInNestedTables(int maxRowsToShowInNestedTables) {
		this.maxRowsToShowInNestedTables = maxRowsToShowInNestedTables;
	}

	public List<HNodePath> getColumns() {
		return columns;
	}
	
	public List<VHNode> getHeaderViewNodes() {
		return this.headerViewNodes;
	}
	
	public List<VHNode> getHeaderViewNodes(String htableId) {
		return this.htableHeaderViewMap.get(htableId);
	}
	
	public void organizeColumn(JSONArray columns) {
//		ArrayList<HNode> newHeaders = new ArrayList<HNode>();
//		
//		for(Object columnObj : columns) {
//			JSONObject column = (JSONObject)columnObj;
//			if(column.getBoolean("visible") == true) {
//				newHeaders.add(this.headers.getHNode(column.getString("id")));
//				Object nestedColumns = column.get("nestedColumns");
//				if(nestedColumns != null && nestedColumns instanceof JSONArray) {
//					
//				}
//			}
//		}
	}
	
	private ArrayList<HNode> generateOrganizedColumns(JSONArray columns) {
		ArrayList<HNode> newHeaders = new ArrayList<HNode>();
//		
//		for(Object columnObj : columns) {
//			JSONObject column = (JSONObject)columnObj;
//			if(column.getBoolean("visible") == true) {
//				HNode node = this.headers.getHNode(column.getString("id"));
//				newHeaders.add(node);
//				Object nestedColumns = column.get("nestedColumns");
//				if(nestedColumns != null && nestedColumns instanceof JSONArray) {
//					ArrayList<HNode> nestedHeaders = generateOrganizedColumns((JSONArray)nestedColumns);
//					node.
//				}
//			}
//		}
		return newHeaders;
	}
	
	public void generateWorksheetListJson(String prefix, PrintWriter pw) {
		pw.println(prefix + "{");
		String newPref = prefix + "  ";

		pw.println(newPref
				+ JSONUtil.json(WorksheetListUpdate.JsonKeys.worksheetId, this.getWorksheetId()));
		pw.println(newPref
				+ JSONUtil.json(WorksheetListUpdate.JsonKeys.isUpToDate, upToDate));
		pw.println(newPref
				+ JSONUtil.json(WorksheetListUpdate.JsonKeys.isCollapsed, collapsed));

		pw.println(newPref
				+ JSONUtil.json(WorksheetListUpdate.JsonKeys.encoding,
						worksheet.getEncoding()));
		pw.println(newPref
				+ JSONUtil.jsonLast(WorksheetListUpdate.JsonKeys.title,
						worksheet.getTitle()));

		
		pw.println(prefix + "}");
	}
}
