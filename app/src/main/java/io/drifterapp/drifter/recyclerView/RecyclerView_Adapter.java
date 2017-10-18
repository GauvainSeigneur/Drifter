package io.drifterapp.drifter.recyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import io.drifterapp.drifter.Audio;
import io.drifterapp.drifter.R;

/**
 * Created by gse on 18/10/2017.
 */

public class RecyclerView_Adapter extends RecyclerView.Adapter<ViewHolder> {

    List<Audio> list = Collections.emptyList();
    Context context;
    int visibleItem =4;
    public int num = 1;
    public int displaySize;

    public RecyclerView_Adapter(List<Audio> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(list.get(position).getTitle());
        holder.cover.setImageURI(list.get(position).getUriImage());
        holder.duration.setText(list.get(position).getDuration());
        list.get(position).getBitmap();
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        //return list.size();
        if(displaySize > list.size())
            return list.size();
        else
            return displaySize;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void setDisplayCount(int numberOfEntries) {
        displaySize = numberOfEntries;
        notifyDataSetChanged();

    }

}

class ViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    ImageView play_pause;
    ImageView cover;
    TextView duration;

    ViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        play_pause = (ImageView) itemView.findViewById(R.id.play_pause);
        cover = (ImageView) itemView.findViewById(R.id.cover);
        duration = (TextView) itemView.findViewById(R.id.duration);
    }
}