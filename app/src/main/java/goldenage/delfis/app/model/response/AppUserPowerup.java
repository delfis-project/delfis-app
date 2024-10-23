package goldenage.delfis.app.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserPowerup {
    private long id;
    private int transactionPrice;
    private String transactionDate;
    private long fkAppUserId;
    private long fkPowerupId;
}
