package vaa.mdt.aptcoffee.analysis;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.Set;

import vaa.mdt.aptcoffee.R;
import vaa.mdt.aptcoffee.RevenueActivity;
import vaa.mdt.aptcoffee.dao.AssociationRulesAdapterDAO;
import vaa.mdt.aptcoffee.dao.HoaDonChiTietDAO;
import vaa.mdt.aptcoffee.model.AprioriAlgorithm;

public class mba extends AppCompatActivity {
    private HoaDonChiTietDAO hoaDonChiTietDAO;
    private RecyclerView recyclerViewRules;
    private AssociationRulesAdapterDAO adapter;
    Button bieudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mba);

        // Khởi tạo RecyclerView
        recyclerViewRules = findViewById(R.id.recyclerViewRules);
        bieudo = findViewById(R.id.bieudo);

        recyclerViewRules.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo DAO để lấy dữ liệu giao dịch
        hoaDonChiTietDAO = new HoaDonChiTietDAO(this);

        // Gọi Apriori để phân tích giao dịch với minSupport = 0.1 (10%) và minConfidence = 0.1 (10%)
        new AnalyzeTransactionsTask().execute(0.1, 0.1);
        setupButton();
    }

    /**
     * AsyncTask để xử lý bất đồng bộ việc phân tích giao dịch.
     */
    private class AnalyzeTransactionsTask extends AsyncTask<Double, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Double... params) {
            double minSupport = params[0];
            double minConfidence = params[1];

            // Lấy dữ liệu giao dịch kèm tên hàng hóa
            Map<String, List<String>> transactions = hoaDonChiTietDAO.getTransactionDataWithProductNames();

            // Kiểm tra dữ liệu giao dịch
            if (transactions == null || transactions.isEmpty()) {
                Log.e("MBAActivity", "Không có dữ liệu giao dịch.");
                return null;
            }

            Log.d("MBAActivity", "Số lượng giao dịch: " + transactions.size());

            // Gọi thuật toán Apriori để tìm tập phổ biến
            List<Set<String>> frequentItemsets = AprioriAlgorithm.generateFrequentItemsets(transactions, minSupport);

            // Kiểm tra tập phổ biến
            if (frequentItemsets == null || frequentItemsets.isEmpty()) {
                Log.e("MBAActivity", "Không tìm thấy tập phổ biến.");
                return null;
            }

            Log.d("MBAActivity", "Số lượng tập phổ biến: " + frequentItemsets.size());

            // Sinh luật kết hợp từ tập phổ biến
            List<String> associationRules = AprioriAlgorithm.generateAssociationRules(frequentItemsets, transactions, minConfidence);

            // Kiểm tra luật kết hợp
            if (associationRules == null || associationRules.isEmpty()) {
                Log.e("MBAActivity", "Không tìm thấy luật kết hợp.");
                return null;
            }

            Log.d("MBAActivity", "Số lượng luật kết hợp: " + associationRules.size());
            return associationRules;
        }

        @Override
        protected void onPostExecute(List<String> associationRules) {
            if (associationRules == null || associationRules.isEmpty()) {
                Log.e("MBAActivity", "Không có dữ liệu để hiển thị.");
                return;
            }

            // Hiển thị kết quả lên RecyclerView
            adapter = new AssociationRulesAdapterDAO(associationRules);
            recyclerViewRules.setAdapter(adapter);
        }
    }
    private void setupButton() {
        if (bieudo != null) {
            bieudo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mba.this, RevenueActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("MBA_Activity", "Button bieudo not found in layout");
        }
    }
}

