package com.example.android.sunshine.app;

import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    private CustomIconShareProvider mShareActionProvider;
    private static final String HASHTAG = " #SunhineApp";
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    private TextView weatherTxt;
    private String mForecastStr;

    public DetailActivityFragment ()
    {
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);



    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.menu_item_share)
        {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = new CustomIconShareProvider (getActivity());
                MenuItemCompat.setActionProvider(item, mShareActionProvider);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else
        {
            Log.v(LOG_TAG, "Share action provider is null");
        }

    }

    private Intent createShareForecastIntent()
    {
        Intent share = new Intent (Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, mForecastStr + " " + HASHTAG);
        share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.setType("text/plain");
        return share;
    }


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RelativeLayout fragmentLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_detail, container, false);
        weatherTxt = (TextView)fragmentLayout.findViewById(R.id.weather_txt);




        return fragmentLayout;
    }

    @Override
    public Loader<Cursor> onCreateLoader (int id, Bundle args)
    {

        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Intent intent = getActivity().getIntent();
        Log.v(LOG_TAG, "Intent is " + intent + " + in onCreateLoader");
        Uri detailWeatherUri = null;
        if (intent== null)
            return null;

        detailWeatherUri = intent.getData();

        return new CursorLoader(getActivity(), detailWeatherUri, DETAIL_COLUMNS, null, null, sortOrder);
    }

    private static final String[] DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    @Override
    public void onLoadFinished (Loader<Cursor> loader, Cursor data)
    {
        Log.v(LOG_TAG, "onLoadFinished");
        if (data.moveToNext())
        {
            boolean isMetric = Utility.isMetric(getActivity());
            double high = data.getDouble(ForecastAdapter.COL_WEATHER_MAX_TEMP);
            double low = data.getDouble(ForecastAdapter.COL_WEATHER_MIN_TEMP);

            String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility
                    .formatTemperature(low, isMetric);

            mForecastStr = Utility.formatDate(data.getLong(ForecastAdapter.COL_WEATHER_DATE)) +
                    " - " + data.getString(ForecastAdapter.COL_WEATHER_DESC) +
                    " - " + highLowStr;
            weatherTxt.setText(mForecastStr);

            if (mShareActionProvider != null)
                mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset (Loader<Cursor> loader)
    {

    }

    @Override
    public void onStart ()
    {
        Log.v(LOG_TAG, "onStart");
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        super.onStart();
    }

    @Override
    public void onResume ()
    {
        Log.v(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause ()
    {
        Log.v(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop ()
    {
        Log.v(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy ()
    {
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
