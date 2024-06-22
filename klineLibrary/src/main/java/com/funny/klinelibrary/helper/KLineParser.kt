package com.funny.klinelibrary.helper

import com.funny.klinelibrary.entity.KLineItem
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import net.minidev.json.JSONValue

class KLineParser(val mKlineJson: String) {

    var klineList: MutableList<KLineItem> = mutableListOf()

    /**
     * 解析K线数据
     */
    fun parseKlineData() {
        val obj = JSONValue.parse(mKlineJson)
        if (obj is JSONArray) {
            for (i in obj.indices) {
                val jsonObject = obj[i] as JSONObject
                val kLineItem: KLineItem = getKLineItem(jsonObject)
                klineList.add(kLineItem)
            }
        }
    }

    /**
     * 获取每一个交易日数据
     */
    private fun getKLineItem(obj: JSONObject): KLineItem {
        return KLineItem(
            obj.getAsString("day"),
            (obj.getAsString("open").toFloat()),
            (obj.getAsString("high").toFloat()),
            (obj.getAsString("low").toFloat()),
            (obj.getAsString("close").toFloat()),
            (obj.getAsString("volume").toLong())
        )
    }
}