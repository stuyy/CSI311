package csi311;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class MachineSpec implements Serializable {

	// Since defined as an inner class, must be declared static or Jackson can't deal.
	public static class StateTransitions implements Serializable {
		private String state; 
		private List<String> transitions;
		public StateTransitions() { }
		public String getState() { return state; }
		public void setState(String state) { this.state = state.toLowerCase(); } 
		public List<String> getTransitions() { return transitions; } 
		public void setTransitions(List<String> transitions) { 
			this.transitions = transitions;
			if (this.transitions != null) {
				for (int i = 0; i < transitions.size(); i++) {
					transitions.set(i, transitions.get(i).toLowerCase()); 
				}
			}
		} 
	}
	
	private List<StateTransitions> machineSpec;
	public MachineSpec() { }
	public List<StateTransitions> getMachineSpec() { return machineSpec; } 
	public void setMachineSpec(List<StateTransitions> machineSpec) { this.machineSpec = machineSpec; } 
}


