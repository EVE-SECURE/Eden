package com.zdonnell.eve.apilink;

import com.beimin.eveapi.exception.ApiException;
import com.zdonnell.eve.ILoadingActivity;

public abstract class APIExceptionCallback<T> {

	public static final int STATE_CACHED_RESPONSE_ACQUIRED_VALID = 0;
	public static final int STATE_CACHED_RESPONSE_ACQUIRED_INVALID = 1;
	public static final int STATE_CACHED_RESPONSE_NOT_FOUND = 2;
	public static final int STATE_SERVER_RESPONSE_ACQUIRED = 3;
	public static final int STATE_SERVER_RESPONSE_FAILED = 4;
	
	private ILoadingActivity requestingActivity;
	
	/**
	 * This value is an internal counter to keep track of where we are in the
	 * request process.
	 */
	private int stateInt;
	
	public APIExceptionCallback(ILoadingActivity activity)
	{
		requestingActivity = activity;
		if (activity != null) initializeCallback();
	}
	
	private void initializeCallback()
	{
		stateInt = 2;
		requestingActivity.dataLoading();
	}
	
	public void updateState(int state)
	{
		boolean dataError = (state == STATE_SERVER_RESPONSE_FAILED);
		
		switch (state)
		{
		/* The API Request returned a cached response that is valid, we are done */
		case STATE_CACHED_RESPONSE_ACQUIRED_VALID:
			stateInt -= 2;
			break;
		/* The API Request returned a cached response that has expired, we need to wait for
		 * the server now
		 */
		case STATE_CACHED_RESPONSE_ACQUIRED_INVALID:
			stateInt -= 1;
			break;
		/* There was no response in the cache, so now we just wait for the server */
		case STATE_CACHED_RESPONSE_NOT_FOUND:
			stateInt -= 1;
			break;
		/* The server returned a response, we are done */
		case STATE_SERVER_RESPONSE_ACQUIRED:
			stateInt -= 1;
			break;
		/* The server could not be contacted, we are done */
		case STATE_SERVER_RESPONSE_FAILED:
			stateInt -= 1;
			break;
		}
		
		if (requestingActivity != null) checkState(dataError);
	}
	
	private void checkState(boolean dataError)
	{
		if (stateInt == 0) requestingActivity.loadingFinished(dataError);
	}
	
	public abstract void onUpdate(T response);

	public abstract void onError(T response, ApiException exception);	
}
