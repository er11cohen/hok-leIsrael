package com.eran.hokleisrael;




import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

public class ParashotActivity extends Activity {

	String BreshitEnglish = "Breshit,Noah,Lekhlkha,Vayera,HayeSara,Toldot,Vayetse,Vayishlah,Vayeshev,Mikets,Vayigash,Vayhi";
	String ShmotEnglish ="Shmot,Vaera,Bo,Bshalah,Yitro,Mishpatim,Truma,Ttsave,Kitisa,Vayakhel,Pkude";
	String VayikraEnglish ="Vayikra,Tsav,Shmini,Tazria,Mtsora,Aharemot,Kdoshim,Emor,Bhar,Bhukotay";
	String BamidbarEnglish ="Bamidbar,Naso,Bhaalotkha,Shlahlkha,Korah,Hukat,Balak,Pinhas,Matot,Mase";
	String DvarimEnglish ="Dvarim,Vaethanan,Ekev,Ree,Shoftim,Kitetse,Kitavo,Nitsavim,Vayelekh,Haazinu,Vzothabraha";
	
	String BreshitHebrew ="בראשית,נח,לך לך,וירא,חיי שרה,תולדות,ויצא,וישלח,וישב,מקץ,ויגש,ויחי";
	String ShmotHebrew ="שמות,וארא,בא,בשלח,יתרו,משפטים,תרומה,תצוה,כי תשא,ויקהל,פקודי";
	String VayikraHebrew ="ויקרא,צו,שמיני,תזריע,מצרע,אחרי מות,קדושים,אמור,בהר,בחקתי";
	String BamidbarHebrew ="במדבר,נשא,בהעלתך,שלח לך,קרח,חוקת,בלק,פינחס,מטות,מסעי";
	String DvarimHebrew = "דברים,ואתחנן,עקב,ראה,שופטים,כי תצא,כי תבוא,ניצבים,וילך,האזינו,וזאת הברכה";
	
	String humashEn,humashHe;
	Parash parash;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parashot);
		
		 if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) 
		  {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		  }
		
		String[] EngliseArr = null;
		String[] HebrewArr = null;
		
        Intent intent = getIntent();
        parash = (Parash)intent.getParcelableExtra("parash");
        //humashHe = intent.getStringExtra("humashHe");
        humashHe = parash.getHumashHe();
        setTitle(humashHe);
       // humashEn = intent.getStringExtra("humashEn");
        humashEn = parash.getHumashEn();
        
        if (humashEn.equals("Breshit"))
        {
        	EngliseArr = BreshitEnglish.split(",");
    		HebrewArr = BreshitHebrew.split(",");
		}
        else if(humashEn.equals("Shmot"))
        {
        	EngliseArr = ShmotEnglish.split(",");
    		HebrewArr = ShmotHebrew.split(",");
		}
        else if(humashEn.equals("Vayikra"))
        {
        	EngliseArr = VayikraEnglish.split(",");
    		HebrewArr = VayikraHebrew.split(",");
		}
        else if(humashEn.equals("Bamidbar"))
        {
        	EngliseArr = BamidbarEnglish.split(",");
    		HebrewArr = BamidbarHebrew.split(",");
		}
        else if(humashEn.equals("Dvarim"))
        {
        	EngliseArr = DvarimEnglish.split(",");
    		HebrewArr = DvarimHebrew.split(",");
		}
        
        LinearLayout ll = (LinearLayout)findViewById(R.id.LinearLayoutParashot);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        Button btnParash;
        
        for (int i = 0; i < EngliseArr.length; i++)
        {
        	btnParash= new Button(this);
        	btnParash.setText(HebrewArr[i]);
        	btnParash.setTag(EngliseArr[i]);
        	btnParash.setOnClickListener(SelectParash);
            ll.addView(btnParash, lp);
		}
	} 

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.parashot, menu);
		return true;
	}
	*/
	
	View.OnClickListener SelectParash = new View.OnClickListener() {
	    public void onClick(View v) {
	    	
	    	Intent intent = new Intent(getApplicationContext(),DayActivity.class);
	    	
//	    	intent.putExtra("parshHe", ((Button)v).getText());
//	    	intent.putExtra("parshEn", v.getTag().toString());
//	    	intent.putExtra("humashEn", humashEn);
	    	parash.setParshHe((String)((Button)v).getText());
	    	parash.setParshEn((String)((Button)v).getTag());
	    	intent.putExtra("parash", parash);
	    	startActivity(intent); 
	    }
	  };
	  
	  
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
