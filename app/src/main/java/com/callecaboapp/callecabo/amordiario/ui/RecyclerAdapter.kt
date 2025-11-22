package com.callecaboapp.callecabo.amordiario.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callecaboapp.callecabo.amordiario.databinding.ItemFavoritosBinding

class RecyclerAdapter(private val chistes: List<String>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoritosBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chistes[position])
    }

    override fun getItemCount() = chistes.size


    class ViewHolder(private val binding: ItemFavoritosBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chiste: String) {
            binding.textoChisteFavorito.text = chiste
        }
    }
}