package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;


public class MainActivity extends AppCompatActivity implements ForecastFragment.OnFragmentInteractionListener
{
    private String LOG_TAG = MainActivity.class.getSimpleName();
    private String mLocation;

    private String FORECASTFRAGMENT_TAG = ForecastFragment.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = DetailActivityFragment.class.getSimpleName();

    private boolean mTwoPane;

    @Override
    public void onFragmentInteraction (Uri uri)
    {
        if (mTwoPane)
        {
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, uri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.weather_detail_container, fragment).commit();
        }
        else
        {
            Intent onePaneIntent = new Intent (MainActivity.this, DetailActivity.class);
            onePaneIntent.setData(uri);
            startActivity(onePaneIntent);
        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocation = Utility.getPreferredLocation(this);




        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.weather_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                                           .commit();
            }
        }
        else
        {
            mTwoPane = false;
            //getSupportActionBar().setElevation(0f);
        }

        ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        ff.setUseTodayLayout(!mTwoPane);
    }

    @Override
    protected void onResume ()
    {
        super.onResume();
        String location = Utility.getPreferredLocation( this );
        String units = Utility.getPreferredUnits(this);
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation))
        {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if ( null != ff )
            {
                ff.onLocationChanged();
            }
            DetailActivityFragment df = (DetailActivityFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df )
            {
                df.onLocationChanged(location);
            }
            mLocation = location;
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


            String locationString = Utility.getPreferredLocation(this);
            String intentString = "geo:0?q="+locationString;
            Log.v(LOG_TAG, "map intent message is " + intentString);
            Uri location = Uri.parse(intentString);

            Intent intent = new Intent (Intent.ACTION_VIEW, location);

            PackageManager packageManager = getPackageManager();
            List activities = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;
            if (isIntentSafe)
                startActivity(intent);

            else
                Log.e(LOG_TAG, "Could not start map intent with " + intentString);

            return true;
        }



        return super.onOptionsItemSelected(item);
    }


}


