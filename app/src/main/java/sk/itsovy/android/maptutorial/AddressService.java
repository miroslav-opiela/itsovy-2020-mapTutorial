package sk.itsovy.android.maptutorial;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class AddressService extends JobIntentService {

    private static final int JOB_ID = 4;

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AddressService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        for (int i = 0; i <= 40; i++) {
            Log.d("SERVICE", "hello " + i);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
