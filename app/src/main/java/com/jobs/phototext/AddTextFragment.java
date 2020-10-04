package com.jobs.phototext;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.veritas1.verticalslidecolorpicker.VerticalSlideColorPicker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;



public class AddTextFragment extends BottomSheetDialogFragment implements FontAdapter.FontAdapterClickListener{

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

        final VerticalSlideColorPicker colorPicker = (VerticalSlideColorPicker) itemView.findViewById(R.id.color_picker);

        colorPicker.setOnColorChangeListener(new VerticalSlideColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChange(int selectedColor) {

               if(selectedColor!=0)
               {
                   colorSelected=selectedColor;
               et_addText.setTextColor(selectedColor);
               }
            }
        });

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

    @Override
        public void OnFontSelected(String fontName) {
        typefaceSelected= Typeface.createFromAsset(getContext().getAssets(),new StringBuilder("fonts/")
                .append(fontName).toString());


        }

}