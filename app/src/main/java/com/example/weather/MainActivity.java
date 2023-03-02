package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.weather.fragments.Active;
import com.example.weather.fragments.Help;
import com.example.weather.fragments.Home;
import com.example.weather.location.GpsTracker;
import com.example.weather.network.ConnectivityReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener{

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    private GpsTracker gpsTracker;
    ConnectivityReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.active);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        getLocation();
        getSupportActionBar().setTitle("Weather");
    }

    public void getLocation(){
        gpsTracker = new GpsTracker(MainActivity.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.home:
                fragment = new Home();
                break;

            case R.id.active:
                fragment = new Active();
                break;

            case R.id.help:
                fragment = new Help();
                break;

        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Internet Check
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        receiver = new ConnectivityReceiver();
        registerReceiver(receiver, filter);

        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.getInstance().setConnectivityListener(this);
        unregisterReceiver(receiver);
    }
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected){

        }else{
            AlertDialog.Builder builder_internet = new AlertDialog.Builder(this);
            builder_internet.setTitle("No Internet");

            // set the custom layout
            View view_email = getLayoutInflater().inflate(R.layout.no_internet_layout, null);
            builder_internet.setView(view_email);
            builder_internet.setCancelable(false);

            // add a button
            builder_internet.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            final AlertDialog dialog_email = builder_internet.create();
            dialog_email.show();
            dialog_email.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    int i = 0;
                    boolean isConnected_close = ConnectivityReceiver.isConnected();
                    if (isConnected_close){
                        internetRes();
                        i = 1;
                    }
                    if (i == 1) {
                        wantToCloseDialog = true;
                    }
                    if(wantToCloseDialog)
                        dialog_email.dismiss();
                }
            });
        }
    }
    private void internetRes(){
        finish();
        startActivity(getIntent());
        Toast.makeText(getApplicationContext(), "Connected to Internet", Toast.LENGTH_LONG).show();
    }
}