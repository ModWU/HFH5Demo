package com.example.hfh5pay;

public interface IPayListener {
	void payStart(int payType);
	void payFinished(boolean isSuccess, String info);
	void payBack(String info);
	void doThing(String info);
}
