package ntu.scse.mdp2022.mainui.Chat;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ntu.scse.mdp2022.mainui.R;

public class SentHolder extends RecyclerView.ViewHolder {
    TextView msg;

    public SentHolder (View view) {
        super(view);
        msg = view.findViewById(R.id.msgBody);
    }

    public void bind(Message theMsg, Context c) {
        msg.setText(theMsg.getMessage());
    }

}
