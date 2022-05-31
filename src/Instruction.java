public class Instruction {
    String type;
    int destReg;
    int src1;
    int src2;
    int address;
    
    public Instruction(String op, int dest, int src1, int src2) {
    	this.type = op;
    	this.destReg = dest;
    	this.src1 = src1;
    	this.src2 = src2;
    }
    
    public Instruction(String type, int x, int y) {
        this.type = type;
        this.destReg = x;
        this.address = y;
    }
    
    public int getDestReg() {
		return destReg;
	}

	public void setDestReg(int destReg) {
		this.destReg = destReg;
	}

	public int getSrc1() {
		return src1;
	}

	public void setSrc1(int src1) {
		this.src1 = src1;
	}

	public int getSrc2() {
		return src2;
	}

	public void setSrc2(int src2) {
		this.src2 = src2;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	

}
