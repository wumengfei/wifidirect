package com.example.android.wifidirect;

import android.renderscript.Sampler.Value;
import android.util.Log;

public class CollisionPrediction {
	//data1 is from smartphone, data2 is from vehicle
	//��ʱ�Ѿ�γ����Ϊ��ʵ������
	//����ֵ˵����
	//0:����ײ
	//1:��ײ������೵��
	//2:��ײ�����Ҳ೵��
	//-1:��ʾ����
	private Gps data1;
	private Gps data2;
	
	public  CollisionPrediction(){};

	public CollisionPrediction(Gps data1, Gps data2){
		this.data1 = data1;
		this.data2 = data2;
	}
	public void setData1(Gps data1){
		this.data1 = data1;
	}

	public void setData2(Gps data2){
		this.data2 = data2;
	}
	
	public int collisionDirection(){
		
		int intersectionThreshold = 5;//�˳�����ʱ��Ĳ�ֵ
		int preCollisionTime = 15;//�ٶ�������ײ��ʱ�����preCollisionTime�����Ȳ�Ԥ��
		double x1 = data1.getX();
		double y1 = data1.getY();
		double angle1 = data1.stdAngle(data1.getAngle());//�õ���׼����ĽǶ�
		double radian1 = data1.angleToRadian(angle1);
		double v1 = data1.getSpeed();//������ÿСʱת��Ϊ��ÿ��
		
		double x2 = data2.getX();
		double y2 = data2.getY();
		double angle2 = data2.stdAngle(data2.getAngle());
		double radian2 = data2.angleToRadian(angle2);
		double v2 = data2.getSpeed();//������ÿСʱת��Ϊ��ÿ��
		
		double timeDifference = data2.getGpsTime() - data1.getGpsTime();
		
		//xi,yi insection point location
		double xi = 0;
		double yi = 0;
		//the time of smartphone/vehicle to the intersection point
		double t1 = 0;
		double t2 = 0;
		
		double k1 = Math.tan(radian1);
		double k2 = Math.tan(radian2);
		
		//y - y1 = k1 (x - x1)
		//y - y2 = k2 ��x - x2)
		//ת����
		//k1 * x - y = k1 * x1 - y1
		//k2 * x - y = k2 * x2 - y2
		//common solution of�� 
		//ax+by=e
		//cx+dy=f
		//a = k1, b = -1, e = k1 * x1 - y1
		//c = k2, d = -1, f = k2 * x2 - y2
		double a;
		double b;
		double e;
		double c;
		double d;
		double f;
		
		//��б�ʾ�Ϊ������ʱ����Ϊ���ཻ
		if (((radian1 == Math.PI/2) || (radian1 == Math.PI*3/2)) && ((radian2 == Math.PI/2) || (radian2 == Math.PI*3/2))){
			return 0;
		}else if (((radian1 == Math.PI/2) || (radian1 == Math.PI*3/2))){ 	//k1Ϊ���������һ��б��Ϊ����������
			b = 0;
			a = 1;
			e = x1;
			c = k2;
			d = -1;
			f = (k2 * x2 - y2);
		
		}else if (((radian2 == Math.PI/2) || (radian2 == Math.PI*3/2))){     	//������һ��б��Ϊ����������			
			d = 0;
			c = 1;
			f = x2;
			a = k1;
			b = -1;
			e = (k1 * x1 - y1);
		}else{
			a = k1;
			b = -1;
			e = (k1 * x1 - y1);
			c = k2;
			d = -1;
			f = (k2 * x2 - y2);
		}
		Log.d("a",String.valueOf(a));
		Log.d("b",String.valueOf(b));
		Log.d("c",String.valueOf(c));
		Log.d("d",String.valueOf(d));
		Log.d("e",String.valueOf(e));
		Log.d("f",String.valueOf(f));
		Log.d("k1",String.valueOf(k1));
		Log.d("k2",String.valueOf(k2));
		Log.d("tan(pi/2)",String.valueOf(Math.tan(Math.PI/2)));

		
		//�ٶ��������غϻ�ƽ�У�����Ϊ������ײ��һ����Ϊ�غ���ƽ�и���С��
		//������Ϊ�����߹�������ʻ�����в���ÿһ�̶��غϻ�ƽ��
		
		//���ڳ������Ļ�������ͬ����Ƚ����������ٶȣ�Ŀǰ��û�м�
		if ((a*d - b*c) == 0){
			return 0;
		}else{
			xi = (e*d - b*f)/(a*d - b*c);
			yi = (a*f - e*c)/(a*d - b*c);
 			Log.d("interx",String.valueOf(xi));
 			Log.d("intery",String.valueOf(yi));
 			
		}
		//�ٶ�Ϊ0�򲻻���ײ
		if ((v1 == 0) || (v2 == 0)){
			return 0;
		}
		t1 = Math.sqrt((xi - x1)*(xi - x1) + (yi - y1)*(yi -y1))/v1;
		t2 = Math.sqrt((xi - x2)*(xi - x2) + (yi - y2)*(yi -y2))/v2;
//		Log.d("timeDifference",String.valueOf(timeDifference));
		t2 = t2 + timeDifference;//��Ϊ���յ��ĳ����İ����ӳ٣�����Ҫ��t2��ȥ���ʱ��
		//�ٶ�������ײ��ʱ�����collisionTime�����Ȳ�Ԥ��
		if ((t1 > preCollisionTime) && (t2 > preCollisionTime)){
			return 0;
		}
		Log.d("t2", String.valueOf(t2));
		Log.d("t1", String.valueOf(t1));
		//�ǲ��Ǽн�Ū����
		Log.d("direction sin cos", String.valueOf(Math.sin(radian1))+","+String.valueOf(Math.cos(radian1)));
		if (Math.abs(t2-t1) >= intersectionThreshold) {
			Log.d("tb2", String.valueOf(t2));
			Log.d("tb1", String.valueOf(t1));
			return 0;	
		}else if((Math.cos(radian1)*(xi-x1) >= 0)&&(Math.sin(radian1)*(yi-y1) >= 0) ){//ȷ�������������˵�ǰ��
			//vehicle - smartphone,
			//left:(0,pi)(-2pi,-pi);
			//right:(-pi,0)(pi,2pi)
			double radian_dif = xyToRadian(x2-x1,y2-x1)-radian1;
			Log.d("radian_dif", String.valueOf(radian_dif));
			Log.d("x2", String.valueOf(x2));
			Log.d("y2", String.valueOf(y2));
			Log.d("xyToRadian(x2,y2)", String.valueOf(xyToRadian(x2,y2)));
			if(((radian_dif >= 0) && (radian_dif <= Math.PI)||
					((radian_dif >= -2 * Math.PI) &&(radian_dif <= -Math.PI)))){
				return 1;
			}
			if(((radian_dif >= -Math.PI) && (radian_dif < 0)||
					((radian_dif > Math.PI) &&(radian_dif < 2*Math.PI)))){
				return 2;
			}	
		}else{
			Log.d("else", "else");
			return 0;
		}	
		return -1;
	}
	
	//������ת��Ϊ��x��ļнǣ�ӳ�䵽0-2pi
	public double xyToRadian(double x, double y){
		double radian = 0;
		if(x >= 0){
			radian = Math.asin(y/Math.sqrt(x*x + y*y));
			if (radian < 0){
				radian = 2*Math.PI + radian;
			}
		}else{
			radian = Math.PI - Math.asin(y/Math.sqrt(x*x + y*y));
		}
		return radian;
	}
}
