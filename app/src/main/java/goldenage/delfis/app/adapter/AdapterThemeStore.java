package goldenage.delfis.app.adapter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import goldenage.delfis.app.R;
import goldenage.delfis.app.api.DelfisApiService;
import goldenage.delfis.app.model.request.AppUserPowerupRequest;
import goldenage.delfis.app.model.request.AppUserThemeRequest;
import goldenage.delfis.app.model.response.AppUserPowerup;
import goldenage.delfis.app.model.response.AppUserTheme;
import goldenage.delfis.app.model.response.Powerup;
import goldenage.delfis.app.model.response.Theme;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterThemeStore extends RecyclerView.Adapter<AdapterThemeStore.ViewHolderTheme> {
    private final List<Theme> themes;
    private final User requisitante;
    private PopupWindow popupWindow;

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

            itemView.setOnClickListener(v -> {
                if (requisitante.getThemes() != null) {
                    for (Theme theme : requisitante.getThemes()) {
                        if (theme.getId() == themes.get(getAdapterPosition()).getId()) {
                            Toast.makeText(itemView.getContext(), "Você já possui esse tema!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                if (requisitante.getCoins() >= themes.get(getAdapterPosition()).getPrice()) {
                    showBuyPopup((Activity) v.getContext());

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("coins", requisitante.getCoins() - themes.get(getAdapterPosition()).getPrice());

                    AppUserThemeRequest appUserThemeRequest = new AppUserThemeRequest(
                            themes.get(getAdapterPosition()).getPrice(),
                            LocalDate.now().toString(),
                            requisitante.getId(),
                            themes.get(getAdapterPosition()).getId()
                    );

                    DelfisApiService apiService = RetrofitFactory.getClient().create(DelfisApiService.class);
                    Call<AppUserTheme> call = apiService.createAppUserTheme(requisitante.getToken(), appUserThemeRequest);
                    call.enqueue(new Callback<AppUserTheme>() {
                        @Override
                        public void onResponse(Call<AppUserTheme> call, Response<AppUserTheme> response) {
                            if (response.isSuccessful()) {
                                Call<User> call2 = apiService.updateUserPartially(requisitante.getToken(), requisitante.getId(), updates);
                                call2.enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        if (response.isSuccessful()) {
                                            User user = response.body();
                                            if (user != null) {
                                                requisitante.setCoins(user.getCoins());

                                                if (requisitante.getThemes() == null)
                                                    requisitante.setThemes(new ArrayList<>());
                                                requisitante.getThemes().add(themes.get(getAdapterPosition()));

                                                Toast.makeText(itemView.getContext(), "Tema comprado! Reinicie o app para vê-lo!", Toast.LENGTH_LONG).show();

                                                @SuppressLint("UnsafeIntentLaunch")
                                                Intent intent = ((Activity) itemView.getContext()).getIntent();
                                                intent.putExtra("user", requisitante);
                                                ((Activity) itemView.getContext()).finish();
                                                itemView.getContext().startActivity(intent);
                                            }
                                        } else {
                                            Toast.makeText(itemView.getContext(), "Erro ao comprar tema!", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                        Toast.makeText(itemView.getContext(), "Erro ao adquirir tema: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(itemView.getContext(), "Erro ao comprar tema!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AppUserTheme> call, Throwable t) {
                            Toast.makeText(itemView.getContext(), "Erro ao adquirir tema: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(itemView.getContext(), "Moedas insuficientes!", Toast.LENGTH_LONG).show();
                }
            });
        }

        private void showBuyPopup(Activity activity) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);

            @SuppressLint("InflateParams")
            View popupView = inflater.inflate(R.layout.popup_buy, null);

            popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

            popupWindow.showAtLocation(activity.getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);

            new Handler().postDelayed(() -> {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }, 10_000);
        }
    }
}