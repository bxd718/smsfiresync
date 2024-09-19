package mz.ac.bxd.project.sms_listener;

import android.content.Context;
import android.util.Log;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import org.json.JSONException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractedDataFromMessage {

    private String idRecarga;
    private String dataRecarga;
    private String valorRecarga;
    private String contacto;
    private Context context;
    static FleetTransactionService transactionService = new FleetTransactionService();
    private FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    // Constructor to initialize Context and Crashlytics
    public ExtractedDataFromMessage(Context context) {
        this.context = context;
        this.crashlytics = FirebaseCrashlytics.getInstance();  // Initialize Crashlytics
        crashlytics.log("ExtractedDataFromMessage initialized");
    }

    public Recarga extractDataFromMessageEmola(String mensagem) throws IOException, JSONException {
        try {
            crashlytics.log("Extracting data from Emola message");

            String padraoIdTransacao = "ID da (transacao|transaccao): ((PP|CI)\\d{6}\\.\\d{4}\\.[A-Za-z]\\d+)";
            Pattern patternIdTransacao = Pattern.compile(padraoIdTransacao);
            Matcher matcherIdTransacao = patternIdTransacao.matcher(mensagem);

            if (matcherIdTransacao.find()) {
                idRecarga = matcherIdTransacao.group(2);
            } else {
                idRecarga = "Emola";
            }

            String padraoValor = "\\b(\\d*([\\d\\,]*)\\.?\\d+)MT\\b";
            Pattern patternValor = Pattern.compile(padraoValor);
            Matcher matcherValor = patternValor.matcher(mensagem);

            if (matcherValor.find()) {
                valorRecarga = matcherValor.group(1).replace(",", "");
            }

            String padraoHoraData = "as (\\d{2}:\\d{2}:\\d{2})(?: de)? (\\d{2}/\\d{2}/\\d{4})";
            Pattern patternHoraData = Pattern.compile(padraoHoraData);
            Matcher matcherHoraData = patternHoraData.matcher(mensagem);

            if (matcherHoraData.find()) {
                String hora = matcherHoraData.group(1);
                String data = matcherHoraData.group(2);
                dataRecarga = hora + " " + data;
            }

            String padraoContacto = "conta\\s(\\d{9,})";
            Pattern patternContacto = Pattern.compile(padraoContacto);
            Matcher matcherContacto = patternContacto.matcher(mensagem);

            if (matcherContacto.find()) {
                contacto = matcherContacto.group(1);
            }

            if (transactionService.createFleetTransaction(contacto, (int) Double.parseDouble(valorRecarga), idRecarga)) {
                crashlytics.log("Recarga created successfully");
                return new Recarga(idRecarga, dataRecarga, (int) Double.parseDouble(valorRecarga), contacto, "", true);
            }

            crashlytics.log("Recarga creation failed");
            return new Recarga(idRecarga, dataRecarga, (int) Double.parseDouble(valorRecarga), "none", "", false);
        } catch (Exception e) {
            crashlytics.recordException(e);  // Capture non-fatal error
            Log.e("ExtractedData", "Error extracting data from Emola: " + e.getMessage());
            throw e;  // Re-throw the exception
        }
    }

    public static Recarga extractDataFromMessageMpesa(String mensagem) throws IOException, JSONException {
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.log("Extracting data from Mpesa message");

        String idRecarga = null;
        String dataRecarga = null;
        String valorRecarga = null;
        String contacto = null;

        try {
            // Extract transaction ID (Confirmed line)
            int posicaoConfirmed = mensagem.indexOf("Confirmed");

            if (posicaoConfirmed != -1) {
                idRecarga = mensagem.substring(0, posicaoConfirmed).trim();
            }

            // Extract value
            String padraoValor = "\\b(\\d*([\\d\\,]*)\\.?\\d+)MT\\b";
            Pattern patternValor = Pattern.compile(padraoValor);
            Matcher matcherValor = patternValor.matcher(mensagem);

            if (matcherValor.find()) {
                valorRecarga = matcherValor.group(1).replace(",", "");
            } else {
                idRecarga = "Mpesa";
                dataRecarga = "data";
                valorRecarga = "0";
            }

            // Extract date and time
            String padraoData = "(\\d{1,2}/\\d{1,2}/\\d{2,4} at \\d{1,2}:\\d{2} [APMapm]{2})";
            Pattern patternData = Pattern.compile(padraoData);
            Matcher matcherData = patternData.matcher(mensagem);

            if (matcherData.find()) {
                dataRecarga = matcherData.group(1);
            }

            // Extract phone number
            String padraoContacto = "258(\\d{9,})";
            Pattern patternContacto = Pattern.compile(padraoContacto);
            Matcher matcherContacto = patternContacto.matcher(mensagem);

            if (matcherContacto.find()) {
                contacto = matcherContacto.group(1);
            }

            if (transactionService.createFleetTransaction(contacto, (int) Double.parseDouble(valorRecarga), idRecarga)) {
                crashlytics.log("Recarga created successfully for Mpesa");
                return new Recarga(idRecarga, dataRecarga, (int) Double.parseDouble(valorRecarga), contacto, "", true);
            }

            crashlytics.log("Recarga creation failed for Mpesa");
            return new Recarga(idRecarga, dataRecarga, (int) Double.parseDouble(valorRecarga), "none", "", false);
        } catch (Exception e) {
            crashlytics.recordException(e);  // Capture any exceptions
            Log.e("ExtractedData", "Error extracting data from Mpesa: " + e.getMessage());
            throw e;
        }
    }
}
