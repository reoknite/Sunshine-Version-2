package com.example.android.sunshine.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ShareActionProvider mShareActionProvider;
    private String mDetailForecastStr;
    private static final int WEATHER_DETAIL_LOADER = 1;

    private TextView mDayView;
    private TextView mDateView;
    private TextView mHighView;
    private TextView mLowView;
    private TextView mForecastView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    private static final String[] DETAIL_FORECAST_COLUMNS = {
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
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_PRESSURE = 6;
    static final int COL_WEATHER_WIND_SPEED = 7;
    static final int COL_WEATHER_DEGREES = 8;
    static final int COL_LOCATION_SETTING = 9;
    static final int COL_WEATHER_CONDITION_ID = 10;
    static final int COL_COORD_LAT = 11;
    static final int COL_COORD_LONG = 12;


    public DetailFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(WEATHER_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDayView = (TextView) rootView.findViewById(R.id.list_item_day_textview);
        mDateView = (TextView) rootView.findViewById(R.id.list_item_date_textview);
        mHighView = (TextView) rootView.findViewById(R.id.list_item_high_textview);
        mLowView = (TextView) rootView.findViewById(R.id.list_item_low_textview);
        mForecastView = (TextView) rootView.findViewById(R.id.list_item_forecast_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.list_item_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.list_item_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.list_item_pressure_textview);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (mDetailForecastStr != null) {
            setShareIntent();
        }
    }

    public void setShareIntent() {
        String forecast = mDetailForecastStr;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + " #sunshineapp");
        mShareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();

        if (intent == null) {
            return null;
        }

        CursorLoader loader = new CursorLoader(
                getActivity(),
                intent.getData(),
                DETAIL_FORECAST_COLUMNS,
                null,
                null,
                null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        Activity context = getActivity();
        View rootView = getView();
        if (rootView == null) {
            return;
        }
        boolean isMetric = Utility.isMetric(getActivity());

        mDayView.setText(Utility.getDayName(context, data.getLong(COL_WEATHER_DATE)));
        mDateView.setText(Utility.getFormattedMonthDay(context, data.getLong(COL_WEATHER_DATE)));

        String high = Utility.formatTemperature(context, data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        mHighView.setText(high);

        String low = Utility.formatTemperature(context, data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        mLowView.setText(low);

        String weatherDescription = data.getString(COL_WEATHER_DESC);
        mForecastView.setText(weatherDescription);
        mForecastView.setCompoundDrawablesWithIntrinsicBounds(
                null,
                context.getResources().getDrawable(Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_CONDITION_ID))),
                null,
                null);

        mHumidityView.setText(context.getString(R.string.format_humidity, data.getInt(COL_WEATHER_HUMIDITY)));
        mWindView.setText(Utility.getFormattedWind(context, data.getDouble(COL_WEATHER_WIND_SPEED), data.getDouble(COL_WEATHER_DEGREES)));
        mPressureView.setText(context.getString(R.string.format_pressure, data.getDouble(COL_WEATHER_PRESSURE)));

        String dateString = Utility.getFriendlyDayString(context, data.getLong(COL_WEATHER_DATE));
        mDetailForecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
        // set string value to share
        if (mShareActionProvider != null) {
            setShareIntent();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
