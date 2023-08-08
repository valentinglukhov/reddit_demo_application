package com.example.redditdemoapplication.ui

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.redditdemoapplication.data.Repository
import com.example.redditdemoapplication.data.LinkInfo
import retrofit2.HttpException

private const val LOG_TAG = "TAG"

class SubredditsLinksPagingSource(
    private val repository: Repository,
    private val subredditName: String,
    ) : PagingSource<String, LinkInfo>() {
    override fun getRefreshKey(state: PagingState<String, LinkInfo>): String? {
        return state.anchorPosition?.let { state.closestItemToPosition(it)?.data?.id }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, LinkInfo> {
        val afterItem = params.key
        myLog("PagingSourceLinks start")

        return try {
            val response = repository.getRedditLinks(subredditName = subredditName, after = afterItem)

            if (response.isSuccessful && response.body() != null) {
                val subredditsLinksInfo = response.body()!!.data.children
                val nextPageAnchor = response.body()!!.data.after
                LoadResult.Page(
                    data = subredditsLinksInfo,
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