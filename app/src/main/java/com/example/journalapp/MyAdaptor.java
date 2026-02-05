package com.example.journalapp;

import static androidx.core.content.ContextCompat.startActivity;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.MyViewHolder> {


    //variables
    private Context context;
    private List<Journal> journalList;

    public MyAdaptor(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.journal_row,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Journal currentJournal=journalList.get(position);
        holder.title.setText(currentJournal.getTitle());
        holder.thoughts.setText(currentJournal.getThoughts());
        holder.name.setText(currentJournal.getUserName());

        String imgUrl=currentJournal.getImageUrl();
        String timeAgo=(String) DateUtils.getRelativeTimeSpanString(currentJournal.getTimeAdded().getSeconds()*1000);

        holder.dateAdded.setText(timeAgo);
        //Glide for diplaying image
        Glide.with(context)
                .load(imgUrl)
                .fitCenter()

                .into(holder.image);


    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    //View Holder
    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView title,thoughts,dateAdded,name;
        public ImageView image,shareButton;
        public String userId,username;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.journal_title_list);
            thoughts=itemView.findViewById(R.id.journal_thought_list);
            dateAdded=itemView.findViewById(R.id.journal_timestamp_list);
            image=itemView.findViewById(R.id.journal_image_list);
            name=itemView.findViewById(R.id.journal_row_username);
            shareButton=itemView.findViewById(R.id.journal_row_share_button);

            shareButton.setOnClickListener(v->{
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this Android app tutorial 👇\nhttps://github.com/");
                context.startActivity(Intent.createChooser(shareIntent, "Share via"));

            });
        }
    }
}
