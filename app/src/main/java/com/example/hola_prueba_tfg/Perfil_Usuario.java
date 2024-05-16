package com.example.hola_prueba_tfg;

// Importaciones necesarias para la actividad de Android

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


// Declaración de la clase de la actividad
public class Perfil_Usuario extends AppCompatActivity {

    // Declaración de variables para elementos de la interfaz de usuario
    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDob, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private String fullName, email, dob, gender, mobile;

    private SwipeRefreshLayout swipeContainer;

    // Objeto para manejar la autenticación de Firebase
    private FirebaseAuth authProfile;

    // Método que se llama cuando se crea la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        // Establecer el título de la barra de acción
        if (getSupportActionBar() != null) {
           getSupportActionBar().setTitle("Perfil");
        }

        desliza_para_actualizar();

        // Inicialización de vistas (TextViews, ProgressBar, etc.) mediante sus ID
        textViewWelcome = findViewById(R.id.textView_show_welcome);
        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewDob = findViewById(R.id.textView_show_dob);
        textViewGender = findViewById(R.id.textView_show_gender);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        progressBar = findViewById(R.id.progress_bar);

        // Inicialización de FirebaseAuth para la autenticación de Firebase
        authProfile = FirebaseAuth.getInstance();

        // Obteniendo el usuario actualmente autenticado
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        // Verifica si el usuario está autenticado
        if (firebaseUser == null) {
            // Mostrar un mensaje si no hay usuario autenticado
            Toast.makeText(Perfil_Usuario.this, "Algo salió mal, detalles de usuarios no están disponibles", Toast.LENGTH_LONG).show();
        } else {

           // comprobar_email_verificado(firebaseUser);

            // Si hay un usuario autenticado, mostrar la información del perfil
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }


}

    private void desliza_para_actualizar() {
       swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {

               startActivity(getIntent());
               finish();
               overridePendingTransition(0,0);
               swipeContainer.setRefreshing(false);

           }
       });
        swipeContainer.setColorSchemeResources(android.R.color.holo_purple, android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);



    }

    private void comprobar_email_verificado(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            mostrar_dialogo_alerta();

        }
    }
    private void mostrar_dialogo_alerta() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Perfil_Usuario.this);
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


    // Método para mostrar el perfil de usuario basado en el usuario actual
    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();

        // Obteniendo una referencia a la ubicación del usuario en la base de datos de Firebase
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Usuarios_registrados");

        // Escuchando los cambios en los datos del usuario específico
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Obtener los detalles del usuario desde Firebase Database
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                // Verificar si se obtuvieron detalles válidos del usuario
                if (readUserDetails != null) {
                    // Obtener información específica del usuario

                    //fullName = firebaseUser.getDisplayName();
                    //email = firebaseUser.getEmail();

                    fullName = readUserDetails.textNombre;
                    email = firebaseUser.getEmail();
                   // email = readUserDetails.textEmail;
                    dob = readUserDetails.textFecha; // dob es la fecha de nacimiento
                    gender = readUserDetails.textPassword; // El género se almacena en el campo de contraseña (esto podría ser un error)
                    mobile = readUserDetails.textTelefono;

                    // Actualizar las vistas con la información del usuario
                    //textViewWelcome.setText(getString(R.string.perfil_usuario, fullName));
                    textViewWelcome.setText("¡Bienvenido, " + fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDob.setText(dob);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);
                }

                // Ocultar la barra de progreso después de obtener los datos del usuario
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Mostrar un mensaje si la lectura de datos se cancela o falla
                Toast.makeText(Perfil_Usuario.this, "¡Algo salió mal!", Toast.LENGTH_LONG).show();

                // Ocultar la barra de progreso en caso de error
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
            Intent intent = new Intent(Perfil_Usuario.this, Cambiar_password.class);
            startActivity(intent);
        }else if (id == R.id.Actualizar_perfil) {
            Intent intent = new Intent(Perfil_Usuario.this, Actualizar_perfil.class);
            startActivity(intent);
        }else if (id == R.id.Actualizar_email) {
            Intent intent = new Intent(Perfil_Usuario.this, Actualizar_Email.class);
            startActivity(intent);
        }else if (id == R.id.Eliminar_perfil) {
            Intent intent = new Intent(Perfil_Usuario.this, Eliminar_perfil.class);
            startActivity(intent);
        }/*else if (id == R.id.Ajustes) {
            Toast.makeText(Perfil_Usuario.this, "menu_ajustes", Toast.LENGTH_LONG).show();

            //Intent intent = new Intent(Perfil_Usuario.this, Ajustes.class);
            //startActivity(intent);
        }*/else if (id == R.id.cerrar_sesion) {
            authProfile.signOut();
            Toast.makeText(Perfil_Usuario.this, "Sesion cerrada", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Perfil_Usuario.this, MainActivity_PantallaInicio.class);

            // Redirigir al usuario a la actividad principal
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(Perfil_Usuario.this, "Algo salió mal ", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }
}