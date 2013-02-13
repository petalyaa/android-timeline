package org.pet.timeline;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.pet.timeline.HistoryData.CallType;
import org.pet.timeline.HistoryData.DataType;

import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		
		setContentView(R.layout.activity_main);
		
		final ArrayList<HistoryData> historyData = new ArrayList<HistoryData>();
		getPhoneCallHistory(historyData); // Get phone history from android
		getReceivedSmsHistory(historyData); // Get receive sms history from android
		getSentSmsHistory(historyData); // Get sent sms history from android
		getInstalledMarketApp(historyData); // Get application installed from google play
		Collections.sort(historyData);
		final Context newContext = this;
		final LayoutInflater inflater = (LayoutInflater) newContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		HistoryListAdapter historyListAdapter = new HistoryListAdapter(getApplicationContext(), historyData);
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HistoryData data = historyData.get(position);
				final AlertDialog dialog = new AlertDialog.Builder(newContext).create();
				dialog.setTitle(data.getType().getValue());
//				dialog.setMessage("Testing message");
				
				View selectionView = inflater.inflate(R.layout.history_items_popup, null, false);
				ListView listView = (ListView) selectionView.findViewById(R.id.histortPopupListItem);
				PopupItemHandler rawItemHandler = null;
				String[] rawItems = null;
				switch(data.getType()) {
				case CALL : 
					rawItems = getResources().getStringArray(R.array.call_popup_items);
					rawItemHandler = new CallPopupItemsHandler(newContext);
					break;
				case PLAY :
					rawItems = getResources().getStringArray(R.array.application_popup_items);
					rawItemHandler = new ApplicationPopupItemHandler(newContext);
					break;
				case SMS :
					rawItems = getResources().getStringArray(R.array.messaging_popup_items);
					rawItemHandler = new SmsPopupItemHandler(newContext);
					break;
				}
				final String[] items = rawItems;
				final PopupItemHandler itemHandler = rawItemHandler;
				if(rawItems != null) {
					SimpleTextAdapter adapter = new SimpleTextAdapter(newContext, rawItems, data);
					listView.setAdapter(adapter);
				}
				
				dialog.setView(selectionView, 2, 2, 2, 2);
				
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Log.v(TAG, "You have selected option " + items[position]);
						dialog.dismiss();
						if(itemHandler != null) {
							itemHandler.performAction(position);
						}
					}
					
				});
				
				dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		listView.setAdapter(historyListAdapter);
	}
	
	private ArrayList<HistoryData> getInstalledMarketApp(ArrayList<HistoryData> list) {
		Log.d(TAG, "Getting list of installed market app.");
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
	    for(int i=0;i<packs.size();i++) {
	        PackageInfo p = packs.get(i);
	        if ((packs.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
	        	continue;
	        }
	        HistoryData data = new HistoryData();
	        String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
	        Timestamp dateInstalled = new Timestamp(p.firstInstallTime);
	        Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
	        String dateInstalledStr = sdf.format(dateInstalled);
	        
	        Map<String, String> replacement = new HashMap<String, String>();
	        replacement.put("appname", appName);
	        replacement.put("date", dateInstalledStr);
	        String text = StringUtil.getString(getApplicationContext(), R.string.play_installed_app, replacement);
	        data.setText(text);
	        data.setAppIcon(icon);
	        data.setName(appName);
	        data.setType(DataType.PLAY);
	        data.setTimestamp(dateInstalled);
	        list.add(data);
	    }
		return list;
	}
	
	private ArrayList<HistoryData> getSentSmsHistory(ArrayList<HistoryData> list) {
		Log.d(TAG, "Getting list of sent sms history.");
		String[] selectedColumns = new String[]{"_id", "thread_id", "address", "date", "body"};
		Cursor cursor = getContentResolver().query(Uri.parse("content://sms/sent"), selectedColumns, null, null, null);
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			String number = cursor.getString(2);
			String dateSentStr = cursor.getString(3);
			String toName = getContactName(number, getContentResolver());
			Timestamp dateSent = new Timestamp(Long.parseLong(dateSentStr));
			String dateTimeAction = sdf.format(dateSent);
			if(toName.equals(number)) {
				toName = getString(R.string.unknown_contact);
			}
			Map<String, String> replacement = new HashMap<String, String>();
			replacement.put("name", toName);
			replacement.put("number", number);
			replacement.put("date", dateTimeAction);
			String text = StringUtil.getString(getApplicationContext(), R.string.sms_sent_message, replacement);
			HistoryData data = new HistoryData();
			data.setType(DataType.SMS);
			data.setImageId(R.drawable.sms);
			data.setText(text);
			data.setName(toName);
			data.setTimestamp(dateSent);
			list.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}
	
	private ArrayList<HistoryData> getReceivedSmsHistory(ArrayList<HistoryData> list) {
		Log.d(TAG, "Getting list of recieved sms history.");
		String[] selectedColumns = new String[]{"_id", "thread_id", "address", "person", "date_sent", "date", "body"};
		Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), selectedColumns, null, null, null);
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			String address = cursor.getString(2);
			String dateStr = cursor.getString(5);
			String newFromPerson = getContactName(address, getContentResolver());
			String dateTimeAction = sdf.format(new Timestamp(Long.valueOf(dateStr)));
			if(newFromPerson.equals(address)) {
				newFromPerson = getString(R.string.unknown_contact);
			} 
			HistoryData data = new HistoryData();
			data.setType(DataType.SMS);
			data.setImageId(R.drawable.sms);
			Map<String, String> replacement = new HashMap<String, String>();
			replacement.put("name", newFromPerson);
			replacement.put("number", address);
			replacement.put("date", dateTimeAction);
			String text = StringUtil.getString(getApplicationContext(), R.string.sms_receive_message, replacement);
			data.setText(text);
			data.setName(newFromPerson);
			data.setTimestamp(new Timestamp(Long.parseLong(dateStr)));
			list.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}
	
	private String getContactName(String num, ContentResolver cr) {
	    Uri u = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(num));
	    String[] projection = new String[] { ContactsContract.Contacts.DISPLAY_NAME};
	    Cursor c = cr.query(u, projection, null, null, null);
	    try {
	        if (!c.moveToFirst())
	            return num;
	        int index = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
	        return c.getString(index);
	    } finally {
	        if (c != null)
	            c.close();
	    }
	}
	
	private ArrayList<HistoryData> getPhoneCallHistory(ArrayList<HistoryData> list) {
		Log.d(TAG, "Getting list of call history.");
		String[] strFields = { CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_TYPE, CallLog.Calls.DATE };
		String strOrder = CallLog.Calls.DATE + " DESC";
		Cursor mCallCursor = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, strFields, null, null, strOrder);
		mCallCursor.moveToFirst();
		while (mCallCursor.isAfterLast() == false) {
			HistoryData data = new HistoryData();
			String number  = mCallCursor.getString(0);
			int type = mCallCursor.getInt(1);
			String cacheName = mCallCursor.getString(2);
			String dateStr = mCallCursor.getString(4);
			StringBuilder sb = new StringBuilder();
			Map<String, String> replacement = new HashMap<String, String>();
			String dateTimeAction = sdf.format(new Timestamp(Long.valueOf(dateStr)));
			if(cacheName == null || cacheName.equals("")) {
				cacheName = getString(R.string.unknown_contact);
			}
			replacement.put("name", cacheName);
			replacement.put("number", number);
			replacement.put("date", dateTimeAction);
			data.setType(DataType.CALL);
			if(type == CallLog.Calls.INCOMING_TYPE) {
				sb.append(StringUtil.getString(getApplicationContext(), R.string.receive_call_message, replacement));
				data.setCallType(CallType.INCOMING);
				data.setImageId(R.drawable.sym_call_incoming);
			} else if (type == CallLog.Calls.MISSED_TYPE) {
				sb.append(StringUtil.getString(getApplicationContext(), R.string.missed_call_message, replacement));
				data.setCallType(CallType.MISSED);
				data.setImageId(R.drawable.sym_call_missed);
			} else {
				sb.append(StringUtil.getString(getApplicationContext(), R.string.make_call_message, replacement));
				data.setCallType(CallType.OUTGOING);
				data.setImageId(R.drawable.sym_call_outgoing);
			}
			data.setText(sb.toString());
			data.setName(cacheName);
			data.setTimestamp(new Timestamp(Long.parseLong(dateStr)));
			list.add(data);
		    mCallCursor.moveToNext();
		}
		mCallCursor.close();
		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
