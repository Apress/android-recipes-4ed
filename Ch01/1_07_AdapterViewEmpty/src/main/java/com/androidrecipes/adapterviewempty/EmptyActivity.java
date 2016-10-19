package com.androidrecipes.adapterviewempty;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


public class EmptyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);
        
        ListView list = (ListView)findViewById(R.id.mylist);
        TextView empty = (TextView)findViewById(R.id.myempty);
        
        /*
         * Attach the empty view.  The framework will show this
         * view when the ListView's adapter has no elements.
         */
        list.setEmptyView(empty);

        //Continue adding adapters and data to the list
    }

}
