package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_view_location) {
            final String BASIC_GEO_PATH = "geo:0,0";
            final String QUERY_PARAM = "q";

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            String location = settings.getString(getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));

            Uri locationUri = Uri.parse(BASIC_GEO_PATH).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, location)
                    .build();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(locationUri);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.d(LOG_TAG, "Couldn't view this location:  " + location + ", no handler.");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
