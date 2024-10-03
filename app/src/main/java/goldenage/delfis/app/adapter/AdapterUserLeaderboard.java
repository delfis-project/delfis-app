package goldenage.delfis.app.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.User;

public class AdapterUserLeaderboard extends RecyclerView.Adapter<AdapterUserLeaderboard.ViewHolderLeaderboard> {
    private final List<User> listaUser;
    private final User requisitante;

    public AdapterUserLeaderboard(List<User> listaUser, User requisitante) {
        this.listaUser = listaUser;
        this.requisitante = requisitante;
    }

    @NonNull
    @Override
    public ViewHolderLeaderboard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_user_leaderboard, parent, false);
        return new ViewHolderLeaderboard(viewItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderLeaderboard holder, @SuppressLint("RecyclerView") int position) {
        User currentUser = listaUser.get(position);
        holder.textNome.setText(currentUser.getUsername());
        holder.textPontos.setText(String.valueOf(currentUser.getPoints()));
        holder.textLugar.setText("#" + (position + 1));

        if (currentUser.getId() == requisitante.getId())
            holder.layout.setBackgroundColor(holder.layout
                    .getContext()
                    .getResources()
                    .getColor(R.color.delfisBackground));
    }

    @Override
    public int getItemCount() {
        return listaUser.size();
    }

    public class ViewHolderLeaderboard extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView textLugar;
        TextView textNome;
        TextView textPontos;

        public ViewHolderLeaderboard(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.linearLayout);
            textNome = itemView.findViewById(R.id.textNome);
            textPontos = itemView.findViewById(R.id.textPontos);
            textLugar = itemView.findViewById(R.id.textLugar);
        }
    }
}