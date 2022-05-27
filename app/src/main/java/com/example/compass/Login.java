package com.example.compass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity implements View.OnClickListener{
    private EditText txtUsername;
    private EditText txtPassword;
    private Button btnLogin;
    private Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogin) {
            final String username = txtUsername.getText().toString();
            final String password = txtPassword.getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe proporcionar usuario y contraseña.", Toast.LENGTH_SHORT).show();
                return;
            }
            SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
            if (!sqLiteHelper.checkUsernamePass(username,password)) {
                Toast.makeText(this, "Usuario o contraseña incorrectos.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (view == btnRegister) {
            final String username = txtUsername.getText().toString();
            final String password = txtPassword.getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe proporcionar usuario y contraseña.", Toast.LENGTH_SHORT).show();
                return;
            }
            SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
            String message = sqLiteHelper.insertUser(username, password) ? "Usuario creado." : "No se pudo añadir el usuario.";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}