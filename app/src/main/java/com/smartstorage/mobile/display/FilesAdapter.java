package com.smartstorage.mobile.display;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartstorage.mobile.CopyFileToGoogleDriveActivity;
import com.smartstorage.mobile.R;
import com.smartstorage.mobile.db.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kasun on 9/26/17.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.DeletedFilesviewHolder> {
    private List<FileDetail> filesListDetail;
    private boolean isSelectedAll=false;
    private ArrayList filenames=new ArrayList();
    private ViewGroup parent;


    public class DeletedFilesviewHolder extends RecyclerView.ViewHolder{
        public TextView url,drive_link,size;

        public ImageView icon_image;
        public CheckBox checkbox_value;


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
        this.parent=parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deleted_files_list_row, parent, false);

        return new DeletedFilesviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeletedFilesviewHolder holder, int position) {
        final FileDetail fileDetail = filesListDetail.get(position);
        final int pos=position;
        holder.url.setText(fileDetail.getUrl());
        holder.drive_link.setText(fileDetail.getDrive_link());
        holder.size.setText(String.valueOf(fileDetail.getSize()));
        holder.icon_image.setImageResource(fileDetail.getIconImage());
        holder.checkbox_value.setChecked(fileDetail.getCheckboxValue());

        if (!isSelectedAll) holder.checkbox_value.setChecked(false);
        else holder.checkbox_value.setChecked(true);

        holder.checkbox_value.setTag(fileDetail);
        holder.checkbox_value.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        FileDetail fd = (FileDetail) cb.getTag();

                        fd.setCheckboxValue(cb.isChecked());
                        filesListDetail.get(pos).setCheckboxValue(cb.isChecked());

//                        Toast.makeText(
//                                v.getContext(),
//                                "Selected files: " + fd.getUrl() + " is "
//                                        + cb.isChecked(), Toast.LENGTH_LONG).show();
                        Log.d("file name:",fd.getUrl());
                        filenames.add(fd.getUrl());
                    }
                }
        );

    }

    @Override
    public int getItemCount() {
        return filesListDetail.size();
    }

    public void selectAll(){
        Log.d("SelectAll","yes");
        isSelectedAll=true;
        notifyDataSetChanged();
    }

    public void deSelectAll(){
        Log.d("deselectAll","yes");
        isSelectedAll=false;
        notifyDataSetChanged();
    }

    public void copyFiles(){
        Log.d("copy files","yes");
        if(!filenames.isEmpty()){
            Log.i("Files Selected...:",String.valueOf(filenames.size()));
            Intent copyFilesIntent=new Intent(parent.getContext(), CopyFileToGoogleDriveActivity.class);
            copyFilesIntent.putStringArrayListExtra("copyingListToGD",filenames);
            copyFilesIntent.setAction("com.smartStorage.copytoGD");
            parent.getContext().sendBroadcast(copyFilesIntent);
        }
    }
    public void setNeverDelete(){
        Log.d("files Adapter:","updating never deleting status.......");
        if(!filenames.isEmpty()){
            for(int i=0;i<filenames.size();i++){
                DatabaseHandler db=DatabaseHandler.getDbInstance(parent.getContext());
                db.updateNeverDeleteStatus(String.valueOf(filenames.get(i)));

            }
        }

    }

}
