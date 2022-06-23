package TermTagIndex;

import java.util.concurrent.Callable;

public class cmdProcessor implements Callable {

	
	String cmd ;
	int id;
	public cmdProcessor(String cmd, int id){
		this.cmd = cmd;
		this.id = id;
	}
	@Override
	public Object call() throws Exception {
		try{
			Process P = Runtime.getRuntime().exec(cmd);    
			if(P.waitFor()==0)
				System.out.println(this.id +" finished");
			else
				System.out.println(this.id +" terminate");
			P.destroy();
			}catch(Exception e){
				e.printStackTrace();
		}
		return null;
	}

}
