package com.eran.hokleisrael;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


public class AppendixActivity extends Activity {

	Parash parash;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appendix);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) 
		  {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		  }
		 parash = new Parash();
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.appendix, menu);
		return true;
	}*/

	 public boolean onOptionsItemSelected(MenuItem item){
		    switch (item.getItemId()) 
		    {
	        	case android.R.id.home:
	        		finish();
	        		break;
		    }
		    return super.onOptionsItemSelected(item);
		}
	
	  public void selectIntro(View v)
	    { 
		 	int day = -1;//no day
		 	
	    	Intent intent = new Intent(getApplicationContext(),WebActivity.class);
//	    	intent.putExtra("day", day);
//	    	intent.putExtra("humashEn", "appendix");
//	    	intent.putExtra("parshEn", "Intro");
//	    	intent.putExtra("parshHe", "הקדמות");
	    	parash.setDay(day);
	    	parash.setHumashEn("appendix");
	    	parash.setParshEn("Intro");
	    	parash.setParshHe("הקדמות");
	    	intent.putExtra("parash", parash);
	    	startActivity(intent); 
	    	
	    }
	  
	  public void selectMamadot(View v)
	    { 
	    	Intent intent = new Intent(getApplicationContext(),DayActivity.class);
//	    	intent.putExtra("humashEn", "appendix");
//	    	intent.putExtra("parshEn", "sederMamadot");
//	    	intent.putExtra("parshHe", "מעמדות");
//	    	intent.putExtra("weekly", true);
	    	
	    	parash.setHumashEn("appendix");
	    	parash.setParshEn("sederMamadot");
	    	parash.setParshHe("מעמדות");
	    	parash.setWeekly(true);
	    	intent.putExtra("parash", parash);
	    	startActivity(intent); 
	    	
	    }
	  
	  public void selectOrhotHaim(View v)
	    { 
	    	Intent intent = new Intent(getApplicationContext(),DayActivity.class);
//	    	intent.putExtra("humashEn", "appendix");
//	    	intent.putExtra("parshEn", "orhotHaim");
//	    	intent.putExtra("parshHe", "אורחות חיים");
//	    	intent.putExtra("weekly", true);
	    	
	    	parash.setHumashEn("appendix");
	    	parash.setParshEn("orhotHaim");
	    	parash.setParshHe("אורחות חיים");
	    	parash.setWeekly(true);
	    	intent.putExtra("parash", parash);
	    	startActivity(intent); 
	    	
	    }
	  
	  public void selectHatzatilHakatan(View v)
	    { 
		    int day = -1;//no day
	    	Intent intent = new Intent(getApplicationContext(),WebActivity.class);
//	    	intent.putExtra("day", day);
//	    	intent.putExtra("humashEn", "appendix");
//	    	intent.putExtra("parshEn", "hatzatilHakatan");
//	    	intent.putExtra("parshHe", "הצעטיל הקטן");
	    	
	    	parash.setDay(day);
	    	parash.setHumashEn("appendix");
	    	parash.setParshEn("hatzatilHakatan");
	    	parash.setParshHe("הצעטיל הקטן");
	    	intent.putExtra("parash", parash);
	    	startActivity(intent); 
	    	
	    }
}
