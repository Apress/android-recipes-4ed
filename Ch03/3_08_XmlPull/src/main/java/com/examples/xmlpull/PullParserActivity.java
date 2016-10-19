package com.examples.xmlpull;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class PullParserActivity extends ListActivity {

    private static final String LOGTAG = "PullParserActivity";
    
    private static class ZoneItem {
        public String id;
        public String displayName;
        public String gmtOffset;
    }
    
    private ArrayAdapter<ZoneItem> mZoneAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mZoneAdapter = new ArrayAdapter<ZoneItem>(this, android.R.layout.simple_list_item_2, android.R.id.text1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;
                if(row == null) {
                    row = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
                }

                ZoneItem item = getItem(position);
                TextView text1 = (TextView) row.findViewById(android.R.id.text1);
                TextView text2 = (TextView) row.findViewById(android.R.id.text2);

                text1.setText(item.displayName);
                text2.setText(item.gmtOffset);

                return row;
            }
        };
        for(ZoneItem piece : getZones(this)) {
            mZoneAdapter.add(piece);
        }
        
        getListView().setAdapter(mZoneAdapter);
    }
    
    /* Time Zone Parsing */
    private static final String TAG_TIMEZONE_LIST = "timezones";
    private static final String TAG_TIMEZONE = "timezone";

    /**
     * Parse the XML resource and return a list of Available Time Zones
     */
    private List<ZoneItem> getZones(Context context) {
        final List<ZoneItem> myData = new ArrayList<ZoneItem>();
        final long date = Calendar.getInstance().getTimeInMillis();
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.timezones);
            while (parser.next() != XmlResourceParser.START_TAG) { }

            parser.next();
            while (parser.getEventType() != XmlResourceParser.END_TAG) {
                while (parser.getEventType() != XmlResourceParser.START_TAG) {
                    if (parser.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return myData;
                    }
                    parser.next();
                }
                if (parser.getName().equals(TAG_TIMEZONE)) {
                    final ZoneItem zone = new ZoneItem();
                    
                    zone.id = parser.getAttributeValue(0);
                    zone.displayName = parser.nextText();
                    zone.gmtOffset = getOffset(zone.id, date);
                    
                    myData.add(zone);
                }
                while (parser.getEventType() != XmlResourceParser.END_TAG) {
                    parser.next();
                }
                parser.next();
            }
            parser  .close();
        } catch (XmlPullParserException xppe) {
            Log.e(LOGTAG, "Ill-formatted timezones.xml file");
        } catch (java.io.IOException ioe) {
            Log.e(LOGTAG, "Unable to read timezones.xml file");
        }

        return myData;
    }

    private static final int HOURS_1 = 60 * 60 * 1000;
    /**
     * Helper method to add a Time Zone item from the parsed XML data
     */
    private String getOffset(String id, long date) {
        final TimeZone tz = TimeZone.getTimeZone(id);
        final int offset = tz.getOffset(date);
        final int p = Math.abs(offset);
        final StringBuilder name = new StringBuilder();
        name.append("GMT");

        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }

        name.append(p / (HOURS_1));
        name.append(':');

        int min = p / 60000;
        min %= 60;

        if (min < 10) {
            name.append('0');
        }
        name.append(min);

        return name.toString();
    }
}
