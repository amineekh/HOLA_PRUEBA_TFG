package com.example.hola_prueba_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Actualizar_perfil extends AppCompatActivity {

    // Declaración de variables de vistas y datos del perfil
    private EditText editTextActualizarNombrePerfil, editTextActualizarFechaPerfil, editTextActualizarTelefonoPerfil;
    private RadioGroup radiogroupactualizargender;
    private RadioButton radioBUTTONGenderSeleccionado;
    private String textNombre, textFecha, textEmail, textTelefono, textPassword;

    private FirebaseAuth authProfile; // Objeto para manejar la autenticación de Firebase

    private ProgressBar progressBar; // Barra de progreso para mostrar durante la carga o procesamiento

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_perfil);

        // Establecer el título de la barra de acción
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Actualizar perfil");
        }

        // Inicialización de vistas (EditText, ProgressBar, etc.) mediante sus IDs
        editTextActualizarNombrePerfil = findViewById(R.id.editText_actualizar_nombre_perfil);
        editTextActualizarFechaPerfil = findViewById(R.id.editText_actualizar_fecha_perfil);
        editTextActualizarTelefonoPerfil = findViewById(R.id.editText_actualizar_telefono_perfil);
        progressBar = findViewById(R.id.progressBar);
        radiogroupactualizargender = findViewById(R.id.radio_group_update_profile_gender);

        // Inicialización de FirebaseAuth para la autenticación de Firebase
        authProfile = FirebaseAuth.getInstance();

        // Obteniendo el usuario actualmente autenticado
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        // Llamar al método para mostrar y cargar el perfil del usuario
        mostrar_perfil(firebaseUser);

        // Asignación de acción al hacer clic en el campo de fecha o en el ícono del calendario
        editTextActualizarFechaPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar el selector de fecha al hacer clic en el campo de fecha
                showMaterialDatePicker();
            }
        });

        // Asignación de acción al hacer clic en el ícono del calendario
        ImageView imageView_date_picker = findViewById(R.id.imageView_date_picker);
        imageView_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar el selector de fecha al hacer clic en el ícono del calendario
                showMaterialDatePicker();
            }
        });

        // Configuración del OnClickListener para el botón de actualizar perfil
        Button buttonActualizarPerfil = findViewById(R.id.button_actualizar_perfil);
        buttonActualizarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamar al método para actualizar el perfil del usuario
                actualizar_perfil(firebaseUser);
            }
        });
    }

    // Método para actualizar el perfil del usuario en Firebase Database
    private void actualizar_perfil(FirebaseUser firebaseUser) {
        // Validación de los campos del perfil
        if (TextUtils.isEmpty(textNombre)) {
            Toast.makeText(Actualizar_perfil.this, "Se requiere nombre completo", Toast.LENGTH_LONG).show();
            editTextActualizarNombrePerfil.setError("Se requiere nombre completo");
            editTextActualizarNombrePerfil.requestFocus();
        } else if (TextUtils.isEmpty(textTelefono)) {
            Toast.makeText(Actualizar_perfil.this, "Se requiere introducir el número de teléfono", Toast.LENGTH_LONG).show();
            editTextActualizarTelefonoPerfil.setError("Se requiere introducir el número de teléfono");
            editTextActualizarTelefonoPerfil.requestFocus();
        } else if (textTelefono.length() != 9) {
            Toast.makeText(Actualizar_perfil.this, "Por favor, introduzca un número de teléfono válido", Toast.LENGTH_LONG).show();
            editTextActualizarTelefonoPerfil.setError("Número de teléfono incorrecto");
            editTextActualizarTelefonoPerfil.requestFocus();
        } else if (TextUtils.isEmpty(textFecha)) {
            Toast.makeText(Actualizar_perfil.this, "Se requiere introducir la fecha de nacimiento", Toast.LENGTH_LONG).show();
            editTextActualizarFechaPerfil.setError("Se requiere introducir la fecha de nacimiento");
            editTextActualizarFechaPerfil.requestFocus();
        } else {
            // Obtener los datos actualizados del perfil del usuario
            textNombre = editTextActualizarNombrePerfil.getText().toString();
            textFecha = editTextActualizarFechaPerfil.getText().toString();
            textTelefono = editTextActualizarTelefonoPerfil.getText().toString();

            // Crear objeto ReadWriteUserDetails con los datos actualizados del usuario
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textNombre, textEmail, textTelefono, textFecha, textPassword);

            // Referencia a la base de datos de Firebase
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Usuarios_registrados");

            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            // Actualizar los detalles del usuario en la base de datos
            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Actualizar el nombre de usuario en Firebase Auth
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textNombre).build();
                        firebaseUser.updateProfile(profileUpdates);

                        // Mostrar mensaje de éxito y redirigir al usuario al perfil
                        Toast.makeText(Actualizar_perfil.this, "Perfil actualizado correctamente", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Actualizar_perfil.this, Perfil_Usuario.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Manejar errores durante la actualización del perfil
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(Actualizar_perfil.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    // Método para mostrar y cargar el perfil del usuario desde Firebase Database
    private void mostrar_perfil(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Usuarios_registrados");

        progressBar.setVisibility(View.VISIBLE);

        // Leer los detalles del perfil del usuario desde la base de datos
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Obtener los detalles del usuario desde Firebase Database
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                // Verificar si se obtuvieron detalles válidos del usuario
                if (readUserDetails != null) {
                    // Asignar los detalles del usuario a las variables locales
                    textNombre = readUserDetails.textNombre;
                    textEmail = firebaseUser.getEmail();
                    textFecha = readUserDetails.textFecha;
                    textTelefono = readUserDetails.textTelefono;
                    textPassword = readUserDetails.textPassword;

                    // Mostrar los detalles del usuario en las vistas correspondientes
                    editTextActualizarNombrePerfil.setText(textNombre);
                    editTextActualizarFechaPerfil.setText(textFecha);
                    editTextActualizarTelefonoPerfil.setText(textTelefono);
                } else {
                    Toast.makeText(Actualizar_perfil.this, "¡Error al obtener los datos del perfil!", Toast.LENGTH_LONG).show();
                }

                // Ocultar la barra de progreso después de obtener los datos del usuario
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Actualizar_perfil.this, "¡Error de base de datos!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Método para mostrar el selector de fecha utilizando MaterialDatePicker
    public void showMaterialDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Selecciona una fecha");

        // Restricción para fechas futuras
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setEnd(System.currentTimeMillis());

        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        // Acción al seleccionar la fecha
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // Formatear y establecer la fecha seleccionada en el EditText de fecha
                String fechaSeleccionada = formatDate(selection);
                editTextActualizarFechaPerfil.setText(fechaSeleccionada);
            }
        });

        // Quitar el foco del EditText después de seleccionar la fecha
        editTextActualizarFechaPerfil.clearFocus();
    }

    // Método para formatear la fecha seleccionada en el formato deseado
    private String formatDate(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date(timestamp));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú de opciones (toolbar) desde el archivo de recursos menu_perfil.xml
        getMenuInflater().inflate(R.menu.menu_perfil, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Manejar las acciones del menú de opciones
        if (id == R.id.refrescar) {
            // Refrescar la actividad
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.Actualizar_password) {
            // Redirigir a la actividad de cambio de contraseña
            Intent intent = new Intent(Actualizar_perfil.this, Cambiar_password.class);
            startActivity(intent);
        } else if (id == R.id.Actualizar_perfil) {
            // Redirigir a la misma actividad (Actualizar perfil)
            Intent intent = new Intent(Actualizar_perfil.this, Actualizar_perfil.class);
            startActivity(intent);
        } else if (id == R.id.Actualizar_email) {
            // Redirigir a la actividad de actualización de email
            Intent intent = new Intent(Actualizar_perfil.this, Actualizar_Email.class);
            startActivity(intent);
        } else if (id == R.id.Eliminar_perfil) {
            Intent intent = new Intent(Actualizar_perfil.this, Eliminar_perfil.class);
            startActivity(intent);
        }/*else if (id == R.id.Ajustes) {
            Toast.makeText(Perfil_Usuario.this, "menu_ajustes", Toast.LENGTH_LONG).show();

            //Intent intent = new Intent(Perfil_Usuario.this, Ajustes.class);
            //startActivity(intent);
        }*/else if (id == R.id.cerrar_sesion) {
            // Cerrar sesión del usuario y redirigir a la pantalla de inicio
            authProfile.signOut();
            Toast.makeText(Actualizar_perfil.this, "Sesión cerrada", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Actualizar_perfil.this, MainActivity_PantallaInicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(Actualizar_perfil.this, "¡Opción no válida!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
