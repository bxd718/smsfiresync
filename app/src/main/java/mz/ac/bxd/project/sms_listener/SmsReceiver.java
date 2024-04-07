package mz.ac.bxd.project.sms_listener;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class SmsReceiver extends BroadcastReceiver {

    public SmsReceiver() {

    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        // Inicializar o Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Utils utils = new Utils(database);

        Bundle bundle = intent.getExtras();

        try {

            if(bundle != null){
                Object[] pdusObj = (Object[]) bundle.get("pdus");

                if (pdusObj != null) {
                    StringBuilder fullMessage = new StringBuilder();  // Para armazenar a mensagem completa

                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = getIncomingMessage(pdusObj[i], bundle);
                        String message = currentMessage.getMessageBody();
                        fullMessage.append(message);

                        // Verificar se é a última parte da mensagem
                        if (i == pdusObj.length - 1) {
                            // Processar a mensagem completa
                            String sender = currentMessage.getDisplayOriginatingAddress();
                            String completeMessage = "Sender: " + sender + " Message: " + fullMessage.toString();
                             // Verificar se está recebendo uma mensagem do Emola ou do Mpesa
                             if ("eMola".equalsIgnoreCase(sender) || "M-Pesa".equalsIgnoreCase(sender)) {
                                utils.criarRecarga(sender, fullMessage.toString());
                             }
                            Log.d("TAG", "Message : " + completeMessage);
                        }
                    }
                } else {
                    Log.d("TAG", "onReceive: SMS is null");
                }

            }

        }catch (Exception e){
            Log.d("TAG", "Exception smsReceiver   : " + e);
        }

    }

    private SmsMessage getIncomingMessage(Object object, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) object, format);
        } else {
            // For versions prior to M, it's safe to use the deprecated method.
            currentSMS = SmsMessage.createFromPdu((byte[]) object);
        }

        return currentSMS;

    }

}
