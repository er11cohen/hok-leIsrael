package com.eran.hokleisrael;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class DayActivity extends Activity {

    String parshHe;
    int day;
    boolean weekly = false;
    Parash parash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        parash = (Parash) intent.getParcelableExtra("parash");
        parshHe = parash.getParshHe();
        weekly = parash.getWeekly();

        setTitle(parshHe);

        if (weekly) {
            Button ShabatBtn = (Button) findViewById(R.id.ShabatBtn);
            ShabatBtn.setVisibility(View.VISIBLE);

            Button dayFridayNight = (Button) findViewById(R.id.day6);//ליל שישי
            dayFridayNight.setVisibility(View.GONE);

            Button dayFriday = (Button) findViewById(R.id.day7);//יום שישי
            dayFriday.setTag("6");
        }
    }

    public void SelectDay(View v) {
        day = Integer.parseInt((String) ((Button) v).getTag());

        Intent intent = new Intent(getApplicationContext(), WebActivity.class);
        parash.setDay(day);
        if (weekly) {
            parash.setWeekly(true);
        }
        intent.putExtra("parash", parash);
        startActivity(intent);

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
