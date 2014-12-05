package com.naar.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)


public class MainActivity extends Activity {
	

	static final int PICK_CONTACT=1;
	public static String theNumber;
	public static String myMsg;
	public static int g = 0;
	

    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;
    
    
    
    
    
    
    
    
    
    
    
    public void PopulatePeopleList()
    {

    mPeopleList.clear();

    Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

    while (people.moveToNext())
    {
    String contactName = people.getString(people.getColumnIndex(
    ContactsContract.Contacts.DISPLAY_NAME));

    String contactId = people.getString(people.getColumnIndex(
    ContactsContract.Contacts._ID));
    String hasPhone = people.getString(people.getColumnIndex(
    ContactsContract.Contacts.HAS_PHONE_NUMBER));

    if ((Integer.parseInt(hasPhone) > 0))
    {

    // You know have the number so now query it like this
    Cursor phones = getContentResolver().query(
    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    null,
    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
    null, null);
    while (phones.moveToNext()) {

    //store numbers and display a dialog letting the user select which.
    String phoneNumber = phones.getString(
    phones.getColumnIndex(
    ContactsContract.CommonDataKinds.Phone.NUMBER));

    String numberType = phones.getString(phones.getColumnIndex(
    ContactsContract.CommonDataKinds.Phone.TYPE));

    Map<String, String> NamePhoneType = new HashMap<String, String>();

    NamePhoneType.put("Name", contactName);
    NamePhoneType.put("Phone", phoneNumber);

    if(numberType.equals("0"))
    NamePhoneType.put("Type", "Work");
    else
    if(numberType.equals("1"))
    NamePhoneType.put("Type", "Home");
    else if(numberType.equals("2"))
    NamePhoneType.put("Type",  "Mobile");
    else
    NamePhoneType.put("Type", "Other");

    //Then add this map to the list.
    mPeopleList.add(NamePhoneType);
    }
    phones.close();
    }
    }
    people.close();

    startManagingCursor(people);
    }
    
    
    
    
    
    
    
    /*
    
    @Override
    public void setOnItemClickListener(AdapterView<?> av, View v, int index, long arg) {
        Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);
        Iterator<String> myVeryOwnIterator = map.keySet().iterator();
              while(myVeryOwnIterator.hasNext()) {
                String key=(String)myVeryOwnIterator.next();
                String value=(String)map.get(key);
                mTxtPhoneNo.setText(value);
            }               
        }
    });
    */
    
    
  /*
    public void onItemClick(AdapterView<?> av, View v, int index, long arg) {
        Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);
        Iterator<String> myVeryOwnIterator = map.keySet().iterator();
              while(myVeryOwnIterator.hasNext()) {
                String key=(String)myVeryOwnIterator.next();
                String value=(String)map.get(key);
                mTxtPhoneNo.setText(value);
            }               
        }
    });
    
    */

    

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		 	super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        
	        final EditText message = (EditText)findViewById(R.id.Message);
			final EditText dateEntry = (EditText)findViewById(R.id.Date);
			final EditText timeEntry = (EditText)findViewById(R.id.Time);
			final EditText autoC = (EditText)findViewById(R.id.mmWhoNo);
			
			Button send = (Button)findViewById(R.id.Send);
			
			final Calendar myCalendar = Calendar.getInstance();
	        
	        
			ActionBar actionBar = getActionBar();
			actionBar.hide();
	       
	        
	        
	        
	        /*************FIRST THREAD**************\*/
			//Makes a thread to get familiar with the shared preferences
	        Thread initEditor = new Thread(){
				
				public void run()
				{
			
			SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(	getString(R.string.Messages)	, "Writes to file\n" );
			editor.commit();
		
			
			
				}
			};
			initEditor.start();
			/*************FIRST THREAD**************\*/
			
			
			
			
	        

	        mPeopleList = new ArrayList<Map<String, String>>();
	        
	        
	        
	        
	        
	        /*************SECOND THREAD**************\*/
	        //Thread that runs method PopulatePeopleList to fill in auto-complete contacts.
	        Thread populatePeople = new Thread(){
				
				public void run()
				{
					PopulatePeopleList();
				}
	        };
	        populatePeople.start();
	        /*************SECOND THREAD**************\*/
	        
	  
	        
	        
	      
	        
	        
	        
	        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);

	        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.custcontview ,new String[] { "Name", "Phone" , "Type" }, new int[] { R.id.ccontName, R.id.ccontNo, R.id.ccontType });
	        

	        mTxtPhoneNo.setAdapter(mAdapter);
	        
	       
	        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					System.out.println("here");
					  Map<String, String> map = (Map<String, String>) (arg0).getItemAtPosition( arg2);
					  System.out.println("here2");
				        Iterator<String> myVeryOwnIterator = map.keySet().iterator();
				        System.out.println("here3");
				              while(myVeryOwnIterator.hasNext()) {
				                String key=(String)myVeryOwnIterator.next();
				                String value=(String)map.get(key);
				                mTxtPhoneNo.setText(value);
				              }
				              System.out.println("here4");
					
					
					
				}
			});
	       
	
	
	
		
		final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

		    @Override
		    public void onDateSet(DatePicker view, int year, int monthOfYear,
		            int dayOfMonth) {
		        // TODO Auto-generated method stub
		        myCalendar.set(Calendar.YEAR, year);
		        myCalendar.set(Calendar.MONTH, monthOfYear);
		        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        updateLabel(dateEntry,myCalendar);
		    }

		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	//	final TimePicker tt = new TimePicker(MainActivity.this);
		
		final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				System.out.println("ABC -- ");
				System.out.println(myCalendar.get(Calendar.HOUR_OF_DAY ));
		        myCalendar.set(Calendar.MINUTE, minute);
		        updateLabel2(timeEntry,myCalendar);
				
			}
		};{
			
			
		}


		
		
		/*
		number.setOnClickListener(new View.OnClickListener() {

	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	        	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
	            startActivityForResult(intent,PICK_CONTACT );//PICK_CONTACT is private static final int, so declare in activity class
	        }
	    });
	    */
		
		
		dateEntry.setOnClickListener(new View.OnClickListener() {

	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub
	            new DatePickerDialog(MainActivity.this, (OnDateSetListener) date, myCalendar
	                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
	                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
	        }
	    });
		
		
		timeEntry.setOnClickListener(new View.OnClickListener() {

	        @Override
	        public void onClick(View v) {
	            // TODO Auto-generated method stub private TimePicker timePicker1;
	        	//new TimePickerDialog(context, callBack, hourOfDay, minute, is24HourView))
	            new TimePickerDialog(MainActivity.this, (OnTimeSetListener) time, myCalendar
	                    .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),false).show();//.showContextMenu();
	        }
	    });
		
		
		
		send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				//Toast.makeText(MainActivity.this, "Message is awaiting send date", Toast.LENGTH_SHORT).show();
				
				
				myMsg = message.getText().toString();
				theNumber = autoC.getText().toString();
			
				
				 
				if(myMsg.length() < 1)
					Toast.makeText(MainActivity.this, "Please Enter a Valid Message", Toast.LENGTH_LONG).show();
				else if(theNumber.length() < 1)
					Toast.makeText(MainActivity.this, "Please Enter a Valid Number", Toast.LENGTH_LONG).show();
				else
				{
				Long abc = myCalendar.getTimeInMillis();
				System.out.print("Time in millis : ");
				System.out.println(myCalendar.getTimeInMillis());
				
				//Toast.makeText(MainActivity.this,"HERE", Toast.LENGTH_LONG).show();
				Toast.makeText(MainActivity.this, theNumber, Toast.LENGTH_LONG).show();
				
				message.setText("");
				
				AlarmReceiver.num = theNumber;
					scheduleMsg(abc);
				//message.setText("");
					
					
				}
					//sendMsg(theNumber, myMsg);
				
					
				
			}
		});
	} //END ON CREATE
	

	
	
	public void readFile(View view){
		
		System.out.println("\n\nHEEEEY\n\n");
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		String defaultValue = getResources().getString(R.string.Messages);
		String highScore = sharedPref.getString(getString(R.string.Messages),defaultValue);
	//	TextView a = (TextView)findViewById(R.id.textView1);
		//a.setVisibility(View.VISIBLE);
	//	a.setText(highScore);
		
		
	}
	
	
	

	public static String getNum(){
		return theNumber;
	}
	   

	      private void updateLabel(EditText dateEntry, Calendar myCalendar) {

	    String myFormat = "MM/dd/yy"; //In which you need put here
	    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

	    dateEntry.setText(sdf.format(myCalendar.getTime()));
	    
	    
	    }
	      
	      private void updateLabel2(EditText timeEntry, Calendar myCalendar) {

	    	String min = myCalendar.get(myCalendar.MINUTE) < 10 ? "0":"";
	    	min+= Integer.toString(   myCalendar.get(myCalendar.MINUTE)  );
	  	    timeEntry.setText(Integer.toString(    myCalendar.get(myCalendar.HOUR)    )+":"+ min+" " + (myCalendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM"));
	  	    
	  	    //System.out.println(myCalendar.HOUR);
	  	    
	  	    
	  	    }
	
	
	
	
	      @Override	
			 public void onActivityResult(int reqCode, int resultCode, Intent data)
			   {
	    	  Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);  startActivityForResult(intent, 1);

	    	  String phoneNo = null ;
	    	  Uri uri = data.getData();
	    	  Cursor cursor = getContentResolver().query(uri, null, null, null, null);
	    	  cursor.moveToFirst();

	    	  int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
	    	  phoneNo = cursor.getString(phoneIndex);
			        }
	      
	
	public void scheduleMsg(Long abc){
		//Toast.makeText(MainActivity.this, "Send m to alarm", Toast.LENGTH_LONG).show();
		Intent intentAlarm = new Intent(MainActivity.this,AlarmReceiver.class);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, abc, PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
		Toast.makeText(this, "ALARM SET", Toast.LENGTH_LONG).show();
		
		System.out.println("hey");
		
		
	}
	
	
	
	
	public void sendMsg(String theNumber, String myMsg){
		Toast.makeText(MainActivity.this, "SMS sent", Toast.LENGTH_LONG).show();
		String SENT = "Message Sent";
		String DELIVERED = "Message Delivered";
		
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
		
		registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				switch(getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(MainActivity.this, "SMS sent", Toast.LENGTH_LONG).show();
					break;
					
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No Service", Toast.LENGTH_LONG).show();
					break;	
				}
			}
		}, new IntentFilter(SENT)
		
				
				
				);
		
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(theNumber, null, myMsg, null, null);
		
		
	}
	
	
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first

	 
	}
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	
	
	
	

}
