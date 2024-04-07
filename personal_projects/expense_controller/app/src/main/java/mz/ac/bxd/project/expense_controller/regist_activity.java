package mz.ac.bxd.project.expense_controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import mz.ac.bxd.project.expense_controller.databinding.ActivityRegistBinding;

public class regist_activity extends AppCompatActivity {

    private ActivityRegistBinding binding;
    String nome, email, senha, senha1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Quando for clicado showPasswordButton
        binding.showPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPassMethod(view, binding.senhaCad);

            }
        });

        binding.showPassBtn02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPassMethod(view, binding.senhaCad02);

            }
        });


        //Quando o botao cadastrar for clicado
        binding.CadastrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Localizar os componentes e inicializar no layout
                nome = binding.nomeCad.getText().toString();
                email =  binding.emailCad.getText().toString();
                senha =  binding.senhaCad.getText().toString();
                senha1 = binding.senhaCad02.getText().toString();

                String sexo = getSex();
                devidamente(nome, email, senha, senha1, sexo);

            }
        });

        binding.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    //Leva os exo da pessoa
    public String getSex(){

        //Aqui selecionamos e atribuimos a variavel sexo o sexo que a pessoa selecionou
        if(binding.radioMasculino.isChecked())
            return binding.radioMasculino.getText().toString();
        else
        if(binding.radioFemenino.isChecked())
            return binding.radioFemenino.getText().toString();

        return "";
    }

    //Procurar saber se todos os campos foram ou nao preenchidos devidademente
    public void devidamente(String name,String mail,String pass,String pass1,String sexo){

        if (name.isEmpty() || mail.isEmpty() || pass.isEmpty()
                || pass1.isEmpty() || sexo.isEmpty()) {

            Toast.makeText(regist_activity.this, "Preencha todos os Campos", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (pass.equals(pass1)) {

                pessoa person = new pessoa(name, mail, sexo, pass);

                //Se tudo estiver certo adicionar a base de dados
                DatabaseHelper db = new DatabaseHelper(regist_activity.this);
                db.addUsers(name, mail, sexo, pass);

                Intent i = new Intent(regist_activity.this, MainActivity.class);
                startActivity(i);

                // terminar a activity
                finish();
            }
        }

    }

    public void showPassMethod(View view, EditText showPass){

        if(showPass.getTransformationMethod() != null) {
            //Show Password
            ((ImageView)(view)).setImageResource(R.drawable.baseline_eye_24);
            showPass.setTransformationMethod(null);

        } else{
            //Hide Password
            ((ImageView)(view)).setImageResource(R.drawable.baseline_visibility_off_24);
            showPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        }


    }

}