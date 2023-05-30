import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class State {
	private String state;
	private ArrayList<Transition> transitions;
	private boolean init = false;
	private boolean fin = false;
	
	public State(String state) {
		this.transitions = new ArrayList<Transition>();
		this.state = state;
	}
	
	public String getState() {
		return this.state;
	}
	
	public void addTransition(Transition t) {
		if(!this.contineTransition(t))
			this.transitions.add(t);
	}
	
	private boolean contineTransition(Transition t) {
		return transitions.contains(t);
	}
	
	public void setInit() {
		this.init = true;
	}
	
	public void setFinal() {
		this.fin = true;
	}
	
	public boolean esteInitiala() {
		return this.init;
	}
	
	public boolean isFinal() {
		return this.fin;
	}
	
	public Transition getTransition(String c) {
		for(int i = 0; i < transitions.size(); i++) {
			if(transitions.get(i).getRead().equals(c))
				return transitions.get(i);
		}
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		State state = (State) o;
		return this.state.equals(state.getState());
	}
	
	public String toString() {
		return this.state + " " + this.transitions;
	}
}

class Transition {
	private String stateStart, stateEnd;
	private String read, write, direction;
	
	public Transition(String stateStart, String stateEnd, String read, String write, String direction) {
		this.stateStart = stateStart;
		this.stateEnd = stateEnd;
		this.read = read;
		this.write = write;
		this.direction = direction;
	}
	
	public String getStartState() {
		return this.stateStart;
	}
	
	public String getFinishState() {
		return this.stateEnd;
	}
	
	public String getRead() {
		return this.read;
	}
	
	public String getWrite() {
		return this.write;
	}
	
	public String getDirection() {
		return this.direction;
	}
	
	@Override
	public boolean equals(Object o) {
		Transition t = (Transition) o;
		return (read.equals(t.getRead()) && write.equals(t.getWrite()) && direction.equals(t.getDirection()));
	}
	
	public String toString() {
		return this.stateStart + " " + this.read + " " + this.stateEnd + " " + this.write + " " + this.direction + " ";
	}
}

public class TuringMachine {
	private ArrayList<State> states;
	private State currentState;
	private Word word;
	private boolean valid = true;
	
	public TuringMachine(Word word) {
		if(word.toString().equals("")) {
			System.out.println("Cuvantul nu este acceptat.");
			valid = false;
		}
		else { 
			this.word = word;
			states = new ArrayList<State>();
			readFile();
		}
	}
	
	public void readFile() {
		StringBuffer path = new StringBuffer(System.getProperty("user.dir"));
		path.append("\\src\\date.txt");
		File file = new File(path.toString());
		try {
			if(file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String s;
				String[] str;
				
				states.add(new State(br.readLine()));
				states.get(0).setInit();
				currentState = states.get(0);
				str = br.readLine().split(" ");
				for(int i = 0; i < str.length; i++) {
					State state = new State(str[i]);
					if(!states.contains(state))
						states.add(new State(str[i]));
					states.get(states.indexOf(state)).setFinal();
				}
		
				while((s = br.readLine()) != null) {
					str = s.split(" ");
					State state = new State(str[0]);
					if(!states.contains(state)) {
						states.add(state);
					}
					Transition t = new Transition(str[0], str[2], str[1], str[3], str[4]);
					states.get(states.indexOf(state)).addTransition(t);
				}
				
				br.close();
			}
			else {
				file.createNewFile();
				readFile();
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public State getNextState(String name) {
		for(State s: states) {
			if(s.getState().equals(name))
				return s;
		}
		return null;
	}
	
	public void solve() {
		int index = 0;
		Transition t;
		int c = 0;
		while(true) {
			if(!valid) break;
			if(currentState.isFinal()) {
				System.out.println("Cuvantul este acceptat.");
				break;
			}
			if(c >= 100000) {
				System.out.println("100000");
				break;
			}
			t = currentState.getTransition(word.getWord().charAt(index) + "");
			if(t == null) {
				System.out.println("Cuvantul nu este acceptat.");
				break;
			}
			word.setWord(index, t.getWrite());
			if(t.getDirection().equals("L")) index -= 1;
			else index += 1;
			currentState = getNextState(t.getFinishState());
			c++;
			System.out.println(t.getStartState() + " -> " + t.getFinishState() + " (" + t.getRead() + t.getWrite() + t.getDirection() + ") => " + word.toString());
		}
	}
}

class Word {
	private String word;
	private int startIndex, endIndex;
	
	public Word(String word) {
		this.word = "B" + word + "B";
		
		startIndex = 0; endIndex = this.word.length() - 1;
		
		for(int i = startIndex; i < endIndex; i++) {
			if(!(this.word.charAt(i) + "").equals("B")) {
				startIndex = i;
				break;
			}
		}
		
		for(int i = endIndex; i >= startIndex; i--) {
			if(!(this.word.charAt(i) + "").equals("B")) {
				endIndex = i;
				break;
			}
		}
	}
        
	public void setWord(int index, String c) {
		this.word = this.word.substring(0,  index) + c + this.word.substring(index + 1);
	}
	
	public String getWord() {
		return this.word;
	}
	
	public String toString() {
		if((this.word.charAt(0) + "").equals("B") && (this.word.charAt(1) + "").equals("B") && getWord().length() == 2) return "";
		return this.word.substring(startIndex, endIndex + 1);
	}
}

class Main{
    public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Introduceti sirul: ");
    String input = scanner.nextLine();
    Word word = new Word(input);
    TuringMachine solution = new TuringMachine(word);
    solution.solve();
    }

}

