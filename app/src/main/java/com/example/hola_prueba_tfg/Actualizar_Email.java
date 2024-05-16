package com.example.hola_prueba_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Actualizar_Email extends AppCompatActivity {

    private TextView textViewActualizarEmailViejo, textView_update_email_authenticated;
    private EditText editTextVerificarPassword, editTextActualizarEmailNuevo;
    private Button buttonActualizarEmail;
    private ProgressBar progress_Bar;

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    private String usuario_email_viejo, usuario_email_nuevo, usuario_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_email);

        // Establecer el título de la barra de acción
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cambiar Email");
        }

        // Inicialización de vistas (TextViews, ProgressBar, etc.) mediante sus ID
        textView_update_email_authenticated = findViewById(R.id.textView_update_email_authenticated);
        editTextVerificarPassword = findViewById(R.id.editText_actualizar_email_verificar_password);
        editTextActualizarEmailNuevo = findViewById(R.id.editText_actualizar_email_nuevo);
        buttonActualizarEmail = findViewById(R.id.button_actualizar_email);
        progress_Bar = findViewById(R.id.progressBar);

        // Deshabilitar el botón y el campo de texto para el nuevo email al inicio
        buttonActualizarEmail.setEnabled(false);
        editTextActualizarEmailNuevo.setEnabled(false);

        // Inicialización de FirebaseAuth para la autenticación de Firebase
        authProfile = FirebaseAuth.getInstance();
        // Obteniendo el usuario actualmente autenticado
        firebaseUser = authProfile.getCurrentUser();

        // Obtener el email viejo del usuario y mostrarlo en un TextView
        usuario_email_viejo = firebaseUser.getEmail();
        TextView textViewEmailViejo = findViewById(R.id.textView_actualizar_email_viejo);
        textViewEmailViejo.setText(usuario_email_viejo);

        // Verificar si el usuario está autenticado
        if (firebaseUser == null) {
            Toast.makeText(Actualizar_Email.this, "Algo salió mal", Toast.LENGTH_LONG).show();
        } else {
            // Proceder con el proceso de reautenticación
            ReAutentificarse(firebaseUser);
        }
    }

    // Método para realizar la reautenticación del usuario
    private void ReAutentificarse(FirebaseUser firebaseUser) {
        Button buttonAutentificacionUser = findViewById(R.id.button_autentificacion_user);
        buttonAutentificacionUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //usuario_pwd = editTextVerificarPassword.getText().toString();

                String hashedPassword = editTextVerificarPassword.getText().toString();
                String usuario_pwd = hashPassword(hashedPassword);

                if (TextUtils.isEmpty(usuario_pwd)) {
                    Toast.makeText(Actualizar_Email.this, "Se necesita la contraseña para continuar", Toast.LENGTH_LONG).show();
                    editTextVerificarPassword.setError("Por favor, introduzca la contraseña para la autentificación");
                    editTextVerificarPassword.requestFocus();
                } else {
                    progress_Bar.setVisibility(View.VISIBLE);

                    // Crear credencial de autenticación con email y contraseña
                    AuthCredential credential = EmailAuthProvider.getCredential(usuario_email_viejo, usuario_pwd);

                    // Reautenticar al usuario con la credencial proporcionada
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Si la reautenticación es exitosa, habilitar la actualización de email
                                progress_Bar.setVisibility(View.GONE);
                                Toast.makeText(Actualizar_Email.this, "Contraseña verificada: Ya puedes ingresar un nuevo Email", Toast.LENGTH_LONG).show();
                                textView_update_email_authenticated.setText("Autenticación Correcta. Ya puedes actualizar tu email");

                                editTextActualizarEmailNuevo.setEnabled(true);
                                editTextVerificarPassword.setEnabled(false);
                                buttonAutentificacionUser.setEnabled(false);
                                buttonActualizarEmail.setEnabled(true);

                                // Cambiar el color de fondo del botón de actualizar email
                                buttonActualizarEmail.setBackgroundTintList(ContextCompat.getColorStateList(Actualizar_Email.this, R.color.rojo));

                                // Configurar el listener para el botón de actualizar email
                                buttonActualizarEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        usuario_email_nuevo = editTextActualizarEmailNuevo.getText().toString();
                                        if (TextUtils.isEmpty(usuario_email_nuevo)) {
                                            Toast.makeText(Actualizar_Email.this, "Por favor, introduzca su nuevo Email", Toast.LENGTH_LONG).show();
                                            editTextActualizarEmailNuevo.setError("Por favor, introduzca su nuevo Email");
                                            editTextActualizarEmailNuevo.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(usuario_email_nuevo).matches()) {
                                            Toast.makeText(Actualizar_Email.this, "Por favor, introduzca un Email válido", Toast.LENGTH_LONG).show();
                                            editTextActualizarEmailNuevo.setError("Por favor, introduzca un Email válido");
                                            editTextActualizarEmailNuevo.requestFocus();
                                        } else if (usuario_email_viejo.matches(usuario_email_nuevo)) {
                                            Toast.makeText(Actualizar_Email.this, "El nuevo Email no puede ser el mismo que el antiguo.", Toast.LENGTH_LONG).show();
                                            editTextActualizarEmailNuevo.setError("El nuevo Email no puede ser el mismo que el antiguo.");
                                            editTextActualizarEmailNuevo.requestFocus();
                                        } else {
                                            // Si todo está correcto, actualizar el email del usuario
                                            progress_Bar.setVisibility(View.VISIBLE);
                                            actualizar_email(firebaseUser);
                                        }
                                    }
                                });

                            } else {
                                // Manejar errores durante la reautenticación
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(Actualizar_Email.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }


    // Método para actualizar el email del usuario
    private void actualizar_email(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(usuario_email_nuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    // Si la actualización es exitosa, enviar correo de verificación
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(Actualizar_Email.this, "Se ha enviado un correo de verificación. Por favor verifica tu correo electrónico.", Toast.LENGTH_LONG).show();

                    // Redirigir a la pantalla de perfil después de la actualización
                    Intent intent = new Intent(Actualizar_Email.this, Perfil_Usuario.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Manejar errores durante la actualización de email
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(Actualizar_Email.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                // Ocultar el ProgressBar después de la operación
                progress_Bar.setVisibility(View.GONE);
            }
        });
    }

    public static String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Add password bytes to digest
            md.update(password.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // Convert bytes to hexadecimal format
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
        // Inflar el menú de opciones en la barra de acción
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
            Intent intent = new Intent(Actualizar_Email.this, Cambiar_password.class);
            startActivity(intent);
        }else if (id == R.id.Actualizar_perfil) {
            Intent intent = new Intent(Actualizar_Email.this, Actualizar_perfil.class);
            startActivity(intent);
        }else if (id == R.id.Actualizar_email) {
            Intent intent = new Intent(Actualizar_Email.this, Actualizar_Email.class);
            startActivity(intent);
        }else if (id == R.id.Eliminar_perfil) {
            Intent intent = new Intent(Actualizar_Email.this, Eliminar_perfil.class);
            startActivity(intent);
        }/*else if (id == R.id.Ajustes) {
            Toast.makeText(Perfil_Usuario.this, "menu_ajustes", Toast.LENGTH_LONG).show();

            //Intent intent = new Intent(Perfil_Usuario.this, Ajustes.class);
            //startActivity(intent);
        }*/else if (id == R.id.cerrar_sesion) {
            authProfile.signOut();
            Toast.makeText(Actualizar_Email.this, "Sesion cerrada", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Actualizar_Email.this, MainActivity_PantallaInicio.class);

            // Redirigir al usuario a la actividad principal
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(Actualizar_Email.this, "Algo salió mal ", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }

}

