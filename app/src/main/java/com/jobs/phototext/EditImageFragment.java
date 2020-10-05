package com.jobs.phototext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class EditImageFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private EditImageFragmentListener listener;
    private SeekBar sb_brightness,sb_contrast,sb_saturation;
    public void setListener(EditImageFragmentListener EditListener) {
       listener =EditListener;
    }

    static EditImageFragment instance;

    public static EditImageFragment getInstance() {
        if(instance == null)
            instance = new EditImageFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemView=inflater.inflate(R.layout.fragment_edit_image, container, false);

        sb_brightness = itemView.findViewById(R.id.sb_brightnes);
        sb_contrast = itemView.findViewById(R.id.sb_contrast);
        sb_saturation = itemView.findViewById(R.id.sb_saturation);

        sb_brightness.setMax(200);
        sb_brightness.setProgress(100);
        sb_brightness.setOnSeekBarChangeListener(this);

        sb_contrast.setMax(20);
        sb_contrast.setProgress(0);
        sb_contrast.setOnSeekBarChangeListener(this);

        sb_saturation.setMax(30);
        sb_saturation.setProgress(10);
        sb_saturation.setOnSeekBarChangeListener(this);

        return itemView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if(listener != null)
        {
            if(seekBar.getId() == R.id.sb_brightnes)
            {
                listener.onBrightnessChanged(progress-100);
            }
            else if(seekBar.getId() == R.id.sb_contrast)
            {
                progress += 10;
                float value = .10f*progress;
                listener.onContrastChanged(value);
            }
            else if(seekBar.getId() == R.id.sb_saturation)
            {
                float value = .10f*progress;
                listener.onSaturationChanged(value);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(listener != null)
            listener.onEditStarted();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditCompleted();

    }
    public void resetControls()
    {
        sb_brightness.setProgress(100);
        sb_contrast.setProgress(0);
        sb_saturation.setProgress(10);
    }
}