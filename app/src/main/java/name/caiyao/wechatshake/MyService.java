package name.caiyao.wechatshake;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MyService extends Service {

    Button btn_floatView;
    WindowManager wm;

    public static final String ON = "name.caiyao.wechatshake.on";
    public static final String OFF = "name.caiyao.wechatshake.off";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ON)){
            if (btn_floatView == null) {

                btn_floatView = new Button(getApplicationContext());
                btn_floatView.setText("摇一摇");

                wm = (WindowManager) getApplicationContext().getSystemService(
                        Context.WINDOW_SERVICE);
                final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

                params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;

                btn_floatView.setOnTouchListener(new View.OnTouchListener() {
                    int lastX, lastY;
                    int paramX, paramY;

                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                lastX = (int) event.getRawX();
                                lastY = (int) event.getRawY();
                                paramX = params.x;
                                paramY = params.y;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int dx = (int) event.getRawX() - lastX;
                                int dy = (int) event.getRawY() - lastY;
                                params.x = paramX + dx;
                                params.y = paramY + dy;
                                // 更新悬浮窗位置
                                wm.updateViewLayout(btn_floatView, params);
                                break;
                        }
                        return false;
                    }
                });

                btn_floatView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getContext().sendBroadcast(new Intent().setAction("name.caiyao.START"));
                    }
                });
                wm.addView(btn_floatView, params);
            }
        }else if(intent.getAction().equals(OFF)){
            if (btn_floatView != null) {
                wm.removeView(btn_floatView);
                btn_floatView = null;
            }
        }
        return START_STICKY;
    }
}
