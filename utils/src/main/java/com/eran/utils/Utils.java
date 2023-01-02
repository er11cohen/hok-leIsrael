package com.eran.utils;

import android.R.drawable;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;

import net.sourceforge.zmanim.ZmanimCalendar;
import net.sourceforge.zmanim.util.GeoLocation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.TimeZone;


public class Utils extends Activity {
    // FEFF because this is the Unicode char represented by the UTF-8 byte order mark (EF BB BF).
    private static final String UTF8_BOM = "\uFEFF";

    public static final String androidOS = Build.VERSION.RELEASE;
    public static final String phonemModel = Build.MODEL;
    public static Boolean utilFullScreen;
    public static final String Location_Permission = "android.permission.ACCESS_COARSE_LOCATION";
    public static final String Notification_Permission = "android.permission.POST_NOTIFICATIONS";

    public static String getVersionName(Context myContext) {
        String versionName = "-1";
        try {
            versionName = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return versionName;
    }

    public static String ReadTxtFile(String filePath, Context myContext) {
        String text = null;
        byte abyte0[];
        try {
            //InputStream inputstream = myContext.getAssets().open("files/"+fileName);
            InputStream inputstream = myContext.getAssets().open(filePath);
            abyte0 = new byte[inputstream.available()];
            inputstream.read(abyte0);
            inputstream.close();
            text = new String(abyte0, "utf-8");
            text = removeSpecialCharacters(text);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return text;
    }

    private static String removeSpecialCharacters(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public static boolean isConnected(Context myContext) {
        ConnectivityManager cm = (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static String TimePadding(String time) {
        if (time.length() == 1) {
            time = "0" + time;
        }
        return time;
    }

    private static Location getLocation(Context myContext) {
        LocationManager lm = (LocationManager) myContext.getSystemService(myContext.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }

    private static Location getLastKnownLocation(Context myContext) {
        LocationManager mLocationManager = (LocationManager) myContext.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @SuppressLint("NewApi")
    public static ZmanimCalendar getZmanimCalendar(Context myContext, String activityPreferences) {
        //Toast.makeText(myContext,"ZmanimCalendar",Toast.LENGTH_LONG).show();
        SharedPreferences appPreferences = myContext.getSharedPreferences(activityPreferences, myContext.MODE_PRIVATE);
        double latitude;
        double longitude;
        Location location = null;

        if (isMarshmallowPlusDevice()) {
            if (PackageManager.PERMISSION_GRANTED == myContext.checkSelfPermission(Location_Permission)) {
                location = getLastKnownLocation(myContext);//getLocation(myContext);
            }
        } else {
            location = getLastKnownLocation(myContext);
        }

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //Toast.makeText(myContext,Double.toString(latitude)+"  " +Double.toString(longitude) ,Toast.LENGTH_LONG).show();

            SharedPreferences.Editor editor = appPreferences.edit();
            editor.putString("latitude", String.valueOf(latitude));
            editor.putString("longitude", String.valueOf(longitude));
            editor.commit();
        } else {
            latitude = Double.parseDouble(appPreferences.getString("latitude", "31.7963186"));
            longitude = Double.parseDouble(appPreferences.getString("longitude", "35.175359"));
            //LAT LONG OF JERUSALEM
            // latitude = 31.7963186;
            // longitude = 35.175359;
        }

        TimeZone timeZone = TimeZone.getDefault();
        GeoLocation geoLocation = new GeoLocation("", latitude, longitude, 0, timeZone);
        ZmanimCalendar zc = new ZmanimCalendar(geoLocation);

        return zc;
    }

    public static void alertDialogShow(final WeakReference<Activity> aReference, Context context, String title, int iconNumber, String textDialogUri,
                                       String btnPositibeText, String btnNegativeText, final String btnNegativeTextIntent) {
        final Activity activity = aReference.get();
        if (activity == null) {
            return;
        }

        String message = ReadTxtFile(textDialogUri, context);
        message = message.replace("version", androidOS).replace("model", phonemModel)
                .replace("myApp", getVersionName(context));

        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setIcon(iconNumber);
        alertDialog.setMessage(fromHtml(message));

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, btnPositibeText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, btnNegativeText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                shareApp(aReference, btnNegativeTextIntent);
            }
        });


        alertDialog.show();

        // Make the textview clickable. Must be called after show()
        ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }


    public static void toggleFullScreen(final WeakReference<Activity> aReference, final Context context, int webView,
                                        final ActionBar actionBar, final boolean fullScreen) {
        final Activity activity = aReference.get();
        if (activity == null) {
            return;
        }

        utilFullScreen = fullScreen;

        final GestureDetector gs = new GestureDetector
                (context,
                        new GestureDetector.SimpleOnGestureListener() {
                            @SuppressLint("NewApi")
                            @Override
                            public boolean onDoubleTap(MotionEvent e) {

                                if (!utilFullScreen) {
                                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                    // actionBar.hide();
                                } else {
                                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                    // actionBar.show();
                                }

                                utilFullScreen = !utilFullScreen;
                                return true;
                            }
                        }
                );

        WebView wv = (WebView) activity.findViewById(webView);
        wv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gs.onTouchEvent(event);
                return false;
            }
        });
    }


    public static int readSize(SharedPreferences references) {
        int size = references.getInt("size", 20);
        return size;
    }

    private static void writeSize(int size, SharedPreferences references) {
        SharedPreferences.Editor editor = references.edit();
        editor.putInt("size", size);
        editor.commit();
    }

    public static void changeSize(boolean increase, SharedPreferences references, WebSettings wvSetting) {

        int size = readSize(references);
        if (increase) {
            size = size + 2;

        } else {
            size = size - 2;
        }

        wvSetting.setDefaultFontSize(size);
        writeSize(size, references);

    }

    public static void NightMode(Boolean change, SharedPreferences references, WebView wv, MenuItem nightModeItem) {
        boolean nightMode = references.getBoolean("nightMode", false);

        if (change) {
            nightMode = !nightMode;
            SharedPreferences.Editor editor = references.edit();
            editor.putBoolean("nightMode", nightMode);
            editor.commit();
        }

        if (nightMode) {
            //wv.loadUrl("javascript:document.body.style.color='white';document.body.style.background = 'black';");
            loadJS(wv, "document.body.style.color='white';document.body.style.background = 'black';");
            if (nightModeItem != null) {
                nightModeItem.setTitle("ביטול מצב לילה");
            }

        } else if (change) {
            //wv.loadUrl("javascript:document.body.style.color='black';document.body.style.background = 'white';");
            loadJS(wv, "document.body.style.color='black';document.body.style.background = 'white';"); //for kitkat and above
            nightModeItem.setTitle("מצב לילה");
        }

    }

    //need permission
    // <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//	  public static void appendLog(String text, String fileName)
//	  {
//	     File logFile = new File(Environment.getExternalStorageDirectory() + "/" +fileName);
//	     if (!logFile.exists())
//	     {
//	        try
//	        {
//	           logFile.createNewFile();
//	        }
//	        catch (IOException e)
//	        {
//	           // TODO Auto-generated catch block
//	           e.printStackTrace();
//	        }
//	     }
//	     try
//	     {
//	        //BufferedWriter for performance, true to set append to file flag
//	        BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
//	        buf.append("\n" + "new Log \n" + text);
//	        buf.newLine();
//	        buf.close();
//	     }
//	     catch (IOException e)
//	     {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//	     }
//	  }

    @SuppressLint("NewApi")
    public static void loadJS(WebView wv, String jsStr) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            wv.evaluateJavascript(jsStr, null);
        } else {
            wv.loadUrl("javascript:" + jsStr);
        }
    }

    public static void firstDoubleClickInfo(SharedPreferences references, final WeakReference<Activity> aReference) {

        final Activity activity = aReference.get();
        if (activity == null) {
            return;
        }

        Boolean firstDoubleClickInfo = references.getBoolean("firstDoubleClickInfo", true);
        if (firstDoubleClickInfo) {
            SuperActivityToast superActivityToast = new SuperActivityToast(activity);
            superActivityToast.setText("כדי להיכנס ולצאת ממסך מלא ניתן להקליק הקלקה כפולה");
            superActivityToast.setDuration(10000);
            superActivityToast.setBackground(SuperToast.Background.RED);
            superActivityToast.setTextColor(Color.BLACK);
            superActivityToast.setTouchToDismiss(true);
            superActivityToast.setAnimations(SuperToast.Animations.SCALE);
            superActivityToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.RIGHT);
            superActivityToast.show();

            //Toast toast = Toast.makeText(WebActivity.this,"לחץ הקלקה כפולה כדי להיכנס למצב מלא", Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.TOP, 0, 200);
            //toast.show();

            SharedPreferences.Editor editor = references.edit();
            editor.putBoolean("firstDoubleClickInfo", false);
            editor.commit();
        }
    }

    public static void showWebView(WebView wv, ProgressBar progressBar, Boolean show) {
        if (show) {
            //hide loading image
            progressBar.setVisibility(View.GONE);
            //show webview
            wv.setVisibility(View.VISIBLE);
        } else {
            //show loading image
            progressBar.setVisibility(View.VISIBLE);
            //hide webview
            wv.setVisibility(View.GONE);
        }
    }

    public static void setOpacity(WebView wv, double opacity) {
        String opacityStyle = "document.body.style.opacity='" + opacity + "'";
        loadJS(wv, opacityStyle);
    }


    @SuppressLint("NewApi")
    public static void firstTimeAskedPermission(Activity activity, String permission) {
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity.getBaseContext());

        defaultSharedPreferences.edit().putBoolean(permission, false).apply();
    }

    public static boolean isFirstTimeAskingPermission(Activity activity, String permission) {
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity.getBaseContext());
        return defaultSharedPreferences.getBoolean(permission, true);
    }


    public static boolean isMarshmallowPlusDevice() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean isPermissionRequestRequired(final Activity activity,
                                                       @NonNull final String permission,
                                                       final int requestCode,
                                                       String defaultMassege,
                                                       String settingsMassege,
                                                       boolean showDialog) {
        if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(permission)) {
            if (!showDialog) {
                return true;
            }
            final boolean isFirstTimeAskingPermission = isFirstTimeAskingPermission(activity, permission);

            String message = defaultMassege;
            String buttonText = "מסכים ברור";
            final boolean showRationale = activity.shouldShowRequestPermissionRationale(permission);
            if (!showRationale && !isFirstTimeAskingPermission) //user checked "never ask again"
            {
                message = settingsMassege;
                buttonText = "הגדרות";
            }

            ((TextView) new AlertDialog.Builder(activity)
                    .setTitle("צדיק תן לנו הרשאה")
                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .setIcon(drawable.ic_input_add)
                    .setMessage(message)
                    .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if (showRationale || isFirstTimeAskingPermission) {
                                activity.requestPermissions(new String[]{permission}, requestCode);
                            } else {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                activity.startActivity(intent);
                            }
                        }
                    })
                    .setCancelable(false)
                    .show()
                    .findViewById(android.R.id.message))
                    .setMovementMethod(LinkMovementMethod.getInstance());

            return true;
        }

        return false;
    }

    @SuppressLint("NewApi")
    public static boolean isPermissionLocationRequired(Activity activity, int requestCode, boolean showDialog) {
        if (isMarshmallowPlusDevice()) {
            String message = "צדיק, על מנת שנוכל לחשב את הלימוד היומי, עליך לאשר את 'הרשאת מיקום'  (חישוב הלימוד היומי תלוי במיקום שלך)";
            String settingsMessage = "צדיק, על מנת שנוכל לחשב את הלימוד היומי, עליך ללחוץ על הגדרות > הרשאות ולאשר את 'הרשאת מיקום' (חישוב הלימוד היומי תלוי במיקום שלך)";
            return isPermissionRequestRequired(activity, Location_Permission, requestCode, message, settingsMessage, showDialog);
        }
        return false;
    }

    @SuppressLint("NewApi")
    public static boolean isPermissionNotificationRequired(Activity activity, int requestCode, boolean showDialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String message = "צדיק ביקשת לקבל תזכורות יומיות, על מנת שהתזכורות יופיעו עליך לאשר את 'הרשאת התראות'";
            String settingsMessage = "צדיק ביקשת לקבל תזכורות יומיות, על מנת שהתזכורות יופיעו עליך ללחוץ על הגדרות > הרשאות ולאשר את 'הרשאת התראות'";
            return isPermissionRequestRequired(activity, Notification_Permission, requestCode, message, settingsMessage, showDialog);
        }
        return false;
    }


    public static void setRingerMode(Activity activity, int phoneStatus, int startRingerMode) {
        if (phoneStatus != -1 && startRingerMode != 0/*silent*/) {
            if (phoneStatus == 0) {
                requestForDoNotDisturbPermissionOrSetDoNotDisturb(activity);
            } else {
                AudioManager am = (AudioManager) activity.getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                am.setRingerMode(phoneStatus);
            }
        }
    }

    @SuppressLint("NewApi")
    private static void requestForDoNotDisturbPermissionOrSetDoNotDisturb(final Activity activity) {
        if (Build.VERSION.SDK_INT < 23) {
            moveToSilentMode(activity);
        } else if (Build.VERSION.SDK_INT >= 23) {
            NotificationManager notificationManager = (NotificationManager) activity.getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager.isNotificationPolicyAccessGranted()) {
                moveToSilentMode(activity);
            } else {

                ((TextView) new AlertDialog.Builder(activity)
                        .setTitle("צדיק תן לנו הרשאה")
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .setIcon(drawable.ic_input_add)
                        .setMessage("צדיק ביקשת לעבור למצב שקט בעת הלימוד, אנא תן לנו הרשאה על מנת שנוכל לשנות זאת")
                        .setPositiveButton("מסכים ברור", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                // Open Setting screen to ask for permission
                                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                activity.startActivity(intent);
                            }
                        })
                        .setNegativeButton("לא כעת", null)
                        .setCancelable(false)
                        .show()
                        .findViewById(android.R.id.message))
                        .setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    private static void moveToSilentMode(Activity activity) {
        AudioManager audioManager = (AudioManager) activity.getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    @SuppressLint("NewApi")
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        }

        return Html.fromHtml(html);
    }

    @SuppressLint("NewApi")
    public static File getFilePath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return context.getExternalFilesDir(null);
        }

        return Environment.getExternalStorageDirectory();
    }


    public static void shareApp(final WeakReference<Activity> aReference, String shareTextIntent) {
        final Activity activity = aReference.get();
        if (activity == null) {
            return;
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareTextIntent);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

}
