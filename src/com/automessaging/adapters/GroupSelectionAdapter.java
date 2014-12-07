package com.automessaging.adapters;

import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.automessaging.R;
import com.automessaging.groups.GroupsActivity.GroupsModel;

public class GroupSelectionAdapter extends ArrayAdapter<GroupsModel>{
	
	
	public Context mContext;
	private LayoutInflater inflater;
	protected List<GroupsModel> groups_list;
    private SparseBooleanArray mSelectedItemsIds;
	
    public GroupSelectionAdapter(Context context, int resourceId,
            List<GroupsModel> groupsListItemTextview) {
        super(context, resourceId, groupsListItemTextview);
        mSelectedItemsIds = new SparseBooleanArray();
        this.mContext = context;
        this.groups_list = groupsListItemTextview;
        inflater = LayoutInflater.from(context);
    }
    
    public class ViewHolder {
    	
    	TextView tv;
    	RelativeLayout rv;

	}

    @Override
    public void remove(GroupsModel object) {
    	groups_list.remove(object);
        notifyDataSetChanged();
    }
 
    public List<GroupsModel> getWorldPopulation() {
        return groups_list;
    }
 
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }
 
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
 
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }
 
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }
 
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
    	ViewHolder holder = view == null ? new ViewHolder() : (ViewHolder) view.getTag();
//    	View v = super.getView(position, view, parent);//let the adapter handle setting up the row views
//    	v.setBackgroundColor(mContext.getResources().getColor(android.R.color.background_light)); //default color
//    	if(mSelectedItemsIds.get(position)){
//    		v.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red
//    	}
    	if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.groups_list_item, null);
            // Locate the TextViews in listview_item.xml
            holder.tv = (TextView) view.findViewById(R.id.groups_list_item_textView);
            //holder.rv = (RelativeLayout) view.findViewById(R.id.groups_relativeLayout);
            
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        holder.tv.setText(groups_list.get(position).getGroupName());
        
        return view;
    }

}
