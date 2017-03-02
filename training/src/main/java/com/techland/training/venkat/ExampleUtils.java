package com.techland.training.venkat;

public class ExampleUtils {
    public static void main(String args[]) {
    	String input="yahoo";
    	char[] data=input.toCharArray();
    	reArrange(data);
    }

    private static void reArrange(char[] data) {
    	String response="";
    	for(int index =0;index <data.length;index++) {
    		int frequency=frequency(data, data[index],index);
    		if((frequency > 0 && frequency%2!=0) ) {
    			response=response.concat(String.valueOf(data[index]));
    		}else if((frequency > 0 && frequency%2==0) ) {
        		index=index+frequency-1;
    		}
    	}
    	System.out.println(response);
    }
    private static int frequency(char[] data,char input,int pos) {
    	int count=0;
    	for(int index =pos;index <data.length;index++) {
    		if((input ^ data[index])==0){
    			count++;
    		}else {
    			break;
    		}
    	}
		return count;
    }
}
