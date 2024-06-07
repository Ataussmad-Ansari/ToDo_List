package com.example.todolist;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist.database.DatabaseHelper;
import com.example.todolist.databinding.ActivityAllTaskBinding;

import java.util.ArrayList;

public class AllTaskActivity extends AppCompatActivity {
    ArrayList<TaskModel> tasks = new ArrayList<>();
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    ActivityAllTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAllTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        allTask();

        binding.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private void allTask() {
        binding.allTaskRV.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        AllTaskAdapter adapter = new AllTaskAdapter(this, tasks);
        binding.allTaskRV.setAdapter(adapter);
        tasks.clear();
        Cursor cursor = databaseHelper.getInfo();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String taskName = cursor.getString(1);
                String taskTime = cursor.getString(2);
                String taskDate = cursor.getString(3);
                TaskModel task = new TaskModel(id, taskName, taskTime, taskDate);
                tasks.add(task);
            }
        } else {
            Toast.makeText(this, "No tasks found", Toast.LENGTH_SHORT).show();
        }
    }
}
