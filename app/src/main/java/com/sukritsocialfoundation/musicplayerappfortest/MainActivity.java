package com.sukritsocialfoundation.musicplayerappfortest;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int DIRECTORY_SELECT_CODE = 102;
    private static final int REQUEST_PERMISSION_CODE = 101;
    private MediaPlayer mediaPlayer;
    private ImageButton playPauseButton;
    private TextView songTitle;
    private List<String> songPaths = new ArrayList<>();
    private RecyclerView recyclerView;
    private int currentSongIndex = 0;

    private boolean isPlaying = false;
    private String currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPauseButton = findViewById(R.id.playPauseButton);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isPlaying) {
                        pauseSong();
                    } else {
                        playSong();
                    }
                } catch (Exception e) {
                    Log.e("PlayPauseButton", "Error playing or pausing song", e);
//                    Toast.makeText(MainActivity.this, "An error occurred while playing or pausing the song", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Please choose the song Directory", Toast.LENGTH_SHORT).show();
                }
            }
        });



        ImageButton prevButton = findViewById(R.id.prevButton);
        ImageButton nextButton = findViewById(R.id.nextButton);

        songTitle = findViewById(R.id.songTitle);
        recyclerView = findViewById(R.id.recyclerView);
        Button selectDirectoryButton = findViewById(R.id.selectDirectoryButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }

        selectDirectoryButton.setOnClickListener(v -> openDirectoryChooser());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        prevButton.setOnClickListener(v -> playPreviousSong());
        nextButton.setOnClickListener(v -> playNextSong());
    }

    private void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            playSongFromList(songPaths.get(currentSongIndex));
        }
    }


    private void playNextSong() {
        if (currentSongIndex < songPaths.size() - 1) {
            currentSongIndex++;
            playSongFromList(songPaths.get(currentSongIndex));
        }
    }


    private void openDirectoryChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, DIRECTORY_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DIRECTORY_SELECT_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    loadMusicFiles(uri);
                } else {
                    Toast.makeText(this, "Failed to select directory", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadMusicFiles(Uri uri) {
        songPaths.clear();
        DocumentFile directory = DocumentFile.fromTreeUri(this, uri);
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (DocumentFile file : directory.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".mp3")) {
                    songPaths.add(file.getUri().toString());
                }
            }
        } else {
            Toast.makeText(this, "Invalid directory", Toast.LENGTH_SHORT).show();
        }

        MusicAdapter adapter = new MusicAdapter(songPaths, this, this::playSongFromList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public void playPauseButtonClicked(View view) {
        if (isPlaying) {
            pauseSong();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                playSongFromList(currentPath);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        }
    }



    private void playSongFromList(String path) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, Uri.parse(path));
            File file = new File(path);
            String fileName = file.getName(); // Get the file name
            int dotIndex = fileName.lastIndexOf("."); // Find the index of the dot (.) before the extension
            if (dotIndex != -1) {
                String songName = fileName.substring(0, dotIndex); // Extract the song name
                songTitle.setText(songName); // Set song name to songTitle TextView
            } else {
                songTitle.setText(fileName); // Set file name to songTitle TextView if no extension found
            }
            playSong();
        } catch (Exception e) {
            // Handle exception when user clicks play/pause button without granting permission
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // You can also log the exception for debugging purposes
            Log.e("Error", "Exception occurred: " + e.getMessage());
        }
    }

    private void playSong() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(songPaths.get(currentSongIndex)));
            songTitle.setText(new File(songPaths.get(currentSongIndex)).getName());
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            isPlaying = true;
        }
    }

    private void pauseSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            isPlaying = false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, play the song
                playSongFromList(currentPath);
            } else {
                // Permission denied, show a dialog or toast
                Toast.makeText(this, "Please grant permission to access storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}


//package com.sukritsocialfoundation.musicplayerappfortest;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import java.util.ArrayList;
//
//public class MainActivity extends AppCompatActivity {
//
//    private MediaPlayer mediaPlayer;
//    private ImageButton playPauseButton;
//    private TextView songTitle;
//    private int currentSongIndex = 0;
//    private ArrayList<Integer> songList = new ArrayList<>();
//    private boolean isPlaying = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        playPauseButton = findViewById(R.id.playPauseButton);
//        songTitle = findViewById(R.id.songTitle);
//        ImageButton nextButton = findViewById(R.id.nextButton);
//        ImageButton prevButton = findViewById(R.id.prevButton);
//
//        // Add your song resources to the songList
//        songList.add(R.raw.a);
//        songList.add(R.raw.abb);
////        songList.add(R.raw.song3);
//
//        // Set up the MediaPlayer with the first song
//        mediaPlayer = MediaPlayer.create(this, songList.get(currentSongIndex));
//        songTitle.setText("Song " + (currentSongIndex + 1));
//
//        // Play/Pause button functionality
//        playPauseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isPlaying) {
//                    pauseSong();
//                } else {
//                    playSong();
//                }
//            }
//        });
//
//        // Next button functionality
//        nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nextSong();
//            }
//        });
//
//        // Previous button functionality
//        prevButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                previousSong();
//            }
//        });
//    }
//
//    private void playSong() {
//        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//            isPlaying = true;
//            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
//        }
//    }
//
//    private void pauseSong() {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//            isPlaying = false;
//            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
//        }
//    }
//
//    private void nextSong() {
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            currentSongIndex = (currentSongIndex + 1) % songList.size();
//            mediaPlayer = MediaPlayer.create(this, songList.get(currentSongIndex));
//            songTitle.setText("Song " + (currentSongIndex + 1));
//            playSong();
//        }
//    }
//
//    private void previousSong() {
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
//            mediaPlayer = MediaPlayer.create(this, songList.get(currentSongIndex));
//            songTitle.setText("Song " + (currentSongIndex + 1));
//            playSong();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        super.onDestroy();
//    }
//}
