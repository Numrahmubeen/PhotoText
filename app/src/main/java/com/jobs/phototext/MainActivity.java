package com.jobs.phototext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jobs.phototext.filters.FilterListener;
import com.jobs.phototext.filters.FilterViewAdapter;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.ViewType;


public class MainActivity extends AppCompatActivity implements AddTextFragmentListener, FilterListener,
        EmojiBSFragment.EmojiListener, EditImageFragmentListener {

    CardView btn_Gallery, btn_Camera, btn_Text, btn_share, btn_filters, btn_emoji, btn_adjust;
    ImageView iv_Save;

    private static final String TAG = "MainActivity";

    //TODO: 10/4/2020 fifth change
    PhotoEditor mPhotoEditor;
    PhotoEditorView mPhotoEditorView;
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    RecyclerView rv_Filters;
    private EmojiBSFragment mEmojiBSFragment;
    EditImageFragment editImageFragment;
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;
    Bitmap orignalBitmap, finalBitmap;

    static {
        System.loadLibrary("NativeImageProcessor");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_Save = findViewById(R.id.iv_save);
        btn_Camera = findViewById(R.id.btn_camera);
        btn_Gallery = findViewById(R.id.btn_choose_from_gallery);
        btn_Text = findViewById(R.id.btn_add_text);
        btn_share = findViewById(R.id.btn_share);
        btn_filters = findViewById(R.id.btn_filters_list);
        btn_emoji = findViewById(R.id.btn_emoji);
        btn_adjust = findViewById(R.id.btn_adjust);
        rv_Filters = findViewById(R.id.rvFilterView);


        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_Filters.setLayoutManager(llmFilters);
        rv_Filters.setHasFixedSize(true);
        rv_Filters.setAdapter(mFilterViewAdapter);

        mEmojiBSFragment = new EmojiBSFragment();
        mEmojiBSFragment.setEmojiListener(this);

        mPhotoEditorView = findViewById(R.id.iv_main);
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();
        loadImage();

        // TODO: 10/4/2020 yaha sy text change ho skta ha.  on long press tou yaha sy continue kr lain 
        mPhotoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {
            @Override
            public void onEditTextChangeListener(View rootView, String text, int colorCode) {

                AddTextFragment addTextFragment = new AddTextFragment();
                addTextFragment.setListener(MainActivity.this);
                addTextFragment.changeText(text,colorCode);
                addTextFragment.show(getSupportFragmentManager(), addTextFragment.getTag());
                rootView.setVisibility(View.INVISIBLE);


//                Toast.makeText(MainActivity.this, "touch", Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "onEditTextChangeListener: called");
//                mPhotoEditor.editText(rootView, "text changed", colorCode);
            }

            @Override
            public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
                Log.d(TAG, "onAddViewListener: called");
            }

            @Override
            public void onRemoveViewListener(int numberOfAddedViews) {
                Log.d(TAG, "onRemoveViewListener: called");
            }

            @Override
            public void onStartViewChangeListener(ViewType viewType) {
                Log.d(TAG, "onStartViewChangeListener: called");
            }

            @Override
            public void onStopViewChangeListener(ViewType viewType) {
                Log.d(TAG, "onStopViewChangeListener: called");
            }
        });

        btn_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv_Filters.setVisibility(View.VISIBLE);
            }
        });
        btn_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTextFragment addTextFragment = AddTextFragment.getInstance();
                addTextFragment.setListener(MainActivity.this);
                addTextFragment.show(getSupportFragmentManager(), addTextFragment.getTag());
            }
        });
        iv_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for greater than lolipop versions we need the permissions asked on runtime
                //so if the permission is not available user will go to the screen to allow storage permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                    return;
                }
                // create new Intent
                mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(Bitmap saveBitmap) {
                        try {
                            saveImage(saveBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
        });


        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create new Intent
                mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(Bitmap saveBitmap) {
                        try {
                            shareImage(saveBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });


            }
        });
        btn_Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetControls();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);

            }
        });
        btn_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetControls();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });
        btn_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());

            }
        });
        btn_adjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editImageFragment = EditImageFragment.getInstance();
                editImageFragment.setListener(MainActivity.this);
                editImageFragment.show(getSupportFragmentManager(), editImageFragment.getTag());
            }
        });


    }

    private void loadImage() {
        orignalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main);
        finalBitmap = orignalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mPhotoEditorView.getSource().setImageBitmap(orignalBitmap);

    }

    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;

    }

    private void shareImage(Bitmap bitmap) throws IOException {


        try {
            File file = new File(getApplicationContext().getExternalCacheDir(), File.separator + getResources().getString(R.string.app_name) + ".png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);

            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");

            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    orignalBitmap = photo.copy(Bitmap.Config.ARGB_8888, true);
                    finalBitmap = orignalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    mPhotoEditorView.getSource().setImageBitmap(orignalBitmap);

                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                        orignalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        finalBitmap = orignalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                        mPhotoEditorView.getSource().setImageBitmap(orignalBitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void saveImage(Bitmap bitmap) throws IOException {
        OutputStream outputStream;
        boolean isSaved;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".png");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            outputStream = resolver.openOutputStream(uri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File file = new File(imagesDir, System.currentTimeMillis() + ".png");
            outputStream = new FileOutputStream(file);
        }
        isSaved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
        if (isSaved) {
            Toast.makeText(this, "Download Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onAddTextButtonClick(Typeface typeface, String text, int color) {
        mPhotoEditor.addText(typeface, text, color);
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        resetControls();
        mPhotoEditor.setFilterEffect(photoFilter);
    }


    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);

    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter myfilter = new Filter();
        myfilter.addSubFilter(new BrightnessSubFilter(brightness));
        mPhotoEditorView.getSource().setImageBitmap(myfilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));


    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myfilter = new Filter();
        myfilter.addSubFilter(new SaturationSubfilter(saturation));
        mPhotoEditorView.getSource().setImageBitmap(myfilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
        contrastFinal = contrast;
        Filter myfilter = new Filter();
        myfilter.addSubFilter(new SaturationSubfilter(contrast));
        mPhotoEditorView.getSource().setImageBitmap(myfilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {

        Bitmap bitmap = orignalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));

        finalBitmap = myFilter.processFilter(bitmap);


    }
}