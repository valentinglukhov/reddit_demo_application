package com.example.redditdemoapplication.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.redditdemoapplication.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response

private const val LOG_TAG = "TAG"
private const val PROFILE_IMG = "profile_img"
private const val AMP = "amp;"
private const val EMPTY_STRING = ""

class MainViewModel(
    private val repository: Repository
) : ViewModel() {

    private var _errorMessageState = MutableStateFlow<String?>(null)
    val errorMessageState = _errorMessageState.asStateFlow()

    var subredditsPagingSource: Flow<PagingData<SubredditInfo>>? = null
    var subredditsLinksPagingSource: Flow<PagingData<LinkInfo>>? = null

    private val _linksWithComments = MutableStateFlow<List<LinkListing>?>(null)
    val linksWithComments = _linksWithComments.asStateFlow()
    private val _userAvatars = MutableStateFlow<Map<String, String>>(emptyMap())
    val userAvatars = _userAvatars.asStateFlow()
    private val _subredditDialogValue = MutableStateFlow<Subreddit?>(null)
    val subredditDialogValue = _subredditDialogValue.asStateFlow()
    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo = _userInfo.asStateFlow()
    private val _favorites = MutableStateFlow<LinkListing?>(null)
    val favorites = _favorites.asStateFlow()

    fun transferSubreddit(subreddit: Subreddit) {
        _subredditDialogValue.value = subreddit
    }

    fun clearErrorState() {
        _errorMessageState.value = null
    }

    fun clearLinksPagingSource() {
        subredditsLinksPagingSource = null
    }

    fun clearLinksWithCommentsStateFlow() {
        _linksWithComments.value = null
        _userAvatars.value = emptyMap()
        subredditsLinksPagingSource = null
    }

    fun favorites() {
        val name = userInfo.value?.name
        name?.let {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    myLog("UserInfo started TRY")
                    val response = repository.favorites(username = name)
                    myLog("UserInfo started RESPONSE ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        _favorites.value = response.body()
                    }
                } catch (e: Exception) {
                    myLog("UserInfo started ERROR ${e.localizedMessage}")
                    _errorMessageState.value = e.localizedMessage
                }
            }
        }
    }

    fun userInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                myLog("UserInfo started TRY")
                val response = repository.userInfo()
                myLog("UserInfo started RESPONSE ${response.isSuccessful}")
                if (response.isSuccessful) {
                    _userInfo.value = response.body()
                }
            } catch (e: Exception) {
                myLog("UserInfo started ERROR ${e.localizedMessage}")
                _errorMessageState.value = e.localizedMessage
            }
        }
    }

    fun getUserAvatars(ids: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getUserAvatars(
                    ids = ids
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        val jsonObject = JSONObject(it.string())
                        val map = jsonObject.toMap()
                        val newMap = mutableMapOf<String, String>()
                        for ((key, value) in map) {
                            val url = getUrl(value)
                            if (url != null) newMap[key] = url
                        }
                        _userAvatars.value = newMap
                    }
                }
            } catch (e: Exception) {
                myLog("getUserAvatars started ERROR ${e.localizedMessage}")
                _errorMessageState.value = e.localizedMessage
            }
        }
    }

    fun getRedditLinkWithComments(subredditName: String, linkId: String) {
        myLog("getRedditLinkWithComments started")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                myLog("getRedditLinkWithComments started TRY")
                val response = repository.getRedditLinkWithComments(
                    subredditName = subredditName,
                    linkId = linkId
                )
                myLog("getRedditLinkWithComments started RESPONSE ${response.isSuccessful}")
                if (response.isSuccessful) {
                    _linksWithComments.value = response.body()
                }
            } catch (e: Exception) {
                myLog("getRedditLinkWithComments started ERROR ${e.localizedMessage}")
                _errorMessageState.value = e.localizedMessage
            }
        }
    }

    fun getRedditLinks(subredditName: String) {
        myLog("ViewModel paging start Links")
        subredditsLinksPagingSource = Pager(
            config = PagingConfig(pageSize = 25, initialLoadSize = 30),
            pagingSourceFactory = {
                SubredditsLinksPagingSource(
                    repository = repository,
                    subredditName = subredditName
                )
            })
            .flow
            .cachedIn(viewModelScope)
    }

    suspend fun vote(
        action: Queries,
        isLiked: Boolean?,
        comment: Link? = null,
        link: Link? = null
    ): Boolean {
        var success = false
        val itemName = comment?.name ?: link?.name
        var response: Response<Result>? = null
        myLog("Vote NAME $itemName")
        myLog("Vote LIKED $isLiked")
        viewModelScope.launch {
            try {
                when(action) {
                    Queries.UPVOTE -> {
                        response = if(isLiked == true) {
                            repository.vote(id = itemName!!, dir = Queries.UNVOTE.vote!!)
                        } else {
                            repository.vote(id = itemName!!, dir = Queries.UPVOTE.vote!!)
                        }
                    }
                    Queries.DOWNVOTE -> {
                        response = if(isLiked == false) {
                            repository.vote(id = itemName!!, dir = Queries.UNVOTE.vote!!)
                        } else {
                            repository.vote(id = itemName!!, dir = Queries.DOWNVOTE.vote!!)
                        }
                    }
                    else -> {}
                }
                response?.let {
                    if (it.isSuccessful && it.code() == 200) {
                        success = true
                    }
                }
            } catch (e: HttpException) {
                _errorMessageState.value = e.localizedMessage
                success = false
            }
        }.join()
        myLog("Vote SUCCESS $success")
        return success
    }

    suspend fun saveUnsaveItems(
        comment: Link? = null,
        link: Link? = null
    ): Boolean {
        var success = false
        val itemName = comment?.name ?: link?.name
        val itemSaved = comment?.saved ?: link?.saved
        val action = if (itemSaved == true) {
            Queries.UNSAVE.query
        } else {
            Queries.SAVE.query
        }
        viewModelScope.launch {
            try {
                val response = repository.saveUnsaveItems(action = action, id = itemName!!)
                if (response.isSuccessful && response.code() == 200) {
                    success = true
                }
            } catch (e: HttpException) {
                _errorMessageState.value = e.localizedMessage
                success = false
            }
        }.join()
        return success
    }


    suspend fun subUnsub(
        subreddit: Subreddit
    ): Boolean {
        var success = false
        myLog("subUnsub ${subreddit.userIsSubscriber} ${subreddit.name}")
        val action = if (subreddit.userIsSubscriber != null && subreddit.userIsSubscriber!!) {
            Queries.UNSUB.query
        } else {
            Queries.SUB.query
        }
        viewModelScope.launch {
            try {
                val response = repository.subUnsub(action = action, sr = subreddit.name)
                if (response.isSuccessful && response.code() == 200) {
                    success = true
                }
            } catch (e: HttpException) {
                _errorMessageState.value = e.localizedMessage
                success = false
            }
        }.join()
        return success
    }

    fun getRedditWhere(where: String, query: String? = null) {
        myLog("ViewModel paging start $where $query")
        subredditsPagingSource = Pager(
            config = PagingConfig(pageSize = 25, initialLoadSize = 30),
            pagingSourceFactory = {
                SubredditsPagingSource(
                    repository = repository,
                    where = where,
                    query = query
                )
            })
            .flow
            .cachedIn(viewModelScope)
    }

    private fun myLog(text: String) {
        Log.d(LOG_TAG, text)
    }

    private fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith { string ->
        when (val value = this[string]) {
            is JSONArray -> {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else -> value
        }
    }

    private fun getUrl(linkedMap: Any?): String? {
        var value: String? = null
        if (linkedMap is java.util.LinkedHashMap<*, *>) linkedMap.keys.forEach { key ->
            if (key == PROFILE_IMG) {
                value = (linkedMap[key] as String?)?.replace(
                    oldValue = AMP,
                    newValue = EMPTY_STRING,
                    ignoreCase = true
                )
            }
        }
        return value
    }
}