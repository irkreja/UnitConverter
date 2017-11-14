package com.programmingexample.unitconverter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;


public class CurrencyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //define members that correspond to Views in our layout
    private Button mCalcButton;
    private TextView mConvertedTextView;
    private EditText mAmountEditText;
    private TextView mUpdatedTextView;
    private Spinner mForSpinner, mHomSpinner;
    private String[] mCurrencies;

    public static final String FOR = "FOR_CURRENCY";
    public static final String HOM = "HOM_CURRENCY";

    //this will contain my developers key
    private String mKey;
    //used to fetch the 'rates' json object from openexchangerates.org

    public static final String RATES = "rates";
    public static final String TIMES = "timestamp";
    public static final String URL_BASE =
            "http://openexchangerates.org/api/latest.json?app_id=";

    //used to format data from openexchangerates.org
    private static final DecimalFormat DECIMAL_FORMAT = new
            DecimalFormat("#,##0.00000");

    //create this interface for instrumentation testing with threads
    private CurrencyTaskCallback mCurrencyTaskCallback;

    public static interface CurrencyTaskCallback {
        void executionDone();
    }

    public void setCurrencyTaskCallback(CurrencyTaskCallback currencyTaskCallback) {
        this.mCurrencyTaskCallback = currencyTaskCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        //add icon to actionbar
/*        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);*/

        //unpack ArrayList from the bundle and convert to array
        ArrayList<String> arrayList = ((ArrayList<String>)
                getIntent().getSerializableExtra(SplashActivity.KEY_ARRAYLIST));
        Collections.sort(arrayList);
        mCurrencies = arrayList.toArray(new String[arrayList.size()]);

        //assign references to our Views;
        mConvertedTextView = (TextView) findViewById(R.id.txt_converted);
        mUpdatedTextView = (TextView) findViewById(R.id.time);
        mAmountEditText = (EditText) findViewById(R.id.edt_amount);
        mCalcButton = (Button) findViewById(R.id.btn_calc);
        mForSpinner = (Spinner) findViewById(R.id.spn_for);
        mHomSpinner = (Spinner) findViewById(R.id.spn_hom);

        //controller: mediates model and view
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(

                //context
                this,
                //view: layout you see when the spinner is closed
                R.layout.spinner_closed,
                //model: the array of Strings
                mCurrencies
        );

        //view: layout you see when the spinner is open
        arrayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        //assign adapters to spinners
        mHomSpinner.setAdapter(arrayAdapter);
        mForSpinner.setAdapter(arrayAdapter);

        mHomSpinner.setOnItemSelectedListener(this);
        mForSpinner.setOnItemSelectedListener(this);

        //set to shared-preferences or pull from shared-preferences on restart
        if (savedInstanceState == null
                && (PrefsMgr.getString(this, FOR) == null &&
                PrefsMgr.getString(this, HOM) == null)) {

            mForSpinner.setSelection(findPositionGivenCode("CNY", mCurrencies));
            mHomSpinner.setSelection(findPositionGivenCode("USD", mCurrencies));

            PrefsMgr.setString(this, FOR,"CNY");
            PrefsMgr.setString(this, HOM,"USD");


        } else {

            mForSpinner.setSelection(findPositionGivenCode(PrefsMgr.getString(this,
                    FOR), mCurrencies));
            mHomSpinner.setSelection(findPositionGivenCode(PrefsMgr.getString(this,
                    HOM), mCurrencies));
        }
        mCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                if (isNumeric(String.valueOf(mAmountEditText.getText()))){
                    new CurrencyConverterTask().execute(URL_BASE + mKey);
                } else {
                    Toast.makeText(CurrencyActivity.this, "Not a numeric value, try again.", Toast.LENGTH_LONG).show();
                }

            }
        });

        mKey = getKey("open_key");
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void invertCurrencies() {
        int nFor = mForSpinner.getSelectedItemPosition();
        int nHom = mHomSpinner.getSelectedItemPosition();
        mForSpinner.setSelection(nHom);
        mHomSpinner.setSelection(nFor);
        mConvertedTextView.setText("");

        PrefsMgr.setString(this, FOR, extractCodeFromCurrency((String)
                mForSpinner.getSelectedItem()));
        PrefsMgr.setString(this, HOM, extractCodeFromCurrency((String)
                mHomSpinner.getSelectedItem()));
    }
    private int findPositionGivenCode(String code, String[] currencies) {

        for (int i = 0; i < currencies.length; i++) {
            if (extractCodeFromCurrency(currencies[i]).equalsIgnoreCase(code)) {
                return i;
            }
        }
        //default
        return 0;
    }

    private String extractCodeFromCurrency(String currency) {
        return (currency).substring(0,3);
    }

    private String getKey(String keyName){
        AssetManager assetManager = this.getResources().getAssets();
        Properties properties = new Properties();
        try {
            InputStream inputStream = assetManager.open("keys.properties");
            properties.load(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  properties.getProperty(keyName);

    }

    public static boolean isNumeric(String str) {
        try{
            double dub = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public abstract class NoUnderlineClickableSpan extends ClickableSpan {
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_currency, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.mnu_invert:
                invertCurrencies();
                break;
            case R.id.mnu_about:
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(CurrencyActivity.this, R.style.AppTheme_Dialog_Green);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.cur);
                builder.setTitle(R.string.title_activity_currency);
                builder.setMessage(R.string.CurrencyMessage);
                // Add the buttons0
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                // Create the AlertDialog
                android.support.v7.app.AlertDialog dialog = builder.show();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                messageText.setMovementMethod(LinkMovementMethod.getInstance());
               // ((TextView) builder.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                messageText.setGravity(Gravity.CENTER);
                break;
            case R.id.mnu_exit:
                android.support.v7.app.AlertDialog.Builder quit = new android.support.v7.app.AlertDialog.Builder(CurrencyActivity.this, R.style.AppTheme_Dialog_Green);
                quit.setCancelable(false);
                quit.setIcon(android.R.drawable.ic_dialog_alert);
                //quit.setTitle("Please Confirm");
                quit.setMessage("Are you sure want to exit Unit Converter?");
                // Add the buttons
                quit.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                });
                quit.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                // Create the AlertDialog
                android.support.v7.app.AlertDialog qdialog = quit.create();
                qdialog.show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        SharedPreferences sharedPref = getSharedPreferences("mypref", 0);
        int view = sharedPref.getInt("viewer", 0);
        if (view == 10) {
            Intent intent = new Intent(this, GridActivity.class);
            startActivity(intent);
            finish();
        } else if (view == 20) {
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, GridActivity.class);
            startActivity(intent);
            finish();

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {

            case R.id.spn_for:
                PrefsMgr.setString(this, FOR,
                        extractCodeFromCurrency((String)mForSpinner.getSelectedItem()));
                break;

            case R.id.spn_hom:
                PrefsMgr.setString(this, HOM,
                        extractCodeFromCurrency((String)mHomSpinner.getSelectedItem()));
                break;

            default:
                break;
        }

        mConvertedTextView.setText("");

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // This is our main converted Task

    private class CurrencyConverterTask extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CurrencyActivity.this,R.style.StyledDialog);
            progressDialog.setTitle("Calculating Result...");
            progressDialog.setMessage("One moment please...");
            progressDialog.setCancelable(true);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CurrencyConverterTask.this.cancel(true);
                            progressDialog.dismiss();
                        }
                    });
            progressDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            return new JSONParser().getJSONFromUrl(params[0]);
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            double dCalculated = 0.0;
            long unixSeconds;
            String strForCode =
                    extractCodeFromCurrency(mCurrencies[mForSpinner.getSelectedItemPosition()]);
            String strHomCode = extractCodeFromCurrency(mCurrencies[mHomSpinner.
                    getSelectedItemPosition()]);
            String strAmount = mAmountEditText.getText().toString();
            try {
                if (jsonObject == null){
                    throw new JSONException("no data available.");
                }
                JSONObject jsonRates = jsonObject.getJSONObject(RATES);
                //JSONObject jsonTimes = jsonObject.getJSONObject(TIMES).getInt(0);
                //Tmp = jsonTimes.toString();
                unixSeconds = jsonObject.getInt(TIMES);
                Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa"); // the format of your date
                sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating
                String formattedDate = sdf.format(date);
                mUpdatedTextView.setText("Last updated on: "+formattedDate);

                if (strHomCode.equalsIgnoreCase("USD")){
                    dCalculated = Double.parseDouble(strAmount) / jsonRates.getDouble(strForCode);
                } else if (strForCode.equalsIgnoreCase("USD")) {
                    dCalculated = Double.parseDouble(strAmount) * jsonRates.getDouble(strHomCode) ;
                }
                else {
                    dCalculated = Double.parseDouble(strAmount) * jsonRates.getDouble(strHomCode)
                            / jsonRates.getDouble(strForCode) ;
                }
            } catch (JSONException e) {
                Toast.makeText(
                        CurrencyActivity.this,
                        "There's been a JSON exception: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                mConvertedTextView.setText("");
                e.printStackTrace();
            }
            mConvertedTextView.setText(DECIMAL_FORMAT.format(dCalculated) + " " + strHomCode);


            progressDialog.dismiss();

            //for testing
            if (mCurrencyTaskCallback != null) {
                mCurrencyTaskCallback.executionDone();
            }
        }
    }
}