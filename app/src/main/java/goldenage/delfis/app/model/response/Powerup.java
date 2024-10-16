package goldenage.delfis.app.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Powerup {
    private long id;
    private String name;
    private int price;
    private String storePictureUrl;
}
