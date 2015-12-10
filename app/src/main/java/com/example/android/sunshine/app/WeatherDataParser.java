package com.example.android.sunshine.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin User on 12/9/2015.
 */
public class WeatherDataParser
{


        /**
         * Given a string of the form returned by the api call:
         * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
         * retrieve the maximum temperature for the day indicated by dayIndex
         * (Note: 0-indexed, so 0 would refer to the first day).
         */
        public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
                throws JSONException
        {

            JSONObject forecast = new JSONObject (weatherJsonStr);
            JSONArray list;
            JSONObject givenDay;
            JSONObject dayTemp;
            String maxTemp;
            if (forecast!= null)
            {
                list = forecast.getJSONArray("list");
                if (list!= null && list.length() > dayIndex)
                {
                    givenDay = list.getJSONObject(dayIndex);
                    if (givenDay!= null)
                    {
                        dayTemp = givenDay.getJSONObject("temp");
                        if (dayTemp!=null)
                        {
                            maxTemp = dayTemp.getString("max");
                            System.out.println (maxTemp.toString());
                            return Double.valueOf(maxTemp);
                        }

                    }
                }


            }


            // TODO: add parsing code here
            return -1;
        }

    }


