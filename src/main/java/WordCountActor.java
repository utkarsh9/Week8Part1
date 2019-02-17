import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class WordCountActor extends AbstractActor {

	// fields
	private List<String> words = new CopyOnWriteArrayList<String>();

	// getter
	public List<String> getWords() {
		return this.words;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Init.class, i -> this.loadFile(i))
				.match(FilteredWords.class, x -> getSender().tell(x, getSelf())).build();
	}

	public static Props props() {
		return Props.create(WordCountActor.class);
	}

	private void loadFile(Init i) {
		try {
			List<String> stopWords = Files.lines(Paths.get("stop-words.txt"))
					.map(line -> line.split(",")).flatMap(Arrays::stream).collect(Collectors.toList());

			List<String> bookWords = Files.lines(Paths.get(i.getFilePath()))
					.flatMap(line -> Arrays.stream(line.split("[\\s,;:?._!--]+"))).map(s -> s.toLowerCase())
					.collect(Collectors.toList());

			for (String word : bookWords) {
				if (!stopWords.contains(word) && !"".equals(word) && !"s".equals(word)) {
					words.add(word);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		getSender().tell(new FilteredWords(words), getSelf());
	}

}
