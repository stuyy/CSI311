package csi311;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class MachineSpec implements Serializable {

	// Since defined as an inner class, must be declared static or Jackson can't deal.
	/**
	 * StateTransition is a static class that represents the current State of a State Machine.
	 * It also has a List of Transitions
	 * @author Anson
	 *
	 */
	public static class State implements Serializable {
		private String state; 
		private List<String> transitions;
		public State() { 
			
		}
		public String getState() { 
			return this.state; 
		}
		public void setState(String state) { 
			this.state = state.toLowerCase(); 
		} 
		public List<String> getTransitions() { 
			return this.transitions; 
		} 
		public void setTransitions(List<String> transitions) { 
			this.transitions = transitions;
			if (this.transitions != null) {
				for (int i = 0; i < transitions.size(); i++) {
					transitions.set(i, transitions.get(i).toLowerCase()); 
				}
			}
		}
	}
	
	private List<State> machineSpec;
	private Integer tenantId;
	public MachineSpec() { 
		
	}
	public List<State> getMachineSpec() { 
		return machineSpec; 
	} 
	public void setMachineSpec(List<State> machineSpec) { 
		this.machineSpec = machineSpec; 
	} 
	public void setTenantId(Integer tenantId)
	{
		this.tenantId = tenantId;
	}
	public Integer getTentantId() {
		return this.tenantId;
	}
}


