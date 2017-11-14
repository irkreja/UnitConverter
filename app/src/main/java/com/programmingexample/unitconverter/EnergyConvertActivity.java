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
public class EnergyConvertActivity extends AppCompatActivity {
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
        Double finalNumber = energyConvert(number, originalUnits, newUnits);
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
                R.array.energy_units, android.R.layout.simple_spinner_item);

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
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EnergyConvertActivity.this, R.style.AppTheme_Dialog_Green);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.energy);
                builder.setTitle("Energy Converter");
                builder.setMessage("You can convert between Joule [J], Kilojoule [kJ], Gram calorie (cal), Kilocalorie (kcal), Watt hour (Wh), Kilowatt hour (kWh) and Electronvolt (eV).");
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
                android.support.v7.app.AlertDialog.Builder quit = new android.support.v7.app.AlertDialog.Builder(EnergyConvertActivity.this, R.style.AppTheme_Dialog_Green);
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


    public double energyConvert(double originalNum, String originalUnit, String desiredUnit)
    { //Begin energyConvert
        //Make two variable doubles, one the original double and one the new one
        double num2 = 0.0d;

        //Store the units into new strings. I find this to be safer, as I can't override the originals this way.
        //Also convert them to lower case
        String original = originalUnit;
        String newU = desiredUnit;

        switch(original)
        { //Begin of conversion table
            case "Joule [J]":
            { //Begin converting from
                switch(newU)
                {
                    case "Joule [J]":
                        num2 = originalNum;
                        break;
                    case "Kilojoule [kJ]":
                        num2 = originalNum * 0.001;
                        break;
                    case "Gram calorie (cal)":
                        num2 = originalNum * 0.239006;
                        break;
                    case "Kilocalorie (kcal)":
                        num2 = originalNum * 0.000239006;
                        break;
                    case "Watt hour (Wh)":
                        num2 = originalNum * 0.000277778;
                        break;
                    case "Kilowatt hour (kWh)":
                        num2 = originalNum * 0.00000027778;
                        break;
                    case "Electronvolt (eV)":
                        num2 = originalNum * 6.242e+18;
                        break;
                }
                break;
            } //end converting from

            case "Kilojoule [kJ]":
            { //Begin converting from
                switch(newU)
                {
                    case "Joule [J]":
                        num2 = originalNum * 1000;
                        break;
                    case "Kilojoule [kJ]":
                        num2 = originalNum;
                        break;
                    case "Gram calorie (cal)":
                        num2 = originalNum * 239.006;
                        break;
                    case "Kilocalorie (kcal)":
                        num2 = originalNum * 0.239006;
                        break;
                    case "Watt hour (Wh)":
                        num2 = originalNum * 0.277778;
                        break;
                    case "Kilowatt hour (kWh)":
                        num2 = originalNum * 0.000277778;
                        break;
                    case "Electronvolt (eV)":
                        num2 = originalNum * 6.242e+21;
                        break;
                }
                break;
            } //end converting from

            case "Gram calorie (cal)":
            { //Begin converting from
                switch(newU)
                {
                    case "Joule [J]":
                        num2 = originalNum * 4.184;
                        break;
                    case "Kilojoule [kJ]":
                        num2 = originalNum * 0.004184;
                        break;
                    case "Gram calorie (cal)":
                        num2 = originalNum;
                        break;
                    case "Kilocalorie (kcal)":
                        num2 = originalNum * 0.001;
                        break;
                    case "Watt hour (Wh)":
                        num2 = originalNum * 0.00116222;
                        break;
                    case "Kilowatt hour (kWh)":
                        num2 = originalNum * 1.1622e-6;
                        break;
                    case "Electronvolt (eV)":
                        num2 = originalNum * 2.611e+19;
                        break;
                }
                break;
            } //end converting from

            case "Kilocalorie (kcal)":
            { //Begin converting from
                switch(newU)
                {
                    case "Joule [J]":
                        num2 = originalNum * 4184;
                        break;
                    case "Kilojoule [kJ]":
                        num2 = originalNum * 4.184;
                        break;
                    case "Gram calorie (cal)":
                        num2 = originalNum * 1000;
                        break;
                    case "Kilocalorie (kcal)":
                        num2 = originalNum;
                        break;
                    case "Watt hour (Wh)":
                        num2 = originalNum * 1.16222;
                        break;
                    case "Kilowatt hour (kWh)":
                        num2 = originalNum * 0.00116222;
                        break;
                    case "Electronvolt (eV)":
                        num2 = originalNum * 2.611e+22;
                        break;
                }
                break;
            } //end converting from

            case "Watt hour (Wh)":
            { //Begin converting from
                switch(newU)
                {
                    case "Joule [J]":
                        num2 = originalNum * 3600;
                        break;
                    case "Kilojoule [kJ]":
                        num2 = originalNum * 3.6;
                        break;
                    case "Gram calorie (cal)":
                        num2 = originalNum * 860.421;
                        break;
                    case "Kilocalorie (kcal)":
                        num2 = originalNum * 0.860421;
                        break;
                    case "Watt hour (Wh)":
                        num2 = originalNum;
                        break;
                    case "Kilowatt hour (kWh)":
                        num2 = originalNum * 0.001;
                        break;
                    case "Electronvolt (eV)":
                        num2 = originalNum * 2.247e+22;
                        break;
                }
                break;
            } //end converting from

            case "Kilowatt hour (kWh)":
            { //Begin converting from
                switch(newU)
                {
                    case "Joule [J]":
                        num2 = originalNum * 3.6e+6;
                        break;
                    case "Kilojoule [kJ]":
                        num2 = originalNum * 3600;
                        break;
                    case "Gram calorie (cal)":
                        num2 = originalNum * 860421;
                        break;
                    case "Kilocalorie (kcal)":
                        num2 = originalNum * 860.421;
                        break;
                    case "Watt hour (Wh)":
                        num2 = originalNum * 1000;
                        break;
                    case "Kilowatt hour (kWh)":
                        num2 = originalNum;
                        break;
                    case "Electronvolt (eV)":
                        num2 = originalNum * 2.247e+25;
                        break;
                }
                break;
            } //end converting from

            case "Electronvolt (eV)":
            { //Begin converting from
                switch(newU)
                {
                    case "Joule [J]":
                        num2 = originalNum * 1.6022e-19;
                        break;
                    case "Kilojoule [kJ]":
                        num2 = originalNum * 1.6022e-22;
                        break;
                    case "Gram calorie (cal)":
                        num2 = originalNum * 3.8293e-20;
                        break;
                    case "Kilocalorie (kcal)":
                        num2 = originalNum * 3.8293e-23;
                        break;
                    case "Watt hour (Wh)":
                        num2 = originalNum * 4.4505e-23;
                        break;
                    case "Kilowatt hour (kWh)":
                        num2 = originalNum * 4.4505e-26;
                        break;
                    case "Electronvolt (eV)":
                        num2 = originalNum * 2.247e+25;
                        break;
                }
                break;
            } //end converting from
        } //End of conversion table

        //Return the final number, num2
        return num2;
    } //End energy Convert

}
