    package mz.ac.bxd.project.sms_listener;

    import android.Manifest;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.os.Bundle;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;

    public class MainActivity extends AppCompatActivity{

        public static final String EXTRA_SMS_SENDER = "extra_sms_sender";
        public static final String EXTRA_SMS_MESSAGE = "extra_sms_message";

        private static final int SMS_REQUEST_CODE = 101;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // Check SMS permission and start BackgroundService
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.RECEIVE_SMS}, SMS_REQUEST_CODE);
            }

        }

        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);

            if(!intent.hasExtra(EXTRA_SMS_SENDER) && !intent.hasExtra(EXTRA_SMS_MESSAGE)){
                return;
            }

        }
    }
