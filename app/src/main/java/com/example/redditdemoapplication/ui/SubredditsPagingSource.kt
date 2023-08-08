package com.example.redditdemoapplication.ui

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.redditdemoapplication.data.Repository
import com.example.redditdemoapplication.data.SubredditInfo
import retrofit2.HttpException
import retrofit2.http.Query

private const val LOG_TAG = "TAG"

class SubredditsPagingSource(
    private val repository: Repository,
    private val where: String,
    private val query: String? = null,

    ) : PagingSource<String, SubredditInfo>() {
    override fun getRefreshKey(state: PagingState<String, SubredditInfo>): String? {
        return state.anchorPosition?.let { state.closestItemToPosition(it)?.data?.name }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SubredditInfo> {
        val afterItem = params.key
        myLog("PagingSource start")

        return try {
            val response = when (where) {
                Tabs.NEW.query -> repository.getRedditWhere(where = where, after = afterItem)
                Tabs.POPULAR.query -> repository.getRedditWhere(where = where, after = afterItem)
                Queries.SEARCH.query -> repository.getRedditWhere(
                    where = where,
                    after = afterItem,
                    query = query
                )
                else -> {repository.getRedditWhere(where = where, after = afterItem)}
            }

            if (response.isSuccessful && response.body() != null) {
                val subredditsInfo = response.body()!!.data.children
                val nextPageAnchor = response.body()!!.data.after
                LoadResult.Page(
                    data = subredditsInfo,
                    nextKey = nextPageAnchor,
                    prevKey = null,
                    itemsAfter = 25
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun myLog(text: String) {
        Log.d(LOG_TAG, text)
    }
}