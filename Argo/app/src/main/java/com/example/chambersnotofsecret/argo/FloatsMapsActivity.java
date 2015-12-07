package com.example.chambersnotofsecret.argo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static android.database.DatabaseUtils.dumpCursorToString;

public class FloatsMapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private DbOpenHelper dbOpenHelper;
    private SQLiteDatabase dbReader;
    final ArrayList<String> Start_Lat = new ArrayList<String>();
    final ArrayList<String> Start_Long = new ArrayList<String>();
    final ArrayList<String> Active = new ArrayList<String>();
    final ArrayList<String> ID = new ArrayList<String>();
    Integer CursorLength;
    String TAG="TAGMAP";
    Integer i=0;
    Integer k = 0;
    Double Lat=0.0;
    Double Lng=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floats_maps);
        Log.d("TAG", "1");

        Log.d("TAG", "2");
        dbOpenHelper = new DbOpenHelper(getApplicationContext());
        Log.d("TAG", "3");
        dbReader = dbOpenHelper.getWritableDatabase();
        String query ="select ID,Active,Start_Lat,Start_Long from argo";
        Cursor cur = dbReader.rawQuery(query, null);
   //     Log.d("TAG", dumpCursorToString(cur));

        cur.moveToFirst();//moves cursor to first record
        Log.d("TAG", "6.1");
        CursorLength=cur.getCount();
        Log.d("TAG", "Cur lentght = " +CursorLength);
        Log.d("TAG", "6.2");
        while(!cur.isAfterLast()){//loops until a record exist after the previous one
            if(k>0) {
                Log.d("TAG", "6.3");
                ID.add(cur.getString(0));
                Active.add(cur.getString(1));
                Start_Lat.add(cur.getString(2));
                Start_Long.add(cur.getString(3));
                cur.moveToNext();//iterates cursor to next record

            }
            k++;
        }
        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        Log.d("TAG", "8");
    }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            Log.d("TAG", "9 Map == null");
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Log.d("TAG", "10");
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        
        Log.d("TAG", "11");
        addMarkerToMap();

    }

    private void addMarkerToMap() {
        Log.d("TAG", "i="+i+" curLenght"+CursorLength);
        while(i<CursorLength) {
            Lat=Double.parseDouble(Start_Lat.get(i));
            Lng=Double.parseDouble(Start_Long.get(i));
            char c = Active.get(i).charAt(0);
            Log.d("TAG", i + " Displaying cursor" + Lat + "  " + Lng);
//CHecks whethe float is active or not and respectively changes snippet and color
            if(c == 'N') {
                Log.d("TAG","Not active");
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Lat, Lng))
                        .snippet("Float Not Active")
                        .title("ID = " + ID.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }else if(c =='Y'){
                Log.d("TAG","Active");
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Lat, Lng))
                        .snippet("Float Active")
                        .title("ID = " + ID.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            }
            i++;
        }
    }
}
