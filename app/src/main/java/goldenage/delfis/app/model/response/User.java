package goldenage.delfis.app.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {
    private long id;
    private String name;
    private String username;
    private String password;
    private String email;
    private int level;
    private int points;
    private int coins;
    private String birthDate;
    private String pictureUrl;
    private String createdAt;
    private String updatedAt;
    private String token;

    @SerializedName("fkPlanId")
    private int planId;
    @SerializedName("fkUserRoleId")
    private int userRoleId;

    private Streak currentStreak;
    private Session currentSession;

    private int counterPlayedGames;
}

