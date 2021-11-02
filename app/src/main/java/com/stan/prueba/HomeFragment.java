package com.stan.prueba;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.stan.prueba.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    RecyclerView ListRecycler;
    ArrayList<ListRecycler> listDatos;
    ListRecyclerAdapter adapter;
    String Id;
    String Name;
    String Age;
    String Cell;
    String Url;
    String uri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Firebase Realtimer
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRootChild = mDatabaseReference.child("Cliente");

        ListRecycler = view.findViewById(R.id.recyclerView);
        ListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        GetRefarrls();
        adapter = new ListRecyclerAdapter(listDatos);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Selectid = listDatos.get(ListRecycler.getChildAdapterPosition(view)).getId();
                String SelectName = listDatos.get(ListRecycler.getChildAdapterPosition(view)).getName();
                String SelectAge = listDatos.get(ListRecycler.getChildAdapterPosition(view)).getAge();
                String SelectCell = listDatos.get(ListRecycler.getChildAdapterPosition(view)).getCell();
                String SelectUrl = listDatos.get(ListRecycler.getChildAdapterPosition(view)).getUrl();
                Intent i = new Intent(getContext(), EditActivity.class);
                Bundle parmetros = new Bundle();
                parmetros.putString("id", Selectid);
                parmetros.putString("idN", SelectName);
                parmetros.putString("idA", SelectAge);
                parmetros.putString("idC", SelectCell);
                parmetros.putString("idU", SelectUrl);
                i.putExtras(parmetros);
                startActivity(i);
            }
        });

        ListRecycler.setAdapter(adapter);

        mRootChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listDatos.clear();
                for(int i= 1; i <= 3; i++) {
                    Id = String.valueOf(i);
                    Name = dataSnapshot.child(String.valueOf(i)).child("Name").getValue().toString();
                    Age = dataSnapshot.child(String.valueOf(i)).child("Age").getValue().toString();
                    Cell = dataSnapshot.child(String.valueOf(i)).child("Cell").getValue().toString();
                    Url = dataSnapshot.child(String.valueOf(i)).child("Url").getValue().toString();
                    listDatos.add(new ListRecycler(Id,Name,Age,Cell,Url));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }

    private ArrayList<ListRecycler> GetRefarrls(){
        listDatos = new ArrayList<>();
        for(int i= 0; i <= 3; i++) {
            listDatos.add(new ListRecycler(Id, Name, Age,Cell,Url));
        }
        return listDatos;
    }


    public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolderDatos> implements View.OnClickListener{

        ArrayList<ListRecycler> listrecycler;

        private View.OnClickListener listener;

        public ListRecyclerAdapter(ArrayList<ListRecycler> listrecycler) {
            this.listrecycler = listrecycler;
        }

        @NonNull
        @Override
        public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler,null,false);
            view.setOnClickListener(this);
            return new ViewHolderDatos(view);
        }

        @Override
        public void onBindViewHolder(ViewHolderDatos viewHolderDatos, int i) {
            viewHolderDatos.Lname.setText(listrecycler.get(i).getName());
            viewHolderDatos.Lage.setText(listrecycler.get(i).getAge());
            viewHolderDatos.Lcell.setText(listrecycler.get(i).getCell());
            Picasso.get().load(listrecycler.get(i).getUrl()).centerCrop().resize(70,70).into(viewHolderDatos.Limg);
        }

        @Override
        public int getItemCount() {
            return listrecycler.size();
        }

        public void setOnClickListener(View.OnClickListener listener){
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            if(listener!=null){
                listener.onClick(view);
            }
        }

        public class ViewHolderDatos extends RecyclerView.ViewHolder {

            TextView Lname;
            TextView Lage;
            TextView Lcell;
            ImageView Limg;

            public ViewHolderDatos(@NonNull View itemView) {
                super(itemView);
                Lname = itemView.findViewById(R.id.list_name);
                Lage = itemView.findViewById(R.id.list_age);
                Lcell = itemView.findViewById(R.id.list_cell);
                Limg = itemView.findViewById(R.id.list_img);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}