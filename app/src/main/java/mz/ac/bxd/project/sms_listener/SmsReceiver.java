package mz.ac.bxd.project.sms_listener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.FirebaseDatabase;

public class SmsReceiver extends BroadcastReceiver {

    public SmsReceiver() {}

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        Utils utils = new Utils(database);
        MessageUtils messageUtils = new MessageUtils(context);

        Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");

                if (pdusObj != null) {
                    StringBuilder fullMessage = new StringBuilder();

                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = getIncomingMessage(pdusObj[i], bundle);
                        String message = currentMessage.getMessageBody();
                        fullMessage.append(message);

                        if (i == pdusObj.length - 1) {
                            String sender = currentMessage.getDisplayOriginatingAddress();

                          //  if ("eMola".equalsIgnoreCase(sender) || "M-Pesa".equalsIgnoreCase(sender)) {
                                if (NetworkUtils.isConnectedToInternet(context)) {
                                    utils.criarRecarga(sender, fullMessage.toString());
                                } else {
                                    messageUtils.saveMessageToFile(sender, fullMessage.toString());
                                }
                           // }

                            Log.d("TAG", "Message: Sender: " + sender + " Message: " + fullMessage);
                            crashlytics.log("Message: Sender: " + sender + " Message: " + fullMessage);

                        }
                    }
                } else {
                    Log.d("TAG", "onReceive: SMS is null");
                    crashlytics.log("onReceive: SMS is null");

                }
            }
        } catch (Exception e) {
            Log.d("TAG", "Exception smsReceiver: " + e);
            crashlytics.log("Exception smsReceiver: " + e);


        }
    }

    private SmsMessage getIncomingMessage(Object object, Bundle bundle) {
        SmsMessage currentSMS;
        String format = bundle.getString("format");
        currentSMS = SmsMessage.createFromPdu((byte[]) object, format);
        return currentSMS;
    }
}
