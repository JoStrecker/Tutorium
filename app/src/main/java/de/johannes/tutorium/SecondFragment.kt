package de.johannes.tutorium

import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import de.johannes.tutorium.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        binding.saveBtn.setOnClickListener {
            addStudent()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addStudent(){
        val dbHelper = StudentReaderContract.StudentReaderDbHelper(requireContext())

        val name = binding.editTextTextPersonName.text.toString()
        val surName = binding.editTextTextPersonName2.text.toString()
        val semester = binding.editTextNumberSigned.text.toString().toIntOrNull()

        // Gets the data repository in write mode
        val db = dbHelper.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(StudentReaderContract.StudentEntry.COLUMN_NAME_NAME, name)
            put(StudentReaderContract.StudentEntry.COLUMN_NAME_SURNAME, surName)
            put(StudentReaderContract.StudentEntry.COLUMN_NAME_SEMESTER, semester ?: 1)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(StudentReaderContract.StudentEntry.TABLE_NAME, null, values)

        binding.editTextTextPersonName.text.clear()
        binding.editTextTextPersonName2.text.clear()
        binding.editTextNumberSigned.text.clear()

        Toast.makeText(context, "Student added succesfully!", Toast.LENGTH_SHORT).show()
    }
}