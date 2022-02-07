package com.oftatech.superschoolboyremastered.util

import android.text.Editable
import android.text.Html
import androidx.compose.ui.graphics.Color
import org.xml.sax.XMLReader
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import com.oftatech.superschoolboyremastered.util.Utils.toOldColor

class TextHtmlTagHandler(private val secondaryColor: Color) : Html.TagHandler {

    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        if (tag.equals("header_size")) {
            handleTextSizeTag(opening, output)
        } else if(tag.equals("secondary_color")) {
            handleColorTag(opening, output)
        }
    }

    private fun handleTextSizeTag(opening: Boolean, output: Editable?) {
        val len: Int = output!!.length
        if (opening) {
            output.setSpan(RelativeSizeSpan(1.5f), len, len, Spannable.SPAN_MARK_MARK)
        } else {
            val obj = getLast(output, RelativeSizeSpan::class.java)
            val where = output.getSpanStart(obj)
            output.removeSpan(obj)
            if (where != len) {
                output.setSpan(
                    RelativeSizeSpan(1.5f),
                    where,
                    len,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun handleColorTag(opening: Boolean, output: Editable?) {
        val len: Int = output!!.length
        if (opening) {
            output.setSpan(ForegroundColorSpan(secondaryColor.toOldColor()), len, len, Spannable.SPAN_MARK_MARK)
        } else {
            val obj = getLast(output, ForegroundColorSpan::class.java)
            val where = output.getSpanStart(obj)
            output.removeSpan(obj)
            if (where != len) {
                output.setSpan(
                    ForegroundColorSpan(secondaryColor.toOldColor()),
                    where,
                    len,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun getLast(text: Editable, kind: Class<*>): Any? {
        val objs = text.getSpans(0, text.length, kind)
        return if (objs.isEmpty()) {
            null
        } else {
            for (i in objs.size downTo 1) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1]
                }
            }
            null
        }
    }
}