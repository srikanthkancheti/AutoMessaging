package com.automessaging.readContacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.automessaging.R;
import com.automessaging.common.Astat;
import com.automessaging.database.ProfilesDatabaseHelper;

public class SelectContactsActivity extends Activity{
	
	private ListView select_listView;
	private EditText search_edt;
	private List<ContactBean> list = new ArrayList<ContactBean>();
	private ContanctAdapter objAdapter;
	private boolean UpdateAB;
	private String groupName;
	private ProfilesDatabaseHelper DbHelper = new ProfilesDatabaseHelper(SelectContactsActivity.this);
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_selectcontacts);
		
		select_listView = (ListView) findViewById(R.id.select_contacts_listView);
		search_edt = (EditText) findViewById(R.id.inputSearch);
		Intent intent = getIntent();
		groupName = intent.getStringExtra("group_name");
		
		Cursor phones = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (phones.moveToNext()) {

			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

			String phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			ContactBean objContact = new ContactBean();
			objContact.setName(name);
			objContact.setPhoneNo(phoneNumber);
			list.add(objContact);
		}
		phones.close();
		Astat.contactCheckArray = new boolean[list.size()];
		objAdapter = new ContanctAdapter(SelectContactsActivity.this, R.layout.select_contacts_list_item, list);
		select_listView.setAdapter(objAdapter);
		
		objAdapter.setEditMode(true);
		objAdapter.notifyDataSetChanged();
		UpdateAB = true;
		invalidateOptionsMenu();

		if (null != list && list.size() != 0) {
			Collections.sort(list, new Comparator<ContactBean>() {

				@Override
				public int compare(ContactBean lhs, ContactBean rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});

		} else {
			showToast("No Contact Found!!!");
		}
		
		select_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listview, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				ContactBean bean = (ContactBean) listview.getItemAtPosition(position);
				String phoneNumber = bean.getPhoneNo();	
				String name = bean.getName();
				
				//Toast.makeText(getApplicationContext(), "Clicked on: "+phoneNumber, Toast.LENGTH_LONG).show();
			}
		});
		
		/**
         * Enabling Search Filter
         * */
		// Capture Text in EditText
		search_edt.addTextChangedListener(new TextWatcher() {
 
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = search_edt.getText().toString().toLowerCase(Locale.getDefault());
                objAdapter.filter(text);
            }
 
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
 
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
            }
        });
	}

	private void showToast(String msg) {
		// TODO Auto-generated method stub
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	      getMenuInflater().inflate(R.menu.actions_select_contacts_list, menu);
	      MenuItem item = null;
	      
	      if(!UpdateAB){
	    	  menu.findItem(R.id.action_done).setEnabled(false).setVisible(false);
				//menu.findItem(R.id.action_add).setEnabled(true).setVisible(true);
				item = menu.add(Menu.NONE, R.id.action_new , Menu.NONE, "Add Number");
	      }else{
	    	    menu.findItem(R.id.action_done).setEnabled(true).setVisible(true);
				//menu.findItem(R.id.action_add).setEnabled(false).setVisible(false);
				item = menu.add(Menu.NONE, R.id.action_done, Menu.NONE,R.string.done);
	      }
		return true;
	      
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {

			case R.id.action_done:
				StringBuilder _itemBuilder = new StringBuilder();
				//objAdapter.setEditMode(false);
				objAdapter.notifyDataSetChanged();
				UpdateAB = false;
				
				for (int i = 0; i < list.size(); i++) {
					if (Astat.contactCheckArray[i]) {
						_itemBuilder.append("'"
								+ list.get(i).getPhoneNo() + "'" + ",");
						DbHelper.executeSQL("INSERT INTO GroupsTable (GroupName, ContactName, PhoneNumber) VALUES ('"+groupName+"', '"+list.get(i).getName()+"','"+ list.get(i).getPhoneNo()+ "')");
					}
				}
				if (_itemBuilder.length() > 0) {
					_itemBuilder.deleteCharAt(_itemBuilder.length() - 1);
					Log.v(getClass().getName(), "Check..selected contactss"
							+ _itemBuilder.toString());
					//Toast.makeText(getApplicationContext(), "Selected Contacts : "+_itemBuilder.toString(), Toast.LENGTH_LONG).show();
					// This will clear the buffer
					_itemBuilder.delete(0, _itemBuilder.length());
				
				}
				
				invalidateOptionsMenu();
				
				finish();
				break;

			}
			return true;
			
	  }
		
}
