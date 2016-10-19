package com.androidrecipes.palette;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.GridView;

public class ColorfulListActivity extends ActionBarActivity {

    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGridView = new GridView(this);
        mGridView.setNumColumns(2);
        mGridView.setAdapter(new ColorfulAdapter(this));

        setContentView(mGridView);
    }


}
