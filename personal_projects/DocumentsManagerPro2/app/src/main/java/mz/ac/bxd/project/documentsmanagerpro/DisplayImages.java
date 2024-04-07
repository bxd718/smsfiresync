package mz.ac.bxd.project.documentsmanagerpro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mz.ac.bxd.project.documentsmanagerpro.databinding.ActivityDisplayImagesBinding;
import com.bumptech.glide.Glide;

public class DisplayImages extends AppCompatActivity {
    private ActivityDisplayImagesBinding biding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        biding = ActivityDisplayImagesBinding.inflate(getLayoutInflater());
        setContentView(biding.getRoot());

        String entryId = "";
        String associatedText = "";
        String imageUri1 = "";
        String imageUri2 = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            entryId = bundle.getString("itemId");
            associatedText = bundle.getString("associatedText");
            imageUri1 = bundle.getString("imageUri1");
            imageUri2 = bundle.getString("imageUri2");
        }
        biding.nomeDocumentoTextView.setText(biding.nomeDocumentoTextView.getText()+associatedText);

        // Use a biblioteca Glide para carregar as imagens do Firebase Storage
        Glide.with(this).load(imageUri1).into(biding.imagem1ImageView);
        Glide.with(this).load(imageUri2).into(biding.imagem2ImageView);

        biding.buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}