package com.eran.hokleisrael; 

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DayActivity extends Activity {

	String parshHe;//,parshEn;
	//String humashEn;
	String dayHe;
	int day;
	boolean weekly = false;
	Parash parash;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day);
		
		 if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) 
		  {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		  }
		
		Intent intent = getIntent();
		parash = (Parash) intent.getParcelableExtra("parash");
		parshHe = parash.getParshHe();
		//parshEn = parash.getParshEn();
        //humashEn = parash.getHumashEn();
        weekly = parash.getWeekly();
        
//		parshHe = intent.getStringExtra("parshHe");
//		parshEn = intent.getStringExtra("parshEn");
//        humashEn = intent.getStringExtra("humashEn");
//        weekly = intent.getBooleanExtra("weekly", false);
        
        setTitle(parshHe);
        
        if (weekly) 
        {
        	Button ShabatBtn = (Button) findViewById(R.id.ShabatBtn);
        	ShabatBtn.setVisibility(View.VISIBLE);
        	
        	Button dayFridayNight = (Button) findViewById(R.id.day6);//ליל שישי
        	dayFridayNight.setVisibility(View.GONE);
        	
        	Button dayFriday = (Button) findViewById(R.id.day7);//יום שישי
        	dayFriday.setTag("6");
		}
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.day, menu);
		return true;
	}*/
	
	 public void SelectDay(View v)
	    { 
		 	//String title = parshHe + " " + ((Button) v).getText();
		 	day = Integer.parseInt((String)((Button)v).getTag());
		 	
	    	Intent intent = new Intent(getApplicationContext(),WebActivity.class);
	    	parash.setDay(day);
//	    	intent.putExtra("day", day);
//	    	intent.putExtra("parshEn", parshEn);
//	    	intent.putExtra("humashEn", humashEn);
//	    	intent.putExtra("parshHe", parshHe);
	    	if (weekly) {
	    		//intent.putExtra("weekly", true);
	    		parash.setWeekly(true);
			}
	    	intent.putExtra("parash", parash);
	    	startActivity(intent); 
	    	
	    }
	 
	 public boolean onOptionsItemSelected(MenuItem item){
		    switch (item.getItemId()) 
		    {
	        	case android.R.id.home:
	        		finish();
	        		break;
		    }
		    return super.onOptionsItemSelected(item);
		}

}
