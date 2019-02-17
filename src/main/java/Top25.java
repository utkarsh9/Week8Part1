import java.util.Map;

public class Top25 {

	Map<String, Integer> top25Word;

	public Top25(Map<String, Integer> top25Words) {
		this.top25Word = top25Words;
	}

	public Map<String, Integer> getTop25Word() {
		return top25Word;
	}

}
