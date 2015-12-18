package com.example.android.sunshine.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.sunshine.app.dummy.DummyContent;

import java.util.ArrayList;
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
public class ForecastFragment extends Fragment implements AbsListView.OnItemClickListener
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private static final String OLDBRIDGE_ZIP = "08857";

    // TODO: Rename and change types of parameters
    private String jsonData;
    private String mParam2;

    private List<String> weatherForecasts;
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
    private ListAdapter mAdapter;

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

        setHasOptionsMenu (true);

        if (getArguments() != null)
        {
            jsonData = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater )
    {
        inflater.inflate(R.menu.forecastfragment, menu);




    }

    @Override
    public void onStart ()
    {
        super.onStart();
        updateWeather();
    }

    private void updateWeather()
    {
        FetchWeatherTask mySnippet = new FetchWeatherTask(getActivity(), mForecastAdapter);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());


        String locationPref = sharedPref.getString(getString(R.string.pref_key_location), getString(R.string.pref_default_location));
        mySnippet.execute(locationPref);
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        weatherForecasts = new ArrayList<>();
        /*weatherForecasts.add("Today - Sunny - 88/63");
        weatherForecasts.add("Tomorrow - Foggy - 70/46");
        weatherForecasts.add("Weds - Cloudy - 72/63");
        weatherForecasts.add("Thurs - Rainy - 64/53");
        weatherForecasts.add("Fri - Foggy - 70/46");
        weatherForecasts.add("Sat - Sunny - 76/68");*/

        //String stringUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Burlington,ca&mode=json&units=metric&cnt=7&appid="+ApiId.APIID;





        mForecastAdapter = new ForecastAdapter(getActivity(),R.layout.list_item_forecast, R.id.list_item_forecast_textview, weatherForecasts);
        ListView weekList = (ListView)rootView.findViewById(R.id.listview_forecast);
        weekList.setAdapter(mForecastAdapter);
        weekList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id)
            {
                /*Toast toast = Toast.makeText(getActivity(), mForecastAdapter.getItem(position), Toast.LENGTH_SHORT);
                toast.show();*/

                Intent intent = new Intent (getActivity(), DetailActivity.class);
                intent.putExtra ("dayWeatherForecast", mForecastAdapter.getItem(position));
                startActivity(intent);
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



}
