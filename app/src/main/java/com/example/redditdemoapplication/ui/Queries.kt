package com.example.redditdemoapplication.ui

enum class Queries(val query: String, val vote: Int? = null) {
    SEARCH("search"),
    SUB("sub"),
    UNSUB("unsub"),
    SAVE("save"),
    UNSAVE("unsave"),
    UPVOTE("upvote", 1),
    DOWNVOTE("downvote", -1),
    UNVOTE("unvote", 0),
}