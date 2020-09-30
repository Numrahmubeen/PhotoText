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

import java.util.ArrayList;
import java.util.List;



public class AddTextFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListener,FontAdapter.FontAdapterClickListener{

    AddTextFragmentListener listener;
    EditText et_addText;
    RecyclerView rv_font,rv_color;
    Button btn_done;
    Typeface typefaceSelected=Typeface.DEFAULT;
    int  REQUEST_CODE=11;
    int colorSelected=Color.parseColor("#000000");


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
        rv_color=itemView.findViewById(R.id.rv_Color);
        rv_color.setHasFixedSize(true);
        rv_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        ColorAdapter adp=new ColorAdapter(getContext(),genColorList(),this);
        rv_color.setAdapter(adp);



        final Fragment fragment=new AddTextFragment();

        rv_font=itemView.findViewById(R.id.rv_Fonts);
        rv_font.setHasFixedSize(true);
        rv_font.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        FontAdapter adapter=new FontAdapter(getContext(),this);
        rv_font.setAdapter(adapter);


         btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().beginTransaction().remove(AddTextFragment.this).commit();
                listener.onAddTextButtonClick(typefaceSelected,et_addText.getText().toString(),  colorSelected);
                et_addText.setText("");

            }
        });

        return itemView;
    }

    private List<Integer> genColorList() {
        List<Integer> colorList=new ArrayList<>();

        colorList.add(Color.parseColor("#e9a6af"));
        colorList.add(Color.parseColor("#ff9aa3"));
        colorList.add(Color.parseColor("#ccc2cb"));
        colorList.add(Color.parseColor("#f7af89"));
        colorList.add(Color.parseColor("#31ecd0"));
        colorList.add(Color.parseColor("#ff7792"));
        colorList.add(Color.parseColor("#ff46b6"));
        colorList.add(Color.parseColor("#9662eb"));
        colorList.add(Color.parseColor("#ffb3ba"));
        colorList.add(Color.parseColor("#E03C31"));
        colorList.add(Color.parseColor("#0093AF"));
        colorList.add(Color.parseColor("#8A3324"));
        colorList.add(Color.parseColor("#602F6B"));
        colorList.add(Color.parseColor("#9F00FF"));

        return colorList;
    }

    @Override
        public void OnFontSelected(String fontName) {
        typefaceSelected= Typeface.createFromAsset(getContext().getAssets(),new StringBuilder("fonts/")
                .append(fontName).toString());


        }

    @Override
    public void onColorSelected(int color) {
        colorSelected=color;
        et_addText.setTextColor(color);
    }
}