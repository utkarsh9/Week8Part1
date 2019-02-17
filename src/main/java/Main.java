import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Main {

	public static void main(String[] args) {
		try {
			ActorSystem system = ActorSystem.create("basic");
			final ActorRef wordActorRef = system.actorOf(WordCountActor.props(), "wca");
			final ActorRef wordFrequencyActorRef = system.actorOf(WordFrequencyActor.props(), "wcah");
			final ActorRef appRef = system
					.actorOf(WordFrequencyAppMasterActor.props(wordActorRef, wordFrequencyActorRef), "wfca");
			appRef.tell(new Init(args[0]), ActorRef.noSender());
			Thread.sleep(3000);
			System.out.println("Press Enter to exit");
			System.in.read();
			system.terminate();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
