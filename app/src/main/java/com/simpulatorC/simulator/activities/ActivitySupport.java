package com.simpulatorC.simulator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.simpulatorC.simulator.R;

public class ActivitySupport extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commands);

        ((ImageView) findViewById(R.id.imageView_commands_back_to_main)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitySupport.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("page","1");
                finish();
                overridePendingTransition(R.anim.enter_from_bottom_to_top, R.anim.exit_from_bottom_to_top);
                startActivity(intent);
            }
        });
    }
}
