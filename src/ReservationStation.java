
public class ReservationStation {
	double vj, vk;
	String  op, qj, qk;
	boolean busy;
	boolean executing;
	int timeLeft;
	int[] arrLatency;
	double result;
	
	public ReservationStation() {
		this.vj = 0.0;
		this.vk = 0.0;
		this.op = "0";
		this.qj = "0";
		this.qk = "0";
		this.busy = false;
		
	}
	
	void add() {
		this.busy=true;
		this.executing=false;
		
		
		arrLatency = DynamicScheduler.getLatencies();
		switch (op) {
		case "ADD":
			timeLeft = arrLatency[0];
			break;
		case "SUB":
			timeLeft = arrLatency[1];
			break;
		case "MUL":
			timeLeft = arrLatency[2];
			break;
		case "DIV":
			timeLeft = arrLatency[3];
			break;
		}
		
	}
	
	public void alu() {
		executing=true;
		switch (op) {
		case "ADD.D":
			result=vj+vk;
			break;
		case "SUB.D":
			result=vj-vk;
			break;
		case "MULT.D":
			result=vj*vk;
			break;
		case "DIV.D":
			result=vj/vk;
			break;
		}
	}
	
	public void readBus(double bus, String tag) {
		if(tag.equals(qj)) {
			vj=bus;
			qj="0";
		}
		else if(tag.equals(qk)) {
			vk=bus;
			qk="0";
		}
	}
	
	public void preempt() {
		busy=false;
	}
	
	public String updateReservationStation() {
		if(executing) {
			timeLeft--;
			if(timeLeft==0)
				return result+"";
			else
				return "not finished";
		}
		else {
			if(qj.equals("0") && qk.equals("0"))
				alu();
			return "not finished";
		}
	}
}
