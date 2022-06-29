package com.storiesapp.core.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.Gson
import com.storiesapp.core.local.AppDatabase.Companion.DATABASE_NAME

class DatabaseProvider(private val context: Context) {

    init {
        initKotPref()
    }

    private fun initKotPref() {
        Kotpref.init(context)
        Kotpref.gson = Gson()
    }

    private var database: AppDatabase? = null

    /**
     * Gets an instance of [AppDatabase].
     *
     * @return an instance of [AppDatabase]
     */
    fun getInstance(): AppDatabase =
        database ?: synchronized(this) {
            database ?: buildDatabase().also { database = it }
        }

    private fun buildDatabase(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addCallback(onCreateDatabase())
            .fallbackToDestructiveMigration()
            .build()

    private fun onCreateDatabase() = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }
    }
}