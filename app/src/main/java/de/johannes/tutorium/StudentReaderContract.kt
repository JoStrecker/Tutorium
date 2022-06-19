package de.johannes.tutorium

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object StudentReaderContract {
    object StudentEntry : BaseColumns{
        const val TABLE_NAME = "students"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_SURNAME = "surname"
        const val COLUMN_NAME_SEMESTER = "semester"
    }

    private const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${StudentEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${StudentEntry.COLUMN_NAME_NAME} TEXT," +
                "${StudentEntry.COLUMN_NAME_SURNAME} TEXT," +
                "${StudentEntry.COLUMN_NAME_SEMESTER} INTEGER)"

    private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${StudentEntry.TABLE_NAME}"

    class StudentReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
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
            const val DATABASE_NAME = "StudentReader.db"
        }
    }
}