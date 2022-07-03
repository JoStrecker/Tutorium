package de.johannes.tutorium

import android.app.AlertDialog
import android.os.Bundle
import android.provider.BaseColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import de.johannes.tutorium.databinding.FragmentFirstBinding
import de.johannes.tutorium.StudentReaderContract.StudentReaderDbHelper
import de.johannes.tutorium.StudentReaderContract.StudentEntry

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        binding.studentList.adapter = StudentAdapter(requireContext(), getDataMap())
        binding.studentList.setOnItemLongClickListener { adapterView, _, i, _ ->
            showDialog(adapterView.adapter.getItemId(i).toInt())
            true
        }
        binding.studentList.setOnItemClickListener { adapterView, _, i, _ ->
            Toast.makeText(context, "${adapterView.adapter.getItem(i)}", Toast.LENGTH_SHORT).show()
        }

        binding.floatingActionButton.setOnClickListener { view ->
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDataMap() : Map<Int, String>{
        val db = StudentReaderDbHelper(requireContext()).readableDatabase

        val cursor = db.query(
            StudentEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${StudentEntry.COLUMN_NAME_SURNAME} ASC"
        )

        val students = mutableMapOf<Int, String>()
        with(cursor) {
            while (moveToNext()) {
                val primaryKey = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                val name = getString(getColumnIndexOrThrow(StudentEntry.COLUMN_NAME_NAME))
                val surname = getString(getColumnIndexOrThrow(StudentEntry.COLUMN_NAME_SURNAME))
                val semester = getString(getColumnIndexOrThrow(StudentEntry.COLUMN_NAME_SEMESTER))
                students.put(primaryKey, "$name $surname | Semester $semester")
            }
        }
        cursor.close()
        db?.close()

        return students
    }

    private fun showDialog(id: Int){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Alert")
        builder.setMessage("Do you really want to delete this student?")

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            removeStudent(id)
            dialog.dismiss()
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun removeStudent(id: Int){
        val db = StudentReaderDbHelper(requireContext()).writableDatabase

        val where = "_id = ?"
        val whereArgs = arrayOf(id.toString())

        val rows = db?.delete(
            StudentEntry.TABLE_NAME,
            where,
            whereArgs
        )

        if((rows ?: 0) > 0){
            binding.studentList.adapter = StudentAdapter(requireContext(), getDataMap())
            Toast.makeText(context, "Student removed succesfully!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "There occurred an error trying to remove this student!", Toast.LENGTH_LONG).show()
        }

        db?.close()
    }
}