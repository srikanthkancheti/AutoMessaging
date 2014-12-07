package com.automessaging.readContacts;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.automessaging.R;
import com.automessaging.ReplyMsgActivity;
import com.automessaging.common.Astat;
import com.automessaging.database.ProfilesDatabaseHelper;
import com.automessaging.groups.GroupsActivity;

public class ContactListActivity extends Activity{
	
	private SelectedContactsAdapter _SelectedContactsAdapter;
	private ListView selected_listView;
	private RelativeLayout selectContacts_rl, selectedContacts_rl;
	private RelativeLayout group_reply_message;
	private EditText replyMessage_edt;
	private ActionBar actionBar;
	private String groupName, group_replyMessage, from_groups_reply_msg;
	private TextView isEmpty_tv;
	private Button ok_btn;
	private ImageView group_reply_template_imageView;
	private ArrayList<SelectedContactsPOJO> selectedContacts = new ArrayList<SelectedContactsPOJO>();
	private ProfilesDatabaseHelper DbHelper = new ProfilesDatabaseHelper(ContactListActivity.this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selected_contacts);

		//select_listView = (ListView) findViewById(R.id.select_contacts_listView);
		selected_listView = (ListView)findViewById(R.id.selected_contacts_listView);
    	//selectContacts_rl = (RelativeLayout) findViewById(R.id.select_contact_rl);
    	selectedContacts_rl =(RelativeLayout) findViewById(R.id.selected_contacts_rl);
    	isEmpty_tv = (TextView) findViewById(R.id.selected_contacts_isEmpty_tv);
    	//group_reply_message = (RelativeLayout) findViewById(R.id.selected_contacts_reply_message_ll);
    	replyMessage_edt = (EditText) findViewById(R.id.groups_reply_editText);
    	ok_btn = (Button) findViewById(R.id.selected_contacts_button);
    	group_reply_template_imageView = (ImageView) findViewById(R.id.group_reply_template_imageView);
    	Intent intent = getIntent();
		groupName = intent.getStringExtra("group_name");
		group_replyMessage = intent.getStringExtra("reply_msg");
		from_groups_reply_msg = intent.getStringExtra("from_groups_reply_msg");
		actionBar = getActionBar();
		actionBar.setTitle(groupName);
		
		if(group_replyMessage != null){
			replyMessage_edt.setText(group_replyMessage);
			groupName = Astat.groupName;
			actionBar.setTitle(groupName);
			getSelectedContacts();
		}else if(groupName.length() == 0 ){
			groupName = Astat.groupName;
			actionBar.setTitle(groupName);
			getSelectedContacts();
			
		}else if(group_replyMessage == null && groupName.length()>0){
			actionBar.setTitle(groupName);
			replyMessage_edt.setText(from_groups_reply_msg);
			getSelectedContacts();
			
		}
		
    	
    	_SelectedContactsAdapter.notifyDataSetChanged();
    	
    	group_reply_template_imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Astat.groupName = groupName;
				Intent replyIntent = new Intent(ContactListActivity.this, ReplyMsgActivity.class).putExtra("Group_Name", groupName);
				startActivity(replyIntent);
				finish();
			}
		});
    	
    	ok_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String replyMessage = replyMessage_edt.getText().toString();
				
				if(replyMessage.length()>0){
					
					if(selected_listView.getCount() >0){
						
						StringBuilder _itemBuilder = new StringBuilder();
						for(int i =0; i<_SelectedContactsAdapter.getCount(); i++){
							//String number = selectedContacts.get(i).getPhoneNumber();
							_itemBuilder.append(selectedContacts.get(i).getPhoneNumber()+ ",");
							}

					
						if(_itemBuilder.length() >0){
							_itemBuilder.deleteCharAt(_itemBuilder.length() - 1);
							DbHelper.storeSelectedContacts(_itemBuilder.toString(), groupName);
							DbHelper.storeGroupReplyMsg(replyMessage, groupName);
							Intent contactListIntent = new Intent(ContactListActivity.this, GroupsActivity.class);
							startActivity(contactListIntent);
							finish();
						}
    				
					}else{
						Toast.makeText(getApplicationContext(), "Group must contain at least one contact !", Toast.LENGTH_LONG).show();
			  	      }
				}else{
					Toast.makeText(getApplicationContext(), "Enter a reply message !", Toast.LENGTH_LONG).show();
			      }
			}
		});
	}

	protected void onResume() {
		super.onResume();
		getSelectedContacts();
		actionBar.setTitle(groupName);
		_SelectedContactsAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.actions_contacts_list, menu);
      MenuItem item = null;
      
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
			String reply = replyMessage_edt.getText().toString();
			if(reply != null){
			Intent selectContactsIntent = new Intent(ContactListActivity.this, SelectContactsActivity.class)
			.putExtra("group_name", groupName)
			.putExtra("group_reply_message", reply);
			startActivity(selectContactsIntent);
	
			}else{
				Toast.makeText(getApplicationContext(), "Please enter a reply message !", Toast.LENGTH_LONG).show();
			}
			return true;

		}
		return true;
		
  }

private void getSelectedContacts() {
	// TODO Auto-generated method stub
	selectedContacts.clear();
	Cursor objcur=DbHelper.getGroupNames("SELECT * FROM GroupsTable WHERE GroupName = '"+groupName+"'");
	try{
	if(objcur!=null&&objcur.moveToFirst()){
		int count=0;
		objcur.moveToPosition(count);
		do{
			SelectedContactsPOJO _contactModel = new SelectedContactsPOJO();
			_contactModel.setContactname(objcur.getString(3));
			_contactModel.setPhoneNumber(objcur.getString(4));
			_contactModel.set_id(objcur.getLong(0));
			selectedContacts.add(_contactModel);
			_contactModel = null;
			
		}while(objcur.moveToNext());			
	}
	}catch(Exception e){
		
		System.out.print("get Groups check...."+e.getMessage());
	}finally{
		objcur.close();
	}
	
	_SelectedContactsAdapter = new SelectedContactsAdapter();
	selected_listView.setAdapter(_SelectedContactsAdapter);
	_SelectedContactsAdapter.notifyDataSetChanged();
	
	if(selected_listView.getCount() == 0){
		isEmpty_tv.setVisibility(View.VISIBLE);
		selected_listView.setVisibility(View.INVISIBLE);
	}else{
		isEmpty_tv.setVisibility(View.INVISIBLE);
		selected_listView.setVisibility(View.VISIBLE);

	}
	

}

public class SelectedContactsAdapter extends BaseAdapter{
	
	LayoutInflater inflater;
	
	 public SelectedContactsAdapter(){
         this.inflater = LayoutInflater.from(ContactListActivity.this);
     }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return selectedContacts == null ? 0 : selectedContacts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return selectedContacts.get(position);
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
            convertView = inflater.inflate(R.layout.selected_contacts_listview_item, null);
            holder.tv1 = (TextView) convertView.findViewById(R.id.selected_contact_name_textView);
            holder.tv2 = (TextView) convertView.findViewById(R.id.selected_contact_number_textView);
            holder.iv = (ImageView) convertView.findViewById(R.id.remove_contact_imageView);
            convertView.setTag(holder);
        }
		
		holder.tv1.setText(selectedContacts.get(position).getContactname());
		holder.tv2.setText(selectedContacts.get(position).getPhoneNumber());
		holder.iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phoneNumber = selectedContacts.get(position).getPhoneNumber();
				DbHelper.deleteGroupsFromDb(phoneNumber);
				getSelectedContacts();
				_SelectedContactsAdapter.notifyDataSetChanged();
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder {

		public TextView tv1, tv2;
		public ImageView iv;

	}

}

public class SelectedContactsPOJO {
	
	public  String contactName, phoneNumber;
	Long _id;

	public String getContactname() {
		return contactName;
	}
	public void setContactname(String string) {
		// TODO Auto-generated method stub
		this.contactName = string;
	}
	
	public long get_id() {
		return _id;
	}
	public void set_id(long long1) {
		// TODO Auto-generated method stub
		this._id = long1;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String string) {
		// TODO Auto-generated method stub
		this.phoneNumber = string;
	}

}
}
