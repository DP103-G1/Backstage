package com.example.manager.menu;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.main.Common;
import com.example.main.R;
import com.example.main.Url;
import com.example.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;


import static android.app.Activity.RESULT_OK;


public class MenuInsertFragment extends Fragment {
    private static final String TAG = "TAG_InsertFragment";
    private FragmentActivity activity;
    private ImageView ivMenu;
    private EditText etId, etName, etPrice, etContent;
    private Switch swStatus;
    private Button btIn;
    private byte[] image;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_menu_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        ivMenu = view.findViewById(R.id.ivMenu);
        etId = view.findViewById(R.id.etId);
        etName = view.findViewById(R.id.etName);
        etPrice = view.findViewById(R.id.etPrice);
        etContent = view.findViewById(R.id.etContent);
        swStatus = view.findViewById(R.id.swStatus);
        btIn = view.findViewById(R.id.btIn);

        swStatus.setOnClickListener(v -> {
            boolean isChecked = swStatus.isChecked();
            if (isChecked) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("確定!!")
                        .setMessage("確定將此產品上架？")
                        .setNegativeButton("取消", (dialog, which) -> swStatus.setChecked(false))
                        .setNegativeButton("確定", (dialog, which) -> swStatus.setChecked(true)).setOnCancelListener(dialog ->
                        swStatus.setChecked(false)).show();
            } else {
                new AlertDialog.Builder(getActivity())
                        .setTitle("確定!!")
                        .setMessage("確定將此產品下架？")
                        .setNegativeButton("取消", (dialog, which) -> swStatus.setChecked(true))
                        .setNegativeButton("確定", (dialog, which) -> swStatus.setChecked(false)).setOnCancelListener(dialog ->
                        swStatus.setChecked(true)).show();
            }
        });

        btIn.setOnClickListener(v -> {
            etId.setText("A13");
            etName.setText("威靈頓牛排");
            etPrice.setText("600");
            etContent.setText("上好的菲力牛排，大火煎上色。");
        });


        Button btTakePicture = view.findViewById(R.id.btTakePicture);
        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                contentUri = FileProvider.getUriForFile(
                        activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                } else {
                    Common.showToast(getActivity(), R.string.textNoCameraApp);
                }
            }
        });

        Button btPickPicture = view.findViewById(R.id.btPickPicture);
        btPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_PICTURE);
            }
        });

        Button btFinishInsert = view.findViewById(R.id.btFinishInsert);
        btFinishInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etId.getText().toString().trim();
                if (id.length() == 0) {
                    etId.setError(getString(R.string.textidIsInvalid));
                    return;
                }
                String name = etName.getText().toString().trim();
                if (name.length() == 0) {
                    etName.setError(getString(R.string.textnameIsInvalid));
                    return;
                }
                String price = etPrice.getText().toString().trim();
                if (price.length() == 0) {
                    etPrice.setError(getString(R.string.textpriceIsInvalid));
                    return;
                }

                int pricest = Integer.parseInt(etPrice.getText().toString().trim());
                int status = swStatus.isChecked() ? 1 : 0;
                String content = etContent.getText().toString().trim();

                if (Common.networkConnected(activity)) {
                    String url = Url.URL_SERVER + "MenuServlet";
                    Menu menu = new Menu(id, name, pricest, status, content);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "add");
                    jsonObject.addProperty("menu", new Gson().toJson(menu));
                    if (image != null) {
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
                    }
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(getActivity(), R.string.textInsertFail);
                    } else {
                        Common.showToast(getActivity(), R.string.textInsertSuccess);
                    }
                } else {
                    Common.showToast(getActivity(), R.string.textNoNetwork);
                }
                navController.popBackStack();
            }
        });

        Button btCancel = view.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                    break;
                case REQ_CROP_PICTURE:
                    Uri uri = intent.getData();
                    Bitmap bitmap = null;
                    if (uri != null) {
                        try {
                            bitmap = BitmapFactory.decodeStream(
                                    activity.getContentResolver().openInputStream(uri));
                            ivMenu.setImageBitmap(bitmap);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            image = out.toByteArray();
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    if (bitmap != null) {
                        ivMenu.setImageBitmap(bitmap);
                    } else {
                        ivMenu.setImageResource(R.drawable.no_image);
                    }
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(sourceImageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", 0);
        intent.putExtra("outputY", 0);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, REQ_CROP_PICTURE);
        } else {
            Toast.makeText(activity, R.string.textNoImageCropAppFound,
                    Toast.LENGTH_SHORT).show();
        }
    }
}

