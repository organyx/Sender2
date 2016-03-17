package com.example.aleks.sender2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "MainActivity";
    private Pubnub pubnub;
    private TextView tv_r_message;
    private EditText et_message;
    private Object m;
    private Activity mActivity;

    private static final int REQUEST_LOCATION = 0;
    private static final int MINUTE = 60000;

    private TextView mLatitudeText;
    private TextView mLongitudeText;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mActivity = MainActivity.this;

        mLatitudeText = (TextView) findViewById(R.id.tv_lat);
        mLongitudeText = (TextView) findViewById(R.id.tv_lng);

        tv_r_message = (TextView) findViewById(R.id.tv_r_message);
        et_message = (EditText) findViewById(R.id.et_message);

        pubnub = new Pubnub("pub-c-7deeec3a-0ced-4d7c-a634-ccd573a51dba", "sub-c-52032cca-e9e7-11e5-8346-0619f8945a4f");

        try {
            pubnub.subscribe("my_channel", new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            pubnub.publish("my_channel", "Hello from " + Build.MODEL, new Callback() {
                            });
                            Log.d(TAG, "connectCallback:" + message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                            Log.d(TAG, "disconnectCallback:" + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                            Log.d(TAG, "reconnectCallback:" + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                            Log.d(TAG, "successCallback:" + message.toString());
                            m = message;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_r_message.setText(m.toString());
                                }
                            });

                            Gson gson = new Gson();
                            gson.toJson(message);

                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                            Log.d(TAG, "errorCallback: " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            }
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINUTE / 4, 0, this);
        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINUTE / 2, 0, this);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_LOCATION:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission Granted
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINUTE / 2, 0, this);
//                } else {
//                    // Permission Denied
//                    Toast.makeText(MainActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
//                            .show();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void on_btn_click(View view) {
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println("on_btn_click | channel: " + channel + " " + response.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                System.out.println("on_btn_click | channel: " + channel + " " + error.toString());
            }
        };
        pubnub.publish("my_channel", et_message.getText().toString(), callback);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null)
        {
            mLatitudeText.setText(String.valueOf(location.getLatitude()));
            mLongitudeText.setText(String.valueOf(location.getLongitude()));
            Log.e("Latitude :", "" + location.getLatitude());
            Log.e("Latitude :", "" + location.getLongitude());
            Callback callback = new Callback() {
                public void successCallback(String channel, Object response) {
                    System.out.println("onLocationChanged | channel: " + channel + " " + response.toString());
                }

                public void errorCallback(String channel, PubnubError error) {
                    System.out.println("onLocationChanged | channel: " + channel + " " + error.toString());
                }
            };

            JSONObject messageJSON = new JSONObject();
            try {
                messageJSON.put("ID", Build.MODEL);
                messageJSON.put("Lat", location.getLatitude());
                messageJSON.put("Lng", location.getLongitude());
                messageJSON.put("TimeToken", new Date().getTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pubnub.publish("my_channel", messageJSON, callback);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
