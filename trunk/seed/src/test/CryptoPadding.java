package test;



public interface CryptoPadding {

	
	public byte[] addPadding(byte[] source, int blockSize);

	
	public byte[] removePadding(byte[] source, int blockSize);

}
