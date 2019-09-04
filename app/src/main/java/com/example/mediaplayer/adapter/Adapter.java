package com.example.mediaplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.model.Audio;

import java.util.List;

public class Adapter  extends RecyclerView.Adapter<Adapter.MyViewHolder>{

    private LayoutInflater inflater;
    private List<Audio> list;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
           void onSongplay(int position);
    }

    public void setOnSongListener(OnItemClickListener onFavouriteClickListener) {
        mListener = onFavouriteClickListener;
    }

    public Adapter(Context ctx, List<Audio> objects) {
        inflater = LayoutInflater.from(ctx);
        list = objects;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view,mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        private MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                    {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                        {
                            listener.onSongplay(position);
                        }
                    }
                }
            });

        }
    }
}
