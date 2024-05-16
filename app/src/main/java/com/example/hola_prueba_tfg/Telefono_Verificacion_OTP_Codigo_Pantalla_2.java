package com.example.hola_prueba_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class Telefono_Verificacion_OTP_Codigo_Pantalla_2 extends AppCompatActivity {

    private EditText[] editTexts;
    private String verificationId;
    private String telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telefono_verificacion_otp_codigo_pantalla2);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.rojo));

        TextView textView_numero = findViewById(R.id.textView_numero);
        telefono = getIntent().getStringExtra("Telefono");
        verificationId = getIntent().getStringExtra("Verificacion");
        textView_numero.setText(String.format("+34 - %s", telefono));

        editTexts = new EditText[]{
                findViewById(R.id.edit_text_code_1),
                findViewById(R.id.edit_text_code_2),
                findViewById(R.id.edit_text_code_3),
                findViewById(R.id.edit_text_code_4),
                findViewById(R.id.edit_text_code_5),
                findViewById(R.id.edit_text_code_6)
        };

        configurarCodigoVerificacion();

        Button boton_verificar = findViewById(R.id.boton_verificar);
        boton_verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarCodigo();
            }
        });

        // Add click listener to textView_renviar_codigo
        TextView textViewRenviarCodigo = findViewById(R.id.textView_renviar_codigo);
        textViewRenviarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renviarCodigo();
            }
        });

        /*
        TextView textView_iniciar_sesion = findViewById(R.id.textView_iniciar_sesion);
        textView_iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Telefono_Verificacion_OTP_Codigo_Pantalla_2.this, MainActivity_IniciarSesion.class);
                startActivity(intent);
                finish();
            }
        });

         */


    }

    private void renviarCodigo() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+34" + telefono,
                20,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // (gestionar la finalización de la verificación si es necesario)
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // ... (manejar el error de verificación)
                        Toast.makeText(Telefono_Verificacion_OTP_Codigo_Pantalla_2.this, "Error al reenviar el código: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Toast.makeText(Telefono_Verificacion_OTP_Codigo_Pantalla_2.this, "Se ha reenviado el código de verificación", Toast.LENGTH_SHORT).show();
                        verificationId = newVerificationId; // Update the verificationId with the new one
                    }
                });
    }

    private void configurarCodigoVerificacion() {
        for (int i = 0; i < editTexts.length; i++) {
            final int currentIndex = i;

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 0) {
                        if (currentIndex > 0) {
                            editTexts[currentIndex - 1].requestFocus();
                        }
                    } else if (currentIndex < editTexts.length - 1) {
                        editTexts[currentIndex + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            };

            editTexts[i].addTextChangedListener(textWatcher);
        }
    }

    private void verificarCodigo() {
        StringBuilder codigoIngresado = new StringBuilder();
        for (EditText editText : editTexts) {
            codigoIngresado.append(editText.getText().toString());
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, codigoIngresado.toString());

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Código correcto, procede a la actividad deseada
                            Toast.makeText(Telefono_Verificacion_OTP_Codigo_Pantalla_2.this, "Código de verificación correcto", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Telefono_Verificacion_OTP_Codigo_Pantalla_2.this, MainActivity_IniciarSesion.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Código incorrecto
                            Toast.makeText(Telefono_Verificacion_OTP_Codigo_Pantalla_2.this, "Código de verificación incorrecto", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


