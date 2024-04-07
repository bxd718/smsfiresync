package ac.mz.projecto01;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Cadastros extends AppCompatActivity {

    EditText nome, numero, email, senha, senha1;
    Button cadastrar;
    RadioButton rb_masculino, rb_femenino;
    Context context;
     private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastros);

        //Localizar os componentes e inicializar no layout
        nome = findViewById(R.id.nome_cad);
        email = findViewById(R.id.email_cad);
        senha = findViewById(R.id.senha_cad);
        senha1 = findViewById(R.id.senha_cad02);
        cadastrar = findViewById(R.id.Cadastrar_btn);
        rb_masculino = findViewById(R.id.radio_masculino);
        rb_femenino = findViewById(R.id.radio_femenino);

        //Quando o botao cadastrar for clicado
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nome.getText().toString();
                String mail = email.getText().toString();
                String pass = senha.getText().toString();
                String pass1 = senha1.getText().toString();
                String sexo ="";


                //Aqui selecionamos e atribuimos a variavel sexo o sexo que a pessoa selecionou
                if(rb_masculino.isChecked())
                    sexo = rb_masculino.getText().toString();
                else
                     if(rb_femenino.isChecked())
                        sexo = rb_femenino.getText().toString();

                devidamente(name, mail, pass, pass1, sexo);

                //

            }

        });

    }

    //Procurar saber se todos os campos foram ou nao preenchidos devidademente
    public void devidamente(String name,String email,String pass,String password,String sexo){

        mAuth = FirebaseAuth.getInstance();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()
                || password.isEmpty() || sexo.isEmpty()) {

            Toast.makeText(Cadastros.this, "Preencha todos os Campos", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (pass.equals(password)) {

                 mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Cadastros.this, "Authentication created.",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Cadastros.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        }

    }


}