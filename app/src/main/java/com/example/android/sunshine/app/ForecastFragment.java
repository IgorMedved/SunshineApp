package com.example.android.sunshine.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 *
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ForecastFragment extends Fragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>
{





    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();


    private static final int FORECAST_LOADER = 0;

    public static final String LIST_POSITION = "position";






    private ForecastAdapter mForecastAdapter;
    private int mListPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;



    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    public void setUseTodayLayout (boolean useTodayLayout)
    {
        mUseTodayLayout = useTodayLayout;
        if(mForecastAdapter!=null)
        {
            mForecastAdapter.setUseTodayLayout(useTodayLayout);
        }
    }



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ForecastFragment ()
    {
    }





    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);



        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);



        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);






    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater )
    {
        inflater.inflate(R.menu.forecastfragment, menu);




    }
// onStart commented out to save on network usage. To update data press refresh from menu
    @Override
    public void onStart ()
    {
        super.onStart();

    }

/*    private void updateWeather()
    {

        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }*/

    private void refreshWeather()
    {


       /* AlarmManager alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), SunshineService.AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() +
                        5   * 1000, alarmIntent);

        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);*/
        SunshineSyncAdapter.syncImmediately(getActivity());

    }

    public void onLocationChanged()
    {
        refreshWeather();
        //getLoaderManager().restartLoader(FORECAST_LOADER, null, this);

    }




    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_refresh)
        {
            //Log.e(LOG_TAG, "I am here!");
            refreshWeather();

            return true;
        }
        else if (id == R.id.action_show_location)
        {
            double latitude = mForecastAdapter.getLatitude();
            double longitude = mForecastAdapter.getLongitude();
            String intentString = "geo:0,0?q="+latitude+","+ longitude +"(Forecast Location)";
            Log.v(LOG_TAG, "map intent message is " + intentString);
            Uri location = Uri.parse(intentString);

            Intent intent = new Intent (Intent.ACTION_VIEW, location);

            PackageManager packageManager = getActivity().getPackageManager();
            List activities = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;
            if (isIntentSafe)
                startActivity(intent);

            else
                Log.e(LOG_TAG, "Could not start map intent with " + intentString);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState (Bundle outState)
    {
        if(mListPosition!= ListView.INVALID_POSITION)
            outState.putInt(LIST_POSITION, mListPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        mForecastAdapter = new ForecastAdapter(getActivity(),null,0);
        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);


        if (savedInstanceState !=null && savedInstanceState.containsKey(LIST_POSITION))
            mListPosition = savedInstanceState.getInt(LIST_POSITION);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView)rootView.findViewById(R.id.listview_forecast);
        //setEmptyText(getString(R.string.no_weather_available));

        mListView.setAdapter(mForecastAdapter);


        mListView.setOnItemClickListener(this);



        return rootView;
    }

    @Override
    public void onAttach (Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity
                    .toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach ()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick (AdapterView<?> parent, View view, int position, long id)
    {
        if (null != mListener)
        {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            if (cursor != null)
            {
                String locationSetting = Utility.getPreferredLocation(getActivity());
                mListener.onFragmentInteraction(WeatherContract.WeatherEntry
                        .buildWeatherLocationWithDate(locationSetting, cursor
                                .getLong(ForecastAdapter.COL_WEATHER_DATE)));

            }
        }
        mListPosition = position;
    }



    @Override
    public Loader<Cursor> onCreateLoader (int id, Bundle args)
    {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System
                .currentTimeMillis());

        return new CursorLoader(getActivity(), weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder);


    }

    @Override
    public void onLoadFinished (Loader<Cursor> loader, Cursor data)
    {
        mForecastAdapter.swapCursor(data);
        if (mListPosition!= ListView.INVALID_POSITION)
        {

            mListView.smoothScrollToPosition(mListPosition);

        }
    }

    @Override
    public void onLoaderReset (Loader<Cursor> loader)
    {
        mForecastAdapter.swapCursor(null);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void onFragmentInteraction (Uri uri);
    }


    private static final String[] FORECAST_COLUMNS = {
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



}
