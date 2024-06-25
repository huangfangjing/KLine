package com.funny.klinelibrary.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.funny.klinelibrary.R
import com.funny.klinelibrary.entity.ExtremeValue
import com.funny.klinelibrary.entity.KLineDrawItem
import com.funny.klinelibrary.helper.KLineSourceHelper
import com.funny.klinelibrary.utils.DateUtils
import com.funny.klinelibrary.utils.DisplayUtils
import com.funny.klinelibrary.utils.NumFormatUtils
import com.funny.klinelibrary.utils.PaintUtils
import kotlin.math.max
import kotlin.math.min

/**
 *@author : hfj
 */
open class BaseChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    //绘图基本
    lateinit var mBitmap: Bitmap
    lateinit var mCanvas: Canvas//画布
    lateinit var mRectF: RectF//绘图区域
    lateinit var mViewRectF: RectF//view全部区域

    lateinit var mDateRectF: RectF//十字线日期点击区域，弹出分时图

    var isDrawHighArrow = false //绘制最高价箭头
    var isDrawLowArrow = false //绘制最高价箭头

    //数据
    var textPadding = 0 //绘制文字间距
    var calePadding = 0 //绘制刻度间距

    init {
        initView()
    }

    constructor(context: Context?) : this(context, null)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mRectF = RectF(
            DisplayUtils.dip2px(context, 10.0f).toFloat(),
            DisplayUtils.dip2px(context, 20.0f).toFloat(),
            (w - DisplayUtils.dip2px(context, 10.0f)).toFloat(),
            (h - DisplayUtils.dip2px(context, 15.0f)).toFloat()
        )
        mViewRectF = RectF(0F, 0F, w.toFloat(), h.toFloat())
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas()
        mCanvas.setBitmap(mBitmap)
    }

    open fun initView() {
        textPadding = DisplayUtils.dip2px(context, 5.0f)
        calePadding = DisplayUtils.dip2px(context, 3.0f)
    }

    fun getLastDrawItem(): KLineDrawItem {
        return getKLineDatas()[getKLineDatas().size - 1]
    }

    fun getFirstDrawItem(): KLineDrawItem {
        return getKLineDatas()[0]
    }

    fun getFocusDrawItem(): KLineDrawItem {
        return getKLineDatas()[max(0, min(getFocusIndex(), getKLineDatas().size - 1))]
    }

    /**
     * 绘制内容
     */
    open fun drawData() {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR) //清空画板
        drawOutLine(getExtremeValue())
        isDrawHighArrow = false
        isDrawLowArrow = false
    }

    /**
     * 极值
     */
    fun getExtremeValue(): ExtremeValue {
        return (parent as KLineViewGroup).mExtremeValue
    }

    /**
     * 数据源
     */
    private fun getViewGroup(): KLineViewGroup {
        return (parent as KLineViewGroup)
    }

    /**
     * 数据源
     */
    fun getKLineDatas(): MutableList<KLineDrawItem> {
        return getViewGroup().mKLineDatas
    }

    /**
     * 选中的index
     */
    fun getFocusIndex(): Int {
        return getViewGroup().mFocusIndex
    }

    /**
     * 选中的focusPoint
     */
    fun getFocusPoint(): PointF {
        return getViewGroup().mFocusPoint
    }

    /**
     * 绘制边框
     */
    open fun drawOutLine(mExtremeValue: ExtremeValue) {

    }

    fun getString(id: Int): String {
        return context.resources.getString(id)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!mBitmap.isRecycled) {
            canvas.drawBitmap(mBitmap, 0f, 0f, null)
        }
    }

    /**
     * 绘制长按时十字线
     */
    open fun drawCrossLine() {

        when (KLineViewGroup.CHART_STATE) {

            KLineViewGroup.STATE_SINGLE_CLICK, KLineViewGroup.STATE_LONG_PRESSED -> {

                // 附图实际y轴位置
                val focusY: Float = getFocusPoint().y - y

                if (mViewRectF.contains(getFocusPoint().x, focusY)) {
                    mCanvas.drawLine(
                        mRectF.left, focusY, mRectF.right, focusY,
                        PaintUtils.FOCUS_LINE_PAINT
                    )
                }
                mCanvas.drawLine(
                    getFocusPoint().x, 0f, getFocusPoint().x, mRectF.bottom,
                    PaintUtils.FOCUS_LINE_PAINT
                )
            }

            KLineViewGroup.STATE_SHOW_MINTUNE -> {
                mCanvas.drawLine(
                    getFocusPoint().x, mViewRectF.top, getFocusPoint().x, mViewRectF.height(),
                    PaintUtils.DOT_LINE_PAINT
                )
            }
        }
    }

    fun drawPopText(
        canvas: Canvas, popRect: RectF, title: String, value: String, isFall: Boolean,
        y: Float
    ) {
        val rect = Rect()
        PaintUtils.TEXT_POP_PAINT.getTextBounds(value, 0, value.length, rect)
        canvas.drawText(
            title,
            popRect.left + textPadding,
            popRect.top + y - textPadding,
            PaintUtils.TEXT_POP_PAINT
        )
        if (isFall) {
            canvas.drawText(
                value,
                popRect.right - rect.width() - textPadding,
                popRect.top + y - textPadding,
                PaintUtils.TEXT_GREEN_PAINT
            )
        } else {
            canvas.drawText(
                value,
                popRect.right - rect.width() - textPadding,
                popRect.top + y - textPadding,
                PaintUtils.TEXT_RED_PAINT
            )
        }
    }
}