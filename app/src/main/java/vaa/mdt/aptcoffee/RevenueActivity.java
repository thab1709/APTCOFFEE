package vaa.mdt.aptcoffee;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vaa.mdt.aptcoffee.R;
import vaa.mdt.aptcoffee.dao.HoaDonChiTietDAO;
import vaa.mdt.aptcoffee.model.RevenuePredictor;
import vaa.mdt.aptcoffee.utils.XDate;

public class RevenueActivity extends AppCompatActivity implements View.OnClickListener {

    private LineChart lineChart;
    private ProgressBar progressBar;
    private TextView tvDailyRevenue, tvMonthlyRevenue, tvYearlyRevenue;
    private Button btnDaily, btnWeekly, btnMonthly, btnCustomRange, btnExport;
    private HoaDonChiTietDAO hoaDonChiTietDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_revenue_predictor);

        hoaDonChiTietDAO = new HoaDonChiTietDAO(this);
        initViews();
        setupToolbar();
        configureChart();
        loadInitialData();
    }

    private void initViews() {
        lineChart = findViewById(R.id.lineChart);
        progressBar = findViewById(R.id.progressBar);
        tvDailyRevenue = findViewById(R.id.tvDailyRevenue);
        tvMonthlyRevenue = findViewById(R.id.tvMonthlyRevenue);
        tvYearlyRevenue = findViewById(R.id.tvYearlyRevenue);

        btnDaily = findViewById(R.id.btnDaily);
        btnWeekly = findViewById(R.id.btnWeekly);
        btnMonthly = findViewById(R.id.btnMonthly);

        btnExport = findViewById(R.id.btnExport);

        btnDaily.setOnClickListener(this);
        btnWeekly.setOnClickListener(this);
        btnMonthly.setOnClickListener(this);

        btnExport.setOnClickListener(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configureChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        lineChart.getAxisRight().setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.LINE);
    }

    private void loadInitialData() {
        showLoading(true);
        loadDailyData();
        updateMonthlyRevenue();
        updateYearlyRevenue();
    }

    private void loadDailyData() {
        new Thread(() -> {
            List<HoaDonChiTietDAO.RevenueData> revenueData = hoaDonChiTietDAO.getRevenueLast7Days();
            double predictedRevenue = RevenuePredictor.predictLinearRegression(revenueData, 1);

            runOnUiThread(() -> {
                updateDailyRevenue(revenueData);
                drawChart(revenueData, predictedRevenue);
                showLoading(false);
            });
        }).start();
    }

    private void loadWeeklyData() {
        showLoading(true);
        new Thread(() -> {
            List<HoaDonChiTietDAO.RevenueData> revenueData = hoaDonChiTietDAO.getRevenueLast4Weeks();
            double predictedRevenue = RevenuePredictor.predictLinearRegression(revenueData, 1);

            runOnUiThread(() -> {
                drawChart(revenueData, predictedRevenue);
                showLoading(false);
            });
        }).start();
    }

    private void loadMonthlyData() {
        showLoading(true);
        new Thread(() -> {
            List<HoaDonChiTietDAO.RevenueData> revenueData = hoaDonChiTietDAO.getRevenueLast6Months();
            double predictedRevenue = RevenuePredictor.predictLinearRegression(revenueData, 1);

            runOnUiThread(() -> {
                drawChart(revenueData, predictedRevenue);
                showLoading(false);
            });
        }).start();
    }

    private void updateDailyRevenue(List<HoaDonChiTietDAO.RevenueData> revenueData) {
        if (revenueData.isEmpty()) {
            tvDailyRevenue.setText("0 VND");
            return;
        }
        tvDailyRevenue.setText(formatCurrency(revenueData.get(revenueData.size() - 1).revenue));
    }

    private void updateMonthlyRevenue() {
        new Thread(() -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            String startDate = XDate.toStringDate(cal.getTime());

            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            String endDate = XDate.toStringDate(cal.getTime());

            int revenue = hoaDonChiTietDAO.getTotalRevenueByDateRange(startDate, endDate);

            runOnUiThread(() -> {
                tvMonthlyRevenue.setText(formatCurrency(revenue));
            });
        }).start();
    }

    private void updateYearlyRevenue() {
        new Thread(() -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            String startDate = XDate.toStringDate(cal.getTime());

            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            String endDate = XDate.toStringDate(cal.getTime());

            int revenue = hoaDonChiTietDAO.getTotalRevenueByDateRange(startDate, endDate);

            runOnUiThread(() -> {
                tvYearlyRevenue.setText(formatCurrency(revenue));
            });
        }).start();
    }

    private void drawChart(List<HoaDonChiTietDAO.RevenueData> revenueDataList, double predictedRevenue) {
        List<Entry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        for (int i = 0; i < revenueDataList.size(); i++) {
            entries.add(new Entry(i, (float) revenueDataList.get(i).revenue));
            try {
                Date date = XDate.toDate(revenueDataList.get(i).date);
                // Format the date as a string before adding to the list
                String formattedDate = XDate.toStringDate(date); // Or use your preferred date format
                dates.add(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
                // Fallback to the original date string if parsing fails
                dates.add(revenueDataList.get(i).date);
            }
        }

        // Thêm điểm dự đoán
        if (predictedRevenue > 0) {
            entries.add(new Entry(revenueDataList.size(), (float) predictedRevenue));
            dates.add("Dự đoán");
        }

        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu");
        dataSet.setColor(Color.parseColor("#6200EE"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.parseColor("#6200EE"));
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(true);

        // Đánh dấu điểm dự đoán
        if (predictedRevenue > 0) {
            dataSet.setCircleColor(revenueDataList.size());
            dataSet.setCircleColor(android.R.color.holo_red_dark);
        }

        LineData lineData = new LineData(dataSet);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));
        lineChart.setData(lineData);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnDaily) {
            loadDailyData();
        } else if (id == R.id.btnWeekly) {
            loadWeeklyData();
        } else if (id == R.id.btnMonthly) {
            loadMonthlyData();
        }  else if (id == R.id.btnExport) {
            exportReport();
        }
    }

    private void showDateRangePicker() {
        // TODO: Triển khai DateRangePickerDialog
    }

    private void exportReport() {
        // TODO: Triển khai xuất báo cáo
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "%,.0f VND", amount);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        lineChart.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}


