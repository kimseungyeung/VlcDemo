package com.xm.vlcdemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xm.vlcdemo.Data.FileData;
import com.xm.vlcdemo.R;

import java.util.ArrayList;

public class FilelistAdapter extends RecyclerView.Adapter<FilelistAdapter.ViewHolder> {
    LayoutInflater layoutInflater;
    ArrayList<FileData>fileDatalist;
    Context context;
    private OnItemClicklistener listener=null;
    public FilelistAdapter(Context con,ArrayList<FileData>flist){
        context =con;
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fileDatalist=flist;
    }
    public interface  OnItemClicklistener{
        void onitemclick(View v,int position);
    }
    public void setonitemclicklistener(OnItemClicklistener slistener){
        this.listener=slistener;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view=layoutInflater.inflate(R.layout.item_filelist,null);
       View view= layoutInflater.from(parent.getContext()).inflate(R.layout.item_filelist,parent,false);

        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        FileData fdata=fileDatalist.get(position);
        if(fdata.getThumnail()!=null) {
            holder.iv_file.setImageBitmap(fdata.getThumnail());
        }
    holder.tv_file.setText(fdata.getFname());

    }

    @Override
    public int getItemCount() {
        return fileDatalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_file;
        ImageView iv_file;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_file=itemView.findViewById(R.id.tv_file);
            iv_file=itemView.findViewById(R.id.iv_file);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    listener.onitemclick(view,position);
                }
            });
        }
    }

    public void setFileDatalist(ArrayList<FileData> fileDatalist) {
        this.fileDatalist = fileDatalist;
        notifyDataSetChanged();
    }
}
