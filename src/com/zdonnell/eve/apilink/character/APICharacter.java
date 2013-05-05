package com.zdonnell.eve.apilink.character;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.beimin.eveapi.character.sheet.CharacterSheetResponse;
import com.beimin.eveapi.core.ApiAuth;
import com.zdonnell.eve.apilink.APIExceptionCallback;

public class APICharacter 
{
	private Context context;
	private ApiAuth<?> apiAuth;
	
	public APICharacter(Context context, ApiAuth<?> apiAuth)
	{
		this.context = context;
		this.apiAuth = apiAuth;
	}
	
	public void getCharacterSheet(APIExceptionCallback<CharacterSheetResponse> callback)
	{
		new CharacterSheetTask(callback, apiAuth, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); 
	}
}
