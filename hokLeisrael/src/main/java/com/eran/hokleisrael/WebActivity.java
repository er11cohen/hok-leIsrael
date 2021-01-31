package com.eran.hokleisrael;


import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.FindListener;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eran.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


//Toast.makeText(WebActivity.this,Integer.toString(scrollY),Toast.LENGTH_LONG).show();
public class WebActivity extends Activity {

    private static final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 1;

    String tishaBeavStr = "tishaBeav";

    WebView wv;
    ProgressBar progressBar;
    WebSettings wvSetting;
    String parshHe, parshEn, humashEn;
    int day;
    int scrollY = 0;
    Menu optionsMenu = null;
    MenuItem nightModeItem;
    String[] daysHebrew = {"יום ראשון", "יום שני", "יום שלישי", "יום רביעי", "יום חמישי", "ליל שישי", "יום שישי"};
    String[] daysHebrewRegular = {"יום ראשון", "יום שני", "יום שלישי", "יום רביעי", "יום חמישי", "יום שישי", "יום שבת"};
    String[] aliyotArr = {"ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שביעי"};
    String doubleHaftara = "Vayetse,Pkude,Shmot,Vayakhel,Aharemot,Kdoshim";
    SharedPreferences HLPreferences;
    SharedPreferences defaultSharedPreferences;
    boolean fullScreen = false;
    AudioManager am;
    String phoneStatus;
    int startRingerMode = 2;//RINGER_MODE_NORMAL
    ActionBar actionBar = null;
    int timeToLoad = 2000;//1100;
    boolean weekly = false;
    boolean isCurrentDay = false;
    Map<String, String> currentDayMap = null;
    String title = "";
    Parash parash;
    String appName = "/HokLeisrael";
    boolean pageReady = false;
    String queryAliya = null;
	/*@Override //not need for rotate
	public void onConfigurationChanged(Configuration newConfig){        
	    super.onConfigurationChanged(newConfig);
	}*/

    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        fullScreen = defaultSharedPreferences.getBoolean("CBFullScreen", false);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            timeToLoad = 3200;
            if (fullScreen) {
                this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        setContentView(R.layout.activity_web);

        am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        phoneStatus = defaultSharedPreferences.getString("phone_status", "-1");

        HLPreferences = getSharedPreferences("HLPreferences", MODE_PRIVATE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (fullScreen) {
                // actionBar.hide();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

        boolean keepScreenOn = defaultSharedPreferences.getBoolean("CBKeepScreenOn", false);
        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        Intent intent = getIntent();
        if (intent.getExtras().containsKey("bookmark")) {
            Parash bookmark = (Parash) intent.getParcelableExtra("bookmark");
            scrollY = bookmark.getScrollY();
            parshHe = bookmark.getParshHe();
            day = bookmark.getDay();
            parshEn = bookmark.getParshEn();
            humashEn = bookmark.getHumashEn();
            weekly = bookmark.getWeekly();
            isCurrentDay = bookmark.getIsCurrentDay();
        } else {
            parash = (Parash) intent.getParcelableExtra("parash");
            parshHe = parash.getParshHe();
            day = parash.getDay();
            parshEn = parash.getParshEn();
            humashEn = parash.getHumashEn();
            weekly = parash.getWeekly();
            isCurrentDay = parash.getIsCurrentDay();
            scrollY = parash.getScrollY();
        }


        wv = (WebView) findViewById(R.id.webViewHL);
        progressBar = (ProgressBar) findViewById(R.id.progressBarHL);
        wvSetting = wv.getSettings();
        //registerForContextMenu(wv);
        registerForContextMenu(progressBar);

        wvSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wvSetting.setJavaScriptEnabled(true);
        //wv.setWebChromeClient(new WebChromeClient());
        //wvSetting.setAllowFileAccess(false);
        LoadWebView();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            WeakReference<Activity> WeakReferenceActivity = new WeakReference<Activity>(this);
            Utils.toggleFullScreen(WeakReferenceActivity, getApplicationContext(), R.id.webViewHL, actionBar, fullScreen);
        }

        if (isCurrentDay) //for move between currentDay and appendix
        {
            currentDayMap = new HashMap<String, String>();
            currentDayMap.put("parshHe", parshHe);
            currentDayMap.put("day", Integer.toString(day));
            currentDayMap.put("parshEn", parshEn);
            currentDayMap.put("humashEn", humashEn);
        }

        WeakReference<Activity> WeakReferenceActivity = new WeakReference<Activity>(this);
        Utils.firstDoubleClickInfo(defaultSharedPreferences, WeakReferenceActivity);
    }


    @SuppressLint("NewApi")
    private void LoadWebView() {
        Utils.setOpacity(wv, 0.1);//for where the wv already exist
        Utils.showWebView(wv, progressBar, false);

        if (day >= 1 && day <= 7) {
            title = daysHebrew[day - 1];
            if (weekly) {
                title = daysHebrewRegular[day - 1];
            }

            title = parshHe + " " + title;
            if (title.length() > 14) {
                setTitle(Utils.fromHtml("<small>" + title + "</small>"));
            } else {
                setTitle(title);
            }

        } else {
            title = parshHe;
            setTitle(title);
        }
        String daySuffix = (day == -1) ? "" : "_" + day;

        String url = "file:///android_asset/" + humashEn + "/" + parshEn + "/" + parshEn + daySuffix + ".html";
        wv.loadUrl(url);

        //String htmlStr = Utils.ReadTxtFile(humashEn +"/"+ parshEn +"/"+parshEn + daySuffix  + ".html", getApplicationContext());
        //htmlStr = htmlStr.replace("</head>", "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/Jeruf.ttf\")}body {font-family: MyFont !important;}</style></head>");
        //wv.loadDataWithBaseURL("", htmlStr, "text/html", "UTF-8", "");


        int size = Utils.readSize(HLPreferences);
        wvSetting.setDefaultFontSize(size);

        wv.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Utils.setOpacity(wv, 0.1);
                Utils.showWebView(wv, progressBar, true);
                if (scrollY > 0 || queryAliya != null) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //Toast.makeText(WebActivity.this,"timeToLoad " +Integer.toString(timeToLoad)+ "  scrollY " +Integer.toString(scrollY),Toast.LENGTH_LONG).show();
                            ChangeWebViewBySettings();
                            if (queryAliya != null) {
                                findAliyot(queryAliya);
                            } else {
                                wv.scrollTo(0, scrollY);
                            }

                            pageReady = true;
                            queryAliya = null;
                            Utils.setOpacity(wv, 1);
                        }
                    }, timeToLoad);
                } else {
                    ChangeWebViewBySettings();
                    pageReady = true;
                    Utils.setOpacity(wv, 1);
                }
            }
        });
    }

    private void ChangeWebViewBySettings() {
        //changing the option menu here cause to crush on old phone
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
            MenuItem nextDay = optionsMenu.findItem(R.id.nextDay);
            MenuItem previousDay = optionsMenu.findItem(R.id.previousDay);
            if (day == -1 || humashEn.equals(tishaBeavStr)) {
                nextDay.setVisible(false);
                previousDay.setVisible(false);
            } else {
                nextDay.setVisible(true);
                previousDay.setVisible(true);
            }


            MenuItem studyActionBar = optionsMenu.findItem(R.id.selectStudyActionBar);
            //MenuItem study = optionsMenu.findItem(R.id.selectStudy);
            MenuItem aliya = optionsMenu.findItem(R.id.selectAliya);
            MenuItem dailyAliya = optionsMenu.findItem(R.id.dailyAliya);

            if (isHokLeisrael()) {
                aliya.setVisible(true);
                dailyAliya.setVisible(true);
            } else {
                aliya.setVisible(false);
                dailyAliya.setVisible(false);
            }
            if (weekly || parshEn.equals("hatzatilHakatan")) {
                studyActionBar.setVisible(false);
                //study.setVisible(false);
            } else {
                studyActionBar.setVisible(true);
                //study.setVisible(true);
            }
        }
		
		
		
		/*if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) // in order to not show in click on menu twice for old device
		  {
			studyActionBar.setVisible(false);
		  }*/

        //MenuItem nightModeItem = optionsMenu.findItem(R.id.nightMode);
        Utils.NightMode(false, HLPreferences, wv, nightModeItem);
//        if (RemoveNikud()) {
//            Utils.loadJS(wv, "document.body.innerHTML=document.body.innerHTML.replace(/[\u0500-\u05CF]/g, \"\")");
//        }
//        if (AlignText()) {
//            Utils.loadJS(wv, "document.body.innerHTML=document.body.innerHTML.replace('justify', 'right')");
//        }

        //String font = "var link = document.createElement('style'); link.type = 'text/css';  link.rel = 'stylesheet';  link.innerHTML= '@font-face {font-family:eran; src: url(\"file:///android_asset/fonts/Alef.ttf\");} body{font-family: eran;}'; document.getElementsByTagName('head')[0].appendChild(link)";
        //wv.loadUrl("javascript:" + font);

    }

    private void nextDay() {
        if (day < 7) {
            day++;
            scrollY = 0;
            LoadWebView();
        }
    }

    private void previousDay() {
        if (day > 1) {
            day--;
            scrollY = 0;
            LoadWebView();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web, menu);
        nightModeItem = menu.findItem(R.id.nightMode);
        optionsMenu = menu;

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
            //MenuItem addBookmarks = optionsMenu.findItem(R.id.addBookmarksActionBar);
            //addBookmarks.setVisible(true);

            if (isCurrentDay) {
                optionsMenu.findItem(R.id.dailyAppendix).setVisible(true);
            }
        }


        // for old phones
        if (day == -1 || humashEn.equals(tishaBeavStr)) {
            MenuItem nextDay = menu.findItem(R.id.nextDay);
            nextDay.setVisible(false);

            MenuItem previousDay = menu.findItem(R.id.previousDay);
            previousDay.setVisible(false);
        }
        if (weekly || parshEn.equals("hatzatilHakatan")) {
            //menu.findItem(R.id.selectStudy).setVisible(false);
            menu.findItem(R.id.selectAliya).setVisible(false);
            menu.findItem(R.id.dailyAliya).setVisible(false);
        }

        if (isHokLeisrael()) {
            menu.findItem(R.id.selectAliya).setVisible(true);
            menu.findItem(R.id.dailyAliya).setVisible(true);
        } else {
            menu.findItem(R.id.selectAliya).setVisible(false);
            menu.findItem(R.id.dailyAliya).setVisible(false);
        }
        // end for old phone

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            //case R.id.selectStudy:
            case R.id.selectStudyActionBar:
                openContextMenu(progressBar);
                break;
            case R.id.dailyAliya:
                openDailyAliya();
                break;
            case R.id.selectAliya:
                openAliya();
                break;
            case R.id.nextDay:
                nextDay();
                break;
            case R.id.previousDay:
                previousDay();
                break;
            case R.id.nightMode:
                //MenuItem nightModeItem = optionsMenu.findItem(R.id.nightMode);
                Utils.NightMode(true, HLPreferences, wv, nightModeItem);
                break;
            case R.id.zoomUp:
                Utils.changeSize(true, HLPreferences, wvSetting);
                break;
            case R.id.zoomDown:
                Utils.changeSize(false, HLPreferences, wvSetting);
                break;
            case R.id.dailyAppendix:
                openDailyAppendixMenu();
                break;
            case R.id.addBookmarks:
                //case R.id.addBookmarksActionBar:
                saveBookmarks();
                break;
            case R.id.voiceHokLeisrael:
                openVoiceHokLeisrael();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
	 
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LoadWebView();
	}*/


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();//Always call the superclass method first

        startRingerMode = am.getRingerMode();
        Utils.setRingerMode(this, Integer.parseInt(phoneStatus), startRingerMode);
    }


    @Override
    protected void onPause() {
        super.onPause();  //Always call the superclass method first

        if (!phoneStatus.equals("-1") && startRingerMode != 0/*silent*/) {
            am.setRingerMode(startRingerMode);
        }
        saveLastLocation();
    }

    private void saveLastLocation() {
        // getBaseContext().getExternalFilesDir(null)
        //  if (!Utils.isPermissionWriteRequired(WebActivity.this, 0, false)) {
        File path = Utils.getFilePath(getApplicationContext());
        File folder = new File(path + appName);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        if (success && pageReady) {
            SharedPreferences preferencesLocations = getSharedPreferences(
                    "Locations", MODE_PRIVATE);
            String preferencesLocationsJson = preferencesLocations
                    .getString("preferencesLocationsJson", null);

            if (preferencesLocationsJson == null)// for second install,
            // remove the old files
            {
                if (folder.isDirectory()) {
                    String[] children = folder.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(folder, children[i]).delete();
                    }
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timeSaved = sdf.format(new Date());

            View content = findViewById(R.id.layout);
            content.setDrawingCacheEnabled(true);
            Bitmap bitmap = content.getDrawingCache();
            File file = new File(path + appName + "/" + timeSaved + ".png");
            ArrayList<Parash> locationList = new ArrayList<Parash>();
            Gson gson = new Gson();
            try {
                file.createNewFile();
                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(CompressFormat.PNG, 100, ostream);
                ostream.close();

                Parash location = new Parash();
                location.setParshHe(parshHe);
                location.setDay(day);
                location.setParshEn(parshEn);
                location.setHumashEn(humashEn);
                int scrollY = wv.getScrollY();
                location.setScrollY(scrollY);
                location.setWeekly(weekly);
                location.setIsCurrentDay(isCurrentDay);
                location.setTimeSaved(timeSaved);

                if (preferencesLocationsJson != null) {
                    locationList = gson.fromJson(preferencesLocationsJson,
                            new TypeToken<ArrayList<Parash>>() {
                            }.getType());
                    if (locationList.size() >= 10) {
                        String idFirstLocation = locationList.get(0)
                                .getTimeSaved();
                        File imageToDelete = new File(path + appName + "/" + idFirstLocation + ".png");
                        if (imageToDelete.exists()) {
                            boolean deleted = imageToDelete.delete();
                        }

                        locationList.remove(0);
                    }
                }

                locationList.add(location);

                String json = gson.toJson(locationList);

                SharedPreferences.Editor editor = preferencesLocations
                        .edit();
                editor.putString("preferencesLocationsJson", json);
                editor.commit();

            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        //  }
    }


//    private boolean AlignText() {
//        boolean alignText = defaultSharedPreferences.getBoolean("CBAlignText", false);
//        //Toast.makeText(WebActivity.this,Boolean.toString(alignText) + " CBAlignText",Toast.LENGTH_LONG).show();
//        return alignText;
//    }
//
//    private boolean RemoveNikud() {
//        boolean removeNikud = defaultSharedPreferences.getBoolean("CBRemoveNikud", false);
//        //Toast.makeText(WebActivity.this,Boolean.toString(removeNikud) + " CBRemoveNikud",Toast.LENGTH_LONG).show();
//        return removeNikud;
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        scrollY = -1;// in order to prevent jump to this in "onPageFinished" function
        menu.setHeaderTitle("בחר לימוד");
        //menu.setHeaderIcon(drawable.nikud);
        if (!humashEn.contains("appendix")) {
            if (day >= 1 && day <= 5) {
                menu.add(0, 1, 0, "התחלה");
                menu.add(0, 2, 0, "נביאים");
                menu.add(0, 3, 0, "כתובים");
                menu.add(0, 4, 0, "משנה");
                menu.add(0, 5, 0, "גמרא");
                menu.add(0, 6, 0, "זוהר");
                menu.add(0, 20, 0, "תרגום הזוהר");
                menu.add(0, 7, 0, "הלכה");
                menu.add(0, 8, 0, "מוסר");
            } else if (day == 6) {
                menu.add(0, 1, 0, "התחלה");
            } else if (day == 7 && !doubleHaftara.contains(parshEn)) {
                menu.add(0, 1, 0, "התחלה");
                menu.add(0, 9, 0, "הפטרה");
                menu.add(0, 4, 0, "משנה");
                menu.add(0, 5, 0, "גמרא");
                menu.add(0, 6, 0, "זוהר");
                menu.add(0, 20, 0, "תרגום הזוהר");
                menu.add(0, 7, 0, "הלכה");
                menu.add(0, 8, 0, "מוסר");
            } else if (day == 7 && doubleHaftara.contains(parshEn)) {
                menu.add(0, 1, 0, "התחלה");
                menu.add(0, 9, 0, "הפטרה לפי הספרדים");
                menu.add(0, 10, 0, "הפטרה לפי האשכנזים");
                menu.add(0, 4, 0, "משנה");
                menu.add(0, 5, 0, "גמרא");
                menu.add(0, 6, 0, "זוהר");
                menu.add(0, 20, 0, "תרגום הזוהר");
                menu.add(0, 7, 0, "הלכה");
                menu.add(0, 8, 0, "מוסר");
            }
        } else if (parshEn.contains("Intro"))//(day == 8)//intro
        {
            menu.add(0, 11, 0, "הקדמת המהרח''ו");
            menu.add(0, 12, 0, "תפילה קודם קריאת התורה");
            menu.add(0, 13, 0, "תפילה קודם קריאת נביאים");
            menu.add(0, 14, 0, "תפילה קודם קריאת כתובים");
            menu.add(0, 15, 0, "תפילה קודם קריאת משנה");
            menu.add(0, 16, 0, "תפילה קודם קריאת הלכה");
            menu.add(0, 17, 0, "תפילה קודם קריאת קבלה");
            menu.add(0, 18, 0, "הקדמת החיד''א");
            menu.add(0, 19, 0, "מעשה רוקח");
        } else {

        }
        getMenuInflater().inflate(R.menu.contextmenuweb, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Toast.makeText(WebActivity.this,item.getTitle(),Toast.LENGTH_LONG).show();
        String hash = null;
        switch (item.getItemId()) {
            case 1:
                wv.scrollTo(0, 0);
                break;
            case 2:
                hash = "navie";
                break;
            case 3:
                hash = "ketuvim";
                break;
            case 4:
                hash = "misnha";
                break;
            case 5:
                hash = "gemara";
                break;
            case 6:
                hash = "zohar";
                break;
            case 7:
                hash = "hlacha";
                break;
            case 8:
                hash = "musar";
                break;
            case 9:
                hash = "haftara";
                break;
            case 10:
                hash = "haftaraAshkenaz";
                break;
            case 11:
                hash = "introMahrchu";
                break;
            case 12:
                hash = "introTora";
                break;
            case 13:
                hash = "introNavie";
                break;
            case 14:
                hash = "introKetuvim";
                break;
            case 15:
                hash = "introMishna";
                break;
            case 16:
                hash = "introHlacha";
                break;
            case 17:
                hash = "introKabala";
                break;
            case 18:
                hash = "introHidah";
                break;
            case 19:
                hash = "introMaseRokeh";
                break;
            case 20:
                hash = "zoharTranslate";
                break;
        }

        if (hash != null) {
            Utils.loadJS(wv, "window.location.hash = '';window.location.hash = '#" + hash + "';");
        }
        return super.onContextItemSelected(item);
    }

    private void openDailyAppendixMenu() {
        List<String> appendixList = new ArrayList<>();
        appendixList.add("חוק לישראל");
        appendixList.add("הקדמות");
        appendixList.add("מעמדות");
        appendixList.add("אורחות חיים");
        appendixList.add("הצעטיל הקטן");

        String[] appendix = appendixList.toArray(new String[appendixList.size()]);

        new AlertDialog.Builder(this)
                .setTitle("תוספות יומיות")
                .setItems(appendix,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                dailyAppendixMenuSelected(i);
                                //Toast.makeText(getApplicationContext(),Integer.toString(i),Toast.LENGTH_LONG).show();
                            }
                        })
                .show();
    }

    private void dailyAppendixMenuSelected(int index) {
        //daysHebrew = 	   {"1יום ראשון","יום שני2","יום שלישי3","יום רביעי4","5יום חמישי","ליל שישי6","יום שישי7"};
        //daysHebrewRegular = {"יום ראשון", "יום שני",  "יום שלישי","יום רביעי" ,"יום חמישי", "יום שישי", "יום שבת"};
        if (isCurrentDay && (index == 2 || index == 3)) {//move from daily(Hok leisrael) to weekly(OrhotHaim or Mamadot)
            if (day == 7) {
                day = 6;
            }
        } else if (weekly && index == 0)//move from weekly to daily
        {
            if (day == 6) {
                day = 7;
            }
        } else if (day == -1) {
            day = Integer.parseInt(currentDayMap.get("day"));
            if ((index == 2 || index == 3) && day == 7) {
                day = 6;
            }
        }

        scrollY = 0;
        isCurrentDay = false;
        weekly = false;

        switch (index) {
            case 0://hok leisrael
                parshHe = currentDayMap.get("parshHe");
                parshEn = currentDayMap.get("parshEn");
                humashEn = currentDayMap.get("humashEn");
                isCurrentDay = true;
                break;
            case 1://intro
                parshHe = "הקדמות";
                day = -1;
                parshEn = "Intro";
                humashEn = "appendix";
                break;
            case 2://mamadot
                parshHe = "מעמדות";
                parshEn = "sederMamadot";
                humashEn = "appendix";
                weekly = true;
                break;
            case 3://orhotHaim
                parshHe = "אורחות חיים";
                parshEn = "orhotHaim";
                humashEn = "appendix";
                weekly = true;
                break;
            case 4://hatzatilHakatan
                parshHe = "הצעטיל הקטן";
                day = -1;
                parshEn = "hatzatilHakatan";
                humashEn = "appendix";
                break;
            default:
                break;
        }

        LoadWebView();
    }

    private void openDailyAliya() {
        Calendar c = Calendar.getInstance();
        final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        aliyaMenuSelected(dayOfWeek, aliyotArr[dayOfWeek - 1]);
    }

    private void openAliya() {
        new AlertDialog.Builder(this)
                .setTitle("בחר עליה")
                .setItems(aliyotArr,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                aliyaMenuSelected(i + 1, aliyotArr[i]);
                              //  Toast.makeText(getApplicationContext(),Integer.toString(i),Toast.LENGTH_LONG).show();
                            }
                        })
                .show();
    }

    private void aliyaMenuSelected(int index, String aliya) {
        String parashotAliyaStr = Utils.ReadTxtFile("files/parashotAliya.csv", getApplicationContext());
        String[] items = parashotAliyaStr.split("\n");
        for (int i = 0; i < items.length; i++) {
            String[] parashotStr = items[i].split(",");
            String parashName = parashotStr[0].toString();
            if (parshEn.equals(parashName)) {
                day = Integer.parseInt(parashotStr[index]);
                scrollY = 0;
                if (index != 1)//for Sunday not need to jump
                {
                    queryAliya = "*" + aliya;
                }
                LoadWebView();
                break;
            }
        }
    }

    private void saveBookmarks() {
        String bookmarksListJson = HLPreferences.getString("bookmarksListJson", null);
        scrollY = wv.getScrollY();
        Parash parash = new Parash(parshHe, parshEn, humashEn, day, scrollY, weekly, isCurrentDay, title);

        ArrayList<Parash> bookmarksList = new ArrayList<Parash>();
        Gson gson = new Gson();

        if (bookmarksListJson != null) {
            bookmarksList = gson.fromJson(bookmarksListJson, new TypeToken<ArrayList<Parash>>() {
            }.getType());
            int bookmarksListSize = bookmarksList.size();
            for (int i = 0; i < bookmarksListSize; i++) {
                if (bookmarksList.get(i).getKey().equals(parash.getKey()))//if exist remove it
                {
                    bookmarksList.remove(i);
                    break;
                }
            }

            if (bookmarksListSize > 20) {
                bookmarksList.remove(0);
            }
        }

        bookmarksList.add(parash);
        String json = gson.toJson(bookmarksList);
        SharedPreferences.Editor editor = HLPreferences.edit();
        editor.putString("bookmarksListJson", json);
        editor.commit();

        String text = title + " נוסף לסימניות";
        Toast.makeText(WebActivity.this, text, Toast.LENGTH_LONG).show();

    }


    //for android 4.1 and above
    @SuppressLint("NewApi")
    private void findAliyot(String query) {
        wv.findAllAsync(query);
        wv.setFindListener(new FindListener() {

            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        wv.clearMatches();//clear the finds
                    }
                }, 300);
            }
        });
    }


    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        wv.clearFocus();//for close pop-up of copy, select etc.
        super.onBackPressed();
    }

    //check if the current limud is HokLeisrael
    private Boolean isHokLeisrael() {
        if (!humashEn.equals("appendix"))
            return true;

        return false;
    }

    private void openVoiceHokLeisrael() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kolhalashon.com/New/chok.aspx")));
    }
}



