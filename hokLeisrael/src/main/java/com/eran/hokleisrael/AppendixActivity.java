package com.eran.hokleisrael;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


public class AppendixActivity extends Activity {

    Parash parash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appendix);
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        parash = new Parash();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectIntro(View v) {
        int day = -1;//no day

        Intent intent = new Intent(getApplicationContext(), WebActivity.class);
        parash.setDay(day);
        parash.setHumashEn("appendix");
        parash.setParshEn("Intro");
        parash.setParshHe("הקדמות");
        intent.putExtra("parash", parash);
        startActivity(intent);
    }

    public void selectMamadot(View v) {
        Intent intent = new Intent(getApplicationContext(), DayActivity.class);
        parash.setHumashEn("appendix");
        parash.setParshEn("sederMamadot");
        parash.setParshHe("מעמדות");
        parash.setWeekly(true);
        intent.putExtra("parash", parash);
        startActivity(intent);

    }

    public void selectOrhotHaim(View v) {
        Intent intent = new Intent(getApplicationContext(), DayActivity.class);
        parash.setHumashEn("appendix");
        parash.setParshEn("orhotHaim");
        parash.setParshHe("אורחות חיים");
        parash.setWeekly(true);
        intent.putExtra("parash", parash);
        startActivity(intent);

    }

    public void selectHatzatilHakatan(View v) {
        int day = -1;//no day
        Intent intent = new Intent(getApplicationContext(), WebActivity.class);
        parash.setDay(day);
        parash.setHumashEn("appendix");
        parash.setParshEn("hatzatilHakatan");
        parash.setParshHe("הצעטיל הקטן");
        intent.putExtra("parash", parash);
        startActivity(intent);

    }
}
