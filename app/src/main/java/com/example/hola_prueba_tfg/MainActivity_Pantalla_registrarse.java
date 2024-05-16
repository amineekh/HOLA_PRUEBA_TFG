package com.example.hola_prueba_tfg;

// Importaciones de clases necesarias

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity_Pantalla_registrarse extends AppCompatActivity {

    // Declaración de variables
    private EditText EditTextNombre, EditTextEmail, EditTextTelefono, editTextFecha;
    private TextInputLayout TextInputRegisterPassword, TextInputRegisterConfirmPassword;
    private Button button_crear_cuenta;
    private static final String TAG="MainActivity_Pantalla_registrarse";
    private Switch switch1, switch2;
    Dialog mi_dialogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pantalla_registrarse);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity_Pantalla_registrarse.this, R.color.rojo));


        mi_dialogo = new Dialog(this);


        // Enlace de vistas con variables
        TextView inicia_sesion = findViewById(R.id.textView_iniciasesion);

        // Definición de acción al hacer clic en el texto de inicio de sesión
        inicia_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí es donde se inicia el nuevo Activity para iniciar sesión
                Intent intent = new Intent(MainActivity_Pantalla_registrarse.this, MainActivity_IniciarSesion.class);
                startActivity(intent);
            }
        });

        // Mostrar un mensaje de toast para indicar que el usuario puede registrarse
        //Toast.makeText(MainActivity_Pantalla_registrarse.this, "Ahora puedes Registrarte", Toast.LENGTH_LONG).show();

        // Asignación de vistas a variables
        EditTextNombre = findViewById(R.id.editText_nombre);
        EditTextEmail = findViewById(R.id.editText_email);
        EditTextTelefono = findViewById(R.id.editText_telefono);
        editTextFecha = findViewById(R.id.editText_fecha);
        TextInputRegisterPassword = findViewById(R.id.text_input_register_password);
        TextInputRegisterConfirmPassword = findViewById(R.id.text_input_register_confirm_password);

        //switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);

        // Asignación de acción al botón de crear cuenta
        button_crear_cuenta = findViewById(R.id.button_crear_cuenta);
        button_crear_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //valores de los switches
                //boolean switch1Activado = switch1.isChecked();
                boolean switch2Activado = switch2.isChecked();

                // Verificar que ambos switches estén activados
                if (switch2Activado) {

                    // Lógica para verificar y registrar usuario
                    String text_nombre = EditTextNombre.getText().toString();
                    String text_email = EditTextEmail.getText().toString();
                    String text_telefono = EditTextTelefono.getText().toString();
                    String text_fecha = editTextFecha.getText().toString();
                    // String text_password = TextInputRegisterPassword.getEditText().getText().toString();

                    String hashedPassword = TextInputRegisterPassword.getEditText().getText().toString();
                    String text_password = hashPassword(hashedPassword);

                    String hashedPassword2 = TextInputRegisterConfirmPassword.getEditText().getText().toString();
                    String text_confirm_password = hashPassword(hashedPassword2);

                    //COMPROBACION DE LOS CAMPOS
                    if (TextUtils.isEmpty(text_nombre)) {
                        // Mostrar un mensaje de error si el nombre está vacío
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, "Nombre", Toast.LENGTH_LONG).show();
                        EditTextNombre.setError("Se requiere nombre completo");
                        EditTextNombre.requestFocus();
                    } else if (TextUtils.isEmpty(text_email)) {
                        // Mostrar un mensaje de error si el email está vacío
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, "Email", Toast.LENGTH_LONG).show();
                        EditTextEmail.setError("Se requiere introducir el Email");
                        EditTextEmail.requestFocus();
                    }
                    // Comprobamos si el email introudcido por el usuario es correcto o no
                    else if (!Patterns.EMAIL_ADDRESS.matcher(text_email).matches()) {
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, "Por favor Vuelva a Introducir un Email", Toast.LENGTH_LONG).show();
                        EditTextEmail.setError("Se requiere introducir el Email Valido");
                        EditTextEmail.requestFocus();
                    } else if (TextUtils.isEmpty(text_telefono)) {
                        // Mostrar un mensaje de error si el número de teléfono está vacío
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, "+34 ", Toast.LENGTH_LONG).show();
                        EditTextTelefono.setError("Se requiere introducir el Número de Telefono");
                        EditTextTelefono.requestFocus();
                    }
                    //comprobar que el numero es correcto
                    else if (text_telefono.length() !=9  ) {
                        // Mostrar un mensaje de error si el número de teléfono no tiene la longitud correcta
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, "Por favor Vuelva a Introducir el Numero de Telefono ", Toast.LENGTH_LONG).show();
                        EditTextTelefono.setError("Numero de telefono incorrecto");
                        EditTextTelefono.requestFocus();
                    } else if (TextUtils.isEmpty(text_fecha)) {
                        // Mostrar un mensaje de error si la fecha está vacía
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, "Fecha Nacimiento", Toast.LENGTH_LONG).show();
                        editTextFecha.setError("Se requiere introducir el Fecha de Nacimiento");
                        editTextFecha.requestFocus();
                    } else if (TextUtils.isEmpty(text_password)) {
                        // Mostrar un mensaje de error si la contraseña está vacía
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, "Contraseña" , Toast.LENGTH_LONG).show();
                        TextInputRegisterPassword.setError("Se requiere introducir la Contraseña");
                        TextInputRegisterPassword.requestFocus();
                    }
                    // la contraseña no debe ser menor que 6 digitos
                    else if (text_telefono.length() < 6 ) {
                        // Mostrar un mensaje de error si la contraseña es demasiado corta
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, "La contraseña debe contener mas de 6 digitos", Toast.LENGTH_LONG).show();
                        EditTextTelefono.setError("Contraseña Insegura");
                        EditTextTelefono.requestFocus();
                    } else if (TextUtils.isEmpty(text_confirm_password)) {
                        // Mostrar un mensaje de error si la confirmación de contraseña está vacía
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, "Confirmar contraseña" , Toast.LENGTH_LONG).show();
                        TextInputRegisterConfirmPassword.setError("Se requiere la Confirmacion de la contraseña");
                        TextInputRegisterConfirmPassword.requestFocus();
                    }
                    // Comprobar que la contraseña son iguales
                    else if (!text_password.equals(text_confirm_password)) {
                        // Mostrar un mensaje de error si las contraseñas no coinciden
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, "Por favor introduzca la misma contraseña" , Toast.LENGTH_LONG).show();
                        TextInputRegisterConfirmPassword.setError("Se requiere la Confirmacion de la contraseña");
                        TextInputRegisterConfirmPassword.requestFocus();

                        // Limpiar los campos de contraseña y confirmación de contraseña
                        TextInputRegisterPassword.getEditText().clearComposingText();
                        TextInputRegisterConfirmPassword.getEditText().clearComposingText();

                    } else {
                        // Si todos los campos están llenos y las contraseñas coinciden, proceder con la creacion del metodo registro
                        registro_usuario(text_nombre, text_email, text_telefono, text_fecha, text_password, text_confirm_password);
                    }

                } else {
                    // Mostrar un mensaje de error si los switches no están activados
                    Toast.makeText(MainActivity_Pantalla_registrarse.this, "Para crear la cuenta debes aceptar los terminos de Politicas de Privacidad", Toast.LENGTH_SHORT).show();
                }





            }
        });


        // Asignación de acción al hacer clic en el campo de fecha
        editTextFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar el selector de fecha al hacer clic en el campo de fecha
                showMaterialDatePicker();
            }
        });
    }

    //METODO PARA MOSTRAR UN POP UP Y SU CONFIGURACION
    public void mostrar_PopUP() {
        TextView text_cerrar;
        TextView textView_continuar;
        mi_dialogo.setContentView(R.layout.activity_pop_up_eleccion_metodo_autentificacion);
        text_cerrar = (TextView) mi_dialogo.findViewById(R.id.text_cerrar);
        textView_continuar = (TextView) mi_dialogo.findViewById(R.id.textView_continuar);

        text_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mi_dialogo.dismiss();
            }
        });

        // Agregar acción al hacer clic en "Continuar"
        textView_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el botón de radio seleccionado
                RadioGroup radioGroup = mi_dialogo.findViewById(R.id.radio_group_metodos);
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == R.id.radioButton_email) {
                    // Si el usuario elige la autenticación por correo electrónico, iniciar la actividad correspondiente
                    Intent intent = new Intent(MainActivity_Pantalla_registrarse.this, Email_Verificacion.class);
                    startActivity(intent);

                    // Enviar correo de verificación al usuario
                    enviar_Correo_Verificacion();

                } else if (selectedId == R.id.radioButton_telefono) {
                    // Si el usuario elige la autenticación por teléfono, iniciar la actividad correspondiente
                    Intent intent = new Intent(MainActivity_Pantalla_registrarse.this, Telefono_Verificacion_OTP_Pantalla_1.class);
                    startActivity(intent);
                }

                // Cerrar el diálogo después de iniciar la actividad
                mi_dialogo.dismiss();
            }
        });

        // Configurar la ventana del diálogo para que aparezca en la parte inferior
        Window dialogWindow = mi_dialogo.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM); // Establecer la gravedad en la parte inferior
        }

        // Mostrar el diálogo
        mi_dialogo.show();
    }

    private void enviar_Correo_Verificacion() {

        // Obtener el usuario actualmente autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Enviar el correo de verificación
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity_Pantalla_registrarse.this, "Se ha enviado un correo de verificación. Por favor verifica tu correo electrónico.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity_Pantalla_registrarse.this, "No se pudo enviar el correo de verificación. Inténtalo de nuevo más tarde.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }


    }


    // Método para registrar un nuevo usuario
    private void registro_usuario(String textNombre, String textEmail, String textTelefono, String textFecha, String textPassword, String textConfirmPassword) {
        // Inicialización de Firebase Authentication
        FirebaseAuth autentificacion = FirebaseAuth.getInstance();

        // Crear usuario con email y contraseña
        autentificacion.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(MainActivity_Pantalla_registrarse.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Si el registro es exitoso, obtener el usuario actual y escribir los detalles en la base de datos
                    FirebaseUser firebaseUser = autentificacion.getCurrentUser();
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textNombre, textEmail, textTelefono, textFecha, textPassword);
                    DatabaseReference referencias_profile = FirebaseDatabase.getInstance().getReference("Usuarios_registrados");

                    // Guardar los detalles del usuario en la base de datos
                    referencias_profile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                // Si se guardan correctamente los detalles, enviar un correo de verificación al usuario y mostrar un mensaje de éxito
                               // firebaseUser.sendEmailVerification();
                               // Toast.makeText(MainActivity_Pantalla_registrarse.this, "Usuario registrado Correctamente, Por favor verifica tu Email", Toast.LENGTH_LONG).show();





                                // Mostrar el diálogo después de un registro exitoso
                                //mostrar_PopUP();

                                // Si el usuario elige la autenticación por correo electrónico, iniciar la actividad correspondiente
                                Intent intent = new Intent(MainActivity_Pantalla_registrarse.this, Email_Verificacion.class);
                                startActivity(intent);

                                // Enviar correo de verificación al usuario
                                enviar_Correo_Verificacion();


                                // Redirigir al usuario a la actividad principal
                                //Intent intent = new Intent(MainActivity_Pantalla_registrarse.this, Perfil_Usuario.class);
                                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                // startActivity(intent);
                                // finish();
                            }else{
                                // Si no se pueden guardar los detalles, mostrar un mensaje de error
                                Toast.makeText(MainActivity_Pantalla_registrarse.this, "Registro Usuario fallido, Por favor vuelve a intentar otra vez", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    // Si el registro falla, manejar excepciones y mostrar mensajes de error correspondientes
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        TextInputRegisterPassword.setError("La Contraseña es insegura. por favor use una combinación de letras, números y caracteres especiales");
                        TextInputRegisterPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        EditTextEmail.setError("El Email no es válido o ya está en uso. Por favor vuelva a introducir un Email ");
                        EditTextEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        EditTextEmail.setError("El usuario ya se registró con este correo electrónico. Usa otro correo electrónico");
                        EditTextEmail.requestFocus();
                    } catch (Exception e) {
                        // Manejar otras excepciones mostrando un mensaje de error y registrando la excepción
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(MainActivity_Pantalla_registrarse.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    // Aqui empieza el metodoCALENDARIO
    public void showMaterialDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Selecciona una fecha");

        // Crear restricción para fechas futuras
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setEnd(System.currentTimeMillis()); // Establecer la fecha final como el tiempo actual

        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                String fechaSeleccionada = formatDate(selection);
                editTextFecha.setText(fechaSeleccionada);
            }


        });

        // Quitar el foco del EditText
        editTextFecha.clearFocus();

    }
    // Aqui acaba el metodoCALENDARIO

    //CALENDARIO
    private String formatDate(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Change format if needed
        return sdf.format(new Date(timestamp));
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
}



