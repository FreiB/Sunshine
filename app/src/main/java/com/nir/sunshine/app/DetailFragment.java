package com.nir.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nir.sunshine.app.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecast;

    private Uri mUri;

   private TextView mTextDate;
   private TextView mTextDay;
   private TextView mTextDesc;
   private TextView mTextHigh;
   private TextView mTextLow;
   private TextView mTextHumid;
   private TextView mTextWind;
   private TextView mTextPress;
    private ImageView mIconView;


    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER = 0;

    public static final String DETAIL_URI = "Uri";

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND = 6;
    private static final int COL_WEATHER_PRESSURE = 7;
    private static final int COL_WEATHER_WIND_DIR = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;
    private static final int COL_LOCATION_SETTING = 10;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    public static DetailFragment newInstance(Uri uri) {
        DetailFragment f = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_URI, uri);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

         mTextDate = (TextView)rootView.findViewById(R.id.details_date_textview);
         mTextDay = (TextView)rootView.findViewById(R.id.details_day_textview);
         mTextDesc = (TextView)rootView.findViewById(R.id.details_desc_textview);
         mTextHigh = (TextView)rootView.findViewById(R.id.details_max_temp_textview);
         mTextLow = (TextView)rootView.findViewById(R.id.details_min_temp_textview);
         mTextHumid = (TextView)rootView.findViewById(R.id.details_humidity_textview);
         mTextWind = (TextView)rootView.findViewById(R.id.details_wind_textview);
         mTextPress = (TextView)rootView.findViewById(R.id.details_pressure_textview);
        mIconView = (ImageView)rootView.findViewById(R.id.details_icon_imageview);



        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.detailfragment, menu);



        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else {
            Log.d(LOG_TAG, "Share Action Provider null");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mUri != null)
            return new CursorLoader(getActivity(),
                mUri,
                FORECAST_COLUMNS,
                null,
                null,
                null);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(!data.moveToFirst())
            return;

        int weatherID = data.getInt(COL_WEATHER_CONDITION_ID);
        String dataString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        String dayString = Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE));
        String weatherDescription = data.getString(COL_WEATHER_DESC);
        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        //String date = Utility.getFriendlyDayString(getActivity(), data.getLong(COL_WEATHER_DATE));
        String humidity = String.format(getString(R.string.format_humidity), data.getFloat(COL_WEATHER_HUMIDITY));
        String wind = Utility.getFormattedWind(getActivity(),
                data.getFloat(COL_WEATHER_WIND),
                data.getFloat(COL_WEATHER_WIND_DIR));
        String pressure = String.format(getString(R.string.format_pressure), data.getFloat(COL_WEATHER_PRESSURE));


        mTextDate.setText(dataString);
        mTextDay.setText(dayString);
        mTextDesc.setText(weatherDescription);
        mTextHigh.setText(high);
        mTextLow.setText(low);
        mTextHumid.setText(humidity);
        mTextWind.setText(wind);
        mTextPress.setText(pressure);
        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherID));
        mIconView.setContentDescription(weatherDescription);



        if (mShareActionProvider != null)
            mShareActionProvider.setShareIntent(createShareForecastIntent());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {



    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}
