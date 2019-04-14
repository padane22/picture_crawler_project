package com.NetBian;

public class Main_bian {

	public static void main(String[] args) {
		MultiThread_bian[] m = new MultiThread_bian[23];
		
		for (int i = 0; i < 23; i++){
			int multi = 5;
			int min = 1 + multi * i;
			int max = multi + multi * i;
			m[i] = new MultiThread_bian(min, max);
			m[i].start();
		}
		
		
	}
}
