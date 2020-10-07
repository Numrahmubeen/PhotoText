package com.jobs.phototext.fragments;

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
import com.jobs.phototext.listener.AddTextFragmentListener;
import com.jobs.phototext.R;
import com.jobs.phototext.adapter.FontAdapter;


public class AddTextFragment extends BottomSheetDialogFragment implements FontAdapter.FontAdapterClickListener {

    AddTextFragmentListener listener;

    // TODO: 10/4/2020 second change 
    private EditText et_addText;
    private RecyclerView rv_font;
    private Button btn_done;
    private Typeface typefaceSelected = Typeface.DEFAULT;
    int REQUEST_CODE = 11;
    int colorSelected = Color.parseColor("#000000");
    private String textToBeChanged;
    private int textColortobeChanged;
    private Boolean checkTextChange = false;


    // TODO: 10/4/2020 third change
    public static AddTextFragment getInstance() {
        if(instance==null)
            instance=new AddTextFragment();
        return instance;
    }

    // TODO: 10/4/2020 fourth change
    static AddTextFragment instance;

    public void setListener(AddTextFragmentListener listener) {
        this.listener = listener;
    }

    public AddTextFragment() {
        // Required empty public constructor
    }

    public void changeText(String text,int color)
    {
        textToBeChanged = text;
        textColortobeChanged = color;
        checkTextChange = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_add_text, container, false);
        et_addText = itemView.findViewById(R.id.et_addText);
        btn_done = itemView.findViewById(R.id.btn_Done);

        final VerticalSlideColorPicker colorPicker = (VerticalSlideColorPicker) itemView.findViewById(R.id.color_picker);
        colorPicker.setOnColorChangeListener(new VerticalSlideColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChange(int selectedColor) {

                if (selectedColor != 0) {
                    colorSelected = selectedColor;
                    et_addText.setTextColor(selectedColor);
                }
            }
        });

        rv_font = itemView.findViewById(R.id.rv_Fonts);
        rv_font.setHasFixedSize(true);
        rv_font.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        FontAdapter adapter = new FontAdapter(getContext(), this);
        rv_font.setAdapter(adapter);

        if (checkTextChange == true) {
            colorSelected = textColortobeChanged;
            et_addText.setText(textToBeChanged);
            et_addText.setTextColor(textColortobeChanged);
        }

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().beginTransaction().remove(AddTextFragment.this).commit();
                listener.onAddTextButtonClick(typefaceSelected, et_addText.getText().toString(), colorSelected);
                et_addText.setText("");

            }
        });

        return itemView;
    }

    @Override
    public void OnFontSelected(String fontName) {
        typefaceSelected = Typeface.createFromAsset(getContext().getAssets(), new StringBuilder("fonts/")
                .append(fontName).toString());
    }

}