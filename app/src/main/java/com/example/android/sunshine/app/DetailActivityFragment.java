package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;


/**
 * User interface for showing detail weather view. It uses a cursor loader to get items from the database
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CustomIconShareProvider mShareActionProvider;
    private static final String HASHTAG = " #SunhineApp";
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    public static final String DETAIL_URI = "mUri";

    private TextView mDayTxt;
    private TextView mDateTxt;
    private TextView mHighTxt;
    private TextView mLowTxt;
    private ImageView mIconImg;
    private TextView mDescriptionTxt;
    private TextView mWindTxt;
    private TextView mHumidityTxt;
    private TextView mPressureTxt;
    private MyCompassView mCompassView;


    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_DESCRIPTION_ID = 5;
    static final int COL_WEATHER_HUMIDITY = 6;
    static final int COL_WEATHER_WIND = 7;
    static final int COL_WEATHER_DEGREES = 8;
    static final int COL_WEATHER_PRESSURE = 9;


    private String mForecastStr;
    private Uri mUri;

    public DetailActivityFragment() {
    }


    @Override
    // saves uri to location of weather information in local database for future reuse
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(DETAIL_URI, mUri);
        Log.v(LOG_TAG, "mUri is " + mUri);

        super.onSaveInstanceState(outState);

    }

    @Override
    // DetailActivityFragment on create
    // initilizes cursorLoader to get info from local weather database
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // set flag for adding additional items to the options menu

        // initialize cursor loader on starting detail activity from main activity
        if (savedInstanceState == null)
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            // restart cursor loader if returning from options screen or after screen rotation
        else
            getLoaderManager().restartLoader(DETAIL_LOADER, savedInstanceState, this);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = new CustomIconShareProvider(getActivity());
        MenuItemCompat.setActionProvider(item, mShareActionProvider);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.v(LOG_TAG, "Share action provider is null");
        }

    }

    // helper function for sharing weather information with other apps (i.e facebook, twitter, mail etc.)
    // creates Intent for starting an app chosen by the user and passing it weather details
    // called when user selects "share" button from menu
    private Intent createShareForecastIntent() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, mForecastStr + " " + HASHTAG);
        share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.setType("text/plain");
        return share;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        // set/update Uri for database requests
        if (arguments != null && savedInstanceState == null)
            mUri = arguments.getParcelable(DETAIL_URI);
        else if (savedInstanceState != null)
            mUri = savedInstanceState.getParcelable(DETAIL_URI);


        // initialize UI elements from resource file
        ScrollView fragmentLayout = (ScrollView) inflater.inflate(R.layout.fragment_detail, container, false);
        mDayTxt = (TextView) fragmentLayout.findViewById(R.id.detail_day_txt);
        mDateTxt = (TextView) fragmentLayout.findViewById(R.id.detail_date_txt);
        mHighTxt = (TextView) fragmentLayout.findViewById(R.id.detail_high_textview);
        mLowTxt = (TextView) fragmentLayout.findViewById(R.id.detail_low_textview);
        mIconImg = (ImageView) fragmentLayout.findViewById(R.id.detail_icon);
        mDescriptionTxt = (TextView) fragmentLayout.findViewById(R.id.detail_description_textview);
        mWindTxt = (TextView) fragmentLayout.findViewById(R.id.detail_wind_textview);
        mHumidityTxt = (TextView) fragmentLayout.findViewById(R.id.detail_humidity_textview);
        mPressureTxt = (TextView) fragmentLayout.findViewById(R.id.detail_pressure_textview);
        mCompassView = (MyCompassView) fragmentLayout.findViewById(R.id.detail_compassview);


        return fragmentLayout;
    }

    // part of Loader callback interface for interacting with weather content provider
    // start database request
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader args" + args);


        if (args != null && args.getParcelable(DETAIL_URI) != null) {
            mUri = args.getParcelable(DETAIL_URI);
            Log.v(LOG_TAG, "detail Uri in on Create Loader is" + mUri);
        }

        if (null != mUri) {
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

            // start database request from location specified in mUri, with information defined in DETAIL_COLUMNS
            return new CursorLoader(getActivity(), mUri, DETAIL_COLUMNS, null, null, sortOrder);
        }


        return null;

        // Sort order:  Ascending, by date.

    }

    // Column details definition for local database request
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

            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,

            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };

    // part of LoaderCallback interface updates User interface with the data provided in Cursor
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished");

        setData(data);


    }

    // helper function for updating User interface with information from database (Cursor)
    private void setData(Cursor data) {
        if (data.moveToNext()) {

            // get data from Cursor
            boolean isMetric = Utility.isMetric(getActivity());
            double high = data.getDouble(COL_WEATHER_MAX_TEMP);
            double low = data.getDouble(COL_WEATHER_MIN_TEMP);
            long date = data.getLong(COL_WEATHER_DATE);
            double humidity = data.getDouble(COL_WEATHER_HUMIDITY);
            double windSpeed = data.getDouble(COL_WEATHER_WIND);
            double windDirection = data.getDouble(COL_WEATHER_DEGREES);
            double pressure = data.getDouble(COL_WEATHER_PRESSURE);
            String weatherDescription = data.getString(COL_WEATHER_DESC);
            int weatherId = data.getInt(COL_WEATHER_DESCRIPTION_ID);
            int artId = Utility.getArtResourceForWeatherCondition(weatherId);
            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
            String highTxt = Utility.formatTemperature(getActivity(), high, isMetric);
            String lowTxt = Utility.formatTemperature(getActivity(), low, isMetric);

            // update UI elements with new data
            mIconImg.setImageResource(artId);
            mDayTxt.setText(Utility.getDayName(getActivity(), date));
            mDateTxt.setText(dateText);
            mHighTxt.setText(highTxt);
            mLowTxt.setText(lowTxt);
            mWindTxt.setText(Utility.getFormattedWind(getActivity(), windSpeed, windDirection));
            mPressureTxt.setText(String.format(getActivity()
                    .getString(R.string.format_pressure), pressure));
            mHumidityTxt.setText(String
                    .format(getActivity().getString(R.string.format_humidity), humidity));
            mDescriptionTxt.setText(weatherDescription);
            mCompassView.updateData(windDirection);

            // saves information from database in formatted String to be used for sharing weather info with other apps
            // (i.e facebook, twitter, email, messaging, etc)
            mForecastStr = String.format("%s - %s - %s/%s", dateText, weatherDescription, highTxt, lowTxt);

            if (mShareActionProvider != null)
                mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    // part of LoaderCallback interface
    // no code neded here
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    // helper function that is called when the user changes the location for which to see the weather
    // re-queries database for new weather information
    void onLocationChanged(String newLocation) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            //  launch a new database query to get weather info for new location
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

}
