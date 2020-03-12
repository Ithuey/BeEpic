package com.beepic.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.beepic.MainActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.support.constraint.Constraints.TAG;

public class LocationMethods extends AppCompatActivity {

    //constant
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    //vars
    private Context mContext;

    MainActivity mainActivity;

    //location
    private FusedLocationProviderClient fusedLocationClient;


    public void LocationMethods(Context context) {
        mContext = context;

    }






    public void getUserLocation() {

        if (checkPermissionArray(Permissions.PERMISSIONS)) {

        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) mContext, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {

                if(location != null){
                    //Toast.makeText(this, "Location " + location.toString(), Toast.LENGTH_SHORT ).show();

                    Log.d(TAG, "onSuccess: Location" + location);

                    Log.d(TAG, "onSuccess: Location Latitude " + location.getLatitude());


                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("user_location");

                    GeoFire geoFire = new GeoFire(mRef);
                    geoFire.setLocation(user_id, new GeoLocation(location.getLatitude(), location.getLongitude()));




                }
            }
        });

    }


    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions");

        ActivityCompat.requestPermissions(LocationMethods.this,
                permissions, VERIFY_PERMISSIONS_REQUEST);

    }


    /**
     * Check an array of permissions
     * @param permission
     * @return
     */
    public boolean checkPermissionArray(String[] permission) {
        Log.d(TAG, "checkPermissionArray: checking permission array");
        for (int i = 0; i < permission.length; i++) {
            String check = permission[i];
            if (!checkPermission(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checking for a single permission
     * @param permmission
     * @return
     */
    public boolean checkPermission(String permmission) {
        Log.d(TAG, "checkPermission: checking permmissions: " + permmission);

        int permissionsRequest = ActivityCompat.checkSelfPermission(mContext, permmission);

        if (permissionsRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: \n permission granted for " + permmission);
            return false;
        } else {
            Log.d(TAG, "checkPermission: \n Permission denied for " + permmission);
            return true;
        }
    }
}
