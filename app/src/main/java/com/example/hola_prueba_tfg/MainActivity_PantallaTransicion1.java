package com.example.hola_prueba_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity_PantallaTransicion1 extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pantalla_transicion1);
        Button pasar_activity_PantallaInicio = findViewById(R.id.button_continuar);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity_PantallaTransicion1.this,R.color.black));
        gestureDetector = new GestureDetector(this, this);
        pasar_activity_PantallaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_PantallaTransicion1.this, MainActivity_PantallaInicio.class);
                startActivity(intent);
            }
        });


    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        float deltaX = e2.getX() - e1.getX();
        float deltaY = e2.getY() - e1.getY();

        // Si el desplazamiento horizontal es mayor que el desplazamiento vertical y hacia la izquierda
        if (Math.abs(deltaX) > Math.abs(deltaY) && deltaX < 0) {
            // Cambiar a la nueva actividad
            Intent intent = new Intent(MainActivity_PantallaTransicion1.this, MainActivity_PantallaTransicion2.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }

        return false;
    }
}