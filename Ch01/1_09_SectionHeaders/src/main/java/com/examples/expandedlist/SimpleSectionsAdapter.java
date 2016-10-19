package com.examples.expandedlist;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleSectionsAdapter<T> extends BaseAdapter implements AdapterView.OnItemClickListener {

    /* Define constants for each view type */
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private LayoutInflater mLayoutInflater;
    private int mHeaderResource;
    private int mItemResource;

    /* Unique collection of all sections */
    private List<SectionItem<T>> mSections;
    /* Grouping of sections, keyed by their initial position */
    private SparseArray<SectionItem<T>> mKeyedSections;

    public SimpleSectionsAdapter(ListView parent, int headerResId, int itemResId) {
        mLayoutInflater = LayoutInflater.from(parent.getContext());
        mHeaderResource = headerResId;
        mItemResource = itemResId;

        //Create a collection with automatically sorted keys
        mSections = new ArrayList<SectionItem<T>>();
        mKeyedSections = new SparseArray<SectionItem<T>>();

        //Attach ourselves as the list's click handler
        parent.setOnItemClickListener(this);
    }

    /*
     * Add a new titled section to the list,
     * or update and existing one
     */
    public void addSection(String title, T[] items) {
        SectionItem<T> sectionItem = new SectionItem<T>(title, items);
        //Add the section, replacing any existing version with the same title
        int currentIndex = mSections.indexOf(sectionItem);
        if (currentIndex >= 0) {
            mSections.remove(sectionItem);
            mSections.add(currentIndex, sectionItem);
        } else {
            mSections.add(sectionItem);
        }

        //Sort the latest collection
        reorderSections();
        //Tell the view data has changed
        notifyDataSetChanged();
    }

    /*
     * Mark the sections with their initial global position
     * as a referable key
     */
    private void reorderSections() {
        mKeyedSections.clear();
        int startPosition = 0;
        for (SectionItem<T> item : mSections) {
            mKeyedSections.put(startPosition, item);
            //This count includes the header view
            startPosition += item.getCount();
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        for (SectionItem<T> item : mSections) {
            //Add the items count
            count += item.getCount();
        }

        return count;
    }

    @Override
    public int getViewTypeCount() {
        //Two view types: headers and items
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderAtPosition(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public T getItem(int position) {
        return findSectionItemAtPosition(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * Override and return false to tell the ListView we
     * have some items (headers) that aren't tappable
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    /*
     * Override to tell the ListView which items (headers)
     * are not tappable
     */
    @Override
    public boolean isEnabled(int position) {
        return !isHeaderAtPosition(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                return getHeaderView(position, convertView, parent);
            case TYPE_ITEM:
                return getItemView(position, convertView, parent);
            default:
                return convertView;
        }
    }

    private View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(mHeaderResource, parent, false);
        }

        SectionItem<T> item = mKeyedSections.get(position);
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);

        textView.setText(item.getTitle());

        return convertView;
    }

    private View getItemView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(mItemResource, parent, false);
        }

        T item = findSectionItemAtPosition(position);
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);

        textView.setText(item.toString());

        return convertView;
    }

    /** OnItemClickListener Methods */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        T item = findSectionItemAtPosition(position);
        if (item != null) {
            onSectionItemClick(item);
        }
    }

    /**
     * Override method to handle click events on specific elements
     * @param item List item the user clicked
     */
    public abstract void onSectionItemClick(T item);

    /* Helper Methods to Map Items to Sections */

    /*
     * Check is a global position value represent a
     * section's header.
     */
    private boolean isHeaderAtPosition(int position) {
        for (int i=0; i < mKeyedSections.size(); i++) {
            //If this position is a key value, it's a header position
            if (position == mKeyedSections.keyAt(i)) {
                return true;
            }
        }

        return false;
    }

    /*
     * Return the explicit list item for the given global
     * position.
     */
    private T findSectionItemAtPosition(int position) {
        int firstIndex, lastIndex;
        for (int i=0; i < mKeyedSections.size(); i++) {
            firstIndex = mKeyedSections.keyAt(i);
            lastIndex = firstIndex + mKeyedSections.valueAt(i).getCount();
            if (position >= firstIndex && position < lastIndex) {
                int sectionPosition = position - firstIndex - 1;
                return mKeyedSections.valueAt(i).getItem(sectionPosition);
            }
        }

        return null;
    }
}
