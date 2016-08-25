package name.caiyao.wechatshake;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by 蔡小木 on 2016/8/23 0023.
 */

public class MainHook implements IXposedHookLoadPackage {
    private int count = 1;
    private int id = 0;
    private static boolean isShake = false;
    private String[] packages = {
            "com.tence01.mm",
            "com.tence02.mm",
            "com.tence03.mm",
            "com.tence04.mm",
            "com.tence05.mm",
            "com.tence06.mm",
            "com.tence07.mm",
            "com.tence08.mm",
            "com.tence09.mm",
            "com.tence10.mm"
    };

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(packages[0]) ||
                loadPackageParam.packageName.equals(packages[1]) ||
                loadPackageParam.packageName.equals(packages[2]) ||
                loadPackageParam.packageName.equals(packages[3]) ||
                loadPackageParam.packageName.equals(packages[4]) ||
                loadPackageParam.packageName.equals(packages[5]) ||
                loadPackageParam.packageName.equals(packages[6]) ||
                loadPackageParam.packageName.equals(packages[7]) ||
                loadPackageParam.packageName.equals(packages[8]) ||
                loadPackageParam.packageName.equals(packages[9]) || loadPackageParam.packageName.equals("com.tencent.mm")) {
            final Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
            final Context systemContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
            systemContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    isShake = true;
                    count = 1;
                    if (id == packages.length) {
                        id = 0;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent();
                            intent.setClassName(packages[id], packages[id] + ".ui.LauncherUI");
                            intent.putExtra("shake", "");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            systemContext.startActivity(intent);
                        }
                    }.start();
                }
            }, new IntentFilter("name.caiyao.START"));

            final Class<?> sensorEL = findClass("android.hardware.SystemSensorManager$SensorEventQueue", loadPackageParam.classLoader);
            hookAllMethods(sensorEL, "dispatchSensorEvent", new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (isShake) {
                        count++;
                        ((float[]) param.args[1])[0] = new Random().nextFloat() * 1200f + 125f;
                        if (count == 200) {
                            isShake = false;
                        }
                    }
                }
            });
            //进入检测
            // com.tencent.mm/.plugin.shake.ui.ShakeReportUI
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                findAndHookMethod(Application.class, "dispatchActivityResumed", Activity.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (((Activity) param.args[0]).getClass().getName().equals(loadPackageParam.packageName + ".plugin.shake.ui.ShakeReportUI")) {
                            systemContext.sendBroadcast(new Intent("name.caiyao.START"));
                        }
                    }
                });

            } else {
                findAndHookMethod(loadPackageParam.packageName + ".plugin.shake.ui.ShakeReportUI", loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        systemContext.sendBroadcast(new Intent("name.caiyao.START"));
                    }
                });
            }

            //自动进入
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                findAndHookMethod(Application.class, "dispatchActivityResumed", Activity.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (((Activity) param.args[0]).getClass().getName().equals(loadPackageParam.packageName + ".ui.LauncherUI")) {
                            Activity activity = (Activity) param.args[0];
                            if (activity != null) {
                                Intent intent = activity.getIntent();
                                if (intent != null) {
                                    String className = intent.getComponent().getClassName();
                                    if (!TextUtils.isEmpty(className) && className.equals(loadPackageParam.packageName + ".ui.LauncherUI") && intent.hasExtra("shake")) {
                                        Intent donateIntent = new Intent();
                                        donateIntent.setClassName(activity, loadPackageParam.packageName + ".plugin.shake.ui.ShakeReportUI");
                                        activity.startActivity(donateIntent);
                                    }
                                }
                            }
                        }
                    }
                });
            } else {
                findAndHookMethod(loadPackageParam.packageName + ".ui.LauncherUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Activity activity = (Activity) param.thisObject;
                        if (activity != null) {
                            Intent intent = activity.getIntent();
                            if (intent != null) {
                                String className = intent.getComponent().getClassName();
                                if (!TextUtils.isEmpty(className) && className.equals(loadPackageParam.packageName + ".ui.LauncherUI") && intent.hasExtra("shake")) {
                                    Intent donateIntent = new Intent();
                                    donateIntent.setClassName(activity, loadPackageParam.packageName + ".plugin.shake.ui.ShakeReportUI");
                                    activity.startActivity(donateIntent);
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}