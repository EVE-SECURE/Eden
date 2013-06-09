package com.zdonnell.eve.apilink.eve;

import android.content.Context;
import android.util.SparseArray;

import com.beimin.eveapi.core.ApiPage;
import com.beimin.eveapi.core.ApiPath;
import com.beimin.eveapi.eve.skilltree.ApiSkill;
import com.beimin.eveapi.eve.skilltree.ApiSkillGroup;
import com.beimin.eveapi.eve.skilltree.SkillTreeParser;
import com.beimin.eveapi.eve.skilltree.SkillTreeResponse;
import com.beimin.eveapi.exception.ApiException;
import com.zdonnell.eve.apilink.APIExceptionCallback;
import com.zdonnell.eve.apilink.APITask;
import com.zdonnell.eve.database.SkillTreeData;

/**
 * 
 * @author Zach
 *
 */
public class SkillTreeTask extends APITask<Void, Void, SkillTreeResponse>
{		
	public SkillTreeTask(APIExceptionCallback<SkillTreeResponse> callback, final Context context)
	{						
		super(callback, context, true, new EveApiInteraction<SkillTreeResponse>()
		{
			@Override
			public SkillTreeResponse perform() throws ApiException 
			{
				SkillTreeParser parser = SkillTreeParser.getInstance();
				SkillTreeResponse response = parser.getResponse();
				fixSkillGroups(response);
				
				SkillTreeData skillTreeData = new SkillTreeData(context);
				skillTreeData.setSkillTree(response.getAll());

				return response;
			}
			
		});
	}
	
	/**
	 * Removes duplicate groups from a {@link SkillTreeResponse}
	 * 
	 * @param response
	 */
	private static void fixSkillGroups(SkillTreeResponse response)
	{
		SparseArray<ApiSkillGroup> correctedSkillGroups = new SparseArray<ApiSkillGroup>();

		for (ApiSkillGroup group : response.getAll())
		{
			// This is the first time we have seen a group of this ID, add it to the corrected list
			if (correctedSkillGroups.get(group.getGroupID()) == null)
			{
				correctedSkillGroups.put(group.getGroupID(), group);
			}
			// The group exists in the corrected list already, add all it's skills to the existing group in the corrected list
			else
			{
				for (ApiSkill skill : group.getSkills()) correctedSkillGroups.get(group.getGroupID()).add(skill);
			}
		}

		// Clear the "unfixed" groups, and add back the fixed ones
		response.getAll().clear();
		for (int i = 0; i < correctedSkillGroups.size(); i++) response.add(correctedSkillGroups.valueAt(i));
	}

	@Override
	public int requestTypeHash() 
	{
		return ApiPath.EVE.getPath().concat(ApiPage.SKILL_TREE.getPage()).hashCode();
	}
	
	@Override
	public SkillTreeResponse buildResponseFromDatabase() 
	{
		SkillTreeResponse response = new SkillTreeResponse();
		
		SkillTreeData skillTree = new SkillTreeData(context);
		for (ApiSkillGroup group : skillTree.getSkillTree()) response.add(group);
		
		return response;
	}
}
