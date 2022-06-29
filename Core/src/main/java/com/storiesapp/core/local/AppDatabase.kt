package com.storiesapp.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.storiesapp.core.local.converts.RoomConverters
import com.storiesapp.core.local.dao.StoryDao
import com.storiesapp.core.model.story.Story

@Database(entities = [
    Story::class
], version = 1)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase(){

    companion object {
        const val DATABASE_NAME = "stories-db"
    }

    abstract fun storyDao() : StoryDao
}