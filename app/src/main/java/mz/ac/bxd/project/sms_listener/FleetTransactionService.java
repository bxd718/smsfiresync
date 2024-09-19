package mz.ac.bxd.project.sms_listener;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.UUID;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class FleetTransactionService {

    private static final String API_BASE_URL = "https://fleet-api.taxi.yandex.net/";
    private final OkHttpClient client;
    private static final String parkId = "5870be4839814bbfb0405c184aec6d77";
    private static final String apiKey = "hYeYZUlJCytNyereQCnGcMbuvAXGBgEwYebFlTqn";
    private static final String FLEET_CLIENT_ID = "taxi/park/5870be4839814bbfb0405c184aec6d77";
    private static final String categoryId = "partner_service_manual_1";
    private FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    public FleetTransactionService() {
        this.client = new OkHttpClient();
    }

    public boolean createFleetTransaction(String driverPhoneNumber, double transactionAmount, String messageId) throws IOException, JSONException {
        String driverId = getDriverIdByPhoneNumber(driverPhoneNumber);

        if (driverId == null) {
            return false;
        }

        // Proceed to create a transaction for the driver
        String requestBody = new JSONObject()
                .put("amount", String.valueOf(transactionAmount))
                .put("category_id", categoryId)
                .put("description", messageId)
                .put("driver_profile_id", driverId)
                .put("park_id", parkId)
                .toString();

        Request request = new Request.Builder()
                .url(API_BASE_URL + "v2/parks/driver-profiles/transactions")
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .addHeader("X-API-Key", apiKey)
                .addHeader("X-Client-ID", FLEET_CLIENT_ID)
                .addHeader("accept-language", "en-US")
                .addHeader("X-Idempotency-token", messageId + UUID.randomUUID().toString())
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Transaction failed: " + response.code() + " " + response.message());
                crashlytics.log("Transaction failed: " + response.code() + " " + response.message());
                return false;
            }

            if (response.body() != null) {
                String responseBody = response.body().string();
                System.out.println("Transaction Response: " + responseBody);
                crashlytics.log("Transaction Response: " + responseBody);
            } else {
                System.err.println("Transaction failed: Response body is null");
                crashlytics.log("Transaction failed: Response body is null");
                return false;
            }
            return true;
        }
    }

    private String getDriverIdByPhoneNumber(String driverPhoneNumber) throws IOException, JSONException {
        String requestBody = new JSONObject()
                .put("query", new JSONObject().put("park", new JSONObject().put("id", parkId))
                        .put("text", driverPhoneNumber))
                .toString();

        Request request = new Request.Builder()
                .url(API_BASE_URL + "v1/parks/driver-profiles/list")
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .addHeader("X-API-Key", apiKey)
                .addHeader("X-Client-ID", FLEET_CLIENT_ID)
                .addHeader("accept-language", "en-US")
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Failed to fetch driver ID: " + response.code() + " " + response.message());
                crashlytics.log("Failed to fetch driver ID: " + response.code() + " " + response.message());
                return null;  // Return null if the response is not successful
            }

            if (response.body() == null) {
                System.err.println("Empty response body");
                crashlytics.log("Empty response body");
                return null;  // Return null if the response body is empty
            }

            String responseBody = response.body().string();
            System.out.println("Driver Lookup Response: " + responseBody);
            crashlytics.log("Driver Lookup Response: " + responseBody);

            JSONObject jsonResponse = new JSONObject(responseBody);

            if (!jsonResponse.has("driver_profiles") || jsonResponse.getJSONArray("driver_profiles").length() == 0) {
                System.err.println("No driver profiles found for phone number: " + driverPhoneNumber);
                crashlytics.log("No driver profiles found for phone number: " + driverPhoneNumber);
                return null;  // Return null if no driver profiles are found
            }

            JSONObject driverProfile = jsonResponse.getJSONArray("driver_profiles")
                    .getJSONObject(0)
                    .getJSONObject("driver_profile");

            if (!driverProfile.has("id")) {
                System.err.println("Driver profile does not have an ID");
                crashlytics.log("Driver profile does not have an ID");
                return null;  // Return null if the driver profile does not have an ID
            }

            return driverProfile.getString("id");
        }
    }

}
