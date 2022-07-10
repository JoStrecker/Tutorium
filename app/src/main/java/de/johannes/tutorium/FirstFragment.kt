package de.johannes.tutorium

import android.app.AlertDialog
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.johannes.tutorium.databinding.FragmentFirstBinding


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

        binding.studentList.adapter = MusicAdapter(requireContext(), getDataMap())
        binding.studentList.setOnItemLongClickListener { adapterView, _, i, _ ->
            showDialog(adapterView.adapter.getItemId(i).toInt())
            true
        }
        binding.studentList.setOnItemClickListener { adapterView, _, i, _ ->
            //Toast.makeText(context, "${adapterView.adapter.getItem(i)}", Toast.LENGTH_SHORT).show()
            val song = adapterView.adapter.getItem(i) as Song
            viewOnSpotify(song.title, song.artist)
        }

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.floatingActionButton2.setOnClickListener {
            sendNotification2()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun viewOnSpotify(title: String, artist: String) {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
            intent.component = ComponentName(
                "com.spotify.music",
                "com.spotify.music.MainActivity"
            )
            intent.putExtra(SearchManager.QUERY, "$title, $artist")
            this.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDataMap() : Map<Int, Song>{
        val db = MusicReaderContract.MusicReaderDbHelper(requireContext()).readableDatabase

        val cursor = db.query(
            MusicReaderContract.MusicEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${MusicReaderContract.MusicEntry.COLUMN_NAME_TITLE} ASC"
        )

        val songs = mutableMapOf<Int, Song>()
        with(cursor) {
            while (moveToNext()) {
                val primaryKey = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                val title = getString(getColumnIndexOrThrow(MusicReaderContract.MusicEntry.COLUMN_NAME_TITLE))
                val artist = getString(getColumnIndexOrThrow(MusicReaderContract.MusicEntry.COLUMN_NAME_ARTIST))
                songs.put(primaryKey, Song(title, artist))
            }
        }
        cursor.close()
        db.close()

        return songs
    }

    private fun showDialog(id: Int){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Alert")
        builder.setMessage(getString(R.string.delete_song_alert))

        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            removeSong(id)
            dialog.dismiss()
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun removeSong(id: Int){
        val song = getDataMap()[id]
        val text = String.format(getString(R.string.delete_song_success_notification), song?.title, song?.artist)

        val db = MusicReaderContract.MusicReaderDbHelper(requireContext()).writableDatabase

        val where = "${BaseColumns._ID} = ?"
        val whereArgs = arrayOf(id.toString())

        val rows = db.delete(
            MusicReaderContract.MusicEntry.TABLE_NAME,
            where,
            whereArgs
        )

        if(rows > 0){
            binding.studentList.adapter = MusicAdapter(requireContext(), getDataMap())
            sendNotification(text)
        }else{
            Toast.makeText(context, getString(R.string.delete_song_fail), Toast.LENGTH_LONG).show()
        }

        db.close()
    }

    private fun sendNotification(text: String){
        var builder = NotificationCompat.Builder(requireContext(), getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_baseline_album_24)
            .setContentTitle(getString(R.string.delte_song_success))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())){
            notify(kotlin.random.Random(12).nextInt(), builder.build())
        }
    }

    /*private fun getDataMap() : Map<Int, String>{
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
        val text = getDataMap()[id] ?: ""

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
            sendNotification(text)
        }else{
            Toast.makeText(context, "There occurred an error trying to remove this student!", Toast.LENGTH_LONG).show()
        }

        db?.close()
    }

    private fun sendNotification(text: String){
        var builder = NotificationCompat.Builder(requireContext(), getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_baseline_emoji_people_24)
            .setContentTitle("Student deleted")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())){
            notify(kotlin.random.Random(12).nextInt(), builder.build())
        }
    }*/

    private fun sendNotification2(){
        val sharedPref = activity?.getSharedPreferences("name", Context.MODE_PRIVATE)

        var builder = NotificationCompat.Builder(requireContext(), getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_baseline_emoji_people_24)
            .setContentTitle("Notification")
            .setContentText(sharedPref?.getString("nameKey", "") ?: "")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())){
            notify(kotlin.random.Random(12).nextInt(), builder.build())
        }
    }
}