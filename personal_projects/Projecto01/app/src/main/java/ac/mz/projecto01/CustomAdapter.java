package ac.mz.projecto01;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.AccessControlContext;

import ac.mz.projecto01.ui.model;

public class CustomAdapter extends FirebaseRecyclerAdapter<model,CustomAdapter.myviewholder>
{
    private Context context;
    public CustomAdapter(@NonNull FirebaseRecyclerOptions<model> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomAdapter.myviewholder holder, int position, @NonNull model model) {
        holder.name.setText(model.getAssociatedText());
        Glide.with(holder.img1.getContext()).load(model.getImageUri1()).into(holder.img1);
        Glide.with(holder.img2.getContext()).load(model.getImageUri2()).into(holder.img2);

        holder.itemView.setOnClickListener(view -> {
            // Exiba o menu de contexto
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.context_menu, popupMenu.getMenu());

                    String itemIdToEdit = getRef(position).getKey();

                    // Obtenha os dados do modelo
                    String associatedText = model.getAssociatedText();
                    String expirationDate = model.getExpirationDate();
                    String imageUri1 = model.getImageUri1();
                    String imageUri2 = model.getImageUri2();

                    // Abra o intent do Qr code
                        // Crie um Intent
                        Intent intent = new Intent(context, QR_maker.class);

                        // Adicione os dados ao Intent
                            intent.putExtra("itemId", itemIdToEdit);
                                    intent.putExtra("associatedText", associatedText);
                                    intent.putExtra("expirationDate", expirationDate);
                                    intent.putExtra("imageUri1", imageUri1);
                                    intent.putExtra("imageUri2", imageUri2);

                        // Inicie a QR_maker Activity
                                    context.startActivity(intent);

        });


        // Adicione um ouvinte de clique longo ao item
        holder.itemView.setOnLongClickListener(view -> {
            // Exiba o menu de contexto
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.context_menu, popupMenu.getMenu());

            // Implemente a lógica de clique no item do menu de contexto
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.edit_option) {
                    //Logica para editar

                        // Obtenha o ID do item atual

                        String itemIdToEdit = getRef(position).getKey();

                        // Obtenha os dados do modelo
                        String associatedText = model.getAssociatedText();
                        String imageUri1 = model.getImageUri1();
                        String imageUri2 = model.getImageUri2();
                        String expirationDate = model.getExpirationDate();

                        // Crie um Bundle para enviar dados ao fragmento de edição
                        Bundle bundle = new Bundle();
                        bundle.putString("itemId", itemIdToEdit);
                        bundle.putString("associatedText", associatedText);
                        bundle.putString("expirationDate", expirationDate);
                        bundle.putString("imageUri1", imageUri1);
                        bundle.putString("imageUri2", imageUri2);

                        // Crie uma instância do fragmento de edição e passe o Bundle
                        AddDocumentFragment editFragment = new AddDocumentFragment();
                        editFragment.setArguments(bundle);

                        // Abra o fragmento de edição
                        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, editFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();


                    return true;
                } else if (itemId == R.id.delete_option) {
                        // Obtenha o ID do item atual
                             String itemIdToDelete = getRef(position).getKey();

                        // Chame um método para deletar o item do banco de dados
                        deleteItemFromDatabase(itemIdToDelete);

                    return true;
                }
                return false;
            });

            popupMenu.show();

            return true;
        });

    }

    void deleteItemFromDatabase(String itemId) {
        DatabaseReference documentsReference = FirebaseDatabase.getInstance().getReference().child("documents");

        // Use o ID para remover o item do banco de dados
        documentsReference.child(itemId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Item deletado com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Sem mensagem de erro";
                Toast.makeText(context, "Falha ao deletar item: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }



    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        ImageView img1, img2;
        TextView name;
        public myviewholder(@NonNull View itemView)
        {
            super(itemView);
            img1=(ImageView)itemView.findViewById(R.id.imageView1);
            img2=(ImageView)itemView.findViewById(R.id.imageView2);
            name=(TextView)itemView.findViewById(R.id.text_docName);
        }
    }
}
