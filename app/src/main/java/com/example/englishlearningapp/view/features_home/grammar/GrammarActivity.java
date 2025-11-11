package com.example.englishlearningapp.view.features_home.grammar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GrammarActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ExpandableListView expandableListView;
    private List<TopicGrammar> topics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grammar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        loadData();
        setupExpandableListView();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        expandableListView = findViewById(R.id.expandable_list);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadData() {
        String json = loadJSONFromAsset();
        if (json != null) {
            Type listType = new TypeToken<ArrayList<TopicGrammar>>() {
            }.getType();
            topics = new Gson().fromJson(json, listType);
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("grammar_ai.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private void setupExpandableListView() {
        if (topics == null) return;

        List<String> groupList = new ArrayList<>();
        HashMap<String, List<String>> childMap = new HashMap<>();

        for (TopicGrammar topic : topics) {
            groupList.add(topic.title);
            List<String> children = new ArrayList<>();
            for (SubItemGrammar subItem : topic.subItems) {
                children.add(subItem.title);
            }
            childMap.put(topic.title, children);
        }

        CustomExpandableListGrammarAdapter adapter = new CustomExpandableListGrammarAdapter(this, groupList, childMap);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String groupTitle = groupList.get(groupPosition);
            TopicGrammar selectedTopic = null;
            for (TopicGrammar topic : topics) {
                if (topic.title.equals(groupTitle)) {
                    selectedTopic = topic;
                    break;
                }
            }
            if (selectedTopic != null) {
                SubItemGrammar subItem = selectedTopic.subItems.get(childPosition);
                Intent intent = new Intent(GrammarActivity.this, DetailGrammarActivity.class);
                intent.putExtra("title", subItem.title);
                intent.putExtra("content", subItem.content);
                startActivity(intent);
            }
            return true;
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}