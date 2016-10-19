package com.androidrecipes.drawerlayout;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ToolbarActivity extends ActionBarActivity
        implements AdapterView.OnItemClickListener {

    private static final String[] ITEMS =
            {"White", "Red", "Green", "Blue"};
    private static final int[] COLORS =
            {Color.WHITE, 0xffe51c23, 0xff259b24, 0xff5677fc};

    private DrawerLayout mDrawerContainer;
    /* Root content pane in layout */
    private View mMainContent;
    /* Main (left) sliding drawer */
    private ListView mDrawerContent;
    /* Toggle object for ActionBar */
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);

        //Inject the toolbar before other APIs try to access it
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerContainer = (DrawerLayout) findViewById(R.id.container_drawer);
        mDrawerContent = (ListView) findViewById(R.id.drawer_main);
        mMainContent = findViewById(R.id.container_root);

        //Toggle indicator must also be the drawer listener,
        // so we extend it to listen for the events ourselves.
        mDrawerToggle  = new ActionBarDrawerToggle(
                this,                 //Host Activity
                mDrawerContainer,     //Container to use
                R.string.drawer_open, //Content description strings
                R.string.drawer_close ) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //Update the options menu
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                //Update the options menu
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //Update the options menu
                supportInvalidateOptionsMenu();
            }
        };

        ListAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ITEMS);
        mDrawerContent.setAdapter(adapter);
        mDrawerContent.setOnItemClickListener(this);

        //Set the toggle as the drawer's event listener
        mDrawerContainer.setDrawerListener(mDrawerToggle);

        //Enable home button actions in the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Synchronize the state of the drawer after any instance
        // state has been restored by the framework
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Update state on any configuration changes
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create the ActionBar actions
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Display the action options based on main drawer state
        boolean isOpen =
                mDrawerContainer.isDrawerVisible(mDrawerContent);
        menu.findItem(R.id.action_delete).setVisible(!isOpen);
        menu.findItem(R.id.action_settings).setVisible(!isOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Let the drawer have a crack at the event first
        // to handle home button events
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            //If this was a drawer toggle, we need to update the
            // options menu, but we have to wait until the next
            // loop iteration for the drawer state to change.
            mDrawerContainer.post(new Runnable() {
                @Override
                public void run() {
                    //Update the options menu
                    supportInvalidateOptionsMenu();
                }
            });
            return true;
        }

        //...Handle other options selections here as normal...
        switch (item.getItemId()) {
            case R.id.action_delete:
                //Delete Action
                return true;
            case R.id.action_settings:
                //Settings Action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Handle click events from items in the list of our main drawer
    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        //Update the background color of the main content
        mMainContent.setBackgroundColor(COLORS[position]);

        //Manually close the drawer
        mDrawerContainer.closeDrawer(mDrawerContent);
    }
}
