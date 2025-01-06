package com.example.brainpath.ui.resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.brainpath.R;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailsFragment extends Fragment {

    private ResourcesViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_resources_details, container, false);

        // Retrieve arguments passed from ResourcesFragment
        String title = null;
        String desc = null;
        String previewUrl = null;

        if (getArguments() != null) {
            title = getArguments().getString("RESOURCE_TITLE");
            desc = getArguments().getString("RESOURCE_DESC");
            previewUrl = getArguments().getString("RESOURCE_PREVIEW_URL");
        }

        // Initialize UI components
        TextView titleView = view.findViewById(R.id.resDetailsTitle);
        TextView descView = view.findViewById(R.id.resDetailsDesc);
        ImageView previewView = view.findViewById(R.id.resDetailsPreview);

        // Set the data to the UI components
        titleView.setText(title);
        descView.setText(desc);

        // Load the preview image (if available)
        if (previewUrl != null) {
            new ImageLoaderTask(previewView).execute(previewUrl);
        }

        return view;
    }

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
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.chat_textbox); // Fallback image
            }
        }
    }

}
