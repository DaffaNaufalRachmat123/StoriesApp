package com.storiesapp.core.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.storiesapp.core.model.story.Story

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(storyList : List<Story>)

    @Query("SELECT * FROM stories")
    fun pagingSource() : PagingSource<Int, Story>

    @Query("DELETE FROM stories")
    fun clearAll()
}