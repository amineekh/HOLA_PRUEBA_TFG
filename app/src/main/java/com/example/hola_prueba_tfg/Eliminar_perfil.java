package com.example.hola_prueba_tfg;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Eliminar_perfil extends AppCompatActivity {
    private EditText editTextUserPwd;
    private Button buttonAutentificacion, buttonEliminar_user;
    private TextView textViewBorrarUser_autentificado;
    // Objeto para manejar la autenticación de Firebase
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progress_Bar;
    String userPwd;
    private static final String TAG="Eliminar_perfil";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_perfil);

        // Establecer el título de la barra de acción
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Eliminar perfil");
        }

        // Inicialización de vistas (TextViews, ProgressBar, etc.) mediante sus ID
        editTextUserPwd = findViewById(R.id.editText_user_pwd);
        textViewBorrarUser_autentificado = findViewById(R.id.textView_borrar_user_autentificado);
        progress_Bar = findViewById(R.id.progressBar);

        buttonAutentificacion = findViewById(R.id.button_autentificarse);
        buttonEliminar_user = findViewById(R.id.button_borrar_user);

        buttonEliminar_user.setEnabled(false);

        // Inicialización de FirebaseAuth para la autenticación de Firebase
        authProfile = FirebaseAuth.getInstance();

        // Obteniendo el usuario actualmente autenticado
        firebaseUser = authProfile.getCurrentUser();

        // Verifica si el usuario está autenticado
        if (firebaseUser.equals("")) {
            // Mostrar un mensaje si no hay usuario autenticado
            Toast.makeText(Eliminar_perfil.this, "Algo salió mal, detalles de usuarios no están disponibles en este momento", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Eliminar_perfil.this, Perfil_Usuario.class);
            startActivity(intent);
            finish();

        } else {

            // Si hay un usuario autenticado, mostrar la información del perfil

            ReAutentificarse(firebaseUser);
            //progress_Bar.setVisibility(View.VISIBLE);
        }




    }

    private void ReAutentificarse(FirebaseUser firebaseUser) {
        buttonAutentificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //userPwd = editTextUserPwd.getText().toString();

                String hashedPassword =  editTextUserPwd.getText().toString();
                String userPwd = hashPassword(hashedPassword);



                if (TextUtils.isEmpty(userPwd)){
                    Toast.makeText(Eliminar_perfil.this, "Se necesita la contraseña para continuar", Toast.LENGTH_LONG).show();
                    editTextUserPwd.setError("Por favor, introduzca la contraseña para la autentificación");
                    editTextUserPwd.requestFocus();
                }else{
                    progress_Bar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                progress_Bar.setVisibility(View.GONE);

                                editTextUserPwd.setEnabled(false);
                                buttonAutentificacion.setEnabled(false);

                                buttonEliminar_user.setEnabled(true);


                                textViewBorrarUser_autentificado.setText("Autenticación Correcta: Ya puedes eliminar tu perfil");
                                Toast.makeText(Eliminar_perfil.this, "Contraseña verificada:  " + " Ya puedes eliminar tu perfil, esta acción es irrebisible", Toast.LENGTH_LONG).show();

                                // cambiar la contrseña
                                buttonEliminar_user.setBackgroundTintList(ContextCompat.getColorStateList(
                                        Eliminar_perfil.this, R.color.rojo)
                                );
                                buttonEliminar_user.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mostrar_dialogo_alerta();
                                    }
                                });

                            }else {
                                // Manejar errores durante la reautenticación
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(Eliminar_perfil.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    progress_Bar.setVisibility(View.GONE);

                                }
                            }

                        }
                    });
                }
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

    private void mostrar_dialogo_alerta() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Eliminar_perfil.this);
        builder.setTitle("Eliminar Perfil");
        builder.setMessage("¿De verdad quieres eliminar tu perfil?, Esta acción es irreversible.");

        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Eliminar_user(firebaseUser);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Eliminar_perfil.this, Perfil_Usuario.class);
                startActivity(intent);
                finish();
            }
        });


        //crear el dialogo
        AlertDialog alertDialog = builder.create();

        //cambiar color a continuar
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });

        //mostrar el dialogo
        alertDialog.show();

        // Obtener el botón positivo del diálogo y cambiar su color de texto a rojo
        /*
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(Color.RED);
        }

         */

    }

    private void Eliminar_user(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Eliminar_datos_user();

                    authProfile.signOut();

                    Toast.makeText(Eliminar_perfil.this, "El usuario ya ha sido eliminado"
                           , Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Eliminar_perfil.this, MainActivity_PantallaInicio.class);
                    startActivity(intent);
                    finish();

                }else {
                    // Manejar errores durante la reautenticación
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(Eliminar_perfil.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        progress_Bar.setVisibility(View.GONE);

                    }
                }
                progress_Bar.setVisibility(View.GONE);

            }
        });

    }


    private void Eliminar_datos_user() {

        // Obteniendo una referencia a la ubicación del usuario en la base de datos de Firebase
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Usuarios_registrados");
        referenceProfile.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Datos del  usuario Eliminados");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Datos del  usuario Eliminados");
                Toast.makeText(Eliminar_perfil.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(Eliminar_perfil.this, Cambiar_password.class);
            startActivity(intent);
        }else if (id == R.id.Actualizar_perfil) {
            Intent intent = new Intent(Eliminar_perfil.this, Actualizar_perfil.class);
            startActivity(intent);
        }else if (id == R.id.Actualizar_email) {
            Intent intent = new Intent(Eliminar_perfil.this, Actualizar_Email.class);
            startActivity(intent);
        }else if (id == R.id.Eliminar_perfil) {
            Intent intent = new Intent(Eliminar_perfil.this, Eliminar_perfil.class);
            startActivity(intent);
        }/*else if (id == R.id.Ajustes) {
            Toast.makeText(Perfil_Usuario.this, "menu_ajustes", Toast.LENGTH_LONG).show();

            //Intent intent = new Intent(Perfil_Usuario.this, Ajustes.class);
            //startActivity(intent);
        }*/else if (id == R.id.cerrar_sesion) {
            authProfile.signOut();
            Toast.makeText(Eliminar_perfil.this, "Sesion cerrada", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Eliminar_perfil.this, MainActivity_PantallaInicio.class);

            // Redirigir al usuario a la actividad principal
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(Eliminar_perfil.this, "Algo salió mal ", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }
}