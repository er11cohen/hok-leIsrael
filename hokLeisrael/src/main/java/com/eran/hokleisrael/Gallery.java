package com.eran.hokleisrael;

import java.io.File;
import java.util.Arrays;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;

import com.eran.utils.Utils;


public class Gallery extends Activity {

LinearLayout myGallery; 
	
    @SuppressLint("NewApi") 
	@Override 
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        String appName = "/HokLeisrael";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) 
		  { 
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		  }
        
        myGallery = (LinearLayout)findViewById(R.id.mygallery);
		File path = Utils.getFilePath(getApplicationContext());
        String targetPath = path  + appName + "/";
    
        File targetDirector = new File(targetPath);
        if (!targetDirector.exists()) {
        	finish();
		    return;
		 }	 
        
        SharedPreferences  preferencesLocations = getSharedPreferences("Locations", MODE_PRIVATE);
        String preferencesLocationsJson = preferencesLocations.getString("preferencesLocationsJson",null);
        if (preferencesLocationsJson == null)//for second install, remove the old files
        {
        	 File folder = new File(path + appName);
        	 if (folder.isDirectory()) 
        	 {
        		 String[] children = folder.list();
        	        for (int i = 0; i < children.length; i++) {
        	            new File(folder, children[i]).delete();
        	        }
        	 }
        	finish();
		    return;
		}
        
        File[] files = targetDirector.listFiles();
        Arrays.sort(files);
        for (int i = files.length-1; i >= 0; i--) {
        	File file = files[i];
        	String fileName = file.getName().replace(".png", "");
        	myGallery.addView(insertPhoto(file.getAbsolutePath(),fileName));
		}
    }
    
   
    
	@SuppressLint("NewApi")
	View insertPhoto(String path,final String fileName)
    {
		int width,height;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) 
		{
			Display display = getWindowManager().getDefaultDisplay();
	    	Point size = new Point();
	    	display.getSize(size);
	    	width = size.x;
	    	height= size.y;
		}
		else 
		{
			Display display = getWindowManager().getDefaultDisplay(); 
			width = display.getWidth();  // deprecated
			height = display.getHeight();  // deprecated
		}
		
		width -=  70;
		height -=  70;

	  	Bitmap bm = decodeSampledBitmapFromUri(path, width - 30 , height -30 );
		
		ImageView imageView = new ImageView(getApplicationContext());
		imageView.setLayoutParams(new LayoutParams(width - 30, height -30));
		//imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bm);
		imageView.setOnClickListener(new OnClickListener()
			{
			   @Override
			   public void onClick(View v) {
			    //Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_LONG).show();
			    Intent i = getIntent();
			    i.putExtra("fileName", fileName);
			    setResult(RESULT_OK, i);
			    finish();
			   }});
		
		
		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(width, height));
		layout.setGravity(Gravity.CENTER);
		
		layout.addView(imageView);
		return layout;
    }
    
    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
    	Bitmap bm = null;
    	
    	// First decode with inJustDecodeBounds=true to check dimensions
    	final BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeFile(path, options);
    	
    	// Calculate inSampleSize
    	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    	
    	// Decode bitmap with inSampleSize set
    	options.inJustDecodeBounds = false;
    	bm = BitmapFactory.decodeFile(path, options); 
    	
    	return bm; 	
    }
    
    public int calculateInSampleSize(
    		
    	BitmapFactory.Options options, int reqWidth, int reqHeight) {
    	// Raw height and width of image
    	final int height = options.outHeight;
    	final int width = options.outWidth;
    	int inSampleSize = 1;
        
    	if (height > reqHeight || width > reqWidth) {
    		if (width > height) {
    			inSampleSize = Math.round((float)height / (float)reqHeight);  	
    		} else {
    			inSampleSize = Math.round((float)width / (float)reqWidth);  	
    		}  	
    	}
    	
    	return inSampleSize;  	
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
