package vaa.mdt.aptcoffee.model;

import java.util.*;

public class AprioriAlgorithm {

    /**
     * Tìm tất cả các itemset phổ biến từ dữ liệu giao dịch.
     *
     * @param transactions Dữ liệu giao dịch, với key là mã giao dịch và value là danh sách sản phẩm.
     * @param minSupport   Ngưỡng hỗ trợ tối thiểu (tỷ lệ phần trăm).
     * @return Danh sách các itemset phổ biến.
     */
    public static List<Set<String>> generateFrequentItemsets(Map<String, List<String>> transactions, double minSupport) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Dữ liệu giao dịch không được rỗng.");
        }

        List<Set<String>> frequentItemsets = new ArrayList<>();
        Map<Set<String>, Integer> itemsetCounts = new HashMap<>();
        int totalTransactions = transactions.size();

        // Bước 1: Tạo itemset kích thước 1 và đếm số lần xuất hiện
        for (List<String> items : transactions.values()) {
            Set<String> uniqueItems = new HashSet<>(items);
            for (String item : uniqueItems) {
                Set<String> singleItemSet = Collections.singleton(item);
                itemsetCounts.put(singleItemSet, itemsetCounts.getOrDefault(singleItemSet, 0) + 1);
            }
        }

        // Bước 2: Lọc các itemset có độ hỗ trợ >= minSupport
        List<Set<String>> currentFrequentItemsets = new ArrayList<>();
        for (Map.Entry<Set<String>, Integer> entry : itemsetCounts.entrySet()) {
            double support = (double) entry.getValue() / totalTransactions;
            if (support >= minSupport) {
                currentFrequentItemsets.add(entry.getKey());
                frequentItemsets.add(entry.getKey());
            }
        }

        // Bước 3: Lặp lại quá trình để tạo các itemset có kích thước lớn hơn
        int k = 2;
        while (!currentFrequentItemsets.isEmpty()) {
            // Tạo candidates từ các itemset phổ biến hiện tại
            List<Set<String>> candidates = generateCandidates(currentFrequentItemsets, k);

            // Đếm số lần xuất hiện của các candidates
            itemsetCounts.clear();
            for (Set<String> candidate : candidates) {
                for (List<String> transaction : transactions.values()) {
                    if (transaction.containsAll(candidate)) {
                        itemsetCounts.put(candidate, itemsetCounts.getOrDefault(candidate, 0) + 1);
                    }
                }
            }

            // Lọc các candidates có độ hỗ trợ >= minSupport
            currentFrequentItemsets.clear();
            for (Map.Entry<Set<String>, Integer> entry : itemsetCounts.entrySet()) {
                double support = (double) entry.getValue() / totalTransactions;
                if (support >= minSupport) {
                    currentFrequentItemsets.add(entry.getKey());
                    frequentItemsets.add(entry.getKey());
                }
            }

            k++;
        }

        return frequentItemsets;
    }

    /**
     * Tạo các luật kết hợp từ tập phổ biến.
     *
     * @param frequentItemsets Danh sách các itemset phổ biến.
     * @param transactions     Dữ liệu giao dịch để tính toán độ hỗ trợ.
     * @param minConfidence    Ngưỡng độ tin cậy tối thiểu (confidence).
     * @return Danh sách các luật kết hợp.
     */
    public static List<String> generateAssociationRules(List<Set<String>> frequentItemsets, Map<String, List<String>> transactions, double minConfidence) {
        List<String> rules = new ArrayList<>();
        Map<Set<String>, Integer> supportCounts = new HashMap<>();

        // Tính số lần xuất hiện của mỗi itemset phổ biến
        for (Set<String> itemset : frequentItemsets) {
            int count = 0;
            for (List<String> transaction : transactions.values()) {
                if (transaction.containsAll(itemset)) {
                    count++;
                }
            }
            supportCounts.put(itemset, count);
        }

        // Sinh luật kết hợp từ itemset phổ biến
        for (Set<String> itemset : frequentItemsets) {
            if (itemset.size() > 1) {
                List<String> items = new ArrayList<>(itemset);
                int itemsetSupport = supportCounts.get(itemset);

                // Tạo các tập con để sinh luật
                for (int i = 1; i < (1 << items.size()) - 1; i++) {
                    Set<String> left = new HashSet<>();
                    Set<String> right = new HashSet<>();

                    for (int j = 0; j < items.size(); j++) {
                        if ((i & (1 << j)) > 0) {
                            left.add(items.get(j));
                        } else {
                            right.add(items.get(j));
                        }
                    }

                    if (!left.isEmpty() && !right.isEmpty()) {
                        int leftSupport = supportCounts.getOrDefault(left, 1); // Tránh chia cho 0
                        double confidence = (double) itemsetSupport / leftSupport;

                        if (confidence >= minConfidence) {
                            // Thay đổi cách hiển thị
                            String rule = left + " → " + right + " (Tỉ lệ mua kèm theo là: " + (confidence * 100) + "%)";
                            rules.add(rule);
                        }
                    }
                }
            }
        }

        return rules;
    }

    /**
     * Tạo các candidates có kích thước k từ các itemset phổ biến có kích thước k-1.
     */
    private static List<Set<String>> generateCandidates(List<Set<String>> frequentItemsets, int k) {
        List<Set<String>> candidates = new ArrayList<>();
        int size = frequentItemsets.size();

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Set<String> unionSet = new HashSet<>(frequentItemsets.get(i));
                unionSet.addAll(frequentItemsets.get(j));

                if (unionSet.size() == k) {
                    candidates.add(unionSet);
                }
            }
        }

        return candidates;
    }
}

