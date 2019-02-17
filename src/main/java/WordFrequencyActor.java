import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class WordFrequencyActor extends AbstractActor {

	Map<String, Integer> wordMap = new LinkedHashMap<String, Integer>();
	Map<String, Integer> top25 = new LinkedHashMap<String, Integer>();

	public static Props props() {
		return Props.create(WordFrequencyActor.class);
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(FilteredWords.class, i -> this.createWordMap(i))
				.match(WordMap.class, j -> this.sortAndTop25(j)).build();
	}

	private void sortAndTop25(WordMap j) {
		Map<String, Integer> a = j.getWordMap().entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
						Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		Set<Entry<String, Integer>> treeMapSet = a.entrySet();
		Iterator<Entry<String, Integer>> it = treeMapSet.iterator();
		int entryCount = 0;
		while (it.hasNext() && entryCount < 25) {
			entryCount++;
			Map.Entry<String, Integer> mapEntry = (Map.Entry<String, Integer>) it.next();
			top25.put(mapEntry.getKey(), mapEntry.getValue());
		}
		getSender().tell(new Top25(top25), getSelf());
	}

	private void createWordMap(FilteredWords i) {
		List<String> words = i.getWords();
		for (String word : words) {
			int count = 1;
			if (wordMap.containsKey(word)) {
				count = wordMap.get(word);
				count++;
			}
			wordMap.put(word, count);
		}
		getSender().tell(new WordMap(wordMap), getSelf());
	}

}
