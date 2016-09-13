package name.caiyao.wechatshake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.view.KeyEvent;

import java.lang.reflect.Field;
import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by 蔡小木 on 2016/8/23 0023.
 */

public class MainHook implements IXposedHookLoadPackage {
    private int count = 1;
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
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("android")) {
            final Class<?> phoneWindowManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                phoneWindowManager = XposedHelpers.findClass("com.android.server.policy.PhoneWindowManager", loadPackageParam.classLoader);
            } else {
                phoneWindowManager = XposedHelpers.findClass("com.android.internal.policy.impl.PhoneWindowManager", loadPackageParam.classLoader);
            }
            XposedBridge.hookAllMethods(phoneWindowManager, "interceptKeyBeforeQueueing", new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    int v1 = ((KeyEvent) param.args[0]).getKeyCode();
                    Field contextField = XposedHelpers.findField(phoneWindowManager, "mContext");
                    contextField.setAccessible(true);
                    final Context context = (Context) contextField.get(param.thisObject);
                    if (v1 == KeyEvent.KEYCODE_K) {
                        context.sendBroadcast(new Intent("name.caiyao.START"));
                    }
                }
            });
            final Class<?> packageManager = XposedHelpers.findClass("com.android.server.pm.PackageManagerService", loadPackageParam.classLoader);
            XposedBridge.hookAllMethods(packageManager, "getPackageInfo", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    PackageInfo packageInfo = (PackageInfo) param.getResult();
                    if (packageInfo.packageName.equals(packages[0]) ||
                            packageInfo.packageName.equals(packages[1]) ||
                            packageInfo.packageName.equals(packages[2]) ||
                            packageInfo.packageName.equals(packages[3]) ||
                            packageInfo.packageName.equals(packages[4]) ||
                            packageInfo.packageName.equals(packages[5]) ||
                            packageInfo.packageName.equals(packages[6]) ||
                            packageInfo.packageName.equals(packages[7]) ||
                            packageInfo.packageName.equals(packages[8]) ||
                            packageInfo.packageName.equals(packages[9]) || packageInfo.packageName.equals("com.tencent.mm")) {
                        packageInfo.versionCode = 800;
                        packageInfo.versionName = "6.2.25";
                        param.setResult(packageInfo);
                    }
                    XposedBridge.log("TESTHook" + packageInfo.packageName);
                }
            });
        }

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
            Context systemContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
            systemContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    isShake = true;
                    count = 1;
                }
            }, new IntentFilter("name.caiyao.START"));
            final Class<?> sensorEL = XposedHelpers.findClass("android.hardware.SystemSensorManager$SensorEventQueue", loadPackageParam.classLoader);
            XposedBridge.hookAllMethods(sensorEL, "dispatchSensorEvent", new XC_MethodHook() {

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
        }
    }
}
