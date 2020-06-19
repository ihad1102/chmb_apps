package com.g.base.room.repository

/**
 * Created by 86839 on 2017/10/12.
 */

enum class Status {
    Loading,
    Content,
    Error,
    Oauth
}


enum class ListStatus {
    LoadMore,
    Refreshing,
    Content
}