package redis001;

public class Calculator {
	private static int result;//��̬���������ڴ洢���н��
	public void add(int n){
		result = result + n;
	}
	
	public void substract(int n){
		result = result - n;
	}
	
	public void clear(){
		result = 0;
	}
	
	public int getResult(){
		return result;
	}
}
