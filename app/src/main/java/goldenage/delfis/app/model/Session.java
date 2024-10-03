package goldenage.delfis.app.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session implements Serializable {
    private String id;
    private String initialDatetime;
    private String finalDatetime;
}
