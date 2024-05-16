package com.example.hola_prueba_tfg;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity_IniciarSesion extends AppCompatActivity {


    private EditText editText_Text_Email_Address2;
    private TextInputLayout text_Input_Layout;

    private FirebaseAuth autentificacion_cuenta;



    private static final String TAG="MainActivity_IniciarSesion";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_iniciar_sesion);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity_IniciarSesion.this,R.color.rojo));

        TextView registrate=findViewById(R.id.textView_registrate);
        registrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí es donde se inicia el nuevo Activity
                Intent intent = new Intent(MainActivity_IniciarSesion.this, MainActivity_Pantalla_registrarse.class);
                startActivity(intent);
            }
        });

        editText_Text_Email_Address2 = findViewById(R.id.editTextTextEmailAddress2);
        text_Input_Layout = findViewById(R.id.textInputLayout);

        autentificacion_cuenta = FirebaseAuth.getInstance();

        //ovidado de contraseña
        TextView  OlvidarPassword = findViewById(R.id.olvidar_password);
        OlvidarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity_IniciarSesion.this, "Ya puedes restablecer tu contraseña", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity_IniciarSesion.this, Olvidar_password.class));
            }
        });

        Button ingresar = findViewById(R.id.button2);
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text_email = editText_Text_Email_Address2.getText().toString();
                String hashedPassword2 = text_Input_Layout.getEditText().getText().toString();
                String text_password = hashPassword(hashedPassword2);

                if (TextUtils.isEmpty(text_email)){
                    Toast.makeText(MainActivity_IniciarSesion.this, "Porfavor introduzca tu Email", Toast.LENGTH_LONG).show();
                    editText_Text_Email_Address2.setError("Se requiere un Email");
                    editText_Text_Email_Address2.requestFocus();
                } // Comprobamos si el email introudcido por el usuario es correcto o no
                else if (!Patterns.EMAIL_ADDRESS.matcher(text_email).matches()) {
                    Toast.makeText(MainActivity_IniciarSesion.this, "Por favor Vuelva a Introducir el Email", Toast.LENGTH_LONG).show();
                    editText_Text_Email_Address2.setError("Se requiere introducir el Email Valido");
                    editText_Text_Email_Address2.requestFocus();
                } else if (TextUtils.isEmpty(text_password)) {
                    Toast.makeText(MainActivity_IniciarSesion.this, "Porfavor introduzca una Contraseña", Toast.LENGTH_LONG).show();
                    text_Input_Layout.setError("Se requiere una Contraseña");
                    text_Input_Layout.requestFocus();
                } else {
                    login_user(text_email, text_password);
                }


            }

        });

    }

    private void login_user(String Email, String Password) {
        autentificacion_cuenta.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(MainActivity_IniciarSesion.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    /*
                    // Aquí es donde se inicia el nuevo Activity
                    Intent intent = new Intent(MainActivity_IniciarSesion.this, MainActivity.class);
                    startActivity(intent);
                    */


                    // EL PROGRAMA INICIA SESION AUNQUE EL USER NO HAYA VERFICADO LA CUENTA....
                    FirebaseUser firebaseUser = autentificacion_cuenta.getCurrentUser();

                    //Toast.makeText(MainActivity_IniciarSesion.this, "Incio sesión Correcto", Toast.LENGTH_LONG).show();

                    // Aquí es donde se inicia el nuevo Activity
                    // Intent intent = new Intent(MainActivity_IniciarSesion.this, MainActivity.class);
                    // startActivity(intent);




                    if (firebaseUser.isEmailVerified()){
                        Toast.makeText(MainActivity_IniciarSesion.this, "Ya has iniciado sesión", Toast.LENGTH_LONG).show();
                        // Aquí es donde se inicia el nuevo Activity
                        Intent intent = new Intent(MainActivity_IniciarSesion.this, MainActivity.class);
                        startActivity(intent);

                    }else {
                        firebaseUser.sendEmailVerification();
                        autentificacion_cuenta.signOut();
                        mostrar_dialogo_alerta();
                    }


                }else{

                    //exepciones que debe cumplir el usuario ala hora de registrarse
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        editText_Text_Email_Address2.setError("El usuario no existe o ya no es válido. Por favor regístrese nuevamente ");
                        editText_Text_Email_Address2.requestFocus();

                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        if (e.getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                            editText_Text_Email_Address2.setError("El Email no es válido. Por favor vuelva a introducir un Email válido");
                            editText_Text_Email_Address2.requestFocus();
                        } else if (e.getErrorCode().equals("ERROR_WRONG_PASSWORD")) {
                            text_Input_Layout.setError("La contraseña es incorrecta");
                            text_Input_Layout.requestFocus();
                        } else {
                            // Manejar otras excepciones de credenciales inválidas aquí
                            Toast.makeText(MainActivity_IniciarSesion.this, "Credenciales inválidas. Verifique su correo electrónico y contraseña.", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(MainActivity_IniciarSesion.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }

                    Toast.makeText(MainActivity_IniciarSesion.this, "Algo salió mal", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

/*
    @Override
    protected void onStart() {
        super.onStart();
        if (autentificacion_cuenta.getCurrentUser() != null ){
            Toast.makeText(MainActivity_IniciarSesion.this, "Ya ha iniciado sesión anteriormente", Toast.LENGTH_LONG).show();

            startActivity(new Intent(MainActivity_IniciarSesion.this, Perfil_Usuario.class));
            finish();

        }else {

            Toast.makeText(MainActivity_IniciarSesion.this, " Puedes iniciar sesión ahora!", Toast.LENGTH_LONG).show();

        }
    }


 */






    private void mostrar_dialogo_alerta() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_IniciarSesion.this);
        builder.setTitle("Email no verificado");
        builder.setMessage("Porfavor verifique el correo electronico ahora mismo. No puedes iniciar sesión sin verificación por correo electrónico");

        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //crear el dialogo
        AlertDialog alertDialog = builder.create();

        //mostrar el dialogo
        alertDialog.show();

        // Obtener el botón positivo del diálogo y cambiar su color de texto a rojo
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(Color.RED);
        }

    }

    public static String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Add password bytes to digest
            md.update(password.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest();


            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


}


