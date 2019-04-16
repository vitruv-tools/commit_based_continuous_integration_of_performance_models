package tools.vitruv.applications.pcmjava.seffstatements.code2seff;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.seff.AbstractAction;

public class ResourceDemandingBehaviourMatching {
	private List<Matching> listMachting;
	
	public ResourceDemandingBehaviourMatching() {
		listMachting = new ArrayList<Matching>();
	}
	
	public void addMatching(AbstractAction newAbstractAction,
			AbstractAction oldAbstractAction, MatchinType matchingType) {
		Matching matching = new Matching(newAbstractAction, 
				oldAbstractAction, matchingType);
		this.listMachting.add(matching);
	}
	
	public List<Matching> getMatchingList(){
		return this.listMachting;
	}
	
	
	public class Matching{
		private AbstractAction newAbstractAction;
		private AbstractAction oldAbstractAction;
		private MatchinType matchinType;
		
		public AbstractAction getNewAbstractAction() {
			return newAbstractAction;
		}
		public void setNewAbstractAction(AbstractAction newAbstractAction) {
			this.newAbstractAction = newAbstractAction;
		}
		public AbstractAction getOldAbstractAction() {
			return oldAbstractAction;
		}
		public void setOldAbstractAction(AbstractAction oldAbstractAction) {
			this.oldAbstractAction = oldAbstractAction;
		}
		public MatchinType getMatchinType() {
			return matchinType;
		}
		public void setMatchinType(MatchinType matchinType) {
			this.matchinType = matchinType;
		}
		
		public Matching(AbstractAction newAbstractAction, AbstractAction oldAbstractAction, MatchinType matchinType) {
			super();
			this.newAbstractAction = newAbstractAction;
			this.oldAbstractAction = oldAbstractAction;
			this.matchinType = matchinType;
		}
		
	}
	
	public enum MatchinType{
		TOTAL_EQUAL, MODIFIED
	}
}
