package com.zdonnell.eve.api.character;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;

import com.zdonnell.eve.api.APICallback;
import com.zdonnell.eve.api.APICredentials;
import com.zdonnell.eve.api.APIObject;
import com.zdonnell.eve.api.BaseRequest;
import com.zdonnell.eve.api.ResourceManager;
import com.zdonnell.eve.api.ResourceManager.APIRequestWrapper;

public class APICharacter extends APIObject {
	
	private ResourceManager resourceManager;
	
	private int characterID;

	public APICharacter(APICredentials credentials, int characterID, Context context) 
	{	
		this.characterID = characterID;
		super.setCredentials(credentials);	
		resourceManager = ResourceManager.getInstance(context);
	}

	public int id() { return characterID; }
	
	/**
	 * @param apiCallback 
	 * 
	 */
	public void getSkillQueue(APICallback<ArrayList<QueuedSkill>> apiCallback) 
	{	
		final String resourceSpecificURL = "char/SkillQueue.xml.aspx";
		String fullURL = BaseRequest.baseURL + resourceSpecificURL;
		
		resourceManager.requestResource(new APIRequestWrapper(apiCallback, new SkillQueueParser(), credentials, fullURL, true, new BasicNameValuePair("characterID", String.valueOf(characterID))));		
	}
	
	private class SkillQueueParser extends APIParser<ArrayList<QueuedSkill>>
	{
		@Override
		public ArrayList<QueuedSkill> parse(Document document) {
			
			NodeList skillNodes = document.getElementsByTagName("row");
			ArrayList<QueuedSkill> skillQueue = new ArrayList<QueuedSkill>();

			for (int x = 0; x < skillNodes.getLength(); x++) {
				Node skillNode = skillNodes.item(x);
				NamedNodeMap skillAttributes = skillNode.getAttributes();

				int skillID = Integer.parseInt(skillAttributes.getNamedItem("typeID").getTextContent());
				int skillLevel = Integer.parseInt(skillAttributes.getNamedItem("level").getTextContent());
				int startSP = Integer.parseInt(skillAttributes.getNamedItem("startSP").getTextContent());
				int endSP = Integer.parseInt(skillAttributes.getNamedItem("endSP").getTextContent());
				String startTime = skillAttributes.getNamedItem("startTime").getTextContent();
				String endTime = skillAttributes.getNamedItem("endTime").getTextContent();

				skillQueue.add(new QueuedSkill(skillID, skillLevel, startSP, endSP, startTime, endTime));
			}
			
			return skillQueue;
		}
	}
}