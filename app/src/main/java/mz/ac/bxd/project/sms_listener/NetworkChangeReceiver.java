package mz.ac.bxd.project.sms_listener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.IOException;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private MessageUtils messageUtils;
    private Utils utils;
    private final FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    // Construtor padrão exigido pelo Android
    public NetworkChangeReceiver() {
        super();
    }

    // Método setter para inicializar as dependências
    public void setDependencies(MessageUtils messageUtils, Utils utils) {
        this.messageUtils = messageUtils;
        this.utils = utils;
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (messageUtils == null || utils == null) {
            Log.e("NetworkChangeReceiver", "Dependencies not initialized");
            return; // Certifique-se de que as dependências estão configuradas
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            Log.d("NetworkChangeReceiver", "Network is connected. Attempting to send pending messages.");
            crashlytics.log("Network is connected. Attempting to send pending messages.");
            try {
                messageUtils.sendPendingMessages(utils); // Enviar mensagens pendentes se conectado
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Log.d("NetworkChangeReceiver", "Network is not connected.");
            crashlytics.log("Network is not connected.");
        }
    }
}
