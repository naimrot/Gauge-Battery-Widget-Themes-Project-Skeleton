package net.hubalek.android.gaugebattwidget.themes.library;

interface IStatusBarInfoRemoteService {

    void updateStatusBarInfo(String title, String text, int level,String callbackPackageName, String callbackActivityName);
    void hideStatusBarInfo();

}
