package ac.mz.projecto01;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ac.mz.projecto01.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding bidingMain;
    private EditText et_mail,et_pass;
    private Button login;
    private TextView criar;
    boolean verifica = false;

    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(MainActivity.this, DrawerNavigation.class);
            startActivity(intent);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();

        et_mail = findViewById(R.id.et_mail);
        et_pass = findViewById(R.id.et_password);
        login = findViewById(R.id.loginButton);
        criar = findViewById(R.id.signupText);

        // Quando for clicado o botão de login deve-se fazer a validação dos campos na base de dados
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = et_mail.getText().toString();
                String password = et_pass.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Login Sucesso.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, DrawerNavigation.class);
                                    startActivity(intent);
                                    finish(); // Finaliza a atividade atual para evitar que o usuário volte para a tela de login
                                } else {
                                    // Se o login falhar, exibe uma mensagem para o usuário.
                                    Toast.makeText(MainActivity.this, "Autenticação falhou.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        criar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Cadastros.class);
                startActivity(intent);
            }
        });
    }}
