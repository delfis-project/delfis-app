package goldenage.delfis.app.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Streak implements Serializable {
    private long id;
    private String initialDate;
    private String finalDate;
}
