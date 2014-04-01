package edu.isi.karma.controller.update;

import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.isi.karma.rep.HNode;
import edu.isi.karma.rep.HTable;
import edu.isi.karma.rep.Worksheet;
import edu.isi.karma.rep.alignment.SemanticTypes;
import edu.isi.karma.view.VWorksheet;
import edu.isi.karma.view.VWorkspace;

public class AllWorksheetHeadersUpdate extends AbstractUpdate {
	private String worksheetId;
	private enum JsonKeys {
		worksheetId, columns, name, id, visible, hideable, children
	}
	
	public AllWorksheetHeadersUpdate(String worksheetId) {
		super();
		this.worksheetId = worksheetId;
	}
	
	@Override
	public void generateJson(String prefix, PrintWriter pw,
			VWorkspace vWorkspace) {
		VWorksheet vWorksheet =  vWorkspace.getViewFactory().getVWorksheetByWorksheetId(worksheetId);
		
		try {
			JSONObject response = new JSONObject();
			response.put(JsonKeys.worksheetId.name(), worksheetId);
			response.put(AbstractUpdate.GenericJsonKeys.updateType.name(), 
					this.getClass().getSimpleName());
			
			Worksheet wk = vWorksheet.getWorksheet();
			HTable headerTable = wk.getHeaders();
			
			JSONArray columns = getColumnsJsonArray(headerTable, vWorksheet, wk.getSemanticTypes());
			response.put(JsonKeys.columns.name(), columns);
			
			pw.println(response.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	private JSONArray getColumnsJsonArray(HTable table, VWorksheet vWorksheet, SemanticTypes semTypes) {
		JSONArray columns = new JSONArray();
		for(String hNodeId : table.getOrderedNodeIds()) {
			JSONObject column = new JSONObject();
			column.put(JsonKeys.id.name(), hNodeId);
			
			HNode hnode = table.getHNode(hNodeId);
			column.put(JsonKeys.name.name(), hnode.getColumnName());
			column.put(JsonKeys.visible.name(), vWorksheet.isHeaderNodeVisible(table.getId(), hNodeId));
			boolean hideable = (semTypes.getSemanticTypeForHNodeId(hNodeId) == null) ? true : false;
			
			
			if(hnode.hasNestedTable()) {
				JSONArray children = getColumnsJsonArray(hnode.getNestedTable(), vWorksheet, semTypes);
				column.put(JsonKeys.children.name(), children);
				if(hideable) {
					//check if any of the children are not hideable, then this is not hideable
					for(int i=0; i<children.length(); i++) {
						JSONObject child = children.getJSONObject(i);
						if(child.getBoolean("hideable") == false) {
							hideable = false;
							break;
						}
					}
				}
			}
			
			column.put(JsonKeys.hideable.name(), hideable);
			columns.put(column);
		}
		return columns;
	}

}
