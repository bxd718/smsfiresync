    package mz.ac.bxd.project.sms_listener;

    import android.Manifest;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.content.pm.PackageManager;
    import android.net.ConnectivityManager;
    import android.os.Bundle;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;

    import com.google.firebase.database.FirebaseDatabase;

    import java.io.IOException;

    public class MainActivity extends AppCompatActivity {

        public static final String EXTRA_SMS_SENDER = "extra_sms_sender";
        public static final String EXTRA_SMS_MESSAGE = "extra_sms_message";
        private static final int SMS_REQUEST_CODE = 101;
        private NetworkChangeReceiver networkChangeReceiver;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // Check SMS permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS}, SMS_REQUEST_CODE);
            }

            // Create instances of MessageUtils and Utils
            MessageUtils messageUtils = new MessageUtils(getApplicationContext());
            Utils utils = new Utils(FirebaseDatabase.getInstance());

            // Register the NetworkChangeReceiver programmatically
            networkChangeReceiver = new NetworkChangeReceiver();
            // Inject dependencies
            networkChangeReceiver.setDependencies(messageUtils, utils);
            // Create the intent filter to monitor connectivity changes
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            // Register the BroadcastReceiver
            registerReceiver(networkChangeReceiver, filter);

            // Send pending messages if the internet is connected when the app starts
            if (NetworkUtils.isConnectedToInternet(getApplicationContext())) {
                try {
                    messageUtils.sendPendingMessages(utils);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // Iniciar o serviço de comunicação por socket
            Intent socketServiceIntent = new Intent(this, SocketService.class);
            startService(socketServiceIntent);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // Unregister the receiver to avoid memory leaks
            unregisterReceiver(networkChangeReceiver);

            // Parar o serviço de comunicação por socket quando a atividade for destruída
            Intent socketServiceIntent = new Intent(this, SocketService.class);
            stopService(socketServiceIntent);
        }

        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);

            if (!intent.hasExtra(EXTRA_SMS_SENDER) && !intent.hasExtra(EXTRA_SMS_MESSAGE)) {
                return;
            }
        }
    }


