package com.Zol_Image_Download;


public class Main_ZolImgDown {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MultiThread_ZolImg[] m = new MultiThread_ZolImg[3];
		
		for (int i = 0; i < 3; i++){
			int multi = 140;
			int min = 1 + multi * i;
			int max = multi + multi * i;
			
			m[i] = new MultiThread_ZolImg(min, max);
			m[i].start();
		}
		
		MultiThread_ZolImg m_spe = new MultiThread_ZolImg(421, 422);
		m_spe.start();
	}

}
