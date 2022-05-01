package com.eran.hokleisrael;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.SuperToast.OnClickListener;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class Bookmarks extends Activity {

    private ListView lv;
    private ArrayList<Parash> bookmarksList;
    SharedPreferences HLPreferences;
    Gson gson;
    ArrayAdapter<Parash> adapter;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lv = (ListView) findViewById(R.id.ListViewBookmarks);

        gson = new Gson();
        HLPreferences = getSharedPreferences("HLPreferences", MODE_PRIVATE);
        String bookmarksListJson = HLPreferences.getString("bookmarksListJson", null);

        if (bookmarksListJson != null) {
            bookmarksList = gson.fromJson(bookmarksListJson, new TypeToken<ArrayList<Parash>>() {
            }.getType());
            adapter = new ArrayAdapter<Parash>(this, android.R.layout.simple_list_item_1, bookmarksList);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View currView, int position, long id) {
                    Parash bookmark = (Parash) lv.getItemAtPosition(position);
                    // Toast.makeText(Bookmarks.this,selected.getTitle(),Toast.LENGTH_LONG).show();
                    Intent i = getIntent();
                    i.putExtra("bookmark", bookmark);
                    setResult(RESULT_OK, i);
                    finish();
                }
            });

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
                    //Toast.makeText(Bookmarks.this,Integer.toString(index),Toast.LENGTH_LONG).show();
                    Parash bookmarkForDelete = bookmarksList.get(index);
                    bookmarksList.remove(index);
                    updateSharedPreferencesAndLV();

                    /*try {*/
                    //the bottom for undo

                    /* Create a Wrappers object to reattach our OnClickWrapper  */
                    // Wrappers wrappers = new Wrappers();
                    // wrappers.add(onClickWrapper);
                    /* Recreate and reshow any SuperActivityToasts that were showing before orientation change */
                    //SuperActivityToast.onRestoreState(savedInstanceState, Bookmarks.this, wrappers);

                    /* Show a SuperActivityToast with a button and OnClickWrapper */
                    SuperActivityToast superActivityToast = new SuperActivityToast(Bookmarks.this, SuperToast.Type.BUTTON);
                    superActivityToast.setDuration(SuperToast.Duration.LONG);
                    superActivityToast.setText("סימניה נמחקה.");
                    superActivityToast.setBackground(SuperToast.Background.GRAY);
                    superActivityToast.setButtonIcon(SuperToast.Icon.Dark.UNDO, "בטל");

                    /* This part is important, pass the Bundle we created earlier as a second parameter here */
                    final Bundle bundle = new Bundle();
                    bundle.putParcelable("bookmarkForDelete", bookmarkForDelete);
                    bundle.putInt("bookmarkForDeleteIndex", index);

                    superActivityToast.setOnClickWrapper(onClickWrapper, bundle);
                    SuperActivityToast.cancelAllSuperActivityToasts();
                    superActivityToast.show();
				/*} 
            	catch (Exception e) 
            	{
            		e.printStackTrace();
					Utils.appendLog(e.toString());
					Toast.makeText(Bookmarks.this,"write to log",Toast.LENGTH_LONG).show();
				}*/


                    return true;
                }
            });

        }
    }


    OnClickWrapper onClickWrapper = new OnClickWrapper("id_undo_wrapper", new OnClickListener() {

        public void onClick(View view, Parcelable token) {
            if (token != null) {
                //Toast.makeText(Bookmarks.this,bookmarkForDelete.getTitle(),Toast.LENGTH_LONG).show();

                Bundle bundle = (Bundle) token;
                Parash bookmarkForDelete = (Parash) bundle.getParcelable("bookmarkForDelete");
                int bookmarkForDeleteIndex = bundle.getInt("bookmarkForDeleteIndex");

                bookmarksList.add(bookmarkForDeleteIndex, bookmarkForDelete);
                updateSharedPreferencesAndLV();
            }
        }

    });

    private void updateSharedPreferencesAndLV() {
        String json = gson.toJson(bookmarksList);
        SharedPreferences.Editor editor = HLPreferences.edit();
        editor.putString("bookmarksListJson", json);
        editor.commit();
        // lv.invalidateViews();//Cause sometimes to crash
        adapter.notifyDataSetChanged();//for refresh
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
