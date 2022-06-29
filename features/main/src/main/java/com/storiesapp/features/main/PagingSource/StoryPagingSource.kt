package com.storiesapp.features.main.PagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.storiesapp.core.local.UserSession
import com.storiesapp.core.model.story.Story
import com.storiesapp.features.main.api.MainApi
import org.apache.http.HttpException
import java.io.IOException

class StoryPagingSource ( private val api : MainApi ) : PagingSource<Int , Story>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            // 1
            val pageNumber = UserSession.pageNumber
            val response = api.getStoryList(page = pageNumber , size = 3 , location = 0)
            // 2
            val listing = response.listStory
            // 3
            UserSession.pageNumber = UserSession.pageNumber + 1
            LoadResult.Page(
                data = listing,
                prevKey = null,
                nextKey = if(listing.size > 0) pageNumber else null
            )
        } catch (exception: IOException) { // 6
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? = 1
}