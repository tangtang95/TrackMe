package com.trackme.trackmeapplication.home;

import android.Manifest;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.trackme.trackmeapplication.R;
import com.trackme.trackmeapplication.localdb.database.AppDatabase;
import com.trackme.trackmeapplication.localdb.entity.PositionData;

/**
 * User location Listener. This class saves the user position when the position change.
 */
public class UserLocationListener implements LocationListener {

    private Location currentBestLocation = null;
    private Context context;

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static final int INITIAL_REQUEST = 1000;

    /**
     * Constructor.
     * Get the last position known.
     *
     * @param context the current activity context.
     */
    public UserLocationListener(Context context) {
        this.context = context;
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, location -> {
                if (location != null) {
                    currentBestLocation = location;
                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentBestLocation == null || isBetterLocation(location, currentBestLocation) ) {
            currentBestLocation = location;
            Runnable addPositionData = () -> {
                AppDatabase appDatabase = Room.databaseBuilder(context,
                        AppDatabase.class, context.getString(R.string.persistent_database_name)).build();
                PositionData positionData = new PositionData();
                positionData.setLatitude(currentBestLocation.getLatitude());
                positionData.setLongitude(currentBestLocation.getLongitude());
                appDatabase.getPositionDataDao().insert(positionData);
            };
            addPositionData.run();
        }
    }

    /** Determines whether one location reading is better than the current location fix
     * @param location  The new location that you want to evaluate
     * @param currentBestLocation  The current location fix, to which you want to compare the new one.
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /**
     * Getter method.
     *
     * @return the current location of the user or the last known.
     */
    public Location getCurrentBestLocation() {
        return currentBestLocation;
    }
}
