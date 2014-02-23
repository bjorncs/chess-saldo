package com.chess.saldo;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bcseime.android.chess.saldo2.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PotViewHolder {
    @InjectView(R.id.pot_name) public TextView name;
    @InjectView(R.id.pot_value) public TextView value;
    @InjectView(R.id.pot_progress) public ProgressBar progress;

    public PotViewHolder(View view) {
        ButterKnife.inject(this, view);
    }
}
