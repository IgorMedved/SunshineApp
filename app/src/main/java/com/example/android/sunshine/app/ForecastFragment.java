package com.example.android.sunshine.app;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        /*for (String s : resultStrs) {
            Log.v(LOG_TAG, "Forecast entry: " + s);
        }*/
        return resultStrs;

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
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_refresh)
        {
            //Log.e(LOG_TAG, "I am here!");
            FetchWeatherTask mySnippet = new FetchWeatherTask();
            String zip = OLDBRIDGE_ZIP;
            mySnippet.execute((String)null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        weatherForecasts = new ArrayList<>();
        weatherForecasts.add("Today - Sunny - 88/63");
        weatherForecasts.add("Tomorrow - Foggy - 70/46");
        weatherForecasts.add("Weds - Cloudy - 72/63");
        weatherForecasts.add("Thurs - Rainy - 64/53");
        weatherForecasts.add("Fri - Foggy - 70/46");
        weatherForecasts.add("Sat - Sunny - 76/68");

        //String stringUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Burlington,ca&mode=json&units=metric&cnt=7&appid="+ApiId.APIID;

        FetchWeatherTask mySnippet = new FetchWeatherTask();
        mySnippet.execute(OLDBRIDGE_ZIP);



        mForecastAdapter = new ForecastAdapter(getActivity(),R.layout.list_item_forecast, R.id.list_item_forecast_textview, weatherForecasts);
        ListView weekList = (ListView)rootView.findViewById(R.id.listview_forecast);
        weekList.setAdapter(mForecastAdapter);
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

    private class FetchWeatherTask extends AsyncTask <String, Void, String>
    {
        private final static String BASE_URI = "http://api.openweathermap.org/data/2.5/forecast/daily";
        private final static String LOCATION = "q";
        private final static String UNITS = "units";
        private final static String DAYS = "cnt";
        private final static String APP_ID = "appid";
        private final static String ZIP = "zip";

      //      ?q=Burlington,ca&mode=json&units=metric&cnt=7&appid="+ApiId.APIID;


        @Override
        protected String doInBackground (String... params)
        {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

           ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                try
                {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are avaiable at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast

                    Uri.Builder builder = Uri.parse(BASE_URI).buildUpon();

                    if (params == null || params[0] == null)
                        builder.appendQueryParameter(LOCATION, "Burlington,ca");
                    else
                        builder.appendQueryParameter(LOCATION, params[0]);
                    builder.appendQueryParameter(UNITS, "metric");
                    builder.appendQueryParameter(DAYS, "7");
                    builder.appendQueryParameter(APP_ID, ApiId.APIID);

                    Log.v(LOG_TAG, builder.build().toString());
                    URI jUri = new URI(builder.build().toString());
                    URL url = jUri.toURL();



                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null)
                    {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0)
                    {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    forecastJsonStr = buffer.toString();
                   // Log.v(LOG_TAG, forecastJsonStr);
                }
                catch (IOException e)
                {
                    Log.e("LOG_TAG", "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                }
                catch (URISyntaxException e)
                {
                    Log.e("LOG_TAG", "Error ", e);
                }
                finally
                {
                    if (urlConnection != null)
                    {
                        urlConnection.disconnect();
                    }
                    if (reader != null)
                    {
                        try
                        {
                            reader.close();
                        }
                        catch (final IOException e)
                        {
                            Log.e("PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }

                // fetch data
            }

            else {
               // Intent intent = new Intent (MainActivity.this, ConnectionErrorActivity.class);
                // display error
                Log.e(LOG_TAG, "No internet connection");
            }


            return forecastJsonStr;
        }

        @Override
        protected void onPostExecute(String webData)
        {
            jsonData = webData;
            try
            {
                weatherForecasts = Arrays.asList(getWeatherDataFromJson(jsonData, 7));
                mForecastAdapter.loadData(weatherForecasts);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    }

}
