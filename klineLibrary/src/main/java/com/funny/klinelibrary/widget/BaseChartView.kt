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
    private lateinit var mBitmap: Bitmap
    lateinit var mCanvas: Canvas
    lateinit var mRectF: RectF
    lateinit var mViewRectF: RectF

    var isDrawHighArrow = false //绘制最高价箭头
    var isDrawLowArrow = false //绘制最高价箭头

    //数据
    var textPadding = 0 //绘制文字间距
    var calePadding = 0 //绘制刻度间距

    //长按
    var isLongPressState = false //记录长按状态
    var mFocusPoint: PointF = PointF() //长按时十字线的交叉点
    var mFocusIndex = 0 //长按时选中的k线list的index
    private var popRect = RectF() //长按弹出的弹框

    init {
        initView()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mRectF = RectF(
            DisplayUtils.dip2px(context, 10.0f).toFloat(),
            DisplayUtils.dip2px(context, 20.0f).toFloat(),
            (w - DisplayUtils.dip2px(context, 10.0f)).toFloat(),
            (h - DisplayUtils.dip2px(context, 20.0f)).toFloat()
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


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!mBitmap.isRecycled) {
            mBitmap.recycle()
        }
    }

    fun getLastDrawItem(): KLineDrawItem {
        return getKLineDatas()[getKLineDatas().size - 1]
    }

    fun getFirstDrawItem(): KLineDrawItem {
        return getKLineDatas()[0]
    }

    fun getFocusDrawItem(): KLineDrawItem {
        return getKLineDatas()[max(0, min(mFocusIndex, getKLineDatas().size - 1))]
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
    fun getKLineDatas(): MutableList<KLineDrawItem> {
        return (parent as KLineViewGroup).mKLineDatas
    }

    /**
     * 绘制边框
     */
    open fun drawOutLine(mExtremeValue: ExtremeValue) {

    }

    private fun getString(id: Int): String {
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
        if (isLongPressState) {
            mFocusIndex =
                (((mFocusPoint.x - mRectF.left) * KLineSourceHelper.K_D_COLUMNS / mRectF.width()).toInt())
            mFocusIndex = max(0, min(mFocusIndex, KLineSourceHelper.K_D_COLUMNS - 1))

            if (mViewRectF.contains(mFocusPoint.x, mFocusPoint.y)) {
                mCanvas.drawLine(
                    mRectF.left, mFocusPoint.y, mRectF.right, mFocusPoint.y,
                    PaintUtils.FOCUS_LINE_PAINT
                )
            }
            mCanvas.drawLine(
                mFocusPoint.x, mRectF.top, mFocusPoint.x, measuredHeight.toFloat(),
                PaintUtils.FOCUS_LINE_PAINT
            )
            showLongPressDialog()
            drawFocusDateValue(mFocusPoint.x, mFocusPoint.y)
        }
    }

    private fun drawFocusDateValue(focusX: Float, focusY: Float) {
        val focusDrawItem = getFocusDrawItem()
        val dateText = DateUtils.getYMD(focusDrawItem.day)
        val dateTextWidth = PaintUtils.WHITE_PAINT.measureText(dateText)
        //防止画出边界外
        val left = min(
            mRectF.right - dateTextWidth - textPadding * 2,
            max(mRectF.left, focusX - dateTextWidth / 2 - textPadding)
        )
        mCanvas.drawRect(
            left,
            mRectF.bottom,
            left + dateTextWidth + textPadding * 2,
            mRectF.bottom + DisplayUtils.dip2px(context, 15.0f),
            PaintUtils.BLUE_RECTF_PAINT
        )

        mCanvas.drawText(
            dateText,
            max(
                mRectF.left + textPadding.toFloat(),
                min(focusX - dateTextWidth / 2, mRectF.right - textPadding - dateTextWidth)
            ),
            mRectF.bottom + textPadding * 2,
            PaintUtils.WHITE_PAINT
        )
        if (focusY in mRectF.top + DisplayUtils.dip2px(
                context,
                15.0f
            ) / 2..mRectF.bottom - DisplayUtils.dip2px(context, 15.0f) / 2
        ) {
            val focusValue =
                (mRectF.height() - focusY + mRectF.top) / mRectF.height() *
                        (getExtremeValue().mKMaxPrice - getExtremeValue().mKMinPrice) + getExtremeValue().mKMinPrice
            val focusValueText = NumFormatUtils.formatFloat(focusValue, 2).toString()
            val focusValueWidth = PaintUtils.WHITE_PAINT.measureText(focusValueText)
            mCanvas.drawRect(
                mRectF.left,
                focusY - DisplayUtils.dip2px(context, 15.0f) / 2,
                focusValueWidth + mRectF.left + textPadding * 2,
                focusY + DisplayUtils.dip2px(context, 15.0f) / 2,
                PaintUtils.BLUE_RECTF_PAINT
            )
            mCanvas.drawText(
                focusValueText,
                mRectF.left + textPadding,
                focusY - DisplayUtils.dip2px(context, 15.0f) / 2 + textPadding * 2,
                PaintUtils.WHITE_PAINT
            )
        }

    }

    /**
     * 长按显示的弹框内容
     */
    private fun showLongPressDialog() {
        val item = getKLineDatas()[max(
            0,
            min(mFocusIndex, (getKLineDatas().size - 1))
        )]
        val popRectWith = DisplayUtils.dip2px(context, 100f)
        val popRectHeight = DisplayUtils.dip2px(context, 110f)
        val left = when (mFocusPoint.x > mRectF.centerX()) {
            true ->
                mFocusPoint.x - DisplayUtils.dip2px(context, 50f) - popRectWith

            else -> mFocusPoint.x + DisplayUtils.dip2px(context, 50f)
        }
        val top: Float = mRectF.top + 50
        popRect.left = left
        popRect.top = top
        popRect.right = left + popRectWith
        popRect.bottom = top + popRectHeight
        mCanvas.drawRect(popRect, PaintUtils.POP_DIALOG_PAINT)

        val perHeight = popRect.height() / 6

        drawPopText(
            mCanvas,
            popRect,
            getString(R.string.open),
            java.lang.String.valueOf(item.open),
            item.compare(item.open),
            perHeight
        )
        drawPopText(
            mCanvas,
            popRect,
            getString(R.string.hign),
            java.lang.String.valueOf(item.high),
            item.compare(item.high),
            perHeight * 2
        )
        drawPopText(
            mCanvas,
            popRect,
            getString(R.string.low),
            java.lang.String.valueOf(item.low),
            item.compare(item.low),
            perHeight * 3
        )
        drawPopText(
            mCanvas,
            popRect,
            getString(R.string.close),
            java.lang.String.valueOf(item.close),
            item.isFall,
            perHeight * 4
        )
        drawPopText(
            mCanvas,
            popRect,
            getString(R.string.diff),
            item.increaseExtra,
            item.isFall,
            perHeight * 5
        )
        val chg: Float = (item.close - item.preClose) * 100 / item.preClose
        val chgDesc = NumFormatUtils.formatFloat(chg, 2, true, true, "--", "--", false)
        drawPopText(
            mCanvas,
            popRect,
            getString(R.string.chg),
            chgDesc,
            item.isFall,
            perHeight * 6
        )
    }

    private fun drawPopText(
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