package mz.ac.bxd.project.sms_listener;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Utils {

    private FirebaseDatabase database;

    public Utils(FirebaseDatabase database) {
        this.database = database;
    }

    void criarRecarga(String sender, String mensagem) {

        if("eMola".equalsIgnoreCase(sender)) {
            salvarRecargaNoFirebase(ExtractedDataFromMessage.extractDataFromMessageEmola(mensagem));
        }else if("M-Pesa".equalsIgnoreCase(sender)) {
            salvarRecargaNoFirebase(ExtractedDataFromMessage.extractDataFromMessageMpesa(mensagem));
        }else {
            throw new IllegalArgumentException("A mensagem não está em nenhum dos formatos esperados (MPESA ou EMOLA).");
        }

    }

    private void salvarRecargaNoFirebase(Recarga recarga) {
        // Referência para o nó "Recargas" no banco de dados
        DatabaseReference recargasRef = database.getReference(recarga.getIdTransacao().replace(".", "_"));

        // Salvar a recarga no Firebase usando a chave gerada
        recargasRef.child(recarga.getIdTransacao().replace(".", "_")).setValue(recarga);
        Log.e("SUCESS", "Recarga salva com sucesso no FIREBASE");

    }

}
