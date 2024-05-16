package com.example.hola_prueba_tfg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Email_Verificacion extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verificacion);
        getWindow().setStatusBarColor(ContextCompat.getColor(Email_Verificacion.this, R.color.rojo));


        // Enlace de vistas con variables
        TextView textView_iniciar_sesion = findViewById(R.id.textView_iniciar_sesion);

        // Definición de acción al hacer clic en el texto de inicio de sesión
        textView_iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí es donde se inicia el nuevo Activity para iniciar sesión
                Intent intent = new Intent(Email_Verificacion.this, MainActivity_IniciarSesion.class);
                startActivity(intent);
            }
        });



        TextView textViewRenvioEnlaceAutentificacion = findViewById(R.id.textView_renvio_enlace_autentificacion);
        textViewRenvioEnlaceAutentificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el usuario actualmente autenticado
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Verificar si el correo electrónico está verificado
                    if (!user.isEmailVerified()) {
                        // Enviar el correo de verificación nuevamente
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Email_Verificacion.this, "Se ha enviado nuevamente el correo de verificación.", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(Email_Verificacion.this, "No se pudo enviar nuevamente el correo de verificación. Inténtalo de nuevo más tarde.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    } else {
                        // Si el correo electrónico ya está verificado, mostrar un mensaje al usuario
                        Toast.makeText(Email_Verificacion.this, "Su correo electrónico ya está verificado.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Si el usuario no está autenticado, redirigirlo a la pantalla de inicio de sesión
                    Intent intent = new Intent(Email_Verificacion.this, MainActivity_IniciarSesion.class);
                    startActivity(intent);
                }
            }
        });


        // Con este ONCLICK conseguimos que cuando el usuario pulse al boton de email pues la app le redirige ala aplicacion de GMAIL
        Button botonEmail = findViewById(R.id.boton_email);
        botonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent con la acción ACTION_SENDTO y el esquema "mailto:"
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));

                // Verificar si hay una actividad que maneje este Intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Abrir la aplicación de correo electrónico predefinida
                    startActivity(intent);
                }
            }
        });






    }


}

