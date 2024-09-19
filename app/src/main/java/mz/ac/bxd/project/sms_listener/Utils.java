package mz.ac.bxd.project.sms_listener;

import android.content.Context;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {

    private FirebaseDatabase database;
    private Context context;  // Keep context
    private FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    public Utils(FirebaseDatabase database) {
        this.database = database;
        this.context = context;
    }

    boolean criarRecarga(final String sender, final String mensagem) throws JSONException, IOException {

        // Create an executor service for background tasks
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ExtractedDataFromMessage extractedData = new ExtractedDataFromMessage(context);

                   // Recarga recarga;
                    //if ("eMola".equalsIgnoreCase(sender)) {
                        Recarga recarga = extractedData.extractDataFromMessageEmola(mensagem);
                    //} else if ("M-Pesa".equalsIgnoreCase(sender)) {
                         recarga = extractedData.extractDataFromMessageMpesa(mensagem);
                    //} else {
                     //   throw new IllegalArgumentException("A mensagem não está em nenhum dos formatos esperados (MPESA ou EMOLA).");
                    //}

                    // Call the method to save to Firebase
                    salvarRecargaNoFirebase(recarga);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    private void salvarRecargaNoFirebase(Recarga recarga) {
        DatabaseReference recargasRef = database.getReference(recarga.getIdTransacao().replace(".", "_")+"");
        recargasRef.setValue(recarga);
        Log.i("SUCESS", "Recarga salva com sucesso no FIREBASE");
        crashlytics.recordException(new Exception(("Recarga salva com sucesso no FIREBASE")));

    }
}
