package com.androidrecipes.tabs;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.common.view.SlidingTabLayout;

public class ActionTabsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        SlidingTabLayout tabLayout = (SlidingTabLayout) findViewById(R.id.tabs);

        viewPager.setAdapter(new TabsPagerAdapter(this));

        /*
         * SlidingTabLayout is bound to ViewPager, both for tab titles
         * and scroll tracking behavior.
         */
        tabLayout.setViewPager(viewPager);
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                //Color to show underneath each tab position
                return Color.WHITE;
            }

            @Override
            public int getDividerColor(int position) {
                //Transparent to hide dividers
                return 0;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tabs, menu);
        return true;
    }

    /*
     * Simple PagerAdapter to display page views with static images
     */
    private static class TabsPagerAdapter extends PagerAdapter {
        private Context mContext;

        public TabsPagerAdapter(Context context) {
            mContext = context;
        }

        /*
         * SlidingTabLayout requires this method to define the
         * text that each tab will display.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Primary";
                case 1:
                    return "Secondary";
                case 2:
                    return "Tertiary";
                case 3:
                    return "Quaternary";
                case 4:
                    return "Quinary";
                default:
                    return "";
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView pageView = new ImageView(mContext);
            pageView.setScaleType(ImageView.ScaleType.CENTER);
            pageView.setImageResource(R.drawable.ic_launcher);

            container.addView(pageView);

            return pageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }
}
