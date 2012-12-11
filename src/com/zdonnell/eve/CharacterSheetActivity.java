package com.zdonnell.eve;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.zdonnell.eve.api.APICredentials;
import com.zdonnell.eve.api.character.APICharacter;
import com.zdonnell.eve.character.detail.ParentAssetsFragment;
import com.zdonnell.eve.character.detail.AttributesFragment;
import com.zdonnell.eve.character.detail.SkillQueueFragment;
import com.zdonnell.eve.character.detail.WalletFragment;

public class CharacterSheetActivity extends BaseActivity
        implements CharacterSheetFragment.Callbacks {

	public CharacterSheetActivity(int titleRes) {
		super(titleRes);
		// TODO Auto-generated constructor stub
	}
	
	public CharacterSheetActivity() {
		super(R.string.app_name);

	}

	private boolean mTwoPane;
    
    private APICharacter assembledChar;
    
    private String[] characterInfo;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheetitem_list);
        
		setSlidingActionBarEnabled(true);

        characterInfo = getIntent().getExtras().getStringArray("character");
        assembledChar = new APICharacter(new APICredentials(Integer.valueOf(characterInfo[1]), characterInfo[2]), Integer.valueOf(characterInfo[0]), getBaseContext());
        
        getActionBar().setTitle(new CharacterDB(this).getCharacterName(assembledChar.id()));
        
        ((CharacterSheetFragment) getSupportFragmentManager().findFragmentById(R.id.sheetitem_list)).setCharacter(assembledChar);
        
        if (findViewById(R.id.sheetitem_detail_container) != null) 
        {
            mTwoPane = true;
            ((CharacterSheetFragment) getSupportFragmentManager().findFragmentById(R.id.sheetitem_list)).setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(int id) {
        if (mTwoPane) {
            
        	Fragment fragment;
        	
        	switch (id)
        	{
        	case CharacterSheetFragment.SKILLS:
        		fragment = new AttributesFragment(assembledChar);
        		break;
        	case CharacterSheetFragment.SKILL_QUEUE:
        		fragment = new SkillQueueFragment(assembledChar);
        		break;
        	case CharacterSheetFragment.ATTRIBUTES:
        		fragment = new AttributesFragment(assembledChar);
        		break;
        	case CharacterSheetFragment.WALLET:
            	fragment = new WalletFragment(assembledChar);
        		break;
        	case CharacterSheetFragment.ASSETS:
            	fragment = new ParentAssetsFragment(assembledChar);
        		break;
        	default:
        		fragment = new AttributesFragment(assembledChar);
        		break;
        	}
        	        	            
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sheetitem_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, CharacterDetailActivity.class);
            detailIntent.putExtra("position", id);
            detailIntent.putExtra("character", characterInfo);
            startActivity(detailIntent);
        }
    }
}
