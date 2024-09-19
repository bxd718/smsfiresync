package mz.ac.bxd.project.sms_listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private MessageUtils messageUtils;
    private Utils utils;
    private FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    // Constructor to initialize MessageUtils and Utils
    public NetworkChangeReceiver(MessageUtils messageUtils, Utils utils) {
        this.messageUtils = messageUtils;
        this.utils = utils;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            Log.d("NetworkChangeReceiver", "Network is connected. Attempting to send pending messages.");
            crashlytics.log("Network is connected. Attempting to send pending messages.");
            messageUtils.sendPendingMessages(utils); // Send pending messages if connected
        } else {
            Log.d("NetworkChangeReceiver", "Network is not connected.");
            crashlytics.log("Network is not connected.");

        }
    }
}


