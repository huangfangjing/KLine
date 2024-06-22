package com.funny.klinelibrary.inter

import com.funny.klinelibrary.entity.ExtremeValue

fun interface IChartDataCountListener<T> {

    fun onReady(data: T, extremeValue: ExtremeValue);
}
