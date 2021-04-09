package com.simpulatorC.simulator.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.simpulatorC.simulator.R;

public class FragmentCamera extends Fragment {

    private ShapeableImageView shapeableImageView;
    private TextView hint;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main_fragment_camera, container, false);

        shapeableImageView = v.findViewById(R.id.frag_camera_start_to_follow);
        hint = v.findViewById(R.id.fragment_camera_hint);
        shapeableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{ Manifest.permission.CAMERA}, 100);
                }
                else startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 100);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100)
        {
            try{ shapeableImageView.setImageBitmap((Bitmap) data.getExtras().get("data")); hint.setVisibility(View.INVISIBLE);}
            catch (Exception ignored){}
        }

    }
}
