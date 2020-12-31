package com.eran.hokleisrael;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.eran.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sourceforge.zmanim.ZmanimCalendar;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;
import net.sourceforge.zmanim.hebrewcalendar.JewishDate;
import android.R.drawable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// from manifest
	public static final String BROADCAST = "com.eran.hokleisrael.android.action.broadcast";
	final String shareTextIntent = "חוק לישראל - hok leisrael https://play.google.com/store/apps/details?id=com.eran.hokleisrael";
	// Toast.makeText(this,Integer.toString(scrollY),Toast.LENGTH_LONG).show();

	SharedPreferences sharedPreferences;
	SharedPreferences HLPreferences;
	Intent intentCurrentDay;
	String chatzot;
	String alotHashchar;
	boolean fridayNotification;
	WeakReference<Activity> WeakReferenceActivity;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fridayNotification = false;
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		HLPreferences = getSharedPreferences("HLPreferences", MODE_PRIVATE);
		WeakReferenceActivity = new WeakReference<Activity>(this);

		String version = HLPreferences.getString("version", "-1");
		if (!version.equals("1.8.5")) {
			String message = Utils.ReadTxtFile("files/newVersion.txt",
					getApplicationContext());
			((TextView) new AlertDialog.Builder(this)
					.setTitle("חדשות ללומדי החוק")
					.setIcon(android.R.drawable.ic_menu_info_details)
					.setIcon(drawable.ic_input_add)
					.setMessage(Utils.fromHtml(message))
					.setPositiveButton("אשריכם תזכו למצוות",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).show().findViewById(android.R.id.message))
					.setMovementMethod(LinkMovementMethod.getInstance());

			SharedPreferences.Editor editor = HLPreferences.edit();
			editor.putString("version", "1.8.5");
			editor.commit();
		}

		onNewIntent(getIntent());
		callToAlarmReceiver();

	}

	private void callToAlarmReceiver() {
		HokUtils.setAlarm(getApplicationContext());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		String fromNotification = intent.getStringExtra("fromNotification");

		if (fromNotification == null) {
			fromNotification = "not fromNotification";
		}
		 //Toast.makeText(this,fromNotification,Toast.LENGTH_LONG).show();

		if (fromNotification.equals("friday")) {
			fridayNotification = true;
			CurrentDay(null);
		}

	}

	public void SelectHumash(View v) {
		String humashEn = (String) ((Button) v).getTag();
		String humashHe = ((Button) v).getText().toString();
		Intent intent = new Intent(getApplicationContext(),
				ParashotActivity.class);
		// intent.putExtra("humashHe", ((Button)v).getText());
		// intent.putExtra("humashEn", humashEn);
		Parash parash = new Parash();
		parash.setHumashHe(humashHe);
		parash.setHumashEn(humashEn);
		intent.putExtra("parash", parash);
		startActivity(intent);
	}

	public void LastLocation(View v) {
		//if (!Utils.isPermissionWriteRequired(MainActivity.this, 1, true))
		// {
			sendLocationToWebView("last");
		// }
	}

	public void OpenSettings(View v) {
		Intent intent = new Intent(getApplicationContext(),
				SettingsActivity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:/* from settings */
			HokUtils.createChannelNotification(getApplicationContext());
			if (!Utils.isPermissionLocationRequired(MainActivity.this, 1, true)) {
				callToAlarmReceiver();
			}
			break;

		case 2:/* from bookmarks */
			if (data != null && data.getExtras().containsKey("bookmark")) {
				// Parash fileSelected = (Parash)
				// data.getSerializableExtra("fileSelected");
				Parash bookmark = (Parash) data.getParcelableExtra("bookmark");
				Intent intent = new Intent(getApplicationContext(),
						WebActivity.class);
				intent.putExtra("bookmark", bookmark);
				startActivity(intent);
			}
			break;
		case 3:// from history
			if (data != null && data.getExtras().containsKey("fileName")) {
				String fileName = data.getStringExtra("fileName");
				sendLocationToWebView(fileName);
				// Toast.makeText(this,data.getStringExtra("fileName"),Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}

	private void sendLocationToWebView(String fileName) {
		SharedPreferences preferencesLocations = getSharedPreferences(
				"Locations", MODE_PRIVATE);
		String preferencesLocationsJson = preferencesLocations.getString(
				"preferencesLocationsJson", null);
		if (preferencesLocationsJson == null)// no history
		{
			return;
		}
		ArrayList<Parash> locationList = new ArrayList<Parash>();
		Gson gson = new Gson();
		locationList = gson.fromJson(preferencesLocationsJson,
				new TypeToken<ArrayList<Parash>>() {
				}.getType());

		Parash parash = null;
		if (fileName.equals("last")) {
			parash = locationList.get(locationList.size() - 1);
		} else {
			for (int i = 0; i < locationList.size(); i++) {
				parash = locationList.get(i);
				if (parash.getTimeSaved().equals(fileName)) {
					break;
				}
			}
		}

		Intent intent = new Intent(getApplicationContext(), WebActivity.class);
		intent.putExtra("parash", parash);
		startActivity(intent);
	}

	public void OpenHelp(View v) {

		Utils.alertDialogShow(WeakReferenceActivity, getApplicationContext(),
				"עזרה", android.R.drawable.ic_menu_help, "files/help.txt",
				"הבנתי", "זכה את הרבים", shareTextIntent);

	}

	public void OpenAbout(View v) {
		Utils.alertDialogShow(WeakReferenceActivity, getApplicationContext(),
				"אודות", android.R.drawable.ic_menu_info_details,
				"files/about.txt", "אשריכם תזכו למצוות", "זכה את הרבים",
				shareTextIntent);
	}

	public void CurrentDay(View v) {

		if (!Utils.isPermissionLocationRequired(MainActivity.this, 1, true)) {
		
		Calendar c = Calendar.getInstance();
		final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		int dayToShabat = 7 - dayOfWeek;
		c.add(Calendar.DATE, dayToShabat);
		JewishCalendar jc = new JewishCalendar(c);
		boolean isLiveInIsrael = sharedPreferences.getBoolean("CBLiveInIsrael",
				true);
		jc.setInIsrael(isLiveInIsrael);
		int parshaIndex = jc.getParshaIndex();
		while (parshaIndex == -1) {
			c.add(Calendar.DATE, 7);
			jc = new JewishCalendar(c);
			parshaIndex = jc.getParshaIndex();
			if (parshaIndex == 0) // Breshit
			{
				parshaIndex = 60;// Vzothabraha
			}
		}

		String parashotMapStr = Utils.ReadTxtFile("files/parashot.csv",
				getApplicationContext());
		Parash parash = null;

		try {
			String[] items = parashotMapStr.split("\n");
			for (int i = 0; i < items.length; i++) {
				String[] parashotStr = items[i].split(",");
				int curParshaIndex = Integer
						.parseInt(parashotStr[0].toString());
				if (curParshaIndex == parshaIndex) {
					parash = new Parash(parshaIndex, parashotStr[1].trim(),// /parshHe
							parashotStr[2].trim(),// parshEn
							parashotStr[3].trim()// humashEn
					);
					break;
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		if (parash == null) {
			return;
		}

		/*********
		 * TishaBav **
		 **********/
		if (parshaIndex == 44)// Vaethanan
		{
			Calendar tishaBavCal = Calendar.getInstance();
			JewishDate jd = new JewishDate(tishaBavCal);
			int day = jd.getJewishDayOfMonth();
			// Toast.makeText(this,"day " +
			// Integer.toString(day),Toast.LENGTH_LONG).show();
			if ((day == 9)
					|| (day == 10 && tishaBavCal.get(Calendar.DAY_OF_WEEK) == 1)/*
																				 * דחוי
																				 * משבת
																				 * לראשון
																				 */
			) {
				ZmanimCalendar zc = Utils.getZmanimCalendar(
						getApplicationContext(), "HLPreferences");
				Calendar tzetStars = Calendar.getInstance();
				tzetStars.setTime(zc.getTzais());

				Calendar currentCal = Calendar.getInstance();
				currentCal.setTime(new Date());

				if (currentCal.before(tzetStars)) // לפני צאת הכוכבים
				{
					Intent intent = new Intent(getApplicationContext(),
							WebActivity.class);
					// intent.putExtra("humashEn", "tishaBeav" );
					// intent.putExtra("parshEn", "tishaBeav");
					// intent.putExtra("parshHe", "תשעה באב");
					// intent.putExtra("day", dayOfWeek);
					parash.setHumashEn("tishaBeav");
					parash.setParshEn("tishaBeav");
					parash.setParshHe("תשעה באב");
					parash.setDay(dayOfWeek);
					intent.putExtra("parash", parash);
					startActivity(intent);
					return;
				}
			}

		}

		/*********
		 * END TishaBav
		 **********/

		if (dayOfWeek == 7) {
			// Toast.makeText(this,"צדיק, בשבת אין לימוד יומי בחוק לישראל",Toast.LENGTH_LONG).show();
			shabatStudy();
		} else {

			if (parash.getParshEn().contains("&")) {

				final Parash parashFinal = parash;
				((TextView) new AlertDialog.Builder(this)
						.setTitle("פרשיות מחוברות")
						.setIcon(android.R.drawable.ic_menu_info_details)
						.setMessage("צדיק בחר פרשה")
						.setPositiveButton(
								parashFinal.getParshHe().split("&")[0],
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										parashFinal.setParshHe(parashFinal
												.getParshHe().split("&")[0]);
										parashFinal.setParshEn(parashFinal
												.getParshEn().split("&")[0]);
										continueToOpenLimud(parashFinal,
												dayOfWeek);
									}
								})
						.setNegativeButton(
								parashFinal.getParshHe().split("&")[1],
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										parashFinal.setParshHe(parashFinal
												.getParshHe().split("&")[1]);
										parashFinal.setParshEn(parashFinal
												.getParshEn().split("&")[1]);
										continueToOpenLimud(parashFinal,
												dayOfWeek);
									}
								}).show().findViewById(android.R.id.message))
						.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				continueToOpenLimud(parash, dayOfWeek);
			}

		}
	  }

	}

	private void continueToOpenLimud(final Parash parash, int dayOfWeek) {
		if (dayOfWeek == 5 || dayOfWeek == 6) {
			dayOfWeek = fridayLimud(dayOfWeek);
		}

		intentCurrentDay = new Intent(getApplicationContext(),
				WebActivity.class);
		// intentCurrentDay.putExtra("parshEn", parash.getParshEn());
		// intentCurrentDay.putExtra("humashEn", parash.getHumashEn());
		// intentCurrentDay.putExtra("parshHe", parash.getParshHe());
		// intentCurrentDay.putExtra("isCurrentDay", true);//for daily Limud
		parash.setIsCurrentDay(true);
		if (dayOfWeek == -1) // flag to open dialog
		{
			// intentCurrentDay.putExtra("day", 6);
			parash.setDay(6);

			String message = Utils.ReadTxtFile("files/fridayPopup.txt",
					getApplicationContext());
			// chatzot = new StringBuffer(chatzot).reverse().toString();
			message = String.format(message, chatzot, alotHashchar);

			((TextView) new AlertDialog.Builder(this)
					.setTitle("צדיק לידיעתך")
					.setIcon(android.R.drawable.ic_menu_info_details)
					.setMessage(Html.fromHtml(message))
					.setPositiveButton("הבנתי",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									intentCurrentDay.putExtra("parash", parash);
									startActivity(intentCurrentDay);
								}
							}).show().findViewById(android.R.id.message))
					.setMovementMethod(LinkMovementMethod.getInstance());
		} else {
			// intentCurrentDay.putExtra("day", dayOfWeek);
			parash.setDay(dayOfWeek);
			intentCurrentDay.putExtra("parash", parash);
			startActivity(intentCurrentDay);
		}
	}

	private int fridayLimud(int day) {
		ZmanimCalendar zc = Utils.getZmanimCalendar(getApplicationContext(),
				"HLPreferences");
		Calendar AlotHashchar = Calendar.getInstance();
		AlotHashchar.setTime(zc.getAlosHashachar());

		Calendar currentCal = Calendar.getInstance();
		currentCal.setTime(new Date());

		Calendar ChatzosCal = Calendar.getInstance();
		ChatzosCal.setTime(zc.getChatzos()); // this is Chatzos of day

		if (day == 5 && !fridayNotification) {
			ChatzosCal.add(Calendar.HOUR_OF_DAY, 12);

			// if (currentCal.getTime().after(ChatzosCal.getTime()))
			if (currentCal.after(ChatzosCal)) {
				return 6;// Friday night
			} else {
				return 5;// Thursday
			}
		} else // day is 6 or from "fridayNotification"
		{
			if (fridayNotification && day == 5) {
				ChatzosCal.add(Calendar.HOUR_OF_DAY, 12);
				AlotHashchar.add(Calendar.HOUR_OF_DAY, 24);
			} else {
				ChatzosCal.add(Calendar.HOUR_OF_DAY, -12);
			}

			// if (currentCal.getTime().before(ChatzosCal.getTime()))
			if (currentCal.before(ChatzosCal)) {
				chatzot = Utils.TimePadding(Integer.toString(ChatzosCal
						.get(ChatzosCal.HOUR_OF_DAY)))
						+ ":"
						+ Utils.TimePadding(Integer.toString(ChatzosCal
								.get(ChatzosCal.MINUTE)));

				alotHashchar = Utils.TimePadding(Integer.toString(AlotHashchar
						.get(AlotHashchar.HOUR_OF_DAY)))
						+ ":"
						+ Utils.TimePadding(Integer.toString(AlotHashchar
								.get(AlotHashchar.MINUTE)));

				return -1;// flag to open dialog
			}
			// else if (currentCal.getTime().before(zc.getAlosHashachar()))
			else if (currentCal.before(AlotHashchar)) {
				return 6;// Friday night
			} else {
				return 7;// Friday day
			}
		}
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getMenuInflater().inflate(R.menu.main, menu);
			MenuItem item = menu.findItem(R.id.menu_item_share);
			ShareActionProvider myShareActionProvider = (ShareActionProvider) item
					.getActionProvider();
			Intent myIntent = new Intent();
			myIntent.setAction(Intent.ACTION_SEND);
			myIntent.putExtra(
					Intent.EXTRA_TEXT,
					"חוק לישראל - Hok Leisrael https://play.google.com/store/apps/details?id=com.eran.hokleisrael");
			myIntent.setType("text/plain");
			myShareActionProvider.setShareIntent(myIntent);
		}
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_score:
			scoreInGooglePlay();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void scoreInGooglePlay() {
		Intent browserIntent = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://play.google.com/store/apps/details?id=com.eran.hokleisrael"));
		startActivity(browserIntent);

		String text = "צדיק אהבת את האפלקציה? דרג אותנו  5 כוכבים וטול חלק בזיכוי הרבים.";
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	public void SelectAppendix(View v) {
		Intent intent = new Intent(getApplicationContext(),
				AppendixActivity.class);
		startActivity(intent);
	}

	private void shabatStudy() {

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		alertDialog.setTitle("לימוד יומי בשבת");
		alertDialog.setIcon(android.R.drawable.ic_menu_info_details);

		alertDialog
				.setMessage("צדיק בשבת אין לימוד יומי בחוק לישראל, אך תוכל ללמוד סדר מעמדות או אורחות חיים ליום השבת.");

		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "אורחות חיים",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						Parash parash = new Parash();
						Intent intent = new Intent(getApplicationContext(),
								WebActivity.class);
						parash.setHumashEn("appendix");
						parash.setParshEn("orhotHaim");
						parash.setParshHe("אורחות חיים");
						parash.setWeekly(true);
						parash.setDay(7);
						intent.putExtra("parash", parash);
						startActivity(intent);

					}
				});

		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "מעמדות",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						Parash parash = new Parash();
						Intent intent = new Intent(getApplicationContext(),
								WebActivity.class);
						parash.setHumashEn("appendix");
						parash.setParshEn("sederMamadot");
						parash.setParshHe("מעמדות");
						parash.setWeekly(true);
						parash.setDay(7);
						intent.putExtra("parash", parash);
						startActivity(intent);
					}
				});

		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "בהזדמנות אחרת",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						// auto close

					}
				});

		alertDialog.show();
	}

	public void openBookmarks(View v) {
		Intent intent = new Intent(getApplicationContext(), Bookmarks.class);
		startActivityForResult(intent, 2);
	}

	public void openHistory(View v) {
//		if (!Utils.isPermissionWriteRequired(MainActivity.this, 2, true))
//		{
			Intent intent = new Intent(getApplicationContext(), Gallery.class);
			startActivityForResult(intent, 3);
//		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
		default:
			for (int i = 0; i < permissions.length; i++) {
				String permission = permissions[i];
				int grantResult = grantResults[i];
				switch (permission) {
				case "android.permission.WRITE_EXTERNAL_STORAGE":
					Utils.firstTimeAskedPermission(MainActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE");
					if (PackageManager.PERMISSION_GRANTED == grantResult)
					{
						//Toast.makeText(this,"PERMISSION GRANTED",Toast.LENGTH_LONG).show();
					}
					break;
				case "android.permission.ACCESS_COARSE_LOCATION":
					Utils.firstTimeAskedPermission(MainActivity.this, "android.permission.ACCESS_COARSE_LOCATION");
					break;
				}
			}
			break;
		}
	}

}
