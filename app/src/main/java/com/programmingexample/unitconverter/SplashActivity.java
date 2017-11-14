package com.programmingexample.unitconverter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;


public class SplashActivity extends AppCompatActivity {

    //url to currency codes used in this application
    public static final String URL_CODES = "http://openexchangerates.org/api/currencies.json";
    public static final String KEY_ARRAYLIST = "key_arraylist";

    //ArrayList of currencies that will be fetched and passed into GridActivity
    private ArrayList<String> mCurrencies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        runTask();
    }

    private class FetchCodesTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            return new JSONParser().getJSONFromUrl(params[0]);
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                if (jsonObject == null) {
                    throw new JSONException("no data available.");
                }
                Iterator iterator = jsonObject.keys();
                String key = "";
                mCurrencies = new ArrayList<String>();
                while (iterator.hasNext()) {
                    key = (String)iterator.next();
                    mCurrencies.add(key + " | " + jsonObject.getString(key));
                }
                Intent mainIntent = new Intent(SplashActivity.this, CurrencyActivity.class);
                mainIntent.putExtra(KEY_ARRAYLIST, mCurrencies);
                startActivity(mainIntent);
                finish();
            } catch (JSONException e) {
                Toast.makeText(
                        SplashActivity.this,
                        "There's been a JSON exception: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                e.printStackTrace();
                finish();
            }
        }
    }
    public void runTask () {
        if(isNetworkAvailable()) {

            new FetchCodesTask().execute(URL_CODES);

        } else {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SplashActivity.this,R.style.AlertDialogStyle);
            builder.setCancelable(false);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle("No Internet Connection");
            builder.setMessage("Internet Connection is required.\nCheck your connection and retry.");
            // Add the buttons
            builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    runTask();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    SharedPreferences sharedPref = getSharedPreferences("mypref", 0);
                    int view = sharedPref.getInt("viewer", 0);
                    if (view == 10) {
                        Intent intent = new Intent(SplashActivity.this, GridActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (view == 20) {
                        Intent intent = new Intent(SplashActivity.this, ListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, GridActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
            // Create the AlertDialog
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();
            Toast.makeText(this, "Network Unavailable!", Toast.LENGTH_LONG).show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
