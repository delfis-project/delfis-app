package goldenage.delfis.app.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.response.Powerup;
import goldenage.delfis.app.model.response.Theme;
import goldenage.delfis.app.model.response.User;

public class AdapterPowerup extends RecyclerView.Adapter<AdapterPowerup.ViewHolderPowerup> {
    private final List<Powerup> powerups;
    private final User requisitante;

    public AdapterPowerup(List<Powerup> powerups, User requisitante) {
        this.powerups = powerups;
        this.requisitante = requisitante;
    }

    @NonNull
    @Override
    public ViewHolderPowerup onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_powerup_theme, parent, false);
        return new ViewHolderPowerup(viewItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderPowerup holder, @SuppressLint("RecyclerView") int position) {
        Powerup powerup = powerups.get(position);
        Glide.with(holder.itemView.getContext()).load(powerup.getStorePictureUrl()).into(holder.img);
        holder.textPrice.setText(String.valueOf(powerup.getPrice()));
    }

    @Override
    public int getItemCount() {
        return powerups.size();
    }

    public class ViewHolderPowerup extends RecyclerView.ViewHolder {
        ImageView img;
        TextView textPrice;

        public ViewHolderPowerup(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            textPrice = itemView.findViewById(R.id.textPrice);
        }
    }
}