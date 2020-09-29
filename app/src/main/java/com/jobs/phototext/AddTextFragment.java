package com.jobs.phototext;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import yuku.ambilwarna.AmbilWarnaDialog;


public class AddTextFragment extends BottomSheetDialogFragment implements FontAdapter.FontAdapterClickListener{

    AddTextFragmentListener listener;
    EditText et_addText;
    RecyclerView rv_font;
    Button btn_done,btn_ChooseColor;
    Typeface typefaceSelected=Typeface.DEFAULT;
    int  REQUEST_CODE=11;
   int initialColor=Color.parseColor("#B71C1C");
   int color;


    public static AddTextFragment getInstance() {
        if(instance==null)
            instance=new AddTextFragment();
        return instance;
    }

    static AddTextFragment instance;

    public void setListener(AddTextFragmentListener listener) {
        this.listener = listener;
    }

    public AddTextFragment() {
        // Required empty public constructor
    }


    public static AddTextFragment newInstance(String param1, String param2) {
        AddTextFragment fragment = new AddTextFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView=inflater.inflate(R.layout.fragment_add_text, container, false);
        et_addText=itemView.findViewById(R.id.et_addText);
        btn_done=itemView.findViewById(R.id.btn_Done);
        btn_ChooseColor=itemView.findViewById(R.id.btn_choose_color);

        final Fragment fragment=new AddTextFragment();

        rv_font=itemView.findViewById(R.id.rv_Fonts);
        rv_font.setHasFixedSize(true);
        rv_font.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        FontAdapter adp=new FontAdapter(getContext(),this);
        rv_font.setAdapter(adp);
       color= -16777216;

        btn_ChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// initialColor is the initially-selected color to be shown in the rectangle on the left of the arrow.
// for example, 0xff000000 is black, 0xff0000ff is blue. Please be aware of the initial 0xff which is the alpha.
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int colorr) {
                       color=colorr;
btn_ChooseColor.setBackgroundColor(colorr);
                    }
                });

                        dialog.show();


            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().beginTransaction().remove(AddTextFragment.this).commit();
                listener.onAddTextButtonClick(typefaceSelected,et_addText.getText().toString(),  color);

            }
        });

        return itemView;
    }

        @Override
        public void OnFontSelected(String fontName) {
        typefaceSelected= Typeface.createFromAsset(getContext().getAssets(),new StringBuilder("fonts/")
                .append(fontName).toString());


        }

}