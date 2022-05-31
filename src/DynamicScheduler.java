import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DynamicScheduler {
	int clock=0;
	Register[] FPRegisters=new Register[32];
	static int addSubLatency;
	static int mulLatency;
	static int divLatency;
	static int loadStoreLatency;
	
	Queue<Double> bus=new LinkedList();
	Queue<String> tag=new LinkedList();
	Queue<Instruction> instructionQueue = new LinkedList();
	
	ReservationStation[] addSubStation=new ReservationStation[3];
	ReservationStation[] mulDivStation=new ReservationStation[2];
	
	
	public DynamicScheduler() {
		this.instructionQueue = readFile("");
		System.out.println(instructionQueue.peek().getType());
		init();
		issue();
		
	}
	
	
	public void schedule() {
		while (true) {
			//executing and writing to bus
			for(int i=0;i<addSubStation.length;i++) {
				String result=addSubStation[i].updateReservationStation();
				if(result != "not finished") {
					bus.add(Double.parseDouble(result));
					tag.add("A"+i+1);
				}
			}
			for(int i=0;i<mulDivStation.length;i++) {
				String result=mulDivStation[i].updateReservationStation();
				if(result != "not finished") {
					bus.add(Double.parseDouble(result));
					tag.add("M"+i+1);
				}
			}
			//same for load and store
			
			//reading from bus
			for(int i=0;i<addSubStation.length;i++) {
				if(tag.peek()!=null)
					addSubStation[i].readBus(bus.peek(), tag.peek());
			}
			for(int i=0;i<mulDivStation.length;i++) {
				if(tag.peek()!=null)
					mulDivStation[i].readBus(bus.peek(), tag.peek());
			}
			//same for load and store
			
			//preemption
			bus.poll();
			String busTag=tag.poll();
			int index=Character.getNumericValue(busTag.charAt(1));
			busTag=busTag.substring(0, 1);
			switch(busTag) {
			case "A":
				addSubStation[index].preempt();
				break;
			case "M":
				mulDivStation[index].preempt();
				break;
			//same for load and store
			}
			
		}
	}
	
	public void issue() {
		while(true) {
		Instruction inst = instructionQueue.element();
		if(inst.getType().equals( "ADD.D" )|| inst.getType() == "SUB.D") {
			for(int i = 0; i < addSubStation.length; i++) {

				if(!addSubStation[i].busy) {
					addSubStation[i].add();
					System.out.println("addSubStation[" + i + "]: " + addSubStation[i].busy);
					instructionQueue.poll();
					FPRegisters[inst.getDestReg()].Qi = "A" + i;
					addSubStation[i].op = inst.getType().substring(0,2);
					if(FPRegisters[inst.getSrc1()].Qi != "0") {
						addSubStation[i].qj = FPRegisters[inst.getSrc1()].Qi;
						addSubStation[i].vj = 0.0;
					}
					else {
						addSubStation[i].qj = "0";
						addSubStation[i].vj = FPRegisters[inst.getSrc1()].value;
					}
					if(FPRegisters[inst.getSrc2()].Qi != "0") {
						addSubStation[i].qk = FPRegisters[inst.getSrc2()].Qi;
						addSubStation[i].vk = 0.0;
					}
					else {
						addSubStation[i].vk = FPRegisters[inst.getSrc2()].value;
						addSubStation[i].qk = "0";
					}
					break;
				}
					
			} 
		}
		else if(inst.getType().equals("MUL.D") || inst.getType().equals("DIV.D")) {
			for(int i = 0; i < mulDivStation.length; i++) {
				if(!mulDivStation[i].busy) {
					mulDivStation[i].add();
					System.out.println("mulDivStation[" + i + "]: " + mulDivStation[i].busy);
					instructionQueue.poll();
					FPRegisters[inst.getDestReg()].Qi = "M" + i;
					mulDivStation[i].op = inst.getType().substring(0,2);
					if(FPRegisters[inst.getSrc1()].Qi != "0") {
						mulDivStation[i].qj = FPRegisters[inst.getSrc1()].Qi;
						mulDivStation[i].vj = 0.0;
					}
					else {
						mulDivStation[i].qj = "0";
						mulDivStation[i].vj = FPRegisters[inst.getSrc1()].value;
					}
					if(FPRegisters[inst.getSrc2()].Qi != "0") {
						mulDivStation[i].qk = FPRegisters[inst.getSrc2()].Qi;
						mulDivStation[i].vk = 0.0;
					}
					else {
						mulDivStation[i].vk = FPRegisters[inst.getSrc2()].value;
						mulDivStation[i].qk = "0";
					}
					break;
				}
			}
		}}
	}
	public static Queue<Instruction> readFile(String path){
        String[] arr= null;
        List<String> instructions = new ArrayList<String>();
        try{
            FileInputStream fstream_school = new FileInputStream("src/code.txt");
            DataInputStream data_input = new DataInputStream(fstream_school);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input));
            String str_line;
            while ((str_line = buffer.readLine()) != null){
                str_line = str_line.trim();
                if ((str_line.length()!=0))
                    instructions.add(str_line);
            }
            arr = (String[])instructions.toArray(new String[instructions.size()]);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	Queue<Instruction> instructionQueue = new LinkedList<Instruction>();

        for(int i = 0; i < arr.length; i++) {
        	String[] alo = arr[i].split(",");
        	String[] a = alo[0].split(" ");
        	if(alo.length == 2) {        		

        		instructionQueue.add(new Instruction(a[0], Integer.parseInt(a[1].substring(1)),Integer.parseInt(alo[1])));
        	}
        	else {
        		instructionQueue.add(new Instruction(a[0], Integer.parseInt(a[1].substring(1)), Integer.parseInt(alo[1].substring(1)), Integer.parseInt(alo[2].substring(1))));

        	}
        }
        return instructionQueue;
    }
	
	public void init() {
		//scanner
		setLatencies(2, 10, 40, 2);
		//get code
		for(int i=0;i<addSubStation.length;i++)
			addSubStation[i]=new ReservationStation();
		for(int i=0;i<mulDivStation.length;i++)
			mulDivStation[i]=new ReservationStation();
		for(int i=0;i<FPRegisters.length;i++)
			FPRegisters[i]=new Register();
//		schedule();
	}
	
	public void setLatencies(int a, int m, int d, int l) {
		this.addSubLatency = a;
		this.mulLatency = m;
		this.divLatency = d;
		this.loadStoreLatency = l;
	}
	
	public static int[] getLatencies() {
		int[] latArr = new int[4];
		latArr[0] = addSubLatency;
		latArr[1] = mulLatency;
		latArr[2] = divLatency;
		latArr[3] = loadStoreLatency;
		
		return latArr;
	}
	
	public static void main(String[] args) {
		DynamicScheduler dynamicScheduler=new DynamicScheduler();
	}
	
	
}
