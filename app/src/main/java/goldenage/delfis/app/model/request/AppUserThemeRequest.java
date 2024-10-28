package goldenage.delfis.app.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserThemeRequest {
    private int transactionPrice;
    private String transactionDate;
    private long fkAppUserId;
    private long fkThemeId;
}
