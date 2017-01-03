package com.lechatelier.indimeter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.io.File;

public class MainActivity extends Activity {
    protected static final String EXTRA_AUTO = "AUTO";
    protected static final String EXTRA_CODE = "CODE";

    /* renamed from: com.lechatelier.indimeter.MainActivity.1 */
    class C00721 implements OnClickListener {
        C00721() {
        }

        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, ProcessImage.class);
            i.putExtra(MainActivity.EXTRA_AUTO, true);
            MainActivity.this.startActivity(i);
        }
    }

    /* renamed from: com.lechatelier.indimeter.MainActivity.2 */
    class C00732 implements OnClickListener {
        C00732() {
        }

        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, ProcessImage.class);
            i.putExtra(MainActivity.EXTRA_AUTO, false);
            MainActivity.this.startActivity(i);
        }
    }

    /* renamed from: com.lechatelier.indimeter.MainActivity.3 */
    class C00743 implements OnClickListener {
        C00743() {
        }

        public void onClick(View v) {
            File dir = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append("/indimeter").toString());
            dir.mkdirs();
            File photo = new File(dir, ".camera.jpg");
            Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
            i.putExtra("output", Uri.fromFile(photo));
            MainActivity.this.startActivityForResult(i, 1);
        }
    }

    /* renamed from: com.lechatelier.indimeter.MainActivity.4 */
    class C00754 implements OnClickListener {
        C00754() {
        }

        public void onClick(View v) {
            File dir = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append("/indimeter").toString());
            dir.mkdirs();
            File photo = new File(dir, ".camera.jpg");
            Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
            i.putExtra("output", Uri.fromFile(photo));
            MainActivity.this.startActivityForResult(i, 2);
        }
    }

    /* renamed from: com.lechatelier.indimeter.MainActivity.5 */
    class C00765 implements OnClickListener {
        C00765() {
        }

        public void onClick(View v) {
            Intent i = new Intent("android.intent.action.PICK");
            i.setType("vnd.android.cursor.dir/image");
            MainActivity.this.startActivityForResult(i, 3);
        }
    }

    /* renamed from: com.lechatelier.indimeter.MainActivity.6 */
    class C00776 implements OnClickListener {
        C00776() {
        }

        public void onClick(View v) {
            Intent i = new Intent("android.intent.action.PICK");
            i.setType("vnd.android.cursor.dir/image");
            MainActivity.this.startActivityForResult(i, 4);
        }
    }

    /* renamed from: com.lechatelier.indimeter.MainActivity.7 */
    class C00797 implements OnClickListener {

        /* renamed from: com.lechatelier.indimeter.MainActivity.7.1 */
        class C00781 implements DialogInterface.OnClickListener {
            private final /* synthetic */ EditText val$param1;
            private final /* synthetic */ EditText val$param2;
            private final /* synthetic */ EditText val$param3;
            private final /* synthetic */ SharedPreferences val$pref;

            C00781(SharedPreferences sharedPreferences, EditText editText, EditText editText2, EditText editText3) {
                this.val$pref = sharedPreferences;
                this.val$param1 = editText;
                this.val$param2 = editText2;
                this.val$param3 = editText3;
            }

            public void onClick(DialogInterface dialog, int id) {
                int parseInt;
                Editor edit = this.val$pref.edit();
                String str = "param1";
                if (this.val$param1.getText().length() != 0) {
                    parseInt = Integer.parseInt(this.val$param1.getText().toString());
                } else {
                    parseInt = this.val$pref.getInt("param1", 160);
                }
                edit = edit.putInt(str, parseInt);
                str = "param2";
                if (this.val$param2.getText().length() != 0) {
                    parseInt = Integer.parseInt(this.val$param2.getText().toString());
                } else {
                    parseInt = this.val$pref.getInt("param2", 40);
                }
                edit = edit.putInt(str, parseInt);
                str = "param3";
                if (this.val$param3.getText().length() != 0) {
                    parseInt = Integer.parseInt(this.val$param3.getText().toString());
                } else {
                    parseInt = this.val$pref.getInt("param3", 12);
                }
                edit.putInt(str, parseInt).commit();
            }
        }

        C00797() {
        }

        public void onClick(View v) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            LayoutParams wmhw = new LayoutParams(-1, -2);
            LinearLayout ll = new LinearLayout(MainActivity.this);
            ll.setOrientation(1);
            ll.setLayoutParams(wmhw);
            EditText param1 = new EditText(MainActivity.this);
            param1.setMaxLines(1);
            param1.setHint("\uc790\ub3d9 - \uc6d0 \uac10\uc9c0 \ubbfc\uac10\ub3c4 1 (" + pref.getInt("param1", 160) + ")");
            param1.setInputType(2);
            param1.setLayoutParams(wmhw);
            ll.addView(param1);
            EditText param2 = new EditText(MainActivity.this);
            param2.setMaxLines(1);
            param2.setHint("\uc790\ub3d9 - \uc6d0 \uac10\uc9c0 \ubbfc\uac10\ub3c4 2 (" + pref.getInt("param2", 40) + ")");
            param2.setInputType(2);
            param2.setLayoutParams(wmhw);
            ll.addView(param2);
            EditText param3 = new EditText(MainActivity.this);
            param3.setMaxLines(1);
            param3.setHint("\uc218\ub3d9 - \uc810 \uac04\uaca9 (" + pref.getInt("param3", 12) + ")");
            param3.setInputType(2);
            param3.setLayoutParams(wmhw);
            ll.addView(param3);
            Builder alert = new Builder(MainActivity.this);
            alert.setView(ll).setPositiveButton(17039370, new C00781(pref, param1, param2, param3)).setNegativeButton(17039360, null);
            AlertDialog alertdialog = alert.create();
            alertdialog.setTitle("\uc124\uc815");
            alertdialog.show();
        }
    }

    /* renamed from: com.lechatelier.indimeter.MainActivity.8 */
    class C00808 implements OnClickListener {
        C00808() {
        }

        public void onClick(View v) {
            Builder alert = new Builder(MainActivity.this);
            alert.setMessage("Made by \ub099\uc0dd\uace0\ub4f1\ud559\uad50 1\ud559\ub144 3\ubc18 - Le Chatelier\n2014\ud559\ub144\ub3c4 \ud504\ub85c\uc81d\ud2b8 \ud559\uc2b5\uc758 \uc77c\ud658\uc73c\ub85c \uc81c\uc791\ud568\n\ud504\ub85c\uc81d\ud2b8 \uc218\ud589 \ubc0f \uc560\ud50c\ub9ac\ucf00\uc774\uc158 \uc81c\uc791\uc5d0 \ub3c4\uc6c0\uc744 \uc8fc\uc2e0 \uc9c0\ub3c4\uad50\uc0ac\uc774\uc2e0 \uc774\uc724\uc815 \uc120\uc0dd\ub2d8\uc744 \ube44\ub86f\ud55c \ub099\uc0dd\uace0\ub4f1\ud559\uad50 \uc120\uc0dd\ub2d8\uaed8 \uac10\uc0ac\uc758 \ub9d0\uc500 \ub4dc\ub9bd\ub2c8\ub2e4.\n\n\uc790\ub3d9 : \uc790\ub3d9\uc73c\ub85c \uc6d0\uc744 \uac10\uc9c0(\uc124\uc815\uc5d0\uc11c \ubbfc\uac10\ub3c4 \uc124\uc815 \uac00\ub2a5, \ub0ae\uc744\uc218\ub85d \ubbfc\uac10)\ud558\uc5ec, \uc6d0 \ub0b4\ubd80\uc758 \ud3c9\uade0 \uc0c9\uc744 \uad6c\ud558\uc5ec pH \uacc4\uc0b0\n\uc218\ub3d9 : \ud55c \uc810(\uc2e4\uc2dc\uac04\uc758 \uacbd\uc6b0 \uc911\uc2ec, \ucd2c\uc601 \ubc0f \uc568\ubc94\uc758 \uacbd\uc6b0 \uc120\ud0dd \uc810 - \uc9e7\uac8c \ud130\uce58\uc2dc \uc120\ud0dd, \uae38\uac8c \ud130\uce58\uc2dc \uc804\uccb4 \uc120\ud0dd \ud574\uc81c)\uacfc \uc778\uc811 4\uac1c \uc810(\uc124\uc815\uc5d0\uc11c \uc810 \uac04\uaca9 \uc870\uc808 \uac00\ub2a5, \ub0ae\uc744\uc218\ub85d \ub113\uc74c)\uc758 \ud3c9\uade0 \uc0c9\uc744 \uad6c\ud558\uc5ec pH \uacc4\uc0b0\n\n\uc2e4\uc2dc\uac04 : \uc2e4\uc2dc\uac04\uc73c\ub85c \uce74\uba54\ub77c\ub85c \ubd80\ud130 \uc601\uc0c1\uc744 \ubc1b\uc544 \ubd84\uc11d, \ud55c \ubc88 \ud130\uce58\uc2dc \uc815\uc9c0\n\ucd2c\uc601 : \uce74\uba54\ub77c \uc571\uc744 \uc2e4\ud589, \ucd2c\uc601\ub41c \uc774\ubbf8\uc9c0\ub97c \ubc1b\uc544 \ubd84\uc11d\n\uc568\ubc94 : \uc568\ubc94 \uc571\uc744 \uc2e4\ud589, \uc120\ud0dd\ub41c \uc774\ubbf8\uc9c0\ub97c \ubc1b\uc544 \ubd84\uc11d\n\n\ub450 \ubc88 \ud130\uce58\uc2dc SD\uce74\ub4dc Indimeter \ud3f4\ub354\uc5d0 \uc774\ubbf8\uc9c0 \ud30c\uc77c\ub85c \uc800\uc7a5").setPositiveButton(17039370, null);
            AlertDialog alertdialog = alert.create();
            alertdialog.setTitle(C0084R.string.app_name);
            alertdialog.show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0084R.layout.activity_main);
        Button button2 = (Button) findViewById(C0084R.id.button2);
        Button button3 = (Button) findViewById(C0084R.id.button3);
        Button button4 = (Button) findViewById(C0084R.id.button4);
        Button button5 = (Button) findViewById(C0084R.id.button5);
        Button button6 = (Button) findViewById(C0084R.id.button6);
        Button button7 = (Button) findViewById(C0084R.id.button7);
        Button button8 = (Button) findViewById(C0084R.id.button8);
        ((Button) findViewById(C0084R.id.button1)).setOnClickListener(new C00721());
        button2.setOnClickListener(new C00732());
        button3.setOnClickListener(new C00743());
        button4.setOnClickListener(new C00754());
        button5.setOnClickListener(new C00765());
        button6.setOnClickListener(new C00776());
        button7.setOnClickListener(new C00797());
        button8.setOnClickListener(new C00808());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            File dir = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append("/indimeter").toString());
            dir.mkdirs();
            Intent i = new Intent(this, ProcessImage.class);
            if (requestCode == 1 || requestCode == 2) {
                i.setData(Uri.fromFile(new File(dir, ".camera.jpg")));
            } else {
                i.setData(data.getData());
            }
            i.putExtra(EXTRA_CODE, requestCode);
            startActivity(i);
        }
    }
}
