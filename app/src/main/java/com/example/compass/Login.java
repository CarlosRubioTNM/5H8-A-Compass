package com.example.compass;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

public class Login extends AppCompatActivity implements View.OnClickListener{
    private EditText txtUsername;
    private EditText txtPassword;
    private Button btnLogin;
    private Button btnRegister;
    private EditText txtResult;
    ProgressDialog progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        txtResult = findViewById(R.id.txtResult);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    /*@Override
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
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        new JSONTask().execute("https://jsonplaceholder.typicode.com/todos/1");
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
            UserDatabase userDatabase = UserDatabase.getUserDatabase(getApplicationContext());
            final UserDAO userDAO = userDatabase.userDao();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserEntity userEntity = userDAO.login(username,password);
                    if (userEntity == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
            }).start();
        } else if (view == btnRegister) {
            UserEntity userEntity = new UserEntity();
            final String username = txtUsername.getText().toString();
            final String password = txtPassword.getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe proporcionar usuario y contraseña.", Toast.LENGTH_SHORT).show();
                return;
            }
            userEntity.setUsername(username);
            userEntity.setPassword(password);
            UserDatabase userDatabase = UserDatabase.getUserDatabase(getApplicationContext());
            final UserDAO userDAO = userDatabase.userDao();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    userDAO.registerUser(userEntity);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Usuario registrado.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
    }

    private class JSONTask extends AsyncTask<String, Void,String> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(Login.this);
            progressBar.setMessage("Cargando...");
            progressBar.setCancelable(false);
            progressBar.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection conn = null;
            BufferedReader bf = null;
            try {
                URL url = new URL(strings[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                InputStream str = conn.getInputStream();
                bf = new BufferedReader(new InputStreamReader(str));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = bf.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch(MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                try {
                    if (bf != null) {
                        bf.close();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
            //SOLAMENTE MOSTRAR LA CADENA DE TEXTO
            //txtResult.setText(result);

            //OBTENER EL OBJETO Y MOSTRAR EL TITULO
            try {
                JSONObject todo = new JSONObject(result);
                txtResult.setText(todo.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}