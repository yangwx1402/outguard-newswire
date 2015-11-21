package com.chenlb.mmseg4j.example;

import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.SimpleSeg;

public class Simple extends Complex {
	
	protected Seg getSeg() {

		return new SimpleSeg(dic);
	}

}
