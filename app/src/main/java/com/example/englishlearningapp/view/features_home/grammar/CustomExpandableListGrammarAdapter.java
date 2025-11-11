package com.example.englishlearningapp.view.features_home.grammar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.englishlearningapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CustomExpandableListGrammarAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> groupList;
    private final HashMap<String, List<String>> childMap;

    public CustomExpandableListGrammarAdapter(Context context, List<String> groupList, HashMap<String, List<String>> childMap) {
        this.context = context;
        this.groupList = groupList;
        this.childMap = childMap;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(childMap.get(groupList.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(childMap.get(groupList.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_grammar, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.list_title);
        lblListHeader.setText(headerTitle);

        ImageView groupIndicator = convertView.findViewById(R.id.group_indicator);
        if (isExpanded) {
            groupIndicator.setImageResource(R.drawable.ic_arrow_drop_up_24dp);
        } else {
            groupIndicator.setImageResource(R.drawable.ic_arrow_drop_down_24dp);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_grammar, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.expanded_list_item);
        txtListChild.setText(childText);

        View divider = convertView.findViewById(R.id.divider);
        if (isLastChild) {
            divider.setVisibility(View.GONE);
        } else {
            divider.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}