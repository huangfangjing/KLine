这是一款使用kotlin语言开发的股票K线走势行情图，效果如下：
![image](https://github.com/huangfangjing/KLine/blob/master/show1.jpg)  ![image](https://github.com/huangfangjing/KLine/blob/master/show2.jpg)

项目中使用到的相关技术
1.自定义View（这里的方式是使用canvas绘图的方式，包含drawPath，drawRectF,drawText.drawBitmap等等）
2.使用GestureDetector实现用户手势检测,项目使用到的手势分别为点击(onSingleTapUp),长按（onLongPress），滑动（onScroll），长按后滑动（MotionEvent.ACTION_MASK）
  缩放（MotionEvent.ACTION_MASK 判断event.pointerCount == 2）
3.onTouch事件传递，本项目包含三个自定义的View，KLineViewGroup（父布局），KLineView（子View，K线图），VolumeView（子View，成交量图）
  项目使用了最简单的方式，集中在父View监听onTouch事件，然后根据需要传递到子View并交给子类具体实现。
4.K线数据的指标计算和绘制位置计算（这个不是项目的技术关键，虽然有些算起来头大）
5.DataBinding数据绑定

部分实现代码

//使用模拟数据（解析asset数据），数据准备完毕后回调
private val onReadyListener: IChartDataCountListener<MutableList<KLineDrawItem>> =
        IChartDataCountListener{ data, extremeValue ->
            mBinding.klineGroup.setData(data, extremeValue) //设置数据源
            mBinding.klineGroup.dispatchDrawData()//分发子类分别绘制
            mBinding.kLineData = data[data.size - 1]//DataBinding数据绑定
        }

 //根据自己需要定义回调接口

//手势滑动
override fun onChartTranslate(me: MotionEvent?, dX: Float) {
        mHelper.initKLineDrawData(dX, KLineSourceHelper.SourceType.MOVE)
        }

 //手势缩放   
override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
        KLineSourceHelper.K_D_COLUMNS = (KLineSourceHelper.K_D_COLUMNS / scaleX).toInt()
        KLineSourceHelper.K_D_COLUMNS =
            max(
                KLineSourceHelper.MIN_COLUMNS,
                min(KLineSourceHelper.MAX_COLUMNS, KLineSourceHelper.K_D_COLUMNS)
            )
        mHelper.initKLineDrawData(0f, KLineSourceHelper.SourceType.SCALE)
    }

//长按
override fun onLongPress(drawItem: KLineDrawItem) {
        mBinding.kLineData = drawItem
    }
