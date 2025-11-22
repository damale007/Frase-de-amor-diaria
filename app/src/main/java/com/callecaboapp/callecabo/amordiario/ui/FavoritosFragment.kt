package com.callecaboapp.callecabo.amordiario.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.callecaboapp.callecabo.amordiario.HandlerSQLite
import com.callecaboapp.callecabo.amordiario.R
import com.callecaboapp.callecabo.amordiario.databinding.FragmentFavoritosBinding

class FavoritosFragment : Fragment() {
    private var _binding: FragmentFavoritosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val baseDatosSQL = HandlerSQLite(requireActivity())
        val db = baseDatosSQL.writableDatabase
        baseDatosSQL.inicia(db)

        val favoritos = baseDatosSQL.select(null)

        val total = baseDatosSQL.count()
        val items = mutableListOf<String>()

        if (total == 0) {
            Toast.makeText(requireContext(), getString(R.string.noFavoritos), Toast.LENGTH_SHORT).show()
        }

        for (i in 0 until total) {
            items.add(i, favoritos?.get(i)?.get(2) ?: "")
        }

        // Asignar el adaptador al ListView
        binding.recyclerView.adapter = RecyclerAdapter(items)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}