package com.jobs.phototext;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontViewHolder> {

    Context context;
    FontAdapterClickListener listener;
    List<String> fontList;
    int row_selected=-1;

    public FontAdapter(Context context, FontAdapterClickListener listener) {
        this.context = context;
        this.listener = listener;
        fontList=loadFontList();
    }

    private List<String> loadFontList() {
        List<String> result=new ArrayList<>();
        result.add("Cheque-Black.otf");
        result.add("Cheque-Regular.otf");
        return result;


    }

    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.font_item,parent,false);

        return new FontViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder holder, int position) {

        if (row_selected == position)
            holder.iv_Check.setVisibility(View.VISIBLE);
        else
            holder.iv_Check.setVisibility(View.INVISIBLE);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), new StringBuilder("fonts/")
                .append(fontList.get(position)).toString());
        String fontName = fontList.get(position).toString();
        fontName = fontName.substring(0, fontName.length() - 4);
        holder.tv_FontName.setText(fontName);
        holder.tv_FontDemo.setTypeface(typeface);
    }

        @Override
        public int getItemCount () {
            return fontList.size();
        }

    public class FontViewHolder extends RecyclerView.ViewHolder {
        TextView tv_FontName,tv_FontDemo;
        ImageView iv_Check;
        public FontViewHolder(View itemView)
        {
            super(itemView);
            tv_FontName=itemView.findViewById(R.id.txt_Fontname);
            tv_FontDemo=itemView.findViewById(R.id.txt_Fontdemo);

            iv_Check=itemView.findViewById(R.id.imgChk);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnFontSelected(fontList.get(getAdapterPosition()));
                    row_selected=getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }
    public interface FontAdapterClickListener
    {
        public void OnFontSelected(String fontName);
    }
}
