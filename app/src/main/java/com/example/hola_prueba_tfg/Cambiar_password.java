package com.example.hola_prueba_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cambiar_password extends AppCompatActivity {

    private EditText editTextIntroducirPwdActual, editTextNuevaPwd, editTextPwdNuevaConfirm;
    private Button btnReAutenticacion, btnCambiarPwd;
    private ProgressBar progressBar;
    private TextView textViewPwdAutentificacion;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth authProfile;
    private FirebaseUser Usuario_actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        // Establecer el título de la barra de acción
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cambiar Contraseña");
        }

        // Inicialización de vistas
        editTextIntroducirPwdActual = findViewById(R.id.editText_cambiar_pwd_actual);

        textViewPwdAutentificacion = findViewById(R.id.textView_cambiar_pwd_autentificacion);

        editTextNuevaPwd = findViewById(R.id.editText_pwd_nueva);
        editTextPwdNuevaConfirm = findViewById(R.id.editText_pwd_nueva_confirm);

        btnReAutenticacion = findViewById(R.id.button_autentificacion);
        btnCambiarPwd = findViewById(R.id.button_cambiar_pwd);
        progressBar = findViewById(R.id.progressBar);

        editTextNuevaPwd.setEnabled(false);
        editTextPwdNuevaConfirm.setEnabled(false);
        btnCambiarPwd.setEnabled(false);

        // Inicialización de FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        Usuario_actual = firebaseAuth.getCurrentUser();

        btnReAutenticacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //metodo de autentificacion
                reAutenticarse();
            }
        });

        btnCambiarPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // metodo de cambiar pwd
                cambiarPassword();
            }
        });
    }

    private void reAutenticarse() {

        String hashedPassword2 = editTextIntroducirPwdActual.getText().toString();
        String passwordActual= hashPassword(hashedPassword2);

        //String passwordActual = editTextIntroducirPwdActual.getText().toString();

        if (TextUtils.isEmpty(passwordActual)) {
            editTextIntroducirPwdActual.setError("Por favor, introduce tu contraseña actual");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Reautenticar al usuario
        Usuario_actual.reauthenticate(com.google.firebase.auth.EmailAuthProvider.getCredential(Usuario_actual.getEmail(), passwordActual)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Habilitar la edición de la nueva contraseña

                            editTextIntroducirPwdActual.setEnabled(false);
                            btnReAutenticacion.setEnabled(false);

                            editTextNuevaPwd.setEnabled(true);
                            editTextPwdNuevaConfirm.setEnabled(true);
                            btnCambiarPwd.setEnabled(true);

                            textViewPwdAutentificacion.setText("Autenticación Correcta: Ya puedes actualizar tu Contraseña");
                            // Cambiar el color de fondo del botón de actualizar email
                            btnCambiarPwd.setBackgroundTintList(ContextCompat.getColorStateList(Cambiar_password.this, R.color.rojo));

                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(Cambiar_password.this, "Autenticación exitosa. Introduce la nueva contraseña.", Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Cambiar_password.this, "Error de autenticación. Comprueba tu contraseña actual.", Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }

    private void cambiarPassword() {

        //String nuevaPassword = editTextNuevaPwd.getText().toString();
        //String confirmarPassword = editTextPwdNuevaConfirm.getText().toString();

        String hashedPassword = editTextNuevaPwd.getText().toString();
        String nuevaPassword = hashPassword(hashedPassword);

        String hashedPassword2 = editTextPwdNuevaConfirm.getText().toString();
        String confirmarPassword = hashPassword(hashedPassword2);

        if (TextUtils.isEmpty(nuevaPassword) || TextUtils.isEmpty(confirmarPassword)) {
            Toast.makeText(this, "Por favor, introduce la nueva contraseña en ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Actualizar la contraseña en Firebase Authentication
        Usuario_actual.updatePassword(nuevaPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Contraseña actualizada correctamente
                            Toast.makeText(Cambiar_password.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();

                            // Actualizar la contraseña en Firebase Realtime Database (opcional)
                            actualizarPasswordEnDatabase(nuevaPassword);

                            // Redirigir a la actividad principal o de perfil
                            Intent intent = new Intent(Cambiar_password.this, Perfil_Usuario.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Cambiar_password.this, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void actualizarPasswordEnDatabase(String nuevaPassword) {
        // Actualizar la contraseña en la base de datos (Ejemplo: usando Realtime Database)
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Usuarios_registrados").child(Usuario_actual.getUid());
        userRef.child("textPassword").setValue(nuevaPassword);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_perfil, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.refrescar){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);

        }else if (id == R.id.Actualizar_password) {
            Intent intent = new Intent(Cambiar_password.this, Cambiar_password.class);
            startActivity(intent);
        }else if (id == R.id.Actualizar_perfil) {
            Intent intent = new Intent(Cambiar_password.this, Actualizar_perfil.class);
            startActivity(intent);
        }else if (id == R.id.Actualizar_email) {
            Intent intent = new Intent(Cambiar_password.this, Actualizar_Email.class);
            startActivity(intent);
        }else if (id == R.id.Eliminar_perfil) {
            Intent intent = new Intent(Cambiar_password.this, Eliminar_perfil.class);
            startActivity(intent);
        }/*else if (id == R.id.Ajustes) {
            Toast.makeText(Perfil_Usuario.this, "menu_ajustes", Toast.LENGTH_LONG).show();

            //Intent intent = new Intent(Perfil_Usuario.this, Ajustes.class);
            //startActivity(intent);
        }*/else if (id == R.id.cerrar_sesion) {
            authProfile.signOut();
            Toast.makeText(Cambiar_password.this, "Sesion cerrada", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Cambiar_password.this, MainActivity_PantallaInicio.class);

            // Redirigir al usuario a la actividad principal
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(Cambiar_password.this, "Algo salió mal ", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }
}