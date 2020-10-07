package com.jobs.phototext.listener;

public interface EditImageFragmentListener {
    void onBrightnessChanged(int brightness);
    void onSaturationChanged(float saturation);
    void onContrastChanged(float contrast);
    void onEditStarted();
    void onEditCompleted();


}
