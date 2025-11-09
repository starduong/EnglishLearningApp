package com.example.englishlearningapp.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.model.User;
import com.example.englishlearningapp.view.activity.LoginActivity;
import com.example.englishlearningapp.view.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView btnLogout, tvFullName, tvEmail, tvId;
    private User currentUser;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Lấy thông tin user từ LoginActivity
        if (getActivity() != null && getActivity().getIntent() != null) {
            Bundle bundle = getActivity().getIntent().getExtras();
            if (bundle != null) {
                currentUser = (User) bundle.getSerializable("user");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize views
        initViews(view);

        // Set user information
        setUserInformation();

        // Setup click listeners
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvId = view.findViewById(R.id.tvId);
    }

    private void setUserInformation() {
        if (currentUser != null) {
            // Set thông tin user lên các TextView
            tvFullName.setText(currentUser.getFullname());
            tvEmail.setText(currentUser.getEmail());
            tvId.setText("ID: " + String.valueOf(currentUser.getId()));
        } else {
            // Nếu không có user, hiển thị thông báo hoặc ẩn các view
            tvFullName.setText("N/A");
            tvEmail.setText("N/A");
            tvId.setText("ID: N/A");
        }
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });
    }


    private void performLogout() {
        // ✅ 1. Xóa dữ liệu lưu đăng nhập
        getActivity();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // ✅ 2. Quay lại màn hình login & đóng Activity hiện tại
        Intent intent = new Intent(getActivity(), LoginActivity.class);

        // Clear activity stack để người dùng không thể quay lại bằng nút back
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        // Đóng activity hiện tại nếu đang trong MainActivity
        if (getActivity() instanceof MainActivity) {
            getActivity().finish();
        }
    }
}