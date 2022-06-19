package de.johannes.tutorium

import android.os.Bundle
import android.provider.BaseColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.studentList.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, getData())
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getData() : MutableList<String>{
        val dbHelper = StudentReaderDbHelper(requireContext())
        val db = dbHelper.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(BaseColumns._ID, StudentEntry.COLUMN_NAME_NAME, StudentEntry.COLUMN_NAME_SURNAME, StudentEntry.COLUMN_NAME_SEMESTER)

        // Filter results WHERE "title" = 'My Title'
        val selection = "${StudentEntry.COLUMN_NAME_NAME} = ?"
        val selectionArgs = arrayOf("My Title")

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