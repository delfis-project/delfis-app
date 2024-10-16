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

public class AdapterThemeStore extends RecyclerView.Adapter<AdapterThemeStore.ViewHolderTheme> {
    private final List<Theme> themes;
    private final User requisitante;

    public AdapterThemeStore(List<Theme> themes, User requisitante) {
        this.themes = themes;
        this.requisitante = requisitante;
    }

    @NonNull
    @Override
    public ViewHolderTheme onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_powerup_theme, parent, false);
        return new ViewHolderTheme(viewItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderTheme holder, @SuppressLint("RecyclerView") int position) {
        Theme theme = themes.get(position);
        Glide.with(holder.itemView.getContext()).load(theme.getStorePictureUrl()).into(holder.img);
        holder.textPrice.setText(String.valueOf(theme.getPrice()));
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    public class ViewHolderTheme extends RecyclerView.ViewHolder {
        ImageView img;
        TextView textPrice;

        public ViewHolderTheme(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            textPrice = itemView.findViewById(R.id.textPrice);
        }
    }
}