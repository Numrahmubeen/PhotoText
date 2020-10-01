package com.jobs.phototext;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

public class DocumentViewer extends AppCompatActivity {

    Button btn_choose,btn_show;
    private static final int PICK_PDF_FILE = 2;
    TextView tv;
    Uri uri=null;
    Integer pageNumber = 0;
    PDFView pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_viewer);

        btn_choose=findViewById(R.id.btn_choose);
        tv=findViewById(R.id.txt);
        btn_show=findViewById(R.id.btn_show);
        pdf=findViewById(R.id.pdfView);

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

                if(uri!=null)
                {

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
                if(uri!=null)
                {
                    Toast.makeText(this, "Get the file"+uri, Toast.LENGTH_SHORT).show();
                    tv.setText("Uri is"+uri);

                }

            }
        }
    }



}