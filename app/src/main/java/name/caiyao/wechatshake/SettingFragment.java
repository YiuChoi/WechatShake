package name.caiyao.wechatshake;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

/**
 * Created by 蔡小木 on 2016/8/25 0025.
 */
public class SettingFragment extends PreferenceFragment {
    private static final String START = "name.caiyao.wechatshake.SHAKE_START";
    private static final String STOP = "name.caiyao.wechatshake.SHAKE_STOP";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(1);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findPreference("open").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean isOpen = getPreferenceManager().getSharedPreferences().getBoolean("open", true);
                if (isOpen) {
                    getActivity().sendBroadcast(new Intent(START));
                } else {
                    getActivity().sendBroadcast(new Intent(STOP));
                }
                return true;
            }
        });
        findPreference("co").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3DG3p0IBE-czsvms3HmfuB4aLAsa-rI9Nh"));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "手机未安装QQ！", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}
