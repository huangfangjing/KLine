package com.funny.klinelibrary.entity

data class KLineItem(
    /**
     * 交易日日期
     */
    var day: String,

    /**
     * 开盘价
     */
    var open: Float ,

    /**
     * 最高价
     */
    var high: Float ,

    /**
     * 最低价
     */
    var low: Float ,

    /**
     * 收盘价
     */
    var close: Float,

    /**
     * 成交量
     */
    var volume: Long

)