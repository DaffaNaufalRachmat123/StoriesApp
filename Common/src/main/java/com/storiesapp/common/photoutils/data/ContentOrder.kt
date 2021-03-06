package com.storiesapp.common.photoutils.data

/**
 *
 * Enum to choose between Ascending or Descending order
 */
enum class ContentOrder(internal val s: String) {
    /** sort by DESC Order */
    DESCENDING("DESC"),

    /** Sort by ASC Order */
    ASCENDING("ASC")
}