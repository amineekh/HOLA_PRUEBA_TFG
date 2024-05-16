package com.example.hola_prueba_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Olvidar_password extends AppCompatActivity {

    private Button buttonCambiarPassword;
    private EditText editTexteEmailCambiarPassword;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile; // Objeto para la autenticación de Firebase
    private final String TAG = "Actualizar_password"; // Etiqueta para mensajes de registro (log)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvidar_password);

        // Establecer el título de la barra de acción
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Restablecer Contraseña");
        }

        // Vinculación de vistas con variables
        buttonCambiarPassword = findViewById(R.id.button_cambiar_password);
        editTexteEmailCambiarPassword = findViewById(R.id.editText_email_cambiar_password);
        progressBar = findViewById(R.id.progressBar);

        // Configurar el botón para cambiar la contraseña
        buttonCambiarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el correo electrónico del campo de texto
                String email = editTexteEmailCambiarPassword.getText().toString();
                if (TextUtils.isEmpty(email)){
                    // Validar si el campo está vacío
                    Toast.makeText(Olvidar_password.this, "Porfavor, introduzca su Email", Toast.LENGTH_LONG).show();
                    editTexteEmailCambiarPassword.setError("Porfavor, introduzca su Email");
                    editTexteEmailCambiarPassword.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    // Validar si el correo electrónico tiene un formato válido
                    Toast.makeText(Olvidar_password.this, "Por favor, introduzca un correo electrónico válido", Toast.LENGTH_SHORT).show();
                    editTexteEmailCambiarPassword.setError("Por favor, introduzca un correo electrónico válido");
                    editTexteEmailCambiarPassword.requestFocus();
                } else {
                    // Si el correo es válido, mostrar la barra de progreso y llamar a la función para cambiar la contraseña
                    progressBar.setVisibility(View.VISIBLE);
                    cambiar_contraseña(email);
                }
            }
        });

    }

    // Método para cambiar la contraseña
    private void cambiar_contraseña(String email) {
        authProfile = FirebaseAuth.getInstance(); // Obtener la instancia de FirebaseAuth

        // Enviar el correo para restablecer la contraseña
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Si se envía con éxito, mostrar mensaje y redirigir a la actividad de inicio de sesión
                    Toast.makeText(Olvidar_password.this, "Por favor revise su bandeja de entrada del correo electrónico para ver el enlace para restablecer la contraseña.", Toast.LENGTH_LONG).show();


                    // Crear un nuevo intent para iniciar la actividad MainActivity_IniciarSesion
                    Intent intent = new Intent(Olvidar_password.this, MainActivity_IniciarSesion.class);
                    // Configurar flags en el intent para controlar el comportamiento de la nueva actividad
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    // Iniciar la actividad MainActivity_IniciarSesion usando el intent creado
                    startActivity(intent);
                    // Finalizar la actividad actual (Actualizar_password) para removerla de la pila de actividades
                    finish();

                } else {
                    // Si ocurre un error, gestionar las excepciones
                    try {
                        throw task.getException(); // Obtener la excepción

                    } catch (FirebaseAuthInvalidUserException e){
                        // Manejar la excepción específica para usuario inválido
                        editTexteEmailCambiarPassword.setError("El usuario no existe o ya no es válido, Porfavor registrese de nuevo.");

                    } catch (Exception e){
                        // Manejar otras excepciones mostrando el mensaje de error y registrándolo
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Olvidar_password.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                // Ocultar la barra de progreso al finalizar
                progressBar.setVisibility(View.GONE);
            }
        });
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
            Intent intent = new Intent(Olvidar_password.this, Cambiar_password.class);
            startActivity(intent);
        }else if (id == R.id.Actualizar_perfil) {
            Intent intent = new Intent(Olvidar_password.this, Actualizar_perfil.class);
            startActivity(intent);
        }else if (id == R.id.Actualizar_email) {
            Intent intent = new Intent(Olvidar_password.this, Actualizar_Email.class);
            startActivity(intent);
        }/*else if (id == R.id.Eliminar_perfil) {
            Intent intent = new Intent(Perfil_Usuario.this, Eliminar_perfil.class);
            startActivity(intent);
        }else if (id == R.id.Ajustes) {
            Toast.makeText(Perfil_Usuario.this, "menu_ajustes", Toast.LENGTH_LONG).show();

            //Intent intent = new Intent(Perfil_Usuario.this, Ajustes.class);
            //startActivity(intent);
        }*/else if (id == R.id.cerrar_sesion) {
            authProfile.signOut();
            Toast.makeText(Olvidar_password.this, "Sesion cerrada", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Olvidar_password.this, MainActivity_PantallaInicio.class);

            // Redirigir al usuario a la actividad principal
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(Olvidar_password.this, "Algo salió mal ", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }
}
