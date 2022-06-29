package com.android.common.view.statelayout

enum class State {
    None,
    Loading, // when loading data
    ShimmerLoading,
    Content, // show data
    Empty, // no data
    Error, // load data fail
    Blank // show nothing
}