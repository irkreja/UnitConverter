package com.programmingexample.unitconverter;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class GridActivity extends AppCompatActivity {


    // Array of strings for ListView Title
    String[] gridviewTitle = new String[]{
            "Currency\nConverter",
            "Time\nConverter",
            "Temperature\nConverter",
            "Digital Storage\nConverter",
            "Area\nConverter",
            "Length\nConverter",
            "Mass\nConverter",
            "Volume\nConverter",
            "Speed\nConverter",
            "Pressure\nConverter",
            "Energy\nConverter",
            "Frequency\nConverter"
    };


    int[] gridviewImage = new int[]{
            R.mipmap.cur,
            R.mipmap.time,
            R.mipmap.tmp,
            R.mipmap.data,
            R.mipmap.area,
            R.mipmap.length,
            R.mipmap.mass,
            R.mipmap.volume,
            R.mipmap.speed,
            R.mipmap.pressure,
            R.mipmap.energy,
            R.mipmap.frequency

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref= getSharedPreferences("mypref", 0);
        int view = sharedPref.getInt("viewer", 0);

        if (view==20) {
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_grid);
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 12; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("gridview_title", gridviewTitle[i]);
            hm.put("gridview_image", Integer.toString(gridviewImage[i]));
            aList.add(hm);
        }

        String[] from = {"gridview_image", "gridview_title"};
        int[] to = {R.id.gridview_image, R.id.gridview_item_title};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.grid_items, from, to);
        GridView androidListView = (GridView) findViewById(R.id.grid_view);
        androidListView.setAdapter(simpleAdapter);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (position == 0) {
                    Intent myIntent = new Intent(view.getContext(), SplashActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

                if (position == 1) {
                    Intent myIntent = new Intent(view.getContext(), TimeConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

                if (position == 2) {
                    Intent myIntent = new Intent(view.getContext(), TempConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

                if (position == 3) {
                    Intent myIntent = new Intent(view.getContext(), DigitalStorageConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

                if (position == 4) {
                    Intent myIntent = new Intent(view.getContext(), AreaConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

                if (position == 5) {
                    Intent myIntent = new Intent(view.getContext(), LengthConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

                if (position == 6) {
                    Intent myIntent = new Intent(view.getContext(), MassConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }
                if (position == 7) {
                    Intent myIntent = new Intent(view.getContext(), VolumeConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

                if (position == 8) {
                    Intent myIntent = new Intent(view.getContext(), SpeedConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

                if (position == 9) {
                    Intent myIntent = new Intent(view.getContext(), PressureConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

                if (position == 10) {
                    Intent myIntent = new Intent(view.getContext(), EnergyConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

                if (position == 11) {
                    Intent myIntent = new Intent(view.getContext(), FrequencyConvertActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.swap_list:
                SharedPreferences sharedPref= getSharedPreferences("mypref", 0);
                SharedPreferences.Editor editor= sharedPref.edit();
                editor.clear();      //its clear all data.
                editor.putInt("viewer", 20);
                //commits your edits
                editor.commit();
                Intent intent = new Intent(this, ListActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.about:
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GridActivity.this,R.style.AppTheme_Dialog_Green);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("Unit Converter");
                builder.setMessage("Version: 1.0.0\n\nDeveloped by\nMd. Rejaul Karim\nMd. Rakibul Hasan Sarkar\nSushmita Rani Bardhan\n\nA simple and friendly unit converter with a clean user interface. It gives easy access to commonly used conversions which are Currency, Time, Temperature, Digital Storage, Area, Length, Mass, Volume, Speed, Pressure, Energy and Frequency.");
                // Add the buttons0
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                // Create the AlertDialog
                android.support.v7.app.AlertDialog dialog = builder.show();
                TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
                break;
            case R.id.mnu_exit:
                android.support.v7.app.AlertDialog.Builder quit = new android.support.v7.app.AlertDialog.Builder(GridActivity.this,R.style.AppTheme_Dialog_Green);
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

        android.support.v7.app.AlertDialog.Builder quit = new android.support.v7.app.AlertDialog.Builder(GridActivity.this,R.style.AppTheme_Dialog_Green);
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
    }

}