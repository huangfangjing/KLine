package com.funny.kline

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.funny.kline.databinding.ActivityMainBinding
import com.funny.klinelibrary.entity.KLineDrawItem
import com.funny.klinelibrary.helper.KLineSourceHelper
import com.funny.klinelibrary.inter.IChartDataCountListener
import com.funny.klinelibrary.inter.KlineGestureListener
import com.funny.klinelibrary.helper.KLineParser
import com.funny.klinelibrary.utils.LocalUtils
import com.funny.klinelibrary.utils.PaintUtils
import kotlin.math.max
import kotlin.math.min


class MainActivity : AppCompatActivity(), KlineGestureListener {

    private lateinit var mHelper: KLineSourceHelper

    private lateinit var mBinding: ActivityMainBinding

    private var mHandle = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarColor()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        PaintUtils.init(this)
        mHelper = KLineSourceHelper(onReadyListener)

        with(mBinding.klineGroup) {
            setKLineActionListener(this@MainActivity)
        }

        with(mBinding.tab) {
            setTitles(arrayOf("日线", "周线", "月线", "年线")).setIsNeedWeight(true)
                .setShowDivider(true).build()
            setOnTabClickListener { position, _ ->
                initData(position)
            }
        }
        mHandle.postDelayed({ mBinding.tab.setSelect(0) }, 500)
    }

    private fun initData(
        position: Int, getJsonName: () -> String = {
            when (position) {
                0 -> "k_day.json"
                1 -> "k_week.json"
                2 -> "k_month.json"
                else -> "k_year.json"
            }
        }
    ) {
        //模拟数据
        val kJson: String = LocalUtils.getFromAssets(this, getJsonName())
        val parser = KLineParser(kJson)
        parser.parseKlineData()
        mHelper.initKDrawData(parser.klineList, mBinding.klineView, mBinding.volumeView)
    }

    private val onReadyListener: IChartDataCountListener<MutableList<KLineDrawItem>> =
        IChartDataCountListener{ data, extremeValue ->
            mBinding.klineGroup.setData(data, extremeValue)
            mBinding.klineGroup.dispatchDrawData()
            mBinding.kLineData = data[data.size - 1]
        }

    override fun onChartTranslate(me: MotionEvent?, dX: Float) {
        mHelper.initKLineDrawData(dX, KLineSourceHelper.SourceType.MOVE)
    }

    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
        KLineSourceHelper.K_D_COLUMNS = (KLineSourceHelper.K_D_COLUMNS / scaleX).toInt()
        KLineSourceHelper.K_D_COLUMNS =
            max(
                KLineSourceHelper.MIN_COLUMNS,
                min(KLineSourceHelper.MAX_COLUMNS, KLineSourceHelper.K_D_COLUMNS)
            )
        mHelper.initKLineDrawData(0f, KLineSourceHelper.SourceType.SCALE)
    }

    override fun onLongPress(drawItem: KLineDrawItem) {
        mBinding.kLineData = drawItem
    }

    /**
     * 设置状态栏底色颜色
     */
    private fun setStatusBarColor() {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)

        //设置状态栏字体为白色
        val stateView: View = window.decorView
        var vis = stateView.systemUiVisibility
        vis = vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() //白色
        stateView.systemUiVisibility = vis //设置状态栏字体颜色
    }
}