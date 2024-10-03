package goldenage.delfis.app.activity;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import goldenage.delfis.app.R;
import goldenage.delfis.app.activity.navbar.ConfigActivity;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.User;
import goldenage.delfis.app.util.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePictureActivity extends AppCompatActivity {
    private final ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean permissionGranted = true;
                for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                    if (Arrays.asList(REQUIRED_PERMISSIONS).contains(entry.getKey()) && !entry.getValue()) {
                        permissionGranted = false;
                        break;
                    }
                }
                if (permissionGranted) {
                    startCamera();
                } else {
                    Toast.makeText(getApplicationContext(), "Permissões negadas. Não é possível usar a câmera sem concedê-las.", Toast.LENGTH_SHORT).show();
                }
            }
    );
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ExecutorService cameraExecutor;
    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private final CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
    private User user;
    ImageView btCapturar, imgCarregando;
    TextView textCarregando, textCapturar;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        user = (User) getIntent().getSerializableExtra("user");
        cameraExecutor = Executors.newSingleThreadExecutor();
        viewFinder = findViewById(R.id.viewFinder);
        imgCarregando = findViewById(R.id.imgCarregando);
        textCarregando = findViewById(R.id.textCarregando);
        textCapturar = findViewById(R.id.textCapturar);

        // Verifica se todas as permissões foram concedidas
        if (allPermissionsGranted()) {
            startCamera(); // Se sim, inicia a câmera
        } else {
            requestPermissions(); // Se não, solicita as permissões
        }

        btCapturar = findViewById(R.id.btCapturar);
        btCapturar.setOnClickListener(v -> takePhoto());
    }

    private void takePhoto() {
        if (imageCapture != null) {
            imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy image) {
                    viewFinder.setVisibility(View.INVISIBLE);
                    btCapturar.setVisibility(View.INVISIBLE);
                    textCapturar.setVisibility(View.INVISIBLE);
                    imgCarregando.setVisibility(View.VISIBLE);
                    textCarregando.setVisibility(View.VISIBLE);
                    super.onCaptureSuccess(image);
                    Bitmap bitmap = imageProxyToBitmap(image);
                    bitmap = rotateImage(bitmap, 270);
                    uploadImageToFirebase(bitmap);
                    image.close();
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    super.onError(exception);
                    Log.e("CameraX", "Photo capture failed", exception);
                }
            });
        }
    }

    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void uploadImageToFirebase(Bitmap imageBitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        @SuppressLint("DefaultLocale") StorageReference imageRef = storageRef
                .child(String.format("profile_pictures/%d.jpg", this.user.getId()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        imageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.d("Firebase", "Download URL: " + downloadUrl);
                savePhotoUrlToDatabase(downloadUrl);
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Erro ao recuperar URL: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Falha no upload: " + e.getMessage());
        });
    }

    private void savePhotoUrlToDatabase(String downloadUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("pictureUrl", downloadUrl);

        DelfisApiService delfisApiService = RetrofitClient.getClient().create(DelfisApiService.class);
        Call<User> call = delfisApiService.updateUserPartially(user.getToken(), user.getId(), map);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    Intent intent = new Intent(ProfilePictureActivity.this, ConfigActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfilePictureActivity.this, "Erro ao salvar URL: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                try {
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                } catch (Exception exc) {
                    Log.e("Log", "Camera binding failed", exc);
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS);
    }
}
