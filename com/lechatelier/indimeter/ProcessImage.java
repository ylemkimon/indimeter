package com.lechatelier.indimeter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.media.TransportMediator;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

@SuppressLint({"ClickableViewAccessibility", "HandlerLeak"})
public class ProcessImage extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final long DOUBLE_CLICK_INTERVAL = 250;
    private static final long LONG_CLICK_INTERVAL = 1000;
    private boolean mAuto;
    private boolean mCapture;
    private Mat mClone;
    private boolean mEnabled;
    private boolean mHasClickedDown;
    private boolean mHasDoubleClicked;
    private long mLastPressTime;
    private BaseLoaderCallback mLoaderCallback;
    Handler mLongClick;
    private CameraBridgeViewBase mOpenCvCameraView;
    private List<Point> mPoint;
    private boolean mRealtime;
    private Mat mRgba;
    private float mScale;
    private int mXDiff;
    private int mYDiff;
    private String path;
    private SurfaceView view;

    /* renamed from: com.lechatelier.indimeter.ProcessImage.1 */
    class C00811 extends Handler {
        C00811() {
        }

        public void handleMessage(Message m) {
            ProcessImage.this.mPoint.clear();
            ProcessImage.this.drawImage();
        }
    }

    /* renamed from: com.lechatelier.indimeter.ProcessImage.3 */
    class C00823 extends Handler {
        private final /* synthetic */ float val$x;
        private final /* synthetic */ float val$y;

        C00823(float f, float f2) {
            this.val$x = f;
            this.val$y = f2;
        }

        public void handleMessage(Message m) {
            if (!ProcessImage.this.mHasDoubleClicked) {
                if (ProcessImage.this.mRealtime) {
                    if (ProcessImage.this.mEnabled) {
                        ProcessImage.this.mOpenCvCameraView.disableView();
                        ProcessImage.this.mEnabled = false;
                        Toast.makeText(ProcessImage.this.getApplicationContext(), "\uc77c\uc2dc\uc815\uc9c0\ub428", 0).show();
                        return;
                    }
                    ProcessImage.this.mOpenCvCameraView.enableView();
                    ProcessImage.this.mEnabled = true;
                } else if (!ProcessImage.this.mAuto) {
                    ProcessImage.this.mPoint.add(new Point((double) ((this.val$x - ((float) ProcessImage.this.mXDiff)) / ProcessImage.this.mScale), (double) ((this.val$y - ((float) ProcessImage.this.mYDiff)) / ProcessImage.this.mScale)));
                    ProcessImage.this.drawImage();
                }
            }
        }
    }

    /* renamed from: com.lechatelier.indimeter.ProcessImage.4 */
    class C00834 implements Runnable {
        private final /* synthetic */ File val$imageFile;

        C00834(File file) {
            this.val$imageFile = file;
        }

        public void run() {
            Toast.makeText(ProcessImage.this.getApplicationContext(), new StringBuilder(String.valueOf(this.val$imageFile.getAbsolutePath())).append("\uc5d0 \uc800\uc7a5\ub428").toString(), 0).show();
        }
    }

    /* renamed from: com.lechatelier.indimeter.ProcessImage.2 */
    class C01152 extends BaseLoaderCallback {
        C01152(Context $anonymous0) {
            super($anonymous0);
        }

        public void onManagerConnected(int status) {
            if (status != 0) {
                super.onManagerConnected(status);
            } else if (ProcessImage.this.mRealtime) {
                ProcessImage.this.mOpenCvCameraView.enableView();
                ProcessImage.this.mEnabled = true;
                ProcessImage.this.mOpenCvCameraView.setOnTouchListener(ProcessImage.this);
            } else {
                ProcessImage.this.drawImage();
            }
        }
    }

    public ProcessImage() {
        this.mEnabled = true;
        this.mAuto = true;
        this.mRealtime = true;
        this.mHasDoubleClicked = false;
        this.mHasClickedDown = false;
        this.mCapture = false;
        this.mPoint = new ArrayList();
        this.mLongClick = new C00811();
        this.mLoaderCallback = new C01152(this);
    }

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().addFlags(Imgproc.INTER_TAB_SIZE2);
        getWindow().addFlags(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        if (VERSION.SDK_INT >= 14) {
            getWindow().getDecorView().setSystemUiVisibility(1);
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mOpenCvCameraView != null) {
            this.mOpenCvCameraView.disableView();
            this.mEnabled = false;
        }
    }

    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            boolean z;
            int code = intent.getIntExtra("CODE", 0);
            if (code == 0) {
                z = true;
            } else {
                z = false;
            }
            this.mRealtime = z;
            String str = "AUTO";
            if (code % 2 == 1) {
                z = true;
            } else {
                z = false;
            }
            this.mAuto = intent.getBooleanExtra(str, z);
            if (this.mRealtime) {
                setContentView(C0084R.layout.realtime);
                this.mOpenCvCameraView = (CameraBridgeViewBase) findViewById(C0084R.id.camera_view);
                this.mOpenCvCameraView.setCvCameraViewListener((CvCameraViewListener2) this);
            } else {
                if (code > 2) {
                    Cursor cursor = managedQuery(intent.getData(), new String[]{"_data"}, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    cursor.moveToFirst();
                    this.path = cursor.getString(column_index);
                } else {
                    this.path = intent.getData().getPath();
                }
                this.view = new SurfaceView(this);
                setContentView(this.view);
                this.view.setOnTouchListener(this);
            }
        }
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, this.mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mOpenCvCameraView != null) {
            this.mOpenCvCameraView.disableView();
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == 1) {
            if (this.mLongClick.hasMessages(1)) {
                this.mLongClick.removeMessages(1);
                this.mHasClickedDown = false;
            }
            if (!this.mHasClickedDown) {
                long pressTime = System.currentTimeMillis();
                if (pressTime - this.mLastPressTime <= DOUBLE_CLICK_INTERVAL) {
                    this.mHasDoubleClicked = true;
                    if (this.mRealtime && this.mEnabled) {
                        this.mCapture = true;
                    } else {
                        capture();
                    }
                } else {
                    this.mHasDoubleClicked = false;
                    new C00823(event.getX(), event.getY()).sendMessageDelayed(new Message(), DOUBLE_CLICK_INTERVAL);
                }
                this.mLastPressTime = pressTime;
            }
            this.mHasClickedDown = false;
        } else if (!(this.mRealtime || this.mAuto || event.getAction() != 0)) {
            Message m = new Message();
            m.what = 1;
            this.mLongClick.sendMessageDelayed(m, LONG_CLICK_INTERVAL);
            this.mHasClickedDown = true;
        }
        return true;
    }

    public void onCameraViewStarted(int width, int height) {
        this.mRgba = new Mat(height, width, CvType.CV_8UC4);
        this.mClone = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        this.mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        this.mRgba = inputFrame.rgba();
        if (this.mAuto) {
            processImage();
        } else {
            processImage((double) (this.mRgba.cols() / 2), (double) (this.mRgba.rows() / 2));
        }
        this.mRgba.copyTo(this.mClone);
        if (this.mCapture) {
            capture();
        }
        return this.mRgba;
    }

    protected void drawImage() {
        this.mRgba = Highgui.imread(this.path);
        Canvas canvas = this.view.getHolder().lockCanvas();
        if (this.mRgba.rows() <= this.mRgba.cols() || canvas.getWidth() <= canvas.getHeight()) {
            Imgproc.cvtColor(this.mRgba, this.mRgba, 4);
            if (this.mAuto) {
                processImage();
            } else {
                for (int i = 0; i < this.mPoint.size(); i++) {
                    processImage((Point) this.mPoint.get(i));
                }
            }
            Bitmap bmp = Bitmap.createBitmap(this.mRgba.cols(), this.mRgba.rows(), Config.ARGB_8888);
            Utils.matToBitmap(this.mRgba, bmp);
            this.mScale = Math.min(((float) canvas.getWidth()) / ((float) bmp.getWidth()), ((float) canvas.getHeight()) / ((float) bmp.getHeight()));
            Matrix matrix = new Matrix();
            matrix.postScale(this.mScale, this.mScale);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            this.mXDiff = (canvas.getWidth() - bmp.getWidth()) / 2;
            this.mYDiff = (canvas.getHeight() - bmp.getHeight()) / 2;
            canvas.drawBitmap(bmp, null, new Rect(this.mXDiff, this.mYDiff, this.mXDiff + bmp.getWidth(), this.mYDiff + bmp.getHeight()), null);
            this.view.getHolder().unlockCanvasAndPost(canvas);
            return;
        }
        setRequestedOrientation(1);
    }

    private void processImage() {
        int i;
        Mat gray = new Mat();
        Mat circles = new Mat();
        Mat mat = this.mRgba;
        if (this.mRealtime) {
            i = 6;
        } else {
            i = 7;
        }
        Imgproc.cvtColor(mat, gray, i);
        Imgproc.GaussianBlur(gray, gray, new Size(9.0d, 9.0d), 2.0d, 2.0d);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Mat mat2 = circles;
        Imgproc.HoughCircles(gray, mat2, 3, 1.0d, getMinMax(gray, true, 8), (double) pref.getInt("param1", 160), (double) pref.getInt("param2", 40), (int) getMinMax(gray, true, 16), (int) getMinMax(gray, true, 2));
        float[] circle = new float[3];
        int i2 = 0;
        while (i2 < circles.cols() && i2 < 20) {
            circles.get(0, i2, circle);
            Point point = new Point((double) circle[0], (double) circle[1]);
            Mat mask = Mat.zeros(this.mRgba.size(), CvType.CV_8UC1);
            Core.circle(mask, point, (int) circle[2], new Scalar(255.0d, 255.0d, 255.0d), -1);
            Scalar mean = Core.mean(this.mRgba, mask);
            Core.circle(this.mRgba, point, (int) circle[2], new Scalar(255.0d, 0.0d, 0.0d, 255.0d), 4);
            point.f4x -= (double) circle[2];
            putpH(point, mean.val);
            i2++;
        }
    }

    private void processImage(Point point) {
        processImage(point.f4x, point.f5y);
    }

    private void processImage(double x, double y) {
        Point point = new Point();
        double[] colors = new double[3];
        double[] color = new double[3];
        int k = 5;
        int divider = PreferenceManager.getDefaultSharedPreferences(this).getInt("param3", 12);
        int i = 0;
        while (i < 5) {
            double minMax;
            if (i == 2 || i == 4) {
                minMax = ((double) (i - 3)) * getMinMax(this.mRgba, false, divider);
            } else {
                minMax = 0.0d;
            }
            double d = x + minMax;
            if (i == 1 || i == 3) {
                minMax = ((double) (i - 2)) * getMinMax(this.mRgba, false, divider);
            } else {
                minMax = 0.0d;
            }
            Point point2 = new Point(d, minMax + y);
            colors = this.mRgba.get((int) (point2.f5y + 0.5d), (int) (point2.f4x + 0.5d));
            if (colors != null) {
                for (int j = 0; j < 3; j++) {
                    color[j] = color[j] + colors[j];
                }
                Mat mat = this.mRgba;
                Core.circle(r20, point2, 3, new Scalar(255.0d, 0.0d, 0.0d, 255.0d), -1);
            } else {
                k--;
            }
            i++;
        }
        for (i = 0; i < 3; i++) {
            color[i] = color[i] / ((double) k);
        }
        putpH(new Point(x - getMinMax(this.mRgba, false, divider), getMinMax(this.mRgba, false, divider * 2) + y), color);
    }

    private void putpH(Point point, double[] color) {
        float[] hsv = new float[3];
        Color.RGBToHSV((int) (color[0] + 0.5d), (int) (color[1] + 0.5d), (int) (color[2] + 0.5d), hsv);
        int hue = Math.round(hsv[0]);
        if (hue <= 230) {
            Core.putText(this.mRgba, " pH=" + String.valueOf(((double) (((((hue * -1) * hue) + (hue * 693)) + 11892) / 100)) / 100.0d), point, 0, 1.0d, ((((int) (color[0] + 0.5d)) * 299) + (((int) (color[1] + 0.5d)) * 587)) + (((int) (color[2] + 0.5d)) * 114) >= 128000 ? new Scalar(0.0d, 0.0d, 0.0d, 255.0d) : new Scalar(255.0d, 255.0d, 255.0d, 255.0d), 4);
        }
    }

    private double getMinMax(Mat mat, boolean max, int divider) {
        if ((!max || mat.cols() <= mat.rows()) && (max || mat.cols() >= mat.rows())) {
            return ((double) mat.rows()) / ((double) divider);
        }
        return ((double) mat.cols()) / ((double) divider);
    }

    @SuppressLint({"SimpleDateFormat"})
    private void capture() {
        FileNotFoundException e;
        IOException e2;
        this.mCapture = false;
        Bitmap bmp = Bitmap.createBitmap(this.mClone.cols(), this.mClone.rows(), Config.ARGB_8888);
        Utils.matToBitmap(this.mClone, bmp);
        File dir = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append("/indimeter").toString());
        dir.mkdirs();
        File imageFile = new File(dir, new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss'.png'").format(new Date()));
        try {
            OutputStream os = new FileOutputStream(imageFile);
            OutputStream outputStream;
            try {
                bmp.compress(CompressFormat.PNG, 0, os);
                os.flush();
                os.close();
                outputStream = os;
            } catch (FileNotFoundException e3) {
                e = e3;
                outputStream = os;
                e.printStackTrace();
                runOnUiThread(new C00834(imageFile));
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imageFile.getAbsolutePath()}, null, null);
            } catch (IOException e4) {
                e2 = e4;
                outputStream = os;
                e2.printStackTrace();
                runOnUiThread(new C00834(imageFile));
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imageFile.getAbsolutePath()}, null, null);
            }
        } catch (FileNotFoundException e5) {
            e = e5;
            e.printStackTrace();
            runOnUiThread(new C00834(imageFile));
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imageFile.getAbsolutePath()}, null, null);
        } catch (IOException e6) {
            e2 = e6;
            e2.printStackTrace();
            runOnUiThread(new C00834(imageFile));
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imageFile.getAbsolutePath()}, null, null);
        }
        runOnUiThread(new C00834(imageFile));
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imageFile.getAbsolutePath()}, null, null);
    }
}
