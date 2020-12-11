package sk.itsovy.android.maptutorial;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressService extends JobIntentService {

    private static final int JOB_ID = 4;
    private static final int RESULT_CODE_OK = 3;
    public static final String INTENT_KEY_RECEIVER = "receiver";
    public static final String INTENT_KEY_LOCATION = "loc";
    public static final String BUNDLE_KEY_ADDRESS = "address";

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AddressService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (!Geocoder.isPresent()) {
            log("Geocoder not present.");
            return;
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        Location location = intent.getParcelableExtra(INTENT_KEY_LOCATION);
        if (location == null) {
            log("Location unknown.");
            return;
        }

        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            log("Get address exception " + e.getLocalizedMessage());
            return;
        }

        if (addressList == null || addressList.size() == 0) {
            log("No address.");
            return;
        }

        Address address = addressList.get(0);
        log(address.toString());

        //for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
        //   address.getAddressLine(i);
        //}

        log(address.getAddressLine(0));

        log("service job done.");

        ResultReceiver resultReceiver = intent.getParcelableExtra(INTENT_KEY_RECEIVER);
        if (resultReceiver == null) {
            log("no result receiver");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_ADDRESS, address.getAddressLine(0));
        resultReceiver.send(RESULT_CODE_OK, bundle);
    }

    private void log(String s) {
        Log.w("SERVICE", s);
    }
}
