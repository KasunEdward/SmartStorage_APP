package com.smartstorage.mobile.display;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartstorage.mobile.R;

import java.util.List;

/**
 * Created by kasun on 9/26/17.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.DeletedFilesviewHolder> {
    private List<FileDetail> filesListDetail;
    private boolean isSelectedAll=false;


    public class DeletedFilesviewHolder extends RecyclerView.ViewHolder{
        public TextView url,drive_link,size;

        public ImageView icon_image;
        public Checkable checkbox_value;

        public DeletedFilesviewHolder(View view) {
            super(view);
            url=(TextView)view.findViewById(R.id.url);
            drive_link=(TextView)view.findViewById(R.id.drive_link);
            size=(TextView)view.findViewById(R.id.size);
            icon_image=(ImageView)view.findViewById(R.id.icon_image);
            checkbox_value=(CheckBox)view.findViewById(R.id.checkbox_value);
        }
    }

    public FilesAdapter(List<FileDetail> filesListDetail){
        this.filesListDetail = filesListDetail;
    }
    @Override
    public DeletedFilesviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deleted_files_list_row, parent, false);

        return new DeletedFilesviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeletedFilesviewHolder holder, int position) {
        FileDetail fileDetail = filesListDetail.get(position);
        holder.url.setText(fileDetail.getUrl());
        holder.drive_link.setText(fileDetail.getDrive_link());
        holder.size.setText(String.valueOf(fileDetail.getSize()));
        holder.icon_image.setImageResource(fileDetail.getIconImage());
        holder.checkbox_value.setChecked(fileDetail.getCheckboxValue());

        if (!isSelectedAll) holder.checkbox_value.setChecked(false);
        else holder.checkbox_value.setChecked(true);

    }

    @Override
    public int getItemCount() {
        return filesListDetail.size();
    }

    public void selectAll(){
        Log.d("onClickSelectAll","yes");
        isSelectedAll=true;
        notifyDataSetChanged();
    }

    public void deSelectAll(){
        Log.d("onClickSelectAll","yes");
        isSelectedAll=false;
        notifyDataSetChanged();
    }

}
