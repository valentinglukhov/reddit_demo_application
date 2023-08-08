package com.example.redditdemoapplication

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.redditdemoapplication.data.*
import com.example.redditdemoapplication.ui.*
import com.example.redditdemoapplication.ui.theme.CustomGray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Credentials
import retrofit2.HttpException
import java.util.*
import kotlin.random.Random

private const val LOG_TAG = "TAG"
private const val CLIENT_ID_QUERY = "client_id"
private const val CLIENT_ID = "0gCHPdaYLHuQvyrTNecEsg"
private const val RESPONSE_TYPE_QUERY = "response_type"
private const val RESPONSE_TYPE = "code"
private const val REDIRECT_URI_QUERY = "redirect_uri"
private const val REDIRECT_URI = "app://open.my.application"
private const val DURATION_QUERY = "duration"
private const val DURATION = "permanent"
private const val SCOPE_QUERY = "scope"
private const val STATE = "state"
private const val SETTINGS = "settings"
private const val REDDIT_AUTH_BASE_URL = "https://www.reddit.com/api/v1/authorize.compact"
private const val EMPTY_STRING = ""
private const val AUTHORIZATION_CODE = "authorization_code"
private const val ACCESS_TOKEN = "access_token"
private const val REFRESH_TOKEN = "refresh_token"
private const val EXPIRES_IN = "expires_in"
private const val SUBREDDIT_DISPLAYED_NAME = "subreddit_displayed_name"
private const val SUBREDDIT_NAME = "subreddit_name"
private const val LINK_ID = "link_id"
private const val MY_ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IlNIQTI1NjpzS3dsMnlsV0VtMjVmcXhwTU40cWY4MXE2OWFFdWFyMnpLMUdhVGxjdWNZIiwidHlwIjoiSldUIn0.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjg4MTQ4NzQwLjAyOTA1NSwiaWF0IjoxNjg4MDYyMzQwLjAyOTA1NCwianRpIjoiQWNVRV9xbTF1MnNfeWVYS3p1SjVCQlI3SDE2eXp3IiwiY2lkIjoiMGdDSFBkYVlMSHVRdnlyVE5lY0VzZyIsImxpZCI6InQyX3RlMTduNmtiIiwiYWlkIjoidDJfdGUxN242a2IiLCJsY2EiOjE2NjU4NTg2NzkwMDAsInNjcCI6ImVKeEVqVUVPZzBBSVJlX3kxOXlvNldKRzBKSTZZZ0J0dkgwemF0TWRmQjdfUGZEUnR3cHJnaEJsRjlDWnVCUUdvUm4zRFlRYjJTMDcwbzdZcWd1elp2VEhyY2JnV3Z0cGRkMUxTcE9JTWtsY0pZTXRvMDRnX0h0bm02NWh0Y2diRy1laWZsS3JlZGY5Z3RocU9fM0tzcVRtQWNKTEk4MFBQTDhCQUFEX184SEZReGMiLCJyY2lkIjoiQ2pCcE5uTnMzQU82bUItOWpUeFRlNFNzaVJZN0JlWkRQQTBYQml6ZFVubyIsImZsbyI6OH0.Hnzna-7k4U3ZjIavyC6OhvRiPheGfZ6uDkrVVXwysrUX7P45I-aoIBaxHoJTMXyCFv53ZNuzC2tvjr5yLJyvAbTEmHlcLdjT1tK0GiIfnOBEC7hv4OrKfHa8mTcLpMW1q7FRKWEQDIZiSYeiFww_Fa5E19CgaLKEbhLZyl9e_aJcYn4lweZyZAt_XQc0ZqDHn-Yzrng6JS5QzqoXqR9JuDJW4W7izKgbhXZBvczU9seUK6WZZCalV2iHZXXzMjg0y7oXlBJT7WagBuZLCf1srMbYxH3NF4PyoRjrYvpTiTOo9jQ0T0NJVSYhchLFbXBeNI6wFGe4Ym8ojPCOtKSTjQ"


class MainActivity : ComponentActivity() {

    private val redditScopes = listOf(
        "identity",
        "edit",
        "flair",
        "history",
        "modconfig",
        "modflair",
        "modlog",
        "modposts",
        "modwiki",
        "mysubreddits",
        "privatemessages",
        "read",
        "report",
        "save",
        "submit",
        "subscribe",
        "vote",
        "wikiedit",
        "wikiread"
    )
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: MainViewModel

    private val bottomNavigationRoutes = listOf(
        BottomNavigationRoutes.Feed,
        BottomNavigationRoutes.Favorites,
        BottomNavigationRoutes.Profile
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            viewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MainViewModel(repository = Repository(this@MainActivity)) as T
                    }
                }
            )
            sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE)
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val errorMessageState = viewModel.errorMessageState.collectAsState()

            LaunchedEffect(key1 = errorMessageState) {
                if (errorMessageState.value != null) {
                    myToast(errorMessageState.value)
                    viewModel.clearErrorState()
                }
            }

            LaunchedEffect(key1 = true) { viewModel.userInfo() }

            Scaffold(bottomBar = {
                if (currentRoute != ScreenRoute.ONBOARD.route && currentRoute != ScreenRoute.LOGIN.route && currentRoute != null)
                    BottomNavigation(backgroundColor = Color.White) {
                        bottomNavigationRoutes.forEach { screen ->
                            BottomNavigationItem(selected = screen.route == BottomNavigationRoutes.Feed.route,
                                onClick = {
                                    navController.navigate(screen.route)
                                    viewModel.clearLinksWithCommentsStateFlow()
                                          },
                                icon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = screen.icon),
                                        contentDescription = screen.contentDescription,
                                        tint = screen.tint
                                    )
                                },
                                label = { Text(screen.title, color = screen.tint) })
                        }
                    }
            }) {
                NavHost(
                    navController = navController,
                    startDestination = ScreenRoute.ONBOARD.route,
                    modifier = Modifier
                ) {
                    composable(route = ScreenRoute.ONBOARD.route) {
                        OnboardScreen(navController = navController)
                    }
                    composable(route = BottomNavigationRoutes.Feed.route) {
                        FeedScreen(navController = navController)
                    }
                    composable(
                        route = ScreenRoute.SUBREDDIT.route + "/{$SUBREDDIT_DISPLAYED_NAME}",
                        arguments = listOf(navArgument(SUBREDDIT_DISPLAYED_NAME) {
                            type = NavType.StringType
                        })
                    ) {
                        val subredditDisplayedName =
                            it.arguments?.getString(SUBREDDIT_DISPLAYED_NAME)
                        LinksScreen(
                            navController = navController,
                            subredditDisplayedName = subredditDisplayedName
                        )
                    }
                    composable(
                        route = ScreenRoute.SUBREDDITLINKWITHCOMMENTS.route
                                + "/{$SUBREDDIT_NAME}/{$LINK_ID}",
                        arguments = listOf(navArgument(SUBREDDIT_NAME) {
                            type = NavType.StringType
                        }, navArgument(LINK_ID) {
                            type = NavType.StringType
                        })
                    ) {
                        val subredditName = it.arguments?.getString(SUBREDDIT_NAME)
                        val linkId = it.arguments?.getString(LINK_ID)
                        LinkWithCommentsScreen(
                            navController = navController,
                            subredditName = subredditName,
                            linkId = linkId
                        )
                    }
                    composable(route = BottomNavigationRoutes.Favorites.route) {
                        FavoritesScreen(navController = navController)
                    }
                    composable(route = BottomNavigationRoutes.Profile.route) {
                        ProfileScreen(navController = navController)
                    }
                }
            }
        }
    }

    @Composable
    fun FeedScreen(navController: NavController) {
        var selectedTab by remember { mutableStateOf(0) }
        var textFieldState by remember { mutableStateOf("") }
        val subreddits = viewModel.subredditsPagingSource?.collectAsLazyPagingItems()
        LaunchedEffect(key1 = true) {
            if (subreddits == null) viewModel.getRedditWhere(Tabs.values()[selectedTab].query)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    value = textFieldState,
                    onValueChange = { value -> textFieldState = value },
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black
                    ),
                    shape = RoundedCornerShape(9.dp)
                )
                IconButton(onClick = {
                    if (textFieldState.isNotEmpty()) {
                        viewModel.getRedditWhere(
                            where = Queries.SEARCH.query,
                            query = textFieldState
                        )
                        val tempString = textFieldState
                        textFieldState = ""
                        textFieldState = tempString
                    }
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = R.drawable.icon_search
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Black
                    )
                }
            }
            TabRow(selectedTabIndex = selectedTab, backgroundColor = Color.White) {
                Tabs.values().forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            viewModel.getRedditWhere(Tabs.values()[selectedTab].query)
                        }
                    ) {
                        Text(text = tab.title, modifier = Modifier.padding(9.dp))
                    }
                }
            }
            subreddits?.let { subreddits ->
                SubredditsLazyColumn(subreddits = subreddits, navController = navController)
            }
        }
    }

    @Composable
    fun SubredditsLazyColumn(
        subreddits: LazyPagingItems<SubredditInfo>,
        navController: NavController
    ) {
        LaunchedEffect(key1 = subreddits.loadState) {
            if (subreddits.loadState.refresh is LoadState.Error) {
                myToast((subreddits.loadState.refresh as LoadState.Error).error.message)
                myLog((subreddits.loadState.refresh as LoadState.Error).error.message.toString())
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            if (subreddits.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 9.dp, vertical = 9.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    items(
                        count = subreddits.itemCount,
                        key = subreddits.itemKey(),
                        contentType = subreddits.itemContentType(
                        )
                    ) { index ->
                        val subreddit = subreddits[index]?.data
                        if (subreddit != null) {
                            SubredditItem(subreddit = subreddit, navController = navController)
                        }
                    }
                    item {
                        if (subreddits.loadState.append is LoadState.Loading) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun SubredditItem(
        subreddit: Subreddit,
        navController: NavController
    ) {
        var isSubscribed by remember {
            mutableStateOf(subreddit.userIsSubscriber)
        }
        val scope = rememberCoroutineScope()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.transferSubreddit(subreddit)
                    navController.navigate("${ScreenRoute.SUBREDDIT.route}/${subreddit.displayName}") {
                        popUpTo(BottomNavigationRoutes.Feed.route) {
                            saveState = true
                        }
                        restoreState = true
                    }
                },
            shape = RoundedCornerShape(9.dp),
            border = BorderStroke(2.dp, CustomGray),
            elevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(19.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    subreddit.headerImg?.let {
                        GlideImage(
                            model = subreddit.headerImg,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(shape = CircleShape)
                        )
                    }
                    Text(
                        text = subreddit.displayNamePrefixed.toString(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(19.dp)
                    )
                    IconButton(onClick = {
                        scope.launch {
                            val success = viewModel.subUnsub(subreddit = subreddit)
                            if (success) isSubscribed = !isSubscribed!!
                        }

                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id = R.drawable.icon_bookmark
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = if (isSubscribed == true) Color.Red else Color.Black
                        )
                    }
                }
                Text(text = subreddit.title, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    @Composable
    fun LinksScreen(navController: NavController, subredditDisplayedName: String?) {
        val subredditLinks = viewModel.subredditsLinksPagingSource?.collectAsLazyPagingItems()
        var dialogState by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(key1 = subredditDisplayedName) {
            subredditDisplayedName?.let {
                if (subredditLinks == null) viewModel.getRedditLinks(subredditDisplayedName)
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = subredditDisplayedName.toString(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = { dialogState = true }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = R.drawable.icon_info
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Black
                    )
                }
                Text(text = getString(R.string.info), modifier = Modifier.padding(horizontal = 19.dp))
            }
            subredditLinks?.let { subredditLinksPagingData ->
                LinksLazyColumn(
                    subredditLinks = subredditLinksPagingData,
                    navController = navController
                )
            }
        }
        if (dialogState) {
            CustomDialog(onDismiss = { state -> dialogState = state })
        }
        BackHandler {
            viewModel.clearLinksPagingSource()
            navController.popBackStack()
        }
    }

    @Composable
    fun CustomDialog(
        onDismiss: (Boolean) -> Unit
    ) {
        val subreddit = viewModel.subredditDialogValue.collectAsState().value
        Dialog(onDismissRequest = { onDismiss(false) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.9f),
                shape = RoundedCornerShape(17.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(19.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            onDismiss(false)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(24.dp)
                                    .background(Color.White),
                                tint = Color.Black,
                            )
                        }
                    }
                    subreddit?.let {
                        subreddit.displayNamePrefixed?.let { it1 -> Text(text = it1) }
                        subreddit.description?.let { it1 -> Text(text = it1) }

                    }
                }
            }
        }
    }

    @Composable
    fun LinksLazyColumn(
        subredditLinks: LazyPagingItems<LinkInfo>,
        navController: NavController
    ) {
        LaunchedEffect(key1 = subredditLinks.loadState) {
            if (subredditLinks.loadState.refresh is LoadState.Error) {
                myToast((subredditLinks.loadState.refresh as LoadState.Error).error.message)
                myLog((subredditLinks.loadState.refresh as LoadState.Error).error.message.toString())
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            if (subredditLinks.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 9.dp, vertical = 9.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    items(
                        count = subredditLinks.itemCount,
                        key = subredditLinks.itemKey(),
                        contentType = subredditLinks.itemContentType(
                        )
                    ) { index ->
                        val subreddit = subredditLinks[index]?.data
                        if (subreddit != null) {
                            LinkItem(
                                subredditLink = subreddit,
                                navController = navController,
                                clickable = true
                            )
                        }
                    }
                    item {
                        if (subredditLinks.loadState.append is LoadState.Loading) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun LinkItem(
        subredditLink: Link,
        navController: NavController,
        clickable: Boolean
    ) {
        var score by remember { mutableStateOf(subredditLink.score) }
        var isLiked by remember { mutableStateOf(subredditLink.likes) }
        val scope = rememberCoroutineScope()
        var isSaved by remember { mutableStateOf(subredditLink.saved) }
        fun voteUpSuccess() {
            score = if (score == null) 1 else if (isLiked == true) score!! - 1 else score!! + 1
            isLiked = when (isLiked) {
                true -> null
                false -> null
                null -> true
            }
        }

        fun voteDownSuccess() {
            score = if (score == null) -1 else if (isLiked == false) score!! + 1 else score!! - 1
            isLiked = when (isLiked) {
                true -> null
                false -> null
                null -> false
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = clickable) {
                    navController.navigate(
                        ScreenRoute.SUBREDDITLINKWITHCOMMENTS.route
                                + "/${subredditLink.subreddit}"
                                + "/${subredditLink.id}"
                    ) {
                        popUpTo(ScreenRoute.SUBREDDIT.route) {
                            saveState = true
                        }
                        restoreState = true
                    }
                },
            shape = RoundedCornerShape(9.dp),
            border = BorderStroke(2.dp, CustomGray),
            elevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(19.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = subredditLink.author ?: "",
                        modifier = Modifier
                            .weight(1f)
                            .padding(19.dp)
                    )
                }
                Text(
                    text = subredditLink.title ?: "", modifier = Modifier
                        .fillMaxWidth()
                        .padding(19.dp)
                )
                GlideImage(
                    model = subredditLink.thumbnail,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(300.dp)
                        .clip(RoundedCornerShape(9.dp))
                )
                subredditLink.selftext?.let {
                    Text(
                        text = subredditLink.selftext,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(19.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id = R.drawable.icon_comment
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                        Text(text = subredditLink.numComments.toString())
                    }
                    IconButton(onClick = {
                        subredditLink.permalink?.let {
                            shareLink(subredditLink.permalink)
                        }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id = R.drawable.icon_share
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = {
                        scope.launch {
                            val success = viewModel.saveUnsaveItems(link = subredditLink)
                            if (success) isSaved = !isSaved!!
                        }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id = R.drawable.icon_save
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = if (isSaved == true) Color.Red else Color.Black
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            scope.launch {
                                val success =
                                    viewModel.vote(
                                        action = Queries.DOWNVOTE,
                                        link = subredditLink,
                                        isLiked = isLiked
                                    )
                                if (success) {
                                    voteDownSuccess()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    id = R.drawable.icon_down
                                ),
                                contentDescription = "",
                                modifier = Modifier.size(24.dp),
                                tint = if (isLiked == false) Color.Red else Color.Black
                            )
                        }
                        Text(text = if (score == null) "0" else score.toString())
                        IconButton(onClick = {
                            scope.launch {
                                val success =
                                    viewModel.vote(
                                        action = Queries.UPVOTE,
                                        link = subredditLink,
                                        isLiked = isLiked
                                    )
                                if (success) {
                                    voteUpSuccess()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    id = R.drawable.icon_up
                                ),
                                contentDescription = "",
                                modifier = Modifier.size(24.dp),
                                tint = if (isLiked == true) Color.Red else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun LinkWithCommentsScreen(
        navController: NavController,
        subredditName: String?,
        linkId: String?
    ) {
        val linkWithComments = viewModel.linksWithComments.collectAsState()
        val requieredImages = mutableListOf<String>()
        LaunchedEffect(key1 = true) {
            if (linkWithComments.value == null && subredditName != null && linkId != null) {
                viewModel.getRedditLinkWithComments(subredditName = subredditName, linkId = linkId)
            }
        }
        if (linkWithComments.value == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            val link = linkWithComments.value!![0].data.children.first().data
            val comments = linkWithComments.value!![1].data.children
            comments.forEach { comment ->
                comment.data.authorFullname?.let { name -> requieredImages.add(name) }
            }
            viewModel.getUserAvatars(requieredImages.joinToString(","))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 9.dp, vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                item {
                    LinkItem(
                        subredditLink = link,
                        navController = navController,
                        clickable = false
                    )
                }
                itemsIndexed(comments) { _, comment ->
                    CommentItem(comment = comment.data)
                }
            }
        }

        BackHandler {
            viewModel.clearLinksWithCommentsStateFlow()
            navController.popBackStack()
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun CommentItem(
        comment: Link,
    ) {
        var score by remember { mutableStateOf(comment.score) }
        var isLiked by remember { mutableStateOf(comment.likes) }
        var isSaved by remember { mutableStateOf(comment.saved) }
        val scope = rememberCoroutineScope()
        val userAvatars = viewModel.userAvatars.collectAsState()

        fun voteUpSuccess() {
            score = if (score == null) 1 else if (isLiked == true) score!! - 1 else score!! + 1
            isLiked = when (isLiked) {
                true -> null
                false -> null
                null -> true
            }
        }

        fun voteDownSuccess() {
            score = if (score == null) -1 else if (isLiked == false) score!! + 1 else score!! - 1
            isLiked = when (isLiked) {
                true -> null
                false -> null
                null -> false
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(9.dp),
            border = BorderStroke(2.dp, CustomGray),
            elevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(19.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (userAvatars.value.containsKey(comment.authorFullname)) {
                        GlideImage(
                            model = userAvatars.value[comment.authorFullname],
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(shape = CircleShape)
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                    Text(
                        text = comment.author ?: "",
                        modifier = Modifier
                            .weight(1f)
                            .padding(19.dp)
                    )
                }
                Text(
                    text = comment.body ?: "", modifier = Modifier
                        .fillMaxWidth()
                        .padding(19.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { comment.permalink?.let { shareLink(it) } }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id = R.drawable.icon_share
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = {
                        scope.launch {
                            val success = viewModel.saveUnsaveItems(comment = comment)
                            if (success) isSaved = !isSaved!!
                        }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id = R.drawable.icon_save
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = if (isSaved == true) Color.Red else Color.Black
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            scope.launch {
                                val success =
                                    viewModel.vote(
                                        action = Queries.DOWNVOTE,
                                        comment = comment,
                                        isLiked = isLiked
                                    )
                                if (success) {
                                    voteDownSuccess()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    id = R.drawable.icon_down
                                ),
                                contentDescription = "",
                                modifier = Modifier.size(24.dp),
                                tint = if (isLiked == false) Color.Red else Color.Black
                            )
                        }
                        Text(text = if (score == null) "0" else score.toString())
                        IconButton(onClick = {
                            scope.launch {
                                val success =
                                    viewModel.vote(
                                        action = Queries.UPVOTE,
                                        comment = comment,
                                        isLiked = isLiked
                                    )
                                if (success) {
                                    voteUpSuccess()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    id = R.drawable.icon_up
                                ),
                                contentDescription = "",
                                modifier = Modifier.size(24.dp),
                                tint = if (isLiked == true) Color.Red else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun FavoritesScreen(navController: NavController) {
        LaunchedEffect(key1 = true) { viewModel.userInfo() }
        LaunchedEffect(key1 = true) { viewModel.favorites() }

        val requieredImages = mutableListOf<String>()
        val favorites = viewModel.favorites.collectAsState().value?.data?.children
        favorites?.forEach { comment ->
            comment.data.authorFullname?.let { name -> requieredImages.add(name) }
        }
        viewModel.getUserAvatars(requieredImages.joinToString(","))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if (favorites == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 9.dp, vertical = 9.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    itemsIndexed(favorites) { _, item ->
                        when (item.kind) {
                            Kind.T1.value -> CommentItem(
                                comment = item.data
                            )
                            Kind.T3.value -> LinkItem(
                                subredditLink = item.data,
                                navController = navController,
                                clickable = true
                            )
                        }
                    }
                }
            }
        }
        BackHandler {
            viewModel.clearLinksWithCommentsStateFlow()
            navController.popBackStack()
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun ProfileScreen(navController: NavController) {
        LaunchedEffect(key1 = true) { viewModel.userInfo() }
        val user = viewModel.userInfo.collectAsState().value
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if (user == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                user.subreddit.iconImg.let {
                    GlideImage(
                        model = user.subreddit.iconImg,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(shape = CircleShape)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = getString(R.string.name))
                    Text(text = user.name)
                }
            }
            Button(onClick = { logout(navController = navController) }) {
                Text(text = getString(R.string.logout))
            }
        }
    }

    @Composable
    fun OnboardScreen(navController: NavController) {
        LaunchedEffect(key1 = intent) {
            if (checkAuthState()) navController.navigate(BottomNavigationRoutes.Feed.route)
        }
        navController.navigate(BottomNavigationRoutes.Feed.route)
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Column(modifier = Modifier.fillMaxSize().padding(70.dp),
//            verticalArrangement = Arrangement.SpaceEvenly,
//            horizontalAlignment = Alignment.CenterHorizontally) {
//                Text(text = getString(R.string.onboard))
//                Button(onClick = { startAuthIntent() }) {
//                    Text(text = getString(R.string.login))
//                }
//            }
//        }
    }

    private fun logout(navController: NavController) {
        sharedPreferences.edit().clear().apply()
        navController.navigate(ScreenRoute.ONBOARD.route)
    }

    private fun shareLink(linkPermalink: String) {
        val linkUrl = getString(R.string.reddip_api, linkPermalink)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = getString(R.string.text_plain)
        intent.putExtra(Intent.EXTRA_TEXT, linkUrl)
        startActivity(Intent.createChooser(intent, getString(R.string.share_with)))
    }

    private suspend fun checkAuthState(): Boolean {
        sharedPreferences.edit().putString(ACCESS_TOKEN, MY_ACCESS_TOKEN).apply()
        val timeInMillis = Date().time
        val data = intent.data
        val authCodeShared = sharedPreferences.getString(AUTHORIZATION_CODE, null)
        val accessToken = sharedPreferences.getString(ACCESS_TOKEN, null)
        val refreshToken = sharedPreferences.getString(REFRESH_TOKEN, null)
        val expiresIn = sharedPreferences.getLong(EXPIRES_IN, 0)
        val credentials = Credentials.basic(CLIENT_ID, EMPTY_STRING)
        myLog("Auth code " + authCodeShared.toString())
        myLog("Access token " + accessToken.toString())
        myLog("Time in millis $timeInMillis")
        myLog("Expires in $expiresIn")
        if (accessToken != null && refreshToken != null && (timeInMillis > expiresIn)) {
            getTokens(refreshToken = refreshToken, credentials = credentials)
        }
        var isAuthSuccess = false
        if (authCodeShared == null || accessToken == null) {
            if (data != null && data.queryParameterNames.contains(RESPONSE_TYPE)
                && data.queryParameterNames.contains(STATE)
            ) {
                val state = data.getQueryParameter(STATE)
                val code = data.getQueryParameter(RESPONSE_TYPE)
                sharedPreferences.edit().putString(AUTHORIZATION_CODE, code).apply()
                val currentState = sharedPreferences.getString(STATE, "")
                if (code != null && (currentState == state)) {
                    isAuthSuccess = getTokens(credentials, code)
                }
            }
        } else isAuthSuccess = true
        myLog("Is auth success $isAuthSuccess")
        return isAuthSuccess
    }

    private suspend fun getTokens(
        credentials: String,
        code: String? = null,
        refreshToken: String? = null
    ): Boolean {
        val scope = CoroutineScope(Dispatchers.IO)
        var getTokenResult = false
        scope.launch {
            try {
                val response = if (refreshToken == null && code != null) {
                    myLog("Getting access token")
                    RedditAuthApi.instance.getAccessToken(
                        code = code,
                        credentials = credentials
                    )
                } else {
                    refreshToken?.let {
                        myLog("Getting refresh token")
                        RedditAuthApi.instance.getRefreshToken(
                            refresh_token = refreshToken,
                            credentials = credentials
                        )
                    }
                }
                if (response != null) {
                    getTokenResult = if (response.isSuccessful && response.code() == 200) {
                        val tokenData = response.body()
                        tokenData?.let {
                            val currentTimeInMillis = Date().time
                            sharedPreferences.edit()
                                .putString(ACCESS_TOKEN, it.access_token)
                                .putString(REFRESH_TOKEN, it.refresh_token)
                                .putLong(
                                    EXPIRES_IN,
                                    it.expires_in * 1000 + currentTimeInMillis
                                ).apply()
                            viewModel.userInfo()
                            myLog("Token data ${it.refresh_token}, ${it.expires_in}, ${it.access_token}")
                        }
                        true
                    } else false
                } else getTokenResult = false
            } catch (e: HttpException) {
                myToast(e.localizedMessage)
                getTokenResult = false
            }
        }.join()
        return getTokenResult
    }

    private fun startAuthIntent() {
        val state = Random.nextInt(1000, 9999).toString()
        myLog("State value = $state")
        sharedPreferences.edit().putString(STATE, state).apply()
        val uri = Uri.parse(REDDIT_AUTH_BASE_URL)
            .buildUpon()
            .appendQueryParameter(CLIENT_ID_QUERY, CLIENT_ID)
            .appendQueryParameter(RESPONSE_TYPE_QUERY, RESPONSE_TYPE)
            .appendQueryParameter(STATE, state)
            .appendQueryParameter(REDIRECT_URI_QUERY, REDIRECT_URI)
            .appendQueryParameter(DURATION_QUERY, DURATION)
            .appendQueryParameter(SCOPE_QUERY, redditScopes.joinToString(" "))
            .build()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun myToast(text: String?) {
        Toast.makeText(this@MainActivity, text.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun myLog(text: String) {
        Log.d(LOG_TAG, text)
    }
}
