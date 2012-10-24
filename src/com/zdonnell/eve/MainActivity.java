package com.zdonnell.eve;

import android.os.Bundle;


public class MainActivity extends BaseActivity {

	public MainActivity() {
		super(R.string.title_bar_slide);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, new CharacterTabFragment())
		.commit();
		
		setSlidingActionBarEnabled(true);
	}
	
}