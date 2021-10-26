package com.example.musicapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentGaleryBinding

class GalleryFragment : Fragment(R.layout.fragment_galery) {
    private var _binding: FragmentGaleryBinding? = null
private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGaleryBinding.inflate(inflater,container,false)
        return binding.root
    }
}