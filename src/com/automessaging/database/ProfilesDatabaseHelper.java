package com.automessaging.database;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProfilesDatabaseHelper extends SQLiteOpenHelper {

	private static ProfilesDatabaseHelper mDBconnection;
	private SQLiteDatabase database;
	public static String Lock = "dblock";
	public static final String KEY_PROFILE = "ProfileName";
	public static final String KEY_START = "StartDate";
	public static final String KEY_STOP = "StopDate";
	public static final String KEY_ROWID = "_Id";
	public static final String KEY_START_HOUR = "StartHourOfDay";
	public static final String KEY_START_MINUTE = "StartMinOfHour";
	public static final String KEY_START_SECOND = "StartSecOfMin";
	public static final String KEY_STOP_HOUR = "StopHourOfDay";
	public static final String KEY_STOP_MINUTE = "StopMinOfHour";
	public static final String KEY_STOP_SECOND = "StopSecOfMin";
	public static final String KEY_SELECTED_GROUPS = "SelectedGroups";
	private static final String DATABASE_NAME = "OnePlace.db";
	private static final String DATABASE_TABLE = "ProfileDetails";
	private static final String TABLE_PROFILE_DETAILS_TAG = "ProfileDetails";
	public static final String KEY_REPLY_MESSAGE = "Message";
	private static final String REPLY_MSG_TABLE = "ReplyMessagesTable";
	public static final String KEY_GROUP_NAME = "GroupName";
	private static final String GROUP_NAMES_TABLE = "GroupDetails";
	private static final String Key_PHONE_NUMBER = "PhoneNumber";
	private static final String GROUPS_TABLE = "GroupsTable";
	private static final String GROUPS_TABLE_REPLY = "GroupReplyMessage";
	private static final String GROUPS_TOGGLE_STATUS = "ToggleStatus";
	
	private static final int DATABASE_VERSION = 2;

	public ProfilesDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public static synchronized ProfilesDatabaseHelper getDBAdapterInstance(
			Context context) {
		synchronized (Lock) {
			if (mDBconnection == null) {
				mDBconnection = new ProfilesDatabaseHelper(context);
			}
			return mDBconnection;
		}

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		db.execSQL(" CREATE TABLE ProfileDetails (_Id  INTEGER primary key autoincrement, ProfileName varchar(50), StartDate varchar(50), StopDate varchar(50), "
				+ "StartHourOfDay INTEGER, StartMinOfHour INTEGER, StopHourOfDay INTEGER, StopMinOfHour INTEGER, SelectedGroups varchar(200), ToggleStatus INTEGER);");
		db.execSQL(" CREATE TABLE ReplyMessagesTable (_Id  INTEGER primary key autoincrement, ProfileName varchar(50), Message varchar(120) NOT NULL);");
		db.execSQL(" CREATE TABLE GroupsTable (_Id  INTEGER primary key autoincrement, GroupName varchar(50), GroupReplyMessage varchar(120), ContactName varchar(50), PhoneNumber varchar(50));");
		db.execSQL(" CREATE TABLE GroupDetails (_Id  INTEGER primary key autoincrement, GroupName varchar(50), Message varchar(120), PhoneNumber varchar(50));");
		db.execSQL(" CREATE TABLE ScheduleMessageDetails (_Id INTEGER primary key autoincrement, Numbers varchar(200), ScheduleTime varchar(50), ScheduleMessage varchar(200));");
		Log.v("DataBaseHelper Class", "Table Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void storeTime(String string) {
		try {
			open();
			database.execSQL(string);
		} catch (Exception e) {
			System.out.println("Error:::" + e.getMessage());
			e.printStackTrace();
		} finally {
			//database.close();
		}
	}

	
	public void open() throws SQLException {
		// TODO Auto-generated method stub
		try{
		database = this.getWritableDatabase();
		}catch(Exception e){
			System.out.println("Error::::" + e.getMessage());	
		}

	}

	public Cursor getProfiles(String string) {
		// TODO Auto-generated method stub
		Cursor profileCur = null;
		try {
			open();
			profileCur = database.rawQuery(string, null);

		} catch (Exception e) {
			profileCur = null;
			System.out.println("Error::::" + e.getMessage());
		} finally {

		}
		return profileCur;
	}

	public Cursor fetchAllProfiles() {

		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_PROFILE, KEY_START, KEY_STOP }, null, null, null, null,
				null);
	}

	/**
	 * Delete the profiles with the given rowId
	 * 
	 * @param rowId
	 *            id of profile to delete
	 * @return true if deleted, false otherwise
	 */
	public void deleteProfilesFromDb(String dataStr) {
		database.execSQL("DELETE from ProfileDetails where ProfileName IN ("+ dataStr + ")");
	}



	public Cursor getMessages(String string) {
		// TODO Auto-generated method stub
		Cursor messageCur = null;
		try {
			open();
			messageCur = database.rawQuery(string, null);

		} catch (Exception e) {
			messageCur = null;
			System.out.println("Error::::" + e.getMessage());
		} finally {

		}
		return messageCur;
	}

	public void executeSQL(String string) {
		// TODO Auto-generated method stub
		try {
			open();
			database.execSQL(string);
		} catch (Exception e) {
			System.out.println("Error:::" + e.getMessage());
		} finally {
			//database.close();
		}
	}

	public Cursor getGroupNames(String string) {
		// TODO Auto-generated method stub
		Cursor groupsCur = null;
		try {
			open();
			groupsCur = database.rawQuery(string, null);

		} catch (Exception e) {
			groupsCur = null;
			System.out.println("Error::::" + e.getMessage());
		} finally {

		}
		return groupsCur;
	}

	public void deleteGroupsFromDb(String string) {
		// TODO Auto-generated method stub
//		database.execSQL("DELETE from GroupsTable where GroupName IN ("
//				+ string + ")");
		
		database.execSQL("DELETE from GroupsTable where PhoneNumber = '"
				+ string + "'");
	}
	
	public void deleteGroups(String string) {
		database.execSQL("DELETE from GroupDetails where GroupName = '"+ string + "'");
	}

	public String getRelpyMessage(String groupName) {
		String msg = null;
		Cursor cursor = database.rawQuery("Select Message FROM GroupDetails WHERE GroupName = '"+groupName+"'", null);
		if(cursor!=null && cursor.getCount()>0)
		{
		      cursor.moveToFirst();
		        do {
		        	msg = cursor.getString(0);
		        } while (cursor.moveToNext());
		      }           
		
		      return msg;
		
	}

	public void storeGroupReplyMsg(String replyMessage, String groupName) {
		// TODO Auto-generated method stub
		ContentValues args = new ContentValues();
        args.put(KEY_REPLY_MESSAGE, replyMessage);
        database.update(GROUP_NAMES_TABLE, args,
                KEY_GROUP_NAME + "= ?", new String[]{ groupName });
       // database.update(GROUP_NAMES_TABLE, args, KEY_GROUP_NAME + "='" + groupName+"'", null);
       // database.update(GROUP_NAMES_TABLE, args, KEY_GROUP_NAME + "=" + groupName, null);
	}

	public void deleteMessages() {
		// TODO Auto-generated method stub
		String GET_ROW_COUNT = "SELECT count(*) FROM ReplyMessagesTable;";
		Long Count = database.compileStatement(GET_ROW_COUNT).simpleQueryForLong();
		if(Count>5){
			database.execSQL("DELETE FROM ReplyMessagesTable WHERE _Id = '1'");
		//database.execSQL("DELETE FROM ReplyMessagesTable WHERE _Id NOT IN (SELECT *FROM ReplyMessagesTable LIMIT 5)");
		}
	}

	public void storeReplyMsg(String profileName, String replyMessage) {
		// TODO Auto-generated method stub
		ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PROFILE, profileName);
        initialValues.put(KEY_REPLY_MESSAGE, replyMessage);
        
        database.insert(REPLY_MSG_TABLE, null, initialValues);
       // initialValues.clear();
	}

	public String getProfileReplyMessage(String tempProfileName) {
		// TODO Auto-generated method stub
		try {
			open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			String msg = null;
			Cursor cursor = database.rawQuery("Select Message FROM ReplyMessagesTable WHERE ProfileName = '"+tempProfileName+"'", null);
			if(cursor!=null && cursor.getCount()>0)
			{
			      cursor.moveToFirst();
			        do {
			        	msg = cursor.getString(0);
			        } while (cursor.moveToNext());
			      }           
			
			      return msg;
	}

	public Cursor getProfileDetails(String string) {
		// TODO Auto-generated method stub
		Cursor proDetCur = null;
		try {
			open();
			proDetCur = database.rawQuery(string, null);

		} catch (Exception e) {
			proDetCur = null;
			System.out.println("Error::::" + e.getMessage());
		} finally {

		}
		return proDetCur;
	}

	public void updateReplyMessage(String profileName, String replyMsg) {
		// TODO Auto-generated method stub
		ContentValues args = new ContentValues();
        args.put(KEY_REPLY_MESSAGE, replyMsg);
        database.update(REPLY_MSG_TABLE, args,KEY_PROFILE + "= ?", new String[]{ profileName });
        args.clear();
	}


	public void storeSelectedContacts(String string, String groupName) {
		// TODO Auto-generated method stub
		ContentValues args = new ContentValues();
        args.put(Key_PHONE_NUMBER, string);
        database.update(GROUP_NAMES_TABLE, args,KEY_GROUP_NAME + "= ?", new String[]{ groupName });
	}

	public void storeGroupsReplyMessage(String groupName,
			String groupReplyMessage) {
		// TODO Auto-generated method stub
		ContentValues args = new ContentValues();
        args.put(GROUPS_TABLE_REPLY, groupReplyMessage);
        database.update(GROUPS_TABLE, args,KEY_GROUP_NAME + "= ?", new String[]{ groupName });
		
	}

	public String getGroupRelpyMessage(String incomingNumber) {
		// TODO Auto-generated method stub
		String num = null;
		String query = "Select GroupReplyMessage FROM GroupsTable WHERE PhoneNumber = '"+incomingNumber+"'";
		try {
			open();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Cursor cursor = database.rawQuery(query, null );
	try {
		if(cursor!=null && cursor.getCount()>0)
		{
		      cursor.moveToFirst();
		        do {
		        	num = cursor.getString(0);
		        } while (cursor.moveToNext());
		      }           
		
	}catch(Exception e){
		cursor = null;
		System.out.println("Error:::: while getting groups reply msg :::::" + e.getMessage());
	} finally {
		if (null != cursor)
			cursor.close();
	}
	return num;
	}

	public void updateToggleStatus(int flag, long id) {
		// TODO Auto-generated method stub
		ContentValues args = new ContentValues();
		args.put(GROUPS_TOGGLE_STATUS, flag);
		database.update(TABLE_PROFILE_DETAILS_TAG, args, KEY_ROWID + "='" +id+"'", null);
	}

	public void updateDateAndTime(String profileName, String startTime,
			String stopTime, int startHourOfDay, int startMinOfHour,
			int stopHourOfDay, int stopMinOfHour, String selectedGroups) {
		// TODO Auto-generated method stub
		ContentValues args = new ContentValues();
		args.put(KEY_PROFILE, profileName);
		args.put(KEY_START, startTime);
		args.put(KEY_STOP, stopTime);
		args.put(KEY_START_HOUR, startHourOfDay);
		args.put(KEY_START_MINUTE, startMinOfHour);
		args.put(KEY_STOP_HOUR, stopHourOfDay);
		args.put(KEY_STOP_MINUTE, stopMinOfHour);
		args.put(KEY_SELECTED_GROUPS, selectedGroups);
		
		database.update(TABLE_PROFILE_DETAILS_TAG, args, KEY_PROFILE + "='" +profileName+"'", null);
	}


	public void deleteGrousData() {
		// TODO Auto-generated method stub
		database.delete(GROUP_NAMES_TABLE,null,null);
	}

	public void deleteGroupContacts(String groupName) {
		// TODO Auto-generated method stub
		database.execSQL("DELETE  FROM GroupsTable WHERE GroupName = '"+ groupName + "'");
	}

	public void deleteProfileReplyMessages(String string) {
		// TODO Auto-generated method stub
		database.execSQL("DELETE  FROM ReplyMessagesTable WHERE ProfileName = '"+ string + "'");
	}

	public Cursor getProfileNames(String string) {
		// TODO Auto-generated method stub
		Cursor profileNamesCur = null;
		try {
			open();
			profileNamesCur = database.rawQuery(string, null);

		} catch (Exception e) {
			profileNamesCur = null;
			System.out.println("Error::::" + e.getMessage());
		} finally {

		}
		return profileNamesCur;
	}

	public void editProfileName(int profileId, String profileName) {
		// TODO Auto-generated method stub
		ContentValues args = new ContentValues();
        args.put(KEY_PROFILE, profileName);
        database.update(TABLE_PROFILE_DETAILS_TAG, args,KEY_ROWID + "= '"+profileId+"'", null);
	}

	public int groupDetailsCount(String groupName) {
		// TODO Auto-generated method stub
		String GET_ROW_COUNT = "SELECT count(PhoneNumber) FROM GroupDetails Where Groupname= '"+groupName+"';";
		int Count = (int) database.compileStatement(GET_ROW_COUNT).simpleQueryForLong();
		return Count;
	}

	public void DeleteSentScheduleMessageDetails(int iD) {
		// TODO Auto-generated method stub
		database.execSQL("DELETE  FROM ScheduleMessageDetails WHERE _Id = '"+iD+ "'");
	}


}
