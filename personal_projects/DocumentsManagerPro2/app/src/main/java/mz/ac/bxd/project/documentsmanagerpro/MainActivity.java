package mz.ac.bxd.project.documentsmanagerpro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import mz.ac.bxd.project.documentsmanagerpro.databinding.ActivityMainBinding;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;




public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding bidingMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bidingMain = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bidingMain.getRoot());


        bidingMain.buttonScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   // Inicia o processo de leitura do QR code
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });

    }

    // Método chamado quando a leitura do QR code é concluída
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Quando o usuário cancela a leitura
                bidingMain.textData.setText("Leitura cancelada");
            } else {
                // Quando a leitura é bem-sucedida
                String qrData = result.getContents();
                Quebrar(qrData);

            }
        }
    }

    public void Quebrar(String qrData){

        // Divida a string usando o caractere |
        String[] dados = qrData.split("\\|");

        // Verifique se há pelo menos 4 partes (chave, nomedoDocumento, link1, link2)
        if (dados.length >= 4) {
            String chave = dados[0];
            String nomeDocumento = dados[1];
            String linkImagem1 = dados[2];
            String linkImagem2 = dados[3];

            // Crie um Intent
            Intent intent = new Intent(this, DisplayImages.class);

            // Adicione os dados ao Intent
            intent.putExtra("itemId", chave);
            intent.putExtra("associatedText", nomeDocumento);
            intent.putExtra("imageUri1", linkImagem1);
            intent.putExtra("imageUri2", linkImagem2);

            // Inicie a QR_maker Activity
            startActivity(intent);


            bidingMain.textData.setText("Chave: " + chave+" \nNome do Documento: "+
                    nomeDocumento+" \nLink da Imagem 1: "+linkImagem1+" \nLink da Imagem 2: "+linkImagem2);
        } else {
            bidingMain.textData.setText("");
        }

    }
}