package de.johannes.tutorium

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

data class Song(
    val title: String,
    val artist: String,
)

class MusicAdapter(private val context: Context, private val dataSource: Map<Int, Song>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource.get(getItemId(position).toInt()) ?: Song("", "")
    }

    override fun getItemId(position: Int): Long {
        return dataSource.keys.elementAt(position).toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        val titleTextView = rowView.findViewById(android.R.id.text1) as TextView
        val song = getItem(position) as Song
        titleTextView.text = "${song.title} | ${song.artist}"
        return rowView
    }

}