package com.example.brainpath.ui.resources;

import android.annotation.SuppressLint;
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

import android.widget.Button;
import android.widget.Toast;

import com.example.brainpath.R;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResourceDetailsFragment extends Fragment {

    private String title, desc, previewUrl;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back press in the fragment
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_resources_details, container, false);

        // Get arguments passed to this fragment
        if (getArguments() != null) {
            title = getArguments().getString("RESOURCE_TITLE");
            desc = getArguments().getString("RESOURCE_DESC"); // This is the short desc
            previewUrl = getArguments().getString("RESOURCE_PREVIEW_URL");
        }

        // Initialize UI components
        TextView titleView = view.findViewById(R.id.resDetailsTitle);
        TextView descView = view.findViewById(R.id.resDetailsDesc); // Display short desc
        ImageView previewView = view.findViewById(R.id.resDetailsPreview);

        // Set data to UI components
        titleView.setText(title);
        descView.setText(desc); // Set the short desc here

        // Load the image without Glide
        new ImageLoaderTask(previewView).execute(previewUrl);

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Reset the toolbar when exiting the fragment
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                activity.getSupportActionBar().setTitle(R.string.app_name); // Reset title to app name
            }
        }
    }

    // Override onOptionsItemSelected to handle the back button press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.bp_logo_color); // Fallback image in case of failure
            }
        }
    }


}
