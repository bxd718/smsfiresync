package mz.ac.bxd.project.my_expense_controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import mz.ac.bxd.project.my_expense_controller.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    DatabaseHelper db = new DatabaseHelper(this);
    private boolean exists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding.showPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(binding.password.getTransformationMethod() != null) {
                    //Show Password
                    ((ImageView)(view)).setImageResource(R.drawable.baseline_eye_24);
                    binding.password.setTransformationMethod(null);

                } else{
                    //Hide Password
                    ((ImageView)(view)).setImageResource(R.drawable.baseline_visibility_off_24);
                    binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }


            }
        });


        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = binding.mail.getText().toString();
                String password = binding.password.getText().toString();

                exists = db.getCustomer(mail, password);
                if (exists == true) {
                    Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    //Intent i = new Intent(MainActivity.this, AcessActivity.class);
                    //startActivity(i);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Activity_Regist.class));
                //finish();
            }
        });

    }

}