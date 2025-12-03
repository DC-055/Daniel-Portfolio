package final_proj_08_11;

import java.util.HashSet;
import java.util.Set;

public class MyButton {
	private String msg;
	private Set<Observer> observers = new HashSet<>();
	
	public MyButton() {
		
	}
	
	public String getMessage() {
		return msg;
	}
        
        public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void attach(Observer o) {
		this.observers.add(o);
	}
	
	public void detach(Observer o) {
		this.observers.remove(o);
	}
	
	public void click() {
		myNotify();
	}
	
	public void myNotify() {
		for(Observer o : this.observers)
			o.update(this);
	}
}
