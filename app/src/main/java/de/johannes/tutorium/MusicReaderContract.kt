package de.johannes.tutorium

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object MusicReaderContract {
    object MusicEntry : BaseColumns{
        const val TABLE_NAME = "songs"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_ARTIST = "artist"
    }

    private const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${MusicEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${MusicEntry.COLUMN_NAME_TITLE} TEXT," +
                "${MusicEntry.COLUMN_NAME_ARTIST} TEXT)"

    private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${MusicEntry.TABLE_NAME}"

    class MusicReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES)
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
        }
        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onUpgrade(db, oldVersion, newVersion)
        }
        companion object {
            // If you change the database schema, you must increment the database version.
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "MusicReader.db"
        }
    }
}