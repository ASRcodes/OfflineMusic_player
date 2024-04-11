package com.example.sangeet

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sangeet.databinding.ActivityMulistBinding
import com.example.sangeet.databinding.PlayerBinding
import java.io.File

class Adapter(private val songs:List<File>):RecyclerView.Adapter<Adapter.ViewHolder>() {
    inner class ViewHolder(var binding: PlayerBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlayerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return songs.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val songFile = songs[position]
        holder.binding.musicName.text = songs[position].nameWithoutExtension
        holder.binding.clickToPlay.setOnClickListener {
            val intent = Intent(holder.itemView.context,Play::class.java).apply {
                putExtra("songPath",songFile.absolutePath)
                putExtra("songTitle",songFile.nameWithoutExtension)
//                Song list
                putExtra("songList",ArrayList(songs.map { it.absolutePath }))
//                Passing the position of currentSong
                putExtra("currentSongIndex",position)
            }
            holder.itemView.context.startActivity(intent)

        }
    }
}