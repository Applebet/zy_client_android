package ando.player.dialog;

public interface OnItemClickListener {
    void itemClicked();

    void onSpeedItemClick(int speedType, float speed, String name);

    void onDefinitionItemClick(int definition, String name, boolean isSmallDefinitionSetChange);

    void onTimingItemClick(int timing, boolean isSmallTimingSetChange);

    void showSmallTimingLayout();

    void showSmallDefinitionLayout();

    void showSmallSpreedLayout();

    void showSmallRouteLayout();
}