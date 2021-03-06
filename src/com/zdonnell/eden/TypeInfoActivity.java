package com.zdonnell.eden;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;


public class TypeInfoActivity extends NavDrawerActivity {

    public TypeInfoActivity() {
        super(R.string.type_info);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int typeID = getIntent().getIntExtra("typeID", 0);

        TypeInfoFragment fragment = new TypeInfoFragment();

        Bundle arguments = new Bundle();
        arguments.putInt("typeID", typeID);
        fragment.setArguments(arguments);

        /**
         * Load the fragment into the activity
         */
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.type_info, menu);

        return true;
    }

    @Override
    protected void refresh() {

    }

}
