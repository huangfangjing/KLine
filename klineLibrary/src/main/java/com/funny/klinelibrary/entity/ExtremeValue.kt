package com.funny.klinelibrary.entity
 class ExtremeValue {
     /**
      * 蜡烛线实际最大价格
      */
     var maxPrice: Float = 0f

     /**
      * 蜡烛线实际最小价格
      */
     var minPrice: Float = 0f

     /**
      * 绘图时使用的最大价格，为了预留边距
      */
     var mKMaxPrice: Float = 0f


     /**
      * 绘图时使用的最小价格，为了预留边距
      */
     var mKMinPrice: Float = 0f


     /**
      * 蜡烛线实际最大成交量
      */
     var maxVolume: Float = 0f

     /**
      * 绘图时使用的最大成交量，为了预留边距
      */
     var mKMaxVolume: Float = 0f

 }