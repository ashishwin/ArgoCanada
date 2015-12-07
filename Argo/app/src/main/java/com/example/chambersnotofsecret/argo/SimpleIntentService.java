package com.example.chambersnotofsecret.argo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.example.chambersnotofsecret.argo.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static android.database.DatabaseUtils.dumpCursorToString;


//http://code.tutsplus.com/tutorials/android-fundamentals-intentservice-basics--mobile-6183



public class SimpleIntentService extends IntentService {

    String TAG ="TAG" ;
    Integer executeQuery=0;
    Integer updateQuery=0;
    Integer createQuery=0;

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    FileReader fReader;
    private DbOpenHelper dbOpenHelper;
    private SQLiteDatabase dbReader;
    private Context context;
    private static String file_url = "http://www.meds-sdmm.dfo-mpo.gc.ca/alphapro/argo/perf_report.csv";


    public SimpleIntentService() {
        super("SimpleIntentService");

        Log.d("TAG", "Intent Service Called");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = this;
        //Opens database and gets writable , since i will be updating it
        Log.d("TAG", "1");
        dbOpenHelper = new DbOpenHelper(context);
        Log.d("TAG", "2");
        dbReader = dbOpenHelper.getWritableDatabase();
        Log.d("TAG", "3");

//I use async task to download file hence calling functions and passing thru its url
        new DownloadFileFromURL().execute(file_url);
        Log.d("TAG", "4");
        //I never neede to transfer data from this background process to Response reciever in Main Activity
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcastIntent);
        Log.d("TAG", "5");
    }



    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        NotificationManager mNotifyManager;
        NotificationCompat.Builder mBuilder;
        Integer i=0;
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("TAG", "6");

            //Begins to Displays progress bar in Notification
            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle("Databse Update")
                    .setContentText("Update in progress")
                    .setSmallIcon(R.drawable.argo_logo)
                    .setColor(getResources().getColor(R.color.theme));
            Log.d(TAG, "download1");
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            Log.d(TAG, "Before DownloADING");
            //Calls separate fn to download file
            downloadFile(f_url);
            Log.d(TAG, "Before database");
            updateDatabase();
            //Calls separate function to update db after downloading file
            Log.d(TAG, "After update db");
            return null;
        }
        protected void onProgressUpdate(String...  progress) {
            // setting progress percentage
//KEEPS progress bar moving while the do in background keeps working
            // thus i use to complete progress bar
            i=i+2;
            Log.d(TAG, "i=" + i);

            mBuilder.setProgress(100, i, false);
            mNotifyManager.notify(0, mBuilder.build());

          }
        @Override
        protected void onPostExecute(String file_url) {
            Log.d(TAG, "post execute");
            // removes progress bar
            mBuilder.setProgress(0, 0, false)
                    .setContentText("Update complete");
            Log.d(TAG, "on progress 8");
            mNotifyManager.notify(0, mBuilder.build());
            Log.d(TAG, "on progress 9");
        }
    }


    public void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        Log.d(TAG, "file copy");
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) > 0)
            outputStream.write(buffer, 0, length);
        inputStream.close();
        outputStream.close();
        Log.d(TAG,"copied");
    }
    public void downloadFile(String[] f_url){
        //Downloads file
        int count;

        Log.d("TAG", "");
        try {
            Log.d(TAG, "download2");
            URL url = new URL(f_url[0]);
            URLConnection connection = url.openConnection();
            Log.d(TAG, "download3");
            connection.connect();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            // Output stream saves here
            OutputStream output = new FileOutputStream("/sdcard/argo.csv");
            byte data[] = new byte[1024];
            long total = 0;

            while ((count = input.read(data)) != -1) {

                total += count;
                output.write(data, 0, count);
            }
            Log.d(TAG, "download DONE");
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) { Log.e("Error: ", e.getMessage()); }

    }
    private void updateDatabase() {
        //Updates or creates db which ever necessary
        Log.d("TAG", "1234");
        try {

            String dbName = "argo.db";
            String dbPath = "/data/data/" + getApplicationContext().getPackageName() + "/databases/";
            Log.d("TAG", "7");
            Log.d(TAG, dbPath + dbName);
            Log.d("TAG", "8");
            File checkDBFile = new File(dbPath + dbName);//links FIle object to database File
            // create db file if it doesn't exist nad creates if not

            Log.d("TAG", "9");
            if (!checkDBFile.exists()) {
//Creates file if doesent exist
                Log.d("TAG", "db file doesnt exist");
                checkDBFile.mkdirs();

            }
            File destFile = new File(dbPath + dbName);

            Log.d("TAG", "10");
            if (!destFile.exists()) {
                Log.d(TAG, "First run, copying default database");
                copyFile(getApplicationContext().getAssets().open(dbName),
                        new FileOutputStream(dbPath + dbName));

                Log.d("TAG", "11");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("dbcheck", e.toString());
        }

        Log.d("TAG", "12");
        //Cursor to check later if data exists in db or not
        Cursor cursor =  dbReader.rawQuery("select ID from argo",null );

        Log.d("TAG", "13");

//        Log.d(TAG, dumpCursorToString(cursor));

        Log.d("TAG", "14");
            String fileName = "argo.csv";
            try{

                Log.d("TAG", "15");
                fReader = new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName);

          //      Log.d("TAG", "16");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        Log.d("TAG", "17");


        Log.d("TAG", "17.2");
        String data = "";
        String tableName = "argo";
        Log.d("TAG", "17.3");
        String columns =    "ID," +
                            "Active," +
                            "Manufacturer," +
                            "Instrument," +
                            "Controller," +
                            "Duty_Cycle," +
                            "Pref_press," +
                            "Start_date," +
                            "Start_Lat," +
                            "Start_Long," +
                            "L_Date," +
                            "Exp_Levels," +
                            "Exp_cycles," +
                            "NFull," +
                            "NPart," +
                            "Missing";
        //Insert String is used when db is created first time
        //Update string for updateing



        String InsertString1 = "INSERT INTO " + tableName + " (" + columns + ") values(";
        String InsertString2 = ");";
        String UpdateString1="Update "+tableName+" SET ";
        String UpdateString2 = "' Where ID='";
        Log.d("TAG", "18");
        dbReader.beginTransaction();
        Log.d("TAG", "19");
        try {
            //starts reading file line by line
            BufferedReader bufferReader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName));
            String tmp = bufferReader.readLine();


            Log.d("TAG","tmp ="+ tmp);
            //checks if line is empty
            if(bufferReader.readLine()!=null){
            while ((data = bufferReader.readLine()) != null) {
        //        Log.d("TAG", "22");
                StringBuilder string = new StringBuilder(InsertString1);
                StringBuilder updateString = new StringBuilder(UpdateString1);
       //         Log.d("TAG", "23");
                data = data.replaceAll(",,", ", ,");
                data = data.replaceAll(",,", ", ,");
                data = data.replaceAll("''", "''");
        //        Log.d("TAG", "24");
                String[] stringArray = data.split(",");

      //          Log.d(TAG,"stringLength ="+stringArray.length);
      //          Log.d(TAG,"stringArray[0] ="+stringArray[0]);
                if(stringArray.length>1) {
       //             Log.d(TAG, "stringArray[1] =" + stringArray[1]);
       //             Log.d(TAG, "stringArray[2] =" + stringArray[2]);



                    //Insert String is used when db is created first time
                    //Update string for updateing

                    updateString.append("Active ='");
                    string.append("'" + stringArray[0] + "', '");
                    updateString.append(stringArray[1] + "',Manufacturer ='");
                    string.append(stringArray[1] + "', '");
                    updateString.append(stringArray[2] + "',Instrument ='");
                    string.append(stringArray[2] + "', '");
                    updateString.append(stringArray[3] + "',Controller ='");
                    string.append(stringArray[3] + "', '");
                    updateString.append(stringArray[4] + "',Duty_Cycle ='");
                    string.append(stringArray[4] + "', '");
                    updateString.append(stringArray[5] + "',Pref_press ='");
                    string.append(stringArray[5] + "', '");
                    updateString.append(stringArray[6] + "',Start_date ='");
                    string.append(stringArray[6] + "', '");
                    updateString.append(stringArray[7] + "',Start_Lat ='");
                    string.append(stringArray[7] + "', '");
                    updateString.append(stringArray[8] + "',Start_Long ='");
                    string.append(stringArray[8] + "', '");
                    updateString.append(stringArray[9] + "',L_Date ='");
                    string.append(stringArray[9] + "', '");
                    updateString.append(stringArray[10] + "',Exp_Levels ='");
                    string.append(stringArray[10] + "', '");
                    updateString.append(stringArray[11] + "',Exp_cycles ='");
                    string.append(stringArray[11] + "', '");
                    updateString.append(stringArray[12] + "',NFull ='");
                    string.append(stringArray[12] + "', '");
                    updateString.append(stringArray[13] + "',NPart ='");
                    string.append(stringArray[13] + "', '");
                    updateString.append(stringArray[14] + "',Missing ='");
                    string.append(stringArray[14] + "', '");


//                    Log.d("TAG", "25");
                    if (stringArray.length == 15) {
                        string.append(" '");
                        updateString.append(" '");
                        Log.d("TAG", "26.1");
                    } else {
                        string.append(stringArray[15] + "'");
                        updateString.append(stringArray[15]);
    //                    Log.d("TAG", "26.2");
                    }
     //               Log.d("TAG", "27");
                    updateString.append(UpdateString2+stringArray[0]+"';");
                    string.append(InsertString2);
    //                Log.d("TAG", "28");


                    Log.d("TAG", String.valueOf(executeQuery));
                    executeQuery++;
//Skips first read line, unnecessary data
                    if (executeQuery > 1){
       //                     Log.d("TAG", "No of executions------------------------------" + String.valueOf(executeQuery));
                        //checks if rows need to be inserted or updated
                            if (cursor.getCount() < 1) {
            //                    Log.d("TAG", "Creating row");
                                createQuery++;
         //                       Log.d("TAG", "Execution query= /n\n" + string.toString());
                              dbReader.execSQL(string.toString());
                            } else {
                                updateQuery++;
           //                     Log.d("TAG", "Updating row");
           //                     Log.d("TAG", "Execution query= /n\n" + updateString.toString());
                              dbReader.execSQL(updateString.toString());
                        }
                    }
                }
            }
                Log.d("TAG", "Total executions------------------------------" + String.valueOf(executeQuery));
                Log.d("TAG", "create executions = " + createQuery);
                Log.d("TAG", "Update executions = "+updateQuery);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "END");

        dbReader.setTransactionSuccessful();
        dbReader.endTransaction();
    }
}