package com.example.android.sunshine.app;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.dummy.DummyContent;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private static final int FORECAST_LOADER = 0;



    // TODO: Rename and change types of parameters
    private String jsonData;
    private String mParam2;


    private ForecastAdapter mForecastAdapter;


    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */


    // TODO: Rename and change types of parameters
    public static ForecastFragment newInstance (String param1, String param2)
    {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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



        if (getArguments() != null)
        {
            jsonData = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater )
    {
        inflater.inflate(R.menu.forecastfragment, menu);




    }
// onStart commented out to save on network usage. To update data press refresh from menu
   /* @Override
    public void onStart ()
    {
        super.onStart();
        updateWeather();
    }
*/
    private void updateWeather()
    {

        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
    }

    public void onLocationChanged()
    {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_refresh)
        {
            //Log.e(LOG_TAG, "I am here!");
            updateWeather();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        mForecastAdapter = new ForecastAdapter(getActivity(),null,0);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);




        Log.v("Test", "onCreate ForecastFragment");

        mListView = (ListView)rootView.findViewById(R.id.listview_forecast);
        setEmptyText(getString(R.string.no_weather_available));
        mListView.setAdapter(mForecastAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick (AdapterView adapterView, View view, int position, long l)
            {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null)
                {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(WeatherContract.WeatherEntry
                                    .buildWeatherLocationWithDate(locationSetting, cursor
                                            .getLong(ForecastAdapter.COL_WEATHER_DATE)));
                    startActivity(intent);
                }
            }
        });


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
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText (CharSequence emptyText)
    {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView)
        {
            ((TextView) emptyView).setText(emptyText);
        }
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
        public void onFragmentInteraction (String id);
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
