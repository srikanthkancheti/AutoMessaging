package com.automessaging.groups;

import java.sql.SQLException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.automessaging.ProfileActivity;
import com.automessaging.R;
import com.automessaging.database.ProfilesDatabaseHelper;
import com.automessaging.readContacts.ContactListActivity;

public class GroupsActivity extends Activity {
	
	private ProfilesDatabaseHelper mDBHelper;
	private boolean[] checkArray;
	private ArrayList<GroupsModel> groups_list = null;
	private ListView groups_listView;
	private boolean isUpdateAB = false;
	private GroupsListAdapter _GroupsListAdapter = null;
	private int count;

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_groups);
		groups_listView = (ListView) findViewById(R.id.groups_listView);
		groups_list = new ArrayList<GroupsActivity.GroupsModel>();
		mDBHelper = new ProfilesDatabaseHelper(this);
		try {
			mDBHelper.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getGroups();
		
		_GroupsListAdapter.notifyDataSetChanged();
		SelectAB_Update();
		groups_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView temp = (TextView) view.findViewById(R.id.groups_list_item_textView);
			    String groupName = temp.getText().toString();
			    String replyMsg = mDBHelper.getRelpyMessage(groupName);
				Intent selectContactsIntent = new Intent(GroupsActivity.this, ContactListActivity.class).putExtra("group_name", groupName).putExtra("from_groups_reply_msg", replyMsg);
				startActivity(selectContactsIntent);
			}
		});
		
		groups_listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView temp = (TextView) view.findViewById(R.id.groups_list_item_textView);
			    final String groupName = temp.getText().toString();
			    AlertDialog.Builder alert = new AlertDialog.Builder(GroupsActivity.this);

				// alert.setTitle("Note Search");
				alert.setTitle("Delete");
				alert.setMessage("Are you sure ! you want to delete "+groupName +"?");

				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mDBHelper.deleteGroups(groupName);
						mDBHelper.deleteGroupContacts(groupName);
						getGroups();
						 _GroupsListAdapter.notifyDataSetChanged();
						 if(!(groups_listView.getCount()>0)){
							 finish();
						 }
						 SelectAB_Update();
						invalidateOptionsMenu();
					}

				});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// Canceled.
								dialog.dismiss();
								
							}
						});
				
				alert.show();
				return false;
			}
		});

	}

	private void getGroups() {
		// TODO Auto-generated method stub
		groups_list.clear();
		Cursor objcur=mDBHelper.getGroupNames("SELECT * FROM GroupDetails");
		try{
		if(objcur!=null&&objcur.moveToFirst()){
			int count=0;
			objcur.moveToPosition(count);
			do{
				GroupsModel _groupsModel = new GroupsModel();
				_groupsModel.setGroupName(objcur.getString(1));
				_groupsModel.set_id(objcur.getLong(0));
				groups_list.add(_groupsModel);
				_groupsModel = null;
				
			}while(objcur.moveToNext());			
		}
		}catch(Exception e){
			
			System.out.print("get Groups check...."+e.getMessage());
		}finally{
			//objcur.close();
		}
		
		checkArray = new boolean[groups_list.size()];
		_GroupsListAdapter = new GroupsListAdapter();
		groups_listView.setAdapter(_GroupsListAdapter);
		_GroupsListAdapter.notifyDataSetChanged();
		invalidateOptionsMenu();
	}

	protected void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
		getGroups();
		SelectAB_Update();
		invalidateOptionsMenu();
	}

	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
		getGroups();
		SelectAB_Update();
		_GroupsListAdapter.notifyDataSetChanged();
		invalidateOptionsMenu();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		Intent intent = new Intent(GroupsActivity.this, ProfileActivity.class);
//		startActivity(intent);
		finish();
	}
	
	public void SelectAB_Update(){
		if(groups_listView.getCount()>0){
			isUpdateAB = false;
		 }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_groups_actions, menu);
		MenuItem item = null;
		if (groups_listView.getCount() > 0) {
			if(!isUpdateAB){
				//menu.findItem(R.id.action_discard).setEnabled(true).setVisible(true);
				menu.findItem(R.id.action_select).setEnabled(true).setVisible(true);
				menu.findItem(R.id.action_new).setEnabled(true).setVisible(true);
				menu.findItem(R.id.action_done).setEnabled(false).setVisible(false);
				item = menu.add(Menu.NONE, R.id.action_new, Menu.NONE, R.string.new_group);
				//item = menu.add(Menu.NONE, R.id.action_discard , Menu.NONE,R.string.delete);
				item = menu.add(Menu.NONE, R.id.action_select, Menu.NONE, "Select");
			}else{
				menu.findItem(R.id.action_done).setEnabled(true).setVisible(true);
				//menu.findItem(R.id.action_discard).setEnabled(false).setVisible(false);
				menu.findItem(R.id.action_select).setEnabled(false).setVisible(false);
				menu.findItem(R.id.action_new).setEnabled(false).setVisible(false);
				item = menu.add(Menu.NONE, R.id.action_done, Menu.NONE,R.string.done);
			}
		}else{
			menu.findItem(R.id.action_select).setEnabled(false).setVisible(false);
			//menu.findItem(R.id.action_discard).setEnabled(false).setVisible(false);
			menu.findItem(R.id.action_done).setEnabled(false).setVisible(false);
			menu.findItem(R.id.action_new).setEnabled(true).setVisible(true);
			item = menu.add(Menu.NONE, R.id.action_new, Menu.NONE, R.string.new_group);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {

		case R.id.action_new:
			// New profile action
			 GroupDialog();
			return true;
			
		case R.id.action_done:
			
			StringBuilder _itemBuilder = new StringBuilder();
			StringBuilder _itemBuild = new StringBuilder();
			_GroupsListAdapter.setEditMode(false);
			_GroupsListAdapter.notifyDataSetChanged();
        	isUpdateAB = false;
        	invalidateOptionsMenu();
        	for(int i =0;i<groups_list.size();i++){
        		
                if(checkArray[i]) {
                	count = mDBHelper.groupDetailsCount(groups_list.get(i).getGroupName());
                	if(count >0){
                	_itemBuilder.append(groups_list.get(i).getGroupName()+ ",");
                	} else{
                		_itemBuild.append(groups_list.get(i).getGroupName()+", ");
                		
                	}
                }
            
        	}
	        	if(!(_itemBuild.length()>0)){
	        		_itemBuilder.deleteCharAt(_itemBuilder.length()-1);
	        		Log.v(getClass().getName(), "Check.."+_itemBuilder.toString());
	        		Intent groupsIntent = new Intent(GroupsActivity.this, ProfileActivity.class).putExtra("selected_groups", _itemBuilder.toString());
	        		startActivity(groupsIntent);
	        		//finish();
	        	}else{
	        		_itemBuild.deleteCharAt(_itemBuild.length()-1);
	        		Toast.makeText(getApplicationContext(), ""+_itemBuild+" has no contacts !", Toast.LENGTH_LONG).show();
	        	}
		
        	getGroups();
        	
            break;
			

		case R.id.action_select:
			_GroupsListAdapter.setEditMode(true);
			_GroupsListAdapter.notifyDataSetChanged();
        	isUpdateAB = true;
        	invalidateOptionsMenu();
			
		}
		return true;

	}

	private void GroupDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alert = new AlertDialog.Builder(GroupsActivity.this);

		// alert.setTitle("Note Search");
		alert.setTitle("Enter Group Name");

		// Set an EditText view to get user input
		final EditText input = new EditText(GroupsActivity.this);
		input.setHint("Group Name");
		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				// Do something with value!
				if (value.length() == 0) {
					input.setError("Please enter a group name");

				} else {
					
					mDBHelper.executeSQL("INSERT INTO GroupDetails (GroupName) VALUES ('"+ value + "')");
					getGroups();
					dialog.dismiss();
				}
			}

		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						dialog.dismiss();
					}
				});

		alert.show();

	}

	public class GroupsListAdapter extends BaseAdapter{
		
		LayoutInflater inflater;
        private boolean isEdit;

        public GroupsListAdapter(){
            this.inflater = LayoutInflater.from(GroupsActivity.this);
        }

		public void setEditMode(boolean b) {
			// TODO Auto-generated method stub
			this.isEdit = b;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return groups_list == null ? 0 : groups_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return groups_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder = convertView == null ? new ViewHolder() : (ViewHolder) convertView.getTag();
			if(convertView == null){
                convertView = inflater.inflate(R.layout.groups_list_item, null);
               // holder.tv = (TextView) findViewById(R.id.groups_list_item_textView);
                holder.tv = (TextView) convertView.findViewById(R.id.groups_list_item_textView);
                holder.iv = (ImageView) convertView.findViewById(R.id.group_checkbox_imageView);
                holder.rv = (RelativeLayout) convertView.findViewById(R.id.groups_relativeLayout);
                holder.ll = (LinearLayout) convertView.findViewById(R.id.groups_listItem_groupName_ll);
                convertView.setTag(holder);
            }
			
			holder.tv.setText(groups_list.get(position).getGroupName());
			   if(isEdit){
	                holder.iv.setVisibility(View.VISIBLE);
	                holder.iv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							 if(checkArray != null && checkArray[position]){
				                    checkArray[position] = false;
				                    _GroupsListAdapter.notifyDataSetChanged();
				                }else{
				                    checkArray[position] = true;
				                    _GroupsListAdapter.notifyDataSetChanged();
				                }
						}
					});
	               
	                if(checkArray != null && checkArray[position]){
	                    holder.iv.setImageResource(R.drawable.setting_check);
	                }else{
	                    holder.iv.setImageResource(R.drawable.setting_check_box_bg);
	                }
	            }else{
	                holder.iv.setVisibility(View.GONE);
	            }
			   

			return convertView;
		}

		public class ViewHolder {
			public LinearLayout ll;
			public ImageView iv;
			public RelativeLayout rv;
			public TextView tv;

		}

	}
	
	public class GroupsModel {

		public  String groupName;
		Long _id;
		
		public long get_id() {
			return _id;
		}

		public void set_id(long _id) {
			this._id = _id;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String title) {
			this.groupName = title;
		}
	}
}
