package name.caiyao.wechatshake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static name.caiyao.wechatshake.MyService.OFF;
import static name.caiyao.wechatshake.MyService.ON;

public class MainActivity extends AppCompatActivity {
    Button btn;
    Button btn_off;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        btn_off = (Button) findViewById(R.id.btn_off);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, MyService.class).setAction(ON));
            }
        });
        btn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, MyService.class).setAction(OFF));
            }
        });
    }
}
