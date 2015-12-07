package com.example.chambersnotofsecret.argo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static android.database.DatabaseUtils.dumpCursorToString;


public class DisplayFloatsList extends ActionBarActivity {
    private DbOpenHelper dbOpenHelper;
    private SQLiteDatabase dbReader;
    Context context;
    String TAG="TAG";
    final ArrayList<String> IDlist = new ArrayList<String>();
    final ArrayList<String> Active = new ArrayList<String>();
    final ArrayList<String> Manufacturer  = new ArrayList<String>();
    final ArrayList<String> Instrument  = new ArrayList<String>();
    final ArrayList<String> Controller = new ArrayList<String>();
    final ArrayList<String> Duty_Cycle = new ArrayList<String>();
    final ArrayList<String> Pref_press = new ArrayList<String>();
    final ArrayList<String> Start_date = new ArrayList<String>();
    final ArrayList<String> Start_Lat = new ArrayList<String>();
    final ArrayList<String> Start_Long = new ArrayList<String>();
    final ArrayList<String> L_Date = new ArrayList<String>();
    final ArrayList<String> Exp_Levels = new ArrayList<String>();
    final ArrayList<String> Exp_cycles = new ArrayList<String>();
    final ArrayList<String> NFull = new ArrayList<String>();
    final ArrayList<String> NPart = new ArrayList<String>();
    final ArrayList<String> Missing = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_floats_list);

// for dialog
        context=getApplicationContext();
//Opens db
        dbOpenHelper = new DbOpenHelper(getApplicationContext());
        dbReader = dbOpenHelper.getWritableDatabase();


        String query ="select * from argo";
        Cursor cur = dbReader.rawQuery(query, null);
//saves all data in respective lists
        cur.moveToFirst();//moves cursor to first record
        int i = 0;
        while(!cur.isAfterLast()){//loops until a record exist after the previous one
            if(i>0)
            IDlist.add(cur.getString(0));
            Active.add(cur.getString(1));
            Manufacturer.add(cur.getString(2));
            Instrument.add(cur.getString(3));
            Controller.add(cur.getString(4));
            Duty_Cycle.add(cur.getString(5));
            Pref_press.add(cur.getString(6));
            Start_date.add(cur.getString(7));
            Start_Lat.add(cur.getString(8));
            Start_Long.add(cur.getString(9));
            L_Date.add(cur.getString(10));
            Exp_Levels.add(cur.getString(11));
            Exp_cycles.add(cur.getString(12));
            NFull.add(cur.getString(13));
            NPart.add(cur.getString(14));
            Missing.add(cur.getString(15));

            cur.moveToNext();//iterates cursor to next record
            i++;
        }
        final ListView listview = (ListView) findViewById(R.id.listView);

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, IDlist);
        // fill list view with values
        listview.setAdapter(adapter);
//create dialog
        final  AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Details");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//Alerts all details of each float in a dialog
                alertDialog.setMessage( "ID = " + IDlist.get(position) + "\n"+
                                        "Active = " + Active.get(position) + "\n" +
                                        "Manufacturer = "+ Manufacturer.get(position)+"\n"+
                                        "Instrument = "+ Instrument.get(position)+"\n"+
                                        "Controller = "+ Controller.get(position)+"\n"+
                                        "Duty_Cycle = "+ Duty_Cycle.get(position)+"\n"+
                                        "Pref_press = "+ Pref_press.get(position)+"\n"+
                                        "Start_date = "+ Start_date.get(position)+"\n"+
                                        "Start_Lat = "+ Start_Lat.get(position)+"\n"+
                                        "Start_Long = "+ Start_Long.get(position)+"\n"+
                                        "L_Date = "+ L_Date.get(position)+"\n"+
                                        "Exp_Levels = "+ Exp_Levels.get(position)+"\n"+
                                        "Exp_cycles = "+ Exp_cycles.get(position)+"\n"+
                                        "NFull = "+ NFull.get(position)+"\n"+
                                        "NPart = "+ NPart.get(position));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        //     Log.d(TAG, String.valueOf(getApplicationContext()));
               alertDialog.setIcon(R.drawable.argo_logo);
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_floats_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.update) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
