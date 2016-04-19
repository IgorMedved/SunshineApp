package com.example.android.sunshine.app;

/**
 * Created by Admin User on 12/18/2015.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter
{
    private final static int VIEW_TYPE_TODAY = 0;
    private final static int VIEW_TYPE_TOMORROW =1;

    private boolean mUseTodayLayout;

    public void setUseTodayLayout (boolean useLayout)
    {
        mUseTodayLayout = useLayout;
    }

    @Override
    public int getViewTypeCount ()
    {
        return 2;
    }

    @Override
    public int getItemViewType (int position)
    {
        return (mUseTodayLayout && position==0)?VIEW_TYPE_TODAY:VIEW_TYPE_TOMORROW;
    }

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;

    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private double mLongitude;
    private double mLatitude;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows( double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);

        String highLowStr = Utility.formatTemperature(mContext, high, isMetric) + "/" + Utility.formatTemperature(mContext, low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        /*// get row indices for our cursor
        int idx_max_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        int idx_min_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        int idx_date = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
        int idx_short_desc = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);*/

        String highAndLow = formatHighLows(
                cursor.getDouble(COL_WEATHER_MAX_TEMP),
                cursor.getDouble(COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(COL_WEATHER_DATE)) +
                " - " + cursor.getString(COL_WEATHER_DESC) +
                " - " + highAndLow;
    }



    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        switch (viewType)
        {
        case VIEW_TYPE_TODAY:
            layoutId = R.layout.list_item_forecast_today;
            break;
        case VIEW_TYPE_TOMORROW:
            layoutId = R.layout.list_item_forecast;
            break;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ForecastViewHolder viewHolder = new ForecastViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        ForecastViewHolder viewHolder = (ForecastViewHolder)view.getTag();
        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(COL_WEATHER_CONDITION_ID);
        // Use placeholder image for now
        int viewType = getItemViewType(cursor.getPosition());
        int iconResourceId = -1;

        switch (viewType)
        {
        case VIEW_TYPE_TODAY:
            iconResourceId = Utility.getArtResourceForWeatherCondition(weatherId);
            break;
        case VIEW_TYPE_TOMORROW:
            iconResourceId = Utility.getIconResourceForWeatherCondition(weatherId);

        }
        viewHolder.iconView.setImageResource(iconResourceId);

        //  Read date from cursor
        String date = Utility.getFriendlyDayString(context, cursor.getLong(COL_WEATHER_DATE));


        viewHolder.dateView.setText(date);

        //  Read weather forecast from cursor
        String forecast = cursor.getString(COL_WEATHER_DESC);

        viewHolder.descriptionView.setText(forecast);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(COL_WEATHER_MAX_TEMP);

        viewHolder.highView.setText(Utility.formatTemperature(context, high, isMetric));

        //  Read low temperature from cursor
        double low = cursor.getDouble(COL_WEATHER_MIN_TEMP);



        viewHolder.lowView.setText(Utility.formatTemperature(context, low, isMetric));

        // read latitude and logitude from cursor
        mLatitude = cursor.getDouble(COL_COORD_LAT);
        mLongitude = cursor.getDouble(COL_COORD_LONG);
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }
}

// ForecastViewHolder contains all UI elements inside forecast list item
class ForecastViewHolder
{
    ImageView iconView;
    TextView dateView;
    TextView descriptionView;
    TextView highView;
    TextView lowView;

    public ForecastViewHolder (View view)
    {
        iconView = (ImageView)view.findViewById(R.id.list_item_icon);
        dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
        descriptionView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
        highView = (TextView) view.findViewById(R.id.list_item_high_textview);
        lowView = (TextView)view.findViewById(R.id.list_item_low_textview);
    }
}