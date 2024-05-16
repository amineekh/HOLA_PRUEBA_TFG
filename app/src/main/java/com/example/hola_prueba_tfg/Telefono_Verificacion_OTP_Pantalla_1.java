package com.example.hola_prueba_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Telefono_Verificacion_OTP_Pantalla_1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telefono_verificacion_otp_pantalla1);
        getWindow().setStatusBarColor(ContextCompat.getColor(Telefono_Verificacion_OTP_Pantalla_1.this, R.color.rojo));

        final EditText introducir_meter_telefono = findViewById(R.id.edit_text_meter_telefono);
        final ProgressBar Progress_bar = findViewById(R.id.progress_bar);
        final Button boton_enviar_otp = findViewById(R.id.boton_enviar_otp);


        boton_enviar_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (introducir_meter_telefono.getText().toString().trim().isEmpty()) {
                    Toast.makeText(Telefono_Verificacion_OTP_Pantalla_1.this, "Por favor, introduzca su número de teléfono", Toast.LENGTH_SHORT).show();
                    return;
                }
                Progress_bar.setVisibility(View.VISIBLE);
                boton_enviar_otp.setVisibility(View.INVISIBLE);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+34" + introducir_meter_telefono.getText().toString(),
                        30,
                        TimeUnit.SECONDS,
                        Telefono_Verificacion_OTP_Pantalla_1.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Progress_bar.setVisibility(View.GONE);
                                boton_enviar_otp.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Progress_bar.setVisibility(View.GONE);
                                boton_enviar_otp.setVisibility(View.VISIBLE);
                                Toast.makeText(Telefono_Verificacion_OTP_Pantalla_1.this, "Error en la verificación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                Progress_bar.setVisibility(View.GONE);
                                boton_enviar_otp.setVisibility(View.VISIBLE);

                                Intent intent = new Intent(getApplicationContext(), Telefono_Verificacion_OTP_Codigo_Pantalla_2.class);
                                intent.putExtra("Telefono", introducir_meter_telefono.getText().toString());
                                intent.putExtra("Verificacion", verificationId);
                                startActivity(intent);
                            }
                        });
            }
        });
    }
}








