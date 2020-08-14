package com.xm.vlcdemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xm.vlcdemo.Data.ImageData;
import com.xm.vlcdemo.R;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    Context context;
    ArrayList<ImageData>imageDataArrayList;
    LayoutInflater layoutInflater;
    public GridAdapter(Context con,ArrayList<ImageData>ilist){
        context=con;
        imageDataArrayList=ilist;
        layoutInflater= (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return imageDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent
    ) {
        ImageData imageData=imageDataArrayList.get(position);
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=layoutInflater.inflate(R.layout.item_grid,null);
            viewHolder.imageView=(ImageView)convertView.findViewById(R.id.iv_image);
            viewHolder.imageView.setImageBitmap(imageData.getThumbnail());
            convertView.setTag(viewHolder);
        }else{
           viewHolder=(ViewHolder)convertView.getTag();
        }

        return convertView;
    }

    public class ViewHolder
    { ImageView imageView;


    }
}
