package ac.mz.projecto01;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import ac.mz.projecto01.databinding.FragmentAddDocumentBinding;
import ac.mz.projecto01.ui.model;

import static android.app.Activity.RESULT_OK;

public class AddDocumentFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private FragmentAddDocumentBinding binding;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final int REQUEST_CAMERA_PERMISSION = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int SELECT_PICTURE = 200;
    private String expirationDate;

    String[] documentsList = {
            "Bilhete de Identidade (BI)",
            "Cartão de Estudante",
            "Cartão de Contribuinte",
            "Passaporte",
            "Título de Residência",
            "Cartão de Eleitor",
            "Carteira de Condução",
            "Cartão Nacional de Saúde",
            "Cartão Jovem",
            "Carteira Profissional"
    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddDocumentBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        binding.spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> aa = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, documentsList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(aa);

        LinearLayout imageContainer = binding.imageContainer;

        Bundle bundle = getArguments();
        if (bundle != null) {
            String itemId = bundle.getString("itemId");
            String associatedText = bundle.getString("associatedText");
            String expirationDateString = bundle.getString("expirationDate");
            String imageUri1 = bundle.getString("imageUri1");
            String imageUri2 = bundle.getString("imageUri2");

            Toast.makeText(getContext(), expirationDateString, Toast.LENGTH_SHORT).show();

            // Configurar o texto associado ao Spinner
            binding.spinner.setSelection(getIndex(binding.spinner, associatedText));

          
            // Definir a data de vencimento no EditText
            if (expirationDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                binding.dataVencimentoEditText.setText(dateFormat.format(expirationDate));
            }

            // Adicionar as imagens ao contêiner de imagens
            addImageToContainer(Uri.parse(imageUri1));
            addImageToContainer(Uri.parse(imageUri2));
        }

        binding.dataVencimentoEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        binding.cameraBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();

            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });

        binding.galeryBtn.setOnClickListener(v -> imageChooser());

        binding.carregarButton.setOnClickListener(v -> {
            String documentType = binding.spinner.getSelectedItem().toString();
            List<Uri> imageUris = new ArrayList<>();

            for (int i = 0; i < binding.imageContainer.getChildCount(); i++) {
                View childView = binding.imageContainer.getChildAt(i);

                if (childView instanceof ImageView) {
                    ImageView imageView = (ImageView) childView;
                    Object tag = imageView.getTag();
                    if (tag != null && tag instanceof String && !((String) tag).isEmpty()) {
                        String imageUriString = (String) tag;
                        Uri imageUri = Uri.parse(imageUriString);
                        imageUris.add(imageUri);
                    }
                }
            }

            uploadImagesWithText(imageUris, documentType, expirationDate);
            if (bundle != null) {
                String itemId = bundle.getString("itemId");
                FirebaseRecyclerOptions<model> options =
                        new FirebaseRecyclerOptions.Builder<model>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("documents"), model.class)
                                .build();

                CustomAdapter adapter = new CustomAdapter(options, getContext());
                adapter.deleteItemFromDatabase(itemId);

            }
        });

        return rootView;
    }




    public void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Converte a data selection para uma string e define no EditText
                        expirationDate = day + "/" + (month + 1) + "/" + year;
                        binding.dataVencimentoEditText.setText(expirationDate);
                    }
                },
                year, month, day);

        // Exibe o seletor de data
        datePickerDialog.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
           }
    }

    // Método para encontrar o índice de uma String no Spinner
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }


    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Uri imageUri = saveImageToFile(imageBitmap);
                addImageToContainer(imageUri);
            } else if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    addImageToContainer(selectedImageUri);
                }
            }
        }
    }

    private void addImageToContainer(Uri imageUri) {
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageURI(imageUri);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200,
                1
        );

        // Use Glide to load the image from the URL into the ImageView
        Glide.with(requireContext())
                .load(imageUri)
                .into(imageView);

        imageView.setLayoutParams(layoutParams);
        imageView.setTag(imageUri.toString());
        imageView.setOnClickListener(v -> showOptionsDialog(imageUri));
        binding.imageContainer.addView(imageView);
        updateImageContainer();
    }

    private void showOptionsDialog(final Uri selectedImageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Deseja remover esta imagem?")
                .setPositiveButton("Cancelar", null)
                .setNegativeButton("Confirmar", (dialog, which) -> removeImageViewByUri(selectedImageUri))
                .show();
    }

    private Uri saveImageToFile(Bitmap imageBitmap) {
        File imagesFolder = new File(requireContext().getFilesDir(), "images");
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }

        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(imagesFolder, fileName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(imageFile);
    }

    private void removeImageViewByUri(Uri selectedImageUri) {
        for (int i = 0; i < binding.imageContainer.getChildCount(); i++) {
            View childView = binding.imageContainer.getChildAt(i);

            if (childView instanceof ImageView) {
                ImageView imageView = (ImageView) childView;
                Object tag = imageView.getTag();

                if (tag != null && tag instanceof String && !((String) tag).isEmpty()) {
                    String uriString = (String) tag;
                    Uri uri = Uri.parse(uriString);

                    if (uri.equals(selectedImageUri)) {
                        binding.imageContainer.removeView(imageView);
                        updateImageTags();
                        updateImageContainer();
                        break;
                    }
                }
            }
        }
    }

    private void updateImageTags() {
        for (int i = 0; i < binding.imageContainer.getChildCount(); i++) {
            View childView = binding.imageContainer.getChildAt(i);

            if (childView instanceof ImageView) {
                ImageView imageView = (ImageView) childView;
                Object tag = imageView.getTag();

                if (tag != null && tag instanceof String && !((String) tag).isEmpty()) {
                    // Retrieve the URI string and set it as the tag again
                    String uriString = (String) tag;
                    imageView.setTag(uriString);
                }
            }
        }
    }


    private void updateImageContainer() {
        for (int i = 0; i < binding.imageContainer.getChildCount(); i++) {
            View childView = binding.imageContainer.getChildAt(i);

            if (childView instanceof ImageView) {
                ImageView imageView = (ImageView) childView;
                Object tag = imageView.getTag();

                if (tag != null && tag instanceof String && !((String) tag).isEmpty()) {
                    String uriString = (String) tag;
                    Uri uri = Uri.parse(uriString);
                    Glide.with(requireContext())
                            .load(uri)
                            .into(imageView);
                }
            }
        }
    }


    private void uploadImagesWithText(List<Uri> imageUris, String associatedText, String expirationDate) {
        DatabaseReference documentsReference = databaseReference.child("documents");

        // Crie um identificador único para o conjunto de dados
        String entryKey = documentsReference.push().getKey();

        // Crie um objeto Model
        model entryModel = new model();
        entryModel.setEntryId(entryKey);
        entryModel.setAssociatedText(associatedText);
        entryModel.setExpirationDate(expirationDate);

        // Faça upload e adicione as URLs das imagens ao Model
        for (int i = 0; i < imageUris.size(); i++) {
            StorageReference imageRef = storageReference.child("images/" + UUID.randomUUID().toString());
            UploadTask uploadTask = imageRef.putFile(imageUris.get(i));

            final int finalI = i;
            uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Adicione a URL da imagem correspondente ao Model
                if (finalI == 0) {
                    entryModel.setImageUri1(uri.toString());
                } else if (finalI == 1) {
                    entryModel.setImageUri2(uri.toString());
                }

                // Se ambas as imagens foram carregadas, adicione o Model ao Realtime Database
                if (entryModel.getImageUri1() != null && entryModel.getImageUri2() != null) {
                    documentsReference.child(entryKey).setValue(entryModel);
                    Toast.makeText(getContext(), "Todas as imagens foram carregadas!", Toast.LENGTH_SHORT).show();
                    getFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment<>()).commit();
                }
            })).addOnFailureListener(e -> Toast.makeText(getContext(), "Falha no upload da imagem: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Lógica a ser executada quando um item é selecionado no Spinner
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Lógica a ser executada quando nada é selecionado no Spinner
    }
}
