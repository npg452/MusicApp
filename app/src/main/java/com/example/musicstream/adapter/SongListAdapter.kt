package com.example.musicstream.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstream.MyExoplayer
import com.example.musicstream.PlayerActivity
import com.example.musicstream.SongsListActivity
import com.example.musicstream.databinding.SongListItemRecyclerRowBinding
import com.example.musicstream.models.SongModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SongListAdapter(private val songIdList: List<String>, private val playlistId: String) :
    RecyclerView.Adapter<SongListAdapter.SongViewHolder>(){
    private val db = FirebaseFirestore.getInstance()
    class SongViewHolder(private val binding: SongListItemRecyclerRowBinding, private val db: FirebaseFirestore) : RecyclerView.ViewHolder(binding.root){
        fun bindData(songId: String, playlistId: String){
            db.collection("songs")
                .document(songId).get()
                .addOnSuccessListener {
                    val song = it.toObject(SongModel::class.java)
                    song?.apply {
                        binding.songTitleTextView.text = title
                        binding.songSubtitleTextView.text = subtitle
                        Glide.with(binding.songCoverImageView).load(coverUrl)
                            .apply(
                                RequestOptions().transform(RoundedCorners(32)) // bo tro goc anh
                            )
                            .into(binding.songCoverImageView)
                        binding.root.setOnClickListener {
                            addToPlaylist(songId, playlistId)
                        }
                    }
                }
        }
        private fun addToPlaylist(songId: String, playlistId: String) {
            db.collection("playlists").document(playlistId)
                .update("songs", FieldValue.arrayUnion(songId))
                .addOnSuccessListener {
                    // Song added to playlist
                }
                .addOnFailureListener { e ->
                    // Log the exception or show some feedback to the user
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongListAdapter.SongViewHolder {
        val binding = SongListItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongListAdapter.SongViewHolder(binding, db)
    }

    override fun getItemCount(): Int {
        return songIdList.size
    }

    override fun onBindViewHolder(holder: SongListAdapter.SongViewHolder, position: Int) {
        holder.bindData(songIdList[position], playlistId)
    }
}