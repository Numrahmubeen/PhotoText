package com.jobs.phototext;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static java.lang.System.in;

public class DocumentViewer extends AppCompatActivity {

    Button btn_choose, btn_show;
    private static final int PICK_PDF_FILE = 2;
    TextView tv;
    Uri uri = null;
    Integer pageNumber = 0;
    PDFView pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_viewer);

        btn_choose = findViewById(R.id.btn_choose);
        tv = findViewById(R.id.txt);
        btn_show = findViewById(R.id.btn_show);
        pdf = findViewById(R.id.pdfView);

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");

                startActivityForResult(intent, PICK_PDF_FILE);
            }
        });
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (uri != null) {

                    readTextFromUri(uri);

                }
            }
        });

    }

    private void readTextFromUri(Uri uri) {
        pdf.fromUri(uri).defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            if (resultData != null) {
                uri = resultData.getData();
                if (uri != null) {
                    Toast.makeText(this, "Get the file" + uri, Toast.LENGTH_SHORT).show();
                    tv.setText("Uri is" + uri);
                    //extractPdfFile(uri);
                }

            }
        }
    }

    private static final String TAG = "DocumentViewer";

    InputStream inputStream;

//    public void extractPdfFile(final Uri uri) {
//        //progress_rl.setVisibility(View.VISIBLE);
//        try {
//            inputStream = this.getContentResolver().openInputStream(uri);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    String fileContent = "";
//                    StringBuilder builder = new StringBuilder();
//                    PdfReader reader = new PdfReader(inputStream);
//                    int n = reader.getNumberOfPages();
//                    for (int i = 1; i <= n; i++) {
//                        fileContent = PdfTextExtractor.getTextFromPage(reader, i);
//                        builder.append(fileContent);
//                    }
//                    reader.close();
//                    Log.d(TAG, "run: " + builder.toString());
//                } catch (Exception e) {
//                    Log.d(TAG, "extractPdfFile: " + e.getMessage());
//                }
//            }
//        }).start();
//
//    }
}