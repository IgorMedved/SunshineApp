package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;


public class MainActivity extends AppCompatActivity implements ForecastFragment.OnFragmentInteractionListener
{
    private String LOG_TAG = MainActivity.class.getSimpleName();

    public void onFragmentInteraction (String id)
    {

    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.container, new ForecastFragment()).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent = new Intent (MainActivity.this, SettingsActivity.class);
            startActivity (intent);

            return true;
        }
        else if (id == R.id.action_refresh)
        {
            Log.v (LOG_TAG, "i am here");
            return false;
        }
        else if (id == R.id.action_show_location)
        {

            SharedPreferences locationPref = PreferenceManager.getDefaultSharedPreferences(this);
            String locationString = locationPref.getString(getString(R.string.pref_key_location), getString(R.string.pref_default_location));
            Uri location = Uri.parse("geo:0?q="+locationString );

            Intent intent = new Intent (Intent.ACTION_VIEW, location);

            PackageManager packageManager = getPackageManager();
            List activities = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;
            if (isIntentSafe)
                startActivity(intent);

            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}


