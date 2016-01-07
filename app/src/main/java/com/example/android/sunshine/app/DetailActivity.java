package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity
{
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();


    @Override
    protected void onCreate (Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        //String weatherForecast = getIntent().getStringExtra("dayWeatherForecast");
        setContentView(R.layout.activity_detail);
        Log.v(LOG_TAG, "saved instance is " + savedInstanceState);

        if (savedInstanceState == null)
        {
            DetailActivityFragment fragment = new DetailActivityFragment();
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, getIntent().getData());
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.weather_detail_container, fragment).commit();
        }
    }



    @Override
    protected void onResume ()
    {
        super.onResume();
        Log.v(LOG_TAG, "onResume");
    }

    @Override
    protected void onStart ()
    {
        Log.v(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart ()
    {
        Log.v(LOG_TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onPause ()
    {
        Log.v(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop ()
    {
        Log.v(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy ()
    {
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent (DetailActivity.this, SettingsActivity.class);
            startActivity (intent);
            return true;
        }
        if (id == R.id.menu_item_share)
        {
            return false; // delagate processing to fragment
        }

        return super.onOptionsItemSelected(item);
    }


}
