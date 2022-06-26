package de.johannes.tutorium

import android.app.AlertDialog
import android.os.Bundle
import android.provider.BaseColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
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
        //binding.studentList.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, getData())
        binding.studentList.adapter = StudentAdapter(requireContext(), getDataMap())
        binding.studentList.setOnItemLongClickListener { adapterView, _, i, _ ->
            showDialog(adapterView.adapter.getItemId(i).toInt())
            true
        }
        binding.studentList.setOnItemClickListener { adapterView, _, i, _ ->
            Toast.makeText(context, "${adapterView.adapter.getItemId(i)}", Toast.LENGTH_SHORT).show()
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

    private fun getData() : MutableList<String>{
        val dbHelper = StudentReaderDbHelper(requireContext())
        val db = dbHelper.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(BaseColumns._ID, StudentEntry.COLUMN_NAME_NAME, StudentEntry.COLUMN_NAME_SURNAME, StudentEntry.COLUMN_NAME_SEMESTER)

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "${StudentEntry.COLUMN_NAME_SURNAME} DESC"

        val cursor = db.query(
            StudentEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        val students = mutableListOf<String>()
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(StudentEntry.COLUMN_NAME_NAME))
                val surname = getString(getColumnIndexOrThrow(StudentEntry.COLUMN_NAME_SURNAME))
                val semester = getString(getColumnIndexOrThrow(StudentEntry.COLUMN_NAME_SEMESTER))
                students.add("$name $surname | Semester $semester")
            }
        }
        cursor.close()

        return students
    }
}