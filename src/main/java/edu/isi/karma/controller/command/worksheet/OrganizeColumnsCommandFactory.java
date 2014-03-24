package edu.isi.karma.controller.command.worksheet;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;

import edu.isi.karma.controller.command.Command;
import edu.isi.karma.controller.command.CommandFactory;
import edu.isi.karma.controller.command.JSONInputCommandFactory;
import edu.isi.karma.controller.history.HistoryJsonUtil;
import edu.isi.karma.rep.Workspace;
import edu.isi.karma.webserver.KarmaException;

public class OrganizeColumnsCommandFactory extends CommandFactory implements
		JSONInputCommandFactory {

	enum Arguments {
		worksheetId, orderedColumns
	}
	
	@Override
	public Command createCommand(JSONArray inputJson, Workspace workspace)
			throws JSONException, KarmaException {
		String worksheetId = HistoryJsonUtil.getStringValue(
				Arguments.worksheetId.name(), inputJson);
		
		JSONArray orderedColumns = HistoryJsonUtil.getJSONArrayValue(
				Arguments.orderedColumns.name(), inputJson);

		OrganizeColumnsCommand cmd = new OrganizeColumnsCommand(
				getNewId(workspace), worksheetId, orderedColumns);
		cmd.setInputParameterJson(inputJson.toString());
		return cmd;
	}

	@Override
	public Command createCommand(HttpServletRequest request, Workspace workspace) {
		
		return null;
	}

}
