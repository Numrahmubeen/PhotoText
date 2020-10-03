package com.jobs.phototext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;


public class MainActivity extends AppCompatActivity implements AddTextFragmentListener, FilterListener ,
        EmojiBSFragment.EmojiListener,
EditImageFragmentListener{

    ImageView iv_Save,iv_Gallery,iv_Camera,iv_Text,iv_share,iv_filters,iv_emoji,iv_adjust;

    PhotoEditor mPhotoEditor;
    PhotoEditorView mPhotoEditorView;
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    RecyclerView rv_Filters;
    private EmojiBSFragment mEmojiBSFragment;
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);

    EditImageFragment editImageFragment;
    int brightnessFinal=0;
    float saturationFinal=1.0f;
    float contrastFinal=1.0f;
    Bitmap orignalBitmap,finalBitmap;

static
{
    System.loadLibrary("NativeImageProcessor");
}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_Save=findViewById(R.id.iv_save);
        iv_Camera=findViewById(R.id.iv_camera);
        iv_Gallery=findViewById(R.id.iv_gallery);
        iv_Text=findViewById(R.id.iv_Text);
        iv_share=findViewById(R.id.iv_share);
        iv_filters=findViewById(R.id.iv_filter);
        iv_emoji=findViewById(R.id.iv_emoji);
        iv_adjust=findViewById(R.id.iv_adjust);
       rv_Filters=findViewById(R.id.rvFilterView);


        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_Filters.setLayoutManager(llmFilters);
        rv_Filters.setHasFixedSize(true);
        rv_Filters.setAdapter(mFilterViewAdapter);

        mEmojiBSFragment = new EmojiBSFragment();
        mEmojiBSFragment.setEmojiListener(this);

        editImageFragment=new EditImageFragment();
        editImageFragment.setListener(this);


        mPhotoEditorView=findViewById(R.id.iv_main);
        mPhotoEditor=new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();

        iv_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv_Filters.setVisibility(View.VISIBLE);
            }
        });
        iv_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTextFragment addTextFragment=AddTextFragment.getInstance();
                addTextFragment.setListener(MainActivity.this);
                addTextFragment.show(getSupportFragmentManager(),addTextFragment.getTag());
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
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
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


        iv_share.setOnClickListener(new View.OnClickListener() {
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
        iv_Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);

            }
        });
        iv_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });
        iv_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());

            }
        });
        iv_adjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetControls();
                mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(Bitmap saveBitmap) {
                        orignalBitmap=saveBitmap;
                        finalBitmap=saveBitmap;
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
              editImageFragment.show(getSupportFragmentManager(),editImageFragment.getTag());




            }
        });



    }

    private void resetControls() {
    if(editImageFragment!=null  )
    {
        editImageFragment.resetControls();
    }
        brightnessFinal=0;
        saturationFinal=1.0f;
        contrastFinal=1.0f;

    }

    private void shareImage( Bitmap bitmap) throws IOException {


        try {
            File file = new File(getApplicationContext().getExternalCacheDir(), File.separator +getResources().getString(R.string.app_name)+".png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID +".provider", file);

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
                    finalBitmap=photo;
                    orignalBitmap=photo;
                    mPhotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        finalBitmap=bitmap;
                        orignalBitmap=bitmap;
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
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
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
        {
            ContentResolver resolver=getContentResolver();
            ContentValues contentValues=new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,System.currentTimeMillis() + ".png");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri uri=resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
            outputStream=resolver.openOutputStream(uri);
        }
        else
            {
               String imagesDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
               File file=new File(imagesDir,System.currentTimeMillis()+".png");
               outputStream=new FileOutputStream(file);
            }
        isSaved=bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        outputStream.flush();
        outputStream.close();
        if(isSaved)
        {
            Toast.makeText(this, "Download Successful", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public void onAddTextButtonClick(Typeface typeface, String text, int color) {
        mPhotoEditor.addText(typeface,text, color);
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }


    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);

    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal=brightness;
        Filter myfilter =new Filter();
        myfilter.addSubFilter(new BrightnessSubFilter(brightness));
        mPhotoEditorView.getSource().setImageBitmap(myfilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));


    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal=saturation;
        Filter myfilter=new Filter();
        myfilter.addSubFilter(new SaturationSubfilter(saturation));
        mPhotoEditorView.getSource().setImageBitmap(myfilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
     contrastFinal=contrast;
        Filter myfilter=new Filter();
        myfilter.addSubFilter(new SaturationSubfilter(contrast));
        mPhotoEditorView.getSource().setImageBitmap(myfilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onEditStarted() {
    mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
        @Override
        public void onBitmapReady(Bitmap saveBitmap) {
            finalBitmap=saveBitmap;
        }

        @Override
        public void onFailure(Exception e) {

        }
    });
    }

    @Override
    public void onEditCompleted() {

        Bitmap bitmap=orignalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        finalBitmap=myFilter.processFilter(bitmap);


    }
}