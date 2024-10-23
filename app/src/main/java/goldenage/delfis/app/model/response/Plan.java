package goldenage.delfis.app.model.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    private long id;
    private String name;
    private BigDecimal price;
    private String description;
}
