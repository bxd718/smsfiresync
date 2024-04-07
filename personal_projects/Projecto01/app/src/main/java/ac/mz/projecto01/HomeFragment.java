package ac.mz.projecto01;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ac.mz.projecto01.databinding.FragmentHomeBinding;
import ac.mz.projecto01.ui.model;

public class HomeFragment<activity> extends Fragment {
    private CustomAdapter adapter;

        private static final String[] documentsList = {
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

    private FragmentHomeBinding binding;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.recicleview.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<model> options =
                new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("documents").orderByChild("associatedText").endAt("\uf8ff"), model.class)
                        .build();

        adapter = new CustomAdapter(options, getContext());
        binding.recicleview.setAdapter(adapter);
        binding.recicleview.setVisibility(View.VISIBLE);

        binding.btSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String pesquisa = binding.search.getText().toString();
                String name = getDocumentName(pesquisa);

                if (name != null) {
                    searchCustomer(name);
                } else {
                    // Tratar o caso em que getDocumentName retorna null
                    Toast.makeText(getContext(), "Nome do documento não encontrado", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.fabReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use o método notifyDataSetChanged para recarregar os dados sem recriar a atividade
                adapter.notifyDataSetChanged();
            }
        });

        binding.fabAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new AddDocumentFragment()).commit();
            }
        });

        // Adiciona um menu de contexto à RecyclerView
        binding.recicleview.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            MenuInflater inflater1 = requireActivity().getMenuInflater();
            inflater1.inflate(R.menu.context_menu, menu);
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void searchCustomer(String nome) {
        if (!TextUtils.isEmpty(nome)) {
            if (nome != null) {
                binding.recicleview.setLayoutManager(new LinearLayoutManager(getContext()));
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("documents");

                FirebaseRecyclerOptions<model> options =
                        new FirebaseRecyclerOptions.Builder<model>()
                                .setQuery(reference.orderByChild("associatedText").startAt(nome).endAt("\uf8ff"), model.class)
                                .build();
                Toast.makeText(getContext(), options+"", Toast.LENGTH_SHORT).show();
                adapter = new CustomAdapter(options, getContext());
                binding.recicleview.setAdapter(adapter);
                binding.recicleview.setVisibility(View.VISIBLE);

            }
        } else {
            FirebaseRecyclerOptions<model> options =
                    new FirebaseRecyclerOptions.Builder<model>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("documents").orderByChild("associatedText").endAt("\uf8ff"), model.class)
                            .build();
            adapter = new CustomAdapter(options, getContext());
            binding.recicleview.setAdapter(adapter);
            binding.recicleview.setVisibility(View.VISIBLE);
        }
    }

    public static String getDocumentName(String initials) {
        String lowercaseInitials = initials.toLowerCase();

        for (String document : documentsList) {
            if (document.toLowerCase().startsWith(lowercaseInitials)) {
                return document;
            }
        }

        return "";
    }
}