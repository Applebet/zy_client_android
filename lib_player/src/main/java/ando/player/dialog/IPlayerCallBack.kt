package ando.player.dialog

interface IPlayerCallBack {
    fun onItemClick()
    fun onListItemClick(item: String,position:Int)
    fun onSpeedItemClick(speedType: Int, speed: Float, name: String?)
    fun onDefinitionItemClick(definition: Int, name: String?, isSmallDefinitionSetChange: Boolean)
    fun onTimingItemClick(timing: Int, isSmallTimingSetChange: Boolean)
    fun showSmallTimingLayout()
    fun showSmallDefinitionLayout()
    fun showSmallSpreedLayout()
    fun showSmallRouteLayout()
}