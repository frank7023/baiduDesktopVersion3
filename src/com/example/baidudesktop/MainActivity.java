package com.example.baidudesktop;

/**
 * ��Activity
 */
import com.example.myView.MyView;
import com.example.myView.MyView.OnTurnplateListener;
import com.example.myView.MyView.Point;
import com.example.utils.WindowData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTurnplateListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ��ȡ��Ļ�ߴ�
        WindowData.screen_height = getWindowManager().getDefaultDisplay()
                .getHeight();
        WindowData.screen_width = getWindowManager().getDefaultDisplay()
                .getWidth();
        // ������Ļ����
        getWindow().setBackgroundDrawableResource(
                R.drawable.trashcan_background);
        // ����View
        MyView myView = new MyView(this, WindowData.screen_width / 2,
                WindowData.screen_height / 2, WindowData.screen_width / 3);
        // ���ü���
        myView.setOnTurnplateListener(this);
        // ���View
        setContentView(myView);
    }

    /**
    * ����¼� ����Point��flag����packageName�����¼�
    */

    @Override
    public void onPointTouch(Point point) {

        Toast.makeText(this, String.valueOf(point.flag), Toast.LENGTH_SHORT)
                .show();
        // �������Ӧ��
        startApp(point.packageName);
        this.finish();

    }

    /**
    * ���ݰ�����Ӧ��
    *
    * @param packageName
    */
    public void startApp(String packageName) {
        Intent intent = MainActivity.this.getPackageManager()
                .getLaunchIntentForPackage(packageName);
        // �Ѱ�װ�� ֱ������
        if (intent != null) {
            startActivity(intent);
        }
    }

}
