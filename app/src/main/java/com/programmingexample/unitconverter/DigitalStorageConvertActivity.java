package com.programmingexample.unitconverter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.text.DecimalFormat;



/** Implemented the activity and set up the onCreate and getSpinnerItems methods. Added code allow the user to interact
 *  with the spinners and make item selections. Had the text box show which item was selected to test functionality
 *  Implemented the convertTime method. This method works in conjunction with the Converter class to convert the input
 *  to the proper output. Made the text view show the final output.
 */
public class DigitalStorageConvertActivity extends AppCompatActivity {
    //Instance variables
    //Need access to the spinners and the text views
    private Spinner spinner1, spinner2;
    private EditText textField1;
    private TextView textView;
    private Double number;

    //When the activity is created, the XML file needs to be shown and the spinners need to be activated and populated.
    //To populate the spinners, the addItemsOnSpinner method was created. A listener is then used to listen
    //for the spinners being used.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //instantiate textField1

        //Selector listener
        AdapterView.OnItemSelectedListener onSpinner = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //These do nothing- this listener merely waits to tell the convert() method what is selected.

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This does nothing right now
            }
        };

        setContentView(R.layout.activity_common);
        //Populate the spinners
        setSpinnerItems();
        //Activate the listener
        spinner1.setOnItemSelectedListener(onSpinner);

    }

    /**
     * Converts a number from the EditText and converts it to a specified new time unit
     */
    public void ConvertUnit(View view) { //Begin convert unit
        //First, hide the keyboard when the convert button is pressed. Thanks to the following Stack Exchange thread for the help:
        //http://stackoverflow.com/questions/3400028/close-virtual-keyboard-on-button-press
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        textField1 = (EditText) findViewById(R.id.unit_input);
        //First, get the number from the text field
        String numStr = textField1.getText().toString();
        //Convert the text to a double.
        //If the user inputs nothing, or inputs an invalid character instead of a number, or
        //if the user's input contains too many decimals, return as 0
        if (numStr.equals("")) {
            number = 0.0;
            textField1.setText(String.valueOf(number.intValue()));
        } else if (numStr.equals(".")) {
            number = 0.0;
            textField1.setText(String.valueOf(number.intValue()));
        } else if (numStr.contains("..")) {
            number = 0.0;
        } else {
            number = Double.valueOf(numStr);
        }


        //Finally, get the text view we need later to display the answer
        textView = (TextView) findViewById(R.id.unit_textView2);

        //Get the units to be used
        String originalUnits = spinner1.getSelectedItem().toString();
        String newUnits = spinner2.getSelectedItem().toString();

        //Feed the units and the number into the Converter class and capture the output
        Double finalNumber = dataConvert(number, originalUnits, newUnits);
        //Set the text view to show the new number. Need to convert double to string
        String finalString = String.valueOf(finalNumber);


        if (finalString.endsWith(".0")) {
            finalString = finalString.replace(".0","");
            textView.setText(finalString);
        }

        else if (finalString.contains("E")) {
            DecimalFormat df = new DecimalFormat("0.###E0");
            finalString = String.valueOf(df.format(finalNumber));
            String[] sp = finalString.split("E");
            String s = sp[0] + " \u00D7 10<sup>" + sp[1] + "</sup>\t";
            textView.setText(Html.fromHtml(s));
        }

        else {
            textView.setText(finalString);
        }

    } //End convert unit

    /**
     * Sets the items in the drop down for the two spinners
     */
    public void setSpinnerItems() {
        spinner1 = (Spinner) findViewById(R.id.unit_spinner1);
        spinner2 = (Spinner) findViewById(R.id.unit_spinner2);
        //Get the options from the string array in strings.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.data_units, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mnu_invert:
                int nFor = spinner1.getSelectedItemPosition();
                int nHom = spinner2.getSelectedItemPosition();
                spinner1.setSelection(nHom);
                spinner2.setSelection(nFor);
                break;
            case R.id.mnu_about:
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DigitalStorageConvertActivity.this, R.style.AppTheme_Dialog_Green);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.data);
                builder.setTitle("Digital Storage Converter");
                builder.setMessage("You can convert between bits [b], bytes [B], kilobits [Kb], kilobytes [KB], megabits [Mb], megabytes [MB], gigabits [Gb], gigabytes [GB], terabits [Tb] and terabytes [TB].");
                // Add the buttons0
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                // Create the AlertDialog
                android.support.v7.app.AlertDialog dialog = builder.show();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
                break;
            case R.id.mnu_exit:
                android.support.v7.app.AlertDialog.Builder quit = new android.support.v7.app.AlertDialog.Builder(DigitalStorageConvertActivity.this, R.style.AppTheme_Dialog_Green);
                quit.setCancelable(false);
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


    public double dataConvert(double originalNum, String original, String newU)
    { //Begin dataConvert
        //Make two variable doubles, one the original double and one the new one
        double num2 = 0.0d;


        switch(original)
        { //Begin of conversion table
            case "bits [b]":
            { //Begin converting from bits [b]
                switch(newU)
                {
                    case "bits [b]":
                        num2 = originalNum;
                        break;
                    case "bytes [B]":
                        num2 = originalNum * 1/8;
                        break;
                    case "kilobits [Kb]":
                        num2 = originalNum * 1/1024;
                        break;
                    case "kilobytes [KB]":
                        num2 = originalNum * 1/8/1024;
                        break;
                    case "megabits [Mb]":
                        num2 = originalNum * 1/1024/1024;
                        break;
                    case "megabytes [MB]":
                        num2 = originalNum * 1/8/1024/1024;
                        break;
                    case "gigabits [Gb]":
                        num2 = originalNum * 1/1024/1024/1024;
                        break;
                    case "gigabytes [GB]":
                        num2 = originalNum * 1/8/1024/1024/1024;
                        break;
                    case "terabits [Tb]":
                        num2 = originalNum * 1/1024/1024/1024/1024;
                        break;
                    case "terabytes [TB]":
                        num2 = originalNum * 1/8/1024/1024/1024/1024;
                        break;
                }
                break;
            } //end converting from bits [b]
            case "bytes [B]":
            { //Begin converting from Bytes [b]
                switch(newU)
                {
                    case "bits [b]":
                        num2 = originalNum * 8;
                        break;
                    case "bytes [B]":
                        num2 = originalNum;
                        break;
                    case "kilobits [Kb]":
                        num2 = originalNum * 8/1024;
                        break;
                    case "kilobytes [KB]":
                        num2 = originalNum * 1/1024;
                        break;
                    case "megabits [Mb]":
                        num2 = originalNum * 8/1024/1024;
                        break;
                    case "megabytes [MB]":
                        num2 = originalNum * 1/1024/1024;
                        break;
                    case "gigabits [Gb]":
                        num2 = originalNum * 8/1024/1024/1024;
                        break;
                    case "gigabytes [GB]":
                        num2 = originalNum * 1/1024/1024/1024;
                        break;
                    case "terabits [Tb]":
                        num2 = originalNum * 8/1024/1024/1024/1024;
                        break;
                    case "terabytes [TB]":
                        num2 = originalNum * 1/1024/1024/1024/1024;
                        break;
                }
                break;
            } //End converting from Bytes [b]
            case "kilobits [Kb]":
            { //Begin converting from kilobits [Kb]
                switch(newU)
                {
                    case "bits [b]":
                        num2 = originalNum * 1024;
                        break;
                    case "bytes [B]":
                        num2 = originalNum * 1024/8;
                        break;
                    case "kilobits [Kb]":
                        num2 = originalNum;
                        break;
                    case "kilobytes [KB]":
                        num2 = originalNum * 1024/8/1024;
                        break;
                    case "megabits [Mb]":
                        num2 = originalNum * 1024/1024/1024;
                        break;
                    case "megabytes [MB]":
                        num2 = originalNum * 1024/8/1024/1024;
                        break;
                    case "gigabits [Gb]":
                        num2 = originalNum * 1024/1024/1024/1024;
                        break;
                    case "gigabytes [GB]":
                        num2 = originalNum * 1024/8/1024/1024/1024;
                        break;
                    case "terabits [Tb]":
                        num2 = originalNum * 1024/1024/1024/1024/1024;
                        break;
                    case "terabytes [TB]":
                        num2 = originalNum * 1024/8/1024/1024/1024/1024;
                        break;
                }
                break;
            } //End converting from kilobits [Kb]
            case "kilobytes [KB]":
            { //Begin converting from kilobytes [KB]
                switch(newU)
                {
                    case "bits [b]":
                        num2 = originalNum * 1024 * 8;
                        break;
                    case "bytes [B]":
                        num2 = originalNum * 1024;
                        break;
                    case "kilobits [Kb]":
                        num2 = originalNum;
                        break;
                    case "kilobytes [KB]":
                        num2 = originalNum * 1024/1024;
                        break;
                    case "megabits [Mb]":
                        num2 = originalNum * 1/1024;
                        break;
                    case "megabytes [MB]":
                        num2 = originalNum * 1024/1024/1024;
                        break;
                    case "gigabits [Gb]":
                        num2 = originalNum * 1/1024/1024;
                        break;
                    case "gigabytes [GB]":
                        num2 = originalNum * 1024/1024/1024/1024;
                        break;
                    case "terabits [Tb]":
                        num2 = originalNum *  1/1024/1024/1024;
                        break;
                    case "terabytes [TB]":
                        num2 = originalNum * 1024/1024/1024/1024/1024;
                        break;
                }
                break;
            } //End converting from kilobytes [KB]
        case "megabits [Mb]":
        { //Begin converting from kilobytes [KB]
            switch(newU)
            {
                case "bits [b]":
                    num2 = originalNum * 1024 * 1024;
                    break;
                case "bytes [B]":
                    num2 = originalNum * 1024*1024/8;
                    break;
                case "kilobits [Kb]":
                    num2 = originalNum * 1024;
                    break;
                case "kilobytes [KB]":
                    num2 = originalNum * 1024/8;
                    break;
                case "megabits [Mb]":
                    num2 = originalNum;
                    break;
                case "megabytes [MB]":
                    num2 = originalNum * 1024/8/1024;
                    break;
                case "gigabits [Gb]":
                    num2 = originalNum * 1/1024;
                    break;
                case "gigabytes [GB]":
                    num2 = originalNum * 1024/8/1024/1024;
                    break;
                case "terabits [Tb]":
                    num2 = originalNum *  1/1024/1024;
                    break;
                case "terabytes [TB]":
                    num2 = originalNum * 1024/8/1024/1024/1024;
                    break;
            }
            break;
        } //End converting from megabits [Mb]
            case "megabytes [MB]":
            { //Begin converting from megabytes [MB]
                switch(newU)
                {
                    case "bits [b]":
                        num2 = originalNum * 8 * 1024 * 1024;
                        break;
                    case "bytes [B]":
                        num2 = originalNum * 1024*1024;
                        break;
                    case "kilobits [Kb]":
                        num2 = originalNum * 8 * 1024;
                        break;
                    case "kilobytes [KB]":
                        num2 = originalNum * 1024;
                        break;
                    case "megabits [Mb]":
                        num2 = originalNum * 8;
                        break;
                    case "megabytes [MB]":
                        num2 = originalNum;
                        break;
                    case "gigabits [Gb]":
                        num2 = originalNum * 8/1024;
                        break;
                    case "gigabytes [GB]":
                        num2 = originalNum * 1/1024;
                        break;
                    case "terabits [Tb]":
                        num2 = originalNum *  8/1024/1024;
                        break;
                    case "terabytes [TB]":
                        num2 = originalNum * 1/1024/1024;
                        break;
                }
                break;
            } //End converting from megabytes [MB]
            case "gigabits [Gb]":
            { //Begin converting from gigabits [Gb]
                switch(newU)
                {
                    case "bits [b]":
                        num2 = originalNum * 1024 * 1024 * 1024;
                        break;
                    case "bytes [B]":
                        num2 = originalNum * 1024 * 1024 * 1024/8;
                        break;
                    case "kilobits [Kb]":
                        num2 = originalNum * 1024 * 1024;
                        break;
                    case "kilobytes [KB]":
                        num2 = originalNum * 1024 * 1024/8;
                        break;
                    case "megabits [Mb]":
                        num2 = originalNum * 1024;
                        break;
                    case "megabytes [MB]":
                        num2 = originalNum * 1024/8;
                        break;
                    case "gigabits [Gb]":
                        num2 = originalNum;
                        break;
                    case "gigabytes [GB]":
                        num2 = originalNum * 1024/8/1024;
                        break;
                    case "terabits [Tb]":
                        num2 = originalNum *  1/1024;
                        break;
                    case "terabytes [TB]":
                        num2 = originalNum * 1024/8/1024/1024;
                        break;
                }
                break;
            } //End converting from gigabits [Gb]
            case "gigabytes [GB]":
            { //Begin converting from gigabytes [GB]
                switch(newU)
                {
                    case "bits [b]":
                        num2 = originalNum * 1024 * 1024 * 1024 * 8;
                        break;
                    case "bytes [B]":
                        num2 = originalNum * 1024 * 1024 * 1024;
                        break;
                    case "kilobits [Kb]":
                        num2 = originalNum * 1024 * 1024 * 8;
                        break;
                    case "kilobytes [KB]":
                        num2 = originalNum * 1024 * 1024;
                        break;
                    case "megabits [Mb]":
                        num2 = originalNum * 1024 * 8;
                        break;
                    case "megabytes [MB]":
                        num2 = originalNum * 1024;
                        break;
                    case "gigabits [Gb]":
                        num2 = originalNum * 8;
                        break;
                    case "gigabytes [GB]":
                        num2 = originalNum;
                        break;
                    case "terabits [Tb]":
                        num2 = originalNum *  8/1024;
                        break;
                    case "terabytes [TB]":
                        num2 = originalNum * 1/1024;
                        break;
                }
                break;
            } //End converting from gigabytes [GB]
            case "terabits [Tb]":
            { //Begin converting from terabits [Tb]
                switch(newU)
                {
                    case "bits [b]":
                        num2 = originalNum * 1024 * 1024 * 1024 * 1024;
                        break;
                    case "bytes [B]":
                        num2 = originalNum * 1024 * 1024 * 1024 * 1024/8;
                        break;
                    case "kilobits [Kb]":
                        num2 = originalNum * 1024 * 1024 * 1024;
                        break;
                    case "kilobytes [KB]":
                        num2 = originalNum * 1024 * 1024*1024/8;
                        break;
                    case "megabits [Mb]":
                        num2 = originalNum * 1024 * 1024;
                        break;
                    case "megabytes [MB]":
                        num2 = originalNum * 1024*1024/8;
                        break;
                    case "gigabits [Gb]":
                        num2 = originalNum * 1024;
                        break;
                    case "gigabytes [GB]":
                        num2 = originalNum * 1024/8;
                        break;
                    case "terabits [Tb]":
                        num2 = originalNum;
                        break;
                    case "terabytes [TB]":
                        num2 = originalNum * 1/8;
                        break;
                }
                break;
            } //End converting from terabits [Tb]
            case "terabytes [TB]":
            { //Begin converting from terabytes [TB]
                switch(newU)
                {
                    case "bits [b]":
                        num2 = originalNum * 1024 * 1024 * 1024 * 1024 * 8;
                        break;
                    case "bytes [B]":
                        num2 = originalNum * 1024 * 1024 * 1024 * 1024;
                        break;
                    case "kilobits [Kb]":
                        num2 = originalNum * 1024 * 1024 * 1024 * 8;
                        break;
                    case "kilobytes [KB]":
                        num2 = originalNum * 1024 * 1024 * 1024;
                        break;
                    case "megabits [Mb]":
                        num2 = originalNum * 1024 * 1024 * 8;
                        break;
                    case "megabytes [MB]":
                        num2 = originalNum * 1024*1024;
                        break;
                    case "gigabits [Gb]":
                        num2 = originalNum * 1024 * 8;
                        break;
                    case "gigabytes [GB]":
                        num2 = originalNum * 1024;
                        break;
                    case "terabits [Tb]":
                        num2 = originalNum * 8;
                        break;
                    case "terabytes [TB]":
                        num2 = originalNum;
                        break;
                }
                break;
            } //End converting from terabytes [TB]
    } //End of conversion table

        //Return the final number, num2
        return num2;
    } //End dataConvert

    }
