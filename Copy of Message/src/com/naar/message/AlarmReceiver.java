package com.naar.message;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver
{

	 static String num;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		//Toast.makeText(context, "Alarm recieved", Toast.LENGTH_LONG).show();
		Toast.makeText(context, "Alarm recieved", Toast.LENGTH_LONG).show();/*
		sendMsg(MainActivity.theNumber, MainActivity.myMsg, AlarmReceiver.this);
		Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();*/
		//System.out.println(MainActivity.theNumber);
		//Toast.makeText(context, MainActivity.theNumber, Toast.LENGTH_LONG).show();
		
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(MainActivity.theNumber, null,MainActivity.myMsg, null, null);
		
		
		
		
		
		
		

		
		
		
		
		
		
		
		
		
		
		
	}
	
	
	
	public void sendMsg(String theNumber, String myMsg,final Context MA){
		Toast.makeText(MA, "SMS sent", Toast.LENGTH_LONG).show();
		String SENT = "Message Sent";
		String DELIVERED = "Message Delivered";
		
		PendingIntent sentPI = PendingIntent.getBroadcast(MA, 0, new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(MA, 0, new Intent(DELIVERED), 0);
		
		registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				switch(getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(MA, "SMS sent", Toast.LENGTH_LONG).show();
					break;
					
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(MA, "No Service", Toast.LENGTH_LONG).show();
					break;	
				}
			}
		}, new IntentFilter(SENT)
		
				
				
				);
		
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(theNumber, null, myMsg, null, null);
		
		
	}



	private void registerReceiver(BroadcastReceiver broadcastReceiver,
			IntentFilter intentFilter) {
		// TODO Auto-generated method stub
		
	}
	
	
}
