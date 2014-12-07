package com.automessaging;

import java.util.ArrayList;

import com.automessaging.common.Astat;
import com.automessaging.database.ProfilesDatabaseHelper;
import com.automessaging.readContacts.ContactListActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ReplyMsgActivity extends Activity {

	ProfilesDatabaseHelper mDBHelper = new ProfilesDatabaseHelper(
			ReplyMsgActivity.this);
	private EditText edt;
	private ImageView template_iv;
	private ListView recentMsg_lv;
	private AlertDialog dialog;
	private String strMsg, replyMessage, profileName, grpupName, startTime, stopTime, replyMsg_from_profile;
	private Button btn_cancel, btn_ok;
	private ArrayAdapter<String> ad;
	private ArrayList<MessageModel> replyMsg_list = null;
	private MessageListAdapter _MessageListAdapter = null;

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_reply_msg);
		InitializeUI();
		edt.setTextColor(Color.parseColor("#FFFFFF"));
		Intent intent = getIntent();
		profileName = intent.getStringExtra("profile_name");
		replyMsg_from_profile = intent.getStringExtra("msgFrom_profile");
		grpupName = intent.getStringExtra("Group_Name");
		edt.setText(replyMsg_from_profile);
		replyMsg_list = new ArrayList<ReplyMsgActivity.MessageModel>();
		getMessages();
		
		template_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(ReplyMsgActivity.this);
				builder.setTitle("Reply Message");
				final EditText editText = new EditText(ReplyMsgActivity.this);
				final ListView listview = new ListView(ReplyMsgActivity.this);
				LinearLayout layout = new LinearLayout(ReplyMsgActivity.this);
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.addView(editText);
				layout.addView(listview);
				builder.setView(layout);
				final String[] str = new String[] {
						"Sorry, I am in a meeting right now.",
						"I'll get back to you.",
						"I'm driving now. I'll call you back later.",
						"I'm very busy now..." };
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						ReplyMsgActivity.this,
						android.R.layout.simple_list_item_1,
						android.R.id.text1, str);
				listview.setAdapter(adapter);
				listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						strMsg = str[position];
						editText.setText(strMsg);

					}
				});

				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String getMsg = editText.getText().toString();
								edt.setText(getMsg);
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				dialog = builder.create();
				dialog.show();
			}
		});

		recentMsg_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						String selectedMsg = replyMsg_list.get(position)
								.getMessage();
						edt.setText(selectedMsg);
					}
				});
		
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				replyMessage = edt.getText().toString();
				Log.v(this.getClass().getName(), "check.." + replyMessage);
				Log.v(this.getClass().getName(), "check.." + profileName);
				if (replyMessage.length()>2 && !replyMessage.trim().isEmpty() && replyMessage.length() <= 120 && profileName != null) {
					mDBHelper.storeReplyMsg(profileName, replyMessage);
					mDBHelper.deleteMessages();
					Intent replyMsgIntent = new Intent(ReplyMsgActivity.this, ProfileActivity.class).putExtra("reply_msg", replyMessage);
					startActivity(replyMsgIntent);
					
				}if (replyMessage.length() > 120) {
					Toast.makeText(getApplicationContext(), "Message should not exceed 120 characters !", Toast.LENGTH_LONG).show();
				}if(replyMessage.length()<2){
					Toast.makeText(getApplicationContext(), "Enter a Reply Message.", Toast.LENGTH_LONG).show();
				}if(profileName== null && grpupName != null){
					Intent replyMsgIntent = new Intent(ReplyMsgActivity.this, ContactListActivity.class).putExtra("reply_msg", replyMessage);
					startActivity(replyMsgIntent);
				}if(!(replyMessage.length() <2)){
					finish();
				}

				
			}
			
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ReplyMsgActivity.this.finish();
			}
		});

	}

	protected void onResume() {
		super.onResume();
		
	}
	private void getMessages() {
		// TODO Auto-generated method stub
		Cursor objcur = mDBHelper.getMessages("SELECT * FROM ReplyMessagesTable ORDER BY _Id DESC LIMIT 5");
		try {
			if (null != objcur && objcur.moveToFirst()) {
				do {

					MessageModel _messageData = new MessageModel();
					_messageData.setMessage(objcur.getString(2));
					_messageData.set_id(objcur.getLong(0));
					replyMsg_list.add(_messageData);
					_messageData = null;
				} while (objcur.moveToNext());
			}
		} catch (Exception ex) {
		} finally {
			objcur.close();
		}

		_MessageListAdapter = new MessageListAdapter();
		recentMsg_lv.setAdapter(_MessageListAdapter);
		
	}

	private void InitializeUI() {
		// TODO Auto-generated method stub

		edt = (EditText) findViewById(R.id.reply_msg_edittext);
		template_iv = (ImageView) findViewById(R.id.template_imageView);
		btn_cancel = (Button) findViewById(R.id.button1);
		btn_ok = (Button) findViewById(R.id.button2);
		recentMsg_lv = (ListView) findViewById(R.id.reply_msg_listview);
	}

	public class MessageListAdapter extends BaseAdapter {

		LayoutInflater inflater;

		public MessageListAdapter() {
			this.inflater = LayoutInflater.from(ReplyMsgActivity.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return replyMsg_list == null ? 0 : replyMsg_list.size();
		}

		@Override
		public MessageModel getItem(int position) {
			// TODO Auto-generated method stub
			return replyMsg_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			final ViewHolder holder = convertView == null ? new ViewHolder(): (ViewHolder) convertView.getTag();

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.replymsg_list_item,null);
				holder.tv = (TextView) convertView.findViewById(R.id.replyMsg_list_item_textView);
				holder.tv.setText(replyMsg_list.get(position).getMessage());
				convertView.setTag(holder);
			}

			return convertView;
		}

		public class ViewHolder {

			private TextView tv;
		}

	}

	public class MessageModel {

		public String msg;
		public long _id;

		public long get_id() {
			return _id;
		}

		public void set_id(long _id) {
			this._id = _id;
		}

		public String getMessage() {
			return msg;
		}

		public void setMessage(String message) {
			this.msg = message;
		}

	}

}
