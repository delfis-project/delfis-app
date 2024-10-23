package goldenage.delfis.app.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WordSearch {
    private String grid;
    private int gridSize;
    private List<String> words;
}
