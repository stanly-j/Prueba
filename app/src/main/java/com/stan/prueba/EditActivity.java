package com.stan.prueba;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    TextView TextViewName;
    ImageView Image;
    EditText Nname;
    EditText Nage;
    EditText Ncell;
    String id;
    String idName;
    String idAge;
    String idCell;
    String idUrl;
    Uri uri;
    private ProgressDialog mProgressDialog;
    private static final int GALERY_INTENT = 1 ;
    private  static final int PERMISO_ALAMCENAMIENTO = 1000;
    private static final int RESOURCE_ID =0;

    //Firebase Realtimer
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRootChild = mDatabaseReference.child("Cliente");
    //Firebase Store
    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        TextViewName = findViewById(R.id.TextName);
        Image = findViewById(R.id.IdImg);
        Nname = findViewById(R.id.editname);
        Nage = findViewById(R.id.editage);
        Ncell = findViewById(R.id.editcell);

        mProgressDialog = new ProgressDialog(this);

        //Datos recibido de Fagment HomeFrament
        Bundle parametros = this.getIntent().getExtras();
        id = parametros.getString("id");
        idName = parametros.getString("idN");
        idAge = parametros.getString("idA");
        idCell = parametros.getString("idC");
        idUrl = parametros.getString("idU");
        TextViewName.setText(idName);

        //Metodo de picasso mostrar imagen
        PicassoCargarImagen();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISO_ALAMCENAMIENTO:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    IniciarDescarga();
                }else{
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void IniciarDescarga() {
        String url = idUrl;
        if(!TextUtils.isEmpty(url)){
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle("Descarga");
            request.setDescription("Descargando archivo ...");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, ""+System.currentTimeMillis());
            DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
            Descargafinalizada();
        }else{
            Toast.makeText(this, "Error de url", Toast.LENGTH_LONG).show();
        }
    }


    private void PicassoCargarImagen(){
        Picasso.get().load(idUrl).resize(250,300).centerCrop().into(Image);
    }


    public void btnCambiar(View view){
        String NName = Nname.getText().toString();
        String NAge = Nage.getText().toString();
        String NCell = Ncell.getText().toString();

        Map<String,Object> Campos = new HashMap<>();
        Campos.put("Name",NName);
        Campos.put("Age",NAge);
        Campos.put("Cell",NCell);

        if(NName.isEmpty()){
            Toast.makeText(this, "El campo Nuevo Nombre esta vacio ", Toast.LENGTH_SHORT).show();
            Nname.requestFocus();
            return;
        }else{
            if(NAge.isEmpty()){
                Toast.makeText(this, "El campo Nueva Edad esta vacio ", Toast.LENGTH_SHORT).show();
                Nage.requestFocus();
                return;
            }
            else{
                if(NCell.isEmpty()){
                    Toast.makeText(this, "El campo Nuevo N. Celular esta vacio ", Toast.LENGTH_SHORT).show();
                    Ncell.requestFocus();
                    return;
                }
                else {
                    mRootChild.child(id).updateChildren(Campos);
                    TextViewName.setText(NName);
                    Nname.requestFocus();
                    Nname.setText("");
                    Nage.setText("");
                    Ncell.setText("");
                    Toast.makeText(this, "Editado exitosamente ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void btnSubir(View view){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,GALERY_INTENT);
    }


    public void btnDescargar(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] permisos = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permisos, PERMISO_ALAMCENAMIENTO);
            }else{
                IniciarDescarga();
            }
        }
    }

    private void Descargafinalizada(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Descarga Exitosa");
        builder.setMessage("Descarga ha finalizado")
                .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALERY_INTENT && resultCode == RESULT_OK){

            mProgressDialog.setTitle("Subiendo Imagen ...");
            mProgressDialog.setMessage("Subiendo Foto a firebase");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            uri = data.getData();
            StorageReference filepath = mStorageReference.child(id).child("p1");

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.dismiss();
                    Picasso.get().load(uri).resize(250,300).centerCrop().into(Image);
                    Toast.makeText(getApplicationContext(), "Foto subida exitosamente ", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }


}