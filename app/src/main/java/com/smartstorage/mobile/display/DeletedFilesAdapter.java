package com.smartstorage.mobile.display;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartstorage.mobile.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by kasun on 9/26/17.
 */

public class DeletedFilesAdapter extends RecyclerView.Adapter<DeletedFilesAdapter.DeletedFilesviewHolder> {
    private List<DeletedFile> deletedFilesList;


    public class DeletedFilesviewHolder extends RecyclerView.ViewHolder{
        public TextView url,drive_link,size;

        public DeletedFilesviewHolder(View view) {
            super(view);
            url=(TextView)view.findViewById(R.id.url);
            drive_link=(TextView)view.findViewById(R.id.drive_link);
            size=(TextView)view.findViewById(R.id.size);
        }
    }

    public DeletedFilesAdapter(List<DeletedFile> deletedFilesList){
        this.deletedFilesList=deletedFilesList;
    }
    @Override
    public DeletedFilesviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deleted_files_list_row, parent, false);

        return new DeletedFilesviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeletedFilesviewHolder holder, int position) {
        DeletedFile deletedFile=deletedFilesList.get(position);
        holder.url.setText(deletedFile.getUrl());
        holder.drive_link.setText(deletedFile.getDrive_link());
        holder.size.setText(String.valueOf(deletedFile.getSize()));

    }

    @Override
    public int getItemCount() {
        return deletedFilesList.size();
    }

}
