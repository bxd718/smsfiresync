package mz.ac.bxd.project.sms_listener;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {

    private final FirebaseDatabase database;
    private final FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    public Utils(FirebaseDatabase database) {
        this.database = database;
    }

    void criarRecarga(final String sender, final String mensagem) {

        // Create an executor service for background tasks
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                ExtractedDataFromMessage extractedData = new ExtractedDataFromMessage();

                Recarga recarga;

               // Recarga recarga;
               if ("eMola".equalsIgnoreCase(sender) && mensagem.contains("Recebeste")) {
                     recarga = extractedData.extractDataFromMessageEmola(mensagem);
                    // Call the method to save to Firebase
                   salvarRecargaNoFirebase(recarga);
                } else if ("M-Pesa".equalsIgnoreCase(sender) && mensagem.contains("received") && !mensagem.contains("SIMO")) {
                     recarga = extractedData.extractDataFromMessageMpesa(mensagem);
                    // Call the method to save to Firebase
                    salvarRecargaNoFirebase(recarga);

               }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void salvarRecargaNoFirebase(Recarga recarga) {
        DatabaseReference recargasRef = database.getReference(recarga.getIdTransacao().replace(".", "_"));
        recargasRef.setValue(recarga);
        Log.i("SUCESS", "Recarga salva com sucesso no FIREBASE");
        crashlytics.recordException(new Exception(("Recarga salva com sucesso no FIREBASE")));

    }
}
