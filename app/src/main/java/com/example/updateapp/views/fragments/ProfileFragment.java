package com.example.updateapp.views.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.updateapp.R;
import com.example.updateapp.databinding.FragmentProfileBinding;
import com.example.updateapp.models.UserModel;
import com.example.updateapp.views.activites.LoginActivity;
import com.example.updateapp.views.activites.NewUpdateActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    Uri profileUri;
    ProgressDialog progressDialog;

    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public ProfileFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        profileUri = uri;
                        binding.profileImage.setImageURI(profileUri);
                        updateProfile(profileUri);
                    }
                }
        );

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Profile Uploading");
        progressDialog.setMessage("We Are Uploading Your Profile");

        binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.termsfeed.com/live/7fe235d0-aa19-4642-9cb1-33a35370530e")));
            }
        });

        binding.term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://allknowledge34.github.io/Terms-Conditions/")));
            }
        });

        binding.newTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), NewUpdateActivity.class);
                startActivity(intent);
            }
        });

        binding.fetchImage.setOnClickListener(v -> checkPermissionAndOpenGallery());

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shareLink = "https://www.pinkesh.site/projects/FinTrack";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this amazing app: " + shareLink);
                startActivity(Intent.createChooser(intent, "Share via"));
            }
        });

        binding.relLogout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        auth.signOut();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });


        loadUserData();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }

    private void checkPermissionAndOpenGallery() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void openGallery() {
        pickImageLauncher.launch("image/*");
    }

    private void loadUserData() {

        firestore.collection("users").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                UserModel model = documentSnapshot.toObject(UserModel.class);

                if (documentSnapshot.exists()){
                    binding.usersName.setText(model.getName());
                    binding.usersEmail.setText(model.getEmail());

                    Picasso.get()
                            .load(model.getProfile())
                            .placeholder(R.drawable.img)
                            .into(binding.profileImage);


                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==2){

            if (data!=null){

                profileUri = data.getData();
                binding.profileImage.setImageURI(profileUri);

                updateProfile(profileUri);
            }
        }
    }

    private void updateProfile(Uri profileUri) {

        progressDialog.show();

        final StorageReference reference = storage.getReference().child("profile").child(FirebaseAuth.getInstance().getUid());

        reference.putFile(profileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                firestore.collection("users").document(FirebaseAuth.getInstance().getUid())
                                        .update("profile",uri.toString());

                                Toast.makeText(getContext(), "Profile update",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
    }

}