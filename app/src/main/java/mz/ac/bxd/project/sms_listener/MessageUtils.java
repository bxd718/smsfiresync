package mz.ac.bxd.project.sms_listener;

import android.content.Context;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MessageUtils {
    private static final String FILE_NAME = "pending_sms.txt";
    private Context context;
    private FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    public MessageUtils(Context context) {
        this.context = context;
    }

    public void saveMessageToFile(String sender, String message) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
            String smsData = "Sender: " + sender + " Message: " + message + "\n";
            fos.write(smsData.getBytes());
            fos.close();
            Log.d("MessageUtils", "Message saved locally: " + smsData);
            crashlytics.log("Message saved locally: " + smsData);
        } catch (Exception e) {
            Log.d("MessageUtils", "Error saving message to file: " + e);
            crashlytics.log("Error saving message to file: " + e);
        }
    }

    public void sendPendingMessages(final Utils utils) {
        if (!NetworkUtils.isConnectedToInternet(context)) {
            Log.d("MessageUtils", "No internet connection. Will not attempt to send pending messages.");
            crashlytics.log("No internet connection. Will not attempt to send pending messages.");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("MessageUtils", "Reading pending messages from file: " + FILE_NAME);
                    crashlytics.log("Reading pending messages from file: " + FILE_NAME);
                    FileInputStream fis = context.openFileInput(FILE_NAME);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        Log.d("MessageUtils", "Pending message found: " + line);
                        crashlytics.log("Pending message found: " + line);
                        String[] parts = line.split("Message: ");
                        String sender = parts[0].replace("Sender: ", "").trim();
                        String message = parts[1].trim();

                        Log.d("MessageUtils", "Attempting to send message: " + message);
                        crashlytics.log("Attempting to send message: " + message);
                        utils.criarRecarga(sender, message);
                    }

                    reader.close();
                    fis.close();
                    Log.d("MessageUtils", "All pending messages processed.");
                    crashlytics.log("All pending messages processed.");

                    // Clear file after processing all messages
                    clearPendingMessagesFile();

                } catch (Exception e) {
                    Log.d("MessageUtils", "Error reading or sending pending messages: " + e);
                    crashlytics.log("Error reading or sending pending messages: " + e);
                }
            }
        }).start();
    }


    private void clearPendingMessagesFile() {
        try {
            Log.d("MessageUtils", "Clearing pending messages file.");
            crashlytics.log("Clearing pending messages file.");
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write("".getBytes());  // Clear the file content by overwriting with an empty string
            fos.close();
            Log.d("MessageUtils", "Pending messages file cleared.");
            crashlytics.log("Pending messages file cleared.");
        } catch (Exception e) {
            Log.d("MessageUtils", "Error clearing pending messages file: " + e);
            crashlytics.log("Error clearing pending messages file: " + e);
        }
    }

}
