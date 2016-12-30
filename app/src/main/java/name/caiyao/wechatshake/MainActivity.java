package name.caiyao.wechatshake;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btn;
    Button btn_off;
    Button btn_floatView;
    WindowManager wm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        btn_off = (Button) findViewById(R.id.btn_off);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        btn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_floatView != null) {
                    wm.removeView(btn_floatView);
                    btn_floatView = null;
                }
            }
        });
    }
}
