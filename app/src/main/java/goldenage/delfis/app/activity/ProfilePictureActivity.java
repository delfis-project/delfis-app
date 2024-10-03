package goldenage.delfis.app.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import goldenage.delfis.app.R;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.User;
import goldenage.delfis.app.util.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePictureActivity extends AppCompatActivity {
    private static final String IMAGE_MIME_TYPE = "image/jpeg";
    private static final String IMAGE_PATH = "Pictures/delfis";
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ExecutorService cameraExecutor;
    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
    private User user;

    ImageView btCapturar;

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

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions();
        }

        btCapturar = findViewById(R.id.btCapturar);
        btCapturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }

        // Definir um nome e caminho para a imagem
        String name = "IMG_" + System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
        values.put(MediaStore.Images.Media.MIME_TYPE, IMAGE_MIME_TYPE);
        values.put(MediaStore.Images.Media.RELATIVE_PATH, IMAGE_PATH);

        // Configurar as opções de saída da imagem
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
        ).build();

        // Listener para rotação da imagem
        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation;
                if (orientation >= 45 && orientation < 135) {
                    rotation = Surface.ROTATION_270;
                } else if (orientation >= 135 && orientation < 225) {
                    rotation = Surface.ROTATION_180;
                } else if (orientation >= 225 && orientation < 315) {
                    rotation = Surface.ROTATION_90;
                } else {
                    rotation = Surface.ROTATION_0;
                }
                imageCapture.setTargetRotation(rotation);
            }
        };
        orientationEventListener.enable();

        // Salvar a imagem e fazer upload
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = outputFileResults.getSavedUri();
                if (savedUri != null) {
                    uploadToFirebase(savedUri);
                } else {
                    Toast.makeText(ProfilePictureActivity.this, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("Log", "Erro ao capturar imagem: " + exception.getMessage());
            }
        });
    }

    private void uploadToFirebase(Uri fileUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference photoRef = storageRef.child("profile_pictures/" + user.getId() + ".jpg");

        UploadTask uploadTask = photoRef.putFile(fileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();

                Toast.makeText(ProfilePictureActivity.this, "Upload bem-sucedido!", Toast.LENGTH_SHORT).show();
                Log.d("Firebase", "Download URL: " + downloadUrl);

                savePhotoUrlToDatabase(downloadUrl);
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfilePictureActivity.this, "Erro ao recuperar URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(ProfilePictureActivity.this, "Falha no upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
        boolean permissionGranted = true;
        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            if (Arrays.asList(REQUIRED_PERMISSIONS).contains(entry.getKey()) && !entry.getValue()) {
                permissionGranted = false;
                break;
            }
        }
        if (!permissionGranted) {
            Toast.makeText(getApplicationContext(), "Permissão NEGADA.", Toast.LENGTH_SHORT).show();
        } else {
            startCamera();
        }
    });
}