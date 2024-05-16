package com.example.musicstream

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicstream.adapter.PlaylistSongsAdapter
import com.example.musicstream.adapter.SongListAdapter
import com.example.musicstream.adapter.SongsListAdapter
import com.example.musicstream.databinding.ActivityPlaylistDetailsBinding
import com.example.musicstream.models.SongModel
import com.google.firebase.firestore.FirebaseFirestore

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDetailsBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playlistName = intent.getStringExtra("playlistName")
        binding.moreInfoPD.text = playlistName

        val playlistId = intent.getStringExtra("playlistId") ?: ""
        Log.d("1111111",playlistId)

        loadPlaylistSongs(playlistId)

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)

        binding.addBtnPD.setOnClickListener {
            val playlistId = intent.getStringExtra("playlistId")
            val playlistName = intent.getStringExtra("playlistName")
            val songId = intent.getStringExtra("songId")
            Log.d("testing", playlistId.toString())
            val intent = Intent(this, SongList::class.java)
            intent.putExtra("playlistId", playlistId)
            intent.putExtra("playlistName", playlistName)
            intent.putExtra("songId", songId)
            startActivity(intent)
        }

    }

    private fun loadPlaylistSongs(playlistId: String) {
        if (playlistId.isNotEmpty()) {
            db.collection("playlists").document(playlistId)
                .get()
                .addOnSuccessListener { document ->
                    val songIdList = document.get("songs") as? List<String>
                    songIdList?.let {
                        Log.d("LoadPlaylistSongs", "Song IDs retrieved: $it")
                        val songs = mutableListOf<SongModel>()
                        for (id in it) {
                            db.collection("songs").document(id)
                                .get()
                                .addOnSuccessListener { songDocument ->
                                    val song = songDocument.toObject(SongModel::class.java)
                                    song?.let { s ->
                                        songs.add(s)
                                        if (songs.size == it.size) {
                                            val songsListAdapter = PlaylistSongsAdapter(songs)
                                            binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
                                            binding.playlistDetailsRV.adapter = songsListAdapter
                                        }
                                    }
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LoadPlaylistSongs", "Error retrieving songs", e)
                }
        } else {
            // Handle the case when playlistId is null or empty
        }
    }
}
