package com.example.chambersnotofsecret.argo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private ResponseReceiver receiver;
    private DbOpenHelper dbOpenHelper;
    private SQLiteDatabase dbReader;
    Context context;

    String TAG="TAG";
    String TAG2="TAG2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "0--------------------------");
        // opes my db and reads it
        dbOpenHelper = new DbOpenHelper(getApplicationContext());
        dbReader = dbOpenHelper.getWritableDatabase();
        Log.d(TAG, "1---------------------");
        //INstantiating Response reciver classs
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);//Registering response reciever class

        Log.d(TAG, "1-------------------------");
        //calls fn
        updateDb();
        Log.d(TAG, "last-----------------");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Log.d(TAG,"menu1");
                updateDb();
            Log.d(TAG, "menu last");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateDb() {
        Log.d(TAG,"Update db1");
        //Starts Intent service class which works in background
        Intent intent = new Intent(this, SimpleIntentService.class);
        startService(intent);
        Log.d(TAG, "Update intent satrted");

    }
    public void onDestroy(){
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void listFloats(View view) {
        //Starts activity after click which displayes float's ids and further details
        Intent intent = new Intent(this,DisplayFloatsList.class );
        startActivity(intent);
    }

    public void floatsMap(View view) {
// starts map activity
        Intent intent = new Intent(this,FloatsMapsActivity.class );
        startActivity(intent);

    }

    public class ResponseReceiver extends BroadcastReceiver{
//never used this functions since i didn't fetch any result from intent service
        public static final String ACTION_RESP =
                "com.example.intent.action.MESSAGE_PROCEED";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"onRecieve");
         }
    }
}
