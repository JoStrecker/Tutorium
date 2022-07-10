package de.johannes.tutorium

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.set
import de.johannes.tutorium.databinding.FragmentThirdBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ThirdFragment : Fragment() {

    private var _binding: FragmentThirdBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdBinding.inflate(inflater, container, false)
        binding.editTitle.setText(getName())
        binding.saveBtn.setOnClickListener {
            saveName(binding.editTitle.text.toString())
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getName() : String{
        val sharedPref = activity?.getSharedPreferences("name", Context.MODE_PRIVATE)
        return sharedPref?.getString("nameKey", "") ?: ""
    }

    private fun saveName(name: String){
        val sharedPref = activity?.getSharedPreferences("name", Context.MODE_PRIVATE)
        with(sharedPref?.edit()){
            this?.putString("nameKey", name)
            this?.apply()
        }
    }
}