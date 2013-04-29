package com.zdonnell.eve.api.priceservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import com.zdonnell.eve.apilink.APICallback;
import com.zdonnell.eve.helpers.Tools;

public class PriceService {
	
	private static PriceService instance;
	
	private Context context;
	
	/**
	 * Singleton access method
	 * 
	 * @param context
	 * @return
	 */
	public static PriceService getInstance(Context context)
	{
		if (instance == null)
		{
			instance = new PriceService(context);
		}
		return instance;
	}
	
	private PriceService(Context context)
	{
		this.context = context;
	}
	
	public void getValues(Integer[] typeIDs, APICallback<SparseArray<Float>> callback)
	{			
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (preferences.getBoolean("price_enabled", true))
		{
			Integer[] strippedTypeIDs = Tools.stripDuplicateIDs(typeIDs);
			new PriceDatabaseTask(callback, context).execute(strippedTypeIDs);
		}
	}
}