package com.example.hola_prueba_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity_PantallaInicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pantalla_inicio);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity_PantallaInicio.this,R.color.mi_color_de_barra_de_notificaciones));


        Button pasar_activity_pantalla_registro = findViewById(R.id.boton_registrarse);

        Button pasar_activity_iniciar_sesion = findViewById(R.id.boton_iniciar_sesion);

        pasar_activity_pantalla_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_PantallaInicio.this, MainActivity_Pantalla_registrarse.class);
                startActivity(intent);
            }
        });

        pasar_activity_iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_PantallaInicio.this, MainActivity_IniciarSesion.class);
                startActivity(intent);
            }
        });
    }
}