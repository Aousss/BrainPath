package com.example.brainpath.ui.resources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResourcesAdapter extends RecyclerView.Adapter<ResourcesAdapter.ResourcesViewHolder> {

    private Context context;
    private List<Resources> resourcesList;
    private OnItemClickListener onItemClickListener;

    // Constructor
    public ResourcesAdapter(Context context, List<Resources> resourcesList, OnItemClickListener listener) {
        this.context = context;
        this.resourcesList = resourcesList;
        this.onItemClickListener = listener;

    }

    @Override
    public ResourcesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.resources, parent, false);
        return new ResourcesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ResourcesViewHolder holder, int position) {
        Resources resources = resourcesList.get(position);
        holder.resTitle.setText(resources.getResTitle());
        holder.resDesc.setText(resources.getFullDesc()); // Show fullDesc in the list

        // Load the preview image
        new ImageLoaderTask(holder.resPreview).execute(resources.getResPreview());

        // Set the click listener
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(resources));
    }


    // New method to update the resources list
    public void updateResources(List<Resources> newResources) {
        resourcesList.clear();          // Clear the old list
        resourcesList.addAll(newResources);  // Add new resources
        notifyDataSetChanged();         // Notify RecyclerView to refresh
    }

    @Override
    public int getItemCount() {
        return resourcesList.size();
    }


    // ViewHolder for the Resources item
    public static class ResourcesViewHolder extends RecyclerView.ViewHolder {
        TextView resTitle, resDesc;
        ImageView resPreview;

        public ResourcesViewHolder(View itemView) {
            super(itemView);
            resTitle = itemView.findViewById(R.id.resTitle);
            resDesc = itemView.findViewById(R.id.resDesc);
            resPreview = itemView.findViewById(R.id.resPreview);
        }
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(Resources resources);
    }


    // AsyncTask to load images from a URL
    private static class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        public ImageLoaderTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                Log.e("ImageLoaderTask", "Failed to load image", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.bp_logo_color); // Fallback image
            }
        }
    }
}
