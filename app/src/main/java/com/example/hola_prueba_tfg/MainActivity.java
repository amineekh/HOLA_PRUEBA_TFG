package com.example.hola_prueba_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autentificacion_cuenta;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //autentificacion_cuenta = FirebaseAuth.getInstance();



        Button botonPerfil = findViewById(R.id.perfil);
        botonPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí inicia la actividad Perfil_Usuario
                Intent intent = new Intent(MainActivity.this, Perfil_Usuario.class);
                startActivity(intent);
            }
        });

    }
    /*
    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si el usuario ya ha iniciado sesión cada vez que esta actividad se vuelve visible
        // Verificar si el usuario ya ha iniciado sesión al iniciar la actividad
        if (autentificacion_cuenta.getCurrentUser() != null) {
            Toast.makeText(MainActivity.this, "Ya has iniciado sesión, porque lo has hecho antes", Toast.LENGTH_LONG).show();

            // startActivity(new Intent(MainActivity.this, Perfil_Usuario.class));
           // finish(); // Cerrar esta actividad para que el usuario no pueda regresar atrás con el botón "Atrás"

        } else {
            Toast.makeText(MainActivity.this, "Puedes iniciar sesión ahora!", Toast.LENGTH_LONG).show();
            // Aquí puedes agregar más lógica si deseas, como mostrar un botón para iniciar sesión, etc.
        }
    }

     */




}