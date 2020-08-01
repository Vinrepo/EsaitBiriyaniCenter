package com.restaurant.esaitbiriyanicenter
import com.intrusoft.sectionedrecyclerview.Section

public class SectionHeader(childList: List<Child>?,sectionText: String?) : Section<Child> {
    var childList: List<Child>? = childList
    private var sectionText: String? = sectionText
    override fun getChildItems(): List<Child>? {
        return childList
    }

    fun getSectionText(): String? {
        return sectionText
    }
}