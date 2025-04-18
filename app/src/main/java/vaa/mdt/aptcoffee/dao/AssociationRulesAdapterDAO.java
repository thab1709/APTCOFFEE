package vaa.mdt.aptcoffee.dao;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vaa.mdt.aptcoffee.R;

public class AssociationRulesAdapterDAO extends RecyclerView.Adapter<AssociationRulesAdapterDAO.ViewHolder> {
    private List<String> associationRules;

    public AssociationRulesAdapterDAO(List<String> associationRules) {
        this.associationRules = associationRules;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String rule = associationRules.get(position);
        holder.tvRule.setText(rule);
    }

    @Override
    public int getItemCount() {
        return associationRules.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRule = itemView.findViewById(R.id.tvRule);
        }
    }
}