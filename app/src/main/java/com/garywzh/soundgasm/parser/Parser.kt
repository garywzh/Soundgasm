package com.garywzh.soundgasm.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document


/**
 * Created by garywzh on 2018/3/4.
 */
object Parser {
    fun toDoc(html: String): Document {
        val document = Jsoup.parse(html)
        val settings = document.outputSettings().prettyPrint(false)
        document.outputSettings(settings)
        return document
    }
}