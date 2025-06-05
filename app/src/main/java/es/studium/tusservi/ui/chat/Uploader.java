package es.studium.tusservi.ui.chat;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Uploader {

    public interface UploadCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }

    public void subirArchivo(Context context, Uri fileUri, String tipo, int idEmisor, int idReceptor, UploadCallback callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                callback.onFailure("No se pudo abrir el archivo");
                return;
            }

            // Obtener mime type
            String mimeType = context.getContentResolver().getType(fileUri);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            // Obtener nombre archivo (opcional)
            String fileName = getFileName(context, fileUri);
            if (fileName == null) fileName = "archivo";

            // Crear RequestBody a partir del InputStream
            byte[] fileBytes = readBytesFromInputStream(inputStream);
            inputStream.close();

            RequestBody fileBody = RequestBody.create(fileBytes, MediaType.parse(mimeType));

            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("archivo", fileName, fileBody)
                    .addFormDataPart("tipo", tipo)
                    .addFormDataPart("idEmisor", String.valueOf(idEmisor))
                    .addFormDataPart("idReceptor", String.valueOf(idReceptor))
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://10.0.2.2/TUSSERVI/subirArchivo.php")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onSuccess(response.body().string());
                    } else {
                        callback.onFailure("Error en servidor: " + response.code());
                    }
                }
            });

        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    private byte[] readBytesFromInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    private String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}


