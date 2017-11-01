package com.smartstorage.mobile.display;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartstorage.mobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kasun on 9/26/17.
 */

public class DemoMigrationFilesAdapter extends RecyclerView.Adapter<DemoMigrationFilesAdapter.MigrationFilesviewHolder> {
    private List<FileDetail> filesListDetail;
    private ArrayList filenames = new ArrayList();
    private ViewGroup parent;


    public class MigrationFilesviewHolder extends RecyclerView.ViewHolder {
        public TextView url, drive_link, size;

        public ImageView icon_image;


        public MigrationFilesviewHolder(View view) {
            super(view);
            url = (TextView) view.findViewById(R.id.url);
            drive_link = (TextView) view.findViewById(R.id.migration_val);
            size = (TextView) view.findViewById(R.id.size);
            icon_image = (ImageView) view.findViewById(R.id.icon_image);
        }
    }

    public DemoMigrationFilesAdapter(List<FileDetail> filesListDetail) {
        this.filesListDetail = filesListDetail;
    }

    @Override
    public MigrationFilesviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deleted_files_list_row, parent, false);

        return new MigrationFilesviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MigrationFilesviewHolder holder, int position) {
        final FileDetail fileDetail = filesListDetail.get(position);
        final int pos = position;
        holder.url.setText(fileDetail.getUrl());
        holder.drive_link.setText(fileDetail.getDrive_link());
        holder.size.setText(String.valueOf(fileDetail.getSize()));
        holder.icon_image.setImageResource(fileDetail.getIconImage());

    }

    @Override
    public int getItemCount() {
        return filesListDetail.size();
    }

}
