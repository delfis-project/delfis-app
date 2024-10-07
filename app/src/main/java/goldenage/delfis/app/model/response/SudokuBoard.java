package goldenage.delfis.app.model.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SudokuBoard {
    private List<List<String>> board;
}

