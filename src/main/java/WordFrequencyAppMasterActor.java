import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class WordFrequencyAppMasterActor extends AbstractActor {

	// Fields
	private final ActorRef wordCountActor;
	private final ActorRef wordFrequencyActor;

	// Constructor
	public WordFrequencyAppMasterActor(ActorRef wordCountActor, ActorRef wordFrequencyActor) {
		this.wordCountActor = wordCountActor;
		this.wordFrequencyActor = wordFrequencyActor;
	}

	// Props
	public static Props props(ActorRef wordCountActor, ActorRef wordFrequencyActor) {
		return Props.create(WordFrequencyAppMasterActor.class,
				() -> new WordFrequencyAppMasterActor(wordCountActor, wordFrequencyActor));
	}

	// Messages
	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Init.class, i -> wordCountActor.tell(i, getSelf()))
				.match(FilteredWords.class, a -> wordFrequencyActor.tell(a, getSelf()))
				.match(WordMap.class, b -> wordFrequencyActor.tell(b, getSelf()))
				.match(Top25.class, c -> this.display(c)).build();
	}

	private void display(Top25 c) {
		for (Map.Entry<String, Integer> me : c.getTop25Word().entrySet()) {
			System.out.println(me.getKey() + "-" + me.getValue());
		}
	}

}
