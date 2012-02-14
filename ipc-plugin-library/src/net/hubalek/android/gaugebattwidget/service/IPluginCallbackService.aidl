package net.hubalek.android.gaugebattwidget.service;

interface IPluginCallbackService {
    void updateBatteryInfo (int level, int voltageMillivolts, int temperatureC, int status);
}
