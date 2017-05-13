package com.edwintechnology.labyrinth;

public class Block {
	public Corner TL;
	public Corner TR;
	public Corner BL;
	public Corner BR;
	
	public Block(float _tlx, float _tly, float _tlz, float _trx, float _try, float _trz, float _blx, float _bly, float _blz, float _brx, float _bry, float _brz)
	{
		TL = new Corner(_tlx, _tly, _tlz);
		TR = new Corner(_trx, _try, _trz);
		BL = new Corner(_blx, _bly, _blz);
		BR = new Corner(_brx, _bry, _brz);
	}
}
