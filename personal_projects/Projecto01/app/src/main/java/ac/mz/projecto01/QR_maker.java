package ac.mz.projecto01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import ac.mz.projecto01.databinding.ActivityQrMakerBinding;
import ac.mz.projecto01.databinding.FragmentHomeBinding;

public class QR_maker extends AppCompatActivity {

    private ActivityQrMakerBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrMakerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        // Concatene os valores em uma única string
        String dataToEncode = entryId + "|" + associatedText + "|" + imageUri1 + "|" + imageUri2;

        Bitmap qrCode = generateQRCode(dataToEncode);

        if (qrCode != null) {
            // Exiba o QR code na sua ImageView
            binding.imageViewQR.setImageBitmap(qrCode);

            binding.qrName.setText(binding.qrName.getText()+associatedText);
        }

        binding.btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QR_maker.this, DrawerNavigation.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public static Bitmap generateQRCode(String data) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

}